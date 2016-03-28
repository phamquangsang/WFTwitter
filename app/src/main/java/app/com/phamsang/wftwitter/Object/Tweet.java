package app.com.phamsang.wftwitter.Object;

import android.content.ContentValues;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.com.phamsang.wftwitter.data.Contract;

/**
 * Created by Quang Quang on 3/26/2016.
 */
public class Tweet {
    private static final String LOG_TAG = Tweet.class.getSimpleName();
    @SerializedName("id")
    private long mId;
    @SerializedName("text")
    private String mText;
    @SerializedName("created_at")
    private String mTime;
    @SerializedName("retweet_count")
    private int mRetweetCount;
    @SerializedName("favorite_count")
    private int mFavouriteCount;
    private String mImageUrl;
    private String mDisplayImageUrl;
    @SerializedName("user")
    private User mUser;

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public String getDisplayImageUrl() {
        return mDisplayImageUrl;
    }

    public void setDisplayImageUrl(String displayImageUrl) {
        mDisplayImageUrl = displayImageUrl;
    }

    public Tweet(long id, User user, String text, String time, int retweetCount, int favouriteCount, String imageUrl) {
        mId = id;
        mUser = user;
        mText = text;
        mTime = time;
        mRetweetCount = retweetCount;
        mFavouriteCount = favouriteCount;
        mImageUrl = imageUrl;
    }

    public Tweet() {

    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public int getRetweetCount() {
        return mRetweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        mRetweetCount = retweetCount;
    }

    public int getFavouriteCount() {
        return mFavouriteCount;
    }

    public void setFavouriteCount(int favouriteCount) {
        mFavouriteCount = favouriteCount;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    @Override
    public String toString(){
        return "id: "+mId+" - text: "+mText+" - time: "+mTime+" - retweet count: "+mRetweetCount+" - like count: "+mFavouriteCount+ " - imageUrl: "+mImageUrl;

    }


    public static Tweet fromJson(JSONObject object){
        Gson gson =new Gson();
        Tweet tweet = new Tweet();
        tweet = gson.fromJson(object.toString(),Tweet.class);
        try {
            JSONObject entities = object.getJSONObject("entities");
            JSONArray media = entities.getJSONArray("media");
            if(media!=null){
                for(int i=0 ;i<media.length();++i){
                    JSONObject mediaObject = media.getJSONObject(i);
                    if(mediaObject.getString("type").equalsIgnoreCase("photo")){
                        tweet.setImageUrl(mediaObject.getString("media_url"));
                        tweet.setDisplayImageUrl(mediaObject.getString("display_url"));
                    }
                }
            }


            JSONObject user = object.getJSONObject("user");
            tweet.setUser(gson.fromJson(user.toString(),User.class));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(LOG_TAG,"buiding Tweet from json failed - "+ e.toString());
        }
        return tweet;
    }

    public ContentValues toContentValue(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.TweetEntry.COLLUMN_ID,mId);
        contentValues.put(Contract.TweetEntry.COLLUMN_DISPLAY_URL, mDisplayImageUrl);
        contentValues.put(Contract.TweetEntry.COLLUMN_IMAGE_URL, mImageUrl);
        contentValues.put(Contract.TweetEntry.COLLUMN_LIKE, mFavouriteCount);
        contentValues.put(Contract.TweetEntry.COLLUMN_RETWEET,mRetweetCount);
        contentValues.put(Contract.TweetEntry.COLLUMN_TIME, mTime);
        contentValues.put(Contract.TweetEntry.COLUMN_TEXT, mText);
        contentValues.put(Contract.TweetEntry.COLLUMN_USER_ID, mUser.getId());
        return contentValues;
    }

}
