package com.smart_dispatch.driver.service;

import com.smart_dispatch.driver.model.DispatchAlert;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DriverStompService {

    private final String engineWsUrl;
    private final CopyOnWriteArrayList<Consumer<DispatchAlert>> alertListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<String>> statusListeners = new CopyOnWriteArrayList<>();

    private WebSocketStompClient stompClient;
    private StompSession currentSession;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean connected = false;

    public DriverStompService(@Value("${engine.ws.url:http://localhost:8080/smart-city-ws}") String engineWsUrl) {
        this.engineWsUrl = engineWsUrl;
    }

    @PostConstruct
    public void init() {
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());
        SockJsClient sockJsClient = new SockJsClient(transports);

        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new StringMessageConverter());

        connect();
    }

    public synchronized void connect() {
        if (connected && currentSession != null && currentSession.isConnected()) {
            return;
        }

        notifyStatus("Establishing secure link to HQ...");
        try {
            stompClient.connectAsync(engineWsUrl, new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    currentSession = session;
                    connected = true;
                    notifyStatus("✅ SECURE LINK ACTIVE - WAITING FOR DISPATCH");
                    System.out.println("✅ [DRIVER STOMP] Connected to Engine WebSocket at " + engineWsUrl);

                    session.subscribe("/topic/alerts", new StompFrameHandler() {
                        @Override
                        public Type getPayloadType(StompHeaders headers) {
                            return String.class;
                        }

                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                            try {
                                String json = (String) payload;
                                System.out.println("🚨 [DRIVER STOMP] Incoming dispatch alert: " + json);

                                String patientId = extractField(json, "patientId");
                                String severity = extractField(json, "severity");
                                String destination = extractField(json, "destination");
                                String status = extractField(json, "status");

                                DispatchAlert alert = new DispatchAlert(
                                        patientId.isBlank() ? "P-UNKNOWN" : patientId,
                                        severity.isBlank() ? "LEVEL_1_CRITICAL" : severity,
                                        destination.isBlank() ? "Hospital" : destination,
                                        status.isBlank() ? "DISPATCHED" : status
                                );
                                notifyAlert(alert);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    connected = false;
                    currentSession = null;
                    notifyStatus("❌ HQ CONNECTION LOST - RETRYING...");
                    System.err.println("⚠️ [DRIVER STOMP] Transport error: " + exception.getMessage());
                    scheduler.schedule(DriverStompService.this::connect, 5, TimeUnit.SECONDS);
                }
            });
        } catch (Exception e) {
            connected = false;
            notifyStatus("❌ HQ CONNECTION LOST - RETRYING...");
            scheduler.schedule(this::connect, 5, TimeUnit.SECONDS);
        }
    }

    private String extractField(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    public void addAlertListener(Consumer<DispatchAlert> listener) {
        alertListeners.add(listener);
    }

    public void removeAlertListener(Consumer<DispatchAlert> listener) {
        alertListeners.remove(listener);
    }

    public void addStatusListener(Consumer<String> listener) {
        statusListeners.add(listener);
        if (connected) {
            listener.accept("✅ SECURE LINK ACTIVE - WAITING FOR DISPATCH");
        } else {
            listener.accept("Establishing secure link to HQ...");
        }
    }

    public void removeStatusListener(Consumer<String> listener) {
        statusListeners.remove(listener);
    }

    private void notifyAlert(DispatchAlert alert) {
        for (Consumer<DispatchAlert> l : alertListeners) {
            try {
                l.accept(alert);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyStatus(String status) {
        for (Consumer<String> l : statusListeners) {
            try {
                l.accept(status);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @PreDestroy
    public void cleanup() {
        scheduler.shutdown();
        if (currentSession != null && currentSession.isConnected()) {
            currentSession.disconnect();
        }
    }
}
