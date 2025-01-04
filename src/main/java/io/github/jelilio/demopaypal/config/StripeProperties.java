package io.github.jelilio.demopaypal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("stripe")
public record StripeProperties(
    String secretKey
) {
}
