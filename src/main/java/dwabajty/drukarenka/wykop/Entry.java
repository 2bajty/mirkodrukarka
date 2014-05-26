package dwabajty.drukarenka.wykop;

/**
 * Created by atok on 2014-05-24.
 */
public class Entry {

    public boolean scraped;

    public int id;
    public String author;
    public String author_avatar;
    public String author_avatar_big;
    public String author_avatar_med;
    public String author_avatar_lo;

    public String author_group;
    public String author_sex;
    public String date;

    public String body;
    public String url;

    /*
    "receiver":null,
    "receiver_avatar":null,
    "receiver_avatar_big":null,
    "receiver_avatar_med":null,
    "receiver_avatar_lo":null,
    "receiver_group":null,
    "receiver_sex":null,
    "comments":[

      ],
     */

    public boolean blocked;
    public int vote_count;

    public int user_vote;
    public boolean user_favorite;

    /*
    "voters":[
        {
        "author":"Prado502",
            "author_group":1,
            "author_avatar":"http:\/\/c3397992.X.cdn03.imgwykop.pl\/Prado502_XDbHiIjQrt,q60.jpg",
            "author_avatar_big":"http:\/\/c3397992.X.cdn03.imgwykop.pl\/Prado502_XDbHiIjQrt,q150.jpg",
            "author_avatar_med":"http:\/\/c3397992.X.cdn03.imgwykop.pl\/Prado502_XDbHiIjQrt,q48.jpg",
            "author_avatar_lo":"http:\/\/c3397992.X.cdn03.imgwykop.pl\/Prado502_XDbHiIjQrt,q30.jpg",
            "author_sex":"male",
            "date":"2014-05-24 16:06:51"
    }
    ],
     */

    public String type;

    public boolean deleted;
    public String violation_url;


    public String toString() {
        return "Entry#" + id;
    }
//      "can_comment":null,
//    "app":null

    public Embed embed;

}
