package io.github.jelilio.demopaypal.service.impl;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import io.github.jelilio.demopaypal.model.CompletedOrder;
import io.github.jelilio.demopaypal.model.PaymentOrder;
import io.github.jelilio.demopaypal.service.PaypalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PaypalServiceImpl implements PaypalService {
  private static final Logger LOG = LoggerFactory.getLogger(PaypalServiceImpl.class);

  private final PayPalHttpClient payPalHttpClient;

  public PaypalServiceImpl(PayPalHttpClient payPalHttpClient) {
    this.payPalHttpClient = payPalHttpClient;
  }

  @Override
  public PaymentOrder createPayment(BigDecimal fee) {
    LOG.info("createPayment: fee: {}", fee);

    OrderRequest orderRequest = new OrderRequest();
    orderRequest.checkoutPaymentIntent("CAPTURE");
    AmountWithBreakdown amountBreakdown = new AmountWithBreakdown().currencyCode("USD").value(fee.toString());
    PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest().amountWithBreakdown(amountBreakdown);
    orderRequest.purchaseUnits(List.of(purchaseUnitRequest));
    ApplicationContext applicationContext = new ApplicationContext()
        .returnUrl("http://localhost:8080/paypal/capture")
        .cancelUrl("http://localhost:8080/paypal/cancel");

    orderRequest.applicationContext(applicationContext);
    OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest().requestBody(orderRequest);

    try {
      HttpResponse<Order> orderHttpResponse = payPalHttpClient.execute(ordersCreateRequest);
      Order order = orderHttpResponse.result();
      LOG.info("createPayment: order: {}", order);

      String redirectUrl = order.links().stream()
          .filter(link -> "approve".equals(link.rel()))
          .findFirst()
          .orElseThrow(NoSuchElementException::new)
          .href();

      return new PaymentOrder("success",  order.id(), redirectUrl);
    } catch (IOException e) {
      LOG.error("createPayment: error: {}", e.getMessage());
      return new PaymentOrder("Error");
    }
  }

  @Override
  public CompletedOrder completePayment(String token, String payerId) {
    LOG.info("completePayment: token: {}, payerId: {}", token, payerId);
    OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(token);
    try {
      HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersCaptureRequest);
      if (httpResponse.result().status() != null) {
        return new CompletedOrder("success", token, payerId);
      }
    } catch (IOException e) {
      LOG.error("completePayment: error: {}", e.getMessage());
    }
    return new CompletedOrder("error");
  }
}
