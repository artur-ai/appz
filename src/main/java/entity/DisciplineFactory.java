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
        switch (type.toLowerCase().trim()) {

            case "основи програмування":
                if (courseYear != 1) {
                    throw new IllegalArgumentException(
                            "Основи програмування доступні тільки для 1 курсу. Поточний курс: " + courseYear
                    );
                }
                return new Discipline(
                        "Основи програмування", hours, 1,
                        new AutomaticCreditStrategy(), eventManager
                );

            case "алгоритми":
                if (courseYear != 2) {
                    throw new IllegalArgumentException(
                            "Алгоритми та структури даних доступні тільки для 2 курсу. Поточний курс: " + courseYear
                    );
                }
                return new Discipline(
                        "Алгоритми та структури даних", hours, 2,
                        new ExamStrategy(), eventManager
                );

            case "ооп":
                if (courseYear < 1 || courseYear > 2) {
                    throw new IllegalArgumentException(
                            "ООП доступне тільки для 1-2 курсів. Поточний курс: " + courseYear
                    );
                }
                return new Discipline(
                        "Об'єктно-орієнтоване програмування", hours, 2,
                        new ExamStrategy(), eventManager
                );

            default:
                throw new IllegalArgumentException(
                        "Невідома дисципліна: '" + type + "'. " +
                                "Доступні: 'основи програмування', 'алгоритми', 'ооп'."
                );
        }
    }
}
