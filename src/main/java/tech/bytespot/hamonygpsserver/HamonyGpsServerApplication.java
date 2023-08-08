package tech.bytespot.hamonygpsserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class HamonyGpsServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(HamonyGpsServerApplication.class, args);
  }

  @GetMapping("/hello")
  public String getMessage() {
    return "Hello World!";
  }
}
