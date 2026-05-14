package entity;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private static final int MIN_SUBGROUP_SIZE = 10;

    private String groupName;
    private List<Student> students = new ArrayList<>();

    public Group(String groupName) {
        this.groupName = groupName;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public List<Student> getStudents() {
        return students;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getSubgroupCount() {
        int size = students.size();
        if (size < MIN_SUBGROUP_SIZE) return 0;
        return size / MIN_SUBGROUP_SIZE;
    }

    public List<List<Student>> splitIntoSubgroups() {
        if (getSubgroupCount() == 0) {
            throw new IllegalStateException(
                    "Неможливо створити підгрупи: у групі менше " + MIN_SUBGROUP_SIZE + " студентів."
            );
        }

        List<List<Student>> subgroups = new ArrayList<>();
        int subgroupCount = getSubgroupCount();

        for (int i = 0; i < subgroupCount; i++) {
            subgroups.add(new ArrayList<>());
        }

        for (int i = 0; i < students.size(); i++) {
            int idx = Math.min(i / MIN_SUBGROUP_SIZE, subgroupCount - 1);
            subgroups.get(idx).add(students.get(i));
        }

        return subgroups;
    }

    public boolean canFormSubgroups() {
        return getSubgroupCount() > 0;
    }
}
