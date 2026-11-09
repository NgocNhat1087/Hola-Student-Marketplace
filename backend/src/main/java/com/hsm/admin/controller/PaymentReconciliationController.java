package com.hsm.admin.controller;
import com.hsm.admin.support.AdminAuthorization;
import com.hsm.admin.dto.AdminDtos;
import com.hsm.admin.service.PaymentReconciliationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/** UC 38 entry point matching the integrated communication diagram. */
@RestController
@RequestMapping("/api/admin")
public class PaymentReconciliationController extends AdminAuthorization {
  private final PaymentReconciliationService service;
  public PaymentReconciliationController(PaymentReconciliationService service){this.service=service;}
  @GetMapping("/payments/reconciliation") public Object reconciliation(HttpServletRequest r){admin(r);return service.reconciliation();}
  @PostMapping("/payments/funds/{id}/reconcile") public Object reconcile(@PathVariable Long id,@Valid @RequestBody AdminDtos.Reconciliation x,HttpServletRequest r){return service.reconcile(id,x.action(),x.transactionId(),x.reason(),x.refundReference(),admin(r));}
}
