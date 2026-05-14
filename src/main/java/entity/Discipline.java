package entity;

import events.EventManager;
import events.ProgressEventArgs;
import strategy.AssessmentStrategy;

import java.util.ArrayList;
import java.util.List;

public class Discipline {
    private static final int MIN_HOURS = 64;

    private String name;
    private int hours;
    private int maxCourseYear;
    private AssessmentStrategy assessmentStrategy;
    private List<Teacher> teachers = new ArrayList<>();
    private EventManager eventManager;

    protected Discipline(String name, int hours, int maxCourseYear,
                         AssessmentStrategy strategy, EventManager eventManager) {
        if (hours < MIN_HOURS) {
            throw new IllegalArgumentException(
                    "На дисципліну повинно відводитися не менше " + MIN_HOURS + " годин!"
            );
        }
        this.name = name;
        this.hours = hours;
        this.maxCourseYear = maxCourseYear;
        this.assessmentStrategy = strategy;
        this.eventManager = eventManager;
    }

    public String getName() {
        return name;
    }

    public int getHours() {
        return hours;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    private int getMaxTeachers(Group group) {
        int subgroups = group.getSubgroupCount();
        return subgroups + 1; // підгрупи + лектор
    }

    public void assignTeacher(Teacher teacher, Group group) {
        int maxTeachers = getMaxTeachers(group);
        if (teachers.size() >= maxTeachers) {
            throw new IllegalStateException(
                    "Не можна призначити більше " + maxTeachers +
                            " викладач(ів) для групи '" + group.getGroupName() +
                            "' (підгруп: " + group.getSubgroupCount() + " + 1 лектор)."
            );
        }
        if (!teacher.canTakeDiscipline()) {
            throw new IllegalStateException(
                    "Викладач " + teacher.getName() + " вже веде іншу дисципліну!"
            );
        }
        teachers.add(teacher);
        teacher.assignToDiscipline();
        System.out.println("✅ Викладач " + teacher.getName() +
                " призначений на дисципліну '" + name + "'.");
    }

    public void setAssessmentStrategy(AssessmentStrategy strategy) {
        this.assessmentStrategy = strategy;
        System.out.println("🔄 Стратегія оцінювання для '" + name + "' змінена.");
    }

    public void removeTeacher(Teacher teacher) {
        if (teachers.remove(teacher)) {
            teacher.releaseFromDiscipline();
            System.out.println("🔄 Викладач " + teacher.getName() +
                    " звільнений з дисципліни '" + name + "'.");
        } else {
            System.out.println("⚠ Викладач " + teacher.getName() +
                    " не веде дисципліну '" + name + "'.");
        }
    }

    public void startStudyProcess(Group group, int requiredLabs) {
        if (teachers.isEmpty()) {
            throw new IllegalStateException(
                    "Дисципліна '" + name + "' не має жодного призначеного викладача!"
            );
        }

        System.out.println("\n📋 Викладачі дисципліни '" + name + "':");
        teachers.forEach(t -> System.out.println("   - " + t.getName()));

        if (group.canFormSubgroups()) {
            List<List<Student>> subgroups = group.splitIntoSubgroups();
            System.out.println("👥 Лабораторні розбиті на " + subgroups.size() + " підгруп(и):");
            for (int i = 0; i < subgroups.size(); i++) {
                System.out.println("   Підгрупа " + (i + 1) + ": " + subgroups.get(i).size() + " студент(ів)");
            }
        } else {
            System.out.println("⚠ Підгрупи не формуються (менше 10 студентів у групі).");
        }

        System.out.println();

        for (Student student : group.getStudents()) {
            if (!student.hasComputer()) {
                eventManager.notify(new ProgressEventArgs(student,
                        "Не може вивчати '" + name + "' — немає комп'ютера."));
                continue;
            }

            if (student.hasCompletedDiscipline(name)) {
                eventManager.notify(new ProgressEventArgs(student,
                        "Вже вивчав дисципліну '" + name + "' раніше — пропускається."));
                continue;
            }

            if (student.getCourseYear() > maxCourseYear) {
                eventManager.notify(new ProgressEventArgs(student,
                        "Студент " + student.getCourseYear() + "-го курсу не може вивчати '" +
                                name + "' (дозволено до " + maxCourseYear + " курсу)."));
                continue;
            }
            boolean passed = assessmentStrategy.evaluate(student, requiredLabs, eventManager);
            if (passed) {
                student.addCompletedDiscipline(name);
            }
        }
    }
}
