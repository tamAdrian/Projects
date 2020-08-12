package utils;

import domain.Nota;

public class GradeChangeEvent extends AbstractChangeEvent<Nota> {

    public GradeChangeEvent(ChangeEventType changeEventType, Nota data) {
        super(changeEventType, data);
    }

    public GradeChangeEvent(ChangeEventType changeEventType, Nota data, Nota oldData) {
        super(changeEventType, data, oldData);
    }

}
