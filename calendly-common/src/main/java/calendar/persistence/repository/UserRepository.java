package calendar.persistence.repository;

import calendar.persistence.entities.v1.UserEntity;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@EnableAutoConfiguration
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  @Query("SELECT u.email FROM UserEntity u WHERE u.id IN :ids")
  List<String> findEmailsByIds(@Param("ids") List<Long> ids);

  Optional<UserEntity> findByEmailAndIsDeletedFalse(String email);

  @Modifying
  @Transactional
  @Query("UPDATE UserEntity u SET u.isDeleted = true WHERE u.email = :email")
  void softDeleteByEmail(@Param("email") String email);

}
