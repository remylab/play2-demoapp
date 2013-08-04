package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.RandomStringUtils;

import play.db.ebean.Model;

@Entity
public class Invitation extends Model {
    public static Finder<Long, Invitation> find = new Finder<Long, Invitation>(Long.class, Invitation.class);

    @Id
    public long id;
    @ManyToOne(cascade = CascadeType.REMOVE)
    public MGroup group;
    public String memberEmail;
    public String confirmationToken;

    public Invitation(MGroup group, String email) {
        this.group = group;
        this.memberEmail = email;
        this.confirmationToken = RandomStringUtils.randomAlphanumeric(40);
    }

    public static Invitation create(Long groupId, String email) {
        if (!invitationExists(groupId, email)) {
            Invitation invitation = new Invitation(MGroup.find.ref(groupId), email);
            invitation.save();
            return invitation;
        }
        return null;
    }

    private static boolean invitationExists(Long groupId, String email) {
        if (find.where().eq("memberEmail", email).where().eq("group.id", groupId).findUnique() != null) {
            System.out.println("INFO : invitation exists for " + groupId + "/" + email);
            return true;
        }
        return false;
    }
}
