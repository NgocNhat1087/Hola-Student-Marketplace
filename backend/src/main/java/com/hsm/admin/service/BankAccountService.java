package com.hsm.admin.service;
import com.hsm.admin.support.AdminSupport;
import com.hsm.admin.repository.BankAccountRepository;
import com.hsm.admin.entity.ReceivingBankAccountRecord;
import com.hsm.account.entity.User;
import com.hsm.account.repository.UserRepository;
import com.hsm.common.ApiException;
import com.hsm.admin.support.AdminMailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
public class BankAccountService extends AdminSupport {
  private final BankAccountRepository repository;
  public BankAccountService(BankAccountRepository db,UserRepository users,AdminMailService email){super(db,users,email);this.repository=db;}
  @Transactional(readOnly=true)
  public Object bankAccounts(){
    return repository.findNewestFirst().stream().map(b->{
      Map<String,Object> v=new LinkedHashMap<>();
      v.put("id",b.getId());v.put("bank_name",b.getBankName());v.put("account_number",b.getAccountNumber());
      v.put("account_holder",b.getAccountHolder());v.put("is_enabled",b.getEnabled());v.put("created_at",b.getCreatedAt());
      return v;
    }).toList();
  }
  @Transactional
  public Object addBank(String name,String account,String holder,User admin){
    validateBank(name,account,holder);
    var record=repository.saveAndFlush(new ReceivingBankAccountRecord(name.trim(),account.trim(),holder.trim(),admin.getId()));
    audit(admin,"BANK_ACCOUNT",record.getId(),"CREATE",null);
    return Map.of("id",record.getId());
  }
  @Transactional
  public Object editBank(long id,String name,String account,String holder,User admin){
    validateBank(name,account,holder);
    var record=findOrThrow(id);
    record.updateDetails(name.trim(),account.trim(),holder.trim());
    repository.save(record);
    audit(admin,"BANK_ACCOUNT",id,"UPDATE",null);return Map.of("id",id);
  }
  private ReceivingBankAccountRecord findOrThrow(long id){
    return repository.findById(id).orElseThrow(()->new ApiException(404,"BANK_NOT_FOUND","Receiving bank account not found."));
  }
  private void validateBank(String name,String account,String holder){
    require(name!=null&&!name.isBlank()&&holder!=null&&!holder.isBlank()&&account!=null&&account.matches("[0-9]{6,30}"),"Bank name, holder and 6-30 digit account number are required.");
  }
  @Transactional
  public Object enableBank(long id,boolean enabled,User admin){
    // Pessimistic locks serialize updates to existing accounts; DB unique constraint is final safeguard.
    var accounts=repository.lockAllAccounts();
    var selected=accounts.stream().filter(b->Objects.equals(b.getId(),id)).findFirst()
      .orElseThrow(()->new ApiException(404,"BANK_NOT_FOUND","Receiving bank account not found."));
    if(enabled){
      for(var b:accounts)b.setEnabled(false);
      repository.saveAllAndFlush(accounts); // clears any previous enabled flag first
      selected.setEnabled(true);
    } else selected.setEnabled(false);
    repository.saveAndFlush(selected);
    audit(admin,"BANK_ACCOUNT",id,enabled?"ENABLE":"DISABLE",null);
    return Map.of("id",id,"enabled",enabled);
  }
}
