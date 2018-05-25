package app.basket;

import app.product.Product;
import app.product.ProductController;
import app.user.User;
import app.user.UserController;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static app.util.RequestUtil.getParamProductID;
import static app.util.RequestUtil.getParamUserID;
import static app.util.RequestUtil.getQueryUsername;

public class BasketController {
    private static HashMap<Integer, Basket> baskets = new HashMap<>();

    public static void createBasket(Integer userID)
    {
        Basket basket = null;
        if(!baskets.containsKey(userID))
            basket = new Basket(userID, new HashMap<>());

        baskets.put(userID, basket);
    }

    public static void addProduct(Integer userID, Product product)
    {
        if(baskets.get(userID).getProducts() == null)
        {
            HashMap<Product, Integer> products = new HashMap<>();
            baskets.get(userID).setProducts(products);
        }

        if(baskets.get(userID).getProducts().containsKey(product))
        {
            Integer numOfProducts = baskets.get(userID).getProducts().get(product);
            baskets.get(userID).getProducts().put(product, ++numOfProducts);
        }
        else
            baskets.get(userID).getProducts().put(product, 1);
    }

    public static void deleteProduct(Integer userID, Product product)
    {
        if(baskets.get(userID).getProducts() == null)
            return;

        if(!baskets.get(userID).getProducts().containsKey(product))
            return;

        baskets.get(userID).getProducts().remove(product);
    }

    public static void emptyBasket(Integer userID)
    {
        baskets.get(userID).getProducts().clear();
    }

    public static void deleteBasket(Integer userID)
    {
        baskets.remove(userID);
    }

    public static ArrayList<Product> getProducts(Integer userID)
    {
        Basket basket = baskets.get(userID);
        ArrayList<Product> products = new ArrayList<>();
        for(Map.Entry<Product, Integer> entry : basket.getProducts().entrySet())
        {
            products.add(entry.getKey());
        }
        return products;
    }

    public static ArrayList<Integer> getQuantities(Integer userID)
    {
        Basket basket = baskets.get(userID);
        ArrayList<Integer> quantities = new ArrayList<>();
        for(Map.Entry<Product, Integer> entry : basket.getProducts().entrySet())
        {
            quantities.add(entry.getValue());
        }
        return quantities;
    }

    //spark routes
    public static Route addProductToBasket = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        String currentUser = request.session().attribute("currentUser");
        UserController uc = new UserController();
        User us = uc.getUserByUsername(currentUser);

        Integer productID = Integer.parseInt(getParamProductID(request));
        ProductController pc = new ProductController();
        Product pr = pc.getProductByID(productID);

        BasketController.addProduct(us.getId(), pr);

        response.redirect("/index/all/");

        return null;
    };

    public static Route removeProduct = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        String currentUser = request.session().attribute("currentUser");

        if(currentUser == null)
            return null;

        UserController uc = new UserController();
        User us = uc.getUserByUsername(currentUser);
        Integer productID = Integer.parseInt(getParamProductID(request));
        ProductController pc = new ProductController();
        Product pr = pc.getProductByID(productID);

        BasketController.deleteProduct(us.getId(), pr);

        response.redirect("/index/shopping-cart/");

        return null;
    };

}
