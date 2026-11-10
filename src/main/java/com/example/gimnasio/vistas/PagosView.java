package com.example.gimnasio.vistas;

import com.example.gimnasio.datos.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route(value = "pagos", layout = MainLayout.class)
public class PagosView extends VerticalLayout {

    private final Almacen almacen;
    private final Grid<Pago> grid = new Grid<>();

    public PagosView(Almacen almacen) {
        this.almacen = almacen;

        TextField documento = new TextField("Documento del miembro");
        Select<Plan> plan = new Select<>();
        plan.setLabel("Plan");
        plan.setItems(Plan.values());
        plan.setValue(Plan.MENSUAL);
        NumberField monto = new NumberField("Monto");

        FormLayout form = new FormLayout(documento, plan, monto);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 3));

        Button registrar = new Button("Registrar pago", e -> {
            String error = Validador.primerError(
                Validador.documento(documento.getValue()),
                plan.getValue() == null ? "Elige un plan" : null,
                Validador.monto(monto.getValue()));
            if (error != null) {
                Notification.show(error, 4000, Notification.Position.MIDDLE);
                return;
            }
            try {
                Miembro m = almacen.registrarPago(documento.getValue(), plan.getValue(), monto.getValue());
                Notification.show("Pago registrado. Nuevo vencimiento de "
                    + m.getNombre() + ": " + m.getFechaVencimiento(), 5000, Notification.Position.MIDDLE);
                documento.clear();
                monto.clear();
                refrescar();
            } catch (RuntimeException ex) {
                Notification.show(ex.getMessage(), 4000, Notification.Position.MIDDLE);
            }
        });
        registrar.getElement().setAttribute("theme", "primary");

        grid.addColumn(Pago::fecha).setHeader("Fecha");
        grid.addColumn(Pago::documento).setHeader("Documento");
        grid.addColumn(Pago::plan).setHeader("Plan");
        grid.addColumn(Pago::monto).setHeader("Monto");

        add(new H2("Pagos"), form, registrar, grid);
        refrescar();
    }

    private void refrescar() {
        grid.setItems(almacen.getPagos().reversed()); // los mas recientes primero
    }

}
