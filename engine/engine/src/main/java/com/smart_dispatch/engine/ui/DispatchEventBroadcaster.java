package com.smart_dispatch.engine.ui;

import com.smart_dispatch.engine.model.DispatchAlert;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Component
public class DispatchEventBroadcaster {
    private final CopyOnWriteArrayList<Consumer<DispatchAlert>> listeners = new CopyOnWriteArrayList<>();

    public void register(Consumer<DispatchAlert> listener) {
        listeners.add(listener);
    }

    public void unregister(Consumer<DispatchAlert> listener) {
        listeners.remove(listener);
    }

    public void broadcast(DispatchAlert alert) {
        for (Consumer<DispatchAlert> listener : listeners) {
            try {
                listener.accept(alert);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
