package tech.bytespot.hamonygpsserver.version2;

import org.springframework.core.serializer.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomGpsDeserializer implements Deserializer<List<TrackerDeviceData>> {

  @Override
  public List<TrackerDeviceData> deserialize(InputStream inputStream) throws IOException {
    ByteArrayOutputStream deviceDataOutputStream = new ByteArrayOutputStream();
    while (inputStream.available() > 0) {
      deviceDataOutputStream.write(inputStream.read());
    }

    System.out.println("Collected byte array : " + deviceDataOutputStream.toByteArray().length);
    var splitByteArray = this.splitByteArray(deviceDataOutputStream.toByteArray());
    System.out.println("Collected byte array : " + splitByteArray.size());

    List<TrackerDeviceData> dataList = new ArrayList<>();

    splitByteArray.forEach(
        byteArray -> {
          InputStream dataStream = new ByteArrayInputStream(byteArray);
          TrackerDeviceData deviceData;
          try {
            deviceData = this.mapSingleTrackerData(dataStream);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          dataList.add(deviceData);
        });

    //    while (inputStream.available() > 2) {
    //      var deviceData = this.mapSingleTrackerData(inputStream);
    //      dataList.add(deviceData);
    //    }

    return dataList;
  }

  public List<TrackerDeviceData> deserialize1(InputStream inputStream) throws IOException {
    ByteArrayOutputStream deviceDataOutputStream = new ByteArrayOutputStream();
    while (inputStream.available() > 0) {
      deviceDataOutputStream.write(inputStream.read());
    }

    System.out.println("Collected byte array : " + deviceDataOutputStream.toByteArray().length);

    List<TrackerDeviceData> dataList = new ArrayList<>();

    TrackerDeviceData trackerDeviceData = new TrackerDeviceData();
    byte[] buffer = new byte[2];

    // Read header (not needed, just to move the stream forward)
    inputStream.read(buffer);

    // Read and parse length (not needed in this example, but could be used to check message
    // integrity)
    inputStream.read(buffer);

    // Read and parse ID
    byte[] idBuffer = new byte[7];
    inputStream.read(idBuffer);
    String id = bytesToHex(idBuffer);
    trackerDeviceData.setId(id);

    // Read and parse command
    inputStream.read(buffer);
    String command = bytesToHex(buffer);
    trackerDeviceData.setCommand(command);

    // Here it gets tricky because we need to read an undefined number of bytes until we reach the
    // checksum
    // Assuming the checksum is always 2 bytes, we can read until the stream has 2 bytes left
    ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
    while (inputStream.available() > 2) {
      dataOutputStream.write(inputStream.read());
    }
    String data = bytesToHex(dataOutputStream.toByteArray());
    trackerDeviceData.setData(data);

    // Read and parse checksum
    inputStream.read(buffer);
    String checksum = bytesToHex(buffer);
    trackerDeviceData.setChecksum(checksum);
    dataList.add(trackerDeviceData);

    //    var arrayList = this.splitByteArray(inputStream.readAllBytes());
    //    System.out.println("Received bytes list: "+arrayList.size());
    return dataList;
  }

  private String bytesToHex(byte[] bytes) {
    StringBuilder hexString = new StringBuilder();
    for (byte b : bytes) {
      hexString.append(String.format("%02X", b));
    }
    return hexString.toString();
  }

  private TrackerDeviceData getSingleDeviceData(
      byte[] idBuffer, byte[] commandBuffer, byte[] dataBuffer, byte[] checksumBuffer) {
    String id = this.bytesToHex(idBuffer);
    String command = this.bytesToHex(commandBuffer);
    String data = this.bytesToHex(dataBuffer);
    String checksum = this.bytesToHex(checksumBuffer);
    return new TrackerDeviceData(id, command, data, checksum);
  }

  public static List<byte[]> splitByteArray(byte[] byteArray) {
    List<byte[]> result = new ArrayList<>();
    int start = 0;

    for (int i = 0; i < byteArray.length - 1; i++) {
      if (byteArray[i] == 0x0D && byteArray[i + 1] == 0x0A) {
        byte[] subArray = new byte[i - start];
        System.arraycopy(byteArray, start, subArray, 0, i - start);
        result.add(subArray);
        i++; // Skip the next byte since we already checked it
        start = i + 1;
      }
    }

    // Handle any bytes after the last \r\n
    if (start < byteArray.length) {
      byte[] subArray = new byte[byteArray.length - start];
      System.arraycopy(byteArray, start, subArray, 0, byteArray.length - start);
      result.add(subArray);
    }

    return result;
  }

  private TrackerDeviceData mapSingleTrackerData(InputStream inputStream) throws IOException {

    TrackerDeviceData trackerDeviceData = new TrackerDeviceData();
    byte[] buffer = new byte[2];
    // Read header (not needed, just to move the stream forward)
    inputStream.read(buffer);

    // Read and parse length (not needed in this example, but could be used to check message
    // integrity)
    inputStream.read(buffer);

    // Read and parse ID
    byte[] idBuffer = new byte[7];
    inputStream.read(idBuffer);
    String id = bytesToHex(idBuffer);
    trackerDeviceData.setId(id);

    // Read and parse command
    inputStream.read(buffer);
    String command = bytesToHex(buffer);
    trackerDeviceData.setCommand(command);

    // Here it gets tricky because we need to read an undefined number of bytes until we reach the
    // checksum
    // Assuming the checksum is always 2 bytes, we can read until the stream has 2 bytes left
    ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
    while (inputStream.available() > 2) {
      dataOutputStream.write(inputStream.read());
    }
    String data = bytesToHex(dataOutputStream.toByteArray());
    trackerDeviceData.setData(data);

    // Read and parse checksum
    inputStream.read(buffer);
    String checksum = bytesToHex(buffer);
    trackerDeviceData.setChecksum(checksum);
    return trackerDeviceData;
  }

  public static List<byte[]> splitInputStream(InputStream inputStream) throws IOException {
    List<byte[]> result = new ArrayList<>();
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int prevByte = -1, currentByte;

    while ((currentByte = inputStream.read()) != -1) {
      if (prevByte == 0x0D && currentByte == 0x0A) {
        result.add(buffer.toByteArray());
        buffer.reset();
      } else {
        if (prevByte != -1) {
          buffer.write(prevByte);
        }
      }
      prevByte = currentByte;
    }

    // Handle the last byte and any bytes after the last \r\n
    if (prevByte != -1 && prevByte != 0x0D) {
      buffer.write(prevByte);
    }

    if (buffer.size() > 0) {
      result.add(buffer.toByteArray());
    }

    return result;
  }
}
