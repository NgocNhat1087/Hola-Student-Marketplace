package com.hsm.admin.repository;
import com.hsm.admin.entity.ReceivingBankAccountRecord;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
/** UC39 - all bank-account CRUD uses Spring Data JPA. */
public interface BankAccountRepository extends JpaRepository<ReceivingBankAccountRecord,Long>, AdminJpaOperations {
  @Query("select b from ReceivingBankAccountRecord b order by b.id desc")
  List<ReceivingBankAccountRecord> findNewestFirst();
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select b from ReceivingBankAccountRecord b order by b.id")
  List<ReceivingBankAccountRecord> lockAllAccounts();
}
