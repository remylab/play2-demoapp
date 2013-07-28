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
        Member member = new Member(email, firstName, lastName, getStoredPassword(password));
        member.save();
        return member;
    }

    public static Member authenticate(String email, String password) {
        return find.where().eq("email", email).eq("password", getStoredPassword(password)).findUnique();
    }

    private static String getStoredPassword(String s) {
        return StringUtil.encrypt("SHA1", s, passwordSeed);
    }
}