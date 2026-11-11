package com.example.gimnasio.vistas;

import com.example.gimnasio.datos.Almacen;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.ColorScheme;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;

public class MainLayout extends AppLayout {

    private final Almacen almacen;
    private final Button botonTema = new Button();
    private boolean oscuro = false;

    public MainLayout(Almacen almacen) {
        this.almacen = almacen;

        H1 titulo = new H1("Gimnasio");
        titulo.getStyle().set("font-size", "1.2rem").set("margin", "0 0 0 8px");

        botonTema.getElement().setAttribute("theme", "tertiary");
        botonTema.setAriaLabel("Cambiar entre tema claro y oscuro");
        botonTema.getStyle().set("margin-left", "auto").set("margin-right", "12px");
        botonTema.setIcon(VaadinIcon.MOON.create());
        botonTema.addClickListener(e -> {
            aplicarTema(!oscuro);
            // Recuerda la preferencia en el navegador
            UI.getCurrent().getPage().executeJs(
                    "localStorage.setItem('tema', $0)", oscuro ? "oscuro" : "claro");
        });

        addToNavbar(new DrawerToggle(), titulo, botonTema);

        SideNav menu = new SideNav();
        menu.addItem(new SideNavItem("Miembros", MiembrosView.class, VaadinIcon.USERS.create()));
        menu.addItem(new SideNavItem("Pagos", PagosView.class, VaadinIcon.MONEY.create()));
        menu.addItem(new SideNavItem("Portería", PorteriaView.class, VaadinIcon.SIGN_IN.create()));
        menu.addItem(new SideNavItem("Vencimientos", VencimientosView.class, VaadinIcon.CLOCK.create()));
        menu.addItem(new SideNavItem("Estadísticas", EstadisticasView.class, VaadinIcon.BAR_CHART.create()));
        menu.addItem(new SideNavItem("Mapa de calor", MapaCalorView.class, VaadinIcon.GRID_BIG.create()));
        addToDrawer(menu);
    }

    // Cambia el esquema de color y el icono del botón
    private void aplicarTema(boolean oscuro) {
        this.oscuro = oscuro;
        UI.getCurrent().getPage().setColorScheme(
                oscuro ? ColorScheme.Value.DARK : ColorScheme.Value.LIGHT);
        botonTema.setIcon(oscuro ? VaadinIcon.SUN_O.create() : VaadinIcon.MOON.create());
    }

    @Override
    protected void onAttach(AttachEvent evento) {
        super.onAttach(evento);

        // Lee la preferencia guardada en el navegador
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('tema');")
                .then(String.class, guardado -> {
                    if ("oscuro".equals(guardado)) aplicarTema(true);
                });

        int cantidad = almacen.porVencer().size();
        if (cantidad > 0) {
            Notification.show(cantidad + " membresía(s) vencen en los próximos 5 días", 5000,
                    Notification.Position.TOP_CENTER);
        }
    }
}