package app.index;

import spark.*;
import java.util.*;
import app.util.*;



public class IndexController {
    public static Route serveIndexPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        //model.put("users", userDao.getAllUserNames());
        //model.put("book", bookDao.getRandomBook());
        String currentUser = request.session().attribute("currentUser");
        if(currentUser == null)
            response.redirect("/login/");
        else
        {
            model.put("currentUser", request.session().attribute("currentUser"));
            return ViewUtil.render(request, model, "index");
        }
        return null;
    };
}
