/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.entidades;


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
public class RastreadorTest {
    
    public RastreadorTest(){
    
    }
    
    /**
     * @brief Validar Rastreador en la BBDD
     */
    @Test
    void testValidacionRastreador(){
        
        String clave="dae2022";
        
        Rastreador rastreador=new Rastreador(
            "26516720C",
            "Manolo Manuel Manolez",
            "988674533",
            clave);
        
        Validator validator=Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Rastreador>> violations=validator.validate(rastreador);
        
        Assertions.assertThat(violations).isEmpty();
    }
    
    /**
     * @brief comprobar que la clave coincide 
     */
    @Test
    void testComprobacionClave(){
        
        String clave="dae2022";
        Rastreador rastreador=new Rastreador("26516720C","Manolo Manuel Manolez","123456789",clave);
        
        Assertions.assertThat(rastreador.claveValida(clave)).isTrue();
    }
   
}
