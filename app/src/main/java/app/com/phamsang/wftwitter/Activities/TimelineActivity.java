package app.com.phamsang.wftwitter.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import app.com.phamsang.wftwitter.DatabaseUtilities;
import app.com.phamsang.wftwitter.Fragment.MentionFragment;
import app.com.phamsang.wftwitter.Fragment.TimelineFragment;
import app.com.phamsang.wftwitter.Object.User;
import app.com.phamsang.wftwitter.R;
import app.com.phamsang.wftwitter.TweetComposerDialog;
import app.com.phamsang.wftwitter.TwitterClient;
import app.com.phamsang.wftwitter.Utilities;

public class TimelineActivity extends AppCompatActivity implements TweetComposerDialog.NoticeDialogListener {
    //constant
    public static final int TWEET_DETAILREQUEST_CODE = 1;
    public final static String TWITTER_SETTING = "twitter_setting";
    public final static int COUNT = 20;
    private static final String LOG_TAG = TimelineActivity.class.getSimpleName();

    private TwitterClient mClient;
    private User mUser;

    //tab setup
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private OnPostButtonClickListener mOnPostButtonClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeMemberVariable();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            mClient.clearAccessToken();
            DatabaseUtilities.deleteDatabase(this);
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        }else if(id==R.id.action_profile){
            UserDetailActivity.startDetailView(mUser,this);
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeMemberVariable() {
        mClient = (TwitterClient) TwitterClient.getInstance(TwitterClient.class, this);
        if (mClient.isAuthenticated() == false) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
//        else {
//            FragmentManager fm = getSupportFragmentManager();
//            fm.beginTransaction().replace(R.id.container, TweetListFragment.newInstance("a", "b"), TweetListFragment.HOME_TIMELINE_FRAGMENT_TAG).commit();
//        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        getUserDetail();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TweetComposerDialog composer = TweetComposerDialog.newInstance(mUser, null);
                composer.show(getSupportFragmentManager(), "COMPOSER_TAG");
            }
        });
    }


    private void getUserDetail() {
        mClient.getAccount(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (statusCode != 200) {
                    Log.e(LOG_TAG, "error getting user detail information code: " + statusCode);
                }
                Gson gson = new Gson();
                mUser = gson.fromJson(response.toString(), User.class);
                Log.i(LOG_TAG, "use detail: " + mUser.toDetailString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Override
    public void onDialogPostButtonClick() {
//        TweetListFragment fragment = (TweetListFragment)
//                getSupportFragmentManager().findFragmentByTag(TweetListFragment.HOME_TIMELINE_FRAGMENT_TAG);
//        fragment.getLatestTweets(fragment.getSinceId());
        if(mOnPostButtonClickListener!=null)
            mOnPostButtonClickListener.onPostButtonClick();
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
                return TimelineFragment.newInstance();
            }
            if(position==1){
                MentionFragment mentionFragment = new MentionFragment();
                return  mentionFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Timeline";
                case 1:
                    return "Mention";
            }
            return null;
        }
    }
    public interface OnPostButtonClickListener{
        void onPostButtonClick();
    }
    public void setOnPostButtonClickListener(OnPostButtonClickListener onPostButtonClickListener){
        mOnPostButtonClickListener = onPostButtonClickListener;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(resultCode, resultCode, data);
        if(requestCode==TWEET_DETAILREQUEST_CODE){
            if(resultCode==RESULT_OK){
                if(mOnPostButtonClickListener!=null)//new tweet posted, notify timelineFragment refresh tweet list.
                    mOnPostButtonClickListener.onPostButtonClick();
            }
        }
    }
}
