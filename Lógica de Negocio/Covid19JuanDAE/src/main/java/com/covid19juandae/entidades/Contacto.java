/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.entidades;

import java.time.LocalDateTime;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;

/**
 *
 * @author Pc
 */
public class Contacto {
    
    /**Fecha de contacto**/
    @PastOrPresent
    LocalDateTime fecha;
    
    /**Riesgo del contacto**/
    @Positive
    double riesgo;
    
    /**Usuario contactado**/
    Usuario usuario;

    /**
     * @brief Constructor de contacto
     * @param LocalDateTime fecha
     * @param double riesgo
     */
    public Contacto(LocalDateTime fecha, Usuario usuario) {
        this.fecha = fecha;
        this.usuario = usuario;
        this.riesgo = 0;
    }

    /**
    * @return the fecha
    */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
    * @return the Usuario
    */
    public Usuario getUsuario() {
        return usuario;
    }
    
    /**
    * @return the riesgo
    */
    public double getRiesgo() {
        return riesgo;
    }

    /**
     * @Brief Setter de riesgo
     * @param riesgo 
     */
    public void setRiesgo(double riesgo) {
        this.riesgo = riesgo;
    }

}
