package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.apache.commons.lang3.RandomStringUtils;

import play.db.ebean.Model;
import tools.DateUtil;

@Entity
public class Invitation extends Model {
    public static Finder<Long, Invitation> find = new Finder<Long, Invitation>(Long.class, Invitation.class);

    @Id
    public long id;
    @ManyToOne(cascade = CascadeType.REMOVE)
    public MGroup group;
    public String email;
    public String confirmationToken;
    @OneToOne
    public Member sender;

    public Long creationTime;

    public Invitation(Member member, MGroup group, String email) {
        this.group = group;
        this.email = email;
        this.confirmationToken = RandomStringUtils.randomAlphanumeric(40);
        this.sender = member; // member who sent the invitation

        this.creationTime = DateUtil.getTimeNow();
    }

    public static Invitation create(Member member, Long groupId, String email) {
        if (findInvitationByEmail(groupId, email) == null) {
            Invitation invitation = new Invitation(member, MGroup.find.ref(groupId), email);
            invitation.save();
            return invitation;
        }
        return null;
    }

    private static Invitation findInvitationByEmail(Long groupId, String email) {
        Invitation invit = find.where().eq("email", email).where().eq("group.id", groupId).findUnique();
        if (invit != null) {
            return invit;
        }
        return null;
    }

    public static Invitation findInvitationByToken(Long groupId, String token) {
        Invitation invit = find.where().eq("confirmationToken", token).where().eq("group.id", groupId).findUnique();
        if (invit != null) {
            return invit;
        }
        return null;
    }
}
