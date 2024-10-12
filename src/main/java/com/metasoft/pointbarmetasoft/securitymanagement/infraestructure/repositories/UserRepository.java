package com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.Admin;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.Role;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Boolean existsByEmail(String email);
    @Query(value = "SELECT EXISTS(SELECT 1 FROM users WHERE dni = :dni AND user_type = 'client')", nativeQuery = true)
    Boolean existsByDni(@Param("dni") String dni);

    @Query(value = "INSERT INTO user_roles (user_id, role_id) VALUES (:userId, :roleId)", nativeQuery = true)
    void addRoleToUser(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM adminuser_roles WHERE user_id = :userId AND rol_id = :roleId)", nativeQuery = true)
    Long userHasRole(@Param("userId") Long userId,@Param("roleId") Long roleId);

    @Modifying
    @Query(value = "DELETE FROM user_roles WHERE user_id = :userId", nativeQuery = true)
    void deleteUserRolesNative(@Param("userId") Long userId);

    @Query(value = "SELECT u.roles FROM UserEntity u WHERE u.id = :UserId")
    Set<Role> getRolesByUserId(@Param("UserId") Long UserId);

    @Query("SELECT u FROM UserEntity u WHERE u.business.id = :businessId")
    List<UserEntity> findByBusinessId(@Param("businessId") Long businessId); //obtener los usuarios de un negocio específico
}
