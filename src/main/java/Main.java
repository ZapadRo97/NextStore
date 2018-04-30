import java.util.ArrayList;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        //TODO: create folders for Model, View, Controller
        //get("/hello", (req, res) -> "Hello World");

        Controller controller = new Controller();
        //controller.addUser("Matei", "Ioan", "matio", "mation@mat.io", "12345", "0734");
        //ArrayList<User> au = controller.getUsersList();
        //System.out.println("Number of users: " + au.size());
        //System.out.println(au.toString());
    }
}
