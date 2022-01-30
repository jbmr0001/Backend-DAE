/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.servicios;

import com.covid19juandae.entidades.Contacto;
import com.covid19juandae.entidades.ContactoCercano;
import com.covid19juandae.excepciones.*;
import com.covid19juandae.entidades.Rastreador;
import com.covid19juandae.entidades.Usuario;
import com.covid19juandae.repositorios.RepositorioRastreadores;
import com.covid19juandae.repositorios.RepositorioUsuarios;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 *
 * @author jmait
 */
@SpringBootApplication(scanBasePackages={"com.covid19juandae.repositorios"})
@Service
@Validated
public class ServicioCovidApp {
    
    @Autowired
    RepositorioUsuarios repositorioUsuarios;
    
    @Autowired
    RepositorioRastreadores repositorioRastreadores;
    
    
    static final LocalDateTime PASADO_DISTANTE = LocalDateTime.of(1970, 1, 1, 0, 0);
    static final LocalDateTime FUTURO_DISTANTE = LocalDateTime.of(2100, 1, 1, 0, 0);
    
    int numPositivos;
    
    public enum Enum{ MAYOR, MENOR}
    
    public ServicioCovidApp() {
      
    }
    
    /**
     * Dar de alta al usuario y guardarlo en la lista de usuarios
     * @param usuario el usuario a dar de alta
     */
    public void altaUsuario(@NotNull @Valid Usuario usuario) {
        if (repositorioUsuarios.buscar(usuario.getUuid()).isPresent()) {
            throw new UsuarioYaRegistrado();
        }
        
        // Registrar usuario
        usuario.setfAlta(LocalDateTime.now());
        repositorioUsuarios.guardar(usuario);
        
    }
    
     /**
     * Dar de alta al rastreador y guardarlo en la lista de rastreadores
     * @param rastreador el rastreador a dar de alta
     */
    public void altaRastreador(@NotNull @Valid Rastreador rastreador) {
        if (repositorioRastreadores.buscar(rastreador.getDni()).isPresent()){
            throw new RastreadorYaRegistrado();
        }
        
        // Registrar rastreador
        repositorioRastreadores.guardar(rastreador);
    }
    
    /**
     * Realiza un login de un rastreador
     * @param dniRastreador el DNI del rastreador
     * @param clave la clave de acceso
     * @return el objeto de la clase Rastreador asociado
     */
    public Optional<Rastreador> loginRastreador(@NotBlank String dniRastreador, @NotBlank String clave) {
        Optional<Rastreador> loginRastreador = repositorioRastreadores.buscar(dniRastreador)
                .filter((cliente)->cliente.claveValida(clave));
        return loginRastreador;
    }
   
    /**
     * @param dniRastreador
     * @return numero de positivos actuales
     */
    public int numPositivosActual(String dniRastreador){
        if (!repositorioRastreadores.buscar(dniRastreador).isPresent()) {
            throw new RastreadorNoRegistrado();
        }
        
        return repositorioUsuarios.verNumPositivos();
    }
    /**
     * @param dniRastreador
     * @param uuidUsuario
     * @brief el rastreador notifica un positivo
     */
    public void notificarPositivo(String dniRastreador, UUID uuidUsuario){
       
        Usuario usuario = repositorioUsuarios.buscar(uuidUsuario).orElseThrow(UsuarioNoRegistrado::new);
        Rastreador rastreador=repositorioRastreadores.buscar(dniRastreador).orElseThrow(RastreadorNoRegistrado::new);
        //actualizamos usuario
        usuario.setPositivo(true);
        usuario.setfPositivo(LocalDateTime.now());
        repositorioUsuarios.actualizar(usuario);
        //actualizamos rastreador
        rastreador.incrementaReportados();
        repositorioRastreadores.actualizar(rastreador);
        
    }
    
    /**
     * @param dniRastreador
     * @param uuidUsuario
     * @brief el rastreador notifica un usuario curado
     */
    public void notificarCuracion(String dniRastreador, UUID uuidUsuario){
        Usuario usuario = repositorioUsuarios.buscar(uuidUsuario).orElseThrow(UsuarioNoRegistrado::new);
        Rastreador rastreador=repositorioRastreadores.buscar(dniRastreador).orElseThrow(RastreadorNoRegistrado::new);
        //actualizamos usuario    
        usuario.setPositivo(false);
        usuario.setfCuracion(LocalDateTime.now());
        repositorioUsuarios.actualizar(usuario);
        //actualizamos rastreador
        rastreador.decrementaReportados();
        repositorioRastreadores.actualizar(rastreador);
    }
    
     /**
     * @param dniRastreador
     * @brief Estadistica de la lista de usuarios positivos en una quincena
     * @return Entero de nº de usuarios positivos
     */
    public int numPositivosQuincena(String dniRastreador){
        if (!repositorioRastreadores.buscar(dniRastreador).isPresent()) {
            throw new RastreadorNoRegistrado();
        }
        
        return repositorioUsuarios.verNumPositivosQuincena();
    }
    
    /**
     * @param dniRastreador
     * @brief Estadistica de la lista de infectados en todo el tiempo
     * @return List de usuarios infectados
     */
    public int numInfectadosTotales(String dniRastreador) {
        if (!repositorioRastreadores.buscar(dniRastreador).isPresent()) {
            throw new RastreadorNoRegistrado();
        }
        repositorioRastreadores.buscar(dniRastreador).orElseThrow(RastreadorNoRegistrado::new);
        return repositorioUsuarios.verNumInfectadosTotales();
    }
    
    
    /**
     * @param dniRastreador
     * @brief Estadisticas de usuarios reportados por un rastreador segun su DNI
     * @return List de positivos reportados por un rastreador
     */
    public int numPositivosReportados(String dniRastreador){
        if (!repositorioRastreadores.buscar(dniRastreador).isPresent()) {
            throw new RastreadorNoRegistrado();
        }
        
        return repositorioRastreadores.verPositivosReportados(dniRastreador);
    }
    
     /**
     * @param dniRastreador
     * @brief Borra los contactos una vez hayan pasado 31 dias desde su deteccion.
     *        Se ejecuta todos los viernes de cada mes a las 10
     */
    @Scheduled(cron="0 0 10 ? * FRI")
    public void borrarContactos31Dias(String dniRastreador){
        if (!repositorioRastreadores.buscar(dniRastreador).isPresent()) {
            throw new RastreadorNoRegistrado();
        }
        repositorioUsuarios.borrarContactos31Dias();
    }
    
     /**
     * @param dniRastreador
     * @brief numero total de contactos / numero total de usuarios
     * @return media
     */
    public int mediaContactosPositivo(String dniRastreador){
         if (!repositorioRastreadores.buscar(dniRastreador).isPresent()) {
            throw new RastreadorNoRegistrado();
         }
         return repositorioUsuarios.mediaContactosPositivos();
    }
    
    /**
     * @param uuidUsuario
     * @param contactos
     * @param dniRastreador
     * @brief notifica que un usuario ha estado en contacto con otro infectado
     */
    @Transactional
    public void notificarContactosCercano( UUID uuidUsuario, LinkedList<ContactoCercano> contactos,String dniRastreador){
        Usuario usuario = repositorioUsuarios.buscar(uuidUsuario).orElseThrow(UsuarioNoRegistrado::new);
        repositorioRastreadores.buscar(dniRastreador).orElseThrow(RastreadorNoRegistrado::new);
        
        
        //Criterio de inserción para evitar repetidos
        contactos.forEach(contactoCercano -> {
            Usuario usuarioAux=repositorioUsuarios.buscar(contactoCercano.getUsuarioContacto().getUuid()).orElseThrow(UsuarioNoRegistrado::new);
            Enum mayor= Enum.MAYOR;
            if(uuidUsuario.toString().compareTo(contactoCercano.toString()) == mayor.ordinal()){
                contactoCercano.setUsuarioContactado(usuarioAux);//lo actualizamos para recibir fecha positivo
                usuario.insertarContactoOrdenado(contactoCercano);
           
            }else{
                //cambiamos el contacto por el usuario
                contactoCercano.setUsuarioContactado(usuario);
                usuarioAux.insertarContactoOrdenado(contactoCercano);
                repositorioUsuarios.actualizar(usuarioAux);
            }
        });
        
        repositorioUsuarios.actualizar(usuario);
        
    }
    
   
    /**
     * @param uuidUsuario
     * @param dniRastreador
     * @brief Estadistica de la lista de contactos infectados cercanos a un usuario positivo en un periodo de tiempo
     * @return List de contactos cercanos infectados
     */
    public List<Contacto> getListaContactosCercanosPositivo(UUID uuidUsuario,String dniRastreador) { 
        
        if (!repositorioRastreadores.buscar(dniRastreador).isPresent()) {
            throw new RastreadorNoRegistrado();
        }
                
        repositorioUsuarios.buscar(uuidUsuario).orElseThrow(UsuarioNoRegistrado::new);
        
        return repositorioUsuarios.getListaContactosCercanos(uuidUsuario);
    }
    
}
