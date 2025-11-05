package org.yarmosh.dao;

import jakarta.persistence.*;
import org.yarmosh.model.Weather;

import java.util.List;
import java.util.logging.Level;

public class DaoWeather extends DAO<Weather> {

    public void create(Weather weather) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(weather);
            tx.commit();
            logger.log(Level.INFO, "Создан Weather: {0}", weather);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при создании Weather: " + weather, e);
            throw e; // пробрасываем исключение наверх
        } finally {
            em.close();
        }
    }

    public Weather read(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Weather> query = em.createNamedQuery("Weather.findById", Weather.class);
            query.setParameter("id", id);
            Weather result = query.getSingleResult();
            logger.log(Level.INFO, "Прочитан Weather с id={0}: {1}", new Object[]{id, result});
            return result;
        } catch (NoResultException e) {
            logger.log(Level.WARNING, "Weather с id={0} не найден.", id);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при чтении Weather с id=" + id, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(Weather weather) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.merge(weather);
            tx.commit();
            logger.log(Level.INFO, "Обновлён Weather: {0}", weather);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при обновлении Weather: " + weather, e);
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
            Weather weather = em.find(Weather.class, id);
            if (weather != null) {
                em.remove(weather);
                logger.log(Level.INFO, "Удалён Weather с id={0}", id);
            } else {
                logger.log(Level.WARNING, "Попытка удалить несуществующий Weather с id={0}", id);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.log(Level.SEVERE, "Ошибка при удалении Weather с id=" + id, e);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Weather> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Weather> query = em.createNamedQuery("Weather.findAll", Weather.class);
            List<Weather> result = query.getResultList();
            logger.log(Level.INFO, "Получено {0} записей Weather.", result.size());
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при получении всех записей Weather.", e);
            throw e;
        } finally {
            em.close();
        }
    }
}
