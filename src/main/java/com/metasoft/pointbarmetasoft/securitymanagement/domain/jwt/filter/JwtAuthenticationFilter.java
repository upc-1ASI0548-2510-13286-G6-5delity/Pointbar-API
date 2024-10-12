package com.metasoft.pointbarmetasoft.securitymanagement.domain.jwt.filter;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.responseDto.AdminSignInResponseDto;
import com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.responseDto.ClientSignInResponseDto;
import com.metasoft.pointbarmetasoft.securitymanagement.application.dtos.responseDto.EmployeeSignInResponseDto;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.entities.UserEntity;
import com.metasoft.pointbarmetasoft.securitymanagement.domain.jwt.utils.JwtUtils;
import com.metasoft.pointbarmetasoft.securitymanagement.infraestructure.repositories.UserRepository;
import com.metasoft.pointbarmetasoft.shared.config.ModelMapperConfig;
import com.metasoft.pointbarmetasoft.shared.exception.ResourceNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final ModelMapperConfig modelMapperConfig;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserRepository userRepository, ModelMapperConfig modelMapperConfig) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.modelMapperConfig = modelMapperConfig;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        UserEntity userEntity = null;
        String email = "";
        String password = "";

        try {
            userEntity = new ObjectMapper().readValue(request.getInputStream(), UserEntity.class);
            email = userEntity.getEmail();
            System.out.println("email: " + email);
            password = userEntity.getPassword();
        } catch (StreamReadException e) {
            log.error("Error during authentication attempt: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            log.error("Error during authentication attempt: {}", e.getMessage());
            throw new AuthenticationServiceException("Error during authentication attempt", e);
        } catch (IOException e) {
            log.error("Error during authentication attempt: {}", e.getMessage());
            throw new AuthenticationServiceException("Error during authentication attempt", e);
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        log.info("Attempting authentication...");
        return getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException, java.io.IOException {

        System.out.println("ESTAMOS EN AUTENTICATION SUSSESSFULL");

        UserDetails userDetails = (UserDetails) authResult.getPrincipal();
        var user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var dtype = user.getClass().getSimpleName();
        log.info("User: {}", user.getRoles());

        String jwtToken="";
        if (dtype.equals("Admin")) {
            var adminUserResponseDto = modelMapperConfig.modelMapper().map(user, AdminSignInResponseDto.class);
            adminUserResponseDto.setDtype(dtype);
            jwtToken = jwtUtils.generateAccessToken(authResult,adminUserResponseDto);
        }

        if (dtype.equals("Employee")) {
            var employeeSignInResponseDto = modelMapperConfig.modelMapper().map(user, EmployeeSignInResponseDto.class);
            employeeSignInResponseDto.setDtype(dtype);
            jwtToken = jwtUtils.generateAccessToken(authResult,employeeSignInResponseDto);
        }

        else if (dtype.equals("Client")) {
            var normalUserResponseDto = modelMapperConfig.modelMapper().map(user, ClientSignInResponseDto.class);
            normalUserResponseDto.setDtype(dtype);

            jwtToken = jwtUtils.generateAccessToken(authResult, normalUserResponseDto);//,adminUserResponseDto);
        }

        Map<String, Object> httpResponse = new HashMap<>();
        httpResponse.put("token",  jwtToken);
        httpResponse.put("Message", "Login successful");

        response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.getWriter().flush();
        super.successfulAuthentication(request, response, chain, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException, ServletException {
        System.out.println("ESTAMOS EN AUTENTICATION NO SUSSSESSFULL");

        // Aquí puedes manejar la autenticación fallida
        log.error("Authentication failed: {}", failed.getMessage());

        // Por ejemplo, puedes enviar una respuesta JSON indicando que la autenticación falló
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Authentication failed");
        errorResponse.put("message", failed.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
        response.getWriter().flush();

    }
}
