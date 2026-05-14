import entity.*;
import events.EventManager;
import events.ProgressEventArgs;
import events.ProgressObserver;
import strategy.AutomaticCreditStrategy;
import strategy.ExamStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI implements ProgressObserver {
    private final Scanner scanner;
    private final DisciplineFactory factory;
    private final EventManager eventManager;

    private Group currentGroup;
    private final List<Teacher> teachers;
    private final List<Discipline> disciplines;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.eventManager = new EventManager();
        this.eventManager.subscribe(this);
        this.factory = new DisciplineFactory(this.eventManager);
        this.teachers = new ArrayList<>();
        this.disciplines = new ArrayList<>();
    }

    @Override
    public void onProgressUpdated(ProgressEventArgs args) {
        System.out.println("🎓 [ПОДІЯ] Студент: " + args.getStudent().getName() +
                " | " + args.getMessage());
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== 🛠  МЕНЮ УПРАВЛІННЯ НАВЧАЛЬНИМ ПРОЦЕСОМ ===");
            System.out.println("1.  Створити групу");
            System.out.println("2.  Додати студента до групи");
            System.out.println("3.  Створити викладача");
            System.out.println("4.  Створити дисципліну (Фабрика)");
            System.out.println("5.  Призначити викладача на дисципліну");
            System.out.println("6.  Звільнити викладача з дисципліни");
            System.out.println("7.  Змінити стратегію оцінювання дисципліни");
            System.out.println("8.  Симулювати здачу лабораторної роботи");
            System.out.println("9.  Запустити процес оцінювання");
            System.out.println("10. Інформація про групу (підгрупи)");
            System.out.println("0.  Вихід");
            System.out.print("👉 Оберіть дію: ");

            String choice = scanner.nextLine().trim();
            System.out.println("------------------------------------------------");

            try {
                switch (choice) {
                    case "1":  createGroup(); break;
                    case "2":  addStudent(); break;
                    case "3":  createTeacher(); break;
                    case "4":  createDiscipline(); break;
                    case "5":  assignTeacher(); break;
                    case "6":  removeTeacher(); break;
                    case "7":  changeStrategy(); break;
                    case "8":  simulateLabCompletion(); break;
                    case "9":  startAssessment(); break;
                    case "10": showGroupInfo(); break;
                    case "0":  running = false; System.out.println("👋 Вихід з програми..."); break;
                    default:   System.out.println("⚠ Невідома команда. Спробуйте ще раз.");
                }
            } catch (Exception e) {
                System.err.println("❌ ПОМИЛКА: " + e.getMessage());
            }
        }
    }

    private void createGroup() {
        System.out.print("Введіть назву групи (напр. Б-121-24): ");
        String name = scanner.nextLine();
        currentGroup = new Group(name);
        System.out.println("✅ Група '" + name + "' успішно створена!");
    }

    private void addStudent() {
        requireGroup();
        System.out.print("Ім'я студента: ");
        String name = scanner.nextLine();
        System.out.print("Курс (1-5): ");
        int course = Integer.parseInt(scanner.nextLine());
        System.out.print("Чи є комп'ютер/ноутбук? (так/ні): ");
        boolean hasPc = scanner.nextLine().trim().equalsIgnoreCase("так");

        Student student = new Student(name, course, hasPc);
        currentGroup.addStudent(student);
        System.out.println("✅ Студент " + name + " (" + course + " курс) доданий до групи '" +
                currentGroup.getGroupName() + "'.");
    }

    private void createTeacher() {
        System.out.print("Ім'я викладача: ");
        String name = scanner.nextLine();
        teachers.add(new Teacher(name));
        System.out.println("✅ Викладач " + name + " успішно створений!");
    }

    private void createDiscipline() {
        System.out.println("Доступні дисципліни: 'основи програмування', 'алгоритми', 'ооп'");
        System.out.print("Введіть назву: ");
        String name = scanner.nextLine();
        System.out.print("Для якого курсу групи (1-4): ");
        int course = Integer.parseInt(scanner.nextLine());
        System.out.print("Кількість аудиторних годин (мін. 64): ");
        int hours = Integer.parseInt(scanner.nextLine());

        Discipline discipline = factory.createDiscipline(name, course, hours);
        disciplines.add(discipline);
        System.out.println("✅ Дисципліна '" + discipline.getName() + "' успішно створена!");
    }

    private void assignTeacher() {
        requireGroup();
        requireDisciplinesAndTeachers();

        Discipline discipline = selectDiscipline();
        Teacher teacher = selectTeacher();

        discipline.assignTeacher(teacher, currentGroup);
    }

    private void removeTeacher() {
        requireDisciplinesAndTeachers();

        Discipline discipline = selectDiscipline();

        List<Teacher> disciplineTeachers = discipline.getTeachers();
        if (disciplineTeachers.isEmpty()) {
            System.out.println("⚠ На цій дисципліні немає призначених викладачів.");
            return;
        }

        System.out.println("Викладачі дисципліни '" + discipline.getName() + "':");
        for (int i = 0; i < disciplineTeachers.size(); i++) {
            System.out.println((i + 1) + ". " + disciplineTeachers.get(i).getName());
        }
        System.out.print("Оберіть номер викладача для звільнення: ");
        int tIndex = Integer.parseInt(scanner.nextLine()) - 1;

        discipline.removeTeacher(disciplineTeachers.get(tIndex));
    }

    private void changeStrategy() {
        if (disciplines.isEmpty()) throw new IllegalStateException("Немає створених дисциплін!");

        Discipline discipline = selectDiscipline();

        System.out.println("Оберіть нову стратегію оцінювання:");
        System.out.println("1. Залік автоматом (AutomaticCredit)");
        System.out.println("2. Екзамен (Exam)");
        System.out.print("👉 ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                discipline.setAssessmentStrategy(new AutomaticCreditStrategy());
                break;
            case "2":
                discipline.setAssessmentStrategy(new ExamStrategy());
                break;
            default:
                System.out.println("⚠ Невідомий вибір.");
        }
    }

    private void simulateLabCompletion() {
        requireGroup();
        if (currentGroup.getStudents().isEmpty()) {
            throw new IllegalStateException("У групі немає студентів!");
        }

        List<Student> students = currentGroup.getStudents();
        System.out.println("Студенти у групі '" + currentGroup.getGroupName() + "':");
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            System.out.println((i + 1) + ". " + s.getName() +
                    " [" + s.getCourseYear() + " курс]" +
                    " | Здано лаб: " + s.getCompletedLabs());
        }
        System.out.print("Оберіть номер студента, який здав лабу: ");
        int sIndex = Integer.parseInt(scanner.nextLine()) - 1;

        students.get(sIndex).completeLab();
        System.out.println("✅ Лабораторну зараховано. Тепер здано лаб: " +
                students.get(sIndex).getCompletedLabs());
    }

    private void startAssessment() {
        requireGroup();
        if (disciplines.isEmpty()) throw new IllegalStateException("Немає створених дисциплін!");

        Discipline discipline = selectDiscipline();

        System.out.print("Мінімальна кількість зданих лаб для допуску: ");
        int requiredLabs = Integer.parseInt(scanner.nextLine());

        System.out.println("\n--- 🚀 ПОЧАТОК ОЦІНЮВАННЯ: " + discipline.getName() + " ---");
        discipline.startStudyProcess(currentGroup, requiredLabs);
        System.out.println("--- 🏁 ОЦІНЮВАННЯ ЗАВЕРШЕНО ---\n");
    }

    private void showGroupInfo() {
        requireGroup();
        List<Student> students = currentGroup.getStudents();

        System.out.println("\n📊 Група: " + currentGroup.getGroupName());
        System.out.println("   Студентів: " + students.size());
        System.out.println("   Підгруп (для лаб): " + currentGroup.getSubgroupCount());

        if (students.isEmpty()) {
            System.out.println("   (порожня група)");
            return;
        }

        System.out.println("\n   Список студентів:");
        for (Student s : students) {
            String disciplines = s.getCompletedDisciplines().isEmpty()
                    ? "немає"
                    : String.join(", ", s.getCompletedDisciplines());
            System.out.println("   - " + s.getName() +
                    " | Курс: " + s.getCourseYear() +
                    " | ПК: " + (s.hasComputer() ? "є" : "немає") +
                    " | Лаб здано: " + s.getCompletedLabs() +
                    " | Пройдені дисципліни: " + disciplines);
        }
    }


    private void requireGroup() {
        if (currentGroup == null) {
            throw new IllegalStateException("Спочатку створіть групу (Пункт 1)!");
        }
    }

    private void requireDisciplinesAndTeachers() {
        if (disciplines.isEmpty() || teachers.isEmpty()) {
            throw new IllegalStateException("Спочатку створіть хоча б одну дисципліну та одного викладача!");
        }
    }

    private Discipline selectDiscipline() {
        System.out.println("Список дисциплін:");
        for (int i = 0; i < disciplines.size(); i++) {
            System.out.println((i + 1) + ". " + disciplines.get(i).getName());
        }
        System.out.print("Оберіть номер дисципліни: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        return disciplines.get(index);
    }

    private Teacher selectTeacher() {
        System.out.println("Список викладачів:");
        for (int i = 0; i < teachers.size(); i++) {
            Teacher t = teachers.get(i);
            System.out.println((i + 1) + ". " + t.getName() +
                    (t.canTakeDiscipline() ? "" : " [вже зайнятий]"));
        }
        System.out.print("Оберіть номер викладача: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        return teachers.get(index);
    }

    public static void main(String[] args) {
        ConsoleUI app = new ConsoleUI();
        app.start();
    }
}
