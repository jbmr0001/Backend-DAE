/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.covid19juandae.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Codificador sencillo para contraseñas basado en Md5 (no seguro)
 * @author ajrueda
 */
public class CodificadorPassword {
    
    static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private CodificadorPassword() {
    }

    public static String codificar(String cadena) {
/*
        String cadenaCodificada = null;
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(cadena.getBytes());
            cadenaCodificada = Base64.getEncoder().withoutPadding().encodeToString(md.digest());
        }
        catch(NoSuchAlgorithmException e) {
            // No debe ocurrir puesto que MD5 es un algoritmo que existe en la
            // implementación Java estándar
        }
*/
        return encoder.encode(cadena);
    }
    
    public static boolean igual(String password, String passwordCodificado) {
        return encoder.matches(password, passwordCodificado);
    }
}