/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.ControladoresREST;

import com.covid19juandae.controladoresREST.DTO.DTOContacto;
import com.covid19juandae.controladoresREST.DTO.DTOContactoCercano;
import com.covid19juandae.controladoresREST.DTO.DTORastreador;
import com.covid19juandae.controladoresREST.DTO.DTOUsuario;
import com.covid19juandae.entidades.Rastreador;
import com.covid19juandae.entidades.Usuario;
import com.covid19juandae.util.CodificadorMd5;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author jmait
 */
@SpringBootTest(classes = com.covid19juandae.app.Covid19App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"test"})
public class ControladorRESTTest {
    
    @LocalServerPort
    int localPort;

    @Autowired
    MappingJackson2HttpMessageConverter springBootJacksonConverter;

    TestRestTemplate restTemplate;

    /**
     * @brief Crear un TestRestTemplate para las pruebas
     */
    @PostConstruct
    void crearRestTemplateBuilder() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                .rootUri("http://localhost:" + localPort + "/covid19juandae")
                .additionalMessageConverters(List.of(springBootJacksonConverter));
        
        restTemplate = new TestRestTemplate(restTemplateBuilder);        
    }

    /**
     * @brief Intento de creación de un rastreador inválido
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testAltaRastreadorInvalido() {
        //rastreador invalido
        String clave="dae2022";
        DTORastreador rastreador = new DTORastreador(
                "26516720C",
            "Manolo José Manolez",
            "6976671381111",
                clave);

        ResponseEntity<String> respuesta = restTemplate.postForEntity("/rastreadores", rastreador, String.class);
       
        Assertions.assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    
    /**
     * @brief test de alta y login de rastreador
     */
    @Test    
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)                
    public void testAltaYAccesoDatosRastreador() {
        String clave="dae2022";
        DTORastreador rastreador = new DTORastreador(
                "26516720C",
            "Manolo José Manolez",
            "697667138",
                clave);

        ResponseEntity<DTORastreador> respuestaAlta = restTemplate.postForEntity(
                "/rastreadores",
                rastreador,
                DTORastreador.class
        );
        Assertions.assertThat(respuestaAlta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTORastreador> respuestaLogin = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .getForEntity(
                        "/rastreadores/{dni}",
                        DTORastreador.class,
                        rastreador.getDni()
                );

        Assertions.assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        DTORastreador rastreadorLogin = respuestaLogin.getBody();

        Assertions.assertThat(rastreador.getDni().equals(rastreadorLogin.getDni()));
    }

    
    /**
     * @brief Intento de creación de un usuario inválido
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testAltaUsuarioInvalido() {
        DTOUsuario user = new DTOUsuario(
            "685F938563");

        ResponseEntity<DTOUsuario> respuesta = restTemplate.postForEntity("/usuarios", user, DTOUsuario.class);
       
        Assertions.assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    
    /**
     * @brief test de alta y login de rastreador
     */
    @Test    
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)                
    public void testAltaYAccesoDatosUsuario() {
       
        DTOUsuario user = new DTOUsuario(
            "685938563");

        ResponseEntity<DTOUsuario> respuestaAlta = restTemplate.postForEntity("/usuarios", user, DTOUsuario.class);
        Assertions.assertThat(respuestaAlta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
      
         
        String clave="dae2022";
        DTORastreador rastreador = new DTORastreador(
                "26516720C",
            "Manolo José Manolez",
            "697667138",
                clave);

        ResponseEntity<DTORastreador> respuestaAltaRastreador = restTemplate.postForEntity(
                "/rastreadores",
                rastreador,
                DTORastreador.class
        );
        Assertions.assertThat(respuestaAltaRastreador.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTOUsuario> respuestaGet = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .getForEntity(
                        "/usuarios/{uuid}",
                        DTOUsuario.class,
                        respuestaAlta.getBody().getUuid()
                );

        Assertions.assertThat(respuestaGet.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        DTOUsuario respuestaUsuario = respuestaGet.getBody();

        Assertions.assertThat(user.getTelefono().equals(respuestaUsuario.getTelefono()));
        
        
    }
    
    /**
     * @brief Test de Notificar una Curación de usuario
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD) 
    public void testNotificarCuracion(){
        //Rastreador
        DTORastreador rastreador = new DTORastreador(
            "26516720C",
            "Manulo José Manolez",
            "653456781",
            "dae2022");

        ResponseEntity<DTORastreador> respuestaAltaRastreador = restTemplate.postForEntity(
                "/rastreadores",
                rastreador,
                DTORastreador.class
        );
        Assertions.assertThat(respuestaAltaRastreador.getStatusCode()).isEqualTo(HttpStatus.CREATED);
       
        DTOUsuario user = new DTOUsuario(
            "685938563");

        ResponseEntity<DTOUsuario> respuestaAltaUsuario = restTemplate.postForEntity("/usuarios", user, DTOUsuario.class);
        Assertions.assertThat(respuestaAltaUsuario.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTOUsuario> respuestaLogin = restTemplate
                .getForEntity(
                        "/usuarios/{uuid}",
                        DTOUsuario.class,
                        respuestaAltaUsuario.getBody().getUuid()
                );

        Assertions.assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        ResponseEntity<DTOUsuario> respuestaNotificar = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .postForEntity(
                        "/rastreadores/{dni}/notificarcuracion",
                        respuestaAltaUsuario.getBody(),
                        DTOUsuario.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaNotificar.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(respuestaNotificar.getBody().getPositivo()).isFalse();
    }
    
    /**
     * @brief Test de Notificar un usuario Positivo
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD) 
    public void testNotificarPositivo(){
        //Rastreador
        DTORastreador rastreador = new DTORastreador(
            "26516720C",
            "Manulo José Manolez",
            "653456781",
            "dae2022");

        ResponseEntity<DTORastreador> respuestaAltaRastreador = restTemplate.postForEntity(
                "/rastreadores",
                rastreador,
                DTORastreador.class
        );
        Assertions.assertThat(respuestaAltaRastreador.getStatusCode()).isEqualTo(HttpStatus.CREATED);
       
        DTOUsuario user = new DTOUsuario(
            "685938563");

        ResponseEntity<DTOUsuario> respuestaAltaUsuario = restTemplate.postForEntity("/usuarios", user, DTOUsuario.class);
        Assertions.assertThat(respuestaAltaUsuario.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTOUsuario> respuestaLogin = restTemplate
                .getForEntity(
                        "/usuarios/{uuid}",
                        DTOUsuario.class,
                        respuestaAltaUsuario.getBody().getUuid()
                );

        Assertions.assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        ResponseEntity<DTOUsuario> respuestaNotificar = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .postForEntity(
                        "/rastreadores/{dni}/notificarpositivo",
                        respuestaAltaUsuario.getBody(),
                        DTOUsuario.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaNotificar.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        Assertions.assertThat(respuestaNotificar.getBody().getPositivo()).isTrue();
    }
    
    /**
     * @brief test comprobar numero actual de positivos
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testVerNumPositivosActual() {
       
       //Rastreador
        DTORastreador rastreador = new DTORastreador(
            "26516720C",
            "Manulo José Manolez",
            "653456781",
            "dae2022");

        ResponseEntity<DTORastreador> respuestaAltaRastreador = restTemplate.postForEntity(
                "/rastreadores",
                rastreador,
                DTORastreador.class
        );
        Assertions.assertThat(respuestaAltaRastreador.getStatusCode()).isEqualTo(HttpStatus.CREATED);
       
        DTOUsuario user = new DTOUsuario(
            "685938563");

        ResponseEntity<DTOUsuario> respuestaAltaUsuario = restTemplate.postForEntity("/usuarios", user, DTOUsuario.class);
        Assertions.assertThat(respuestaAltaUsuario.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTOUsuario> respuestaLogin = restTemplate
                .getForEntity(
                        "/usuarios/{uuid}",
                        DTOUsuario.class,
                        respuestaAltaUsuario.getBody().getUuid()
                );

        Assertions.assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        ResponseEntity<DTOUsuario> respuestaNotificar = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .postForEntity(
                        "/rastreadores/{dni}/notificarpositivo",
                        respuestaAltaUsuario.getBody(),
                        DTOUsuario.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaNotificar.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<Integer> respuestaNumeroPositivos = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .getForEntity(
                        "/rastreadores/{dni}/positivosactual",
                        Integer.class,
                        rastreador.getDni()
                );
        Assertions.assertThat(respuestaNumeroPositivos.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaNumeroPositivos.getBody()).isEqualTo(1);
    }
    
    /**
     * @brief test detecta si se reportan bien los usuarios positivos 
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testVerNumPositivosReportados() {
       
       //Rastreador
        DTORastreador rastreador = new DTORastreador(
            "26516720C",
            "Manulo José Manolez",
            "653456781",
            "dae2022");

        ResponseEntity<DTORastreador> respuestaAltaRastreador = restTemplate.postForEntity(
                "/rastreadores",
                rastreador,
                DTORastreador.class
        );
        Assertions.assertThat(respuestaAltaRastreador.getStatusCode()).isEqualTo(HttpStatus.CREATED);
       
        DTOUsuario user = new DTOUsuario(
            "685938563");

        ResponseEntity<DTOUsuario> respuestaAltaUsuario = restTemplate.postForEntity("/usuarios", user, DTOUsuario.class);
        Assertions.assertThat(respuestaAltaUsuario.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTOUsuario> respuestaLogin = restTemplate
                .getForEntity(
                        "/usuarios/{uuid}",
                        DTOUsuario.class,
                        respuestaAltaUsuario.getBody().getUuid()
                );

        Assertions.assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.OK);
         
        ResponseEntity<DTOUsuario> respuestaNotificar = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .postForEntity(
                        "/rastreadores/{dni}/notificarpositivo",
                        respuestaAltaUsuario.getBody(),
                        DTOUsuario.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaNotificar.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<Integer> respuestaNumeroReportados = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .getForEntity(
                        "/rastreadores/{dni}/positivosreportados",
                        Integer.class,
                        rastreador.getDni()
                );
        Assertions.assertThat(respuestaNumeroReportados.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaNumeroReportados.getBody()).isEqualTo(1);
    }
    
    /**
     * @brief Test de devolver lista de usuarios por quincena indicada
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testVerNumPositivosQuincena() {
       
       //Rastreador
        DTORastreador rastreador = new DTORastreador(
            "26516720C",
            "Manulo José Manolez",
            "653456781",
            "dae2022");

        ResponseEntity<DTORastreador> respuestaAltaRastreador = restTemplate.postForEntity(
                "/rastreadores",
                rastreador,
                DTORastreador.class
        );
        Assertions.assertThat(respuestaAltaRastreador.getStatusCode()).isEqualTo(HttpStatus.CREATED);
       
        DTOUsuario user = new DTOUsuario(
            "685938563");

        ResponseEntity<DTOUsuario> respuestaAltaUsuario = restTemplate.postForEntity("/usuarios", user, DTOUsuario.class);
        Assertions.assertThat(respuestaAltaUsuario.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTOUsuario> respuestaLogin = restTemplate
                .getForEntity(
                        "/usuarios/{uuid}",
                        DTOUsuario.class,
                        respuestaAltaUsuario.getBody().getUuid()
                );

        Assertions.assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        ResponseEntity<DTOUsuario> respuestaNotificar = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .postForEntity(
                        "/rastreadores/{dni}/notificarpositivo",
                        respuestaAltaUsuario.getBody(),
                        DTOUsuario.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaNotificar.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<Integer> respuestaNumeroPositivosQuinc = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .getForEntity(
                        "/rastreadores/{dni}/positivosquinc",
                        Integer.class,
                        rastreador.getDni()
                );
        Assertions.assertThat(respuestaNumeroPositivosQuinc.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaNumeroPositivosQuinc.getBody()).isEqualTo(1);
    }
    
    /**
     * @brief Test consulta el historico de infectados totales
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testVerNumInfectadosTotales() {
       
       //Rastreador
        DTORastreador rastreador = new DTORastreador(
            "26516720C",
            "Manulo José Manolez",
            "653456781",
            "dae2022");

        ResponseEntity<DTORastreador> respuestaAltaRastreador = restTemplate.postForEntity(
                "/rastreadores",
                rastreador,
                DTORastreador.class
        );
        Assertions.assertThat(respuestaAltaRastreador.getStatusCode()).isEqualTo(HttpStatus.CREATED);
       
        DTOUsuario user = new DTOUsuario(
            "685938563");

        ResponseEntity<DTOUsuario> respuestaAltaUsuario = restTemplate.postForEntity("/usuarios", user, DTOUsuario.class);
        Assertions.assertThat(respuestaAltaUsuario.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTOUsuario> respuestaLogin = restTemplate
                .getForEntity(
                        "/usuarios/{uuid}",
                        DTOUsuario.class,
                        respuestaAltaUsuario.getBody().getUuid()
                );

        Assertions.assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        ResponseEntity<DTOUsuario> respuestaNotificarPositivo = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .postForEntity(
                        "/rastreadores/{dni}/notificarpositivo",
                        respuestaAltaUsuario.getBody(),
                        DTOUsuario.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaNotificarPositivo.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        //Lo notificamos positivo
        ResponseEntity<DTOUsuario> respuestaNotificarCuracion= restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .postForEntity(
                        "/rastreadores/{dni}/notificarpositivo",
                        respuestaAltaUsuario.getBody(),
                        DTOUsuario.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaNotificarCuracion.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        //Lo notificamos curado
        ResponseEntity<Integer> respuestaNumeroInfectados = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .getForEntity(
                        "/rastreadores/{dni}/infectadostotales",
                        Integer.class,
                        rastreador.getDni()
                );
        Assertions.assertThat(respuestaNumeroInfectados.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaNumeroInfectados.getBody()).isEqualTo(1);
    }
    
    /**
     * @brief test notificar contactos cercanos correctamente
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD) 
    public void testNotificarYVerContactosCercanos(){
        
        
        //Rastreador
        DTORastreador rastreador = new DTORastreador(
            "26516720C",
            "Manulo José Manolez",
            "653456781",
            "dae2022");

        ResponseEntity<DTORastreador> respuestaAltaRastreador = restTemplate.postForEntity(
                "/rastreadores",
                rastreador,
                DTORastreador.class
        );
        Assertions.assertThat(respuestaAltaRastreador.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        //usuario1
        DTOUsuario usuario1 = new DTOUsuario(
            "685938563");

        ResponseEntity<DTOUsuario> respuestaAltaUsuario1 = restTemplate.postForEntity("/usuarios", usuario1, DTOUsuario.class);
        ResponseEntity<DTOUsuario> respuestaLogin1 = restTemplate
                .getForEntity(
                        "/usuarios/{uuid}",
                        DTOUsuario.class,
                        respuestaAltaUsuario1.getBody().getUuid()
                );
        Assertions.assertThat(respuestaLogin1.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        //usuario2
        DTOUsuario usuario2 = new DTOUsuario(
            "697667137");
        
        ResponseEntity<DTOUsuario> respuestaAltaUsuario2 = restTemplate.postForEntity("/usuarios", usuario2, DTOUsuario.class);
        ResponseEntity<DTOUsuario> respuestaLogin2 = restTemplate
                .getForEntity(
                        "/usuarios/{uuid}",
                        DTOUsuario.class,
                        respuestaAltaUsuario2.getBody().getUuid()
                );
        Assertions.assertThat(respuestaLogin2.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        //usuario 3, al que se le asignan los contactos
        DTOUsuario usuario3 = new DTOUsuario(
            "697333137");

        ResponseEntity<DTOUsuario> respuestaAltaUsuario3 = restTemplate.postForEntity("/usuarios", usuario3, DTOUsuario.class);
        ResponseEntity<DTOUsuario> respuestaLogin3 = restTemplate
                .getForEntity(
                        "/usuarios/{uuid}",
                        DTOUsuario.class,
                        respuestaAltaUsuario3.getBody().getUuid()
                );
        
        Assertions.assertThat(respuestaLogin3.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        //notificar usuarios
        ResponseEntity<DTOUsuario> respuestaNotificar1 = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .postForEntity(
                        "/rastreadores/{dni}/notificarpositivo",
                        respuestaAltaUsuario1.getBody(),
                        DTOUsuario.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaNotificar1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTOUsuario> respuestaNotificar2 = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .postForEntity(
                        "/rastreadores/{dni}/notificarpositivo",
                        respuestaAltaUsuario2.getBody(),
                        DTOUsuario.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaNotificar2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTOUsuario> respuestaNotificar3 = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .postForEntity(
                        "/rastreadores/{dni}/notificarpositivo",
                        respuestaAltaUsuario3.getBody(),
                        DTOUsuario.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaNotificar3.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
       
        LocalDateTime fechaContacto=LocalDateTime.now();
        LinkedList<DTOContactoCercano> lista=new LinkedList();
        
        //asignacion contactos
        lista.add(new DTOContactoCercano(respuestaAltaUsuario1.getBody().getUuid(),fechaContacto,12.0,21.0));
        lista.add(new DTOContactoCercano(respuestaAltaUsuario3.getBody().getUuid(),fechaContacto,12.0,21.0));
        
        ResponseEntity<Void> respuestaNotificarContactoCercano = restTemplate
                .postForEntity(
                       "/usuarios/{uuid}/contactos",
                        lista,
                        Void.class,
                        respuestaAltaUsuario3.getBody().getUuid()
                        
                );
        
        Assertions.assertThat(respuestaNotificarContactoCercano.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTOContacto[]> respuestaContactos = restTemplate
                 .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .getForEntity(
                        "/rastreadores/{dni}/usuarios/{uuid}/contactos",
                        DTOContacto[].class,
                        rastreador.getDni(),respuestaAltaUsuario3.getBody().getUuid()
                );

        Assertions.assertThat(respuestaContactos.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaContactos.getBody().length).isEqualTo(2);
        
    }
    
    
    /**
     * @brief Test de el borrado de contactos 31 días
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testborrarContactos31Dias() {
        
         
        //Rastreador
        DTORastreador rastreador = new DTORastreador(
            "26516720C",
            "Manulo José Manolez",
            "653456781",
            "dae2022");

        ResponseEntity<DTORastreador> respuestaAltaRastreador = restTemplate.postForEntity(
                "/rastreadores",
                rastreador,
                DTORastreador.class
        );
        Assertions.assertThat(respuestaAltaRastreador.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        //usuario1
        DTOUsuario usuario1 = new DTOUsuario(
            "685938563");

        ResponseEntity<DTOUsuario> respuestaAltaUsuario1 = restTemplate.postForEntity("/usuarios", usuario1, DTOUsuario.class);
        ResponseEntity<DTOUsuario> respuestaLogin1 = restTemplate
                .getForEntity(
                        "/usuarios/{uuid}",
                        DTOUsuario.class,
                        respuestaAltaUsuario1.getBody().getUuid()
                );
        Assertions.assertThat(respuestaLogin1.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        //usuario2
        DTOUsuario usuario2 = new DTOUsuario(
            "697667137");
        
        ResponseEntity<DTOUsuario> respuestaAltaUsuario2 = restTemplate.postForEntity("/usuarios", usuario2, DTOUsuario.class);
        ResponseEntity<DTOUsuario> respuestaLogin2 = restTemplate
                .getForEntity(
                        "/usuarios/{uuid}",
                        DTOUsuario.class,
                        respuestaAltaUsuario2.getBody().getUuid()
                );
        Assertions.assertThat(respuestaLogin2.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        //usuario3
        DTOUsuario usuario3 = new DTOUsuario(
            "697333137");

        ResponseEntity<DTOUsuario> respuestaAltaUsuario3 = restTemplate.postForEntity("/usuarios", usuario3, DTOUsuario.class);
        ResponseEntity<DTOUsuario> respuestaLogin3 = restTemplate
                .getForEntity(
                        "/usuarios/{uuid}",
                        DTOUsuario.class,
                        respuestaAltaUsuario3.getBody().getUuid()
                );
        
        Assertions.assertThat(respuestaLogin3.getStatusCode()).isEqualTo(HttpStatus.OK);
         
        //notificaciones positivos
        ResponseEntity<DTOUsuario> respuestaNotificar1 = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .postForEntity(
                        "/rastreadores/{dni}/notificarpositivo",
                        respuestaAltaUsuario1.getBody(),
                        DTOUsuario.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaNotificar1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTOUsuario> respuestaNotificar2 = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .postForEntity(
                        "/rastreadores/{dni}/notificarpositivo",
                        respuestaAltaUsuario2.getBody(),
                        DTOUsuario.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaNotificar2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTOUsuario> respuestaNotificar3 = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .postForEntity(
                        "/rastreadores/{dni}/notificarpositivo",
                        respuestaAltaUsuario3.getBody(),
                        DTOUsuario.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaNotificar3.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
       
        LocalDateTime fechaContacto=LocalDateTime.now();
        LinkedList<DTOContactoCercano> lista=new LinkedList();
        
        //asignacion contactos        
        lista.add(new DTOContactoCercano(respuestaAltaUsuario1.getBody().getUuid(),fechaContacto,12.0,21.0));
        lista.add(new DTOContactoCercano(respuestaAltaUsuario3.getBody().getUuid(),fechaContacto,12.0,21.0));
        
        ResponseEntity<Void> respuestaNotificarContactoCercano = restTemplate
                .postForEntity(
                       "/usuarios/{uuid}/contactos",
                        lista,
                        Void.class,
                        respuestaAltaUsuario3.getBody().getUuid()
                        
                );
        
        Assertions.assertThat(respuestaNotificarContactoCercano.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTOContacto[]> respuestaContactos = restTemplate
                 .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .getForEntity(
                        "/rastreadores/{dni}/usuarios/{uuid}/contactos",
                        DTOContacto[].class,
                        rastreador.getDni(),respuestaAltaUsuario3.getBody().getUuid()
                );

        Assertions.assertThat(respuestaContactos.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaContactos.getBody().length).isEqualTo(2);
        
        //borrado
        ResponseEntity<Void> respuestaBorrado = restTemplate
                 .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .getForEntity(
                        "/rastreadores/{dni}/borra31",
                        Void.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaBorrado.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    
    /**
     * @brief test realizar la media de los contactos por usuario positivo
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testverMediaContactosPositivos() {
      
        //Rastreador
        DTORastreador rastreador = new DTORastreador(
            "26516720C",
            "Manulo José Manolez",
            "653456781",
            "dae2022");

        ResponseEntity<DTORastreador> respuestaAltaRastreador = restTemplate.postForEntity(
                "/rastreadores",
                rastreador,
                DTORastreador.class
        );
        Assertions.assertThat(respuestaAltaRastreador.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        //usuario1
        DTOUsuario usuario1 = new DTOUsuario(
            "685938563");

        ResponseEntity<DTOUsuario> respuestaAltaUsuario1 = restTemplate.postForEntity("/usuarios", usuario1, DTOUsuario.class);
        ResponseEntity<DTOUsuario> respuestaLogin1 = restTemplate
                .getForEntity(
                        "/usuarios/{uuid}",
                        DTOUsuario.class,
                        respuestaAltaUsuario1.getBody().getUuid()
                );
        Assertions.assertThat(respuestaLogin1.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        //usuario2
        DTOUsuario usuario2 = new DTOUsuario(
            "697667137");
        
        ResponseEntity<DTOUsuario> respuestaAltaUsuario2 = restTemplate.postForEntity("/usuarios", usuario2, DTOUsuario.class);
        ResponseEntity<DTOUsuario> respuestaLogin2 = restTemplate
                .getForEntity(
                        "/usuarios/{uuid}",
                        DTOUsuario.class,
                        respuestaAltaUsuario2.getBody().getUuid()
                );
        Assertions.assertThat(respuestaLogin2.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        //usuario3
        DTOUsuario usuario3 = new DTOUsuario(
            "697333137");

        ResponseEntity<DTOUsuario> respuestaAltaUsuario3 = restTemplate.postForEntity("/usuarios", usuario3, DTOUsuario.class);
        ResponseEntity<DTOUsuario> respuestaLogin3 = restTemplate
                .getForEntity(
                        "/usuarios/{uuid}",
                        DTOUsuario.class,
                        respuestaAltaUsuario3.getBody().getUuid()
                );
        
        Assertions.assertThat(respuestaLogin3.getStatusCode()).isEqualTo(HttpStatus.OK);
       
        //notificaciones
        ResponseEntity<DTOUsuario> respuestaNotificar1 = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .postForEntity(
                        "/rastreadores/{dni}/notificarpositivo",
                        respuestaAltaUsuario1.getBody(),
                        DTOUsuario.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaNotificar1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTOUsuario> respuestaNotificar2 = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .postForEntity(
                        "/rastreadores/{dni}/notificarpositivo",
                        respuestaAltaUsuario2.getBody(),
                        DTOUsuario.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaNotificar2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTOUsuario> respuestaNotificar3 = restTemplate
                .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .postForEntity(
                        "/rastreadores/{dni}/notificarpositivo",
                        respuestaAltaUsuario3.getBody(),
                        DTOUsuario.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaNotificar3.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
       
        LocalDateTime fechaContacto=LocalDateTime.now();
        LinkedList<DTOContactoCercano> lista=new LinkedList();
        
        //asignacion contactos a user 3
        lista.add(new DTOContactoCercano(respuestaAltaUsuario1.getBody().getUuid(),fechaContacto,12.0,21.0));
        lista.add(new DTOContactoCercano(respuestaAltaUsuario3.getBody().getUuid(),fechaContacto,12.0,21.0));
        
        ResponseEntity<Void> respuestaNotificarContactoCercano = restTemplate
                .postForEntity(
                       "/usuarios/{uuid}/contactos",
                        lista,
                        Void.class,
                        respuestaAltaUsuario3.getBody().getUuid()
                        
                );
        
        Assertions.assertThat(respuestaNotificarContactoCercano.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ResponseEntity<DTOContacto[]> respuestaContactos = restTemplate
                 .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .getForEntity(
                        "/rastreadores/{dni}/usuarios/{uuid}/contactos",
                        DTOContacto[].class,
                        rastreador.getDni(),respuestaAltaUsuario3.getBody().getUuid()
                );

        Assertions.assertThat(respuestaContactos.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(respuestaContactos.getBody().length).isEqualTo(2);
        
        //obtener media
        ResponseEntity<Double> respuestaMedia = restTemplate
                 .withBasicAuth(rastreador.getDni(), rastreador.getClave())
                .getForEntity(
                        "/rastreadores/{dni}/media",
                        Double.class,
                        rastreador.getDni()
                );
        
        Assertions.assertThat(respuestaMedia.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // 1/3=0,666 
        Assertions.assertThat(respuestaMedia.getBody()).isEqualTo(0.3333333333333333);
   
    }
}
