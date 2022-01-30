package com.covid19juandae.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 *
 * @author admin
 */
public class CodificadorMd5 {

    private CodificadorMd5() {
    }

    public static String codificar(String cadena) {
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
        return cadenaCodificada;
    }
}
