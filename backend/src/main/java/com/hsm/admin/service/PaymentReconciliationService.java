package com.hsm.admin.service;
import com.hsm.admin.support.AdminSupport;
import com.hsm.admin.repository.PaymentReconciliationRepository;
import com.hsm.account.entity.User;
import com.hsm.account.repository.UserRepository;
import com.hsm.common.ApiException;
import com.hsm.admin.support.AdminMailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class PaymentReconciliationService extends AdminSupport {
  private final PaymentReconciliationRepository repository;
  public PaymentReconciliationService(PaymentReconciliationRepository db, UserRepository users, AdminMailService email) {
    super(db,users,email); this.repository=db;
  }
  // UC38: gateway funds are persisted by the gateway's own integration; admin only resolves them here.
  public Object reconciliation(){return Map.of("funds",repository.findUnreconciledFunds(),"transactions",repository.findReconciliationTransactions());}
  @Transactional public Object reconcile(long fundId,String action,Long txId,String explanation,String refundRef,User admin){
    String a=value(action).toUpperCase(Locale.ROOT);require(Set.of("ASSIGN","REFUND","ACTIVATE").contains(a),"Choose ASSIGN, REFUND or ACTIVATE.");String why=reason(explanation);
    var fund=one(repository.SQL_03,fundId);require(!"RECONCILED".equals(fund.get("fund_status")),"Fund has already been reconciled.");
    Long tx=txId!=null?txId:(fund.get("boost_transaction_id")==null?null:number(fund.get("boost_transaction_id")));
    if(a.equals("REFUND")){require(refundRef!=null&&!refundRef.isBlank(),"Refund reference is required.");repository.update(repository.SQL_04,refundRef.trim(),admin.getId(),fundId);}
    else {
      require(tx!=null,"Select a transaction to assign the fund.");var transaction=one(repository.SQL_05,tx);
      require(((java.math.BigDecimal)fund.get("amount")).compareTo((java.math.BigDecimal)transaction.get("amount"))==0,"The payment amount does not match; refund instead.");
      if(a.equals("ACTIVATE")){
        require("APPROVED".equals(transaction.get("approval_status"))&&"FOR_SALE".equals(transaction.get("sale_status")),"The post is no longer eligible.");
        long active=repository.queryForObject(repository.SQL_06,Long.class,transaction.get("post_id"));require(active==0,"The post already has an active boost.");
        repository.update(repository.SQL_07,tx);
        notifyUser(number(transaction.get("seller_id")),"BOOST_PAYMENT_SUCCESS","Boost activated","Your boost was manually activated after reconciliation.","BOOST_TRANSACTION",tx);
      } else repository.update(repository.SQL_08,tx);
      repository.update(repository.SQL_09,tx,admin.getId(),fundId);
    }
    audit(admin,"INCOMING_FUND",fundId,a,why);return Map.of("id",fundId,"status","RECONCILED");
  }


}
