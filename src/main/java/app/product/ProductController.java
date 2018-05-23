package app.product;


import app.user.User;
import app.util.SessionBase;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class ProductController extends SessionBase {

    public Integer addProduct(Product product)
    {
        Transaction tx = null;
        Integer productID = null;
        try{
            tx = getSession().beginTransaction();
            productID = (Integer)getSession().save(product);
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
        System.out.println("A product has been added");
        return productID;
    }
    public ArrayList<Product> getProductsList()
    {
        Transaction tx = null;
        List products = null;
        try{
            tx = getSession().beginTransaction();
            products = getSession().createQuery("FROM Product").list();
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
        ArrayList<Product> listOfProducts = new ArrayList<>(products.size());
        listOfProducts.addAll(products);
        return listOfProducts;
    }
    public void deleteProduct(Integer ProductID)
    {
        Transaction tx = null;
        try{
            tx = getSession().beginTransaction();
            Product product = (Product)getSession().get(Product.class, ProductID);
            getSession().delete(product);
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
        System.out.println("The use with id " + ProductID + " has been deleted successfully");
    }
    public Product getProductByID(int id)
    {
        Transaction tx = null;
        List products = null;
        try{
            tx = getSession().beginTransaction();
            products = getSession().createQuery("FROM Product WHERE id="+id).list();
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

        if(products != null)
            if(!products.isEmpty())
                return (Product)products.get(0);
        return null;
    }
    public void changeProduct(Product product)
    {
        Transaction tx = null;
        Integer userID = null;
        try{
            tx = getSession().beginTransaction();

            Query query = getSession().createQuery("UPDATE Product SET name = :name, categoryID = :categoryID, " +
                    "price = :price, description = :description, image = :image, quantity = :quantity," +
                    " imagePath = :imagePath " +
                    " where id = :id");
            query.setParameter("name", product.getName());
            query.setParameter("categoryID", product.getCategoryID());
            query.setParameter("price", product.getPrice());
            query.setParameter("description", product.getDescription());
            query.setParameter("image", product.getImage());
            query.setParameter("quantity", product.getQuantity());
            query.setParameter("imagePath", product.getImagePath());
            query.setParameter("id", product.getId());
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
        System.out.println("A product has been added");
    }
}
