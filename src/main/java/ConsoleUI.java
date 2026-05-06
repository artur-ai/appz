import entity.*;
import events.EventManager;
import events.ProgressEventArgs;
import events.ProgressObserver;

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
        System.out.println("🎓 [ПОДІЯ] Студент: " + args.getStudent().getName() + " | Статус: " + args.getMessage());
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== 🛠 МЕНЮ УПРАВЛІННЯ НАВЧАЛЬНИМ ПРОЦЕСОМ ===");
            System.out.println("1. Створити групу");
            System.out.println("2. Створити студента (додати в групу)");
            System.out.println("3. Створити викладача");
            System.out.println("4. Створити дисципліну через Фабрику");
            System.out.println("5. Призначити викладача на дисципліну");
            System.out.println("6. Симулювати здачу лаб. роботи студентом");
            System.out.println("7. Запустити процес оцінювання (Екзамен/Залік)");
            System.out.println("0. Вихід");
            System.out.print("👉 Оберіть дію: ");

            String choice = scanner.nextLine();
            System.out.println("------------------------------------------------");

            try {
                switch (choice) {
                    case "1": createGroup(); break;
                    case "2": addStudent(); break;
                    case "3": createTeacher(); break;
                    case "4": createDiscipline(); break;
                    case "5": assignTeacher(); break;
                    case "6": simulateLabCompletion(); break;
                    case "7": startAssessment(); break;
                    case "0": running = false; System.out.println("👋 Вихід з програми..."); break;
                    default: System.out.println("⚠ Невідома команда. Спробуйте ще раз.");
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
        System.out.println("✅ Група " + name + " успішно створена!");
    }

    private void addStudent() {
        if (currentGroup == null) throw new IllegalStateException("Спочатку створіть групу (Пункт 1)!");

        System.out.print("Ім'я студента: ");
        String name = scanner.nextLine();
        System.out.print("Курс (1-4): ");
        int course = Integer.parseInt(scanner.nextLine());
        System.out.print("Чи є комп'ютер/ноутбук? (так/ні): ");
        boolean hasPc = scanner.nextLine().trim().equalsIgnoreCase("так");

        Student student = new Student(name, course, hasPc);
        currentGroup.addStudent(student);
        System.out.println("✅ Студент " + name + " доданий до групи " + currentGroup.getGroupName() + ".");
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
        System.out.print("Для якого курсу створюється: ");
        int course = Integer.parseInt(scanner.nextLine());
        System.out.print("Кількість аудиторних годин (мін. 64): ");
        int hours = Integer.parseInt(scanner.nextLine());

        Discipline discipline = factory.createDiscipline(name, course, hours);
        disciplines.add(discipline);
        System.out.println("✅ Дисципліна '" + discipline.getName() + "' успішно створена!");
    }

    private void assignTeacher() {
        if (disciplines.isEmpty() || teachers.isEmpty()) {
            throw new IllegalStateException("Спочатку створіть хоча б одну дисципліну та викладача!");
        }

        System.out.println("Список дисциплін:");
        for (int i = 0; i < disciplines.size(); i++) {
            System.out.println((i + 1) + ". " + disciplines.get(i).getName());
        }
        System.out.print("Оберіть номер дисципліни: ");
        int dIndex = Integer.parseInt(scanner.nextLine()) - 1;

        System.out.println("Список викладачів:");
        for (int i = 0; i < teachers.size(); i++) {
            System.out.println((i + 1) + ". " + teachers.get(i).getName());
        }
        System.out.print("Оберіть номер викладача: ");
        int tIndex = Integer.parseInt(scanner.nextLine()) - 1;

        disciplines.get(dIndex).assignTeacher(teachers.get(tIndex));
        System.out.println("✅ Викладача призначено!");
    }

    private void simulateLabCompletion() {
        if (currentGroup == null || currentGroup.getStudents().isEmpty()) {
            throw new IllegalStateException("В групі немає студентів!");
        }

        System.out.println("Студенти в групі:");
        List<Student> students = currentGroup.getStudents();
        for (int i = 0; i < students.size(); i++) {
            System.out.println((i + 1) + ". " + students.get(i).getName() + " (Здано лаб: " + students.get(i).getCompletedLabs() + ")");
        }
        System.out.print("Оберіть номер студента, який здав лабу: ");
        int sIndex = Integer.parseInt(scanner.nextLine()) - 1;

        students.get(sIndex).completeLab();
        System.out.println("✅ Лабораторну зараховано. Тепер здано лаб: " + students.get(sIndex).getCompletedLabs());
    }

    private void startAssessment() {
        if (currentGroup == null) throw new IllegalStateException("Немає групи для оцінювання!");
        if (disciplines.isEmpty()) throw new IllegalStateException("Немає створених дисциплін!");

        System.out.println("Список дисциплін для оцінювання:");
        for (int i = 0; i < disciplines.size(); i++) {
            System.out.println((i + 1) + ". " + disciplines.get(i).getName());
        }
        System.out.print("Оберіть номер дисципліни: ");
        int dIndex = Integer.parseInt(scanner.nextLine()) - 1;

        System.out.print("Введіть мінімальну кількість зданих лаб для допуску: ");
        int requiredLabs = Integer.parseInt(scanner.nextLine());

        System.out.println("\n--- 🚀 ПОЧАТОК ОЦІНЮВАННЯ ---");
        disciplines.get(dIndex).startStudyProcess(currentGroup, requiredLabs);
        System.out.println("--- 🏁 ОЦІНЮВАННЯ ЗАВЕРШЕНО ---\n");
    }

    public static void main(String[] args) {
        ConsoleUI app = new ConsoleUI();
        app.start();
    }
}