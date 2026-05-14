package strategy;

import entity.Student;
import events.EventManager;
import events.ProgressEventArgs;

public class ExamStrategy implements AssessmentStrategy {

    @Override
    public boolean evaluate(Student student, int requiredLabs, EventManager eventManager) {
        if (student.getCompletedLabs() < requiredLabs) {
            eventManager.notify(new ProgressEventArgs(student,
                    "НЕ допущений до екзамену — борги по лабораторних " +
                            "(" + student.getCompletedLabs() + "/" + requiredLabs + " здано)."));
            return false;
        }
        eventManager.notify(new ProgressEventArgs(student,
                "Допущений до екзамену та успішно склав ✓ " +
                        "(" + student.getCompletedLabs() + "/" + requiredLabs + " лаб здано)."));
        return true;
    }
}
