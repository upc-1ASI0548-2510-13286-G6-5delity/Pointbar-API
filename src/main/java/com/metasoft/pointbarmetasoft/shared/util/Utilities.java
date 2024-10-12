package com.metasoft.pointbarmetasoft.shared.util;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.Role;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.enums.ERole;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class Utilities implements CommandLineRunner{
    private final RolRepository rolRepository;

    public Utilities(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
    }

    private void initializeRoles(){
        Set<ERole> roleNames = new HashSet<>(Arrays.asList(ERole.ADMIN,
                ERole.CLIENT, ERole.EMPLOYEE));

        for(ERole roleName : roleNames){
            if (!rolRepository.existsByRolName(roleName)) {
                var newRole = new Role();
                newRole.setRolName(roleName);
                rolRepository.save(newRole);
            }
        }
    }
}
