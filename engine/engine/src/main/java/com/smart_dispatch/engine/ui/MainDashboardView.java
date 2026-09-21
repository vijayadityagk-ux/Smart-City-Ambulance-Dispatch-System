package com.smart_dispatch.engine.ui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Smart City Dispatch - Central Hub")
public class MainDashboardView extends VerticalLayout {

    public MainDashboardView() {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        getStyle()
                .set("background-color", "#121212")
                .set("color", "#ffffff")
                .set("font-family", "system-ui, -apple-system, sans-serif")
                .set("padding", "30px");

        Div card = new Div();
        card.getStyle()
                .set("background-color", "#1e1e1e")
                .set("border-radius", "16px")
                .set("border", "1px solid #333")
                .set("padding", "36px")
                .set("max-width", "650px")
                .set("width", "100%")
                .set("text-align", "center")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.5)");

        H1 title = new H1("🚑 Smart City Emergency Dispatch");
        title.getStyle()
                .set("color", "#ff4444")
                .set("font-size", "2.2rem")
                .set("margin", "0 0 10px 0");

        Paragraph desc = new Paragraph("Centralized Command & Dispatch System (100% Pure Java)");
        desc.getStyle()
                .set("color", "#888888")
                .set("font-size", "1.1rem")
                .set("margin-bottom", "30px");

        card.add(title, desc);

        Div grid = new Div();
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fit, minmax(240px, 1fr))")
                .set("gap", "16px")
                .set("margin-top", "20px");

        grid.add(
                createHubCard("🚑 Paramedic UI", "/paramedic", "Select trauma type and request hospital dispatch", "#ff4444"),
                createHubCard("🧭 Driver Navigation HUD", "/driver", "Live route tracking & real-time dispatch alerts", "#00ff00"),
                createHubCard("📡 Live Dispatch Feed", "/index.html", "Original emergency simulator and STOMP feed", "#00ccff"),
                createHubCard("🗄️ H2 Database Console", "/h2-console", "Inspect in-memory hospital bed allocations", "#ffaa00")
        );

        card.add(grid);
        add(card);
    }

    private Anchor createHubCard(String titleText, String href, String descText, String accentColor) {
        Anchor link = new Anchor(href);
        link.getStyle()
                .set("text-decoration", "none")
                .set("color", "inherit");

        Div card = new Div();
        card.getStyle()
                .set("background-color", "#282828")
                .set("padding", "20px")
                .set("border-radius", "12px")
                .set("border-left", "5px solid " + accentColor)
                .set("text-align", "left")
                .set("cursor", "pointer")
                .set("transition", "transform 0.2s, background-color 0.2s");

        Span title = new Span(titleText);
        title.getStyle()
                .set("color", accentColor)
                .set("font-weight", "bold")
                .set("font-size", "1.15rem")
                .set("display", "block")
                .set("margin-bottom", "6px");

        Span desc = new Span(descText);
        desc.getStyle()
                .set("color", "#aaaaaa")
                .set("font-size", "0.9rem")
                .set("display", "block");

        card.add(title, desc);
        link.add(card);
        return link;
    }
}
