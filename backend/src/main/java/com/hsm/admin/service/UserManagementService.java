package com.hsm.admin.service;
import com.hsm.admin.support.AdminSupport;
import com.hsm.admin.repository.UserManagementRepository;
import com.hsm.account.entity.User;
import com.hsm.account.repository.UserRepository;
import com.hsm.common.ApiException;
import com.hsm.admin.support.AdminMailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class UserManagementService extends AdminSupport {
  private final UserManagementRepository repository;
  public UserManagementService(UserManagementRepository db, UserRepository users, AdminMailService email) {
    super(db,users,email); this.repository=db;
  }
  // UC37: warn / lock / unlock; lock cancels pending orders and offers and freezes confirmed orders.
  public Object users(String q){String search="%"+(q==null?"":q.trim())+"%";return repository.searchUsers(search);}
  @Transactional public Object sanction(long id,String action,String explanation,User admin){
    String a=value(action).toUpperCase(Locale.ROOT);require(Set.of("WARN","LOCK","UNLOCK").contains(a),"Choose WARN, LOCK or UNLOCK.");
    String why=a.equals("UNLOCK")?value(explanation):reason(explanation);
    User target=users.findById(id).orElseThrow(()->new ApiException(404,"USER_NOT_FOUND","User not found."));
    require(!idEquals(id,admin.getId()) && target.getRole()!=User.Role.ADMIN,"Administrators cannot be sanctioned.");
    if(a.equals("LOCK")){
      require(target.getStatus()!=User.Status.LOCKED,"User already locked.");
      repository.update(repository.SQL_02,id);
      repository.update(repository.SQL_03,id,admin.getId(),why);
      // Preserve statuses and history for each cancelled order.
      var pending=repository.queryForList(repository.SQL_04,id,id);
      for(var o:pending){long oid=number(o.get("order_id"));repository.update(repository.SQL_05,oid);history(oid,"PENDING_CONFIRMATION","CANCELLED",admin.getId(),"ACCOUNT_LOCK","Account locked");notifyOtherParty(o,id,oid,"Order cancelled because an account was locked.");}
      var confirmed=repository.queryForList(repository.SQL_06,id,id);
      for(var o:confirmed){long oid=number(o.get("order_id"));repository.update(repository.SQL_07,oid);history(oid,"CONFIRMED","CONFIRMED",admin.getId(),"ACCOUNT_LOCK","Frozen because account locked");notifyOtherParty(o,id,oid,"Order frozen because an account was locked.");}
      var openOffers=repository.queryForList(repository.SQL_08,id,id);
      for(var offer:openOffers)cancelOffer(offer,"CANCELLED",admin,id);
      var acceptedOffers=repository.queryForList(repository.SQL_09,id,id);
      for(var offer:acceptedOffers)cancelOffer(offer,"VOID",admin,id);
      repository.update(repository.SQL_10,id);
    } else if(a.equals("UNLOCK")){
      require(target.getStatus()==User.Status.LOCKED,"User is not locked.");
      repository.update(repository.SQL_11,id);
      repository.update(repository.SQL_12,id);
      var frozen=repository.queryForList(repository.SQL_13,id,id);
      for(var o:frozen){long oid=number(o.get("order_id"));
        long locked=repository.queryForObject(repository.SQL_14,Long.class,oid);
        if(locked==0){repository.update(repository.SQL_15,oid);history(oid,"CONFIRMED","CONFIRMED",admin.getId(),"ADMIN","Order unfrozen after unlocking");}
      }
      repository.update(repository.SQL_16,id);
    } else repository.update(repository.SQL_17,id,admin.getId(),why);
    notifyUser(id,"ACCOUNT_SANCTION","Account management","Action: "+a+". "+why,"USER",id);
    if(!a.equals("UNLOCK"))inform(id,"HSM account "+a.toLowerCase(Locale.ROOT),"Your account received action "+a+". Reason: "+why);
    audit(admin,"USER",id,a,why);return Map.of("id",id,"status",a);
  }
  private void cancelOffer(Map<String,Object> offer,String status,User admin,long lockedUserId){
    long oid=number(offer.get("offer_id"));String old=value(offer.get("offer_status"));
    repository.update(repository.SQL_18,status,oid);
    repository.update(repository.SQL_19,oid,old,status,admin.getId(),"Account locked");
    long buyer=number(offer.get("buyer_id")),seller=number(offer.get("seller_id"));
    long other=buyer==lockedUserId?seller:buyer;
    notifyUser(other,"OFFER_RESPONDED","Offer cancelled","Offer #"+oid+" was "+status+" because the other account was locked.","PRICE_OFFER",oid);
  }
  private boolean idEquals(Long a,Long b){return Objects.equals(a,b);}
  private void notifyOtherParty(Map<String,Object> o,long locked,long orderId,String content){long buyer=number(o.get("buyer_id")),seller=number(o.get("seller_id"));long other=buyer==locked?seller:buyer;notifyUser(other,"ORDER_STATUS","Order status changed",content,"ORDER",orderId);}
  public Object frozenOrders(long user){return repository.queryForList(repository.SQL_20,user,user);}
  @Transactional public Object resolveFrozen(long id,String action,String explanation,User admin){String a=value(action).toUpperCase(Locale.ROOT);require(Set.of("COMPLETED","CANCELLED").contains(a),"Choose COMPLETED or CANCELLED.");String why=reason(explanation);closeFrozen(id,a,why,admin);return Map.of("id",id,"status",a);}
  public void closeFrozen(long id,String state,String why,User admin){var o=one(repository.SQL_21,id);require("CONFIRMED".equals(o.get("order_status"))&&number(o.get("is_frozen"))==1,"Order must be confirmed and frozen.");
    if(state.equals("COMPLETED")){repository.update(repository.SQL_22,id);repository.update(repository.SQL_23,o.get("post_id"));}
    else repository.update(repository.SQL_24,why,id);
    history(id,"CONFIRMED",state,admin.getId(),"ADMIN",why);for(Object member:List.of(o.get("buyer_id"),o.get("seller_id")))notifyUser(number(member),"ORDER_STATUS","Frozen order resolved","Order #"+id+" was "+state+". "+why,"ORDER",id);
    audit(admin,"ORDER",id,"CLOSE_"+state,why);
  }


}
