package utils;

public class AbstractChangeEvent<T> {

    private ChangeEventType changeEventType;
    private T data, oldData;

    public AbstractChangeEvent(ChangeEventType changeEventType, T data) {
        this.changeEventType = changeEventType;
        this.data = data;
    }

    public AbstractChangeEvent(ChangeEventType changeEventType, T data, T oldData) {
        this.changeEventType = changeEventType;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getChangeEventType() {
        return changeEventType;
    }

    public void setChangeEventType(ChangeEventType changeEventType) {
        this.changeEventType = changeEventType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getOldData() {
        return oldData;
    }

    public void setOldData(T oldData) {
        this.oldData = oldData;
    }
}
