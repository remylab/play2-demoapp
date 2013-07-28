package controllers;

import models.Member;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class Login extends Controller {

    public final static Form<LoginModel> loginForm = new Form<LoginModel>(LoginModel.class);

    public static class LoginModel {

        public String email;
        public String password;

    }

    public static Result authenticate() {
        Form<LoginModel> form = loginForm.bindFromRequest();

        if (Member.authenticate(form.get().email, form.get().password) == null) {
            flash("errorLogin", "");
        } else {
            Application.onLogin(form.get().email);
        }
        return redirect(routes.Application.index());
    }
}
