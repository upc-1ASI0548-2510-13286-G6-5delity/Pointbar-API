package com.metasoft.pointbarmetasoft.securitymanagement.domain.jwt.filter;
import com.metasoft.pointbarmetasoft.securitymanagement.application.services.CustomUserDetailsServiceImpl;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.jwt.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsServiceImpl customUserDetailsService;

    public JwtAuthorizationFilter(JwtUtils jwtUtils, CustomUserDetailsServiceImpl customUserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String tokenHeader = request.getHeader("Authorization");

        log.info("JwtAuthorizationFilter - Token Header: {}", tokenHeader);
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);

            if (jwtUtils.isTokenValid(token)) {
                String email = jwtUtils.getEmailFromToken(token);
                log.info("JwtAuthorizationFilter - Token is valid. User: {}", email);

                var userDetails = customUserDetailsService.loadUserByUsername(email);
                System.out.println("userDetails: " + userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                //aqui cambie el userdetails por email
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                System.out.println("ESTAMOS EN AUTENTICATION NO SUSSSESSFULL, PRIMER ELSE");

                log.warn("JwtAuthorizationFilter - Token is invalid.");
                log.info("HttpServletResponse.SC_UNAUTHORIZED: {}", HttpServletResponse.SC_UNAUTHORIZED);
                //lanzar excepcion por token expirado
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"message\": \"Unauthorized\"}");
                return;
            }
        } else {
            System.out.println("ESTAMOS EN AUTHORIZATION , SEGUNDO ELSE");
            log.warn("JwtAuthorizationFilter - Token not provided or invalid format.");
        }
        filterChain.doFilter(request, response);
    }
}