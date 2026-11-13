package kz.dodo.exception;

/** Бос тапсырыс асханаға жіберілгенде лақтырылады. */
public class EmptyOrderException extends Exception {
    public EmptyOrderException(String message) {
        super(message);
    }
}
