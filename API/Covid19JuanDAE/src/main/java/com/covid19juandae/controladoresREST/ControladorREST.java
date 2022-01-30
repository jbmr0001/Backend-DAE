/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.controladoresREST;

import com.covid19juandae.controladoresREST.DTO.DTOContacto;
import com.covid19juandae.controladoresREST.DTO.DTOContactoCercano;
import com.covid19juandae.controladoresREST.DTO.DTORastreador;
import com.covid19juandae.controladoresREST.DTO.DTOUsuario;
import com.covid19juandae.entidades.Contacto;
import com.covid19juandae.entidades.ContactoCercano;
import com.covid19juandae.entidades.Rastreador;
import com.covid19juandae.entidades.Usuario;
import com.covid19juandae.excepciones.RastreadorNoRegistrado;
import com.covid19juandae.excepciones.RastreadorYaRegistrado;
import com.covid19juandae.excepciones.UsuarioNoRegistrado;
import com.covid19juandae.excepciones.UsuarioYaRegistrado;
import com.covid19juandae.servicios.ServicioCovidApp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author jmait
 */
@RestController
@RequestMapping("/covid19juandae")
public class ControladorREST {
    
    @Autowired
    ServicioCovidApp servicios;
    
    /** Handler para excepciones de violación de restricciones
     * @param e  */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handlerViolacionRestricciones(ConstraintViolationException e) {
        //return ResponseEntity.badRequest().body(e.getMessage());
    }

    /** Handler para excepciones de accesos de usuarios no registrados
     * @param e */
    @ExceptionHandler(UsuarioNoRegistrado.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handlerUsuarioNoRegistrado(UsuarioNoRegistrado e) {
    }
    
    
    /** Handler para excepciones de accesos de rastreadores no registrados
     * @param e */
    @ExceptionHandler(RastreadorNoRegistrado.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handlerRastreadorNoRegistrado(RastreadorNoRegistrado e) {
    }
    
    
     /** Creación de usuarios */
    @PostMapping("/usuarios")
    ResponseEntity<DTOUsuario> altaUsuario(@RequestBody DTOUsuario usuario) {
        try {
            UUID uuid=servicios.altaUsuario(usuario.aUsuario());
          
            return ResponseEntity.status(HttpStatus.CREATED).body(new DTOUsuario(uuid));
        }
        catch(UsuarioYaRegistrado e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    /** consulta de usuarios**/
    @GetMapping("/usuarios/{uuid}")
    ResponseEntity<DTOUsuario> verUsuario(@PathVariable String uuid) {
        Optional<Usuario> usuario = servicios.verUsuario(UUID.fromString(uuid));
        return usuario
                .map(u -> ResponseEntity.ok(new DTOUsuario(u)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    
     /** Creación de Rastreadores */
    @PostMapping("/rastreadores")
    ResponseEntity<DTORastreador> altaRastreador(@RequestBody DTORastreador rastreador) {
        try {
            String dni=servicios.altaRastreador(rastreador.aRastreador());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(new DTORastreador(dni));
        }
        catch(RastreadorYaRegistrado e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    
    /** Consulta de rastreador */
    @GetMapping("/rastreadores/{dni}")
    ResponseEntity<DTORastreador> verRastreador(@PathVariable String dni) {
        Optional<Rastreador> rastreador = servicios.verRastreador(dni);
        return rastreador
                .map(r -> ResponseEntity.ok(new DTORastreador(r)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /** Notificar curacion*/
    @PostMapping("/rastreadores/{dni}/notificarcuracion")
    ResponseEntity<DTOUsuario> notificarCuracion(@PathVariable String dni,@RequestBody DTOUsuario usuario) {
        try {
            servicios.notificarCuracion(dni, usuario.getUuid());
            return ResponseEntity.status(HttpStatus.CREATED).body(new DTOUsuario(servicios.verUsuario(usuario.getUuid()).get()));
        }
        catch(UsuarioNoRegistrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /** Notificar positivo*/
    @PostMapping("/rastreadores/{dni}/notificarpositivo")
    ResponseEntity<DTOUsuario> notificarPositivo(@PathVariable String dni,@RequestBody DTOUsuario usuario) {
        try {
            servicios.notificarPositivo(dni, usuario.getUuid());
            return ResponseEntity.status(HttpStatus.CREATED).body(new DTOUsuario(servicios.verUsuario(usuario.getUuid()).get()));
        }
        catch(UsuarioNoRegistrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    
    /* Notificar contacto */
    @PostMapping("/usuarios/{uuid}/contactos")
    ResponseEntity<Void> notificarContacto(@PathVariable String uuid, @RequestBody LinkedList<DTOContactoCercano> contactosCercanos) {
        try {
            
            LinkedList<ContactoCercano> contactos=new LinkedList<>();
            contactosCercanos.forEach(contactoCercanoDTO -> {
                contactos.add(contactoCercanoDTO.aContactoCercano());
            });
            servicios.notificarContactosCercano(UUID.fromString(uuid), contactos);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        catch(UsuarioNoRegistrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    
    /* Listar Contactos */
    @GetMapping("/rastreadores/{dni}/usuarios/{uuid}/contactos")
    @ResponseStatus(HttpStatus.OK)
    List<DTOContacto> verContactos(@PathVariable String uuid,@PathVariable String dni) {
        return servicios.getListaContactosCercanosPositivo(UUID.fromString(uuid), dni).stream()
           .map(DTOContacto::new).collect(Collectors.toList());
            
    }
  
    /** Consultar numero de positivos actuales */
    @GetMapping("/rastreadores/{dni}/positivosactual")
    ResponseEntity<Integer> verNumPositivosActual(@PathVariable String dni) {
        Integer numPositivos = servicios.numPositivosActual(dni);
        return ResponseEntity.ok(numPositivos);
    }
    
    /** Consultar numero de positivos por quincenas */
    @GetMapping("/rastreadores/{dni}/positivosquinc")
    ResponseEntity<Integer> verNumPositivosQuincena(@PathVariable String dni) {
        Integer numPositivos = servicios.numPositivosQuincena(dni);
        return ResponseEntity.ok(numPositivos);
    }
    
    /** Consultar numero de positivos reportados por el rastreador */
    @GetMapping("/rastreadores/{dni}/positivosreportados")
    ResponseEntity<Integer> verNumPositivosReportados(@PathVariable String dni) {
        Integer numPositivos = servicios.numPositivosReportados(dni);
        return ResponseEntity.ok(numPositivos);
    }
    
    /** Consultar numero de infectados totales */
    @GetMapping("/rastreadores/{dni}/infectadostotales")
    ResponseEntity<Integer> verNumInfectadosTotales(@PathVariable String dni) {
        Integer numInfectados = servicios.numInfectadosTotales(dni);
        return ResponseEntity.ok(numInfectados);
    }
    
    /** Consultar numero de positivos que han tenido contacto */
    @GetMapping("/rastreadores/{dni}/media")
    ResponseEntity<Double> verMediaContactosPositivos(@PathVariable String dni) {
        Double media = servicios.mediaContactosPositivo(dni);
        return ResponseEntity.status(HttpStatus.OK).body(media);
    }
    
    /** Borrar contactos positivos ultimo mes */
    @GetMapping("/rastreadores/{dni}/borra31")
    ResponseEntity<Void> borradoContactos31Dias(@PathVariable String dni) {
        servicios.borrarContactos31Dias(dni);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    
}
