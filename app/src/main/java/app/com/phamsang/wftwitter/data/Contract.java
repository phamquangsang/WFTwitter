package app.com.phamsang.wftwitter.data;

import android.provider.BaseColumns;

/**
 * Created by Quang Quang on 3/27/2016.
 */
public final class Contract {
    public Contract(){

    }

    public static final class TweetEntry implements BaseColumns {
        public static final String TABLE_NAME = "Tweet";
        public static final String COLLUMN_ID = "tweet_id";
        public static final String COLLUMN_TIME = "time";
        public static final String COLUMN_TEXT = "text";
        public static final String COLLUMN_IMAGE_URL = "image_url";
        public static final String COLLUMN_DISPLAY_URL = "display_image_url";
        public static final String COLLUMN_RETWEET = "retweet_count";
        public static final String COLLUMN_LIKE = "favorite_count";
        public static final String COLLUMN_USER_ID = "user_id";
    }

    public static final class UserEntry implements BaseColumns{
        public static final String TABLE_NAME = "user";
        public static final String COLLUMN_ID = "user_id";
        public static final String COLLUMN_NAME = "user_name";
        public static final String COLLUMN_SCREEN_NAME = "screen_name";
        public static final String COLLUMN_LOCATION = "location";
        public static final String COLLUMN_DESCRIPTION = "description";
        public static final String COLLUMN_FOLLOWERS = "followers_count";
        public static final String COLLUMN_FRIENDS = "friend_count";
        public static final String COLLUMN_LIKES = "likes_count";
        public static final String COLLUMN_STATUS_COUNT = "startus_count";
        public static final String COLLUMN_BACKGROUND_IMAGE_URL = "background_image_url";
        public static final String COLLUMN_PROFILE_IMAGE_URL = "profile_image";
    }

}
