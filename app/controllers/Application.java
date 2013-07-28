package controllers;

import models.Member;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

    public static Result index() {
        session("lang", "fr");
        return ok(index.render(getUser()));
    }

    private static Member getUser() {
        return (Member.find.where().eq("email", session("email")).findUnique());
    }

    public static void onLogin(String email) {
        session("email", email);
    }

    public static void onLogout() {
        session().remove("email");
    }

}
