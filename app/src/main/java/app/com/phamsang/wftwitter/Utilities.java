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


}
