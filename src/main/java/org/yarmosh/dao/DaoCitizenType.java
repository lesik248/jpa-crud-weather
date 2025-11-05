package org.yarmosh.dao;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.yarmosh.model.CitizenType;
import org.yarmosh.model.CitizenType_;

import java.util.List;
import java.util.logging.Level;

public class DaoCitizenType extends DAO<CitizenType> {

    public void create(CitizenType type) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.merge(type);
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

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<CitizenType> cq = cb.createQuery(CitizenType.class);
            Root<CitizenType> root = cq.from(CitizenType.class);
            cq.select(root).where(cb.equal(root.get(CitizenType_.id), id));

            TypedQuery<CitizenType> query = em.createQuery(cq);
            CitizenType result = query.getSingleResult();

            logger.log(Level.INFO, "Прочитан CitizenType с id={0}: {1}", new Object[]{id, result});
            return result;
        } catch (NoResultException e) {
            logger.log(Level.WARNING, "CitizenType с id={0} не найден.", id);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при чтении CitizenType с id=" + id, e);
            throw new PersistenceException("Не удалось прочитать CitizenType с id=" + id, e);
        } finally {
            em.close();
        }
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

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaDelete<CitizenType> delete = cb.createCriteriaDelete(CitizenType.class);
            Root<CitizenType> root = delete.from(CitizenType.class);
            delete.where(cb.equal(root.get(CitizenType_.id), id));

            int deleted = em.createQuery(delete).executeUpdate();
            if (deleted > 0) {
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
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<CitizenType> cq = cb.createQuery(CitizenType.class);
            Root<CitizenType> root = cq.from(CitizenType.class);
            cq.select(root);

            TypedQuery<CitizenType> query = em.createQuery(cq);
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
