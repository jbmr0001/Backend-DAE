/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.servicios;

import com.covid19juandae.entidades.Contacto;
import com.covid19juandae.entidades.ContactoCercano;
import com.covid19juandae.entidades.Rastreador;
import com.covid19juandae.entidades.Usuario;
import com.covid19juandae.excepciones.RastreadorNoRegistrado;
import com.covid19juandae.excepciones.UsuarioNoRegistrado;
import com.covid19juandae.repositorios.RepositorioRastreadores;
import com.covid19juandae.repositorios.RepositorioUsuarios;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author jmait
 */
@SpringBootTest(classes = com.covid19juandae.app.Covid19App.class)
@ActiveProfiles(profiles = {"test"})
public class ServicioCovidAppTest {
    
    @Autowired
    ServicioCovidApp servicioCovidApp;
    
    @Autowired
    RepositorioRastreadores repositorioRastreadores;
    
    @Autowired
    RepositorioUsuarios repositorioUsuarios;
    
    @Test
    public void testAccesoServicioCovidApp() {
        Assertions.assertThat(servicioCovidApp).isNotNull();
    }
    
    /**
     * @brief Test alta usuario con teléfono no válido 
    */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testAltaUsuarioInvalido() {
       
        LocalDateTime fAlta = LocalDateTime.of(2021, 9, 19,0,0);
        LocalDateTime fPos = LocalDateTime.of(2021, 9, 24,0,0);
        LocalDateTime fCur = LocalDateTime.of(2021, 10, 24,0,0);
                
        Usuario user = new Usuario(
            "685F938563");
        Assertions.assertThatThrownBy(() -> {
            servicioCovidApp.altaUsuario(user); })
                .isInstanceOf(ConstraintViolationException.class);
    }

    /**
     * @brief Test alta rastreador con DNI no válido 
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testAltaRastreadorInvalido() {
       
        String clave="dae2022";
        
        Rastreador rastreador=new Rastreador(
            "26516720Cd",
            "Manolo José Manolez",
            "123456789",
            clave);

        Assertions.assertThatThrownBy(() -> {
            servicioCovidApp.altaRastreador(rastreador); })
                .isInstanceOf(ConstraintViolationException.class);
    }
    
    /**
     * @brief Test alta y login Rastreador
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testAltaYLoginRastreador() {
        
        String clave="dae2022";
        
        Rastreador rastreador=new Rastreador(
            "26516720D",
            "Manolo José Manolez",
            "697667138",
            clave);
        
        servicioCovidApp.altaRastreador(rastreador);
        Optional<Rastreador> rastreadorLogin = servicioCovidApp.loginRastreador(rastreador.getDni(), "dae2022");
        
        Assertions.assertThat(rastreadorLogin.isPresent()).isTrue();
        Assertions.assertThat(rastreadorLogin.get().getDni()).isEqualTo(rastreador.getDni());
        
    }
    
    /**
     * @brief test comprobacion insercion correcta en BBDD
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testInsercionEnBaseDeDatos() {
        //Rastreador
        Rastreador rastreador = new Rastreador(
            "26516720C",
            "Manolo José Manolez",
            "653456789",
            "dae2022");

        LocalDateTime fAlta = LocalDateTime.of(2021, 9, 19,0,0);
        LocalDateTime fCur = LocalDateTime.of(2021, 10, 3,0,0);
        LocalDateTime fPos = LocalDateTime.of(2021, 9, 24,0,0);
        
        // Usuario
        Usuario user = new Usuario(
            "685938563",
            fAlta,
             false,
            fPos,
            fCur);
        
        servicioCovidApp.altaUsuario(user);
        servicioCovidApp.altaRastreador(rastreador);
        
        Usuario comprobacionUsuario=repositorioUsuarios.buscar(user.getUuid()).orElseThrow(UsuarioNoRegistrado::new);
        Rastreador comprobacionRastreador=repositorioRastreadores.buscar(rastreador.getDni()).orElseThrow(RastreadorNoRegistrado::new);
        
        Assertions.assertThat(comprobacionUsuario.getUuid()).isEqualTo(user.getUuid());
        Assertions.assertThat(comprobacionUsuario.getPositivo()).isEqualTo(user.getPositivo());
        Assertions.assertThat(comprobacionUsuario.getTelefono()).isEqualTo(user.getTelefono());
        Assertions.assertThat(comprobacionUsuario.getfCuracion()).isEqualTo(user.getfCuracion());
        Assertions.assertThat(comprobacionUsuario.getfPositivo()).isEqualTo(user.getfPositivo());
        
        Assertions.assertThat(comprobacionRastreador.getDni()).isEqualTo(rastreador.getDni());
        Assertions.assertThat(comprobacionRastreador.getNombre()).isEqualTo(rastreador.getNombre());
        Assertions.assertThat(comprobacionRastreador.getTelefono()).isEqualTo(rastreador.getTelefono());
        Assertions.assertThat(comprobacionRastreador.getClave()).isEqualTo(rastreador.getClave());
        
    }
    
    
    /**
     * @brief Test de Notificar Positivo
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testNotificarPositivo() {
        
        //Rastreador
        Rastreador rastreador = new Rastreador(
            "26516720C",
            "Manulo José Manolez",
            "653456781",
            "dae2022");

        
       // LocalDateTime fechaPositivo = LocalDateTime.of(2021, 10, 7,0,0);
        // Usuario
        Usuario user = new Usuario(
            "685938563");
       
        servicioCovidApp.altaRastreador(rastreador);
        servicioCovidApp.altaUsuario(user);
        servicioCovidApp.notificarPositivo(rastreador.getDni(), user.getUuid());
        
        Usuario comprobacionUsuario=repositorioUsuarios.buscar(user.getUuid()).orElseThrow(UsuarioNoRegistrado::new);
        Rastreador comprobacionRastreador=repositorioRastreadores.buscar(rastreador.getDni()).orElseThrow(RastreadorNoRegistrado::new);
        
        
        Assertions.assertThat(servicioCovidApp.numPositivosActual(rastreador.getDni())).isNotEqualTo(0);
        Assertions.assertThat(comprobacionRastreador.getNumUsuariosReportados()).isEqualTo(1);
        Assertions.assertThat(comprobacionUsuario.getfPositivo()).isNotNull();
        
    }
    
    /**
     * @brief Test de Notificar Curación
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testNotificarCuracion() {
        
        //Rastreador
        Rastreador rastreador = new Rastreador(
            "26516720C",
            "Manolo José Manolez",
            "653456789",
            "dae2022");
        
       // LocalDateTime fechaCuracion = LocalDateTime.of(2021, 10, 7,0,0);
       
        // Usuario
        Usuario user = new Usuario(
            "685938563");
        
        servicioCovidApp.altaRastreador(rastreador);
        servicioCovidApp.altaUsuario(user);
        
        servicioCovidApp.notificarPositivo(rastreador.getDni(), user.getUuid());
        servicioCovidApp.notificarCuracion(rastreador.getDni(), user.getUuid());
        
        Usuario comprobacionUsuario=repositorioUsuarios.buscar(user.getUuid()).orElseThrow(UsuarioNoRegistrado::new);
        Rastreador comprobacionRastreador=repositorioRastreadores.buscar(rastreador.getDni()).orElseThrow(RastreadorNoRegistrado::new);
        
        
        Assertions.assertThat(servicioCovidApp.numPositivosActual(rastreador.getDni())).isEqualTo(0);
        Assertions.assertThat(comprobacionRastreador.getNumUsuariosReportados()).isEqualTo(0);
        Assertions.assertThat(comprobacionUsuario.getPositivo()).isFalse();
    }
    
    /**
     * @brief test comprobar numero actual de positivos
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testVerNumPositivosActual() {
       
        String clave="dae2022";
        
        Rastreador rastreador=new Rastreador(
            "26516720C",
            "Manolo José Manolez",
            "697667138",
            clave);
        
        servicioCovidApp.altaRastreador(rastreador);
        
          Usuario user = new Usuario(
            "685938561",
            null,
            false,
            null,
            null);
          
        servicioCovidApp.altaUsuario(user);
        servicioCovidApp.notificarPositivo(rastreador.getDni(), user.getUuid());
        
        Assertions.assertThat(servicioCovidApp.numPositivosActual("26516720C")).isNotEqualTo(0);
    }
    
    
    /**
     * @brief Test de devolver lista de usuarios por quincena indicada
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testVerPositivosQuincena(){
       
        Usuario usuario1 = new Usuario(
            "685938563");
        
        Usuario usuario2 = new Usuario(
            "697667137");
        
        Usuario usuario3 = new Usuario(
            "697333137");
        
        //Rastreador
        Rastreador rastreador = new Rastreador(
            "26516720C",
            "Manolo José Manolez",
            "653456789",
            "dae2022");
        
        servicioCovidApp.altaUsuario(usuario1);
        servicioCovidApp.altaUsuario(usuario2);
        servicioCovidApp.altaUsuario(usuario3);
        servicioCovidApp.altaRastreador(rastreador);
        servicioCovidApp.notificarPositivo(rastreador.getDni(), usuario1.getUuid());
        servicioCovidApp.notificarPositivo(rastreador.getDni(), usuario2.getUuid());
        servicioCovidApp.notificarPositivo(rastreador.getDni(), usuario3.getUuid());
        
        Assertions.assertThat(servicioCovidApp.numPositivosQuincena(rastreador.getDni())).isBetween(3,3);
     
    }
    
    /**
     * @brief Test consulta lista infectados totales
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testVerInfectadosTotales() {
        Usuario usuario1 = new Usuario(
            "685938563");
        
        Usuario usuario2 = new Usuario(
            "697667137");
        
        Usuario usuario3 = new Usuario(
            "697333137");
        
        //Rastreador
        Rastreador rastreador = new Rastreador(
            "26516720C",
            "Manolo José Manolez",
            "653456789",
            "dae2022");
        
        servicioCovidApp.altaUsuario(usuario1);
        servicioCovidApp.altaUsuario(usuario2);
        servicioCovidApp.altaUsuario(usuario3);
        servicioCovidApp.altaRastreador(rastreador);
        
        servicioCovidApp.notificarPositivo(rastreador.getDni(), usuario1.getUuid());
        servicioCovidApp.notificarPositivo(rastreador.getDni(), usuario3.getUuid());
        servicioCovidApp.notificarCuracion(rastreador.getDni(), usuario3.getUuid());
        
        Assertions.assertThat(servicioCovidApp.numInfectadosTotales(rastreador.getDni())).isEqualTo(2);
        
    }
    
    /**
     * @brief test detecta positivos reportados
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testVerPositivosResportados() {
        Usuario usuario1 = new Usuario(
            "685938563");
        
        Usuario usuario2 = new Usuario(
            "697667137");
        
        Usuario usuario3 = new Usuario(
            "697333137");
        
        //Rastreador
        Rastreador rastreador = new Rastreador(
            "26516720C",
            "Manolo José Manolez",
            "653456789",
            "dae2022");
        
        servicioCovidApp.altaUsuario(usuario1);
        servicioCovidApp.altaUsuario(usuario2);
        servicioCovidApp.altaUsuario(usuario3);
        servicioCovidApp.altaRastreador(rastreador);
        
        
        servicioCovidApp.notificarPositivo(rastreador.getDni(), usuario1.getUuid());
        servicioCovidApp.notificarPositivo(rastreador.getDni(), usuario2.getUuid());
        servicioCovidApp.notificarPositivo(rastreador.getDni(), usuario3.getUuid());
        
        Assertions.assertThat(servicioCovidApp.numPositivosReportados(rastreador.getDni())).isEqualTo(3);
    }
    
    /**
     * @brief test notificar contactos cercanos correctamente
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testNotificarContactosCercanos() {

        
        Usuario usuario1 = new Usuario(
            "685938563");
        
        Usuario usuario2 = new Usuario(
            "697667137");
        
        Usuario usuario3 = new Usuario(
            "697333137");
        
        //Rastreador
        Rastreador rastreador = new Rastreador(
            "26516720C",
            "Manolo José Manolez",
            "653456789",
            "dae2022");
        
        servicioCovidApp.altaUsuario(usuario1);
        servicioCovidApp.altaUsuario(usuario2);
        servicioCovidApp.altaUsuario(usuario3);
        
        servicioCovidApp.altaRastreador(rastreador);
        
        servicioCovidApp.notificarPositivo(rastreador.getDni(), usuario1.getUuid());
        servicioCovidApp.notificarCuracion(rastreador.getDni(), usuario2.getUuid());
        servicioCovidApp.notificarPositivo(rastreador.getDni(), usuario2.getUuid());
        servicioCovidApp.notificarPositivo(rastreador.getDni(), usuario3.getUuid());
        
        
        LocalDateTime fechaContacto=LocalDateTime.now();
        LinkedList<ContactoCercano> lista=new LinkedList();
        
        lista.add(new ContactoCercano(usuario1,fechaContacto,12.0,21.0));
        lista.add(new ContactoCercano(usuario2,fechaContacto,12.0,21.0));
        
        servicioCovidApp.notificarContactosCercano(usuario1.getUuid(), lista);
        
        repositorioUsuarios.buscar(usuario1.getUuid()).orElseThrow(UsuarioNoRegistrado::new);
        Assertions.assertThat(servicioCovidApp.numPositivosActual(rastreador.getDni())).isEqualTo(3);
        Assertions.assertThat(servicioCovidApp.getListaContactosCercanosPositivo(usuario1.getUuid(),rastreador.getDni()).size()).isEqualTo(2);
        Assertions.assertThat(servicioCovidApp.getListaContactosCercanosPositivo(usuario1.getUuid(),rastreador.getDni()).get(0).getUsuario().getUuid()).isEqualTo(usuario1.getUuid());
        Assertions.assertThat(servicioCovidApp.getListaContactosCercanosPositivo(usuario1.getUuid(),rastreador.getDni()).get(0)).isInstanceOf(Contacto.class);
        
    }
    
    /**
     * @brief test realizar la media de los contactos por usuario positivo
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testMediaContactosPositivo() {
        
        Usuario usuario1 = new Usuario(
            "685938563");
        
        Usuario usuario2 = new Usuario(
            "697667137");
        
        Usuario usuario3 = new Usuario(
            "697333137");
        
        //Rastreador
        Rastreador rastreador = new Rastreador(
            "26516720C",
            "Manolo José Manolez",
            "653456789",
            "dae2022");
        
        servicioCovidApp.altaUsuario(usuario1);
        servicioCovidApp.altaUsuario(usuario2);
        servicioCovidApp.altaUsuario(usuario3);
        
        servicioCovidApp.altaRastreador(rastreador);
        
        
        servicioCovidApp.notificarPositivo(rastreador.getDni(), usuario1.getUuid());
        servicioCovidApp.notificarPositivo(rastreador.getDni(), usuario2.getUuid());
        servicioCovidApp.notificarPositivo(rastreador.getDni(), usuario3.getUuid());
        
        
        LocalDateTime fechaContacto=LocalDateTime.of(2021, 10, 23,11,22);
        LocalDateTime fechaContacto2=LocalDateTime.of(2021, 10, 24,11,22);
        
        
        LinkedList<ContactoCercano> listaU=new LinkedList();
        LinkedList<ContactoCercano> listaU1=new LinkedList();
        
        listaU.add(new ContactoCercano(usuario2,fechaContacto,12.0,21.0));
        listaU.add(new ContactoCercano(usuario2,fechaContacto,12.0,21.0));
        listaU.add(new ContactoCercano(usuario2,fechaContacto,12.0,21.0));
        listaU.add(new ContactoCercano(usuario2,fechaContacto,12.0,21.0));
        
        listaU1.add(new ContactoCercano(usuario1,fechaContacto2,10.0,21.0));
        listaU1.add(new ContactoCercano(usuario1,fechaContacto2,10.0,21.0));
        
        
        servicioCovidApp.notificarContactosCercano(usuario1.getUuid(), listaU);
        servicioCovidApp.notificarContactosCercano(usuario2.getUuid(), listaU1);
        // 2/3=0,666 
        Assertions.assertThat(servicioCovidApp.mediaContactosPositivo(rastreador.getDni())).isEqualTo(0.6666666666666666);
        
        
    }
    
    /**
     * @brief Test de el borrado de contactos 31 días
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testBorradoContactos31Dias(){
        Usuario usuario1 = new Usuario(
            "685938563");
        
        Usuario usuario2 = new Usuario(
            "697667137");
        
        Usuario usuario3 = new Usuario(
            "697333137");
        
        //Rastreador
        Rastreador rastreador = new Rastreador(
            "26516720C",
            "Manolo José Manolez",
            "653456789",
            "dae2022");
        servicioCovidApp.altaUsuario(usuario1);
        servicioCovidApp.altaUsuario(usuario2);
        servicioCovidApp.altaUsuario(usuario3);
        servicioCovidApp.altaRastreador(rastreador);
        servicioCovidApp.notificarPositivo(rastreador.getDni(), usuario1.getUuid());
        servicioCovidApp.notificarPositivo(rastreador.getDni(), usuario2.getUuid());
        servicioCovidApp.notificarPositivo(rastreador.getDni(), usuario3.getUuid());
      
        LocalDateTime fechaContacto=LocalDateTime.of(2020, 8, 19,11,1);
     
        LinkedList<ContactoCercano> lista=new LinkedList();
        
        lista.add(new ContactoCercano(usuario1,fechaContacto,12.0,21.0));
        lista.add(new ContactoCercano(usuario2,fechaContacto,12.0,21.0));
        
        servicioCovidApp.notificarContactosCercano(usuario1.getUuid(), lista);
       
        servicioCovidApp.borrarContactos31Dias(rastreador.getDni());
 
        Assertions.assertThat(servicioCovidApp.getListaContactosCercanosPositivo(usuario1.getUuid(),rastreador.getDni()).size()).isEqualTo(0);
    }
  
}
