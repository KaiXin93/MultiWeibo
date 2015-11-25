package com.example.qkx.multiweibo.previous;

import java.util.List;

/**
 * Created by qkx on 2015/11/13.
 */
public class WeiboDetail {
    public List<Statuse> statuses;

    public class Statuse {
        public String text;
        public User user;
        public List<PicUrl> pic_urls;
        public RetweetedStatus retweeted_status;

        public class User {
            public String screen_name;
            public String profile_image_url;
        }
        public class PicUrl {
            public String thumbnail_pic;
        }
        public class RetweetedStatus {
            public String text;
            public User user;
            public List<PicUrl> pic_urls;
        }
    };
}
