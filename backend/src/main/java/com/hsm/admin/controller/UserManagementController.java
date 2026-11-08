package com.hsm.admin.controller;
import com.hsm.admin.support.AdminAuthorization;
import com.hsm.admin.dto.AdminDtos;
import com.hsm.admin.service.UserManagementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/** UC 37 entry point matching the integrated communication diagram. */
@RestController
@RequestMapping("/api/admin")
public class UserManagementController extends AdminAuthorization {
  private final UserManagementService service;
  public UserManagementController(UserManagementService service){this.service=service;}
  @GetMapping("/users") public Object users(@RequestParam(defaultValue="") String q,HttpServletRequest r){admin(r);return service.users(q);}
  @GetMapping("/users/{id}/frozen-orders") public Object frozen(@PathVariable Long id,HttpServletRequest r){admin(r);return service.frozenOrders(id);}
  @PostMapping("/users/{id}/sanction") public Object sanction(@PathVariable Long id,@Valid @RequestBody AdminDtos.Sanction x,HttpServletRequest r){return service.sanction(id,x.action(),x.reason(),admin(r));}
  @PostMapping("/orders/{id}/resolve-frozen") public Object resolve(@PathVariable Long id,@Valid @RequestBody AdminDtos.Decision x,HttpServletRequest r){return service.resolveFrozen(id,x.action(),x.reason(),admin(r));}
}
