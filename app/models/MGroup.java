package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.db.ebean.Model;

@Entity
public class MGroup extends Model {
    public static Finder<Long, MGroup> find = new Finder<Long, MGroup>(Long.class, MGroup.class);

    @Id
    public long id;
    public String name;
    @ManyToMany(cascade = CascadeType.REMOVE)
    public List<Member> members = new ArrayList<Member>();

    public MGroup(String name, Member owner) {
        this.name = name;
        this.members.add(owner);
    }

    public static MGroup create(String name, String ownerEmail) {
        MGroup group = new MGroup(name, Member.findByEmail(ownerEmail));
        group.save();
        group.saveManyToManyAssociations("members");
        return group;
    }

    public static MGroup add(Long id, Member member) {
        MGroup group = find.ref(id);
        group.members.add(member);
        group.saveManyToManyAssociations("members");
        return group;
    }
}
