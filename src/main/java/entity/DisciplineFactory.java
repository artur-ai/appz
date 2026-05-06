package entity;

import events.EventManager;
import strategy.AutomaticCreditStrategy;
import strategy.ExamStrategy;

public class DisciplineFactory {
    private EventManager eventManager;

    public DisciplineFactory(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public Discipline createDiscipline(String type, int courseYear, int hours) {
        switch (type.toLowerCase()) {
            case "основи програмування":
                if (courseYear != 1) throw new IllegalArgumentException("Основи програмування доступні тільки на 1 курсі.");
                return new Discipline("Основи програмування", hours, new AutomaticCreditStrategy(), eventManager);

            case "алгоритми":
                if (courseYear != 2) throw new IllegalArgumentException("Алгоритми доступні тільки на 2 курсі.");
                return new Discipline("Алгоритми та структури даних", hours, new ExamStrategy(), eventManager);

            case "ооп":
                if (courseYear > 2) throw new IllegalArgumentException("ООП доступне тільки для 1-2 курсів.");
                return new Discipline("Об'єктно-орієнтоване програмування", hours, new ExamStrategy(), eventManager);

            default:
                throw new IllegalArgumentException("Невідома дисципліна.");
        }
    }
}
