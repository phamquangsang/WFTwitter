package app.com.phamsang.wftwitter.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import app.com.phamsang.wftwitter.data.Contract.*;

/**
 * Created by Quang Quang on 3/27/2016.
 */
public class TwitterDatabaseHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = TwitterDatabaseHelper.class.getSimpleName();
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "wftwitter.db";
    public TwitterDatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TWEET_TABLE = "CREATE TABLE "+
                TweetEntry.TABLE_NAME +" (" +
                TweetEntry._ID + " INTEGER PRIMARY KEY ,"+
                TweetEntry.COLLUMN_ID + " INTEGER NOT NULL UNIQUE ," +
                TweetEntry.COLUMN_TEXT + " TEXT , " +
                TweetEntry.COLLUMN_TIME +" TEXT NOT NULL , "   +
                TweetEntry.COLLUMN_IMAGE_URL + " TEXT , "+
                TweetEntry.COLLUMN_DISPLAY_URL + " TEXT , " +
                TweetEntry.COLLUMN_RETWEET + " INTEGER NOT NULL , "+
                TweetEntry.COLLUMN_LIKE + " INTEGER NOT NULL , "+
                TweetEntry.COLLUMN_USER_ID + " INTEGER NOT NULL ,  "+
                TweetEntry.COLLUMN_IS_LIKED+ " INTEGER NOT NULL , "+
                TweetEntry.COLLUMN_IS_RETWEETED + " INTEGER NOT NULL , "+
                " FOREIGN KEY (" + TweetEntry.COLLUMN_USER_ID + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry.COLLUMN_ID + ") " +
                ");";
        final String SQL_CREATE_USER_TABLE = "CREATE TABLE "+
                UserEntry.TABLE_NAME +" (" +
                UserEntry._ID + " INTEGER PRIMARY KEY , "+
                UserEntry.COLLUMN_ID + " INTEGER UNIQUE ON CONFLICT REPLACE NOT NULL  , "+
                UserEntry.COLLUMN_NAME + " TEXT NOT NULL , " +
                UserEntry.COLLUMN_SCREEN_NAME + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE , " +
                UserEntry.COLLUMN_LOCATION + " TEXT , "+
                UserEntry.COLLUMN_DESCRIPTION + " TEXT , "+
                UserEntry.COLLUMN_FOLLOWERS + " INTEGER , "+
                UserEntry.COLLUMN_FRIENDS + " INTEGER , "+
                UserEntry.COLLUMN_LIKES + " INTEGER , "+
                UserEntry.COLLUMN_STATUS_COUNT + " INTEGER , "+
                UserEntry.COLLUMN_BACKGROUND_IMAGE_URL + " TEXT , "+
                UserEntry.COLLUMN_PROFILE_IMAGE_URL + " TEXT  "+
                ");";
        db.execSQL(SQL_CREATE_TWEET_TABLE);
        db.execSQL(SQL_CREATE_USER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TweetEntry.TABLE_NAME);
        onCreate(db);
    }


}
