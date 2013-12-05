package models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TagType {

    @Id
    public String type;

    public TagType(String type) {
        this.type = type;
    }
}
