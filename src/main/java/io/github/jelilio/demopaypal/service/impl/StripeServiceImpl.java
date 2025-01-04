package io.github.jelilio.demopaypal.service.impl;

import com.paypal.http.HttpResponse;
import com.paypal.orders.Order;
import com.paypal.orders.OrdersCaptureRequest;
import com.stripe.param.checkout.SessionRetrieveParams;
import io.github.jelilio.demopaypal.config.StripeProperties;
import io.github.jelilio.demopaypal.model.CompletedOrder;
import io.github.jelilio.demopaypal.model.PaymentOrder;
import io.github.jelilio.demopaypal.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.stripe.Stripe;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

@Service
public class StripeServiceImpl implements StripeService {
  private final StripeProperties stripeProperties;

  private static final Logger LOG = LoggerFactory.getLogger(StripeServiceImpl.class);

  public StripeServiceImpl(StripeProperties stripeProperties) {
    this.stripeProperties = stripeProperties;
  }

  @Override
  public PaymentOrder createPayment(BigDecimal fee) {
    // Next, create a checkout session by adding the details of the checkout
    Stripe.apiKey = stripeProperties.secretKey();

    SessionCreateParams.Builder paramsBuilder =
        SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setCustomer("cus_RPiN96TT4OdU76")
            .setSuccessUrl("http://localhost:8080/stripe/success?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl("http://localhost:8080/stripe/failure?session_id={CHECKOUT_SESSION_ID}");

    paramsBuilder.addLineItem(
        SessionCreateParams.LineItem.builder()
            .setQuantity(1L)
            .setPriceData(
                SessionCreateParams.LineItem.PriceData.builder()
                    .setProductData(
                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .putMetadata("app_id", "prd-1")
                            .setName("prd-1")
                            .build()
                    )
                    .setCurrency("USD")
                    .setUnitAmountDecimal(new BigDecimal(100))
                    .build())
            .build()
    );

    try {
      Session session = Session.create(paramsBuilder.build());
      return new PaymentOrder("success",  session.getId(), session.getUrl());
    } catch (StripeException e) {
      LOG.error("createPayment: error: {}", e.getMessage());
      return new PaymentOrder("Error");
    }
  }

  @Override
  public CompletedOrder completePayment(String sessionId) {
    LOG.info("completePayment: sessionId: {}", sessionId);
    // Next, create a checkout session by adding the details of the checkout
    Stripe.apiKey = stripeProperties.secretKey();

    SessionRetrieveParams params =
        SessionRetrieveParams.builder()
            .addExpand("line_items")
            .build();

    Session checkoutSession = null;
    try {
      checkoutSession = Session.retrieve(sessionId, params, null);
      if (!Objects.equals(checkoutSession.getPaymentStatus(), "unpaid")) {
        return new CompletedOrder("success");
      }
    } catch (StripeException e) {
      LOG.error("completePayment: error: {}", e.getMessage());
    }
    return new CompletedOrder("error");
  }
}
