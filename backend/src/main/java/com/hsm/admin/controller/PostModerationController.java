package com.hsm.admin.controller;
import com.hsm.admin.support.AdminAuthorization;
import com.hsm.admin.dto.AdminDtos;
import com.hsm.admin.service.PostModerationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/** UC 34 entry point matching the integrated communication diagram. */
@RestController
@RequestMapping("/api/admin")
public class PostModerationController extends AdminAuthorization {
  private final PostModerationService service;
  public PostModerationController(PostModerationService service){this.service=service;}
  @GetMapping("/posts/pending") public Object posts(HttpServletRequest r){admin(r);return service.pendingPosts();}
  @PostMapping("/posts/items/{id}/decision") public Object decidePost(@PathVariable Long id,@Valid @RequestBody AdminDtos.Decision x,HttpServletRequest r){return service.decidePostItem(id,x.action(),x.reason(),admin(r));}
  @PostMapping("/posts/edits/{id}/decision") public Object decideEdit(@PathVariable Long id,@Valid @RequestBody AdminDtos.Decision x,HttpServletRequest r){return service.decidePostEdit(id,x.action(),x.reason(),admin(r));}
}
