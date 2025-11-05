package org.yarmosh.dao;

import jakarta.persistence.*;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public abstract class DAO<T> {

  protected EntityManagerFactory emf;
  protected final Logger logger = Logger.getLogger(getClass().getName());

  public DAO() {
      emf = Persistence.createEntityManagerFactory("JPADemo");
  }

  public abstract void create(T item)
    throws SQLException;

  public abstract T read(int id) throws SQLException;

  public abstract void update(T entity)
    throws SQLException;

  public abstract void delete(int id)
    throws SQLException;

  public abstract List<T> getAll() throws SQLException;
}
