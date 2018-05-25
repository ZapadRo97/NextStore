package app.login;

import app.basket.BasketController;
import app.user.*;
import app.util.*;
import org.mindrot.jbcrypt.BCrypt;
import spark.*;
import java.util.*;

import static app.util.RequestUtil.*;

public class LoginController {

    public static Route serveLoginPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();

        if(request.session().attribute("loggedIn") != null && request.session().attribute("loggedIn").equals(true))
            response.redirect("/index/all/");

        model.put("authenticationFailed", false);
        return ViewUtil.render(request, model, "login");
    };

    public static Route serveSignUpPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();

        if(request.session().attribute("loggedIn") != null && request.session().attribute("loggedIn").equals(true))
            response.redirect("/index/all/");


        return ViewUtil.render(request, model, "signup");
    };

    public static Route handleLoginPost = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        UserController uc = new UserController();
        if (!uc.authenticate(getQueryUsername(request), getQueryPassword(request))) {
            model.put("authenticationFailed", true);
            return ViewUtil.render(request, model, "login");
        }


        User us = uc.getUserByUsername(getQueryUsername(request));
        BasketController.createBasket(us.getId());
        //request.session().attribute("currentUser", getQueryUsername(request));
        //if (getQueryLoginRedirect(request) != null) {
        //    response.redirect(getQueryLoginRedirect(request));
        //}


        //model.put("authenticationSucceeded", true);
        request.session(true);
        request.session().attribute("loggedIn", true);
        request.session().attribute("currentUser", getQueryUsername(request));

        if(uc.getUserByUsername(getQueryUsername(request)).getAccessLevel() == 1)
        {
            request.session().attribute("isAdmin", true);
            response.redirect("/dashboard/");
        }
        else
        {
            request.session().attribute("isAdmin", false);
            response.redirect("/index/all/");
        }

        return null;
    };

    public static Route handleLogout = (Request request, Response response) -> {

        UserController uc = new UserController();
        User us = uc.getUserByUsername(request.session().attribute("currentUser"));
        BasketController.deleteBasket(us.getId());

        request.session().removeAttribute("currentUser");
        request.session().attribute("loggedIn", false);
        response.redirect("/login/");
        return null;
    };

    public static Route handleSignUpPost = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        UserController uc = new UserController();
        String pwSalt = BCrypt.gensalt(10);
        String pwHash = BCrypt.hashpw(getQueryPassword(request), pwSalt);
        if (uc.addUser(getQueryFirstName(request), getQueryLastName(request),
                getQueryUsername(request), getQueryEmail(request),
                pwHash, getQueryPhoneNumber(request), pwSalt) != null) {

            response.redirect("/login/");
        }
        else
        {
            model.put("userCreated", false);
            return ViewUtil.render(request, model, "signup");
        }
        return null;
    };
}
