package com.smart_dispatch.engine.ui;

import com.smart_dispatch.engine.model.DispatchAlert;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.function.Consumer;

@Route("driver")
@PageTitle("🚑 Driver Navigation HUD")
public class DriverView extends VerticalLayout {

    private final DispatchEventBroadcaster broadcaster;
    private Consumer<DispatchAlert> alertListener;

    private final Div topBar;
    private final Div idleContainer;
    private final Div activeContainer;

    private final Span hospitalText;
    private final Span severityText;
    private final Span patientText;
    private final IFrame mapIframe;

    public DriverView(DispatchEventBroadcaster broadcaster) {
        this.broadcaster = broadcaster;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
                .set("background-color", "#0a0a0a")
                .set("color", "#00ff00")
                .set("font-family", "system-ui, -apple-system, monospace")
                .set("position", "relative")
                .set("overflow", "hidden");

        // Top Bar
        topBar = new Div();
        topBar.setText("✅ SECURE LINK ACTIVE - WAITING FOR DISPATCH");
        topBar.getStyle()
                .set("background-color", "#1a1a1a")
                .set("padding", "15px")
                .set("border-bottom", "2px solid #333")
                .set("color", "#00ff00")
                .set("text-align", "center")
                .set("font-weight", "bold")
                .set("font-size", "14px")
                .set("letter-spacing", "1px")
                .set("width", "100%")
                .set("box-sizing", "border-box");

        // Idle View
        idleContainer = new Div();
        idleContainer.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("justify-content-center", "center")
                .set("height", "calc(100vh - 60px)")
                .set("width", "100%")
                .set("padding-top", "15vh")
                .set("box-sizing", "border-box");

        Div pulseCircle = new Div();
        pulseCircle.setText("📡");
        pulseCircle.getStyle()
                .set("font-size", "50px")
                .set("margin-bottom", "20px");

        H1 idleHeader = new H1("PATROLLING SECTOR");
        idleHeader.getStyle()
                .set("color", "#00ff00")
                .set("font-size", "32px")
                .set("font-weight", "900")
                .set("letter-spacing", "3px")
                .set("margin", "0 0 10px 0");

        Paragraph subIdle = new Paragraph("Maintain speed and wait for HQ instructions.");
        subIdle.getStyle()
                .set("color", "#777777")
                .set("font-size", "16px")
                .set("margin", "0");

        idleContainer.add(pulseCircle, idleHeader, subIdle);

        // Active Container (Map + HUD overlays)
        activeContainer = new Div();
        activeContainer.getStyle()
                .set("position", "relative")
                .set("width", "100%")
                .set("height", "100vh")
                .set("display", "none");

        // Map IFrame
        mapIframe = new IFrame();
        mapIframe.getStyle()
                .set("position", "absolute")
                .set("top", "0")
                .set("left", "0")
                .set("width", "100%")
                .set("height", "100%")
                .set("border", "none");

        // Floating Top Card
        Div floatingTopCard = new Div();
        floatingTopCard.getStyle()
                .set("position", "absolute")
                .set("top", "24px")
                .set("left", "50%")
                .set("transform", "translateX(-50%)")
                .set("background-color", "rgba(20, 20, 20, 0.95)")
                .set("padding", "20px")
                .set("border-radius", "15px")
                .set("border", "2px solid #ff4444")
                .set("text-align", "center")
                .set("width", "85%")
                .set("max-width", "600px")
                .set("box-shadow", "0 4px 15px rgba(0,0,0,0.6)")
                .set("z-index", "1000");

        H2 alertHeader = new H2("🚨 EMERGENCY DISPATCH");
        alertHeader.getStyle()
                .set("color", "#ff4444")
                .set("font-size", "22px")
                .set("font-weight", "900")
                .set("letter-spacing", "2px")
                .set("margin", "0 0 8px 0");

        hospitalText = new Span("Metro Trauma Center");
        hospitalText.getStyle()
                .set("color", "#ffffff")
                .set("font-size", "26px")
                .set("font-weight", "bold")
                .set("display", "block")
                .set("margin", "4px 0");

        severityText = new Span("LEVEL 1 CRITICAL");
        severityText.getStyle()
                .set("color", "#ff4444")
                .set("font-size", "16px")
                .set("font-weight", "bold")
                .set("display", "block")
                .set("margin-top", "6px");

        floatingTopCard.add(alertHeader, hospitalText, severityText);

        // Floating Bottom Card
        Div floatingBottomCard = new Div();
        floatingBottomCard.getStyle()
                .set("position", "absolute")
                .set("bottom", "24px")
                .set("left", "50%")
                .set("transform", "translateX(-50%)")
                .set("background-color", "rgba(0, 40, 0, 0.95)")
                .set("padding", "16px")
                .set("border-radius", "15px")
                .set("border", "2px solid #00ff00")
                .set("text-align", "center")
                .set("width", "85%")
                .set("max-width", "600px")
                .set("box-shadow", "0 4px 15px rgba(0,0,0,0.6)")
                .set("z-index", "1000");

        Span etaText = new Span("📍 GPS Rerouted • ETA: 4 Minutes");
        etaText.getStyle()
                .set("color", "#00ff00")
                .set("font-size", "18px")
                .set("font-weight", "bold")
                .set("display", "block")
                .set("margin-bottom", "4px");

        patientText = new Span("Patient ID: P-0000");
        patientText.getStyle()
                .set("color", "#00ccff")
                .set("font-size", "14px")
                .set("font-family", "monospace")
                .set("display", "block");

        floatingBottomCard.add(etaText, patientText);

        activeContainer.add(mapIframe, floatingTopCard, floatingBottomCard);

        add(topBar, idleContainer, activeContainer);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();

        alertListener = alert -> {
            ui.access(() -> updateDispatch(alert));
        };

        broadcaster.register(alertListener);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (alertListener != null) {
            broadcaster.unregister(alertListener);
        }
        super.onDetach(detachEvent);
    }

    private void updateDispatch(DispatchAlert alert) {
        topBar.getStyle().set("display", "none");
        idleContainer.getStyle().set("display", "none");
        activeContainer.getStyle().set("display", "block");

        hospitalText.setText(alert.destination());
        severityText.setText(alert.severity().replace('_', ' '));
        patientText.setText("Patient ID: " + alert.patientId());

        String mapHtml = generateMapHtml(alert.destination());
        mapIframe.getElement().setProperty("srcdoc", mapHtml);
    }

    private String generateMapHtml(String destination) {
        boolean isTrauma = destination != null && destination.contains("Trauma");
        double destLat = isTrauma ? 19.0500 : 18.9700;
        double destLon = isTrauma ? 72.8400 : 72.8200;
        double startLat = 18.9300;
        double startLon = 72.8200;

        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<meta charset='UTF-8'/>"
                + "<link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'/>"
                + "<script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>"
                + "<style>body,html{margin:0;padding:0;height:100%;background:#111;}#map{height:100%;width:100%;}</style>"
                + "</head>"
                + "<body>"
                + "<div id='map'></div>"
                + "<script>"
                + "  var map = L.map('map').setView([" + startLat + ", " + startLon + "], 13);"
                + "  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {"
                + "    attribution: '© OpenStreetMap'"
                + "  }).addTo(map);"
                + "  L.marker([" + startLat + ", " + startLon + "]).addTo(map).bindPopup('🚑 Ambulance Location');"
                + "  L.marker([" + destLat + ", " + destLon + "]).addTo(map).bindPopup('🏥 " + (destination != null ? destination : "Hospital") + "');"
                + "  fetch('https://router.project-osrm.org/route/v1/driving/" + startLon + "," + startLat + ";" + destLon + "," + destLat + "?geometries=geojson')"
                + "    .then(function(res){return res.json();})"
                + "    .then(function(data){"
                + "      if(data && data.routes && data.routes[0]){"
                + "        var route = data.routes[0].geometry;"
                + "        L.geoJSON(route, { style: { color: '#00ccff', weight: 6, opacity: 0.8 } }).addTo(map);"
                + "        map.fitBounds([[" + startLat + "," + startLon + "], [" + destLat + "," + destLon + "]], { padding: [60, 60] });"
                + "      }"
                + "    })"
                + "    .catch(function(err){ console.error('OSRM route error:', err); });"
                + "</script>"
                + "</body>"
                + "</html>";
    }
}
