package domain;

public class Student implements HasID<Integer>{

    private Integer idStudent;
    private String nume;
    private String grupa;
    private String email;
    private String indrumator;
    private Status status;

    public Student(Integer idStudent, String nume, String grupa, String email, String indrumator) {
        this.idStudent = idStudent;
        this.nume = nume;
        this.grupa = grupa;
        this.email = email;
        this.indrumator = indrumator;
        this.status = Status.Activ;

    }

    @Override
    public Integer getID() {
        return idStudent;
    }

    @Override
    public void setID(Integer integer) {
        this.idStudent = integer;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getGrupa() {
        return grupa;
    }

    public void setGrupa(String grupa) {
        this.grupa = grupa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIndrumator() {
        return indrumator;
    }

    public void setIndrumator(String indrumator) {
        this.indrumator = indrumator;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return   "(" + idStudent + ")" + " " + nume + " " + grupa;
    }
}