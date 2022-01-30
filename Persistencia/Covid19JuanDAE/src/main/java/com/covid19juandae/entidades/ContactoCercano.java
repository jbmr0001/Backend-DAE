/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.entidades;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;

/**
 *
 * @author jmait
 */
public class ContactoCercano {
    
    /**UUID version 4**/
    @NotNull
    Usuario UsuarioContactado;
    
    /** Fecha del UsuarioContactado**/
    @PastOrPresent
    LocalDateTime fecha;
    
    /** Disctancia al UsuarioContactado**/
    @Positive
    Double distancia;
    
    /**duracion del UsuarioContactado cercano**/
    @Positive
    Double duracion;
    
    /**Numero de días en contacto**/
    @Positive 
    Double numDias;

    /**
     * @brief constructor de ContactoCercano
     * @param contacto
     * @param fecha
     * @param distancia
     * @param duracion 
     */
    public ContactoCercano(Usuario contacto, LocalDateTime fecha, Double distancia, Double duracion) {
        this.UsuarioContactado = contacto;
        this.fecha = fecha;
        this.distancia = distancia;
        this.duracion = duracion;
        this.numDias=0.0;
    }

    /**
     * @return UsuarioContactado
     */
    public Usuario getUsuarioContacto() {
        return UsuarioContactado;
    }

    /**
     * @return fecha
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * @return distancia
     */
    public Double getDistancia() {
        return distancia;
    }

    /**
     * @return duracion
     */
    public Double getDuracion() {
        return duracion;
    }

    public void setUsuarioContactado(Usuario UsuarioContactado) {
        this.UsuarioContactado = UsuarioContactado;
    }
    
    
}
