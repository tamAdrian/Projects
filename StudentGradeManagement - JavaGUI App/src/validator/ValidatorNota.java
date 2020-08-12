package validator;

import domain.Nota;

public class ValidatorNota implements Validator<Nota> {
    @Override
    public void validate(Nota entity) throws ValidationException {

        String err = "";

        if (entity.getValoare() < 0 || entity.getValoare() > 10) {
            err += "Nota invalida!\n";
        }
        if (err.length() > 0) {
            throw new ValidationException(err);
        }

    }
}