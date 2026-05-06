package entity;

public class Teacher {
    private String name;
    private boolean isCurrentlyTeaching;

    public Teacher(String name) {
        this.name = name;
        this.isCurrentlyTeaching = false;
    }

    public String getName() {
        return name;
    }

    public boolean canTakeDiscipline() {
        return !isCurrentlyTeaching;
    }

    public void assignToDiscipline() {
        this.isCurrentlyTeaching = true;
    }

    public void releaseFromDiscipline() {
        this.isCurrentlyTeaching = false;
    }
}
