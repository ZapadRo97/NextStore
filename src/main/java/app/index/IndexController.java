package app.index;

import app.basket.BasketController;
import app.product.Category;
import app.product.CategoryController;
import app.product.Product;
import app.product.ProductController;
import app.user.User;
import app.user.UserController;
import spark.*;

import java.math.BigDecimal;
import java.util.*;
import app.util.*;

import static app.util.RequestUtil.getParamCategID;
import static app.util.RequestUtil.getProductsListOfLists;
import static app.util.RequestUtil.getProductsListOfListsSearch;


public class IndexController {
    public static Route serveIndexPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        String currentUser = request.session().attribute("currentUser");
        CategoryController cc = new CategoryController();
        ArrayList<Category> categories = cc.getCategoryList();
        model.put("categories", categories);

        ArrayList<String> categLinks = new ArrayList<>();
        for(Category category : categories)
        {
            categLinks.add("<a href='/index/" + category.getId() + "'>" +category.getName() + "</a>");
        }
        model.put("categLinks", categLinks);

        ArrayList<ArrayList<Product>> splitProducts = getProductsListOfLists(0);
        model.put("products", splitProducts);
        if(currentUser == null)
            response.redirect("/login/");
        else
        {
            model.put("currentUser", request.session().attribute("currentUser"));
            return ViewUtil.render(request, model, "index");
        }


        return null;
    };

    public static Route serveCategPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        String currentUser = request.session().attribute("currentUser");
        CategoryController cc = new CategoryController();
        ArrayList<Category> categories = cc.getCategoryList();
        model.put("categories", categories);

        ArrayList<String> categLinks = new ArrayList<>();
        for(Category category : categories)
        {
            categLinks.add("<a href='/index/" + category.getId() + "'>" +category.getName() + "</a>");
        }
        model.put("categLinks", categLinks);
        model.put("cid", getParamCategID(request));

        int paramCategID = Integer.parseInt(getParamCategID(request));

        for(Category category : categories)
        {
            if(category.getId() == paramCategID)
            {
                model.put("categ", category.getName());
                break;
            }
        }

        ArrayList<ArrayList<Product>> splitProducts = getProductsListOfLists(paramCategID);
        model.put("products", splitProducts);

        if(model.get("categ")== null)
            response.redirect("/index/all/");

        if(currentUser == null)
            response.redirect("/login/");
        else
        {
            model.put("currentUser", request.session().attribute("currentUser"));
            return ViewUtil.render(request, model, "categ-view");
        }
        return null;
    };

    public static Route getSearchedProduct = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        String currentUser = request.session().attribute("currentUser");
        CategoryController cc = new CategoryController();
        ArrayList<Category> categories = cc.getCategoryList();
        model.put("categories", categories);

        ArrayList<String> categLinks = new ArrayList<>();
        for(Category category : categories)
        {
            categLinks.add("<a href='/index/" + category.getId() + "'>" +category.getName() + "</a>");
        }
        model.put("categLinks", categLinks);

        ArrayList<ArrayList<Product>> splitProducts = getProductsListOfListsSearch(request.queryParams("product-name"));
        model.put("products", splitProducts);
        if(currentUser == null)
            response.redirect("/login/");
        else
        {
            model.put("currentUser", request.session().attribute("currentUser"));
            return ViewUtil.render(request, model, "index");
        }
        return null;
    };

    public static Route serveRedirect = (Request request, Response response) -> {
        response.redirect("/index/all/");
        return null;
    };

    public static Route serveShoppingCartPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        String currentUser = request.session().attribute("currentUser");
        model.put("currentUser", currentUser);
        if(currentUser == null)
            response.redirect("/login/");

        UserController uc = new UserController();
        User ur = uc.getUserByUsername(currentUser);
        Integer userID = ur.getId();

        ArrayList<Product> products = BasketController.getProducts(userID);
        ArrayList<Integer> quantities = BasketController.getQuantities(userID);

        CategoryController cc = new CategoryController();
        ArrayList<Category> categories = cc.getCategoryList();
        model.put("categories", categories);

        model.put("products", products);
        model.put("quantities", quantities);

        BigDecimal totalPrice = new BigDecimal(0);
        for(int index = 0; index < products.size(); index++)
        {
            totalPrice = totalPrice.add(products.get(index).getPrice().multiply(new BigDecimal(quantities.get(index))));
        }

        model.put("totalPrice", totalPrice);

        return ViewUtil.render(request, model, "shopping-cart");
    };

    public static Route buyProducts = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        String currentUser = request.session().attribute("currentUser");
        model.put("currentUser", currentUser);
        if(currentUser == null)
            response.redirect("/login/");

        UserController uc = new UserController();
        User ur = uc.getUserByUsername(currentUser);
        Integer userID = ur.getId();

        CategoryController cc = new CategoryController();
        ArrayList<Category> categories = cc.getCategoryList();
        HashMap<Integer, Category> mapCategories = new HashMap<>();
        for(Category category : categories)
            mapCategories.put(category.getId(), category);

        ProductController pc = new ProductController();

        ArrayList<Product> products = BasketController.getProducts(userID);
        ArrayList<Integer> quantities = BasketController.getQuantities(userID);

        for(int index = 0; index < products.size(); index++)
        {
            int oldQuantity = products.get(index).getQuantity();
            products.get(index).setQuantity(oldQuantity - quantities.get(index));
            int soldItems = mapCategories.get(products.get(index).getCategoryID()).getNumberOfSoldProducts();
            mapCategories.get(products.get(index).getCategoryID()).setNumberOfSoldProducts(soldItems + quantities.get(index));
            pc.changeProduct(products.get(index));
        }

        for(Category category : categories)
            cc.sellProductFromCategory(category.getId(), category.getNumberOfSoldProducts());



        BasketController.emptyBasket(userID);

        response.redirect("/index/all/");

        return null;
    };

}
