/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.entidades;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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
               
        UUID uuid = UUID.randomUUID();
        LocalDateTime fAlta = LocalDateTime.of(2021, 9, 19,0,0);
        LocalDateTime fPos = LocalDateTime.of(2021, 9, 24,0,0);
        LocalDateTime fCur = LocalDateTime.of(2021, 10, 3,0,0);
                
        Usuario user = new Usuario(
            uuid,
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
            UUID.randomUUID(),
            "685F938563",
            LocalDateTime.of(2021, 9, 19,0,0),
             true,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario2 = new Usuario(
            UUID.randomUUID(),
            "697667137",
            LocalDateTime.of(2021, 9, 19,0,0),
             true,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        LocalDateTime fechaContacto=LocalDateTime.of(2021, 10, 19,11,22);
       
        usuario1.insertarContactoOrdenado(new Contacto(fechaContacto,usuario2), 1.4, 20, usuario1.getfPositivo().getDayOfMonth()-fechaContacto.getDayOfMonth());
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
            UUID.randomUUID(),
            "685F938563",
            LocalDateTime.of(2021, 9, 19,0,0),
             true,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario2 = new Usuario(
            UUID.randomUUID(),
            "697667137",
            LocalDateTime.of(2021, 9, 19,0,0),
             true,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario3 = new Usuario(
            UUID.randomUUID(),
            "697333137",
            LocalDateTime.of(2021, 9, 19,0,0),
             true,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        LocalDateTime fechaContacto=LocalDateTime.of(2021, 10, 19,11,22);
        usuario1.insertarContactoOrdenado(new Contacto(fechaContacto,usuario2), 1.4, 20,usuario1.getfPositivo().getDayOfMonth()-fechaContacto.getDayOfMonth());
        usuario1.insertarContactoOrdenado(new Contacto(fechaContacto,usuario3), 0.2, 20,usuario1.getfPositivo().getDayOfMonth()-fechaContacto.getDayOfMonth());
   
        Assertions.assertThat(usuario1.getContactosCercanos().get(0).getRiesgo()).isLessThan(usuario1.getContactosCercanos().get(1).getRiesgo());
        
    }
    
    /**
     * Brief Test del borrado de contactos anteriores a una fecha
     */
    @Test
    void testBorradoAnteriorFecha(){
        Usuario usuario1 = new Usuario(
            UUID.randomUUID(),
            "685F938563",
            LocalDateTime.of(2021, 9, 19,0,0),
             true,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario2 = new Usuario(
            UUID.randomUUID(),
            "697667137",
            LocalDateTime.of(2021, 9, 19,0,0),
             true,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario3 = new Usuario(
            UUID.randomUUID(),
            "697333137",
            LocalDateTime.of(2021, 9, 19,0,0),
             true,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        LocalDateTime fechaContacto=LocalDateTime.of(2021, 10, 19,11,22);
        LocalDateTime fechaBorrado=LocalDateTime.of(2021, 10, 22,11,22);
        
        usuario1.insertarContactoOrdenado(new Contacto(fechaContacto,usuario2), 1.4, 20,usuario1.getfPositivo().getDayOfMonth()-fechaContacto.getDayOfMonth());
        usuario1.insertarContactoOrdenado(new Contacto(fechaContacto,usuario3), 0.2, 20,usuario1.getfPositivo().getDayOfMonth()-fechaContacto.getDayOfMonth());
        usuario1.borrarContactosAnterioresFecha(fechaBorrado);
        
        Assertions.assertThat(usuario1.getContactosCercanos().size()).isEqualTo(0);
    }
}
