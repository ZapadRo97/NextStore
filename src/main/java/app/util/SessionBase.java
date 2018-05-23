package app.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionBase {

    private SessionFactory sessionFactory;
    private Session session;

    private SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }

        return sessionFactory;
    }

    public Session getSession() {
        if (session == null || !session.isOpen()) {
            session = getSessionFactory().openSession();
        }

        return session;
    }

    protected void closeSession() {
        session.close();
    }
}
