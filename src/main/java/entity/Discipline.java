package entity;

import events.EventManager;
import events.ProgressEventArgs;
import strategy.AssessmentStrategy;

import java.util.ArrayList;
import java.util.List;

public class Discipline {
    private String name;
    private int hours;
    private AssessmentStrategy assessmentStrategy;
    private List<Teacher> teachers = new ArrayList<>();
    private EventManager eventManager;

    protected Discipline(String name, int hours, AssessmentStrategy strategy, EventManager eventManager) {
        if (hours < 64) {
            throw new IllegalArgumentException("На дисципліну повинно відводитися не менше 64 годин!");
        }
        this.name = name;
        this.hours = hours;
        this.assessmentStrategy = strategy;
        this.eventManager = eventManager;
    }

    public String getName() {
        return name;
    }

    public void assignTeacher(Teacher teacher) {
        if (!teacher.canTakeDiscipline()) {
            throw new IllegalStateException("Викладач " + teacher.getName() + " вже веде іншу дисципліну!");
        }
        teachers.add(teacher);
        teacher.assignToDiscipline();
    }

    public void startStudyProcess(Group group, int requiredLabs) {
        for (Student student : group.getStudents()) {
            if (!student.hasComputer()) {
                eventManager.notify(new ProgressEventArgs(student, "Не може вивчати " + name + " (немає комп'ютера)."));
                continue;
            }
            assessmentStrategy.evaluate(student, requiredLabs, eventManager);
        }
    }
}
