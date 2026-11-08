package com.hsm.admin.controller;
import com.hsm.admin.support.AdminAuthorization;
import com.hsm.admin.dto.AdminDtos;
import com.hsm.admin.service.BankAccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/** UC 39 entry point matching the integrated communication diagram. */
@RestController
@RequestMapping("/api/admin")
public class BankAccountController extends AdminAuthorization {
  private final BankAccountService service;
  public BankAccountController(BankAccountService service){this.service=service;}
  @GetMapping("/bank-accounts") public Object bankAccounts(HttpServletRequest r){admin(r);return service.bankAccounts();}
  @PostMapping("/bank-accounts") public Object addBank(@Valid @RequestBody AdminDtos.BankAccount x,HttpServletRequest r){return service.addBank(x.bankName(),x.accountNumber(),x.accountHolder(),admin(r));}
  @PutMapping("/bank-accounts/{id}") public Object editBank(@PathVariable Long id,@Valid @RequestBody AdminDtos.BankAccount x,HttpServletRequest r){return service.editBank(id,x.bankName(),x.accountNumber(),x.accountHolder(),admin(r));}
  @PostMapping("/bank-accounts/{id}/enabled") public Object enableBank(@PathVariable Long id,@Valid @RequestBody AdminDtos.Enabled x,HttpServletRequest r){return service.enableBank(id,x.enabled(),admin(r));}
}
