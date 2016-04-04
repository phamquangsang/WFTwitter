package app.com.phamsang.wftwitter.Fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import app.com.phamsang.wftwitter.R;
import app.com.phamsang.wftwitter.Utilities;
import app.com.phamsang.wftwitter.data.Contract;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserTimelineFragment extends TweetListFragment {


    private static final String LOG_TAG = UserTimelineFragment.class.getSimpleName();
    public static final String USER_SCREEN_NAME_ARGS = "user_screen_name";

    private String mScreenName;
    public UserTimelineFragment() {
        // Required empty public constructor
    }



    // TODO: Rename and change types and number of parameters
    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString(USER_SCREEN_NAME_ARGS,screenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arg = getArguments();
        if(arg!=null)
            mScreenName = arg.getString(USER_SCREEN_NAME_ARGS);
    }

    @Override
    protected void loadingTweets() {
        //not support saving in database
        //loading from internet
        getLatestTweets(-1);

    }

    @Override
    public void getLatestTweets(long sinceId) {
        if (!Utilities.isNetworkAvailable(getActivity())) {
            Snackbar.make(mContainer, R.string.no_internet_warning, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            mSwipeRefreshLayout.setRefreshing(false);
//            Toast.makeText(getContext(),"no internet connection",Toast.LENGTH_SHORT).show();
            return;
        }
        mSwipeRefreshLayout.setRefreshing(true);
        mClient.getUserTimeline(mScreenName,sinceId, -1, COUNT, new JsonHttpResponseHandler() {
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


                    mAdapter.insertDataset(list,0);
                    mRecyclerView.smoothScrollToPosition(0);


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

    @Override
    public void getMoreTweets(long maxId) {
        //call when user hit the end of the list
        if (!Utilities.isNetworkAvailable(getContext())) {
            Snackbar.make(mContainer, R.string.no_internet_warning, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        Log.i(LOG_TAG,"loading more - maxId: "+getMaxId());
        mSwipeRefreshLayout.setRefreshing(true);
        mClient.getUserTimeline(mScreenName,-1, maxId, COUNT, new JsonHttpResponseHandler() {
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


                    int endPosition = mAdapter.getItemCount();
                    mAdapter.insertDataset(list,endPosition);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(LOG_TAG, errorResponse.toString());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}
