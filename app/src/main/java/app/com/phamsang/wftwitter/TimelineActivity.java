package app.com.phamsang.wftwitter;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.com.phamsang.wftwitter.Object.Tweet;
import app.com.phamsang.wftwitter.Object.User;

public class TimelineActivity extends AppCompatActivity implements TweetComposerDialog.NoticeDialogListener{
    //constant
    public final static String TWITTER_SETTING = "twitter_setting";
    public final static int COUNT = 20;
    private static final String LOG_TAG = TimelineActivity.class.getSimpleName();
    private static final String SINCE_ID_KEY = "since_id";
    private static final String MAX_ID_KEY = "max_id";

    private TwitterClient mClient;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private TimelineAdapter mAdapter;
    private SharedPreferences mSetting;

    private long mSinceId;
    private long mMaxId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mClient = (TwitterClient)TwitterClient.getInstance(TwitterClient.class,this);
        if (mClient.isAuthenticated() == false) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLatestTweets(0L);
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TimelineAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(long sinceId, long max) {
                Toast.makeText(TimelineActivity.this, "loading more...", Toast.LENGTH_SHORT).show();
                getMoreTweets(mMaxId);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TweetComposerDialog composer = TweetComposerDialog.newInstance(new User());
                composer.show(getFragmentManager(),"COMPOSER_TAG");
            }
        });
        mAdapter.swapdata(Utilities.queryTweets(TimelineActivity.this));
        mSetting = getSharedPreferences(TWITTER_SETTING, MODE_PRIVATE);
        mSinceId = mSetting.getLong(SINCE_ID_KEY, 0L);
        mMaxId = mSetting.getLong(MAX_ID_KEY, 0L);


        if (mAdapter.getItemCount() == 0) {
            getLatestTweets(-1);
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getLatestTweets(final long sinceId) {
        if (!Utilities.isNetworkAvailable(this)) {
            Snackbar.make(mRecyclerView, R.string.no_internet_warning, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            mSwipeRefreshLayout.setRefreshing(false);
//            Toast.makeText(TimelineActivity.this,"no internet connection available...",Toast.LENGTH_LONG).show();
            return;
        }
        mClient.getHomeTimeline(sinceId, -1, COUNT, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Utilities.deleteDatabase(TimelineActivity.this);
                List<Tweet> list = new ArrayList<Tweet>();
                try {
                    for (int i = 0; i < response.length(); ++i) {
                        JSONObject object = response.getJSONObject(i);
                        Tweet tweet = Tweet.fromJson(object);
                        list.add(tweet);
                    }
                    //update since max;
                    if(response.length()!=0){
                        updateSinceId(list.get(0).getId());
                        updateMaxId(list.get(list.size() - 1).getId() - 1L);
                    }

                    //todo add to database
                    if (Utilities.insertToDatabase(TimelineActivity.this, list) == 1)//if insert to database succeed?
                        mAdapter.swapdata(list);
                    else
                        Log.e(LOG_TAG, "faild to insert data to database! ");
                    mSwipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(LOG_TAG, errorResponse.toString());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void getMoreTweets(long maxId) {
        if (!Utilities.isNetworkAvailable(this)) {
            Snackbar.make(mRecyclerView, R.string.no_internet_warning, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
//            Toast.makeText(TimelineActivity.this,"no internet connection available...",Toast.LENGTH_LONG).show();
            return;
        }
        mClient.getHomeTimeline(-1, maxId, COUNT, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                List<Tweet> list = new ArrayList<Tweet>();
                try {
                    for (int i = 0; i < response.length(); ++i) {
                        JSONObject object = response.getJSONObject(i);
                        Tweet tweet = Tweet.fromJson(object);
                        list.add(tweet);
                    }
                    //update since max;
                    if(response.length()!=0){
                        updateMaxId(list.get(list.size() - 1).getId() - 1L);//-1 according to twitter api recommended
                    }
                    //todo add to database
                    if (Utilities.insertToDatabase(TimelineActivity.this, list) == 1)//if insert to database succeed?
                    {
                        int endPosition = mAdapter.getItemCount() - 1;
                        mAdapter.getDataset().addAll(list);
                        mAdapter.notifyItemRangeInserted(endPosition+1, list.size());
                    } else
                        Log.e(LOG_TAG, "faild to insert data to database! ");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(LOG_TAG, errorResponse.toString());
            }
        });
    }

    private void updateSinceId(long sinceId) {
        mSinceId = sinceId;
        mSetting.edit().putLong(SINCE_ID_KEY, sinceId).commit();
    }

    private void updateMaxId(long maxId) {
        mMaxId = maxId;
        mSetting.edit().putLong(MAX_ID_KEY, maxId).commit();
    }

    @Override
    public void onDialogPostButtonClick() {
        getLatestTweets(mSinceId);
    }
}
