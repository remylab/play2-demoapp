package tools;

import interceptors.BaseAction;

import java.util.List;

import models.MGroup;
import models.Member;
import play.mvc.Http;

public class Chelper {

    /**
     * How to use in the view :
     * 
     * @import tools._
     * 
     * @for(group <- CHelper.groups() ) { @group.name }
     * 
     */
    public static List<MGroup> groups() {
        return (List<MGroup>) Http.Context.current().args.get(BaseAction.GROUPS);
    }

    public static Member member() {
        return (Member) Http.Context.current().args.get(BaseAction.MEMBER);
    }

    public static boolean getBool(String key) {
        Object val = Http.Context.current().args.get(key);
        if (val != null) {
            return (Boolean) Http.Context.current().args.get(key);
        }
        return false;
    }

    public static void setBool(String key, Boolean value) {
        Http.Context.current().args.put(key, value);
    }
}
