package io.github.jelilio.demopaypal.web;

import io.github.jelilio.demopaypal.model.CompletedOrder;
import io.github.jelilio.demopaypal.model.PaymentOrder;
import io.github.jelilio.demopaypal.service.PaypalService;
import io.github.jelilio.demopaypal.service.impl.PaypalServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/paypal")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {
  private static final Logger LOG = LoggerFactory.getLogger(PaypalServiceImpl.class);

  private final PaypalService paypalService;

  public PaymentController(PaypalService paypalService) {
    this.paypalService = paypalService;
  }

  @PostMapping(value = "/init")
  public PaymentOrder createPayment(@RequestParam("sum") BigDecimal sum) {
    LOG.info("createPayment: fee: {}", sum);
    return paypalService.createPayment(sum);
  }

  @GetMapping(value = "/capture")
  public CompletedOrder completePayment(@RequestParam("token") String token, @RequestParam("PayerID") String payerId) {
    LOG.info("completePayment: token: {}, payerId: {}", token, payerId);
    return paypalService.completePayment(token, payerId);
  }
}
