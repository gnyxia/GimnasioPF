package com.example.gimnasio.datos;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

@Component
public class Almacen {

    public record Resultado(boolean exito, String mensaje) {}

    private static final Path CARPETA = Path.of("datos");

    private final List<Miembro> miembros = new ArrayList<>();
    private final List<Pago> pagos = new ArrayList<>();
    private final List<Asistencia> asistencias = new ArrayList<>();
    private final List<Rutina> rutinas = new ArrayList<>();
    private final List<RegistroPeso> pesos = new ArrayList<>();

    public Almacen() {
        if (Files.exists(CARPETA.resolve("miembros.txt"))) {
            cargar();
        } else {
            crearEjemplo();
        }
    }

    // ---------- Lectura y escritura de archivos ----------

    private List<String> leer(String archivo) {
        Path ruta = CARPETA.resolve(archivo);
        if (!Files.exists(ruta)) return List.of(); // archivo que falta: lista vacía, no se cae
        try {
            return Files.readAllLines(ruta, StandardCharsets.UTF_8).stream()
                    .filter(l -> !l.isBlank()).toList();
        } catch (IOException e) {
            System.err.println("No se pudo leer " + archivo + ": " + e.getMessage());
            return List.of();
        }
    }

    private void escribir(String archivo, List<String> lineas) {
        try {
            Files.createDirectories(CARPETA);
            Files.write(CARPETA.resolve(archivo), lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo guardar " + archivo + ". Revisa los permisos de la carpeta datos", e);
        }
    }

    // Cada línea se lee en su propio try/catch: una dañada se ignora y las demás se cargan
    private <T> void cargarLista(String archivo, Function<String, T> lector, List<T> destino) {
        int numero = 0;
        for (String linea : leer(archivo)) {
            numero++;
            try {
                destino.add(lector.apply(linea));
            } catch (Exception e) {
                System.err.println("Línea " + numero + " ignorada en " + archivo
                        + ": \"" + linea + "\" (" + e.getMessage() + ")");
            }
        }
    }

    private void cargar() {
        cargarLista("miembros.txt", Miembro::desdeLinea, miembros);
        cargarLista("pagos.txt", Pago::desdeLinea, pagos);
        cargarLista("asistencias.txt", Asistencia::desdeLinea, asistencias);
        cargarLista("rutinas.txt", Rutina::desdeLinea, rutinas);
        cargarLista("pesos.txt", RegistroPeso::desdeLinea, pesos);
    }

    private void guardarMiembros() { escribir("miembros.txt", miembros.stream().map(Miembro::aLinea).toList()); }
    private void guardarPagos() { escribir("pagos.txt", pagos.stream().map(Pago::aLinea).toList()); }
    private void guardarAsistencias() { escribir("asistencias.txt", asistencias.stream().map(Asistencia::aLinea).toList()); }
    private void guardarRutinas() { escribir("rutinas.txt", rutinas.stream().map(Rutina::aLinea).toList()); }
    private void guardarPesos() { escribir("pesos.txt", pesos.stream().map(RegistroPeso::aLinea).toList()); }

    // ---------- Datos de ejemplo (fechas relativas a hoy) ----------

    private void crearEjemplo() {
        LocalDate hoy = LocalDate.now();
        miembros.add(new Miembro("1001", "Ana Pérez", "3001234567", Plan.TRIMESTRAL, hoy.minusDays(10), hoy.plusDays(80)));
        miembros.add(new Miembro("1002", "Carlos Gómez", "3012345678", Plan.MENSUAL, hoy.minusDays(27), hoy.plusDays(3)));
        miembros.add(new Miembro("1003", "Laura Restrepo", "3023456789", Plan.ANUAL, hoy.minusDays(100), hoy.plusDays(265)));
        miembros.add(new Miembro("1004", "Juan Torres", "3034567890", Plan.MENSUAL, hoy.minusDays(29), hoy.plusDays(1)));
        miembros.add(new Miembro("1005", "Marta Ríos", "3045678901", Plan.MENSUAL, hoy.minusDays(60), hoy.minusDays(30)));
        miembros.add(new Miembro("1006", "Pedro Salazar", "3056789012", Plan.TRIMESTRAL, hoy.minusDays(95), hoy.minusDays(5)));
        miembros.add(new Miembro("1007", "Sofía Cano", "3067890123", Plan.MENSUAL, hoy.minusDays(45), hoy.minusDays(15)));
        miembros.add(new Miembro("1008", "Diego Mejía", "3078901234", Plan.TRIMESTRAL, hoy.minusDays(50), hoy.plusDays(40)));

        pagos.add(new Pago("1001", hoy.minusDays(10), 150000, Plan.TRIMESTRAL));
        pagos.add(new Pago("1002", hoy.minusDays(27), 60000, Plan.MENSUAL));
        pagos.add(new Pago("1003", hoy.minusDays(100), 480000, Plan.ANUAL));

        // Asistencias de las últimas 4 semanas (sirven para estadísticas y mapa de calor)
        Random azar = new Random(42);
        int[] horas = {6, 7, 8, 12, 17, 18, 18, 19, 19, 20};
        for (Miembro m : miembros) {
            if (m.getEstado() == Estado.VENCIDA) continue;
            for (int d = 1; d <= 28; d++) {
                if (azar.nextInt(100) < 45) {
                    LocalDateTime entrada = hoy.minusDays(d)
                            .atTime(horas[azar.nextInt(horas.length)], azar.nextInt(60));
                    asistencias.add(new Asistencia(m.getDocumento(), entrada,
                            entrada.plusMinutes(60 + azar.nextInt(31))));
                }
            }
        }

        rutinas.add(new Rutina("1001", "Lunes: pecho y tríceps\nMiércoles: espalda y bíceps\nViernes: pierna"));
        pesos.add(new RegistroPeso("1001", hoy.minusDays(10), 64.5));
        pesos.add(new RegistroPeso("1001", hoy.minusDays(3), 63.8));

        guardarMiembros(); guardarPagos(); guardarAsistencias(); guardarRutinas(); guardarPesos();
    }

    // ---------- Miembros (CRUD) ----------

    public synchronized List<Miembro> getMiembros() { return new ArrayList<>(miembros); }

    public synchronized Optional<Miembro> buscar(String documento) {
        String doc = documento == null ? "" : documento.trim();
        return miembros.stream().filter(m -> m.getDocumento().equals(doc)).findFirst();
    }

    public synchronized void agregarMiembro(Miembro nuevo) {
        if (buscar(nuevo.getDocumento()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un miembro con ese documento");
        }
        miembros.add(nuevo);
        guardarMiembros();
    }

    public synchronized void actualizarMiembro(String documento, String nombre, String telefono, Plan plan) {
        Miembro m = buscar(documento)
                .orElseThrow(() -> new IllegalArgumentException("El miembro ya no existe"));
        m.setNombre(nombre);
        m.setTelefono(telefono);
        m.setPlan(plan);
        guardarMiembros();
    }

    // Borra también todo lo que pertenece al miembro, para no dejar datos huérfanos
    public synchronized void eliminarMiembro(String documento) {
        miembros.removeIf(m -> m.getDocumento().equals(documento));
        pagos.removeIf(p -> p.documento().equals(documento));
        asistencias.removeIf(a -> a.getDocumento().equals(documento));
        rutinas.removeIf(r -> r.documento().equals(documento));
        pesos.removeIf(p -> p.documento().equals(documento));
        guardarMiembros(); guardarPagos(); guardarAsistencias(); guardarRutinas(); guardarPesos();
    }

    public synchronized List<Miembro> porVencer() {
        LocalDate hoy = LocalDate.now();
        return miembros.stream()
                .filter(m -> !m.getFechaVencimiento().isBefore(hoy)
                          && !m.getFechaVencimiento().isAfter(hoy.plusDays(5)))
                .sorted(Comparator.comparing(Miembro::getFechaVencimiento))
                .toList();
    }

    // ---------- Pagos ----------

    public synchronized List<Pago> getPagos() { return new ArrayList<>(pagos); }

    public synchronized Miembro registrarPago(String documento, Plan plan, double monto) {
        Miembro m = buscar(documento)
                .orElseThrow(() -> new IllegalArgumentException("No existe un miembro con ese documento"));
        pagos.add(new Pago(m.getDocumento(), LocalDate.now(), monto, plan));
        guardarPagos();      // primero el pago
        m.renovar(plan);
        guardarMiembros();   // luego el miembro
        return m;
    }

    // ---------- Asistencias ----------

    public synchronized List<Asistencia> getAsistencias() { return new ArrayList<>(asistencias); }

    public synchronized List<Asistencia> asistenciasDe(String documento) {
        return asistencias.stream().filter(a -> a.getDocumento().equals(documento)).toList();
    }

    private Optional<Asistencia> abierta(String documento) {
        return asistencias.stream()
                .filter(a -> a.getDocumento().equals(documento) && a.getSalida() == null)
                .findFirst();
    }

    public synchronized Resultado registrarEntrada(String documento) {
        Optional<Miembro> om = buscar(documento);
        if (om.isEmpty()) return new Resultado(false, "No existe un miembro con ese documento");
        Miembro m = om.get();
        if (m.getEstado() == Estado.VENCIDA) {
            return new Resultado(false, "ACCESO DENEGADO: la membresía de " + m.getNombre()
                    + " venció el " + m.getFechaVencimiento());
        }
        if (abierta(m.getDocumento()).isPresent()) {
            return new Resultado(false, m.getNombre() + " ya tiene una entrada sin salida");
        }
        asistencias.add(new Asistencia(m.getDocumento(), LocalDateTime.now(), null));
        guardarAsistencias();
        return new Resultado(true, "Bienvenido/a, " + m.getNombre());
    }

    public synchronized Resultado registrarSalida(String documento) {
        Optional<Miembro> om = buscar(documento);
        if (om.isEmpty()) return new Resultado(false, "No existe un miembro con ese documento");
        Miembro m = om.get();
        Optional<Asistencia> abierta = abierta(m.getDocumento());
        if (abierta.isEmpty()) {
            return new Resultado(false, m.getNombre() + " no tiene una entrada abierta");
        }
        abierta.get().setSalida(LocalDateTime.now());
        guardarAsistencias();
        return new Resultado(true, "Hasta luego, " + m.getNombre());
    }

    // ---------- Rutina y peso ----------

    public synchronized String getRutina(String documento) {
        return rutinas.stream().filter(r -> r.documento().equals(documento))
                .map(Rutina::texto).findFirst().orElse("");
    }

    public synchronized void guardarRutina(String documento, String texto) {
        rutinas.removeIf(r -> r.documento().equals(documento));
        rutinas.add(new Rutina(documento, texto));
        guardarRutinas();
    }

    public synchronized List<RegistroPeso> getPesos(String documento) {
        return pesos.stream().filter(p -> p.documento().equals(documento))
                .sorted(Comparator.comparing(RegistroPeso::fecha).reversed()).toList();
    }

    public synchronized void agregarPeso(RegistroPeso registro) {
        pesos.add(registro);
        guardarPesos();
    }
}