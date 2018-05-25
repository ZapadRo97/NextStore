package app.dashboard;

import app.product.Category;
import app.product.CategoryController;
import app.product.Product;
import app.product.ProductController;
import app.user.User;
import app.user.UserController;
import app.util.RequestUtil;
import app.util.ViewUtil;
import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.mindrot.jbcrypt.BCrypt;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.MultipartConfigElement;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.util.RequestUtil.*;

public class DashboardController {
    public static Route serveAdminPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        RequestUtil.checkAdmin(request, response);
        model.put("currentUser", request.session().attribute("currentUser"));
        return ViewUtil.render(request, model, "dashboard");
    };

    public static Route serveUserManagementPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        RequestUtil.checkAdmin(request, response);
        model.put("currentUser", request.session().attribute("currentUser"));

        UserController uc = new UserController();
        ArrayList<User> users = uc.getUsersList();
        //maybe check users
        //maybe
        ArrayList<String> editLinks = new ArrayList<>();
        ArrayList<String> deleteLinks = new ArrayList<>();
        for(User user : users)
        {
            editLinks.add("<a class=\"btn btn-success\" href=\"/dashboard/users/" + user.getId() +"/\">Edit</a>");
            deleteLinks.add("<a class=\"btn btn-danger\" href=\"/dashboard/deleteu/" + user.getId() + "/\" onclick=\"return confirm('Are you sure you want to delete this user?');\">Delete</a>");
        }

        model.put("users", users);
        model.put("editLinks", editLinks);
        model.put("deleteLinks", deleteLinks);
        return ViewUtil.render(request, model, "user-management");
    };
    public static Route serveUserEditPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        RequestUtil.checkAdmin(request, response);
        model.put("currentUser", request.session().attribute("currentUser"));
        model.put("uid", getParamUserID(request));

        UserController uc = new UserController();
        User us = uc.getUserByID(Integer.parseInt(getParamUserID(request)));
        if(us == null)
            response.redirect("/dashboard/user-management/");

        model.put("user", us);
        return ViewUtil.render(request, model, "user-edit");
    };
    public static Route serveNewUserPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        RequestUtil.checkAdmin(request, response);
        model.put("currentUser", request.session().attribute("currentUser"));

        return ViewUtil.render(request, model, "new-user");
    };
    public static Route handleNewUser = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        UserController uc = new UserController();
        String pwSalt = BCrypt.gensalt(10);
        String pwHash = BCrypt.hashpw(getQueryPassword(request), pwSalt);
        if (uc.addUser(getQueryFirstName(request), getQueryLastName(request),
                getQueryUsername(request), getQueryEmail(request),
                pwHash, getQueryPhoneNumber(request), pwSalt) != null) {

            response.redirect("/dashboard/user-management/");
        }
        return null;
    };
    public static Route handleDeleteUser = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        RequestUtil.checkAdmin(request, response);
        //model.put("currentUser", request.session().attribute("currentUser"));
        //model.put("uid", getParamUserID(request));

        //delete
        UserController uc = new UserController();
        uc.deleteUser(Integer.parseInt(getParamUserID(request)));

        response.redirect("/dashboard/user-management/");
        return null;

    };
    public static Route finishEditUser = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        UserController uc = new UserController();

        User us = uc.getUserByID(Integer.parseInt(getParamUserID(request)));
        if(us == null)
            response.redirect("/dashboard/user-management/");

        String queryPassword = getQueryPassword(request);
        String pwSalt = us.getSalt();
        String pwHash = us.getHashedPassword();
        if(!us.getHashedPassword().equals(queryPassword))
        {
            pwSalt = BCrypt.gensalt(10);
            pwHash = BCrypt.hashpw(queryPassword, pwSalt);
        }

        uc.changeUser(Integer.parseInt(getParamUserID(request)), getQueryFirstName(request), getQueryLastName(request),
                getQueryUsername(request), getQueryEmail(request),
                pwHash, getQueryPhoneNumber(request), pwSalt);

        response.redirect("/dashboard/user-management/");

        return null;
    };

    public static Route serveProductManagementPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        RequestUtil.checkAdmin(request, response);
        model.put("currentUser", request.session().attribute("currentUser"));
        ProductController pc = new ProductController();
        ArrayList<Product> products = pc.getProductsList(0);
        model.put("products", products);
        CategoryController cc = new CategoryController();
        ArrayList<Category> categories = cc.getCategoryList();
        model.put("categories", categories);

        ArrayList<String> editLinks = new ArrayList<>();
        ArrayList<String> deleteLinks = new ArrayList<>();
        //get all images from database that doesn't exists locally
        for(Product product : products)
        {
            String filePath = "src/main/resources/public/temp-images/" + product.getImagePath();
            File f = new File(filePath);
            if(!f.exists() || f.isDirectory())
            {
                OutputStream out = null;
                byte [] data = product.getImage().getBytes( 1, ( int ) product.getImage().length() );
                try {
                    out = new BufferedOutputStream(new FileOutputStream(filePath));
                    out.write(data);
                } finally {
                    if (out != null) out.close();
                }
            }

            editLinks.add("<a class=\"btn btn-success\" href=\"/dashboard/products/" + product.getId() +"/\">Edit</a>");
            deleteLinks.add("<a class=\"btn btn-danger\" href=\"/dashboard/deletep/" + product.getId() + "/\" onclick=\"return confirm('Are you sure you want to delete this user?');\">Delete</a>");
        }

        model.put("editLinks", editLinks);
        model.put("deleteLinks", deleteLinks);
        return ViewUtil.render(request, model, "product-management");
    };
    public static Route serveNewProductPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        RequestUtil.checkAdmin(request, response);
        model.put("currentUser", request.session().attribute("currentUser"));

        CategoryController cc = new CategoryController();
        ArrayList<Category> categories = cc.getCategoryList();
        model.put("categories", categories);

        return ViewUtil.render(request, model, "new-product");
    };
    public static Route handleNewProduct = (Request request, Response response) -> {
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        Map<String, Object> model = new HashMap<>();
        RequestUtil.checkAdmin(request, response);
        model.put("currentUser", request.session().attribute("currentUser"));

        Product product = new Product();
        product.setName(request.queryParams("name"));
        product.setDescription(request.queryParams("description"));
        product.setCategoryID(Integer.parseInt(request.queryParams("category")));
        product.setQuantity(Integer.parseInt(request.queryParams("stock")));
        product.setPrice(new BigDecimal(request.queryParams("price")));


        ProductController pc = new ProductController();
        try (InputStream is = request.raw().getPart("image").getInputStream()) {
            // Use the input stream to create a file
            Blob blob = Hibernate.getLobCreator(pc.getSession()).createBlob(IOUtils.toByteArray(is));
            product.setImage(blob);
        }
        product.setImagePath(getFileName(request.raw().getPart("image")));
        if(pc.addProduct(product) != null)
            response.redirect("/dashboard/product-management/");

        return null;
    };
    public static Route serveProductEditPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        RequestUtil.checkAdmin(request, response);
        model.put("currentUser", request.session().attribute("currentUser"));
        model.put("pid", getParamProductID(request));

        CategoryController cc = new CategoryController();
        ArrayList<Category> categories = cc.getCategoryList();
        model.put("categories", categories);

        ProductController pc = new ProductController();
        Product pr = pc.getProductByID(Integer.parseInt(getParamProductID(request)));
        if(pr == null)
            response.redirect("/dashboard/product-management/");

        model.put("product", pr);
        return ViewUtil.render(request, model, "product-edit");
    };
    public static Route finishEditProduct = (Request request, Response response) -> {
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        Map<String, Object> model = new HashMap<>();
        ProductController pc = new ProductController();

        Product pr = pc.getProductByID(Integer.parseInt(getParamProductID(request)));
        if(pr == null)
            response.redirect("/dashboard/product-management/");

        if(!getFileName(request.raw().getPart("image")).isEmpty())
        {
            try (InputStream is = request.raw().getPart("image").getInputStream()) {
                // Use the input stream to create a file
                Blob blob = Hibernate.getLobCreator(pc.getSession()).createBlob(IOUtils.toByteArray(is));
                pr.setImage(blob);
            }
            pr.setImagePath(getFileName(request.raw().getPart("image")));
        }
        pr.setName(request.queryParams("name"));
        pr.setDescription(request.queryParams("description"));
        pr.setCategoryID(Integer.parseInt(request.queryParams("category")));
        pr.setQuantity(Integer.parseInt(request.queryParams("stock")));
        pr.setPrice(new BigDecimal(request.queryParams("price")));

        pc.changeProduct(pr);
        response.redirect("/dashboard/product-management/");


        return null;
    };
    public static Route handleDeleteProduct = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        RequestUtil.checkAdmin(request, response);
        ProductController pc = new ProductController();
        pc.deleteProduct(Integer.parseInt(getParamProductID(request)));

        response.redirect("/dashboard/product-management/");
        return null;
    };

    public static Route serveReportCategory = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        RequestUtil.checkAdmin(request, response);
        model.put("currentUser", request.session().attribute("currentUser"));

        CategoryController cc = new CategoryController();
        ArrayList<Category> categories = cc.getCategoryList();
        model.put("categories", categories);

        return ViewUtil.render(request, model, "report-category");
    };
}
