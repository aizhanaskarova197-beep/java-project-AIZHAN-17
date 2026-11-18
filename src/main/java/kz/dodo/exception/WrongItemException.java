package kz.dodo.exception;

/** Қаптамадағы позициялар тапсырыс құрамына сәйкес келмегенде лақтырылады (мысалы, Пепперони орнына Гавай). */
public class WrongItemException extends Exception {
    public WrongItemException(String message) {
        super(message);
    }
}
