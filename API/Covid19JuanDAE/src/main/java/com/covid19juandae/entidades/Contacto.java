/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;

/**
 * Contacto del usuario
 * @author Pc
 */
@Entity
public class Contacto implements Serializable {
    
    /**ID de contacto**/
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    int idContacto;
    
    /**Fecha de contacto**/
    @PastOrPresent
    LocalDateTime fecha;
    
    /**Riesgo del contacto**/
    @Positive
    double riesgo;
    
    /**Usuario contactado**/
    @ManyToOne
    Usuario usuario;
    
    /**
     * @brief Constructor por defecto de Contacto
     */
    public Contacto() {
    }

    /**
     * @param fecha
     * @param usuario
     * @param riesgo
     * @brief Constructor de contacto
     */
    public Contacto(LocalDateTime fecha, Usuario usuario, double riesgo) {
        this.fecha = fecha;
        this.usuario = usuario;
        this.riesgo = riesgo;
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

    public int getIdContacto() {
        return idContacto;
    }
    
    

}
