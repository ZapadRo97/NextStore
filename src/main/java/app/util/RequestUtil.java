package app.util;


import spark.*;

import javax.servlet.http.Part;

public class RequestUtil {

    public static boolean removeSessionAttrLoggedOut(Request request) {
        Object loggedOut = request.session().attribute("loggedOut");
        request.session().removeAttribute("loggedOut");
        return loggedOut != null;
    }

    public static String removeSessionAttrLoginRedirect(Request request) {
        String loginRedirect = request.session().attribute("loginRedirect");
        request.session().removeAttribute("loginRedirect");
        return loginRedirect;
    }

    public static String getSessionCurrentUser(Request request) {
        return request.session().attribute("currentUser");
    }

    public static String getQueryUsername(Request request) {
        return request.queryParams("username");
    }

    public static String getQueryPassword(Request request) {
        return request.queryParams("password");
    }
    public static String getQueryLastName(Request request) {
        return request.queryParams("lastname");
    }
    public static String getQueryFirstName(Request request) {
        return request.queryParams("firstname");
    }
    public static String getQueryEmail(Request request) {
        return request.queryParams("email");
    }
    public static String getQueryPhoneNumber(Request request) {
        return request.queryParams("phonenumber");
    }
    public static void checkAdmin(Request request, Response response)
    {
        if(request.session().attribute("loggedIn") == null || !(Boolean)request.session().attribute("loggedIn"))
            response.redirect("/login/");
        else if(request.session().attribute("isAdmin") == null || !(Boolean)request.session().attribute("isAdmin"))
            response.redirect("/index/");
    }

    public static String getParamUserID(Request request) {
        return request.params("uid");
    }

    public static String getParamProductID(Request request) {
        return request.params("pid");
    }

    public static String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

}
