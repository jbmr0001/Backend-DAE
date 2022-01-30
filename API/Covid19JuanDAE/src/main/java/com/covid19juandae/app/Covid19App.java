/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author jmait
 */
@SpringBootApplication(scanBasePackages={"com.covid19juandae.servicios","com.covid19juandae.repositorios","com.covid19juandae.controladoresREST","com.covid19juandae.seguridad"})
@EntityScan(basePackages="com.covid19juandae.entidades")
public class Covid19App {
    
    public static void main(String[] args) throws Exception {
       
        // Creación de servidor
        SpringApplication servidor = new SpringApplication(Covid19App.class);
        ApplicationContext contexto = servidor.run(args);
    }
}
