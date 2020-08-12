package repository;

import domain.HasID;
import validator.Validator;
import java.io.*;

public abstract class AbstractFileRepository<ID, E extends HasID<ID>> extends InMemoryRepository<ID, E> {

    private String fileName;

    public AbstractFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        this.fileName = fileName;
        this.loadData();
    }

    /**
     * load data from a file
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void loadData() {

        try (BufferedReader br = new BufferedReader(new FileReader(this.fileName))) {

            String linie;
            while ((linie = br.readLine()) != null) {
                E entity = createEntity(linie);
                super.save(entity);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Save an entity in file.
     *
     * @param entity
     * @throws IOException
     */
    public void saveEntity(E entity) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.fileName, true))) {
            bw.write(entity.toString());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param entity
     * @return entity - if the id already exists
     */
    @Override
    public E save(E entity) {
        E aux = super.save(entity);
        if (aux == null) {
            saveEntity(entity);
        }
        return aux;
    }

    /**
     * clear the entire file
     *
     * @throws FileNotFoundException
     */
    public void clearFile() {

        try (PrintWriter write = new PrintWriter(this.fileName)) {
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * delete an entity and write all to file
     *
     * @param id
     * @return the deleted entity or null - if the entity doesn't exist
     */
    @Override
    public E delete(ID id) {
        E returnedEntity = super.delete(id);
        if (returnedEntity == null) {
            return null;
        }

        clearFile();
        super.findAll().forEach(entity -> saveEntity(entity));

        return returnedEntity;

    }

    /**
     * update an entity and write all to file
     *
     * @param entity
     * @return the updated entity
     */
    @Override
    public E update(E entity) {
        E returnedEntity = super.update(entity);
        if (returnedEntity == null) {
            clearFile();
            super.findAll().forEach(e -> saveEntity(e));
        }
        return returnedEntity;
    }

    protected abstract E createEntity(String linie);
}
