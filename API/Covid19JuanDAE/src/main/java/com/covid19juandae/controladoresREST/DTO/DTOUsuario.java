/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.controladoresREST.DTO;

import com.covid19juandae.entidades.Contacto;
import com.covid19juandae.entidades.Usuario;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
    
    
/**
 * 
 * @author jmait
 */
public class DTOUsuario {
    /**UUID version 4**/
    UUID uuidUsuario;
    
    /**Telefono**/
    String telefono;
    
    /** Fecha de alta */
    LocalDateTime fAlta;
    
    /** Si es positivo */
    boolean positivo;
    
    /** Fecha positivo */
    LocalDateTime fPositivo;
    
    /** Fecha curacion */
    LocalDateTime fCuracion;
    
    /**
     * @brief Constructor del Usuario
     * @param telefono
     * @param fAlta
     * @param positivo
     * @param fPositivo
     * @param fCuracion 
     */
    public DTOUsuario(String telefono, LocalDateTime fAlta, boolean positivo, LocalDateTime fPositivo, LocalDateTime fCuracion) {
        this.uuidUsuario = null;
        this.telefono = telefono;
        this.fAlta = fAlta;
        this.positivo = positivo;
        this.fPositivo = fPositivo;
        this.fCuracion = fCuracion;
        
    }
    
    /**
     * @param usuario
     * @brief Constructor del usuario solo con teléfono y UUID(random)
     */
    public DTOUsuario(Usuario usuario) {
        this.uuidUsuario = usuario.getUuid();
        this.telefono = usuario.getTelefono();
        this.fAlta = usuario.getfAlta();
        this.positivo = usuario.getPositivo();
        this.fPositivo = usuario.getfPositivo();
        this.fCuracion = usuario.getfCuracion();
    }
    
    /**
     * @brief Constructor del DTOUsuario solo pasandole por parámetro el telefono
     * @param telefono 
     */
    public DTOUsuario(String telefono){
        this.uuidUsuario =null;
        this.telefono = telefono;
        this.fAlta = null;
        this.positivo = false;
        this.fPositivo = null;
        this.fCuracion = null;
    }
    
    /**
     * @brief Constructor del DTOUsuario con solo el UUID como dato
     * @param uuid 
     */
    public DTOUsuario(UUID uuid){
        this.uuidUsuario =uuid;
        this.telefono = null;
        this.fAlta = null;
        this.positivo = false;
        this.fPositivo = null;
        this.fCuracion = null;
    }
   
    /**
     * @return the uuidUsuario
     */
    public UUID getUuid() {
        return uuidUsuario;
    }

    /**
     * @return the telefono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * @return the fAlta
     */
    public LocalDateTime getfAlta() {
        return fAlta;
    }

    /**
     * @return the positivo
     */
    public Boolean getPositivo() {
        return positivo;
    }

    /**
     * @return the fPositivo
     */
    public LocalDateTime getfPositivo() {
        return fPositivo;
    }

    /**
     * @return the fCuracion
     */
    public LocalDateTime getfCuracion() {
        return fCuracion;
    }

    
    /**
     * @return Usuario
     * @brief devuelve un usuario nuevo
     */
    public Usuario aUsuario() {
        return new Usuario(telefono, fAlta, positivo, fPositivo, fCuracion);
    }

    /**
     * @return UUID del usuario
     */
    public UUID getUuidUsuario() {
        return uuidUsuario;
    }

    /**
     * @return el estado del usuario
     */
    public boolean isPositivo() {
        return positivo;
    }

    public void setUuidUsuario(UUID uuidUsuario) {
        this.uuidUsuario = uuidUsuario;
    }

}