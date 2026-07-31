package com.vajraiot.VJ_RLY_RFMS_REST_APIs.repository;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.LoginCredentials;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.enums.LoginRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoginCredentialsRepository extends JpaRepository<LoginCredentials, Long> {

    Optional<LoginCredentials> findByUsername(String username);

    boolean existsByUsername(String username);

    void deleteByUsername(String username);

    List<LoginCredentials> findByRole(LoginRoles loginRoles);
}
