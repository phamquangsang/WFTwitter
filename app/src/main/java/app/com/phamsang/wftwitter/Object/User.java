package app.com.phamsang.wftwitter.Object;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import app.com.phamsang.wftwitter.data.Contract.UserEntry;

/**
 * Created by Quang Quang on 3/27/2016.
 */
public class User implements Parcelable {
    @SerializedName("id_str")
    private long mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("screen_name")
    private String mScreenName;
    @SerializedName("location")
    private String mLocation;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("followers_count")
    private int mFollowersCount;
    @SerializedName("friends_count")
    private int mFriendCount;
    @SerializedName("favourites_count")
    private int mLikeCount;
    @SerializedName("statuses_count")
    private int mStatusCount;
    @SerializedName("profile_background_image_url")
    private String mBackgroundUrl;
    @SerializedName("profile_image_url")
    private String mProfileUrl;
    @SerializedName("following")
    private boolean isFollowing;
    @SerializedName("notifications")
    private boolean isNotification;

    public User() {

    }

    public User(long id, String name, String screenName, String location, String description, int followersCount, int friendCount, int likeCount, int statusCount, String backgroundUrl, String profileUrl) {
        mId = id;
        mName = name;
        mScreenName = screenName;
        mLocation = location;
        mDescription = description;
        mFollowersCount = followersCount;
        mFriendCount = friendCount;
        mLikeCount = likeCount;
        mStatusCount = statusCount;
        mBackgroundUrl = backgroundUrl;
        mProfileUrl = profileUrl;
    }

    public static User fromContentValue(ContentValues value) {
        long id = value.getAsLong(UserEntry.COLLUMN_ID);
        String description = value.getAsString(UserEntry.COLLUMN_DESCRIPTION);
        String backgroundUrl = value.getAsString(UserEntry.COLLUMN_BACKGROUND_IMAGE_URL);
        String profileUrl = value.getAsString(UserEntry.COLLUMN_PROFILE_IMAGE_URL);
        int follower = value.getAsInteger(UserEntry.COLLUMN_FOLLOWERS);
        int friends = value.getAsInteger(UserEntry.COLLUMN_FRIENDS);
        int likes = value.getAsInteger(UserEntry.COLLUMN_LIKES);
        String locations = value.getAsString(UserEntry.COLLUMN_LOCATION);
        String name = value.getAsString(UserEntry.COLLUMN_NAME);
        String screenName = value.getAsString(UserEntry.COLLUMN_SCREEN_NAME);
        int statusCount = value.getAsInteger(UserEntry.COLLUMN_STATUS_COUNT);

        User user =
                new User(id, name, screenName, locations, description, follower, friends, likes, statusCount, backgroundUrl, profileUrl);
        return user;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getScreenName() {
        return mScreenName;
    }

    public void setScreenName(String screenName) {
        mScreenName = screenName;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public int getFollowersCount() {
        return mFollowersCount;
    }

    public void setFollowersCount(int followersCount) {
        mFollowersCount = followersCount;
    }

    public int getFriendCount() {
        return mFriendCount;
    }

    public void setFriendCount(int friendCount) {
        mFriendCount = friendCount;
    }

    public int getLikeCount() {
        return mLikeCount;
    }

    public void setLikeCount(int likeCount) {
        mLikeCount = likeCount;
    }

    public int getStatusCount() {
        return mStatusCount;
    }

    public void setStatusCount(int statusCount) {
        mStatusCount = statusCount;
    }

    public String getBackgroundUrl() {
        return mBackgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        mBackgroundUrl = backgroundUrl;
    }

    public String getProfileUrl() {
        return mProfileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        mProfileUrl = profileUrl;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean pFollowing) {
        isFollowing = pFollowing;
    }

    public boolean isNotification() {
        return isNotification;
    }

    public void setNotification(boolean pNotification) {
        isNotification = pNotification;
    }

    public ContentValues toContentValue() {
        ContentValues content = new ContentValues();
        content.put(UserEntry.COLLUMN_ID, mId);
        content.put(UserEntry.COLLUMN_DESCRIPTION, mDescription);
        content.put(UserEntry.COLLUMN_BACKGROUND_IMAGE_URL, mBackgroundUrl);
        content.put(UserEntry.COLLUMN_FOLLOWERS, mFollowersCount);
        content.put(UserEntry.COLLUMN_FRIENDS, mFriendCount);
        content.put(UserEntry.COLLUMN_LIKES, mLikeCount);
        content.put(UserEntry.COLLUMN_LOCATION, mLocation);
        content.put(UserEntry.COLLUMN_NAME, mName);
        content.put(UserEntry.COLLUMN_SCREEN_NAME, mScreenName);
        content.put(UserEntry.COLLUMN_PROFILE_IMAGE_URL, mProfileUrl);
        content.put(UserEntry.COLLUMN_STATUS_COUNT, mStatusCount);
        content.put(UserEntry.COLLUMN_FOLLOWING, isFollowing?1:0);
        content.put(UserEntry.COLLUMN_NOTIFICATION, isNotification?1:0);
        return content;
    }

    public String toDetailString(){
        String detail = "ID: "+mId+" - Name: "+mName+" - Description: "+mDescription+" - Location: "+mLocation+" - Profile Image: "+mProfileUrl;
        return detail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mName);
        dest.writeString(this.mScreenName);
        dest.writeString(this.mLocation);
        dest.writeString(this.mDescription);
        dest.writeInt(this.mFollowersCount);
        dest.writeInt(this.mFriendCount);
        dest.writeInt(this.mLikeCount);
        dest.writeInt(this.mStatusCount);
        dest.writeString(this.mBackgroundUrl);
        dest.writeString(this.mProfileUrl);
        dest.writeByte(isFollowing ? (byte) 1 : (byte) 0);
        dest.writeByte(isNotification ? (byte) 1 : (byte) 0);
    }

    protected User(Parcel in) {
        this.mId = in.readLong();
        this.mName = in.readString();
        this.mScreenName = in.readString();
        this.mLocation = in.readString();
        this.mDescription = in.readString();
        this.mFollowersCount = in.readInt();
        this.mFriendCount = in.readInt();
        this.mLikeCount = in.readInt();
        this.mStatusCount = in.readInt();
        this.mBackgroundUrl = in.readString();
        this.mProfileUrl = in.readString();
        this.isFollowing = in.readByte() != 0;
        this.isNotification = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
