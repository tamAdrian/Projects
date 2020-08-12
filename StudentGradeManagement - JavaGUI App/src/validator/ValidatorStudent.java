package validator;

import domain.Student;

public class ValidatorStudent implements Validator<Student> {

    boolean valideazaNume(String nume) {

        String[] splitNume = nume.split(" ");
        if (splitNume.length < 2) {
            return false;
        }
        for (String s : splitNume) {
            if (s.length() <= 2) {
                return false;
            }
            if (!s.matches("[A-Z][a-z]+")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void validate(Student student) {

        String errors = "";

        if (student.getID() < 10000 || student.getID() > 99999) {
            errors += "Numar matricol invalid!\n";
        }

        String nume = student.getNume();
        if (nume == "") {
            errors += "Nume vid!\n";
        } else {
            if (!valideazaNume(nume)) {
                errors += "Nume incorect!\n";
            }
        }

        String email = student.getEmail();
        if (!email.matches("[a-z,0-9]{1,}@scs.ubbcluj.ro")) {
            errors += "Email incorect!\n";
        }

        String numeIndrumator = student.getIndrumator();
        if (!valideazaNume(numeIndrumator)) {
            errors += "Nume indrumator incorect!\n";
        }

        String grupa = student.getGrupa();
        if (!grupa.matches("[1-9][1-3][1-9][/][1-2]")) {
            errors += "Numar grupa invalid!\n";
        }

        if (!errors.equals("")) {
            throw new ValidationException(errors);
        }
    }

}