/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.excepciones;

/**
 * Excepción provocada por intentos de acceso o creación de cuentas
 * de usuarios no registrados
 * @author ajrueda
 */
public class UsuarioNoRegistrado extends RuntimeException {

    public UsuarioNoRegistrado() {
    }
}
