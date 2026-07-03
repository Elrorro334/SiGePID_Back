package com.sigepid.auth.infrastructure.security;

import com.sigepid.auth.domain.entity.User;
import com.sigepid.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
//servicio para cargar los detalles del usuario a partir del username, implementa la interfaz UserDetailsService de Spring Security
public class CustomUserDetailsService implements UserDetailsService {
//repositorio de usuario para acceder a la base de datos
    private final UserRepository userRepository;

    @Override
    //metodo para cargar los detalles del usuario a partir del username, se busca el usuario en la base de datos y se devuelve un objeto UserDetails con el username, password y rol
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username
                ));
//se devuelve un objeto UserDetails con el username, password y rol del usuario
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                )
        );
    }
}
