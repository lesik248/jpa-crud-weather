package org.yarmosh.dao;

import jakarta.persistence.*;
import org.yarmosh.model.CitizenType;

import java.util.List;
import java.util.logging.Level;

public class DaoCitizenType extends DAO<CitizenType> {

    public void create(CitizenType type) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(type);
            tx.commit();
            logger.log(Level.INFO, "Создан CitizenType: {0}", type);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при создании CitizenType: " + type, e);
            throw new PersistenceException("Не удалось создать CitizenType: " + type, e);
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
            logger.log(Level.INFO, "Прочитан CitizenType с id={0}: {1}", new Object[]{id, result});
        } catch (NoResultException e) {
            logger.log(Level.WARNING, "CitizenType с id={0} не найден.", id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при чтении CitizenType с id=" + id, e);
            throw new PersistenceException("Не удалось прочитать CitizenType с id=" + id, e);
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
            logger.log(Level.INFO, "Обновлён CitizenType: {0}", type);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при обновлении CitizenType: " + type, e);
            throw new PersistenceException("Не удалось обновить CitizenType: " + type, e);
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
                logger.log(Level.INFO, "Удалён CitizenType с id={0}", id);
            } else {
                logger.log(Level.WARNING, "Попытка удалить несуществующий CitizenType с id={0}", id);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при удалении CitizenType с id=" + id, e);
            throw new PersistenceException("Не удалось удалить CitizenType с id=" + id, e);
        } finally {
            em.close();
        }
    }

    public List<CitizenType> getAll() {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<CitizenType> query = em.createNamedQuery("CitizenType.findAll", CitizenType.class);
            List<CitizenType> result = query.getResultList();
            logger.log(Level.INFO, "Получено {0} записей CitizenType.", result.size());
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при получении всех записей CitizenType.", e);
            throw new PersistenceException("Не удалось получить список CitizenType.", e);
        } finally {
            em.close();
        }
    }
}
