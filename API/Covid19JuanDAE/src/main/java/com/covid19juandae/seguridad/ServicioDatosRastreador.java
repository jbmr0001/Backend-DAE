/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.seguridad;

import com.covid19juandae.entidades.Rastreador;
import com.covid19juandae.servicios.ServicioCovidApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio que proporciona los datos del usuario
 * @author ajrueda
 */
@Service
public class ServicioDatosRastreador implements UserDetailsService {

    @Autowired
    ServicioCovidApp servicioCovidApp;
    
    PasswordEncoder encoder;
    
    public ServicioDatosRastreador() {
        encoder = new BCryptPasswordEncoder();
    }
    
    PasswordEncoder getEncoder() {
        return encoder;
    }
    
    @Override
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {
        Rastreador rastreador = servicioCovidApp.verRastreador(dni)
                .orElseThrow(() -> new UsernameNotFoundException(""));
        
        return User.withUsername(rastreador.getDni())
                .roles("RASTREADOR").password(rastreador.getClave())
                .build();
    }
}