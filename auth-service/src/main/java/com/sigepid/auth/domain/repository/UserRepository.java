package com.sigepid.auth.domain.repository;

import com.sigepid.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
//repositorio de usuario, con metodos para buscar por username y email, y para verificar si existen por username y email
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//metodo para buscar un usuario por username
    Optional<User> findByUsername(String username);
//metodo para buscar un usuario por email
    Optional<User> findByEmail(String email);
//metodo para verificar si existe un usuario por username
    Boolean existsByUsername(String username);
//metodo para verificar si existe un usuario por email
    Boolean existsByEmail(String email);
}
