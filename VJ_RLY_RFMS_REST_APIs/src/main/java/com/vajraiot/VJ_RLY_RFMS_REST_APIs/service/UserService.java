package com.vajraiot.VJ_RLY_RFMS_REST_APIs.service;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.LoginCredentialsDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.LoginRequestDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.LoginResponseDTO;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.LoginCredentials;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.enums.LoginRoles;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository.LoginCredentialsRepository;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final LoginCredentialsRepository loginCredentialsRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public LoginResponseDTO login(LoginRequestDTO request) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        LoginCredentials user = loginCredentialsRepository.findByUsername(request.getUsername())
                        .orElseThrow();

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return LoginResponseDTO.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    public LoginCredentials signUp(LoginCredentialsDTO loginCredentialsDto) {
        Optional<LoginCredentials> loginCredentialsList = loginCredentialsRepository.findByUsername(loginCredentialsDto.getUsername());
        if(!loginCredentialsList.isEmpty()){
            throw new RuntimeException("User already exists");
        }

        if(loginCredentialsDto.getRole().equals(LoginRoles.ADMIN)){
            List<LoginCredentials> admins = loginCredentialsRepository.findByRole(LoginRoles.ADMIN);
            if(!admins.isEmpty()){
                throw new RuntimeException("Admin already exists");
            }
        }

        LoginCredentials loginCredentials = LoginCredentials.builder()
                .username(loginCredentialsDto.getUsername())
                .password(passwordEncoder.encode(loginCredentialsDto.getPassword()))
                .role(loginCredentialsDto.getRole())
                .email(loginCredentialsDto.getEmail())
                .phoneNumber(loginCredentialsDto.getPhoneNumber())
                .createdAt(LocalDateTime.now())
                .build();
        return loginCredentialsRepository.save(loginCredentials);
    }

    public LoginCredentials createUser(LoginCredentialsDTO loginCredentialsDto) {
        Optional<LoginCredentials> existing = loginCredentialsRepository.findByUsername(loginCredentialsDto.getUsername());
        if(!existing.isEmpty()){
            throw new RuntimeException("User already exists");
        }

        LoginCredentials users = LoginCredentials.builder()
                .username(loginCredentialsDto.getUsername())
                .password(passwordEncoder.encode(loginCredentialsDto.getPassword()))
                .role(loginCredentialsDto.getRole())
                .email(loginCredentialsDto.getEmail())
                .phoneNumber(loginCredentialsDto.getPhoneNumber())
                .createdAt(LocalDateTime.now())
                .build();
        return loginCredentialsRepository.save(users);
    }

    public List<LoginCredentialsDTO> getUsers() {
        List<LoginCredentials>  loginCredentials = loginCredentialsRepository.findAll();

        return loginCredentials.stream()
                .map(this::mapToDto)
                .toList();

    }
    private LoginCredentialsDTO mapToDto(LoginCredentials loginCredentials){
        return LoginCredentialsDTO.builder()
                .username(loginCredentials.getUsername())
                .role(loginCredentials.getRole())
                .email(loginCredentials.getEmail())
                .phoneNumber(loginCredentials.getPhoneNumber())
                .build();
    }

    @Transactional
    public LoginCredentialsDTO editUser(String username, LoginCredentialsDTO loginCredentialsDto) {
        LoginCredentials loginCredentials = loginCredentialsRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));

        if(loginCredentialsDto.getRole().equals(LoginRoles.ADMIN)){
            if(loginCredentialsDto.getEmail() != null){
                loginCredentials.setEmail(loginCredentialsDto.getEmail());
            }
            if(loginCredentialsDto.getPhoneNumber() != null){
                loginCredentials.setPhoneNumber(loginCredentialsDto.getPhoneNumber());
            }
            if(loginCredentialsDto.getPassword() != null){
                loginCredentials.setPassword(passwordEncoder.encode(loginCredentialsDto.getPassword()));
            }
            if(loginCredentialsDto.getRole() != null){
                loginCredentials.setRole(loginCredentialsDto.getRole());
            }
        }

       LoginCredentials updated =  loginCredentialsRepository.save(loginCredentials);

        return LoginCredentialsDTO.builder()
                .username(updated.getUsername())
                .role(updated.getRole())
                .email(updated.getEmail())
                .phoneNumber(updated.getPhoneNumber())
//                .password(passwordEncoder.encode(updated.getPassword()))
                .password(updated.getPassword())
                .build();
    }

    @Transactional
    public String deleteUser(String username) {
        try {
            if(!loginCredentialsRepository.existsByUsername(username)){
                return "User does not exist";
            }
            loginCredentialsRepository.deleteByUsername(username);
            return "User has been deleted";
        } catch (Exception e) {
            log.error("Error deleting User");
            return e.getMessage();
        }
    }
}
