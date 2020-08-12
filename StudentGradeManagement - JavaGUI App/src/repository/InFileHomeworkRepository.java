package repository;

import domain.Tema;
import validator.Validator;
import java.util.Arrays;
import java.util.List;

public class InFileHomeworkRepository extends AbstractFileRepository<Integer, Tema> {

    public InFileHomeworkRepository(Validator<Tema> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    protected Tema createEntity(String linie) {

        List<String> atribute =  Arrays.asList(linie.split("\\|"));

        String id = atribute.get(0).split("=")[1];
        String descriere = atribute.get(1).split("=")[1];
        String deadline = atribute.get(2).split("=")[1];
        String predata = atribute.get(3).split("=")[1];

        Tema t = new Tema(Integer.parseInt(id), descriere, Integer.parseInt(deadline), Integer.parseInt(predata));
        return t;
    }
}
