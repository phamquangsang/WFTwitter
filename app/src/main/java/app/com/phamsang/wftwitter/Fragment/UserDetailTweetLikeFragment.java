package app.com.phamsang.wftwitter.Fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.com.phamsang.wftwitter.Object.Tweet;
import app.com.phamsang.wftwitter.R;
import app.com.phamsang.wftwitter.Utilities;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserDetailTweetLikeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDetailTweetLikeFragment extends TweetListFragment {
    private static final String LOG_TAG = UserDetailTweetLikeFragment.class.getSimpleName() ;
    private String mScreenName;
    private static final String USER_SCREEN_NAME_ARGS = "user_screen_name_arg" ;

    public UserDetailTweetLikeFragment() {
        // Required empty public constructor
    }


    public static UserDetailTweetLikeFragment newInstance(String screenName) {
        UserDetailTweetLikeFragment fragment = new UserDetailTweetLikeFragment();
        Bundle args = new Bundle();
        args.putString(USER_SCREEN_NAME_ARGS,screenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mScreenName = getArguments().getString(USER_SCREEN_NAME_ARGS);
        }
    }

    @Override
    protected void loadingTweets() {
        getLatestTweets(-1);
    }

    @Override
    public void getLatestTweets(long sinceId) {
        if (!Utilities.isNetworkAvailable(getActivity())) {
            Snackbar.make(mContainer, R.string.no_internet_warning, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        mClient.getUserFavoritesList(mScreenName,sinceId, -1, COUNT, new JsonHttpResponseHandler() {
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
        mClient.getUserFavoritesList(mScreenName,-1, maxId, COUNT, new JsonHttpResponseHandler() {
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
