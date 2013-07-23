package controllers;

import play.mvc.Controller;
import tools.LanguageUtil;

public class ControllerExtended extends Controller {

    protected static String message(String key, Object... args) {
        return LanguageUtil.message(session(), key, args);
    }

}
