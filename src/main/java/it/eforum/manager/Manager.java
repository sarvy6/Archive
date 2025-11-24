package it.eforum.manager;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import it.eforum.entity.Employee;
import it.eforum.entity.PaySlip;

public class Manager {
    private final static SessionFactory factory = buildSessionFactory(); 

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");

            configuration.addAnnotatedClass(Employee.class);
            configuration.addAnnotatedClass(PaySlip.class);

            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    public static void shutdown(){
        factory.close();
    }
    protected void create(Object object) {
        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(object);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    protected void edit(Object object) {
        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(object);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    protected void delete(Object object) {
        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();
            session.remove(object);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    protected <T> List<T> selectAll(Class<T> clazz) {
        try (Session session = factory.openSession()) {
            Query<T> query = session.createQuery("from " + clazz.getSimpleName(), clazz);
            return query.getResultList();
        }
    }

    protected <T> T findById(Class<T> clazz, Object id) {
        try (Session session = factory.openSession()) {
            return session.get(clazz, (java.io.Serializable) id);
        }
    }

    protected <T> List<T> executeSelect(String hql, Class<T> clazz) {
        try (Session session = factory.openSession()) {
            Query<T> query = session.createQuery(hql, clazz);
            return query.getResultList();
        }
    }
}
