package service;

import domain.Status;
import domain.Student;
import repository.CrudRepository;

public class StudentService {

    private CrudRepository<Integer, Student> repoStudenti;

    public StudentService(CrudRepository<Integer, Student> repoStudenti) {
        this.repoStudenti = repoStudenti;
    }

    /**
     * adds a student
     *
     * @param s
     * @return null if the given student was saved or the student, if id already exists
     */
    public Student addStudent(Student s) {
        return repoStudenti.save(s);
    }

    /**
     * remove a student if its status is 'absolvent', otherwise if status is 'activ' will be changed in 'inactiv'
     *
     * @param id
     * @return student - if status is 'activ', and will be changed in 'inactiv'
     * - if status is 'absolvent', the student will be removed
     * otherwise return null - if status is 'inactiv', and student won't be removed
     */
    public Student deleteStudent(Integer id) {

        Student s = repoStudenti.findOne(id);
        if (s.getStatus() == Status.Activ) {
            s.setStatus(Status.Inactiv);
            Student s1 = s;
            repoStudenti.update(s1);
            return s;
        }

        if (s.getStatus() == Status.Absolvent) {
            return repoStudenti.delete(s.getID());
        }

        return null; //if student status is "Inactive" we return null
    }

    /**
     * update the name, email or professor of a student.
     *
     * @param id
     * @param nume
     * @param email
     * @param indrumator
     * @return the updated student
     */
    public Student updateStudent(Integer id, String nume, String email, String indrumator) {

        if (repoStudenti.findOne(id) == null) {
            throw new RuntimeException("Student inexistent!\n");
        }

        Student s = repoStudenti.findOne(id);

        String newName, newEmail, newIndrumator;
        if (nume.equals("")) {
            newName = s.getNume();
        } else {
            newName = nume;
        }

        if (email.equals("")) {
            newEmail = s.getEmail();
        } else {
            newEmail = email;
        }

        if (indrumator.equals("")) {
            newIndrumator = s.getIndrumator();
        } else {
            newIndrumator = indrumator;
        }

        Student newSt = new Student(s.getID(), newName, s.getGrupa(), newEmail, newIndrumator);
        newSt.setStatus(s.getStatus());

        return repoStudenti.update(newSt);
    }

    public Iterable<Student> allStudents() {
        return repoStudenti.findAll();
    }

    public Student findStudent(Integer id) {

        Student returnedStudent = repoStudenti.findOne(id);
        if (returnedStudent == null) {
            throw new RuntimeException("Student inexistent!\n");
        }
        return returnedStudent;
    }
}
