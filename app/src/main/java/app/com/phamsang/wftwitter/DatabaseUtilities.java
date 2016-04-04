package app.com.phamsang.wftwitter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.com.phamsang.wftwitter.Object.Tweet;
import app.com.phamsang.wftwitter.Object.User;
import app.com.phamsang.wftwitter.data.Contract;
import app.com.phamsang.wftwitter.data.TwitterDatabaseHelper;

/**
 * Created by Quang Quang on 4/2/2016.
 */
public class DatabaseUtilities {
    public static List<Tweet> queryMentionTweets(Context context) {
        TwitterDatabaseHelper dbHelper = new TwitterDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final String MY_QUERY = "SELECT * FROM "+ Contract.MentionTweetEntry.TABLE_NAME+" tw INNER JOIN "
                + Contract.UserEntry.TABLE_NAME+" us ON tw."
                + Contract.TweetEntry.COLLUMN_USER_ID+"=us."
                + Contract.UserEntry.COLLUMN_ID + " order by tw."+ Contract.TweetEntry.COLLUMN_ID+" DESC";
        Log.d("querryTweet: ","query string: "+MY_QUERY);
        Cursor c = db.rawQuery(MY_QUERY,null);
        Log.d("querryTweet: ","result count: "+c.getCount());
        List<Tweet> dataset = new ArrayList<Tweet>();
        for(int i=0;i<c.getCount();++i){
            c.moveToPosition(i);
            Tweet tweet = new Tweet();
            tweet.setId(c.getLong(c.getColumnIndex(Contract.TweetEntry.COLLUMN_ID)));
            tweet.setDisplayImageUrl(c.getString(c.getColumnIndex(Contract.TweetEntry.COLLUMN_DISPLAY_URL)));
            tweet.setImageUrl(c.getString(c.getColumnIndex(Contract.TweetEntry.COLLUMN_IMAGE_URL)));
            tweet.setFavouriteCount(c.getInt(c.getColumnIndex(Contract.TweetEntry.COLLUMN_LIKE)));
            tweet.setRetweetCount(c.getInt(c.getColumnIndex(Contract.TweetEntry.COLLUMN_RETWEET)));
            tweet.setText(c.getString(c.getColumnIndex(Contract.TweetEntry.COLUMN_TEXT)));
            tweet.setTime(c.getString(c.getColumnIndex(Contract.TweetEntry.COLLUMN_TIME)));
            int isliked = c.getInt(c.getColumnIndex(Contract.TweetEntry.COLLUMN_IS_LIKED));
            tweet.setFavorited((isliked==1)?true:false);
            int isRetweet = c.getInt(c.getColumnIndex(Contract.TweetEntry.COLLUMN_IS_RETWEETED));
            tweet.setRetweeted((isRetweet==1)?true:false);
            tweet.setUsersMention(c.getString(c.getColumnIndex(Contract.TweetEntry.COLLUMN_USERS_MENTION)));

            User user = new User();
            user.setId(c.getLong(c.getColumnIndex(Contract.UserEntry.COLLUMN_ID)));
            user.setBackgroundUrl(c.getString(c.getColumnIndex(Contract.UserEntry.COLLUMN_BACKGROUND_IMAGE_URL)));
            user.setDescription(c.getString(c.getColumnIndex(Contract.UserEntry.COLLUMN_DESCRIPTION)));
            user.setFollowersCount(c.getInt(c.getColumnIndex(Contract.UserEntry.COLLUMN_FOLLOWERS)));
            user.setFriendCount(c.getInt(c.getColumnIndex(Contract.UserEntry.COLLUMN_FRIENDS)));
            user.setLikeCount(c.getInt(c.getColumnIndex(Contract.UserEntry.COLLUMN_LIKES)));
            user.setLocation(c.getString(c.getColumnIndex(Contract.UserEntry.COLLUMN_LOCATION)));
            user.setName(c.getString(c.getColumnIndex(Contract.UserEntry.COLLUMN_NAME)));
            user.setProfileUrl(c.getString(c.getColumnIndex(Contract.UserEntry.COLLUMN_PROFILE_IMAGE_URL)));
            user.setScreenName(c.getString(c.getColumnIndex(Contract.UserEntry.COLLUMN_SCREEN_NAME)));
            user.setStatusCount(c.getInt(c.getColumnIndex(Contract.UserEntry.COLLUMN_STATUS_COUNT)));
            int isFollowing = c.getInt(c.getColumnIndex(Contract.UserEntry.COLLUMN_FOLLOWING));
            user.setFollowing((isFollowing==1)?true:false);
            int isNotification = c.getInt(c.getColumnIndex(Contract.UserEntry.COLLUMN_NOTIFICATION));
            user.setNotification((isNotification==1)?true:false);
            tweet.setUser(user);
            dataset.add(tweet);
        }
        c.close();
        db.close();
        return dataset;
    }

    public static int updateUser(long id, ContentValues value, Context context){
        TwitterDatabaseHelper dbHelper = new TwitterDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = Contract.UserEntry.COLLUMN_ID+"=?";
        String[] selectionArg = {Long.toString(id)};
        int result = db.update(Contract.UserEntry.TABLE_NAME,value,selection,selectionArg);
        db.close();
        return result;
    }

    public static int updateTweet(long id, ContentValues value, Context context, String tableName){
        TwitterDatabaseHelper dbHelper = new TwitterDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = Contract.TweetEntry.COLLUMN_ID+"=?";
        String[] selectionArg = {Long.toString(id)};
        int result = db.update(tableName,value,selection,selectionArg);
        db.close();
        return result;
    }
    public static List<Tweet> queryTweets(Context context){
        TwitterDatabaseHelper dbHelper = new TwitterDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final String MY_QUERY = "SELECT * FROM "+ Contract.TweetEntry.TABLE_NAME+" tw INNER JOIN "
                + Contract.UserEntry.TABLE_NAME+" us ON tw."
                + Contract.TweetEntry.COLLUMN_USER_ID+"=us."
                + Contract.UserEntry.COLLUMN_ID + " order by tw."+ Contract.TweetEntry.COLLUMN_ID+" DESC";
        Log.d("querryTweet: ","query string: "+MY_QUERY);
        Cursor c = db.rawQuery(MY_QUERY,null);
        Log.d("querryTweet: ","result count: "+c.getCount());
        List<Tweet> dataset = new ArrayList<Tweet>();
        for(int i=0;i<c.getCount();++i){
            c.moveToPosition(i);
            Tweet tweet = new Tweet();
            tweet.setId(c.getLong(c.getColumnIndex(Contract.TweetEntry.COLLUMN_ID)));
            tweet.setDisplayImageUrl(c.getString(c.getColumnIndex(Contract.TweetEntry.COLLUMN_DISPLAY_URL)));
            tweet.setImageUrl(c.getString(c.getColumnIndex(Contract.TweetEntry.COLLUMN_IMAGE_URL)));
            tweet.setFavouriteCount(c.getInt(c.getColumnIndex(Contract.TweetEntry.COLLUMN_LIKE)));
            tweet.setRetweetCount(c.getInt(c.getColumnIndex(Contract.TweetEntry.COLLUMN_RETWEET)));
            tweet.setText(c.getString(c.getColumnIndex(Contract.TweetEntry.COLUMN_TEXT)));
            tweet.setTime(c.getString(c.getColumnIndex(Contract.TweetEntry.COLLUMN_TIME)));
            int isliked = c.getInt(c.getColumnIndex(Contract.TweetEntry.COLLUMN_IS_LIKED));
            tweet.setFavorited((isliked==1)?true:false);
            int isRetweet = c.getInt(c.getColumnIndex(Contract.TweetEntry.COLLUMN_IS_RETWEETED));
            tweet.setRetweeted((isRetweet==1)?true:false);
            tweet.setUsersMention(c.getString(c.getColumnIndex(Contract.TweetEntry.COLLUMN_USERS_MENTION)));

            User user = new User();
            user.setId(c.getLong(c.getColumnIndex(Contract.UserEntry.COLLUMN_ID)));
            user.setBackgroundUrl(c.getString(c.getColumnIndex(Contract.UserEntry.COLLUMN_BACKGROUND_IMAGE_URL)));
            user.setDescription(c.getString(c.getColumnIndex(Contract.UserEntry.COLLUMN_DESCRIPTION)));
            user.setFollowersCount(c.getInt(c.getColumnIndex(Contract.UserEntry.COLLUMN_FOLLOWERS)));
            user.setFriendCount(c.getInt(c.getColumnIndex(Contract.UserEntry.COLLUMN_FRIENDS)));
            user.setLikeCount(c.getInt(c.getColumnIndex(Contract.UserEntry.COLLUMN_LIKES)));
            user.setLocation(c.getString(c.getColumnIndex(Contract.UserEntry.COLLUMN_LOCATION)));
            user.setName(c.getString(c.getColumnIndex(Contract.UserEntry.COLLUMN_NAME)));
            user.setProfileUrl(c.getString(c.getColumnIndex(Contract.UserEntry.COLLUMN_PROFILE_IMAGE_URL)));
            user.setScreenName(c.getString(c.getColumnIndex(Contract.UserEntry.COLLUMN_SCREEN_NAME)));
            user.setStatusCount(c.getInt(c.getColumnIndex(Contract.UserEntry.COLLUMN_STATUS_COUNT)));
            int isFollowing = c.getInt(c.getColumnIndex(Contract.UserEntry.COLLUMN_FOLLOWING));
            user.setFollowing((isFollowing==1)?true:false);
            int isNotification = c.getInt(c.getColumnIndex(Contract.UserEntry.COLLUMN_NOTIFICATION));
            user.setNotification((isNotification==1)?true:false);
            tweet.setUser(user);
            dataset.add(tweet);
        }
        c.close();
        db.close();
        return dataset;
    }

    public static int insertToDatabase(Context c, List<Tweet> list, String tableName){
        TwitterDatabaseHelper dbHelper = new TwitterDatabaseHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int insertCounter =0;
        for(int i = 0;i<list.size();++i){
            ContentValues tweet = list.get(i).toContentValue();
            ContentValues user = list.get(i).getUser().toContentValue();
//            long t = db.insert(TweetEntry.TABLE_NAME,null,tweet);
            db.insert(Contract.UserEntry.TABLE_NAME,null, user);
            long t = db.insert(tableName,null,tweet);
            if(t!=-1)
                insertCounter++;
        }
        db.close();
        if(insertCounter==list.size())
            return 1;
        else
            return 0;
    }
    public static void deleteDatabase(Context c){
        TwitterDatabaseHelper dbHelper = new TwitterDatabaseHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(Contract.TweetEntry.TABLE_NAME,null,null);
        db.delete(Contract.UserEntry.TABLE_NAME, null, null);
        db.delete(Contract.MentionTweetEntry.TABLE_NAME,null,null);
        db.close();
    }


}
