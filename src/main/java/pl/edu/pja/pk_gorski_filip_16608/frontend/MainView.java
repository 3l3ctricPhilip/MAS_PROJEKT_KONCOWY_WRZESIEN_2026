package pl.edu.pja.pk_gorski_filip_16608.frontend;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("")
@PageTitle("gui mas project")
public class MainView extends AppLayout {

    public MainView() {
        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("Booker");
        title.getStyle().set("font-size", "1.125rem").set("margin", "0");

        RouterLink newConcertViewLink = new RouterLink("concertform", NewConcertView.class);

        SideNav nav = getSideNav(newConcertViewLink);
        nav.getStyle().set("margin", "var(--vaadin-gap-s)");

        Scroller scroller = new Scroller(nav);

        addToDrawer(scroller);
        addToNavbar(toggle, title);
    }

    private SideNav getSideNav(RouterLink routerLink) {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Dodaj koncert",    routerLink.getHref(),   VaadinIcon.TICKET.create()));
        nav.addItem(new SideNavItem("Przypisz salę koncertową",    "assignhall",   VaadinIcon.AREA_SELECT.create()));
        nav.addItem(new SideNavItem("Moje koncerty",    "myconcerts",   VaadinIcon.CALENDAR.create()));

        return nav;
    }
}