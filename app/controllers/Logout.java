package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class Logout extends Controller {

    public static Result logout() {
        Application.onLogout();
        return redirect(routes.Application.index());
    }
}
