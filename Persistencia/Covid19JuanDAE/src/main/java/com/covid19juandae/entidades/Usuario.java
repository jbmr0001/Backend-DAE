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
import java.io.Serializable;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;


/**
 * @attr uuid, tlf, fechaalta, positivo, fechaPOsitivo, fechaCur
 * @author jmait
 */
@Entity
public class Usuario implements Serializable {
    
    /**UUID version 4**/
    @Id
    @NotNull
    private UUID uuidUsuario;
    
    /**Telefono**/
    @Size(min=9, max=9)
    @Pattern(regexp=ExprReg.TLF)
    private String telefono;
    
    /** Fecha de alta */
    //@PastOrPresent
    private LocalDateTime fAlta;
    
    /** Si es positivo */
    private boolean positivo;
    
    /** Fecha positivo */
    //@PastOrPresent
    private LocalDateTime fPositivo;
    
    /** Fecha curacion */
    @PastOrPresent
    private LocalDateTime fCuracion;
    
    /** Usuarios contactados */
    @OneToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL)//lazy
    @JoinColumn(name="usuario")
    List<Contacto> contactosCercanos;
    
    /**
     * @brief Constructor por defecto
     */
    public Usuario() {
    }
    
    /**
     * @brief Constructor del Usuario
     * @param telefono
     * @param fAlta
     * @param positivo
     * @param fPositivo
     * @param fCuracion 
     */
    public Usuario(String telefono, LocalDateTime fAlta, boolean positivo, LocalDateTime fPositivo, LocalDateTime fCuracion) {
        this.uuidUsuario = UUID.randomUUID();
        this.telefono = telefono;
        this.fAlta = fAlta;
        this.positivo = positivo;
        this.fPositivo = fPositivo;
        this.fCuracion = fCuracion;
        this.contactosCercanos=new ArrayList<>();
    }
    
    /**
     * @brief Constructor del usuario solo con teléfono y UUID(random)
     * @param telefono
     */
    public Usuario(String telefono) {
        this.uuidUsuario = UUID.randomUUID();
        this.telefono = telefono;
        this.fAlta = null;
        this.positivo = false;
        this.fPositivo = null;
        this.fCuracion = null;
        this.contactosCercanos=new ArrayList<>();
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
     * @brief Setter de fecha de Alta
     * @param fAlta
     */
    public void setfAlta(LocalDateTime fAlta) {
        this.fAlta = fAlta;
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
     * @param contactoCercano
     * @Brief Función para insertar de forma ordenada según el riesgo los contactos cercanos
     */
    public void insertarContactoOrdenado(ContactoCercano contactoCercano){
      
        int numDias=contactoCercano.getUsuarioContacto().getfPositivo().getDayOfMonth()-contactoCercano.getFecha().getDayOfMonth();
        double distancia=contactoCercano.getDistancia();
        double duracion=contactoCercano.getDuracion();
        
        double riesgo=heuristica(distancia,duracion,numDias);
                
        
        Contacto c=new Contacto(contactoCercano.getFecha(),contactoCercano.getUsuarioContacto(),riesgo);
        
        
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
