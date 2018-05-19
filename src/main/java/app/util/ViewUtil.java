package app.util;

import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import org.eclipse.jetty.http.*;
import spark.*;
import java.util.*;
import static app.util.RequestUtil.*;

public class ViewUtil {

    public static String render(Request request, Map<String, Object> model, String templatePath) {
        //model.put("msg", new MessageBundle(getSessionLocale(request)));
        model.put("currentUser", getSessionCurrentUser(request));
        //model.put("WebPath", Path.Web.class); // Access application URLs from templates
        ThymeleafTemplateEngine engine = new ThymeleafTemplateEngine();
        return engine.render(new ModelAndView(model, templatePath));
    }
}
