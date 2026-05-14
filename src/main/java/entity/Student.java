package entity;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String name;
    private int courseYear;
    private boolean hasComputer;
    private int completedLabs;
    private final List<String> completedDisciplines = new ArrayList<>();

    public Student(String name, int courseYear, boolean hasComputer) {
        if (courseYear < 1 || courseYear > 6) {
            throw new IllegalArgumentException("Курс повинен бути від 1 до 6.");
        }
        this.name = name;
        this.courseYear = courseYear;
        this.hasComputer = hasComputer;
        this.completedLabs = 0;
    }

    public String getName() { return name; }
    public int getCourseYear() { return courseYear; }
    public boolean hasComputer() { return hasComputer; }
    public int getCompletedLabs() { return completedLabs; }

    public void completeLab() {
        this.completedLabs++;
    }

    public boolean hasCompletedDiscipline(String disciplineName) {
        return completedDisciplines.contains(disciplineName);
    }

    public void addCompletedDiscipline(String disciplineName) {
        completedDisciplines.add(disciplineName);
    }

    public List<String> getCompletedDisciplines() {
        return completedDisciplines;
    }
}
