/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.seguridad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Proveedor de datos de seguridad de CovidApp
 *
 * @author admin
 */
@Configuration
public class ServicioSeguridadCovid19App extends WebSecurityConfigurerAdapter {
    @Autowired
    ServicioDatosRastreador servicioDatosRastreador;
    
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(servicioDatosRastreador)
            .passwordEncoder(new BCryptPasswordEncoder());
        
        //auth.inMemoryAuthentication()
               //.withUser("covid19juandae").roles("RASTREADOR").password("{noop}secret");
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable();
        
        httpSecurity.httpBasic();
        
        httpSecurity.authorizeRequests().antMatchers(HttpMethod.POST, "/covid19juandae/rastreadores").anonymous();
        httpSecurity.authorizeRequests().antMatchers(HttpMethod.POST, "/covid19juandae/usuarios").anonymous();
        httpSecurity.authorizeRequests().antMatchers(HttpMethod.POST, "/covid19juandae/usuarios/{uuid}/contactos").anonymous();
        
        httpSecurity.authorizeRequests().antMatchers("/covid19juandae/rastreadores/{dni}/**")
               .access("hasRole('RASTREADOR') and #dni == principal.username");
        
    }
}
