package repository;

import domain.Status;
import domain.Student;
import validator.Validator;
import java.util.Arrays;
import java.util.List;

public class InFileStudentRepository extends AbstractFileRepository<Integer, Student> {

    public InFileStudentRepository(Validator<Student> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    protected Student createEntity(String linie) {

        List<String> atribute = Arrays.asList(linie.split("\\|"));

        String id = atribute.get(0).split("=")[1];
        String nume = atribute.get(1).split("=")[1];
        String grupa = atribute.get(2).split("=")[1];
        String email = atribute.get(3).split("=")[1];
        String indrumator = atribute.get(4).split("=")[1];
        String status = atribute.get(5).split("=")[1];

        Student st = new Student(Integer.parseInt(id),nume, grupa, email, indrumator);
        st.setStatus(Status.valueOf(status));

        return st;
    }
}
