package springData.DAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import springData.domain.UserRole;

@Repository
@Transactional
public class RoleDAO {

   @Autowired
   private EntityManager entityManager;

   @SuppressWarnings("unchecked")
   public List<String> getRoleNames(int userId) {
      String sql = "Select ur.appRole.role from " + UserRole.class.getName() + " ur " //
              + " where ur.appUser.userId = :userId ";

      Query query = this.entityManager.createQuery(sql, String.class);
      query.setParameter("userId", userId);
      return query.getResultList();
   }

}
