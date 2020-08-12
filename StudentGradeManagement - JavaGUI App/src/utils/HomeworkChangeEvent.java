package utils;

import domain.Tema;

public class HomeworkChangeEvent {

    private ChangeEventType type;
    private Tema data, oldData;

    public HomeworkChangeEvent(ChangeEventType type, Tema data) {
        this.type = type;
        this.data = data;
    }

    public HomeworkChangeEvent(ChangeEventType type, Tema data, Tema oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

}
