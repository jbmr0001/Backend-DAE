/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.entidades;

import com.covid19juandae.util.CodificadorMd5;
import com.covid19juandae.util.ExprReg;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author jmait
 */
public class Rastreador {
    
    /**DNI rastreador**/
    @Size(min=9, max=9)
    @Pattern(regexp=ExprReg.DNI)
    private String dni;
    
    /**Nombre**/
    @NotBlank
    private String nombre;
    
    /**Telefono**/
    @Pattern(regexp=ExprReg.TLF)
    private String telefono;
    
    /**Clave de acceso al sistema**/
    @NotNull
    private String clave;
    
    /**Positivos Reportados**/
    List<Usuario> usuariosreport;
    
    /**
     * @brief Constructor del Rastreador
     * @param dni
     * @param telefono
     * @param nombre
     * @param clave
     */
    public Rastreador(String dni, String nombre, String telefono, String clave){
        this.dni=dni;
        this.nombre=nombre;
        this.telefono=telefono;
        this.clave= CodificadorMd5.codificar(clave);
        this.usuariosreport= new ArrayList<>();
        
    }
    
    /**
     * Compara la clave con la del cliente, codificándola en Md5
     * @param clave
     * @return 
     */
    public boolean claveValida(String clave) {
        return this.clave.equals(CodificadorMd5.codificar(clave));        
    }

    /**
     * @return the dni
     */
    public String getDni() {
        return dni;
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
     * @brief incrementa y añade un usuario positivo
     * @param usuario 
     */
    public void incrementaReportados(Usuario usuario){
        usuariosreport.add(usuario);
    }
    
    /**
     * @brief elimina un usuario positivo de la lista
     * @param usuario 
     */
    public void eliminaReportado(Usuario usuario){
        usuariosreport.remove(usuario);
    }
    
    /**
     * @return lista de usuarios reportados
     */
    public List getReportados() {
        return usuariosreport;
    }
    
}