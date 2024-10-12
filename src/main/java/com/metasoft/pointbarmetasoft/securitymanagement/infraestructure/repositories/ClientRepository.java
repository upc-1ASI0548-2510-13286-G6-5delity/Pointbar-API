package com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Boolean existsByDni(String dni);
}
