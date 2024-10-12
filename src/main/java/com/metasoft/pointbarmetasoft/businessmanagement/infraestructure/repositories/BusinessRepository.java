package com.metasoft.pointbarmetasoft.businessmanagement.infraestructure.repositories;
import com.metasoft.pointbarmetasoft.businessmanagement.domain.entities.Business;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.Admin;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {
    Optional<Business> findByUsersContaining(UserEntity admin);
}
