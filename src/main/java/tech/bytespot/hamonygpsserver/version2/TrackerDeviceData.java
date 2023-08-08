package tech.bytespot.hamonygpsserver.version2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackerDeviceData {
    private String id;
    private String command;
    private String data;
    private String checksum;
}
