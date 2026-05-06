package events;

import entity.Student;

public class ProgressEventArgs {
    private final Student student;
    private final String message;

    public ProgressEventArgs(Student student, String message) {
        this.student = student;
        this.message = message;
    }

    public Student getStudent() { return student; }
    public String getMessage() { return message; }
}
