package entity;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String groupName;
    private List<Student> students = new ArrayList<>();

    public Group(String groupName) {
        this.groupName = groupName;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public List<Student> getStudents() { return students; }

    public boolean canFormSubgroups() {
        return students.size() >= 20;
    }

    public String getGroupName() {
        return groupName;
    }
}
