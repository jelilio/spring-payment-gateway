package io.github.jelilio.demopaypal;

import io.github.jelilio.demopaypal.config.StripeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({StripeProperties.class})
public class DemoPaypalApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoPaypalApplication.class, args);
  }

}
