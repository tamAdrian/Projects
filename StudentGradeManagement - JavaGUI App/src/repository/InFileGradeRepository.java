package repository;

import domain.Nota;
import utils.Pair;
import validator.Validator;
import java.util.Arrays;
import java.util.List;

public class InFileGradeRepository extends AbstractFileRepository<Pair<Integer, Integer>, Nota> {

    public InFileGradeRepository(Validator<Nota> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    protected Nota createEntity(String linie) {

        List<String> atribute = Arrays.asList(linie.split("\\|"));
        String idStudent = atribute.get(0).split("=")[1];
        String idTema = atribute.get(1).split("=")[1];
        String valoare = atribute.get(2).split("=")[1];
        String saptamana = atribute.get(3).split("=")[1];

        Nota nota = new Nota(Integer.parseInt(idStudent), Integer.parseInt(idTema), Float.parseFloat(valoare), Integer.parseInt(saptamana));
        return nota;
    }
}