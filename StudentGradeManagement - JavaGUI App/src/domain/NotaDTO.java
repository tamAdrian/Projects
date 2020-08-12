package domain;

public class NotaDTO {

    private String numeStudent;
    private Integer numeTema;
    private Float valoareNota;

    public NotaDTO(String numeStudent, Integer numeTema, Float valoareNota) {
        this.numeStudent = numeStudent;
        this.numeTema = numeTema;
        this.valoareNota = valoareNota;
    }

    public String getNumeStudent() {
        return numeStudent;
    }

    public void setNumeStudent(String numeStudent) {
        this.numeStudent = numeStudent;
    }

    public Integer getNumeTema() {
        return numeTema;
    }

    public void setNumeTema(Integer numeTema) {
        this.numeTema = numeTema;
    }

    public Float getValoareNota() {
        return valoareNota;
    }

    public void setValoareNota(Float valoareNota) {
        this.valoareNota = valoareNota;
    }
}
