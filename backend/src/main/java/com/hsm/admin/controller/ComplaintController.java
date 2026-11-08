package com.hsm.admin.controller;
import com.hsm.admin.support.AdminAuthorization;
import com.hsm.admin.dto.AdminDtos;
import com.hsm.admin.service.ComplaintService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/** UC 36 entry point matching the integrated communication diagram. */
@RestController
@RequestMapping("/api/admin")
public class ComplaintController extends AdminAuthorization {
  private final ComplaintService service;
  public ComplaintController(ComplaintService service){this.service=service;}
  @GetMapping("/complaints") public Object complaints(@RequestParam(defaultValue="PENDING") String status,HttpServletRequest r){admin(r);return service.complaints(status);}
  @PostMapping("/complaints/{id}/decision") public Object complaint(@PathVariable Long id,@Valid @RequestBody AdminDtos.ComplaintDecision x,HttpServletRequest r){return service.decideComplaint(id,x.decision(),x.orderOutcome(),x.reason(),admin(r));}
}
