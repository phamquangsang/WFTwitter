package app.com.phamsang.wftwitter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.com.phamsang.wftwitter.Object.Tweet;
import app.com.phamsang.wftwitter.Object.User;
import app.com.phamsang.wftwitter.data.Contract.*;
import app.com.phamsang.wftwitter.data.TwitterDatabaseHelper;


/**
 * Created by Quang Quang on 3/27/2016.
 */
public class Utilities {
    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void deleteDatabase(Context c){
        TwitterDatabaseHelper dbHelper = new TwitterDatabaseHelper(c);
        dbHelper.getWritableDatabase().delete(TweetEntry.TABLE_NAME,null,null);
        dbHelper.getWritableDatabase().delete(UserEntry.TABLE_NAME, null, null);
    }

    public static int insertToDatabase(Context c, List<Tweet> list){
        TwitterDatabaseHelper dbHelper = new TwitterDatabaseHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int insertCounter =0;
        for(int i = 0;i<list.size();++i){
            ContentValues tweet = list.get(i).toContentValue();
            ContentValues user = list.get(i).getUser().toContentValue();
            long t = db.insert(TweetEntry.TABLE_NAME,null,tweet);
            db.insert(UserEntry.TABLE_NAME,null, user);
            if(t!=-1)
                insertCounter++;
        }
        if(insertCounter==list.size())
            return 1;
        else
            return 0;
    }

    public static List<Tweet> queryTweets(Context context){
        TwitterDatabaseHelper dbHelper = new TwitterDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final String MY_QUERY = "SELECT * FROM "+ TweetEntry.TABLE_NAME+" tw INNER JOIN "
                + UserEntry.TABLE_NAME+" us ON tw."
                + TweetEntry.COLLUMN_USER_ID+"=us."
                + UserEntry.COLLUMN_ID + " order by tw."+TweetEntry.COLLUMN_ID+" DESC";
        Log.d("querryTweet: ","query string: "+MY_QUERY);
        Cursor c = db.rawQuery(MY_QUERY,null);
        Log.d("querryTweet: ","result count: "+c.getCount());
        List<Tweet> dataset = new ArrayList<Tweet>();
        for(int i=0;i<c.getCount();++i){
            c.moveToPosition(i);
            Tweet tweet = new Tweet();
            tweet.setId(c.getLong(c.getColumnIndex(TweetEntry._ID)));
            tweet.setDisplayImageUrl(c.getString(c.getColumnIndex(TweetEntry.COLLUMN_DISPLAY_URL)));
            tweet.setImageUrl(c.getString(c.getColumnIndex(TweetEntry.COLLUMN_IMAGE_URL)));
            tweet.setFavouriteCount(c.getInt(c.getColumnIndex(TweetEntry.COLLUMN_LIKE)));
            tweet.setRetweetCount(c.getInt(c.getColumnIndex(TweetEntry.COLLUMN_RETWEET)));
            tweet.setText(c.getString(c.getColumnIndex(TweetEntry.COLUMN_TEXT)));
            tweet.setTime(c.getString(c.getColumnIndex(TweetEntry.COLLUMN_TIME)));
            int isliked = c.getInt(c.getColumnIndex(TweetEntry.COLLUMN_IS_LIKED));
            tweet.setFavorited((isliked==1)?true:false);
            int isRetweet = c.getInt(c.getColumnIndex(TweetEntry.COLLUMN_IS_RETWEETED));
            tweet.setRetweeted((isRetweet==1)?true:false);

            User user = new User();
            user.setId(c.getLong(c.getColumnIndex(UserEntry.COLLUMN_ID)));
            user.setBackgroundUrl(c.getString(c.getColumnIndex(UserEntry.COLLUMN_BACKGROUND_IMAGE_URL)));
            user.setDescription(c.getString(c.getColumnIndex(UserEntry.COLLUMN_DESCRIPTION)));
            user.setFollowersCount(c.getInt(c.getColumnIndex(UserEntry.COLLUMN_FOLLOWERS)));
            user.setFriendCount(c.getInt(c.getColumnIndex(UserEntry.COLLUMN_FRIENDS)));
            user.setLikeCount(c.getInt(c.getColumnIndex(UserEntry.COLLUMN_LIKES)));
            user.setLocation(c.getString(c.getColumnIndex(UserEntry.COLLUMN_LOCATION)));
            user.setName(c.getString(c.getColumnIndex(UserEntry.COLLUMN_NAME)));
            user.setProfileUrl(c.getString(c.getColumnIndex(UserEntry.COLLUMN_PROFILE_IMAGE_URL)));
            user.setScreenName(c.getString(c.getColumnIndex(UserEntry.COLLUMN_SCREEN_NAME)));
            user.setStatusCount(c.getInt(c.getColumnIndex(UserEntry.COLLUMN_STATUS_COUNT)));
            tweet.setUser(user);
            dataset.add(tweet);
        }
        c.close();
        return dataset;
    }
    public static String twitterTimeFormat(String input){
        final String pattern = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        simpleDateFormat.setLenient(true);
        try {
            Date date = simpleDateFormat.parse(input);
            long timeStamp = date.getTime();
            String relativeTime = "";
            return relativeTime = DateUtils.getRelativeTimeSpanString(timeStamp, System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    public static int updateTweet(long id, ContentValues value, Context context){
        TwitterDatabaseHelper dbHelper = new TwitterDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = TweetEntry.COLLUMN_ID+"=?";
        String[] selectionArg = {Long.toString(id)};
        int result = db.update(TweetEntry.TABLE_NAME,value,selection,selectionArg);
        return result;
    }
}
