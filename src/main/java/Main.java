import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        //TODO: create folders for Model, View, Controller
        //get("/hello", (req, res) -> "Hello World");

        //Controller controller = new Controller();
        //controller.addUser("Matei", "Ioan", "matio", "mation@mat.io", "12345", "0734");
        //controller.deleteUser(3);
        //ArrayList<User> au = controller.getUsersList();
        //System.out.println("Number of users: " + au.size());
        //System.out.println(au.toString());

        Map map = new HashMap();
        map.put("name", "Sam");

        // hello.mustache file is in resources/templates directory
        get("/hello", (rq, rs) -> new ModelAndView(map, "hello.mustache"), new MustacheTemplateEngine());
    }
}
