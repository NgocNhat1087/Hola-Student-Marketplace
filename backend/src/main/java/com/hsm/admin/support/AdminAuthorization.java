package com.hsm.admin.support;
import com.hsm.account.entity.User;
import com.hsm.common.ApiException;
import com.hsm.security.SessionFilter;
import jakarta.servlet.http.HttpServletRequest;
public abstract class AdminAuthorization {
  protected User admin(HttpServletRequest request){
    Object value=request.getAttribute(SessionFilter.USER);
    if(!(value instanceof User user)) throw new ApiException(401,"UNAUTHENTICATED","Please log in.");
    if(user.getRole()!=User.Role.ADMIN || user.getStatus()!=User.Status.ACTIVE) throw new ApiException(403,"FORBIDDEN","Admin access required.");
    return user;
  }
}
