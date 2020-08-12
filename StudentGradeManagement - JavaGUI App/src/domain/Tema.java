package domain;

public class Tema implements HasID<Integer> {

    private Integer idTema;
    private String descriere;
    private Integer deadline;
    private Integer predata;

    public Tema(Integer idTema, String descriere, Integer deadline, Integer predata) {
        this.idTema = idTema;
        this.descriere = descriere;
        this.deadline = deadline;
        this.predata = predata;
    }

    @Override
    public Integer getID() {
        return idTema;
    }

    @Override
    public void setID(Integer integer) {
        this.idTema = integer;
    }

    public Integer getIdTema() {
        return idTema;
    }

    public void setIdTema(Integer idTema) {
        this.idTema = idTema;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public Integer getDeadline() {
        return deadline;
    }

    public void setDeadline(Integer deadline) {
        this.deadline = deadline;
    }

    public Integer getPredata() {
        return predata;
    }

    public void setPredata(Integer predata) {
        this.predata = predata;
    }

    @Override
    public String toString() {
        return "id=" + idTema + "|" +
                "descriere=" + descriere + '|' +
                "deadline=" + deadline + '|'+
                "predata=" + predata;
    }
}
