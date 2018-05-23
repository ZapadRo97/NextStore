package app.product;

import app.user.User;
import app.util.SessionBase;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

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
}
