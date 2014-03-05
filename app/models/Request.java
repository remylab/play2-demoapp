package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import play.db.ebean.Model;
import tools.DateUtil;

@Entity
public class Request extends Model {
    public static Finder<Long, Request> find = new Finder<Long, Request>(Long.class, Request.class);

    @Id
    public long id;
    @ManyToOne
    public Member requester;
    @OneToOne
    public Item itemWanted;

    public boolean isActive;
    public boolean isReplied;

    public Long requestTime;
    public Long givenTime;
    public Long returnTime;
    public Long replyTime;

    public Request(Member member, Item item) {
        this.requester = member;
        this.itemWanted = item;

        this.requestTime = DateUtil.getTimeNow();
        this.isActive = true;
        this.isReplied = false;
    }
}
