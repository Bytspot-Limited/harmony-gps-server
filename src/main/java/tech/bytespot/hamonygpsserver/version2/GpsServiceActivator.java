package tech.bytespot.hamonygpsserver.version2;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GpsServiceActivator {
  @ServiceActivator(inputChannel = "tcpIn")
  public void handle(List<TrackerDeviceData> dataList) {
    // Handle the GpsData...
    var data = dataList.get(0);

    // You can save the data to your database here.
    log.info("Received payload: {}", dataList);

    log.info("Formatted data: {}", mapResponse(data.getData()));
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
    private String coordinate;
    private String direction;

    private String coordinate2;
    private String direction2;

    private String speedInKnots;
    private String headingDirection;
    private String date;
  }

  private List<String> convertStringToStringArray(String dataString) {
    List<String> formattedData = new ArrayList<>();

    while (dataString.contains(",")) {
      String[] parts = dataString.split(",", 2);
      formattedData.add(parts[0]);

      dataString = parts.length > 1 ? parts[1] : "";
    }
    return formattedData;
  }

  private GpsData mapResponse(String data) {
    // Convert hexadecimal to ASCII
    String dataString = this.hexToAscii(data);

    // Convert ASCII string to a String array, to get individual values
    List<String> stringList = this.convertStringToStringArray(dataString);

    // Map the collected values to a POJO
    if (stringList.size() >= 9) {
      return GpsData.builder()
          .time(stringList.get(0))
          .status(stringList.get(1))
          .coordinate(stringList.get(2))
          .direction(stringList.get(3))
          .coordinate2(stringList.get(4))
          .direction2(stringList.get(5))
          .speedInKnots(stringList.get(6))
          .headingDirection(stringList.get(7))
          .date(stringList.get(8))
          .build();
    }
    return GpsData.builder().build();
  }
}
