package controllers;

import models.Member;

import org.apache.commons.lang3.StringUtils;

import play.data.Form;
import play.mvc.Result;
import tools.StringUtil;

public class Register extends ControllerExtended {

    private final static Form<RegisterModel> registerForm = new Form<RegisterModel>(RegisterModel.class);

    public static Result index() {
        return ok(views.html.register.render(registerForm));
    }

    public static class RegisterModel {

        public String email;
        public String firstName;
        public String lastName;
        public String password;
        public String passwordval;
    }

    public static Result add() {
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
            Member.create(form.get().email, form.get().firstName, form.get().lastName, form.get().password);
            flash("success", message("register.form.success", form.get().email));
            Application.onLogin(form.get().email);
            return redirect(routes.Register.index());
        }
    }
}
