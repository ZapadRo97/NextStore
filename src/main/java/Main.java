import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        //get("/hello", (req, res) -> "Hello World");

        Controller controller = new Controller();
        controller.addUser("Mate", "Ioan", "matio", "mation@mat.io", "12345", "0734");
    }
}
