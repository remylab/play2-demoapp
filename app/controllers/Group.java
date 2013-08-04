package controllers;

import java.util.ArrayList;
import java.util.HashMap;

import models.Invitation;
import models.MGroup;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import tools.StringUtil;

public class Group extends Controller {
    private static final DynamicForm dynForm = play.data.Form.form();

    public static Result add() {
        ArrayList<String> errors = new ArrayList<String>();

        String email = dynForm.bindFromRequest().get("email");
        String name = dynForm.bindFromRequest().get("name");
        String sInvitations = dynForm.bindFromRequest().get("invitations");

        String[] invitations = sInvitations.split(" ");

        if (StringUtil.isEmpty(email)) {
            errors.add("no email");
        }
        if (StringUtil.isEmpty(name)) {
            errors.add("no name");
        }

        if (errors.size() > 0) {
            return badRequest();
        } else {

            HashMap<String, String> emailsMap = new HashMap<String, String>();

            MGroup group = MGroup.create(name, email);
            for (String invEmail : invitations) {
                Invitation invit = Invitation.create(group.id, invEmail);
                if (invit != null) {
                    emailsMap.put(invEmail, "http://" + request().host() + "/joingroup/" + group.id + "/" + invit.confirmationToken);
                }
            }

            return ok(views.html.ajax.group.render(name, emailsMap));
        }
    }
}
