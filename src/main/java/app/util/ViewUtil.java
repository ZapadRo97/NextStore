package app.util;

import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import org.eclipse.jetty.http.*;
import spark.*;
import java.util.*;
import static app.util.RequestUtil.*;

public class ViewUtil {

    public static String render(Request request, Map<String, Object> model, String templatePath) {
        //model.put("currentUser", getSessionCurrentUser(request));
        //model.put("name", "SAM");
        ThymeleafTemplateEngine engine = new ThymeleafTemplateEngine();
        return engine.render(new ModelAndView(model, templatePath));
    }
}
