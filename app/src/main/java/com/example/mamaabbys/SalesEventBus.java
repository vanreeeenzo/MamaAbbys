package com.example.mamaabbys;

import java.util.ArrayList;
import java.util.List;

public class SalesEventBus {
    private static SalesEventBus instance;
    private List<OnSaleListener> listeners = new ArrayList<>();

    public interface OnSaleListener {
        void onSaleMade();
    }

    private SalesEventBus() {}

    public static synchronized SalesEventBus getInstance() {
        if (instance == null) {
            instance = new SalesEventBus();
        }
        return instance;
    }

    public void registerListener(OnSaleListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unregisterListener(OnSaleListener listener) {
        listeners.remove(listener);
    }

    public void notifySaleMade() {
        for (OnSaleListener listener : listeners) {
            listener.onSaleMade();
        }
    }
} 