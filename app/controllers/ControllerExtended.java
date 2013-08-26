package controllers;

import interceptors.BaseAction;
import play.mvc.Controller;
import play.mvc.With;
import tools.LanguageUtil;

@With(BaseAction.class)
public class ControllerExtended extends Controller {

    protected static String message(String key, Object... args) {
        return LanguageUtil.message(session(), key, args);
    }

}
