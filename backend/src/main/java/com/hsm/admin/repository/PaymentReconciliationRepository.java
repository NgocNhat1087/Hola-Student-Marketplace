package com.hsm.admin.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hsm.admin.entity.IncomingFundRecord;

import java.util.List;
import java.util.Map;

/** SQL persistence for the PaymentReconciliation subsystem. Schema: database/HSM_Database_Design.sql. */
public interface PaymentReconciliationRepository extends JpaRepository<IncomingFundRecord, Long>, AdminJpaOperations {
  public static final String SQL_01="SELECT f.*,t.transaction_code,t.payment_status FROM incoming_funds f LEFT JOIN boost_transactions t ON t.boost_transaction_id=f.boost_transaction_id WHERE f.fund_status='UNIDENTIFIED' OR (f.fund_status='MATCHED' AND t.payment_status='PENDING_RECONCILIATION') ORDER BY f.received_at DESC";
  public static final String SQL_02="SELECT boost_transaction_id AS id,transaction_code,amount,payment_status,boost_status,post_id FROM boost_transactions WHERE payment_status='PENDING_RECONCILIATION' ORDER BY created_at DESC";
  public static final String SQL_03="SELECT * FROM incoming_funds WHERE fund_id=? FOR UPDATE";
  public static final String SQL_04="UPDATE incoming_funds SET fund_status='RECONCILED',resolution='REFUNDED',refund_reference=?,reconciled_by=?,reconciled_at=NOW() WHERE fund_id=?";
  public static final String SQL_05="SELECT t.*,p.approval_status,p.sale_status FROM boost_transactions t JOIN posts p ON p.post_id=t.post_id WHERE t.boost_transaction_id=? FOR UPDATE";
  public static final String SQL_06="SELECT COUNT(*) FROM boost_transactions WHERE post_id=? AND boost_status='ACTIVE'";
  public static final String SQL_07="UPDATE boost_transactions t JOIN boost_packages b ON b.package_id=t.package_id SET t.payment_status='RECONCILED',t.boost_status='ACTIVE',t.paid_at=COALESCE(t.paid_at,NOW()),t.boost_starts_at=NOW(),t.boost_ends_at=DATE_ADD(NOW(), INTERVAL b.duration_days DAY) WHERE t.boost_transaction_id=?";
  public static final String SQL_08="UPDATE boost_transactions SET payment_status='RECONCILED' WHERE boost_transaction_id=?";
  public static final String SQL_09="UPDATE incoming_funds SET boost_transaction_id=?,fund_status='RECONCILED',resolution='ASSIGNED',reconciled_by=?,reconciled_at=NOW() WHERE fund_id=?";
  default List<Map<String,Object>> findUnreconciledFunds(){return queryForList(SQL_01);}
  default List<Map<String,Object>> findReconciliationTransactions(){return queryForList(SQL_02);}

}
