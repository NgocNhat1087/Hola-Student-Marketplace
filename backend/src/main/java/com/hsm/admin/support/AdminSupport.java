package com.hsm.admin.support;
import com.hsm.account.entity.User;
import com.hsm.account.repository.UserRepository;
import com.hsm.common.ApiException;
import com.hsm.admin.support.AdminMailService;
import com.hsm.admin.repository.AdminJpaOperations;
import java.util.*;

/** Reusable cross-cutting domain operations; no UC34-39 business workflow belongs here. */
public abstract class AdminSupport {
  protected final AdminJpaOperations db;
  protected final UserRepository users;
  protected final AdminMailService email;
  protected AdminSupport(AdminJpaOperations db,UserRepository users,AdminMailService email){this.db=db;this.users=users;this.email=email;}
  protected ApiException invalid(String msg){return new ApiException(400,"INVALID_ACTION",msg);}
  protected void require(boolean valid,String msg){if(!valid)throw invalid(msg);}
  protected String reason(String s){require(s!=null&&!s.isBlank(),"A reason is required.");return s.trim();}
  protected Map<String,Object> one(String sql,Object... args){List<Map<String,Object>> result=db.queryForList(sql,args);if(result.isEmpty())throw new ApiException(404,"NOT_FOUND","The requested record does not exist.");return result.get(0);}
  protected long number(Object x){return x instanceof Boolean b?(b?1:0):((Number)x).longValue();}
  protected String value(Object x){return Objects.toString(x,"");}
  protected void audit(User admin,String target,long id,String action,String why){db.update(db.SHARED_SQL_01,admin.getId(),target,id,action,why);}
  protected void notifyUser(long userId,String type,String title,String body,String relatedType,long relatedId){db.update(db.SHARED_SQL_02,userId,type,title,body,relatedType,relatedId);}
  protected void inform(long userId,String subject,String body){String address=value(one(db.SHARED_SQL_03,userId).get("email"));email.sendNotification(address,subject,body);}
  protected void history(long orderId,String old,String status,long adminId,String source,String why){db.update(db.SHARED_SQL_04,orderId,old,status,adminId,source,why);}


}
