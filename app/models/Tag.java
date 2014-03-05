package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.db.ebean.Model;

@Entity
public class Tag extends Model {
    public static Finder<Long, Tag> find = new Finder<Long, Tag>(Long.class, Tag.class);

    @Id
    public long id;
    @OneToOne
    public TagType type;
    public String text;

    public Tag(TagType type, String text) {
        this.type = type;
        this.text = text;
    }
}
