import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;

public class Controller {

    private SessionFactory sessionFactory;
    private Session session;

    private SessionFactory getSessionFactory() {
        if(sessionFactory == null)
        {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }

        return sessionFactory;
    }

    private Session getSession() {
        if(session == null || !session.isOpen())
        {
            session = getSessionFactory().openSession();
        }

        return session;
    }

    private void closeSession() {
        session.close();
    }

    //user related functions
    public Integer addUser(String firstName, String lastName, String nickname, String email, String password, String phoneNumber, int accessLevel)
    {
        Transaction tx = null;
        Integer userID = null;
        try{
            tx = getSession().beginTransaction();
            User user = new User(firstName, lastName, nickname, email, password, phoneNumber, accessLevel);
            userID = (Integer)getSession().save(user);
            tx.commit();
        }
        catch(HibernateException ex)
        {
            if(tx != null)
                tx.rollback();
            ex.printStackTrace();
        }
        finally
        {
            closeSession();
        }
        System.out.println("An user has been added");
        return userID;
    }
    public Integer addUser(String firstName, String lastName, String nickname, String email, String password, String phoneNumber)
    {
       return addUser(firstName, lastName, nickname, email, password, phoneNumber, 0);
    }
    public ArrayList<User> getUsersList()
    {
        Transaction tx = null;
        List users = null;
        try{
            tx = getSession().beginTransaction();
            users = getSession().createQuery("FROM User").list();
            tx.commit();
        }
        catch(HibernateException ex)
        {
            if(tx != null)
                tx.rollback();
            ex.printStackTrace();
        }
        finally{
            closeSession();
        }

        //TODO: check the list before returning
        ArrayList<User> listOfUsers = new ArrayList<>(users.size());
        listOfUsers.addAll(users);
        return listOfUsers;
    }

}
