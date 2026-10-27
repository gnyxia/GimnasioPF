package com.example.gimnasio.datos;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Miembro {
    private final String documento;
    private String nombre;
    private String telefono;
    private Plan plan;
    private final LocalDate fechaInicio;
    private LocalDate fechaVencimiento;

    // Miembro nuevo: el vencimiento se calcula solo
    public Miembro(String documento, String nombre, String telefono, Plan plan, LocalDate fechaInicio) {
        this(documento, nombre, telefono, plan, fechaInicio, 
            fechaInicio.plusMonths(plan.getMeses()));
    }

    // Al leer el archivo: el vencimiento ya viene guardado (Puede tener renovaciones)
    public Miembro(String documento, String nombre, String telefono, Plan plan, LocalDate fechaInicio, LocalDate fechaVencimiento) {
        this.documento = documento;
        this.nombre = nombre;
        this.telefono = telefono;
        this.plan = plan;
        this.fechaInicio = fechaInicio;
        this.fechaVencimiento = fechaVencimiento;
    }

    public long diasRestantes() {
        return ChronoUnit.DAYS.between(LocalDate.now(), fechaVencimiento);
    }

    public long diasTotales() {
        return Math.max(1, ChronoUnit.DAYS.between(fechaInicio, fechaVencimiento));
    }

    // regla si vence hoy (0 dias) cuenta como POR_VENCER, todavia puede entrar.
    // Vencida solo desde manana
    public Estado getEstado() {
        long dias = diasRestantes();
        if (dias < 0) return Estado.VENCIDA;
        if (dias <= 5) return  Estado.POR_VENCER;
        return Estado.ACTIVA;
    }

    // Si sigue vigente se suma al vencimiento; si ya se vencio, cuenta desde hoy
    public void renovar(Plan nuevoPlan) {
        LocalDate hoy = LocalDate.now();
        LocalDate base = fechaVencimiento.isBefore(hoy) ? hoy : fechaVencimiento;
        this.plan = nuevoPlan;
        this.fechaVencimiento = base.plusMonths(nuevoPlan.getMeses());
    }

    public String aLinea() {
        return String.join("|", documento, nombre.replace("|", "/"),
        telefono.replace("|", "/"),
        fechaInicio.toString(), fechaVencimiento.toString());
    }

    public static Miembro desdeLinea(String linea) {
        String[] p = linea.split("\\/", -1);
        return  new Miembro(p[0], p[1], p[2], Plan.valueOf(p[3]), LocalDate.parse(p[4]), LocalDate.parse(p[5]));
    }

    public String getDocumento() { return documento; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public Plan getPlan() { return plan; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setPlan(Plan plan) { this.plan = plan; }
}
