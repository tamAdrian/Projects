package domain;

import utils.Pair;

public class Nota implements HasID<Pair<Integer, Integer>>{

    private Pair<Integer, Integer> id;
    private Integer student;
    private Integer tema;
    private float valoare;
    private int saptamana;

    @Override
    public Pair<Integer, Integer> getID() {
        return id;
    }

    @Override
    public void setID(Pair<Integer, Integer> integerIntegerPair) {
        this.id.setA(integerIntegerPair.getA());
        this.id.setB(integerIntegerPair.getB());
    }

    public Nota(Integer student, Integer tema, float valoare, int saptamana) {
        this.id = new Pair<>(student, tema);
        this.student = student;
        this.tema = tema;
        this.valoare = valoare;
        this.saptamana = saptamana;
    }

    public Integer getStudent() {
        return student;
    }

    public void setStudent(Integer student) {
        this.student = student;
    }

    public Integer getTema() {
        return tema;
    }

    public void setTema(Integer tema) {
        this.tema = tema;
    }

    public float getValoare() {
        return valoare;
    }

    public void setValoare(float valoare) {
        this.valoare = valoare;
    }

    public int getSaptamana() {
        return saptamana;
    }

    public void setSaptamana(int saptamana) {
        this.saptamana = saptamana;
    }

    @Override
    public String toString() {
        return
                "student=" + student +
                        "|tema=" + tema +
                        "|valoare=" + valoare +
                        "|saptamana=" + saptamana;
    }
}
