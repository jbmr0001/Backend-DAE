/*

 */
package com.covid19juandae.controladoresREST.DTO;

import com.covid19juandae.entidades.ContactoCercano;
import com.covid19juandae.entidades.Usuario;
import java.time.LocalDateTime;
import java.util.UUID;

/*
 *
 * @author jmait
 */
public class DTOContactoCercano {

    /*UUID version 4*/
    UUID usuarioContactado;

    /* Fecha del usuarioContactado*/
    LocalDateTime fecha;

    /** Disctancia al usuarioContactado*/
    Double distancia;

    /*duracion del usuarioContactado cercano*/
    Double duracion;

    /*Numero de días en contacto*/
    Double numDias;

    /*
     * @brief Constructor por defecto de Contacto
     */
    public DTOContactoCercano() {
    }

    /*
     * @param contacto
     * @param fecha
     * @param distancia
     * @param duracion
     * @brief Constructor de contacto
     */
    public DTOContactoCercano(UUID contacto, LocalDateTime fecha, Double distancia, Double duracion) {
        this.usuarioContactado = contacto;
        this.fecha = fecha;
        this.distancia = distancia;
        this.duracion = duracion;
        this.numDias=0.0;
    }

    /*
     * @brief Constructor de contacto cercano pasando un contacto previamente creado
     * @param contacto 
     */
    public DTOContactoCercano(ContactoCercano contacto){
        //this.idContacto=contacto.g
        this.usuarioContactado=contacto.getUsuarioContacto().getUuid();
        this.distancia=contacto.getDistancia();
        this.duracion=contacto.getDistancia();
        this.fecha=contacto.getFecha();
        this.numDias=contacto.getNumDias();
    }

    public UUID getUsuarioContactado() {
        return usuarioContactado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Double getDistancia() {
        return distancia;
    }

    public Double getDuracion() {
        return duracion;
    }

    public Double getNumDias() {
        return numDias;
    }

    public ContactoCercano aContactoCercano() {
        Usuario u=new Usuario();
        u.setUuidUsuario(usuarioContactado);
        return new ContactoCercano(u, fecha, distancia, duracion);
    }


}
