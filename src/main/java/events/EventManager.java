package events;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private final List<ProgressObserver> listeners = new ArrayList<>();

    public void subscribe(ProgressObserver observer) {
        if (!listeners.contains(observer)) {
            listeners.add(observer);
        }
    }

    public void unsubscribe(ProgressObserver observer) {
        listeners.remove(observer);
    }

    public void notify(ProgressEventArgs args) {
        for (ProgressObserver observer : new ArrayList<>(listeners)) {
            observer.onProgressUpdated(args);
        }
    }
}
