package app.user;
import app.util.SessionBase;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.*;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;

public class UserController extends SessionBase{

    //user related functions
    public Integer addUser(String firstName, String lastName, String nickname, String email, String hashedPassword, String phoneNumber, int accessLevel, String salt)
    {
        Transaction tx = null;
        Integer userID = null;
        try{
            tx = getSession().beginTransaction();
            User user = new User(firstName, lastName, nickname, email, hashedPassword, phoneNumber, accessLevel, salt);
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
    public Integer addUser(String firstName, String lastName, String nickname, String email, String password, String phoneNumber, String salt)
    {
       return addUser(firstName, lastName, nickname, email, password, phoneNumber, 0, salt);
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
    public void deleteUser(Integer UserID)
    {
        Transaction tx = null;
        try{
            tx = getSession().beginTransaction();
            User user = (User)getSession().get(User.class, UserID);
            getSession().delete(user);
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
        System.out.println("The use with id " + UserID + " has been deleted successfully");
    }

    public User getUserByUsername(String username)
    {
        Transaction tx = null;
        List users = null;
        try{
            tx = getSession().beginTransaction();
            users = getSession().createQuery("FROM User WHERE nickname='"+username+"'").list();
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

        if(users != null)
            if(!users.isEmpty())
                return (User)users.get(0);
        return null;
    }

    public User getUserByID(int id)
    {
        Transaction tx = null;
        List users = null;
        try{
            tx = getSession().beginTransaction();
            users = getSession().createQuery("FROM User WHERE id="+id).list();
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

        if(users != null)
            if(!users.isEmpty())
                return (User)users.get(0);
        return null;
    }

    public boolean authenticate(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }
        User user = getUserByUsername(username);
        if (user == null) {
            return false;
        }
        String hashedPassword = BCrypt.hashpw(password, user.getSalt());
        return hashedPassword.equals(user.getHashedPassword());
    }

    public void changeUser(Integer id, String firstName, String lastName, String nickname, String email,
                           String hashedPassword, String phoneNumber, String salt)
    {
        Transaction tx = null;
        Integer userID = null;
        try{
            tx = getSession().beginTransaction();

            Query query = getSession().createQuery("UPDATE User SET firstName = :firstName, lastName = :lastName, " +
                    "nickname = :nickname, email = :email, hashedPassword = :hashedPassword, phoneNumber = :phoneNumber," +
                    " salt = :salt " +
                    " where id = :id");
            query.setParameter("firstName", firstName);
            query.setParameter("lastName", lastName);
            query.setParameter("nickname", nickname);
            query.setParameter("email", email);
            query.setParameter("hashedPassword", hashedPassword);
            query.setParameter("salt", salt);
            query.setParameter("phoneNumber", phoneNumber);
            query.setParameter("id", id);
            int result = query.executeUpdate();

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
        System.out.println("An user has been edited");
    }
}
