package repository;

import domain.HasID;
import validator.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class InMemoryRepository<ID, E extends HasID<ID>> implements CrudRepository<ID, E> {

    private List<E> list;
    private Validator<E> validator;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        this.list = new ArrayList<>();
    }

    @Override
    public E findOne(ID id) {

        if(id == null){
            throw new IllegalArgumentException("ID must be not null!");
        }

        Predicate<E> found = x -> id.equals(x.getID());
        E entity = list.stream()
                .filter( x-> found.test(x))
                .findAny()
                .orElse(null);

        return entity;

    }

    @Override
    public Iterable<E> findAll() {
        return list;
    }

    @Override
    public E save(E entity) {

        if(entity == null){
            throw new IllegalArgumentException("Entitatea trebuie sa fie nenula!\n");
        }

        validator.validate(entity);

        E foundEntity = findOne(entity.getID());
        if(foundEntity == null){
            list.add(entity);
            return null;
        }

        return entity;

    }

    @Override
    public E delete(ID id) {

        if(id == null){
            throw new IllegalArgumentException("ID-ul trebuie sa fie nenul !\n");
        }

        E foundEntity = findOne(id);
        if(foundEntity == null){
            return null;
        }

        list.remove(foundEntity);
        return foundEntity;

    }

    @Override
    public E update(E entity) {

        if(entity == null){
            throw new IllegalArgumentException("Entitatea trebuie sa fie nenula!\n");
        }
        validator.validate(entity);

        E foundEntity = findOne(entity.getID());
        if(foundEntity == null){
            return entity;
        }

        int index = list.indexOf(foundEntity);
        list.remove(index);
        list.add(index, entity);
        return null;

    }
}
