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
import tools.StringUtil;

public class Group extends ControllerExtended {
    public static final String INVITATION_ROUTE = "/group/invitation";

    private static final DynamicForm dynForm = play.data.Form.form();

    private final static Form<AcceptModel> acceptForm = new Form<AcceptModel>(AcceptModel.class);

    public static class AcceptModel {

        public Long invitationId;
        public Long groupId;
        public String email;
        public String firstName;
        public String lastName;
        public String password;
        public String passwordval;
        public String newMember;
    }

    /**
     * Process an invitation acceptance from existing or new member
     * 
     * @return
     */
    public static Result accept() {
        Form<AcceptModel> form = acceptForm.bindFromRequest();

        boolean createNewMember = false;
        Member newMember = null;
        Member existingMember = null;
        MGroup group = MGroup.find.ref(form.get().groupId);
        Invitation invit = Invitation.find.ref(form.get().invitationId);

        if (group == null) {
            form.reject("groupId", "group not found");
        } else {

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
                existingMember = Member.findByEmail(form.get().email);
                if (Member.authenticate(form.get().email, form.get().password) == null) {
                    form.reject("password", "password invalid");
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
        return badRequest(views.html.index.render(Membership.getUser()));
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
            Member sender = invit.sender;
            Member invited = Member.findByEmail(invit.email);
            return ok(views.html.join.render(invit, invited, acceptForm));
        }
        return badRequest(views.html.index.render(Membership.getUser()));
    }

    /**
     * An active member create a group and send invitations to a list of emails
     * System create invitation records and fire email with confirmation URL
     * 
     * @return
     */
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
            Member sender = Member.findByEmail(email);

            MGroup group = MGroup.create(name, email);
            for (String invEmail : invitations) {
                Invitation invit = Invitation.create(sender, group.id, invEmail);
                if (invit != null) {
                    emailsMap.put(invEmail, "http://" + request().host() + INVITATION_ROUTE + "/" + group.id + "/" + invit.confirmationToken);
                }
            }

            return ok(views.html.ajax.group.render(name, emailsMap));
        }
    }
}
