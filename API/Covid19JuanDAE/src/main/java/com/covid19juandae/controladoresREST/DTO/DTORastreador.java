/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.controladoresREST.DTO;

import com.covid19juandae.entidades.Rastreador;

/**
 *
 * @author Pc
 */
public class DTORastreador {
     
    /**DNI rastreador**/
    String dniRastreador;
    
    /**Nombre**/
    String nombre;
    
    /**Telefono**/
    String telefono;
    
    /**Clave de acceso al sistema**/
    String clave;
    
    /**Numero de usuarios reportados**/
    int numUsuariosReportados;

    /**
     * Constructor por defecto de DTORastreador
     */
    public DTORastreador() {
    }

    /**
     * @brief Constructor del Rastreador
     * @param dni
     * @param telefono
     * @param nombre
     * @param clave
     */
    public DTORastreador(String dni, String nombre, String telefono, String clave){
        this.dniRastreador=dni;
        this.nombre=nombre;
        this.telefono=telefono;
        this.clave=clave;
        this.numUsuariosReportados=0;
    }
    
     /**
     * @param rastreador
     * @brief Constructor del Rastreador
     */
    public DTORastreador(Rastreador rastreador){
        this.dniRastreador=rastreador.getDni();
        this.nombre=rastreador.getNombre();
        this.telefono=rastreador.getTelefono();
        this.clave= rastreador.getClave();
        this.numUsuariosReportados= rastreador.getNumUsuariosReportados();
    }
    
    /**
     * @brief Constructor de rastreador recibiendo solo el dni
     * @param dni 
     */
    public DTORastreador(String dni){
        this.dniRastreador=dni;
        this.nombre="";
        this.telefono="";
        this.clave="";
        this.numUsuariosReportados=0;
    }
    
    /**
     * @return the dniRastreador
     */
    public String getDni() {
        return dniRastreador;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @return the telefono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * @return the clave
     */
    public String getClave() {
        return clave;
    }

    /**
     * 
     * @return the numUsuariosReportados
     */
    public int getNumUsuariosReportados() {
        return numUsuariosReportados;
    }
    
    /**
     * Convertir DTORastreador a Rastreador
     * @return 
     */
    public Rastreador aRastreador() {
        return new Rastreador(dniRastreador, nombre, telefono, clave,numUsuariosReportados);
    }
    
    /**
     * @brief Set DNI al rastreador
     * @param dniRastreador 
     */
    public void setDniRastreador(String dniRastreador) {
        this.dniRastreador = dniRastreador;
    }

    /**
     * @brief Set Nombre al rastreador
     * @param nombre 
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @brief Set telefono al rastreador
     * @param telefono 
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * @brief Set clave al rastreador
     * @param clave 
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /**
     * @brief Set el dato de los usuarios reportados al rastreador
     * @param numUsuariosReportados 
     */
    public void setNumUsuariosReportados(int numUsuariosReportados) {
        this.numUsuariosReportados = numUsuariosReportados;
    }

    /**
     * @return DNI del rastreador
     */
    public String getDniRastreador() {
        return dniRastreador;
    }
    
}
