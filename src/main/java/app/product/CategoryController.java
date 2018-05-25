package app.product;

import app.user.User;
import app.util.SessionBase;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class CategoryController extends SessionBase{
    public ArrayList<Category> getCategoryList()
    {
        Transaction tx = null;
        List categories = null;
        try{
            tx = getSession().beginTransaction();
            categories = getSession().createQuery("FROM Category").list();
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
        ArrayList<Category> listOfCategories = new ArrayList<>(categories.size());
        listOfCategories.addAll(categories);
        return listOfCategories;
    }
    public void sellProductFromCategory(int categoryID, int newNumber)
    {
        Transaction tx = null;
        try{
            tx = getSession().beginTransaction();
            Query query = getSession().createQuery("UPDATE Category SET numberOfSoldProducts = :numberOfSoldProducts WHERE id = :id");
            query.setParameter("numberOfSoldProducts", newNumber);
            query.setParameter("id", categoryID);

            int result = query.executeUpdate();
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
    }
}
