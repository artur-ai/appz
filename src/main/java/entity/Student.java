package entity;

public class Student {
    private String name;
    private int courseYear;
    private boolean hasComputer;
    private int completedLabs;

    public Student(String name, int courseYear, boolean hasComputer) {
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
}
