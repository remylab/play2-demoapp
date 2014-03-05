package tools;

import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

public class MailUtil {

    public static void sendMailHtml(String subject, String recipient, String from, String html) {
        MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
        mail.setSubject(subject);
        mail.addRecipient(recipient);
        mail.addFrom(from);
        mail.sendHtml(html);
    }
}
