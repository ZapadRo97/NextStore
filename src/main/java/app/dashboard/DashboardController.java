package app.dashboard;

import app.user.User;
import app.user.UserController;
import app.util.RequestUtil;
import app.util.ViewUtil;
import org.mindrot.jbcrypt.BCrypt;
import spark.Request;
import spark.Response;
import spark.Route;

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

}
