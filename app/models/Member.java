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
    public String name;
    public String password;

    public Member(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public static Member authenticate(String email, String password) {
        String encryptedPass = StringUtil.getMd5(password, passwordSeed);
        return find.where().eq("email", email).eq("password", encryptedPass).findUnique();
    }
}