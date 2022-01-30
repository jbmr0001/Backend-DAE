package com.covid19juandae.util;

/**
 * Recopilación de expresiones regulares para validación
 * @author ajrueda
 */
public class ExprReg {
    private ExprReg() {   
    }
    
    public static final String DNI = "\\d{8}[A-HJ-NP-TV-Z]";
    public static final String TLF = "^(\\+34|0034|34)?[6789]\\d{8}$";
    public static final String NUM_CUENTA = "\\d{10}";
    public static final String NUM_TARJETA = 
            "^(?:(?<visa>4[0-9]{12}(?:[0-9]{3})?)|" +
            "(?<mastercard>5[1-5][0-9]{14})|" + 
            "(?<discover>6(?:011|5[0-9]{2})[0-9]{12})|" + 
            "(?<amex>3[47][0-9]{13})|" + 
            "(?<diners>3(?:0[0-5]|[68][0-9])?[0-9]{11})|" + 
            "(?<jcb>(?:2131|1800|35[0-9]{3})[0-9]{11}))$";
    public static final String CVC = "\\d{3}";
}   
