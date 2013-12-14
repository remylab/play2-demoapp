package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.db.ebean.Model;

@Entity
public class Item extends Model {

    public static Finder<Long, Item> find = new Finder<Long, Item>(Long.class, Item.class);

    @Id
    public long id;
    public String title;
    public String description;
    @ManyToMany(cascade = CascadeType.REMOVE)
    public List<Tag> tags = new ArrayList<Tag>();

    public Item(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public static List<Item> getItems(Long memberId) {
        return Item.find.where().eq("member_id", memberId).orderBy("id desc").findList();
    }

}
