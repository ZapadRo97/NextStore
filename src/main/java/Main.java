import app.dashboard.DashboardController;
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


        // Configure Spark
        port(4567);
        //staticFiles.location("/public");
        staticFileLocation("/public");
        staticFiles.expireTime(600L);
        enableDebugScreen();

        // Set up before-filters (called before each get/post)
        before("*",                  Filters.addTrailingSlashes);

        // Set up routes
        get("/index/",          IndexController.serveIndexPage);
        get("/",                 IndexController.serveIndexPage);
        get("/dashboard/",         DashboardController.serveAdminPage);
        get("/dashboard/user-management/",         DashboardController.serveUserManagementPage);
        get("/dashboard/product-management/",      DashboardController.serveAdminPage);
        get("/dashboard/users/0/",            DashboardController.serveNewUserPage);
        post("/dashboard/users/0/",           DashboardController.handleNewUser);
        get("/dashboard/users/:uid/",           DashboardController.serveUserEditPage);
        post("/dashboard/users/:uid/",           DashboardController.finishEditUser);
        get("/dashboard/deleteu/:uid/",         DashboardController.handleDeleteUser);
        //get(Path.Web.BOOKS,          BookController.fetchAllBooks);
        //get(Path.Web.ONE_BOOK,       BookController.fetchOneBook);
        get("/login/",          LoginController.serveLoginPage);
        get("/signup/",         LoginController.serveSignUpPage);
        post("/signup/",         LoginController.handleSignUpPost);
        post("/login/",          LoginController.handleLoginPost);

        get("/logout/",        LoginController.handleLogout);
        //get("*",                     ViewUtil.notFound);

        //Map map = new HashMap();
        //map.put("name", "Sam");
        //get("/test/", (rq, rs) -> new ModelAndView(map, "login"), new ThymeleafTemplateEngine());




        //Set up after-filters (called after each get/post)
        after("*",                   Filters.addGzipHeader);

    }
}
