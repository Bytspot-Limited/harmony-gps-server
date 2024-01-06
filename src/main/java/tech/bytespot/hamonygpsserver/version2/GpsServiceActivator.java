package tech.bytespot.hamonygpsserver.version2;

import java.util.List;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import tech.bytespot.hamonygpsserver.version2.utils.CoordinateConverter;

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
            var mappedResponse = mapResponse(trackerDeviceData);
            this.sendToHarmony(mappedResponse);
            log.info("Formatted data: {}", mappedResponse);
          });
    }
  }

  private void sendToHarmony(GpsData gpsData) {
    var client =
        WebClient.builder()
            .baseUrl("https://harmony-api-d3c63c482f2e.herokuapp.com/api/gps-coordinates")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();

    var response =
        client
            .post()
            .bodyValue(gpsData)
            .retrieve()
            .bodyToMono(GpsData.class)
            .block();
    log.info("GPS coordinates send to harmony successfully: {}", response);
  }

  public String hexToAscii(String hexStr) {
    StringBuilder output = new StringBuilder("");

    for (int i = 0; i < hexStr.length(); i += 2) {
      String str = hexStr.substring(i, i + 2);
      output.append((char) Integer.parseInt(str, 16));
    }

    return output.toString();
  }

  @NoArgsConstructor
  @Setter
  @Getter
  @ToString
  public static class GpsData {
    private String deviceId;
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

  private GpsData mapResponse(TrackerDeviceData data) {
    // Convert hexadecimal to ASCII
    String dataString = this.hexToAscii(data.getData());

    // Convert ASCII string to a String array, to get individual values
    List<String> stringList = this.convertStringToStringArray(dataString);

    // Map the collected values to a POJO
    GpsData gpsData = new GpsData();

    if (stringList.size() >= 9) {

      String rfid = (stringList.get(18) == null) ? "" : this.convertRfid(stringList.get(18));

      StringBuilder latitude = new StringBuilder();
      latitude.append(stringList.get(2)).append(" ").append(stringList.get(3));

      StringBuilder longitude = new StringBuilder();
      longitude.append(stringList.get(4)).append(" ").append(stringList.get(5));

      var coordinates =
          CoordinateConverter.convertCoordinates(latitude.toString(), longitude.toString());
      gpsData.setDeviceId(data.getId());
      gpsData.setTime(stringList.get(0));
      gpsData.setDate(stringList.get(8));
      gpsData.setStatus(stringList.get(1));
      gpsData.setLatitude(String.valueOf(coordinates[0]));
      gpsData.setLongitude(String.valueOf(coordinates[1]));
      gpsData.setSpeedInKnots(stringList.get(6));
      gpsData.setHeadingDirection(stringList.get(7));
      gpsData.setRfid(rfid);
      return gpsData;
    }
    return gpsData;
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
