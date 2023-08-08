//package tech.bytespot.hamonygpsserver.resource;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.integration.annotation.MessageEndpoint;
//import org.springframework.integration.annotation.ServiceActivator;
//import org.springframework.integration.annotation.Transformer;
//
///**
// * * This is an "api" to receive coordinates from a given tracking device
// *
// * @author: eli.muraya (https://github.com/elimuraya95))
// * @date: 26/07/2023
// */
//@Slf4j
//@MessageEndpoint
//public class TrackerCoordinatesResource {
//
//  @Transformer(inputChannel = "fromTcp", outputChannel = "toEcho")
//  public String convert(byte[] bytes) {
//    var hex = this.byteToHex(bytes);
//    log.info("Bytes to Hexadecimal: \n{}", hex);
//    log.info("Hexadecimal to ASCII: \n{}", hexToAscii(hex));
//
////    log.info("Received data---: Byte Length: {} \n{}", bytes.length, asciiByteArrayToString(bytes));
//    // log.info("\n{}", bytes);
//
//    return new String(bytes);
//  }
//
//  @ServiceActivator(inputChannel = "toEcho")
//  public String upCase(String in) {
//    return in.toUpperCase();
//  }
//
//  @Transformer(inputChannel = "resultToString")
//  public String convertResult(byte[] bytes) {
//    log.info("Received data: {}", bytes.toString());
//    return new String(bytes);
//  }
//
//  public static String asciiByteArrayToString(byte[] byteArray) {
//    StringBuilder stringBuilder = new StringBuilder();
//
//    for (byte b : byteArray) {
//      // Convert the decimal ASCII code to a character
//      char character = (char) (b & 0xFF);
//      //      log.info("signed: {}, un-signed: {}", b, (b & 0xFF));
//      stringBuilder.append(character);
//
//      if (b == 0 || b == 1 || b == 2 || b == 3 || b == 4 || b == 5 || b == 6 || b == 7 || b == 8
//          || b == 9 || b == 10 || b == 11 || b == 12 || b == 13 || b == 14 || b == 15 || b == 16
//          || b == 17 || b == 18 || b == 19 || b == 20 || b == 21 || b == 22 || b == 23 || b == 24
//          || b == 25 || b == 26 || b == 27 || b == 28 || b == 29 || b == 30 || b == 31) {
//        //        log.info("Skipping : {}", b);
//      } else {
//        // Append the character to the string
//        // stringBuilder.append(character);
//      }
//    }
//
//    return stringBuilder.toString();
//  }
//
//  private String byteToHex(byte[] byteArray) {
//    StringBuilder sb = new StringBuilder();
//    for (byte b : byteArray) {
//      sb.append(String.format("%02X ", b));
//    }
//    log.info(sb.toString());
//
//    return sb.toString();
//  }
//
//  private static String hexToAscii(String hexStr) {
//    StringBuilder output = new StringBuilder("");
//
//    for (int i = 0; i < hexStr.length(); i += 2) {
//      String str = hexStr.substring(i, i + 2);
//      output.append((char) Integer.parseInt(str, 16));
//    }
//
//    return output.toString();
//  }
//}
