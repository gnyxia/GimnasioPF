package com.example.gimnasio.vistas;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.example.gimnasio.datos.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

@Route(value = "ficha", layout = MainLayout.class)
public class FichaView extends VerticalLayout implements HasUrlParameter<String> {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final Almacen almacen;

    public FichaView(Almacen almacen) {
        this.almacen = almacen;
    }

    @Override 
    public void setParameter(BeforeEvent evento, String documento) {
        removeAll();
        Optional<Miembro> om = almacen.buscar(documento);
        if (om.isEmpty()) {
            add(new H2("Miembro no encontrado"));
            return ;
        }
        Miembro m = om.get();

        HorizontalLayout encabezado = new HorizontalLayout(
                new Avatar(m.getNombre()), new H2(m.getNombre()), Componentes.badge(m));
        encabezado.setAlignItems(FlexComponent.Alignment.CENTER);
        encabezado.setWrap(true);

        Span datos = new Span("Doc: " + m.getDocumento() + " · Tel: " + m.getTelefono() + " · Plan " + m.getPlan() + " · Vence " + m.getFechaVencimiento());

        TabSheet pestanas = new TabSheet();
        pestanas.setWidthFull();
        pestanas.add("Rutina", crearRutina(m));
        pestanas.add("Peso", crearPeso(m));
        pestanas.add("Asistencia", crearAsistencia(m));

        add(encabezado, datos, Componentes.progreso(m), pestanas);
    }

    private  Component crearRutina(Miembro m) {
        TextArea texto = new TextArea("Rutina");
        texto.setWidthFull();
        texto.setMinHeight("150px");
        texto.setValue(almacen.getRutina(m.getDocumento()));
        Button guardar = new Button("Guardar rutina", e -> {
            almacen.guardarRutina(m.getDocumento(), texto.getValue());
            Notification.show("Rutina Guardada");
        });
        return new VerticalLayout(texto, guardar);
    }

    private Component crearPeso(Miembro m) {
        NumberField peso = new NumberField("Peso (kg)");
        DatePicker fecha = new DatePicker("Fecha");
        fecha.setValue(LocalDate.now());
        Grid<RegistroPeso> grid = new Grid<>();
        grid.addColumn(RegistroPeso::fecha).setHeader("Fecha");
        grid.addColumn(RegistroPeso::peso).setHeader("Peso (kg)");
        grid.setItems(almacen.getPesos(m.getDocumento()));

        Button agregar = new Button("Agregar", e -> {
            if (peso.getValue() == null || fecha.isEmpty()) {
                Notification.show("Completa peso y fecha");
                return;
            }
            almacen.agregarPeso(new RegistroPeso(m.getDocumento(), fecha.getValue(), peso.getValue()));
            grid.setItems(almacen.getPesos(m.getDocumento()));
            peso.clear();
        });
        HorizontalLayout fila = new HorizontalLayout(peso, fecha, agregar);
        fila.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        fila.setWrap(true);
        return new VerticalLayout(fila, grid);
    }

    private Component crearAsistencia(Miembro m) {
        Grid<Asistencia> grid = new Grid<>();
        grid.addColumn(a -> a.getEntrada().format(FORMATO)).setHeader("Entrada");
        grid.addColumn(a -> a.getSalida() == null ? "Dentro" : a.getSalida().format(FORMATO)).setHeader("Salida");
        grid.setItems(almacen.asistenciasDe(m.getDocumento()).reversed());
        return grid;
    }


}
