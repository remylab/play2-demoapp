package interceptors;

import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

public class Dummy extends Action.Simple {

    @Override
    public Result call(Context ctx) throws Throwable {
        ctx.args.put("test", "hello dummy interceptor");

        System.out.println("Calling action for " + ctx);
        return delegate.call(ctx);
    }
}
