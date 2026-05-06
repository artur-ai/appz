package strategy;

import entity.Student;
import events.EventManager;

public interface AssessmentStrategy {
    boolean evaluate(Student student, int requiredLabs, EventManager eventManager);
}
