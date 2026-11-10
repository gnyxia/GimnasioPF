package com.example.gimnasio.datos;

import java.time.LocalDate;

public class Validador {

    public static String documento(String v) {
        if (v == null || v.isBlank()) return "El documento es obligatorio";
        if (!v.trim().matches("\\d{5,12}"))
            return "El documento debe tener entre 5 y 12 dígitos, sin puntos ni letras";
        return null;
    }

    public static String nombre(String v) {
        if (v == null || v.isBlank()) return "El nombre es obligatorio";
        String n = v.trim();
        if (n.length() < 3 || n.length() > 60) return "El nombre debe tener entre 3 y 60 caracteres";
        if (!n.matches("[\\p{L} .'-]+")) return "El nombre solo puede tener letras y espacios";
        return null;
    }

    public static String telefono(String v) {
        if (v == null || v.isBlank()) return "El teléfono es obligatorio";
        if (!v.trim().matches("3\\d{9}"))
            return "El teléfono debe tener 10 dígitos y empezar por 3 (ej: 3001234567)";
        return null;
    }

    public static String fechaInicio(LocalDate f) {
        if (f == null) return "La fecha de inicio es obligatoria";
        LocalDate hoy = LocalDate.now();
        if (f.isBefore(hoy.minusYears(1))) return "La fecha de inicio no puede ser de hace más de un año";
        if (f.isAfter(hoy.plusDays(30))) return "La fecha de inicio no puede ser dentro de más de 30 días";
        return null;
    }

    public static String monto(Double m) {
        if (m == null) return "El monto es obligatorio";
        if (m <= 0) return "El monto debe ser mayor que cero";
        if (m > 5_000_000) return "El monto no puede superar 5.000.000";
        return null;
    }

    public static String peso(Double p) {
        if (p == null) return "El peso es obligatorio";
        if (p < 20 || p > 300) return "El peso debe estar entre 20 y 300 kg";
        return null;
    }

    public static String fechaPeso(LocalDate f) {
        if (f == null) return "La fecha es obligatoria";
        if (f.isAfter(LocalDate.now())) return "La fecha no puede ser futura";
        return null;
    }

    public static String rutina(String v) {
        if (v != null && v.length() > 1000) return "La rutina no puede superar 1000 caracteres";
        return null;
    }

    // Devuelve el primer error de la lista, o null si no hay ninguno
    public static String primerError(String... errores) {
        for (String e : errores) {
            if (e != null) return e;
        }
        return null;
    }
}