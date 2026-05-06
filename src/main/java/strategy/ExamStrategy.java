package strategy;

import entity.Student;
import events.EventManager;
import events.ProgressEventArgs;

public class ExamStrategy implements AssessmentStrategy {
    @Override
    public boolean evaluate(Student student, int requiredLabs, EventManager eventManager) {
        if (student.getCompletedLabs() >= requiredLabs) {
            eventManager.notify(new ProgressEventArgs(student, "Допущений до екзамену та успішно його склав."));
            return true;
        } else {
            eventManager.notify(new ProgressEventArgs(student, "НЕ допущений до екзамену (борги по лабах)."));
            return false;
        }
    }
}
