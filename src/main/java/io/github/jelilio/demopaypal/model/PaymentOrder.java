package io.github.jelilio.demopaypal.model;

public record PaymentOrder(
    String result,
    String orderId,
    String redirectUrl
) {
  public PaymentOrder(String result) {
    this(result, null, null);
  }
}
