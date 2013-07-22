package controllers;

import java.util.ArrayList;

import models.Member;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import tools.StringUtil;

public class Register extends Controller {

    private final static Form<RegisterModel> registerForm = new Form<RegisterModel>(RegisterModel.class);

    public static Result index() {
        return ok(views.html.register.render(registerForm));
    }

    public static class RegisterModel {

        public String email;
        public String firstName;
        public String lastName;
        public String password;

        public String validate() {
            ArrayList<String> errors = new ArrayList<String>();
            if (StringUtil.isEmpty(email)) {
                return "email is required";
            }

            if (Member.find.where().eq("email", email).findUnique() != null) {
                return "email already used";
            }

            return null;
        }
    }

    public static Result add() {
        Form<RegisterModel> form = registerForm.bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(views.html.register.render(form));
        } else {
            Member.create(form.get().email, form.get().firstName, form.get().lastName, form.get().password);
            flash("success", "The member has been created");
            session().clear();
            session("email", form.get().email);
            return redirect(routes.Register.index());
        }
    }
}
