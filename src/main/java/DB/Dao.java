package DB;

import BusinessLayer.Football.Team;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    Optional<T> get(String id) throws ClassNotFoundException;

    List<T> getAll() throws ClassNotFoundException;

    void save(T t) throws SQLException;

    void update(T t);

    void delete(T t);

}
