package com.hsm.admin.service;
import com.hsm.admin.support.AdminSupport;
import com.hsm.admin.repository.ComplaintRepository;
import com.hsm.account.entity.User;
import com.hsm.account.repository.UserRepository;
import com.hsm.common.ApiException;
import com.hsm.admin.support.AdminMailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class ComplaintService extends AdminSupport {
  private final UserManagementService userManagementService;
  private final ComplaintRepository repository;
  public ComplaintService(ComplaintRepository db, UserRepository users, AdminMailService email, UserManagementService userManagementService) {
    super(db,users,email); this.repository=db; this.userManagementService=userManagementService;
  }
  // UC36: complaint decision, history, both parties notified via in-app + email.
  public Object complaints(String status){require(Set.of("PENDING","RESOLVED","ALL").contains(status),"Invalid status.");return repository.findComplaints(status);}
  @Transactional public Object decideComplaint(long id,String decision,String orderOutcome,String explanation,User admin){
    String why=reason(explanation);String result=reason(decision);
    var c=one(repository.SQL_02,id);
    require("PENDING".equals(c.get("complaint_status")),"Complaint already decided.");
    long orderId=number(c.get("order_id"));
    if(orderOutcome!=null&&!orderOutcome.isBlank()){
      String state=orderOutcome.toUpperCase(Locale.ROOT);require(Set.of("COMPLETED","CANCELLED").contains(state),"Invalid order outcome.");
      require(number(c.get("is_frozen"))==1 && "CONFIRMED".equals(c.get("order_status")),"Only a frozen confirmed order can be closed by the admin.");
      userManagementService.closeFrozen(orderId,state,why,admin);
    }
    repository.update(repository.SQL_03,admin.getId(),result+" | "+why,id);
    repository.update(repository.SQL_04,id,admin.getId(),result+" | "+why);
    for(long member: new long[]{number(c.get("buyer_id")),number(c.get("seller_id"))}){
      notifyUser(member,"COMPLAINT_RESULT","Complaint resolved",result+". "+why,"COMPLAINT",id);
      inform(member,"HSM complaint decision","Complaint #"+id+": "+result+". "+why);
    }
    audit(admin,"COMPLAINT",id,"RESOLVE",why);return Map.of("id",id,"status","RESOLVED");
  }


}
