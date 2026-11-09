package com.hsm.admin.repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
/** Spring Data custom repository implementation backed by JPA EntityManager (no JdbcTemplate).
 * Native SQL is limited to multi-table reports, locking and atomic business transitions.
 * When extending JpaRepository, callers also get findById/save/findAll/deleteById etc.
 */
public class AdminJpaOperationsImpl implements AdminJpaOperations {
  @PersistenceContext private EntityManager em;
  private Query prepare(String sql,Object... args){
    // Rewrite JDBC positional placeholders to JPA numeric positional placeholders.
    // Admin SQL constants contain only parameter markers outside SQL string literals.
    StringBuilder numbered=new StringBuilder();
    int n=0;
    for(int i=0;i<sql.length();i++){
      char c=sql.charAt(i);
      if(c=='?')numbered.append('?').append(++n);
      else numbered.append(c);
    }
    Query query=em.createNativeQuery(numbered.toString());
    for(int i=0;i<args.length;i++) query.setParameter(i+1,args[i]);
    return query;
  }
  @Override @SuppressWarnings({"unchecked","deprecation"})
  public List<Map<String,Object>> queryForList(String sql,Object... args){
    NativeQuery<?> query=prepare(sql,args).unwrap(NativeQuery.class);
    query.setTupleTransformer(AliasToEntityMapResultTransformer.INSTANCE);
    return (List<Map<String,Object>>) (List<?>) query.getResultList();
  }
  @Override @Transactional
  public int update(String sql,Object... args){return prepare(sql,args).executeUpdate();}
  @Override @SuppressWarnings("unchecked")
  public <T> T queryForObject(String sql,Class<T> type,Object... args){
    Object value=prepare(sql,args).getSingleResult();
    if(value instanceof Number n){
      if(type==Long.class)return (T)Long.valueOf(n.longValue());
      if(type==Integer.class)return (T)Integer.valueOf(n.intValue());
    }
    return type.cast(value);
  }
}
