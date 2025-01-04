package io.github.jelilio.demopaypal.web;

import io.github.jelilio.demopaypal.model.CompletedOrder;
import io.github.jelilio.demopaypal.model.PaymentOrder;
import io.github.jelilio.demopaypal.service.PaypalService;
import io.github.jelilio.demopaypal.service.StripeService;
import io.github.jelilio.demopaypal.service.impl.PaypalServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/stripe")
@CrossOrigin(origins = "http://localhost:4200")
public class StripePaymentController {
  private static final Logger LOG = LoggerFactory.getLogger(StripePaymentController.class);

  private final StripeService stripeService;

  public StripePaymentController(StripeService stripeService) {
    this.stripeService = stripeService;
  }

  @PostMapping(value = "/init")
  public PaymentOrder createPayment(@RequestParam("sum") BigDecimal sum) {
    LOG.info("createPayment: fee: {}", sum);
    return stripeService.createPayment(sum);
  }

  @GetMapping(value = "/success")
  public CompletedOrder completePayment(@RequestParam("session_id") String sessionId) {
    LOG.info("completePayment: sessionId: {}", sessionId);
    return stripeService.completePayment(sessionId);
  }

  @GetMapping(value = "/failure")
  public CompletedOrder failedPayment(@RequestParam("session_id") String sessionId) {
    LOG.info("failedPayment: sessionId: {}", sessionId);
    return stripeService.completePayment(sessionId);
  }
}
