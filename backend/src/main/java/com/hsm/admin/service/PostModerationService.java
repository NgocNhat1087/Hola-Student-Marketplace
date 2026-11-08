package com.hsm.admin.service;
import com.hsm.admin.support.AdminSupport;
import com.hsm.admin.repository.PostModerationRepository;
import com.hsm.account.entity.User;
import com.hsm.account.repository.UserRepository;
import com.hsm.common.ApiException;
import com.hsm.admin.support.AdminMailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class PostModerationService extends AdminSupport {
  private final PostModerationRepository repository;
  public PostModerationService(PostModerationRepository db, UserRepository users, AdminMailService email) {
    super(db,users,email); this.repository=db;
  }
  // UC34: post moderation, including approved edits that leave the approved content untouched until acceptance.
  public Object pendingPosts(){
    return Map.of("posts",repository.findPendingPosts(),
      "edits",repository.findPendingEdits());
  }
  @Transactional public Object decidePostItem(long id,String action,String message,User admin){
    String outcome=value(action).toUpperCase(Locale.ROOT);require(Set.of("APPROVE","REJECT").contains(outcome),"Choose APPROVE or REJECT.");
    String why=outcome.equals("REJECT")?reason(message):null;
    Map<String,Object> p=one(repository.SQL_03,id);
    require("PENDING".equals(p.get("approval_status")),"Only pending posts can be moderated.");
    if(outcome.equals("APPROVE")){
      int days=repository.queryForObject(repository.SQL_04,Integer.class);
      repository.update(repository.SQL_05,admin.getId(),days,id);
    } else repository.update(repository.SQL_06,why,admin.getId(),id);
    notifyUser(number(p.get("seller_id")),outcome.equals("APPROVE")?"POST_APPROVED":"POST_REJECTED","Post moderation",outcome.equals("APPROVE")?"Your post has been approved.":"Your post was rejected: "+why,"POST",id);
    audit(admin,"POST",id,outcome,why);return Map.of("id",id,"status",outcome.equals("APPROVE")?"APPROVED":"REJECTED");
  }
  @Transactional public Object decidePostEdit(long id,String action,String message,User admin){
    String outcome=value(action).toUpperCase(Locale.ROOT);require(Set.of("APPROVE","REJECT").contains(outcome),"Choose APPROVE or REJECT.");
    String why=outcome.equals("REJECT")?reason(message):null;
    Map<String,Object> e=one(repository.SQL_07,id);
    require("PENDING".equals(e.get("edit_status")),"Only pending edits can be moderated.");
    long postId=number(e.get("post_id"));
    if(outcome.equals("APPROVE")){
      repository.update(repository.SQL_08,e.get("title"),e.get("description"),e.get("category_id"),postId);
      repository.update(repository.SQL_09,postId);
      repository.update(repository.SQL_10,postId,id);
      repository.update(repository.SQL_11,postId);
      repository.update(repository.SQL_12,postId,id);
    }
    repository.update(repository.SQL_13,outcome.equals("APPROVE")?"APPROVED":"REJECTED",why,admin.getId(),id);
    long owner=number(one(repository.SQL_14,postId).get("seller_id"));
    notifyUser(owner,"POST_EDIT_REVIEWED","Post edit reviewed",outcome.equals("APPROVE")?"Your changes were approved.":"Changes rejected: "+why,"POST",postId);
    audit(admin,"POST_EDIT",id,outcome,why);return Map.of("id",id,"status",outcome.equals("APPROVE")?"APPROVED":"REJECTED");
  }


}
