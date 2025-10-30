package org.yarmosh.dao;

import javax.persistence.*;

import org.yarmosh.model.Weather;

import java.util.List;
import java.util.logging.Level;

public class DaoWeather extends DAO<Weather> {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("your-persistence-unit");

    public void create(Weather weather) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(weather);
            tx.commit();
            logger.log(Level.INFO, "Created Weather: {0}", weather);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Error creating Weather: " + weather, e);
        } finally {
            em.close();
        }
    }

    public Weather read(int id) {
        EntityManager em = emf.createEntityManager();
        Weather result = null;

        try {
            TypedQuery<Weather> query = em.createNamedQuery("Weather.findById", Weather.class);
            query.setParameter("id", id);
            result = query.getSingleResult();
            logger.log(Level.INFO, "Read Weather with id={0}: {1}", new Object[]{id, result});
        } catch (NoResultException e) {
            logger.log(Level.WARNING, "Weather with id={0} not found.", id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading Weather with id=" + id, e);
        } finally {
            em.close();
        }
        return result;
    }

    public void update(Weather weather) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.merge(weather);
            tx.commit();
            logger.log(Level.INFO, "Updated Weather: {0}", weather);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Error updating Weather: " + weather, e);
        } finally {
            em.close();
        }
    }

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Weather weather = em.find(Weather.class, id);
            if (weather != null) {
                em.remove(weather);
                logger.log(Level.INFO, "Deleted Weather with id={0}", id);
            } else {
                logger.log(Level.WARNING, "Attempted to delete non-existing Weather with id={0}", id);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Error deleting Weather with id=" + id, e);
        } finally {
            em.close();
        }
    }

    public List<Weather> getAll() {
        EntityManager em = emf.createEntityManager();
        List<Weather> result = null;

        try {
            TypedQuery<Weather> query = em.createNamedQuery("Weather.findAll", Weather.class);
            result = query.getResultList();
            logger.log(Level.INFO, "Fetched {0} Weather records.", result.size());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching all Weather records.", e);
        } finally {
            em.close();
        }
        return result;
    }
}