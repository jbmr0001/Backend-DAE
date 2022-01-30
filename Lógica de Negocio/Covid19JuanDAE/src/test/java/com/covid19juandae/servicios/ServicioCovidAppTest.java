/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.servicios;

import com.covid19juandae.entidades.Contacto;
import com.covid19juandae.entidades.Rastreador;
import com.covid19juandae.entidades.Usuario;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import javax.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

/**
 *
 * @author jmait
 */
@SpringBootTest(classes = com.covid19juandae.app.Covid19App.class)
public class ServicioCovidAppTest {
    
    @Autowired
    ServicioCovidApp servicioCovidApp;
    
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
       
        UUID uuid = UUID.randomUUID();
        LocalDateTime fAlta = LocalDateTime.of(2021, 9, 19,0,0);
        LocalDateTime fPos = LocalDateTime.of(2021, 9, 24,0,0);
        LocalDateTime fCur = LocalDateTime.of(2021, 10, 24,0,0);
                
        Usuario user = new Usuario(
            uuid,
            "685F938563",
            fAlta,
             true,
            fPos,
            fCur);

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
            "265167203CB",
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
        
        Rastreador rastreador = new Rastreador(
            "26516720C",
            "Manolo José Manolez",
            "653456789",
            "dae2022");
        
        servicioCovidApp.altaRastreador(rastreador);
        Optional<Rastreador> rastreadorLogin = servicioCovidApp.loginRastreador(rastreador.getDni(), "dae2022");
        
        Assertions.assertThat(rastreadorLogin.isPresent()).isTrue();
        Assertions.assertThat(rastreadorLogin.get()).isEqualTo(rastreador);
    }
    
    /**
     * Brief Test de inserción de contacto
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testInsercionContacto(){
        Usuario usuario1 = new Usuario(
            UUID.randomUUID(),
            "685938563",
            LocalDateTime.of(2021, 9, 19,0,0),
             true,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario2 = new Usuario(
            UUID.randomUUID(),
            "697667137",
            LocalDateTime.of(2021, 9, 19,0,0),
             true,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        LocalDateTime fechaContacto=LocalDateTime.of(2021, 10, 23,11,22);
        
        usuario1.insertarContactoOrdenado(new Contacto(fechaContacto,usuario2), 14, 2, usuario1.getfPositivo().getDayOfMonth()-fechaContacto.getDayOfMonth());
        servicioCovidApp.altaUsuario(usuario1);
        servicioCovidApp.altaUsuario(usuario2);
        
        Assertions.assertThat(usuario1.getContactosCercanos()).hasSize(1);
        Assertions.assertThat(usuario1.getContactosCercanos().get(0)).isInstanceOf(Contacto.class);
        Assertions.assertThat(usuario1.getContactosCercanos().get(0).getFecha()).isEqualTo(LocalDateTime.of(2021, 10, 23,11,22));
        
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
            "Manolo José Manolez",
            "653456789",
            "dae2022");
        
        UUID uuid = UUID.randomUUID();
        LocalDateTime fAlta = LocalDateTime.of(2021, 9, 19,0,0);
        LocalDateTime fCur = LocalDateTime.of(2021, 10, 3,0,0);
        LocalDateTime nuevaFecha = LocalDateTime.of(2021, 10, 7,0,0);
        // Usuario
        Usuario user = new Usuario(
            uuid,
            "685938563",
            fAlta,
             false,
            LocalDateTime.of(2021, 9, 24,0,0),
            fCur);
        
        servicioCovidApp.altaRastreador(rastreador);
        servicioCovidApp.altaUsuario(user);
        servicioCovidApp.notificarPositivo("26516720C", user.getUuid(), nuevaFecha);
        Assertions.assertThat(servicioCovidApp.verPositivosActual("26516720C", LocalDateTime.now())).isEqualTo(1);
        Assertions.assertThat(rastreador.getReportados()).hasSize(1);
        Assertions.assertThat(rastreador.getReportados().get(0)).isInstanceOf(Usuario.class);
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
        
        UUID uuid = UUID.randomUUID();
        LocalDateTime fAlta = LocalDateTime.of(2021, 9, 19,0,0);
        LocalDateTime fCur = LocalDateTime.of(2021, 10, 3,0,0);
        LocalDateTime nuevaFecha = LocalDateTime.of(2021, 10, 7,0,0);
        
        // Usuario
        Usuario user = new Usuario(
            uuid,
            "685938563",
            fAlta,
             false,
            LocalDateTime.of(2021, 9, 24,0,0),
            fCur);
        
        servicioCovidApp.altaRastreador(rastreador);
        servicioCovidApp.altaUsuario(user);
        servicioCovidApp.notificarPositivo("26516720C", user.getUuid(), nuevaFecha);
        servicioCovidApp.notificarCuracion("26516720C", user.getUuid(), nuevaFecha);
        Assertions.assertThat(servicioCovidApp.verPositivosActual("26516720C", LocalDateTime.now())).isEqualTo(0);
        Assertions.assertThat(rastreador.getReportados()).hasSize(0);
 
    }
    
    /**
     * Brief Test de ver usuarios positivos actuales
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testVerPositivosActuales(){
        Usuario usuario1 = new Usuario(
            UUID.randomUUID(),
            "685938563",
            LocalDateTime.of(2021, 9, 19,0,0),
             false,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario2 = new Usuario(
            UUID.randomUUID(),
            "697667137",
            LocalDateTime.of(2021, 9, 19,0,0),
             false,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario3 = new Usuario(
            UUID.randomUUID(),
            "697333137",
            LocalDateTime.of(2021, 9, 19,0,0),
             false,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        //Rastreador
        Rastreador rastreador = new Rastreador(
            "26516720C",
            "Manolo José Manolez",
            "653456789",
            "dae2022");
       
        
        LocalDateTime fechaContacto=LocalDateTime.of(2021, 10, 19,11,22);
        
        
        usuario1.insertarContactoOrdenado(new Contacto(fechaContacto,usuario2), 1.4, 20,usuario1.getfPositivo().getDayOfMonth()-fechaContacto.getDayOfMonth());
        usuario1.insertarContactoOrdenado(new Contacto(fechaContacto,usuario3), 0.2, 20,usuario1.getfPositivo().getDayOfMonth()-fechaContacto.getDayOfMonth());
        servicioCovidApp.altaUsuario(usuario1);
        servicioCovidApp.altaUsuario(usuario2);
        servicioCovidApp.altaUsuario(usuario3);
        servicioCovidApp.altaRastreador(rastreador);
        servicioCovidApp.notificarPositivo("26516720C", usuario1.getUuid(), LocalDateTime.of(2021, 10, 20,0,0));
        servicioCovidApp.notificarPositivo("26516720C", usuario2.getUuid(), LocalDateTime.of(2021, 10, 21,0,0));
        servicioCovidApp.notificarPositivo("26516720C", usuario3.getUuid(), LocalDateTime.of(2021, 10, 22,0,0));
        
        
        Assertions.assertThat(servicioCovidApp.verPositivosActual("26516720C", LocalDateTime.of(2021, 10, 23,0,0))).isEqualTo(3);
    }
    
    /**
     * Brief Test de devolver lista de usuarios por quincena indicada
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testVerPositivosQuincena(){
        Usuario usuario1 = new Usuario(
            UUID.randomUUID(),
            "685938563",
            LocalDateTime.of(2021, 9, 19,0,0),
             false,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario2 = new Usuario(
            UUID.randomUUID(),
            "697667137",
            LocalDateTime.of(2021, 9, 19,0,0),
             false,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario3 = new Usuario(
            UUID.randomUUID(),
            "697333137",
            LocalDateTime.of(2021, 9, 19,0,0),
             false,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        //Rastreador
        Rastreador rastreador = new Rastreador(
            "26516720C",
            "Manolo José Manolez",
            "653456789",
            "dae2022");
        
      
        LocalDateTime fechaContacto=LocalDateTime.of(2021, 10, 19,11,22);
        
        
        usuario1.insertarContactoOrdenado(new Contacto(fechaContacto,usuario2), 1.4, 20,usuario1.getfPositivo().getDayOfMonth()-fechaContacto.getDayOfMonth());
        usuario1.insertarContactoOrdenado(new Contacto(fechaContacto,usuario3), 0.2, 20,usuario1.getfPositivo().getDayOfMonth()-fechaContacto.getDayOfMonth());
        servicioCovidApp.altaUsuario(usuario1);
        servicioCovidApp.altaUsuario(usuario2);
        servicioCovidApp.altaUsuario(usuario3);
        servicioCovidApp.altaRastreador(rastreador);
        servicioCovidApp.notificarPositivo("26516720C", usuario1.getUuid(), LocalDateTime.of(2021, 10, 20,0,0));
        servicioCovidApp.notificarPositivo("26516720C", usuario2.getUuid(), LocalDateTime.of(2021, 10, 21,0,0));
        servicioCovidApp.notificarPositivo("26516720C", usuario3.getUuid(), LocalDateTime.of(2021, 1, 20,0,0));
        
        Assertions.assertThat(servicioCovidApp.verPositivosQuincena(LocalDateTime.of(2021, 10, 21,0,0))).isEqualTo(2);
    }
    
    /**
     * Brief Test de la funcion positivos reportados
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testPositivosReportados(){
        Usuario usuario1 = new Usuario(
            UUID.randomUUID(),
            "685938563",
            LocalDateTime.of(2021, 9, 19,0,0),
             false,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario2 = new Usuario(
            UUID.randomUUID(),
            "697667137",
            LocalDateTime.of(2021, 9, 19,0,0),
             false,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario3 = new Usuario(
            UUID.randomUUID(),
            "697333137",
            LocalDateTime.of(2021, 9, 19,0,0),
             false,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
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
        servicioCovidApp.notificarPositivo("26516720C", usuario1.getUuid(), LocalDateTime.of(2021, 10, 20,0,0));
        servicioCovidApp.notificarPositivo("26516720C", usuario2.getUuid(), LocalDateTime.of(2021, 10, 21,0,0));
        servicioCovidApp.notificarPositivo("26516720C", usuario3.getUuid(), LocalDateTime.of(2021, 1, 20,0,0));
        
        Assertions.assertThat(servicioCovidApp.positivosReportados("26516720C")).hasSize(3);
    }
    
    /**
     * Brief Test de la funcion contacto cercano
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testContactoCercano(){
        Usuario usuario1 = new Usuario(
            UUID.randomUUID(),
            "685938563",
            LocalDateTime.of(2021, 9, 19,0,0),
             false,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario2 = new Usuario(
            UUID.randomUUID(),
            "697667137",
            LocalDateTime.of(2021, 9, 19,0,0),
             false,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario3 = new Usuario(
            UUID.randomUUID(),
            "697333137",
            LocalDateTime.of(2021, 9, 19,0,0),
             false,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
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
        servicioCovidApp.notificarPositivo("26516720C", usuario1.getUuid(), LocalDateTime.of(2021, 10, 20,0,0));
        servicioCovidApp.notificarContactoCercano(usuario1.getUuid(), usuario2.getUuid(), LocalDateTime.of(2021, 10, 21,0,0),2.0,5.0);
        servicioCovidApp.notificarContactoCercano(usuario1.getUuid(), usuario3.getUuid(), LocalDateTime.of(2021, 10, 2,0,0),2.0,5.0);
        
        Assertions.assertThat(usuario1.getContactosCercanos().size()).isEqualTo(2);
        
    }
    
    /**
     * Brief Test de el borrado de contactos 31 días
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testBorradoContactos31Dias(){
        Usuario usuario1 = new Usuario(
            UUID.randomUUID(),
            "685938563",
            LocalDateTime.of(2021, 9, 19,0,0),
             false,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario2 = new Usuario(
            UUID.randomUUID(),
            "697667137",
            LocalDateTime.of(2021, 9, 19,0,0),
             false,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        Usuario usuario3 = new Usuario(
            UUID.randomUUID(),
            "697333137",
            LocalDateTime.of(2021, 9, 19,0,0),
             false,
            LocalDateTime.of(2021, 9, 24,0,0),
            LocalDateTime.of(2021, 10, 24,0,0));
        
        //Rastreador
        Rastreador rastreador = new Rastreador(
            "26516720C",
            "Manolo José Manolez",
            "653456789",
            "dae2022");
        
      
        LocalDateTime fechaContacto=LocalDateTime.of(2021, 8, 19,11,1);
        
        
        usuario1.insertarContactoOrdenado(new Contacto(fechaContacto,usuario2), 1.4, 20,usuario1.getfPositivo().getDayOfMonth()-fechaContacto.getDayOfMonth());
        usuario1.insertarContactoOrdenado(new Contacto(fechaContacto,usuario3), 0.2, 20,usuario1.getfPositivo().getDayOfMonth()-fechaContacto.getDayOfMonth());
        servicioCovidApp.altaUsuario(usuario1);
        servicioCovidApp.altaUsuario(usuario2);
        servicioCovidApp.altaUsuario(usuario3);
        servicioCovidApp.altaRastreador(rastreador);
        servicioCovidApp.notificarPositivo("26516720C", usuario1.getUuid(), LocalDateTime.of(2021, 10, 20,0,0));
        
        servicioCovidApp.borrarContactos31Dias();
        
        Assertions.assertThat(usuario1.getContactosCercanos().size()).isEqualTo(0);
    }
    
}
