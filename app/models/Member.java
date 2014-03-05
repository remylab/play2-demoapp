package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.apache.commons.lang3.RandomStringUtils;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.mvc.Http.Request;
import tools.DateUtil;
import tools.StringUtil;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;

@Entity
public class Member extends Model {
    private static String passwordSeed = "danslajungleterriblejungle";
    public static Finder<String, Member> find = new Finder<String, Member>(String.class, Member.class);

    @Id
    public long id;
    @Email
    @Required
    @MinLength(4)
    @Column(unique = true)
    public String email;
    public String firstName;
    public String lastName;
    public String password;
    public boolean active;
    public String confirmationToken;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    public List<Item> items = new ArrayList<Item>();

    public Long creationTime;

    public Member(String email, String firstName, String lastName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.active = false;
        this.confirmationToken = RandomStringUtils.randomAlphanumeric(40);

        this.creationTime = DateUtil.getTimeNow();
    }

    public static Member create(String email, String firstName, String lastName, String password) {
        Member member = new Member(email, firstName, lastName, getStoredPassword(password));
        member.save();
        return member;
    }

    public static Member confirmToken(String email, String token) {
        Member member = find.where().eq("email", email)
                .eq("confirmationToken", token)
                .findUnique();

        if (member != null) {
            member.active = true;
            member.confirmationToken = null;
            member.save();
        }
        return member;
    }

    public static Member findByEmail(String email) {
        return find.where().eq("email", email)
                .eq("active", true)
                .findUnique();
    }

    public static Member authenticate(String email, String password) {
        return find.where().eq("email", email)
                .eq("password", getStoredPassword(password))
                .eq("active", true)
                .findUnique();
    }

    private static String getStoredPassword(String s) {
        return StringUtil.encrypt("SHA1", s, passwordSeed);
    }

    public static Item addItem(Member member, String title, String description) {
        Item item = new Item(title, description);
        member.items.add(item);
        member.saveManyToManyAssociations("items");
        member.save();
        return item;
    }

    public static List<Item> getItems(Member member, Request request) {
        List<Item> ret = new ArrayList<Item>();

        int nbItems = 10;
        int offset = 0;

        String sql = "select * from item a where exists (select 1 from mgroup_member b where"
                + " a.member_id = b.member_id and exists (select 1 from mgroup_member c where c.member_id = :member_id and c.mgroup_id = b.mgroup_id) ) and a.member_id != :member_id"
                + " offset " + offset + " rows fetch next " + nbItems + " rows only";

        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
        sqlQuery.setParameter("member_id", member.id);
        List<SqlRow> rows = sqlQuery.findList();

        for (SqlRow row : rows) {
            Item i = new Item(row.getString("title"), row.getString("description"));
            i.id = row.getLong("id");
            ret.add(i);
        }
        return ret;
    }

    public static List<Member> membersFromGroup(Long groupId, int page, int nb) {

        List<Member> ret = new ArrayList<Member>();

        String sql = "select a.* from member a inner join mgroup_member b on a.id = b.member_id where b.mgroup_id =:group_id"
                + " order by a.id desc offset " + page + " rows fetch next " + nb + " rows only;";

        System.out.println(sql);

        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
        sqlQuery.setParameter("group_id", groupId);
        List<SqlRow> rows = sqlQuery.findList();

        for (SqlRow row : rows) {
            Member member = new Member("email", row.getString("first_name"), row.getString("last_name"), "password_hash");
            ret.add(member);
        }

        return ret;
    };
}