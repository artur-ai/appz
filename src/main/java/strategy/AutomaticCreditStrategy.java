package strategy;

import entity.Student;
import events.EventManager;
import events.ProgressEventArgs;

public class AutomaticCreditStrategy implements AssessmentStrategy {
    @Override
    public boolean evaluate(Student student, int requiredLabs, EventManager eventManager) {
        eventManager.notify(new ProgressEventArgs(student, "Отримав залік автоматом."));
        return true;
    }
}
