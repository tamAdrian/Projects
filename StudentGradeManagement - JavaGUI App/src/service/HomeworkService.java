package service;

import domain.Tema;
import repository.CrudRepository;
import utils.ChangeEventType;
import utils.HomeworkChangeEvent;
import utils.Observable;
import utils.Observer;
import validator.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class HomeworkService implements Observable<HomeworkChangeEvent> {

    private List<Observer<HomeworkChangeEvent>> observers = new ArrayList<>(); // list of observers
    private CrudRepository<Integer, Tema> repoTeme;

    public HomeworkService(CrudRepository<Integer, Tema> repoTeme) {
        this.repoTeme = repoTeme;
    }

    /**
     * give the last homework
     *
     * @param teme
     * @return last homework or homework with 0 id, if the repository is empty
     */
    public static Tema lastHomework(Iterable<Tema> teme) {

        Tema lastHomework = new Tema(0, "", 0, 0);
        for (Tema homework : teme) {
            lastHomework = homework;
        }
        return lastHomework;
    }

    /**
     * add a homework
     *
     * @param t
     * @return null if the given homework was saved or the homework, if id already exists
     * @throws RuntimeException if given homework deadline is greater than last homework deadline
     */
    public Tema addHomework(Tema t) {

        Iterable<Tema> teme = repoTeme.findAll();

        Tema lastHomework = lastHomework(repoTeme.findAll());
        if (lastHomework.getIdTema() != 0 && t.getDeadline() > lastHomework.getDeadline()) {
            Tema saved = repoTeme.save(t);
            notifyObservers(new HomeworkChangeEvent(ChangeEventType.ADD, saved));
            return saved;
        }

        if (lastHomework.getIdTema() == 0) {
            Tema saved = repoTeme.save(t);
            notifyObservers(new HomeworkChangeEvent(ChangeEventType.ADD, saved));
            return saved;
        } else {
            throw new ValidationException("Modificati deadline-ul temei\n pentru a nu se intercala cu alte teme.\n");
        }

    }

    /**
     * extends the deadline of a homework with one week
     *
     * @param currentWeek
     * @param Id
     * @return null, if we made the change
     * @throws RuntimeException if we couldn't make the change
     */
    public Tema postponeHomework(Integer currentWeek, Integer Id) {

        Tema homework = repoTeme.findOne(Id);
        if ((currentWeek <= homework.getDeadline()) && homework.getDeadline() + 1 <= 14) {
            Tema newHomework = new Tema(homework.getIdTema(), homework.getDescriere(), homework.getDeadline() + 1, homework.getPredata());

            Tema updated = repoTeme.update(newHomework);
            notifyObservers(new HomeworkChangeEvent(ChangeEventType.POSTPONE, updated));
            return updated;
        } else {
            throw new ValidationException("Nu s-a putut prelungi termenul de predare!\n");
        }
    }

    public Iterable<Tema> allHomeworks() {
        return repoTeme.findAll();
    }

    public Tema findHomework(Integer id) {

        Tema returnedEntity = repoTeme.findOne(id);
        if (returnedEntity == null) {
            throw new RuntimeException("Tema inexistenta !\n");
        }
        return returnedEntity;

    }


    @Override
    public void addObserver(Observer<HomeworkChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<HomeworkChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(HomeworkChangeEvent t) {
        observers.stream().forEach(x -> x.update(t));
    }
}
