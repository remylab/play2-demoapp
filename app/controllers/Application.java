package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Item;
import models.MGroup;
import models.Member;
import play.Routes;
import play.mvc.Result;
import views.html.index;

public class Application extends ControllerExtended {

    public static Result index() {
        session("lang", "fr");

        Member member = Membership.getUser();
        List<MGroup> groups = new ArrayList<MGroup>();
        List<Item> items = new ArrayList<Item>();

        if (member != null) {
            groups = MGroup.findInvolving(member);
            if (groups.size() > 0) {
                items = Member.getItems(member, request());
            }
        }
        return ok(index.render(member, groups, items));
    }

    public static void onLogin(String email) {
        session("email", email);
    }

    public static void onLogout() {
        session().remove("email");
    }

    public static Result jsRoutes() {
        response().setContentType("text/javascript");
        return ok(Routes.javascriptRouter("jsRoutes",
                controllers.routes.javascript.Group.add(),
                controllers.routes.javascript.Group.show(),
                controllers.routes.javascript.Group.invite(),
                controllers.routes.javascript.Items.add()
                ));

    }

}
