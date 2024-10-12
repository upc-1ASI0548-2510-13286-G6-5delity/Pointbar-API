package com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.Role;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRolName(ERole rolName);
    boolean existsByRolName(ERole rolName);
}
