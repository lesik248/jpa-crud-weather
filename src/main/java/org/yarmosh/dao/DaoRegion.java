package org.yarmosh.dao;

import jakarta.persistence.*;
import org.yarmosh.model.Region;

import java.util.List;
import java.util.logging.Level;

public class DaoRegion extends DAO<Region> {

    public void create(Region region) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(region);
            tx.commit();
            logger.log(Level.INFO, "Создан Region: {0}", region);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при создании Region: " + region, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public Region read(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Region> query = em.createNamedQuery("Region.findById", Region.class);
            query.setParameter("id", id);
            Region result = query.getSingleResult();
            logger.log(Level.INFO, "Прочитан Region с id={0}: {1}", new Object[]{id, result});
            return result;
        } catch (NoResultException e) {
            logger.log(Level.WARNING, "Region с id={0} не найден.", id);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при чтении Region с id=" + id, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(Region region) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.merge(region);
            tx.commit();
            logger.log(Level.INFO, "Обновлён Region: {0}", region);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при обновлении Region: " + region, e);
            throw e;
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
                logger.log(Level.INFO, "Удалён Region с id={0}", id);
            } else {
                logger.log(Level.WARNING, "Попытка удалить несуществующий Region с id={0}", id);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при удалении Region с id=" + id, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Region> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Region> query = em.createNamedQuery("Region.findAll", Region.class);
            List<Region> result = query.getResultList();
            logger.log(Level.INFO, "Получено {0} записей Region.", result.size());
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при получении всех записей Region.", e);
            throw e;
        } finally {
            em.close();
        }
    }
}
