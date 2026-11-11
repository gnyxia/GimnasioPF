package com.example.gimnasio.datos;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record RegistroPeso(String documento, LocalDate fecha, Double peso) {

    private static final Pattern LINEA_LEGADA =
            Pattern.compile("^(\\d+)(\\d{4}-\\d{2}-\\d{2})(\\d+(?:\\.\\d+)?)$");

    public String aLinea() {
    return documento + "|" + fecha + "|" + peso;
}

    public static RegistroPeso desdeLinea(String linea) {
    String[] p = linea.split("\\|", -1);
    if (p.length == 1) {
        Matcher matcher = LINEA_LEGADA.matcher(linea.trim());
        if (matcher.matches()) {
            return new RegistroPeso(matcher.group(1), LocalDate.parse(matcher.group(2)),
                    Double.parseDouble(matcher.group(3)));
        }
    }
    return new RegistroPeso(p[0], LocalDate.parse(p[1]), Double.parseDouble(p[2]));
}
}
