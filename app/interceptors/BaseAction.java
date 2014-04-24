package interceptors;

import models.MGroup;
import models.Member;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
import controllers.Membership;
import play.mvc.SimpleResult;
import play.libs.F;

public class BaseAction extends Action.Simple {
    
    public static String MEMBER = "BaseAction.member";
    public static String GROUPS = "BaseAction.groups";

    @Override
    public F.Promise<SimpleResult> call(Context ctx) throws Throwable {

        Member member = Membership.getUser();

        ctx.args.put(MEMBER, member);

        if (member != null) {
            ctx.args.put(GROUPS, MGroup.findInvolving(member));
        }
        return delegate.call(ctx);
    }

}
