package org.yarmosh.dao;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.yarmosh.model.Region;
import org.yarmosh.model.Region_;

import java.util.List;
import java.util.logging.Level;

public class DaoRegion extends DAO<Region> {

    public void create(Region region) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.merge(region);
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
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Region> cq = cb.createQuery(Region.class);
            Root<Region> root = cq.from(Region.class);
            cq.select(root).where(cb.equal(root.get(Region_.id), id));

            TypedQuery<Region> query = em.createQuery(cq);
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

            // Обновление с помощью CriteriaUpdate
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<Region> update = cb.createCriteriaUpdate(Region.class);
            Root<Region> root = update.from(Region.class);

            update
                    .set(root.get(Region_.name), region.getName())
                    .set(root.get(Region_.square), region.getSquare())
                    .set(root.get(Region_.citizenType), region.getCitizenType())
                    .where(cb.equal(root.get(Region_.id), region.getId()));

            em.createQuery(update).executeUpdate();
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

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaDelete<Region> delete = cb.createCriteriaDelete(Region.class);
            Root<Region> root = delete.from(Region.class);
            delete.where(cb.equal(root.get(Region_.id), id));

            int deleted = em.createQuery(delete).executeUpdate();
            tx.commit();

            if (deleted > 0)
                logger.log(Level.INFO, "Удалён Region с id={0}", id);
            else
                logger.log(Level.WARNING, "Region с id={0} не найден для удаления.", id);

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
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Region> cq = cb.createQuery(Region.class);
            Root<Region> root = cq.from(Region.class);
            cq.select(root);

            TypedQuery<Region> query = em.createQuery(cq);
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
