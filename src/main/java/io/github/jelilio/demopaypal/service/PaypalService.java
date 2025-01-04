package io.github.jelilio.demopaypal.service;

import io.github.jelilio.demopaypal.model.CompletedOrder;
import io.github.jelilio.demopaypal.model.PaymentOrder;

import java.math.BigDecimal;

public interface PaypalService {
  PaymentOrder createPayment(BigDecimal fee);

  CompletedOrder completePayment(String token, String payerId);
}
