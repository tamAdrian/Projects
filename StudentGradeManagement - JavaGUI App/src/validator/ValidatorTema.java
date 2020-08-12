package validator;

import domain.Tema;

public class ValidatorTema implements Validator<Tema> {

    @Override
    public void validate(Tema tema) {

        String errors = "";

        int numar = tema.getIdTema();
        if (numar < 1 || numar > 14) {
            errors += "Numar tema invalid! (trebuie sa fie cuprins intre 1 si 14)\n";
        }

        int deadline = tema.getDeadline();
        if (deadline <= 1 || deadline > 14) {
            errors += "Deadline invalid! (trebuie sa fie cuprins intre 1 si 14)\n";
        }

        int predat = tema.getPredata();
        if (predat <= 1 || predat > 14) {
            errors += "Saptamana in care a fost predata tema este invalida!\n Trebuie sa fie cuprins intre 1 si 14\n";
        }

        if (predat <= deadline) {
            errors += "Ultima saptamana in care poate fi predata\n trebuie sa fie mai mare decat deadline-ul !\n";
        }

        if (!errors.equals("")) {
            throw new ValidationException(errors);
        }
    }
}
