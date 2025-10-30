package org.yarmosh.dao;

import javax.persistence.*;

import org.yarmosh.model.Region;

import java.util.List;
import java.util.logging.Level;

public class DaoRegion extends DAO<Region> {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("your-persistence-unit");

    public void create(Region region) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(region);
            tx.commit();
            logger.log(Level.INFO, "Created Region: {0}", region);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Error creating Region: " + region, e);
        } finally {
            em.close();
        }
    }

    public Region read(int id) {
        EntityManager em = emf.createEntityManager();
        Region result = null;

        try {
            TypedQuery<Region> query = em.createNamedQuery("Region.findById", Region.class);
            query.setParameter("id", id);
            result = query.getSingleResult();
            logger.log(Level.INFO, "Read Region with id={0}: {1}", new Object[]{id, result});
        } catch (NoResultException e) {
            logger.log(Level.WARNING, "Region with id={0} not found.", id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading Region with id=" + id, e);
        } finally {
            em.close();
        }
        return result;
    }

    public void update(Region region) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.merge(region);
            tx.commit();
            logger.log(Level.INFO, "Updated Region: {0}", region);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Error updating Region: " + region, e);
        } finally {
            em.close();
        }
    }

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Region region = em.find(Region.class, id);
            if (region != null) {
                em.remove(region);
                logger.log(Level.INFO, "Deleted Region with id={0}", id);
            } else {
                logger.log(Level.WARNING, "Attempted to delete non-existing Region with id={0}", id);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Error deleting Region with id=" + id, e);
        } finally {
            em.close();
        }
    }

    public List<Region> getAll() {
        EntityManager em = emf.createEntityManager();
        List<Region> result = null;

        try {
            TypedQuery<Region> query = em.createNamedQuery("Region.findAll", Region.class);
            result = query.getResultList();
            logger.log(Level.INFO, "Fetched {0} Region records.", result.size());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching all Region records.", e);
        } finally {
            em.close();
        }
        return result;
    }
}
