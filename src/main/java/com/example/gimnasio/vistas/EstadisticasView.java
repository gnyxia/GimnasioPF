package com.example.gimnasio.vistas;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.example.gimnasio.datos.*;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Route;

@Route(value = "estadisticas", layout = MainLayout.class)
public class EstadisticasView extends VerticalLayout {

    public EstadisticasView(Almacen almacen) {
        add(new H2("Estadísticas"));

        // 1. Asistencia por dia de la semana
        add(new H3("Asistencia por día de la semana"));
        Map<DayOfWeek, Long> porDia = almacen.getAsistencias().stream().collect(Collectors.groupingBy(a -> a.getEntrada().getDayOfWeek(), Collectors.counting()));
        long maximo = porDia.values().stream().max(Long::compare).orElse(1L);

        for (DayOfWeek dia : DayOfWeek.values()) {
            long cantidad = porDia.getOrDefault(dia, 0L);
            Span nombre = new Span(dia.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-CO")));
            nombre.setMinWidth("90px");
            ProgressBar barra = new ProgressBar();
            barra.setValue((double) cantidad / maximo);
            barra.setWidthFull();
            HorizontalLayout fila = new HorizontalLayout(nombre, barra, new Span(String.valueOf(cantidad)));
            fila.setWidthFull();
            fila.setAlignItems(FlexComponent.Alignment.CENTER);
            fila.expand(barra);
            add(fila);
        }

        // 2. Activos vs vencidos
        add(new H3("Miembros activos vs vencidos"));
        List<Miembro> todos = almacen.getMiembros();
        long vencidos = todos.stream().filter(m -> m.getEstado() == Estado.VENCIDA).count();
        long activos = todos.size() - vencidos;
        double proporcion = todos.isEmpty() ? 0 : (double) activos / todos.size();

        ProgressBar barraActivos = new ProgressBar();
        barraActivos.setValue(proporcion);
        barraActivos.setWidthFull();
        add(new Span("Activos: " + activos + " · Vencidos: " + vencidos
                + " (" + Math.round(proporcion * 100) + "% activos)"), barraActivos);
    }

}
