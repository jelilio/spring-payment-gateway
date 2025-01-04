package io.github.jelilio.demopaypal.model;

public record CompletedOrder(
    String result, String token, String payerId
) {
  public CompletedOrder(String error) {
    this(error, null, null);
  }
}
