package tech.bytespot.hamonygpsserver.version2;

import org.springframework.core.serializer.Deserializer;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomGpsDeserializer implements Deserializer<List<TrackerDeviceData>> {

  public List<TrackerDeviceData> deserialize1(InputStream inputStream) throws IOException {
    List<TrackerDeviceData> deviceDataList = new ArrayList<>();
    TrackerDeviceData trackerDeviceData = new TrackerDeviceData();

    byte[] byteArray = inputStream.readAllBytes();
    System.out.println("Received bytes: " + (byteArray.length));

    for (int i = 0; i < byteArray.length - 1; i++) {
      if (byteArray[i] == 0x0D && byteArray[i + 1] == 0x0A) {
        // Found the sequence \r\n
        System.out.println("Found \\r\\n at position " + i);
      }
    }

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

    deviceDataList.add(trackerDeviceData);

    return deviceDataList;
  }

  @Override
  public List<TrackerDeviceData> deserialize(InputStream inputStream) throws IOException {
    byte[] byteArray = inputStream.readAllBytes();
    System.out.println("Received bytes: " + byteArray.length);

    for (int i = 0; i < byteArray.length - 1; i++) {
      if (byteArray[i] == 0x0D && byteArray[i + 1] == 0x0A) {
        // Found the sequence \r\n
        System.out.println("Found \\r\\n at position " + i);
      }
    }

    List<TrackerDeviceData> deviceDataList = new ArrayList<>();
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

    deviceDataList.add(trackerDeviceData);

    return deviceDataList;
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
}
