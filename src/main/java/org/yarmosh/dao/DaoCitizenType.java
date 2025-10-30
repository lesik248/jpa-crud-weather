package org.yarmosh.dao;

import javax.persistence.*;

import org.yarmosh.model.CitizenType;

import java.util.List;
import java.util.logging.Level;

public class DaoCitizenType extends DAO<CitizenType> {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("your-persistence-unit");

    public void create(CitizenType type) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(type);
            tx.commit();
            logger.log(Level.INFO, "Created CitizenType: {0}", type);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Error creating CitizenType: " + type, e);
        } finally {
            em.close();
        }
    }

    public CitizenType read(int id) {
        EntityManager em = emf.createEntityManager();
        CitizenType result = null;

        try {
            TypedQuery<CitizenType> query = em.createNamedQuery("CitizenType.findById", CitizenType.class);
            query.setParameter("id", id);
            result = query.getSingleResult();
            logger.log(Level.INFO, "Read CitizenType with id={0}: {1}", new Object[]{id, result});
        } catch (NoResultException e) {
            logger.log(Level.WARNING, "CitizenType with id={0} not found.", id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading CitizenType with id=" + id, e);
        } finally {
            em.close();
        }
        return result;
    }

    public void update(CitizenType type) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.merge(type);
            tx.commit();
            logger.log(Level.INFO, "Updated CitizenType: {0}", type);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Error updating CitizenType: " + type, e);
        } finally {
            em.close();
        }
    }

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            CitizenType type = em.find(CitizenType.class, id);
            if (type != null) {
                em.remove(type);
                logger.log(Level.INFO, "Deleted CitizenType with id={0}", id);
            } else {
                logger.log(Level.WARNING, "Attempted to delete non-existing CitizenType with id={0}", id);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Error deleting CitizenType with id=" + id, e);
        } finally {
            em.close();
        }
    }

    public List<CitizenType> getAll() {
        EntityManager em = emf.createEntityManager();
        List<CitizenType> result = null;

        try {
            TypedQuery<CitizenType> query = em.createNamedQuery("CitizenType.findAll", CitizenType.class);
            result = query.getResultList();
            logger.log(Level.INFO, "Fetched {0} CitizenType records.", result.size());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching all CitizenType records.", e);
        } finally {
            em.close();
        }
        return result;
    }
}
