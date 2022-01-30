/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.entidades;

import java.time.LocalDateTime;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author jmait
 */
public class UsuarioTest {
    public UsuarioTest(){
    
    }
    /**
     * Brief Test de validación de usuario
     */
    @Test
    void testValidacionUsuario(){
               
        
        LocalDateTime fAlta = LocalDateTime.of(2021, 9, 19,0,0);
        LocalDateTime fPos = LocalDateTime.of(2021, 9, 24,0,0);
        LocalDateTime fCur = LocalDateTime.of(2021, 10, 3,0,0);
                
        Usuario user = new Usuario(
            "685938563",
            fAlta,
             true,
            fPos,
            fCur);
        
        Validator validator=Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Usuario>> violations=validator.validate(user);
        
        Assertions.assertThat(violations).isEmpty();
    }
    
    /**
     * Brief Test de la heurística del riesgo
     */
    @Test
    void testHeurística(){
         Usuario usuario1 = new Usuario(
            "685F938563",
            LocalDateTime.of(2021, 9, 19,0,0),
             true,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario2 = new Usuario(
            "697667137",
            LocalDateTime.of(2021, 9, 19,0,0),
             true,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        LocalDateTime fechaContacto=LocalDateTime.of(2021, 10, 19,11,22);
        
        Validator validator=Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Usuario>> violations=validator.validate(usuario1);
        
        Validator validator2=Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Usuario>> violations2=validator2.validate(usuario2);
        
        ContactoCercano contactoCercano=new ContactoCercano(usuario2,fechaContacto, 1.4, 20.0);
        
        usuario1.insertarContactoOrdenado(contactoCercano);
        Assertions.assertThat(usuario1.heuristica(1.4, 20, 5)).isPositive();
        Assertions.assertThat(usuario1.heuristica(1.4, 20, 5)).isEqualTo(37.400000000000006);
        Assertions.assertThat(usuario1.heuristica(1.4, 20, 5)).isLessThan(100);

    }
    
    /**
     * Brief Test de la inserción ordenada
     */
    @Test
    void testInsertaOrdenado(){
        Usuario usuario1 = new Usuario(
            "685F938563",
            LocalDateTime.of(2021, 9, 19,0,0),
             true,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario2 = new Usuario(
            "697667137",
            LocalDateTime.of(2021, 9, 19,0,0),
             true,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario3 = new Usuario(
            "697333137",
            LocalDateTime.of(2021, 9, 19,0,0),
             true,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Validator validator=Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Usuario>> violations=validator.validate(usuario1);
        
        Validator validator2=Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Usuario>> violations2=validator2.validate(usuario2);
        
        Validator validator3=Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Usuario>> violations3=validator3.validate(usuario3);
        
        LocalDateTime fechaContacto=LocalDateTime.of(2021, 10, 19,11,22);
        
        ContactoCercano contactoCercano1=new ContactoCercano(usuario2,fechaContacto, 1.4, 20.0);
        ContactoCercano contactoCercano2=new ContactoCercano(usuario3,fechaContacto, 0.2, 20.0);
        
        usuario1.insertarContactoOrdenado(contactoCercano1);
        usuario1.insertarContactoOrdenado(contactoCercano2);
       
   
        Assertions.assertThat(usuario1.getContactosCercanos().get(0).getRiesgo()).isLessThan(usuario1.getContactosCercanos().get(1).getRiesgo());
        
    }

    /**
     * @brief Test de insercion de contactos
     */
    @Test
    void testInsertaContactos(){
        Usuario usuario1 = new Usuario(
            "685F938563",
            LocalDateTime.of(2021, 9, 19,0,0),
             true,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario2 = new Usuario(
            "697667137",
            LocalDateTime.of(2021, 9, 19,0,0),
             true,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        ContactoCercano c=new ContactoCercano(usuario1,LocalDateTime.now(),23.0,10.1);
        ContactoCercano c1=new ContactoCercano(usuario2,LocalDateTime.now(),23.0,10.1);
        
        usuario1.insertarContactoOrdenado(c);
        usuario1.insertarContactoOrdenado(c1);
        
        Assertions.assertThat(usuario1.getContactosCercanos().size()).isEqualTo(2);
    }
}
