package tech.bytespot.hamonygpsserver.version2;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;
import tech.bytespot.hamonygpsserver.version2.utils.CoordinateConverter;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GpsServiceActivator {
  @ServiceActivator(inputChannel = "tcpIn")
  public void handle(List<TrackerDeviceData> dataList) {
    // Handle the GpsData...

    // You can save the data to your database here.
    log.info("Received payload: {}", dataList);

    if (dataList.size() > 0) {
      dataList.forEach(
          trackerDeviceData -> {
            var mappedResponse = mapResponse(trackerDeviceData.getData());
            log.info("Formatted data: {}", mappedResponse);
          });
    }
  }

  public String hexToAscii(String hexStr) {
    StringBuilder output = new StringBuilder("");

    for (int i = 0; i < hexStr.length(); i += 2) {
      String str = hexStr.substring(i, i + 2);
      output.append((char) Integer.parseInt(str, 16));
    }

    return output.toString();
  }

  @Data
  @Builder
  @Getter
  public static class GpsData {
    private String time;
    private String status;
    private String latitude;
    private String longitude;
    private String speedInKnots;
    private String headingDirection;
    private String date;
    private String rfid;
  }

  private List<String> convertStringToStringArray(String dataString) {

    String[] parts = dataString.split("[,\\|]");

    return List.of(parts);
  }

  private GpsData mapResponse(String data) {
    // Convert hexadecimal to ASCII
    String dataString = this.hexToAscii(data);

    // Convert ASCII string to a String array, to get individual values
    List<String> stringList = this.convertStringToStringArray(dataString);

    // Map the collected values to a POJO
    if (stringList.size() >= 9) {

      String rfid = (stringList.get(18) == null) ? "" : this.convertRfid(stringList.get(18));

      StringBuilder latitude = new StringBuilder();
      latitude.append(stringList.get(2)).append(" ").append(stringList.get(3));

      StringBuilder longitude = new StringBuilder();
      longitude.append(stringList.get(4)).append(" ").append(stringList.get(5));

      var coordinates = CoordinateConverter.convertCoordinates(latitude.toString(), longitude.toString());
      return GpsData.builder()
          .time(stringList.get(0))
          .status(stringList.get(1))
          .latitude(String.valueOf(coordinates[0]))
          .longitude(String.valueOf(coordinates[1]))
          .speedInKnots(stringList.get(6))
          .headingDirection(stringList.get(7))
          .date(stringList.get(8))
          .rfid(rfid)
          .build();
    }
    return GpsData.builder().build();
  }

  /**
   * Format the RFID from the hex to decimal, and add filler zeros if length is not 10
   *
   * @param hexRfid
   * @return
   */
  private static String convertRfid(String hexRfid) {
    int decimalValue = Integer.parseInt(hexRfid, 16);
    var rfidString = String.valueOf(decimalValue);
    if (rfidString.length() != 10) {
      int counter = 10 - rfidString.length();
      String filler = "";
      while (counter != 0) {
        filler = filler.concat("0");
        --counter;
      }
      return filler.concat(rfidString);
    }
    return rfidString;
  }
}
