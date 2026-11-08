package com.hsm.admin.dto;
import jakarta.validation.constraints.*;

/** API input DTOs shared by the six administration use cases. */
public final class AdminDtos {
  private AdminDtos(){}
  public record Decision(@NotBlank String action, String reason) {}
  public record ComplaintDecision(@NotBlank String decision, String orderOutcome, String reason) {}
  public record Sanction(@NotBlank String action, String reason) {}
  public record Reconciliation(@NotBlank String action, Long transactionId, String reason, String refundReference) {}
  public record BankAccount(@NotBlank String bankName, @NotBlank String accountNumber, @NotBlank String accountHolder) {}
  public record Enabled(boolean enabled) {}
  public record Result(long id,String status) {}
}
