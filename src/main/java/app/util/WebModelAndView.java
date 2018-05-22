package app.util;

import spark.*;

public class WebModelAndView extends ModelAndView {

    private Request request;
    private Response response;

    public WebModelAndView(Object model, String viewName, Request request, Response response) {
        super(model, viewName);
        this.request = request;
        this.response = response;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }
}