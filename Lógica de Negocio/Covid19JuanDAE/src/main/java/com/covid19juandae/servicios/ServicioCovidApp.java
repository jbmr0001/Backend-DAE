/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.servicios;

import com.covid19juandae.entidades.Contacto;
import com.covid19juandae.excepciones.*;
import com.covid19juandae.entidades.Rastreador;
import com.covid19juandae.entidades.Usuario;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 *
 * @author jmait
 */
@Service
@Validated
public class ServicioCovidApp {
    /** Mapa con la lista de usuarios ordenada por UUID */
    Map<UUID, Usuario> usuarios;
    Map<UUID, Usuario> usuariosPositivos;
    List<Usuario> userPos;
    
    /** Mapa con la lista de rastreadores ordenada por DNI */
    Map<String, Rastreador> rastreadores;
    
    static final LocalDateTime PASADO_DISTANTE = LocalDateTime.of(1970, 1, 1, 0, 0);
    static final LocalDateTime FUTURO_DISTANTE = LocalDateTime.of(2100, 1, 1, 0, 0);
    
    int numPositivos;
    
    public enum Enum{ MAYOR, MENOR}
    
    public ServicioCovidApp() {
        usuarios = new TreeMap<>();
        usuariosPositivos = new TreeMap<>();
        rastreadores = new TreeMap<>();
        
    }
    
    /**
     * Dar de alta al usuario y guardarlo en la lista de usuarios
     * @param usuario el usuario a dar de alta
     */
    public void altaUsuario(@NotNull @Valid Usuario usuario) {
        if (usuarios.containsKey(usuario.getUuid())) {
            throw new UsuarioYaRegistrado();
        }
        
        // Registrar usuario
        usuarios.put(usuario.getUuid(), usuario);  
        
    }
    
     /**
     * Dar de alta al rastreador y guardarlo en la lista de rastreadores
     * @param rastreador el rastreador a dar de alta
     */
    public void altaRastreador(@NotNull @Valid Rastreador rastreador) {
        if (rastreadores.containsKey(rastreador.getDni())) {
            throw new RastreadorYaRegistrado();
        }
        
        // Registrar rastreador
        rastreadores.put(rastreador.getDni(), rastreador);
    }
    
    /**
     * Realiza un login de un rastreador
     * @param dni el DNI del rastreador
     * @param clave la clave de acceso
     * @return el objeto de la clase Rastreador asociado
     */
    public Optional<Rastreador> loginRastreador(@NotBlank String dni, @NotBlank String clave) {
        
        return Optional.ofNullable(rastreadores.get(dni)).filter((rastreador)->rastreador.claveValida(clave));
    }
    
    /**
     * @return numero de positivos actuales
     * @param DNI
     * @param date
     */
    public int verPositivosActual(String dni, LocalDateTime date){
        
        Rastreador rastreador = Optional.ofNullable(rastreadores.get(dni))
                .orElseThrow(RastreadorNoRegistrado::new);
        
        return usuariosPositivos.size();
    }
    
    /**
     * @brief numero total de contactos / numero total de usuarios
     * @return media
     */
    public int mediaContactosPositivo(){
 
        ArrayList<Usuario> listaUsuarios = new ArrayList<Usuario>(usuariosPositivos.values());
        
        Iterator<Usuario> it=listaUsuarios.iterator();
        int numeroContactos=0;
        int media;
        while(it.hasNext()){
            numeroContactos+=it.next().getContactosCercanos().size();
        }
        media=numeroContactos/usuariosPositivos.size();
        
        return media;
    }
    
    /**
     * @brief Borra los contactos una vez hayan pasado 31 dias desde su deteccion
     */
    public void borrarContactos31Dias(){
        LocalDateTime fechaLimite = Optional.ofNullable(LocalDateTime.now().minusDays(31)).orElse(PASADO_DISTANTE);
        
        ArrayList<Usuario> listaUsuarios = new ArrayList<Usuario>(usuariosPositivos.values());
        Iterator<Usuario> it=listaUsuarios.iterator();
        
        while(it.hasNext()){
            it.next().borrarContactosAnterioresFecha(fechaLimite);
           
        }
    }
   
    /**
     * @brief el rastreador notifica un positivo
     * @param String DNI del rastreador 
     * @param UUID identificador del usuario unico
     * @param LocalDateTime de la fecha de la notificacion del positivo
     */
    public void notificarPositivo(String dni, UUID uuid, LocalDateTime fechaPositivo){
        Usuario usuario = Optional.ofNullable(usuarios.get(uuid))
                .orElseThrow(UsuarioNoRegistrado::new);
        
        Rastreador rastreador = Optional.ofNullable(rastreadores.get(dni))
                .orElseThrow(RastreadorNoRegistrado::new);
        
        usuario.setPositivo(true);
        usuario.setfPositivo(fechaPositivo);
        usuariosPositivos.put(uuid, usuario);
        
        rastreadores.get(dni).incrementaReportados(usuario);
    }
    
    /**
     * @brief el rastreador notifica un usuario curado
     * @param String DNI del rastreador 
     * @param UUID identificador del usuario unico
     * @param LocalDateTime de la fecha de la notificacion de la curación
     */
    public void notificarCuracion(String dni, UUID uuid,LocalDateTime fechaCuracion){
        Usuario usuario = Optional.ofNullable(usuarios.get(uuid))
                .orElseThrow(UsuarioNoRegistrado::new);
        
        Rastreador rastreador = Optional.ofNullable(rastreadores.get(dni))
                .orElseThrow(RastreadorNoRegistrado::new);
                
        usuario.setPositivo(false);
        usuario.setfCuracion(fechaCuracion);
        usuariosPositivos.remove(uuid);
        
        rastreadores.get(dni).eliminaReportado(usuario);
    }
    
    /**
     * @brief Estadistica de la lista de infectados en un periodo de tiempo
     * @param LocalDateTime el filtro 'desde' del período 
     * @param LocalDateTime el filtro 'hasta' del período 
     * @return List de usuarios infectados
     */
    public List<Usuario> listaInfectados ( LocalDateTime desde, LocalDateTime hasta) {
        LocalDateTime fechaHoraDesdeConsulta = Optional.ofNullable(desde).orElse(PASADO_DISTANTE);
        LocalDateTime fechaHoraHastaConsulta = Optional.ofNullable(hasta).orElse(FUTURO_DISTANTE);
                
        /*Usuario usuario = Optional.ofNullable(userPos.get(numPositivos))
                .orElseThrow(UsuarioNoRegistrado::new);*/
        userPos = new ArrayList<Usuario>(usuariosPositivos.values());//convierte en lista
        return userPos.stream().filter(m ->
                m.getfPositivo().isAfter(fechaHoraDesdeConsulta) &&
                m.getfPositivo().isBefore(fechaHoraHastaConsulta)
        ).collect(Collectors.toList());
    }
    
    /**
     * @brief Estadistica de la lista de usuarios positivos en una quincena
     * @param LocalDateTime el filtro 'desde' como incio del periodo de la quincena a buscar
     * @return Entero de nº de usuarios positivos
     */
    public int verPositivosQuincena(LocalDateTime desde){
        LocalDateTime fechaHoraDesdeConsulta = Optional.ofNullable(LocalDateTime.now().minusDays(15)).orElse(PASADO_DISTANTE);
        LocalDateTime fechaHoraHastaConsulta = Optional.ofNullable(LocalDateTime.now()).orElse(FUTURO_DISTANTE);
        userPos = new ArrayList<Usuario>(usuariosPositivos.values());//convierte en lista
        
        return userPos.stream().filter(m ->
                m.getfPositivo().isAfter(fechaHoraDesdeConsulta) &&
                m.getfPositivo().isBefore(fechaHoraHastaConsulta)
        ).collect(Collectors.toList()).size();
    }
    
    /**
     * @brief Estadistica de la lista de contactos infectados cercanos a un usuario positivo en un periodo de tiempo
     * @param LocalDateTime el filtro 'desde' del período 
     * @param LocalDateTime el filtro 'hasta' del período 
     * @return List de contactos cercanos infectados
     */
    public List<Contacto> listaContactosCercanosPositivo (UUID uuid, LocalDateTime desde, LocalDateTime hasta) {
        LocalDateTime fechaHoraDesdeConsulta = Optional.ofNullable(desde).orElse(PASADO_DISTANTE);
        LocalDateTime fechaHoraHastaConsulta = Optional.ofNullable(hasta).orElse(FUTURO_DISTANTE);
        
        Usuario usuario = Optional.ofNullable(usuariosPositivos.get(uuid))
                .orElseThrow(UsuarioNoRegistrado::new);

        return usuario.getContactosCercanos().stream().filter(m ->
                m.getFecha().isAfter(fechaHoraDesdeConsulta) &&
                m.getFecha().isBefore(fechaHoraHastaConsulta)
        ).collect(Collectors.toList());
    }
    
     /**
     * @brief notifica que un usuario ha estado en contacto con otro infectado
     * @param UUID identificador del usuario unico
     * @param LocalDateTime el filtro 'desde' del período 
     * @param LocalDateTime el filtro 'hasta' del período
     */
    public void notificarContactoCercano( UUID uuid, UUID contacto,LocalDateTime fecha,Double distancia, Double duracion){
        Usuario usuario = Optional.ofNullable(usuarios.get(uuid))
                .orElseThrow(UsuarioNoRegistrado::new);
        
        Usuario usuarioContactado = Optional.ofNullable(usuarios.get(uuid))
                .orElseThrow(UsuarioNoRegistrado::new);
        
        
        //Criterio de inserción en función de la clave para evitar repetidos
        Enum mayor= Enum.MAYOR;
        if(uuid.toString().compareTo(contacto.toString())==mayor.ordinal()){
            Contacto contact = new Contacto(fecha,usuarioContactado);
            usuario.insertarContactoOrdenado(contact, distancia, duracion, usuario.getfPositivo().getDayOfMonth()-fecha.getDayOfMonth());
        }else{
            Contacto contact = new Contacto(fecha,usuario);
            usuarioContactado.insertarContactoOrdenado(contact, distancia, duracion, usuario.getfPositivo().getDayOfMonth()-fecha.getDayOfMonth());
        }
        
    }
    
    /**
     * @brief Estadisticas de usuarios reportados por un rastreador segun su DNI
     * @param String DNI
     * @return List de positivos reportados por un rastreador
     */
    public List positivosReportados(String dni){
        
        Rastreador rastreador = Optional.ofNullable(rastreadores.get(dni))
                .orElseThrow(RastreadorNoRegistrado::new);
        
        return rastreadores.get(dni).getReportados();
    }
    
  
}
