package controllers;

import java.util.ArrayList;
import java.util.HashMap;

import models.Invitation;
import models.MGroup;
import models.Member;

import org.apache.commons.lang3.StringUtils;

import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import tools.MailUtil;
import tools.StringUtil;

public class Group extends ControllerExtended {
    public static final String INVITATION_ROUTE = "/group/invitation";

    private static final DynamicForm dynForm = play.data.Form.form();

    private final static Form<AcceptModel> acceptForm = new Form<AcceptModel>(AcceptModel.class);

    public static class AcceptModel {

        public Long groupId;
        public String email;
        public String firstName;
        public String lastName;
        public String password;
        public String passwordval;
        public String newMember;
        public String token;
    }

    /**
     * Process an invitation acceptance from existing or new member
     * 
     * @return
     */
    public static Result accept() {
        Form<AcceptModel> form = acceptForm.bindFromRequest();

        boolean createNewMember = false;
        Member existingMember = null;
        MGroup group = MGroup.find.ref(form.get().groupId);

        if (group == null) {
            return redirect(routes.Application.index());
        } else {

            Invitation invit = Invitation.findInvitationByToken(group.id, form.get().token);

            if (invit == null) {
                return redirect(routes.Application.index());
            }

            if (StringUtil.isEmpty(form.get().email)) {
                form.reject("email", message("register.form.required"));
            }

            if (!StringUtil.isEmpty(form.get().newMember)) {

                createNewMember = true;

                if (StringUtil.isEmpty(form.get().firstName)) {
                    form.reject("firstName", message("register.form.required"));
                }
                if (StringUtil.isEmpty(form.get().lastName)) {
                    form.reject("lastName", message("register.form.required"));
                }
                if (StringUtil.isEmpty(form.get().passwordval)) {
                    form.reject("passwordval", message("register.form.required"));
                }
                if (!StringUtil.isEmpty(form.get().password) && !StringUtil.isEmpty(form.get().passwordval) && !StringUtils.trimToEmpty(form.get().password).equals(form.get().passwordval)) {
                    form.reject("passwordval", message("register.form.passwordval"));
                }

            } else {
                existingMember = Member.authenticate(form.get().email, form.get().password);
                if (existingMember == null) {
                    form.reject("password", "password invalid");
                } else {
                    existingMember.active = true;
                    existingMember.confirmationToken = null;
                    existingMember.save();
                }
            }

            if (StringUtil.isEmpty(form.get().password)) {
                form.reject("password", message("register.form.required"));
            }

            if (form.hasErrors()) {
                form.reject(message("register.form.invalid"));
                return badRequest(views.html.join.render(invit, existingMember, form));
            } else {

                Member invitedMember = existingMember;

                if (createNewMember) {
                    invitedMember = Member.create(form.get().email, form.get().firstName, form.get().lastName, form.get().password);
                    invitedMember.active = true;
                    invitedMember.confirmationToken = null;
                    invitedMember.save();
                }

                if (invitedMember == null) {
                    form.reject(message("register.form.invalid"));
                    return badRequest(views.html.join.render(invit, existingMember, form));
                }

                MGroup.add(group.id, invitedMember);
                Application.onLogin(invitedMember.email);

                flash("groupSuccess", group.name);
                return redirect(routes.Application.index());
            }
        }
    }

    /**
     * Process a group invitation : existing member will be asked to enter
     * password to accept invitation new member will need to enter registration
     * information
     * 
     * @param id
     * @param token
     * @return
     */
    public static Result invitation(Long id, String token) {

        Invitation invit = Invitation.findInvitationByToken(id, token);
        if (invit != null) {
            Member invited = Member.findByEmail(invit.email);
            return ok(views.html.join.render(invit, invited, acceptForm));
        }
        return redirect(routes.Application.index());
    }

    /**
     * An active member create a group and send invitations to a list of emails
     * System create invitation records and fire email with confirmation URL
     * 
     * @return
     */
    @Security.Authenticated(AjaxSecured.class)
    public static Result show(Long id) {
        ArrayList<String> errors = new ArrayList<String>();

        Member member = Membership.getUser();
        MGroup group = MGroup.getWithMember(id, member);
        if (group == null) {
            errors.add("member not allowed");
        }

        if (errors.size() > 0) {
            return badRequest();
        } else {

            return ok(views.html.ajax.showgroup.render(group.members));
        }
    }

    /**
     * An active member create a group and send invitations to a list of emails
     * System create invitation records and fire email with confirmation URL
     * 
     * @return
     */
    @Security.Authenticated(AjaxSecured.class)
    public static Result add() {
        ArrayList<String> errors = new ArrayList<String>();

        String name = dynForm.bindFromRequest().get("name");
        String sInvitations = dynForm.bindFromRequest().get("invitations");

        String[] invitations = sInvitations.split(" ");

        if (StringUtil.isEmpty(name)) {
            errors.add("no name");
        }

        if (errors.size() > 0) {
            return badRequest();
        } else {
            Member sender = Membership.getUser();
            MGroup group = MGroup.create(name, sender.email);
            return ok(views.html.ajax.newgroup.render(name, sendInvitations(invitations, group, sender)));
        }
    }

    /**
     * An active member send invitations to an existing group to a list of
     * emails System create invitation records and fire email with confirmation
     * URL
     * 
     * @return
     */
    @Security.Authenticated(AjaxSecured.class)
    public static Result invite(Long id) {
        ArrayList<String> errors = new ArrayList<String>();

        Member member = Membership.getUser();
        MGroup group = MGroup.getWithMember(id, member);
        if (group == null) {
            errors.add("member not allowed");
        }

        String sInvitations = dynForm.bindFromRequest().get("invitations");

        String[] invitations = sInvitations.split(" ");

        if (errors.size() > 0) {
            return badRequest();
        } else {

            return ok(views.html.ajax.invitegroup.render(group.name, sendInvitations(invitations, group, member)));
        }
    }

    private static HashMap<String, String> sendInvitations(String[] invitations, MGroup group, Member sender) {

        HashMap<String, String> emailsMap = new HashMap<String, String>();

        for (String invEmail : invitations) {
            Invitation invit = Invitation.create(sender, group.id, invEmail);

            if (invit != null && !(sender.email).equals(invEmail)) {
                String link = "http://" + request().host() + INVITATION_ROUTE + "/" + group.id + "/" + invit.confirmationToken;
                emailsMap.put(invEmail, link);
                String body = views.html.mails.invitation.render(sender, group.name, link).body();
                MailUtil.sendMailHtml("invitation meeblio", invEmail, "noreply@meeblio.com", body);
            }

        }
        return emailsMap;
    }
}
