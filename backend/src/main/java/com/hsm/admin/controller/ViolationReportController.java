package com.hsm.admin.controller;
import com.hsm.admin.support.AdminAuthorization;
import com.hsm.admin.dto.AdminDtos;
import com.hsm.admin.service.ViolationReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/** UC 35 entry point matching the integrated communication diagram. */
@RestController
@RequestMapping("/api/admin")
public class ViolationReportController extends AdminAuthorization {
  private final ViolationReportService service;
  public ViolationReportController(ViolationReportService service){this.service=service;}
  @GetMapping("/reports") public Object reports(@RequestParam(defaultValue="PENDING") String status,HttpServletRequest r){admin(r);return service.reports(status);}
  @PostMapping("/reports/{id}/decision") public Object report(@PathVariable Long id,@Valid @RequestBody AdminDtos.Decision x,HttpServletRequest r){return service.decideReport(id,x.action(),x.reason(),admin(r));}
}
