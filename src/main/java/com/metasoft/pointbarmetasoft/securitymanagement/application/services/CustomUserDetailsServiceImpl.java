package com.metasoft.pointbarmetasoft.securitymanagement.application.services;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.Role;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("CustomUserDetailsService - loadUserByUsername: {}", email);

        var userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        Set<Role> roles = userRepository.getRolesByUserId(userEntity.getId());
        Collection<? extends GrantedAuthority> authorities = mapRoles(roles);
        System.out.println("authorities: " + authorities);

        return new User(userEntity.getEmail(),
                userEntity.getPassword(),
                true,
                true,
                true,
                true,
                authorities);

    }

    private Collection<? extends GrantedAuthority> mapRoles(Set<Role> roles){
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_".concat(role.getRolName().name())))
                .collect(Collectors.toSet());
    }
}
