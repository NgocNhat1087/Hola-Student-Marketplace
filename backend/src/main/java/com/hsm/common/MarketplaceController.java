package com.hsm.common;

import com.hsm.account.entity.User;
import com.hsm.security.SessionFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/marketplace")
public class MarketplaceController {
  private final JdbcTemplate db;
  public MarketplaceController(JdbcTemplate db){this.db=db;}
  private long me(HttpServletRequest r){User u=(User)r.getAttribute(SessionFilter.USER);if(u==null)throw new ApiException(401,"UNAUTHENTICATED","Please log in");return u.getId();}
  private void post(long id){if(db.queryForObject("SELECT COUNT(*) FROM posts WHERE post_id=?",Integer.class,id)==0)throw new ApiException(404,"NOT_FOUND","Post not found");}
  private void user(long id){if(db.queryForObject("SELECT COUNT(*) FROM users WHERE user_id=?",Integer.class,id)==0)throw new ApiException(404,"NOT_FOUND","User not found");}
  private String content(String s,int max){if(s==null||s.isBlank()||s.trim().length()>max)throw new ApiException(400,"INVALID_CONTENT","Content must be 1-"+max+" characters");return s.trim();}
  public record TextBody(String content){}
  public record ReportBody(String reason){}

  // UC19: public comments; only author can edit or soft-delete.
  @GetMapping("/posts/{postId}/comments") public List<Map<String,Object>> comments(@PathVariable long postId){post(postId);return db.queryForList("SELECT comment_id AS id,post_id AS postId,user_id AS userId,comment_content AS content,created_at AS createdAt,updated_at AS updatedAt FROM comments WHERE post_id=? AND is_deleted=0 ORDER BY created_at DESC",postId);}
  @PostMapping("/posts/{postId}/comments") public Map<String,Object> comment(@PathVariable long postId,@RequestBody TextBody b,HttpServletRequest r){long id=me(r);post(postId);db.update("INSERT INTO comments(post_id,user_id,comment_content) VALUES(?,?,?)",postId,id,content(b.content(),2000));return Map.of("message","Comment added");}
  @PutMapping("/comments/{id}") public Map<String,Object> edit(@PathVariable long id,@RequestBody TextBody b,HttpServletRequest r){int n=db.update("UPDATE comments SET comment_content=? WHERE comment_id=? AND user_id=? AND is_deleted=0",content(b.content(),2000),id,me(r));if(n==0)throw new ApiException(404,"NOT_FOUND","Comment not found or not owned");return Map.of("message","Comment updated");}
  @DeleteMapping("/comments/{id}") public Map<String,Object> delete(@PathVariable long id,HttpServletRequest r){int n=db.update("UPDATE comments SET is_deleted=1 WHERE comment_id=? AND user_id=? AND is_deleted=0",id,me(r));if(n==0)throw new ApiException(404,"NOT_FOUND","Comment not found or not owned");return Map.of("message","Comment deleted");}

  // UC20: favorites, unique by (user_id,post_id).
  @GetMapping("/favorites") public List<Map<String,Object>> favorites(HttpServletRequest r){return db.queryForList("SELECT f.post_id AS postId,p.title,p.reference_price AS price,f.created_at AS createdAt FROM favorites f JOIN posts p ON p.post_id=f.post_id WHERE f.user_id=? ORDER BY f.created_at DESC",me(r));}
  @PostMapping("/favorites/{postId}") public Map<String,Object> favorite(@PathVariable long postId,HttpServletRequest r){long id=me(r);post(postId);db.update("INSERT IGNORE INTO favorites(user_id,post_id) VALUES(?,?)",id,postId);return Map.of("message","Added to favorites");}
  @DeleteMapping("/favorites/{postId}") public Map<String,Object> unfavorite(@PathVariable long postId,HttpServletRequest r){db.update("DELETE FROM favorites WHERE user_id=? AND post_id=?",me(r),postId);return Map.of("message","Removed from favorites");}

  // UC21: follow/unfollow and following list.
  @GetMapping("/following") public List<Map<String,Object>> following(HttpServletRequest r){return db.queryForList("SELECT f.followed_id AS userId,u.email,f.created_at AS createdAt FROM follows f JOIN users u ON u.user_id=f.followed_id WHERE f.follower_id=? ORDER BY f.created_at DESC",me(r));}
  @PostMapping("/following/{userId}") public Map<String,Object> follow(@PathVariable long userId,HttpServletRequest r){long id=me(r);user(userId);if(id==userId)throw new ApiException(400,"SELF_FOLLOW","Cannot follow yourself");db.update("INSERT IGNORE INTO follows(follower_id,followed_id) VALUES(?,?)",id,userId);return Map.of("message","Following user");}
  @DeleteMapping("/following/{userId}") public Map<String,Object> unfollow(@PathVariable long userId,HttpServletRequest r){db.update("DELETE FROM follows WHERE follower_id=? AND followed_id=?",me(r),userId);return Map.of("message","Unfollowed user");}
  @GetMapping("/users/{userId}/posts") public List<Map<String,Object>> userPosts(@PathVariable long userId){user(userId);return db.queryForList("SELECT post_id AS id,title,reference_price AS price,created_at AS createdAt FROM posts WHERE seller_id=? AND approval_status='APPROVED' AND display_status='VISIBLE' ORDER BY created_at DESC",userId);}

  // UC22: in-app notifications (existing DB enum supports SYSTEM).
  @GetMapping("/notifications") public List<Map<String,Object>> notifications(HttpServletRequest r){return db.queryForList("SELECT notification_id AS id,notification_type AS type,title,content,is_read AS isRead,created_at AS createdAt FROM notifications WHERE user_id=? ORDER BY created_at DESC LIMIT 100",me(r));}
  @PutMapping("/notifications/{id}/read") public Map<String,Object> read(@PathVariable long id,HttpServletRequest r){int n=db.update("UPDATE notifications SET is_read=1 WHERE notification_id=? AND user_id=?",id,me(r));if(n==0)throw new ApiException(404,"NOT_FOUND","Notification not found");return Map.of("message","Marked as read");}
  @PutMapping("/notifications/read-all") public Map<String,Object> readAll(HttpServletRequest r){int n=db.update("UPDATE notifications SET is_read=1 WHERE user_id=? AND is_read=0",me(r));return Map.of("updated",n);}

  // UC23: one report per reporter/post, reason mandatory.
  @PostMapping("/posts/{postId}/reports") public Map<String,Object> report(@PathVariable long postId,@RequestBody ReportBody b,HttpServletRequest r){long id=me(r);post(postId);String reason=content(b.reason(),1000);try{db.update("INSERT INTO violation_reports(post_id,reporter_id,report_reason,report_status) VALUES(?,?,?,'PENDING')",postId,id,reason);}catch(DuplicateKeyException e){throw new ApiException(409,"ALREADY_REPORTED","You already reported this post");}return Map.of("message","Report submitted");}
  @GetMapping("/reports/mine") public List<Map<String,Object>> myReports(HttpServletRequest r){return db.queryForList("SELECT report_id AS id,post_id AS postId,report_reason AS reason,report_status AS status,created_at AS createdAt FROM violation_reports WHERE reporter_id=? ORDER BY created_at DESC",me(r));}
}
