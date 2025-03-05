
package DAO;
import java.util.List;
import Exceptions.DuplicateEntryException;
import Exceptions.NotFoundException;

public interface DAO<T> {
public abstract void save(T data) ;
public abstract T getById(int id) throws NotFoundException;
public abstract List<T> listAll();
public abstract void update (T data, int id) throws NotFoundException;
public abstract void delete(T data);
}
