package controllers;

import models.Member;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

    public static Result index() {
        session().clear();
        return ok(index.render(getUser()));
    }

    private static Member getUser() {
        return (Member.find.where().eq("email", session("email")).findUnique());
    }

}
