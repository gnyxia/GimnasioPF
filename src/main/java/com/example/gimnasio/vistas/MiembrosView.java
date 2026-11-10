package com.example.gimnasio.vistas;

import com.example.gimnasio.datos.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;

@Route(value = "", layout = MainLayout.class)
public class MiembrosView extends VerticalLayout {

    private final Almacen almacen;
    private final Grid<Miembro> grid = new Grid<>();
    private final TextField filtro = new TextField();
    private final Select<Estado> filtroEstado = new Select<>();
    private final Select<Plan> filtroPlan = new Select<>();

    public MiembrosView(Almacen almacen) {
        this.almacen = almacen;

        // Búsqueda en tiempo real: EAGER filtra en cada tecla
        filtro.setPlaceholder("Buscar por nombre o documento");
        filtro.setClearButtonVisible(true);
        filtro.setWidthFull();
        filtro.setValueChangeMode(ValueChangeMode.EAGER);
        filtro.addValueChangeListener(e -> refrescar());

        filtroEstado.setLabel("Estado");
        filtroEstado.setItems(Estado.values());
        filtroEstado.setItemLabelGenerator(e -> e == null ? "Todos" : e.getEtiqueta());
        filtroEstado.setEmptySelectionAllowed(true);
        filtroEstado.addValueChangeListener(e -> refrescar());

        filtroPlan.setLabel("Plan");
        filtroPlan.setItems(Plan.values());
        filtroPlan.setItemLabelGenerator(p -> p == null ? "Todos" : p.name());
        filtroPlan.setEmptySelectionAllowed(true);
        filtroPlan.addValueChangeListener(e -> refrescar());

        HorizontalLayout filtros = new HorizontalLayout(filtroEstado, filtroPlan);
        filtros.setWrap(true);

        Button nuevo = new Button("Nuevo miembro", VaadinIcon.PLUS.create(), e -> abrirFormulario(null));
        nuevo.getElement().setAttribute("theme", "primary");

        grid.addComponentColumn(Componentes::avatarNombre).setHeader("Miembro")
                .setAutoWidth(true).setFlexGrow(1);
        grid.addComponentColumn(Componentes::badge).setHeader("Estado");
        grid.addComponentColumn(Componentes::progreso).setHeader("Vigencia").setWidth("120px");
        grid.addColumn(Miembro::getFechaVencimiento).setHeader("Vence");
        grid.addComponentColumn(this::acciones).setHeader("Acciones").setAutoWidth(true).setFlexGrow(0);

        add(new H2("Miembros"), nuevo, filtro, filtros, grid);
        setSizeFull();
        refrescar();
    }

    private Component acciones(Miembro m) {
        Button editar = new Button(VaadinIcon.EDIT.create(), e -> abrirFormulario(m));
        Button borrar = new Button(VaadinIcon.TRASH.create(), e -> confirmarBorrado(m));
        editar.setAriaLabel("Editar");
        borrar.setAriaLabel("Eliminar");
        editar.getElement().setAttribute("theme", "tertiary");
        borrar.getElement().setAttribute("theme", "tertiary error");
        HorizontalLayout fila = new HorizontalLayout(editar, borrar);
        fila.setPadding(false);
        fila.setSpacing(false);
        return fila;
    }

    private void refrescar() {
        String f = filtro.getValue().trim().toLowerCase();
        grid.setItems(almacen.getMiembros().stream()
                .filter(m -> m.getNombre().toLowerCase().contains(f) || m.getDocumento().contains(f))
                .filter(m -> filtroEstado.getValue() == null || m.getEstado() == filtroEstado.getValue())
                .filter(m -> filtroPlan.getValue() == null || m.getPlan() == filtroPlan.getValue())
                .toList());
    }

    // existente == null: crear. existente != null: editar.
    private void abrirFormulario(Miembro existente) {
        boolean edicion = existente != null;
        Dialog dialogo = new Dialog();
        dialogo.setHeaderTitle(edicion ? "Editar miembro" : "Nuevo miembro");

        TextField documento = new TextField("Documento");
        TextField nombre = new TextField("Nombre");
        TextField telefono = new TextField("Teléfono");
        Select<Plan> plan = new Select<>();
        plan.setLabel("Plan");
        plan.setItems(Plan.values());
        DatePicker inicio = new DatePicker("Fecha de inicio");

        if (edicion) {
            documento.setValue(existente.getDocumento());
            documento.setReadOnly(true);
            nombre.setValue(existente.getNombre());
            telefono.setValue(existente.getTelefono());
            plan.setValue(existente.getPlan());
            inicio.setValue(existente.getFechaInicio());
            inicio.setReadOnly(true);
        } else {
            plan.setValue(Plan.MENSUAL);
            inicio.setValue(LocalDate.now());
        }

        FormLayout form = new FormLayout(documento, nombre, telefono, plan, inicio);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        Button guardar = new Button("Guardar", e -> {
            String error = Validador.primerError(
                    edicion ? null : Validador.documento(documento.getValue()),
                    Validador.nombre(nombre.getValue()),
                    Validador.telefono(telefono.getValue()),
                    plan.getValue() == null ? "Elige un plan" : null,
                    edicion ? null : Validador.fechaInicio(inicio.getValue()));
            if (error != null) {
                Notification.show(error, 4000, Notification.Position.MIDDLE);
                return;
            }
            try {
                if (edicion) {
                    almacen.actualizarMiembro(existente.getDocumento(), nombre.getValue().trim(),
                            telefono.getValue().trim(), plan.getValue());
                } else {
                    almacen.agregarMiembro(new Miembro(documento.getValue().trim(),
                            nombre.getValue().trim(), telefono.getValue().trim(),
                            plan.getValue(), inicio.getValue()));
                }
                dialogo.close();
                refrescar();
                Notification.show(edicion ? "Miembro actualizado" : "Miembro creado");
            } catch (RuntimeException ex) {
                Notification.show(ex.getMessage(), 4000, Notification.Position.MIDDLE);
            }
        });
        guardar.getElement().setAttribute("theme", "primary");

        dialogo.add(form);
        dialogo.getFooter().add(new Button("Cancelar", e -> dialogo.close()), guardar);
        dialogo.open();
    }

    private void confirmarBorrado(Miembro m) {
        Componentes.confirmar("Eliminar miembro",
                "¿Eliminar a " + m.getNombre() + " (" + m.getDocumento() + ")? "
                        + "También se borrarán sus pagos, asistencias, rutina y pesos. "
                        + "Esta acción no se puede deshacer.",
                () -> {
                    try {
                        almacen.eliminarMiembro(m.getDocumento());
                        refrescar();
                        Notification.show("Miembro eliminado");
                    } catch (RuntimeException ex) {
                        Notification.show(ex.getMessage());
                    }
                });
    }
}