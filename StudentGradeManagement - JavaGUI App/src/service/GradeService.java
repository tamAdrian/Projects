package service;

import domain.Nota;
import domain.NotaDTO;
import domain.Student;
import domain.Tema;
import repository.CrudRepository;
import utils.*;
import validator.ValidationException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GradeService implements Observable<GradeChangeEvent> {

    private HomeworkService homeworkService;
    private StudentService studentService;
    private CrudRepository<Pair<Integer, Integer>, Nota> repoNote;
    private String folder = "D:\\Adrian\\3.FACULTATE - ANUL II (Sem I)\\MAP\\Projects\\Tema3\\src\\noteStudentiXML\\";

    private List<Observer<GradeChangeEvent>> observers = new ArrayList<>();

    public GradeService(HomeworkService homeworkService, StudentService studentService, CrudRepository<Pair<Integer, Integer>, Nota> repoNote) {
        this.homeworkService = homeworkService;
        this.studentService = studentService;
        this.repoNote = repoNote;
    }

    public Integer saptamaniIntarziere(Integer deadline, Integer saptamanaCurenta) {
        return saptamanaCurenta - deadline;
    }

    /**
     * @param idTema
     * @param saptamanaCurenta
     * @return true if student was late, false otherwise.
     */
    public boolean intarziat(Integer idTema, Integer saptamanaCurenta) {
        Tema tema = homeworkService.findHomework(idTema);
        if (tema == null) {
            throw new RuntimeException("Tema inexistenta!\n");
        }
        return saptamaniIntarziere(tema.getDeadline(), saptamanaCurenta) > 0;
    }

    public float calculeazaDepunctare(Float valoare, Integer deadline, Integer saptCurenta, Integer saptMotivate) {

        Integer saptamaniDepunctare = saptamaniIntarziere(deadline, saptCurenta) - saptMotivate;
        Float valoareNota;
        if (saptamaniDepunctare > 2) {
            valoareNota = 0F;
        } else {
            valoareNota = (float) (valoare - (2.5) * saptamaniDepunctare);
        }
        return valoareNota;
    }

    /**
     * save grade for a student, and write in the specified file the grade.
     *
     * @param n
     * @param saptMotivate
     * @param feedback
     * @throws RuntimeException if the grade was already save.
     */
    public void addGrade(Nota n, Integer saptMotivate, String feedback) {

        Student student = studentService.findStudent(n.getStudent());
        Tema tema = homeworkService.findHomework(n.getTema());


        Integer saptamaniDepunctare = saptamaniIntarziere(tema.getDeadline(), n.getSaptamana()) - saptMotivate;
        Float valoareNota;
        if (saptamaniDepunctare > 2) {
            valoareNota = 0F;
        } else {
            valoareNota = (float) (n.getValoare() - (2.5) * saptamaniDepunctare);
        }

        n.setValoare(valoareNota);
        if (repoNote.save(n) != null) {
            throw new ValidationException("Nota pentru acest laborator a fost deja adaugata!\n");
        } else {
            notifyObservers(new GradeChangeEvent(ChangeEventType.ADD, n));
        }
        writeToFile(n, feedback);

    }

    /**
     * @return list of grades
     */
    public Iterable<Nota> allGrades() {
        return repoNote.findAll();
    }

    public void setFolder(String newFolder) {
        this.folder = newFolder;
    }

    /**
     * write grade in a student file.
     *
     * @param nota
     * @param feedback
     */
    private void writeToFile(Nota nota, String feedback) {
        String filename = studentService.findStudent(nota.getStudent()).getNume();
        Integer deadline = homeworkService.findHomework(nota.getTema()).getDeadline();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(folder + filename, true))) {
            bw.write("Tema" + nota.getTema() + "\n" + "Nota: " + nota.getValoare() + '\n' + "Predata in saptamana: " + nota.getSaptamana() + '\n' + "Deadline: " +
                    deadline + "\nFeedback: " + feedback + "\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> returnListOfIds() {

        List<Tema> list = StreamSupport.stream(homeworkService.allHomeworks().spliterator(), false)
                .collect(Collectors.toList());

        return list.stream()
                .map(tema -> tema.getID().toString())
                .collect(Collectors.toList());

    }

    public List<NotaDTO> getAllNoteDTO() {

        List<Nota> listaNote = StreamSupport.stream(repoNote.findAll().spliterator(), false)
                .collect(Collectors.toList());

        return listaNote.stream()
                .map(nota -> {
                    String numeStudent = studentService.findStudent(nota.getStudent()).getNume();
                    Integer numeTema = homeworkService.findHomework(nota.getTema()).getIdTema();
                    Float valoareNota = nota.getValoare();
                    return new NotaDTO(numeStudent, numeTema, valoareNota);
                })
                .collect(Collectors.toList());
    }

    public HomeworkService getHomeworkService() {
        return homeworkService;
    }

    public void setHomeworkService(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }

    public StudentService getStudentService() {
        return studentService;
    }

    public void setStudentService(StudentService studentService) {
        this.studentService = studentService;
    }

    public List<Student> findStudentByName(String name) {

        String nume = name.toLowerCase();

        List<Student> studentList = StreamSupport.stream(studentService.allStudents().spliterator(), false)
                .collect(Collectors.toList());

        return studentList.stream()
                .filter(x -> x.getNume().toLowerCase().contains(nume))
                .collect(Collectors.toList());

    }

    public List<NotaDTO> filterByStudentNameAndHomework(String numeStudent, String idTema) {

        List<NotaDTO> notaDTOList = StreamSupport.stream(this.getAllNoteDTO().spliterator(), false)
                .collect(Collectors.toList());

        Predicate<NotaDTO> filterByName = nota -> {
            if (numeStudent.equals("")) {
                return true;
            }
            String nume = numeStudent.toLowerCase();
            String numeStudentNota = nota.getNumeStudent().toLowerCase();
            return numeStudentNota.contains(nume);
        };

        Predicate<NotaDTO> filterByHomework = nota -> {
            if (idTema.equals("")) {
                return true;
            }
            Integer id = Integer.parseInt(idTema);
            return nota.getNumeTema().equals(id);
        };

        return notaDTOList.stream()
                .filter(filterByName.and(filterByHomework))
                .collect(Collectors.toList());
    }

    public List<String> getAllHomeworkNumber() {

        List<Nota> notaList = StreamSupport.stream(repoNote.findAll().spliterator(), false)
                .collect(Collectors.toList());

        Function<Nota, String> homeworkNumber = nota -> nota.getTema().toString();

        return notaList.stream()
                .map(homeworkNumber)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

    }

    public List<String> getAllGroupNumber() {

        List<Student> studentList = StreamSupport.stream(getStudentService().allStudents().spliterator(), false)
                .collect(Collectors.toList());

        Function<Student, String> groupNumber = student -> student.getGrupa();

        return studentList.stream()
                .map(groupNumber)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

    }

    public List<NotaDTO> filterByHomeworkNumberAndGroup(String idTema, String numarGrupa) {

        List<NotaDTO> notaDTOList = StreamSupport.stream(getAllNoteDTO().spliterator(), false)
                .collect(Collectors.toList());

        Predicate<NotaDTO> tema = notaDTO -> notaDTO.getNumeTema().toString().equals(idTema);

        Predicate<NotaDTO> grupa = notaDTO -> {
            List<Student> studentList = findStudentByName(notaDTO.getNumeStudent());
            List<Student> finalStudentList =
                    studentList.stream()
                            .filter(student -> student.getGrupa().equals(numarGrupa))
                            .collect(Collectors.toList());

            return !finalStudentList.isEmpty();
        };

        return notaDTOList.stream()
                .filter(tema.and(grupa))
                .collect(Collectors.toList());
    }

    public Float averageForStudent(String name) {

        List<NotaDTO> notaDTOList = StreamSupport.stream(getAllNoteDTO().spliterator(), false)
                .collect(Collectors.toList());

        List<NotaDTO> studentList =
                notaDTOList.stream()
                        .filter(notaDTO -> notaDTO.getNumeStudent().equals(name))
                        .collect(Collectors.toList());

        Float sum = studentList.stream()
                .map(x -> x.getValoareNota())
                .reduce(0F, (x, y) -> x + y);
        return (sum / (studentList.size()));

    }

    @Override
    public void addObserver(Observer<GradeChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<GradeChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(GradeChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }
}