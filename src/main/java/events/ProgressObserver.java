package events;

public interface ProgressObserver {
    void onProgressUpdated(ProgressEventArgs args);
}
