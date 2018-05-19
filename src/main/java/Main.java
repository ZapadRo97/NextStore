import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

import app.util.*;
import app.user.*;
import app.login.*;
import app.index.*;
import app.product.*;

public class Main {
    public static void main(String[] args) {
        //TODO: group by functionality
        //get("/hello", (req, res) -> "Hello World");

        //app.user.UserController controller = new app.user.UserController();
        //controller.addUser("Matei", "Ioan", "matio", "mation@mat.io", "12345", "0734");
        //controller.deleteUser(3);
        //ArrayList<app.user.User> au = controller.getUsersList();
        //System.out.println("Number of users: " + au.size());
        //System.out.println(au.toString());

        //Map map = new HashMap();
        //map.put("name", "Sam");

        // hello.mustache file is in resources/templates directory
        //get("/hello", (rq, rs) -> new ModelAndView(map, "hello"), new ThymeleafTemplateEngine());


        // Configure Spark
        port(4567);
        staticFiles.location("/public");
        staticFiles.expireTime(600L);
        enableDebugScreen();

        // Set up before-filters (called before each get/post)
        before("*",                  Filters.addTrailingSlashes);

        // Set up routes
        //get(Path.Web.INDEX,          IndexController.serveIndexPage);
        //get(Path.Web.BOOKS,          BookController.fetchAllBooks);
        //get(Path.Web.ONE_BOOK,       BookController.fetchOneBook);
        get("/login/",          LoginController.serveLoginPage);
        //post(Path.Web.LOGIN,         LoginController.handleLoginPost);
        //post(Path.Web.LOGOUT,        LoginController.handleLogoutPost);
        //get("*",                     ViewUtil.notFound);

        //Set up after-filters (called after each get/post)
        after("*",                   Filters.addGzipHeader);

    }
}
