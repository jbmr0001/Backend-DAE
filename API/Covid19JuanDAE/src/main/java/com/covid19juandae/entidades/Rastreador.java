/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.entidades;

import com.covid19juandae.util.CodificadorMd5;
import com.covid19juandae.util.CodificadorPassword;
import com.covid19juandae.util.ExprReg;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author jmait
 */
@Entity
public class Rastreador implements Serializable {
    
    /**DNI rastreador**/
    @Id
    @NotNull
    @Size(min=9, max=9)
    //@Pattern(regexp=ExprReg.DNI)
    private String dniRastreador;
    
    /**Nombre**/
    @NotBlank
    private String nombre;
    
    /**Telefono**/
    @Pattern(regexp=ExprReg.TLF)
    private String telefono;
    
    /**Clave de acceso al sistema**/
    @NotNull
    private String clave;
    
    /**Numero de usuarios reportados**/
    private int numUsuariosReportados;
    
    /**
     * @brief Constructor por defecto
     */
    public Rastreador() {
    }
    
    /**
     * @brief Constructor del Rastreador
     * @param dni
     * @param telefono
     * @param nombre
     * @param clave
     */
    public Rastreador(String dni, String nombre, String telefono, String clave){
        this.dniRastreador=dni;
        this.nombre=nombre;
        this.telefono=telefono;
        //this.clave=clave;
        //this.clave= CodificadorMd5.codificar(clave);
        this.clave = (clave != null ? CodificadorPassword.codificar(clave) : null);
        this.numUsuariosReportados=0;
        
    }
    
    /**
     * @param numUsuariosReportados
     * @brief Constructor del Rastreador pasando numUsuariosReportados
     * @param dni
     * @param telefono
     * @param nombre
     * @param clave
     */
    public Rastreador(String dni, String nombre, String telefono, String clave, int numUsuariosReportados){
        this.dniRastreador=dni;
        this.nombre=nombre;
        this.telefono=telefono;
        //this.clave=clave;
        this.clave = (clave != null ? CodificadorPassword.codificar(clave) : null);
        //this.clave= CodificadorMd5.codificar(clave);
        this.numUsuariosReportados=numUsuariosReportados;
        
    }
    
    
    /**
     * Compara la clave con la del rastreador, codificándola en Md5
     * @param clave
     * @return 
     */
    public boolean claveValida(String clave) {
        return CodificadorPassword.igual(clave, this.clave);
        //return this.clave.equals(CodificadorMd5.codificar(clave));        
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
     * @brief aumenta el numero de reportados (int)
     */
    public void incrementaReportados(){
        numUsuariosReportados=numUsuariosReportados+1;
    }
    
    /**
     * @brief disminuye en 1 el numero de reportados (int)
     */
    public void decrementaReportados(){
        numUsuariosReportados=numUsuariosReportados-1;
    }

    /**
     * @return el número de usuarios reportados
     */
    public int getNumUsuariosReportados() {
        return numUsuariosReportados;
    }
    
}