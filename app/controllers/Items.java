package controllers;

import java.util.ArrayList;

import models.Item;
import models.Member;
import play.data.DynamicForm;
import play.mvc.Result;
import play.mvc.Security;
import tools.StringUtil;

public class Items extends ControllerExtended {

    private static final DynamicForm dynForm = play.data.Form.form();

    @Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(views.html.items.render());
    }

    /**
     * 
     * @return
     */
    @Security.Authenticated(AjaxSecured.class)
    public static Result add() {
        ArrayList<String> errors = new ArrayList<String>();

        String title = dynForm.bindFromRequest().get("title");
        String description = dynForm.bindFromRequest().get("description");

        Member member = Membership.getUser();

        if (StringUtil.isEmpty(title)) {
            errors.add("no title");
        }

        if (errors.size() > 0) {
            return badRequest();
        } else {
            member.items.add(Item.create(title, description));
            return ok();
        }
    }

}
