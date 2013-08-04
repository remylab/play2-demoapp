package controllers;

import models.Member;

import org.apache.commons.lang3.StringUtils;

import play.data.Form;
import play.mvc.Result;
import tools.StringUtil;
import controllers.Membership.LoginModel;
import controllers.Membership.RegisterModel;

public class Membership extends ControllerExtended {

    private final static Form<RegisterModel> registerForm = new Form<RegisterModel>(RegisterModel.class);
    public final static Form<LoginModel> loginForm = new Form<LoginModel>(LoginModel.class);

    public static class RegisterModel {

        public String email;
        public String firstName;
        public String lastName;
        public String password;
        public String passwordval;
    }

    public static class LoginModel {

        public String email;
        public String password;

    }

    public static Result register() {
        return ok(views.html.register.render(registerForm));
    }

    public static Result confirmation(String email, String token) {
        if (Member.confirmToken(email, token) != null) {
            flash("confirmationSuccess", "");
            Application.onLogin(email);
        }
        return redirect(routes.Application.index());
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

    public static Result logout() {
        Application.onLogout();
        return redirect(routes.Application.index());
    }

    public static Result addMember() {
        Form<RegisterModel> form = registerForm.bindFromRequest();

        if (StringUtil.isEmpty(form.get().email)) {
            form.reject("email", message("register.form.required"));
        } else if (Member.find.where().eq("email", form.get().email).findUnique() != null) {
            form.reject("email", message("register.form.email.notavailable"));
        }

        if (StringUtil.isEmpty(form.get().password)) {
            form.reject("firstName", message("register.form.required"));
        }
        if (StringUtil.isEmpty(form.get().password)) {
            form.reject("lastName", message("register.form.required"));
        }
        if (StringUtil.isEmpty(form.get().password)) {
            form.reject("password", message("register.form.required"));
        }
        if (StringUtil.isEmpty(form.get().passwordval)) {
            form.reject("passwordval", message("register.form.required"));
        }

        if (!StringUtil.isEmpty(form.get().password) && !StringUtil.isEmpty(form.get().passwordval) && !StringUtils.trimToEmpty(form.get().password).equals(form.get().passwordval)) {
            form.reject("passwordval", message("register.form.passwordval"));
        }

        if (form.hasErrors()) {
            form.reject(message("register.form.invalid"));
            return badRequest(views.html.register.render(form));
        } else {
            Member newMember = Member.create(form.get().email, form.get().firstName, form.get().lastName, form.get().password);
            flash("emailConfirmation", message("register.form.emailConfirmation", "http://" + request().host() + "/confirmation/" + newMember.email + "/" + newMember.confirmationToken));
            return redirect(routes.Membership.register());
        }
    }
}