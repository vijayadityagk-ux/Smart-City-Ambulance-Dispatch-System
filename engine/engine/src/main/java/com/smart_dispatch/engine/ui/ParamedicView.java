package com.smart_dispatch.engine.ui;

import com.smart_dispatch.engine.controller.DispatchController;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Route("paramedic")
@PageTitle("🚑 Paramedic Dispatch")
public class ParamedicView extends VerticalLayout {

    public record InjuryCategory(String id, String label, String severity) {}

    private final List<InjuryCategory> injuryCategories = List.of(
            new InjuryCategory("TRAUMA", "💥 Severe Crash / Trauma", "LEVEL_1_CRITICAL"),
            new InjuryCategory("BURN", "🔥 Severe Burns", "LEVEL_1_CRITICAL"),
            new InjuryCategory("CARDIAC", "❤️ Cardiac Arrest", "LEVEL_2_EMERGENT"),
            new InjuryCategory("FRACTURE", "🦴 Standard Fracture", "LEVEL_4_URGENT")
    );

    private InjuryCategory selectedInjury = null;
    private final List<Div> cardElements = new ArrayList<>();
    private final Button dispatchBtn;

    public ParamedicView(DispatchController dispatchController) {
        setSpacing(true);
        setPadding(true);
        setAlignItems(FlexComponent.Alignment.CENTER);
        setSizeFull();
        getStyle()
                .set("background-color", "#121212")
                .set("color", "#ffffff")
                .set("font-family", "system-ui, -apple-system, sans-serif")
                .set("min-height", "100vh")
                .set("overflow-y", "auto");

        Div container = new Div();
        container.getStyle()
                .set("width", "100%")
                .set("max-width", "540px")
                .set("margin", "20px auto")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "16px");

        H1 header = new H1("🚑 Paramedic Dispatch");
        header.getStyle()
                .set("color", "#ff4444")
                .set("text-align", "center")
                .set("font-size", "2rem")
                .set("font-weight", "800")
                .set("margin", "10px 0 4px 0");

        Paragraph subHeader = new Paragraph("Select Primary Trauma:");
        subHeader.getStyle()
                .set("color", "#aaaaaa")
                .set("text-align", "center")
                .set("font-size", "1.1rem")
                .set("margin-bottom", "12px");

        container.add(header, subHeader);

        // Injury cards
        for (InjuryCategory injury : injuryCategories) {
            Div card = new Div();
            card.setText(injury.label());
            card.getStyle()
                    .set("background-color", "#2a2a2a")
                    .set("color", "#ffffff")
                    .set("padding", "20px")
                    .set("border-radius", "12px")
                    .set("border", "2px solid transparent")
                    .set("font-size", "1.15rem")
                    .set("font-weight", "600")
                    .set("text-align", "center")
                    .set("cursor", "pointer")
                    .set("transition", "all 0.2s ease");

            card.addClickListener(e -> {
                selectedInjury = injury;
                updateCardSelections();
            });

            cardElements.add(card);
            container.add(card);
        }

        // Dispatch button
        dispatchBtn = new Button("🚨 FIND HOSPITAL");
        dispatchBtn.getStyle()
                .set("background-color", "#555555")
                .set("color", "#ffffff")
                .set("padding", "26px")
                .set("border-radius", "12px")
                .set("font-size", "1.3rem")
                .set("font-weight", "bold")
                .set("margin-top", "12px")
                .set("cursor", "not-allowed")
                .set("border", "none")
                .set("transition", "background-color 0.2s ease");

        dispatchBtn.addClickListener(e -> {
            if (selectedInjury == null) {
                Notification.show("Please select an injury type first!", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            dispatchBtn.setEnabled(false);
            dispatchBtn.setText("⏳ DISPATCHING...");

            Map<String, String> payload = Map.of(
                    "description", selectedInjury.label(),
                    "location", "Highway-Junction",
                    "severity", selectedInjury.severity(),
                    "injury", selectedInjury.label()
            );

            try {
                ResponseEntity<String> response = dispatchController.reportEmergency(payload);
                if (response.getStatusCode().is2xxSuccessful()) {
                    showModal("✅ Match Found", response.getBody() + "\n\nTransmitting route to Driver...", "#00ff00");
                } else {
                    showModal("❌ Dispatch Failed", "No eligible hospitals available.", "#ff4444");
                }
            } catch (Exception ex) {
                showModal("❌ Network Error", "Could not dispatch: " + ex.getMessage(), "#ff4444");
            } finally {
                selectedInjury = null;
                updateCardSelections();
                dispatchBtn.setEnabled(true);
                dispatchBtn.setText("🚨 FIND HOSPITAL");
            }
        });

        container.add(dispatchBtn);
        add(container);
    }

    private void updateCardSelections() {
        for (int i = 0; i < injuryCategories.size(); i++) {
            InjuryCategory cat = injuryCategories.get(i);
            Div card = cardElements.get(i);
            if (selectedInjury != null && selectedInjury.id().equals(cat.id())) {
                card.getStyle()
                        .set("border-color", "#ff4444")
                        .set("background-color", "#3a1c1c");
            } else {
                card.getStyle()
                        .set("border-color", "transparent")
                        .set("background-color", "#2a2a2a");
            }
        }

        if (selectedInjury != null) {
            dispatchBtn.getStyle()
                    .set("background-color", "#ff4444")
                    .set("cursor", "pointer");
        } else {
            dispatchBtn.getStyle()
                    .set("background-color", "#555555")
                    .set("cursor", "not-allowed");
        }
    }

    private void showModal(String title, String message, String accentColor) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(true);

        Div content = new Div();
        content.getStyle()
                .set("background-color", "#1e1e1e")
                .set("color", "#ffffff")
                .set("padding", "24px")
                .set("border-radius", "10px")
                .set("border", "2px solid " + accentColor)
                .set("text-align", "center")
                .set("max-width", "400px");

        H1 h = new H1(title);
        h.getStyle()
                .set("color", accentColor)
                .set("font-size", "1.5rem")
                .set("margin-top", "0");

        Paragraph p = new Paragraph(message);
        p.getStyle()
                .set("color", "#dddddd")
                .set("white-space", "pre-line")
                .set("font-size", "1rem")
                .set("line-height", "1.5");

        Button okBtn = new Button("OK", evt -> dialog.close());
        okBtn.getStyle()
                .set("background-color", accentColor)
                .set("color", "#000000")
                .set("font-weight", "bold")
                .set("padding", "10px 24px")
                .set("border-radius", "8px")
                .set("cursor", "pointer")
                .set("margin-top", "15px");

        content.add(h, p, okBtn);
        dialog.add(content);
        dialog.open();
    }
}
