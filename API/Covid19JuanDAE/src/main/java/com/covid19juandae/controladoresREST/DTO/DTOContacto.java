/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.controladoresREST.DTO;

import com.covid19juandae.entidades.Usuario;
import com.covid19juandae.entidades.Contacto;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * @author jmait
 */
public class DTOContacto {
     
    /**ID de contacto**/
    int idContacto;
    
    /**Fecha de contacto**/
    LocalDateTime fecha;
    
    /**Riesgo del contacto**/
    double riesgo;
    
    /**Usuario contactado**/
    UUID usuario;
    
    /**
     * @brief Constructor por defecto de Contacto
     */
    public DTOContacto() {
    }

    /**
     * @param fecha
     * @param usuario
     * @param riesgo
     * @brief Constructor de contacto
     */
    public DTOContacto(LocalDateTime fecha, UUID usuario, double riesgo) {
        this.fecha = fecha;
        this.usuario = usuario;
        this.riesgo = riesgo;
    }
    
    /**
     * @brief Constructor de contacto cercano pasando un contacto previamente creado
     * @param contacto 
     */
    public DTOContacto(Contacto contacto){
        this.idContacto=contacto.getIdContacto();
        this.fecha=contacto.getFecha();
        this.usuario=contacto.getUsuario().getUuid();
        this.riesgo=contacto.getRiesgo();
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
    public UUID getUsuario() {
        return usuario;
    }
    
    /**
    * @return the riesgo
    */
    public double getRiesgo() {
        return riesgo;
    }

    /**
     * 
     * @return idContacto
     */
    public int getIdContacto() {
        return idContacto;
    }
    
}
