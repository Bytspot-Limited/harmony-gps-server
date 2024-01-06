package tech.bytespot.hamonygpsserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@SpringBootApplication
public class HamonyGpsServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(HamonyGpsServerApplication.class, args);
  }

  @GetMapping("/")
  public String getMessage() {
    return "Hello World!";
  }


}
