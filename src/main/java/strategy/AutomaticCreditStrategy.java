package strategy;

import entity.Student;
import events.EventManager;
import events.ProgressEventArgs;

public class AutomaticCreditStrategy implements AssessmentStrategy {
    @Override
    public boolean evaluate(Student student, int requiredLabs, EventManager eventManager) {
        if (student.getCompletedLabs() < requiredLabs) {
            eventManager.notify(new ProgressEventArgs(student,
                    "НЕ отримав залік автоматом — борги по лабораторних " +
                            "(" + student.getCompletedLabs() + "/" + requiredLabs + " здано)."));
            return false;
        }
        eventManager.notify(new ProgressEventArgs(student,
                "Отримав залік автоматом ✓ " +
                        "(" + student.getCompletedLabs() + "/" + requiredLabs + " лаб здано)."));
        return true;
    }
}
