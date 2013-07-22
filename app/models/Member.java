package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;
import tools.StringUtil;

@Entity
public class Member extends Model {
    private static String passwordSeed = "danslajungleterriblejungle";
    public static Finder<String, Member> find = new Finder<String, Member>(String.class, Member.class);

    @Id
    public String email;
    public String firstName;
    public String lastName;
    public String password;

    public Member(String email, String firstName, String lastName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public static Member create(String email, String firstName, String lastName, String password) {
        String encryptedPass = StringUtil.encrypt("SHA1", password, passwordSeed);
        Member member = new Member(email, firstName, lastName, encryptedPass);
        member.save();
        return member;
    }

    public static Member authenticate(String email, String password) {
        String encryptedPass = StringUtil.encrypt("SHA1", password, passwordSeed);
        return find.where().eq("email", email).eq("password", encryptedPass).findUnique();
    }
}