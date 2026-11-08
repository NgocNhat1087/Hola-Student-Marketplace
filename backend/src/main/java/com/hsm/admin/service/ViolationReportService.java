package com.hsm.admin.service;
import com.hsm.admin.support.AdminSupport;
import com.hsm.admin.repository.ViolationReportRepository;
import com.hsm.account.entity.User;
import com.hsm.account.repository.UserRepository;
import com.hsm.common.ApiException;
import com.hsm.admin.support.AdminMailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class ViolationReportService extends AdminSupport {
  private final ViolationReportRepository repository;
  public ViolationReportService(ViolationReportRepository db, UserRepository users, AdminMailService email) {
    super(db,users,email); this.repository=db;
  }
  // UC35: violations and required reporter/post-owner emails.
  public Object reports(String status){require(Set.of("PENDING","POST_REMOVED","DISMISSED","ALL").contains(status),"Invalid status.");return repository.findReports(status);}
  @Transactional public Object decideReport(long id,String action,String explanation,User admin){
    String a=value(action).toUpperCase(Locale.ROOT);require(Set.of("REMOVE","DISMISS").contains(a),"Choose REMOVE or DISMISS.");String why=reason(explanation);
    var r=one(repository.SQL_02,id);
    require("PENDING".equals(r.get("report_status")),"Report already processed.");long post=number(r.get("post_id"));
    if(a.equals("REMOVE"))repository.update(repository.SQL_03,post);
    String status=a.equals("REMOVE")?"POST_REMOVED":"DISMISSED";
    repository.update(repository.SQL_04,status,admin.getId(),why,id);
    audit(admin,"VIOLATION_REPORT",id,a,why);
    inform(number(r.get("reporter_id")),"HSM violation report result","Report #"+id+": "+status+". "+why);
    if(a.equals("REMOVE"))inform(number(r.get("seller_id")),"HSM post removed","Post #"+post+" was removed following a report. "+why);
    return Map.of("id",id,"status",status);
  }


}
