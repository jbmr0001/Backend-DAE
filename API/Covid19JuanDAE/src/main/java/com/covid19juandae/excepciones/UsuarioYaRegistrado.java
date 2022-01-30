/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.excepciones;

/**
 * Excepcion producida por intento de registro de usuarios ya existentes
 * @author ajrueda
 */
public class UsuarioYaRegistrado extends RuntimeException {
    
    public UsuarioYaRegistrado() {
    }  
}
