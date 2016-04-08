package app.com.phamsang.wftwitter.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import app.com.phamsang.wftwitter.DatabaseUtilities;
import app.com.phamsang.wftwitter.Fragment.MediaFragment;
import app.com.phamsang.wftwitter.Fragment.MentionFragment;
import app.com.phamsang.wftwitter.Fragment.UserDetailTweetLikeFragment;
import app.com.phamsang.wftwitter.Fragment.UserTimelineFragment;
import app.com.phamsang.wftwitter.Object.User;
import app.com.phamsang.wftwitter.R;
import app.com.phamsang.wftwitter.TweetComposerDialog;
import app.com.phamsang.wftwitter.TwitterClient;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailActivity extends AppCompatActivity implements TweetComposerDialog.NoticeDialogListener {
    private static final String LOG_TAG = UserDetailActivity.class.getSimpleName();
    private ImageView mBackground;
    private CircleImageView mProfile;
    private User mUser;
    private TextView mName;
    private TextView mScreenName;
    private TextView mDescription;
    private TextView mLink;
    private TextView mFollowing;
    private TextView mFollower;
    private ImageView mFollowButton;
    private ImageView mNotificationButton;

    //tab setup
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    public static void startDetailView(User user, Context c) {
        Bundle b = new Bundle();
        b.putParcelable("USER_EXTRA", user);
        Intent i = new Intent(c, UserDetailActivity.class);
        i.putExtras(b);
        c.startActivity(i);
    }

    public static Intent getIntent(User user, Context c) {
        Bundle b = new Bundle();
        b.putParcelable("USER_EXTRA", user);
        Intent i = new Intent(c, UserDetailActivity.class);
        i.putExtras(b);
       return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arg = getIntent().getExtras();
        mUser = arg.getParcelable("USER_EXTRA");

        setContentView(R.layout.activity_user_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBackground = (ImageView)findViewById(R.id.background);
        Glide.with(this).load(mUser.getBackgroundUrl()).placeholder(R.drawable.user_background).into(mBackground);
        mProfile = (CircleImageView)findViewById(R.id.imageView_profile);
        Glide.with(this).load(mUser.getProfileUrl()).into(mProfile);
        mName = (TextView)findViewById(R.id.textView_Name);
        mName.setText(mUser.getName());
        mScreenName= (TextView)findViewById(R.id.textView_ScreenName);
        mScreenName.setText("@"+mUser.getScreenName());
        mDescription = (TextView)findViewById(R.id.textView_description);
        mDescription.setText(mUser.getDescription());
        mLink = (TextView)findViewById(R.id.textView_link);

        mFollowing = (TextView)findViewById(R.id.textView_following);
        mFollowing.setText(mUser.getFriendCount()+ " Following");
        mFollower  = (TextView)findViewById(R.id.textView_follower);
        mFollower.setText(mUser.getFollowersCount()+" Followers");


        //setup follow and notification button

        mFollowButton = (ImageView)findViewById(R.id.follow_button);
        mNotificationButton = (ImageView)findViewById(R.id.notification_button);
        if(!mUser.isFollowing()){
            mNotificationButton.setVisibility(View.GONE);
            mFollowButton.setSelected(false);
        }else{
            mNotificationButton.setVisibility(View.VISIBLE);
            mFollowButton.setSelected(true);
            mNotificationButton.setSelected(mUser.isNotification()?true:false);
        }
        mNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()) {
                    unRegisterNotification(mUser.getScreenName());
                }
                else{
                    registerNotification(mUser.getScreenName());
                }

            }
        });
        mFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()) {
                    unFollow(mUser.getScreenName());
                }
                else{
                    follow(mUser.getScreenName());
                }

            }
        });


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.user_detail_fragment_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.user_detail_tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    ///-----------------------------Tab setup
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0){
                return UserTimelineFragment.newInstance(mUser.getScreenName());
            }
            if(position==1){
                return  MediaFragment.newInstance(mUser.getScreenName());
            }
            if(position==2){
                return UserDetailTweetLikeFragment.newInstance(mUser.getScreenName());
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Tweets";
                case 1:
                    return "Media";
                case 2:
                    return "Likes";
            }
            return null;
        }
    }

    @Override
    public void onDialogPostButtonClick() {
        //do nothing
    }

    void follow(final String screenName){
        TwitterClient client = TwitterClient.getInstance(this);
        client.follow(screenName, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                int result = DatabaseUtilities.updateUser(mUser.getId(),mUser.toContentValue(),UserDetailActivity.this);

                mFollowButton.setSelected(true);
                mNotificationButton.setVisibility(View.VISIBLE);
                mNotificationButton.setSelected(false);
                Log.e(LOG_TAG,"follow "+screenName + " succeed - response: "+responseBody.toString());


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(LOG_TAG,"follow "+screenName + " failed - cause by: "+responseBody.toString());
            }
        });
    }

    void unFollow(final String screenName){
        TwitterClient client = TwitterClient.getInstance(this);
        client.unFollow(screenName, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                int result = DatabaseUtilities.updateUser(mUser.getId(),mUser.toContentValue(),UserDetailActivity.this);

                mFollowButton.setSelected(false);
                mNotificationButton.setVisibility(View.GONE);
                Log.e(LOG_TAG,"unfollow "+screenName + " succeed - respone : "+responseBody.toString());



            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(LOG_TAG,"unfollow "+screenName + " failed - cause by: "+responseBody.toString());
            }
        });
    }

    void registerNotification(final String screenName){
        TwitterClient client = TwitterClient.getInstance(this);
        client.registerNotification(screenName, true, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                int result = DatabaseUtilities.updateUser(mUser.getId(), mUser.toContentValue(), UserDetailActivity.this);
                mNotificationButton.setSelected(true);
                Log.e(LOG_TAG, "register notification " + screenName + " succeed - respone : " + responseBody.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(LOG_TAG,"register notification "+screenName + " failed - cause by: "+responseBody.toString());
            }
        });
    }
    void unRegisterNotification(final String screenName){
        TwitterClient client = TwitterClient.getInstance(this);
        client.registerNotification(screenName, false, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                int result = DatabaseUtilities.updateUser(mUser.getId(),mUser.toContentValue(),UserDetailActivity.this);
                mNotificationButton.setSelected(false);
                Log.e(LOG_TAG,"register notification "+screenName + " succeed - respone : "+responseBody.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(LOG_TAG,"register notification "+screenName + " failed - cause by: "+responseBody.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
