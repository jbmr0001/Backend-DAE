/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.entidades;


/*import com.covid19juandae.util.CodificadorMd5;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;*/

import com.covid19juandae.util.ExprReg;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.Size;

/**
 * @attr uuid, tlf, fechaalta, positivo, fechaPOsitivo, fechaCur
 * @author jmait
 */
public class Usuario {
    
    /**UUID version 4**/
    private UUID uuid;
    
    /**Telefono**/
    @Size(min=9, max=9)
    @Pattern(regexp=ExprReg.TLF)
    private String telefono;
    
    /** Fecha de alta */
    @Past
    private LocalDateTime fAlta;
    
    /** Si es positivo */
    private boolean positivo;
    
    /** Fecha positivo */
    @Past
    private LocalDateTime fPositivo;
    
    /** Fecha curacion */
    @Past
    private LocalDateTime fCuracion;
    
    /** Usuarios contactados */
    List<Contacto> contactosCercanos;
    
    /**
     * @brief Constructor del Usuario
     * @param uuid
     * @param telefono
     * @param fAlta
     * @param positivo
     * @param fPositivo
     * @param fCuracion 
     */
    public Usuario(UUID uuid, String telefono, LocalDateTime fAlta, boolean positivo, LocalDateTime fPositivo, LocalDateTime fCuracion) {
        this.uuid = UUID.randomUUID();
        this.telefono = telefono;
        this.fAlta = fAlta;
        this.positivo = positivo;
        this.fPositivo = fPositivo;
        this.fCuracion = fCuracion;
        this.contactosCercanos=new ArrayList<>();

    }

    /**
     * @return the uuid
     */
    public UUID getUuid() {
        return uuid;
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
     * @Brief Setter de positivo
     * @param positivo 
     */
    public void setPositivo(boolean positivo) {
        this.positivo = positivo;
    }

    /**
     * @Brief Setter de fecha de positivo
     * @param fPositivo 
     */
    public void setfPositivo(LocalDateTime fPositivo) {
        this.fPositivo = fPositivo;
    }

    /**
     * @Brief Setter de fecha de curación
     * @param fCuracion 
     */
    public void setfCuracion(LocalDateTime fCuracion) {
        this.fCuracion = fCuracion;
    }

    /**
     * @Brief Getter de la Lista de Contactos cercanos
     * @return 
     */
    public List<Contacto> getContactosCercanos() {
        return contactosCercanos;
    }
    
    /**
     * @Brief Función para insertar de forma ordenada según el riesgo los contactos cercanos
     * @param c Contacto
     * @param distancia Double con la distancia en metros del contacto
     * @param duracion Double con la duracion en minutos del contacto
     * @param numDias Entero con el numero de días (fecha positivo-fecha contacto)
     */
    
    public void insertarContactoOrdenado(Contacto c,double distancia,double duracion,int numDias){
      
        c.setRiesgo(heuristica(distancia,duracion,numDias));
        
        contactosCercanos.add(c);
        //Ordenamos la lista según el riesgo
        Collections.sort(contactosCercanos, new Comparator<Contacto>() {
	@Override
	public int compare(Contacto c1, Contacto c2) {
		return new Double(c1.getRiesgo()).compareTo(new Double(c2.getRiesgo()));
	}
        });
    }
    
    /**
     * @Brief Heurística para calcular el riesgo de un contacto. 
     * @param distancia Float con la distancia en metros
     * @param duracion Float con la duración en minutos
     * @param numDias Float con el número de días
     * @return Double entre 0 y 99 con el porcentaje de riesgo
     */
    double heuristica(double distancia,double duracion,double numDias){
        double maxDistancia=2;//distancia máxima para contagio
        double incubacion=14;//periodo de incubacion maximo 
        double minTiempo=15;//tiempo mínimo para contagio
        double maxTiempo=120;//dos horas
        double riesgoDias=0;
        
        //cada factor de riesgo tiene asignado un 33%
        //se calculan los riesgo si superan el valor mínimo
        double riesgoDistancia=0;
        if(distancia<maxDistancia){ 
           riesgoDistancia=((1-(distancia/maxDistancia))*100)*0.33;
        }
        double riesgoDuracion=0;
        if(duracion>minTiempo){
            riesgoDuracion=((duracion/maxTiempo)*100)*0.33;
        }
        
        if(numDias<incubacion){
            riesgoDias=((1-(numDias/minTiempo))*100)*0.33;
        }
        
        return riesgoDias+riesgoDistancia+riesgoDuracion;
    }
    
    /**
     * @Brief Función para borrar los contactos con 31 dias de antiguedad
     * @param fechaLimite Fecha actual
     */
    public void borrarContactosAnterioresFecha(LocalDateTime fechaLimite){
        for(int i=0;i<contactosCercanos.size();i++){
            System.out.println(contactosCercanos.get(i).fecha.toString());
            if(contactosCercanos.get(i).getFecha().isBefore(fechaLimite)){
                contactosCercanos.remove(i);
                //Volvemos atrás ya que hemos decrementado una posición
                i--;
            }
        }
    }
    
}
