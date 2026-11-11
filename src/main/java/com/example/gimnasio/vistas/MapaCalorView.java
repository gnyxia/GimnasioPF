package com.example.gimnasio.vistas;

import com.example.gimnasio.datos.Almacen;
import com.example.gimnasio.datos.Asistencia;
import com.example.gimnasio.datos.Miembro;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Route(value = "mapa-calor", layout = MainLayout.class)
public class MapaCalorView extends VerticalLayout {

    private static final int HORA_INICIO = 6;
    private static final int HORA_FIN = 22;
    private static final Locale ES = Locale.forLanguageTag("es-CO");

    private final Almacen almacen;
    // Solo cuentan las entradas de las últimas 4 semanas
    private final LocalDateTime desde = LocalDateTime.now().minusWeeks(4);

    public MapaCalorView(Almacen almacen) {
        this.almacen = almacen;

        // 1. Conteo de entradas por (día de la semana, hora). Clave: "1-18" = lunes a las 18
        Map<String, Integer> conteo = new HashMap<>();
        for (Asistencia a : almacen.getAsistencias()) {
            if (a.getEntrada().isAfter(desde)) {
                String clave = a.getEntrada().getDayOfWeek().getValue() + "-" + a.getEntrada().getHour();
                conteo.merge(clave, 1, Integer::sum);
            }
        }
        int maximo = conteo.values().stream().max(Integer::compare).orElse(1);

        // 2. Cuadrícula: una columna para las horas y siete para los días
        Div cuadricula = new Div();
        cuadricula.getStyle().set("display", "grid")
                .set("grid-template-columns", "50px repeat(7, 1fr)")
                .set("gap", "2px")
                .set("min-width", "340px");

        cuadricula.add(new Div()); // esquina vacía
        for (DayOfWeek dia : DayOfWeek.values()) {
            Div cabecera = new Div(dia.getDisplayName(TextStyle.SHORT, ES));
            cabecera.getStyle().set("text-align", "center").set("font-size", "12px");
            cuadricula.add(cabecera);
        }

        // 3. Una fila por franja horaria, de 6:00 a 22:00
        for (int hora = HORA_INICIO; hora <= HORA_FIN; hora++) {
            Div etiqueta = new Div(hora + ":00");
            etiqueta.getStyle().set("font-size", "12px");
            cuadricula.add(etiqueta);

            for (DayOfWeek dia : DayOfWeek.values()) {
                int cantidad = conteo.getOrDefault(dia.getValue() + "-" + hora, 0);

                Div celda = new Div();
                celda.getStyle().set("min-height", "24px")
                        .set("border-radius", "3px")
                        .set("cursor", "pointer")
                        .set("background", color(cantidad, maximo));
                celda.getElement().setAttribute("title", cantidad + " entradas");

                final int horaCelda = hora;
                celda.addClickListener(e -> mostrarDetalle(dia, horaCelda));
                cuadricula.add(celda);
            }
        }

        // Contenedor con scroll horizontal para que quepa en el celular
        Div contenedor = new Div(cuadricula);
        contenedor.setWidthFull();
        contenedor.getStyle().set("overflow-x", "auto");

        add(new H2("Mapa de calor de asistencia (últimas 4 semanas)"),
                contenedor,
                crearLeyenda());
    }

    // Más entradas = color más intenso; sin entradas = gris claro
    private String color(int cantidad, int maximo) {
        if (cantidad == 0) return "rgba(128, 128, 128, 0.12)";
        double intensidad = 0.2 + 0.8 * cantidad / maximo;
        return "rgba(0, 120, 255, " + intensidad + ")";
    }

    // Leyenda: de "Menos" a "Más"
    private Component crearLeyenda() {
        HorizontalLayout leyenda = new HorizontalLayout(new Span("Menos"));
        leyenda.setAlignItems(FlexComponent.Alignment.CENTER);
        for (int i = 0; i <= 4; i++) {
            Div cuadro = new Div();
            cuadro.getStyle().set("width", "24px")
                    .set("height", "24px")
                    .set("border-radius", "3px")
                    .set("background", color(i, 4));
            leyenda.add(cuadro);
        }
        leyenda.add(new Span("Más"));
        return leyenda;
    }

    // Al hacer clic en una celda: lista de miembros que asistieron en esa franja
    private void mostrarDetalle(DayOfWeek dia, int hora) {
        List<Miembro> lista = almacen.getAsistencias().stream()
                .filter(a -> a.getEntrada().isAfter(desde)
                          && a.getEntrada().getDayOfWeek() == dia
                          && a.getEntrada().getHour() == hora)
                .map(Asistencia::getDocumento)
                .distinct()
                .map(almacen::buscar)
                .flatMap(Optional::stream)
                .toList();

        Dialog dialogo = new Dialog();
        dialogo.setHeaderTitle(dia.getDisplayName(TextStyle.FULL, ES) + " " + hora + ":00");

        if (lista.isEmpty()) {
            dialogo.add(new Span("Nadie asistió en esta franja."));
        } else {
            Grid<Miembro> grid = new Grid<>();
            grid.addComponentColumn(Componentes::avatarNombre).setHeader("Miembro");
            grid.setItems(lista);
            dialogo.add(grid);
        }

        dialogo.getFooter().add(new Button("Cerrar", e -> dialogo.close()));
        dialogo.open();
    }
}