package app.com.phamsang.wftwitter.Fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.com.phamsang.wftwitter.DatabaseUtilities;
import app.com.phamsang.wftwitter.Object.Tweet;
import app.com.phamsang.wftwitter.R;
import app.com.phamsang.wftwitter.Utilities;
import app.com.phamsang.wftwitter.data.Contract;

/**
 * A simple {@link Fragment} subclass.
 */
public class MentionFragment extends TweetListFragment {

    public static final String MENTION_FRAGMENT_TAG = "mention_fragment";
    private static final String LOG_TAG = MentionFragment.class.getSimpleName();

    public MentionFragment() {
        // Required empty public constructor
    }

    public static MentionFragment newInstance() {
        MentionFragment fragment = new MentionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void loadingTweets(){
        List<Tweet> dataSet = DatabaseUtilities.queryMentionTweets(getContext());
        mAdapter.swapdata(dataSet);
        Log.d(LOG_TAG,"database query count: "+dataSet.size());
        if (mAdapter.getItemCount() == 0) {
            getLatestTweets(-1);
        }
    }

    @Override
    public void getLatestTweets(long sinceId) {
        if (!Utilities.isNetworkAvailable(getActivity())) {
            Snackbar.make(mRecyclerView, R.string.no_internet_warning, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            mSwipeRefreshLayout.setRefreshing(false);
//            Toast.makeText(TimelineActivity.this,"no internet connection available...",Toast.LENGTH_LONG).show();
            return;
        }
        mSwipeRefreshLayout.setRefreshing(true);
        mClient.getMention(sinceId, -1, COUNT, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                //Utilities.deleteDatabase(getActivity());
                List<Tweet> list = new ArrayList<Tweet>();
                try {
                    for (int i = 0; i < response.length(); ++i) {
                        JSONObject object = response.getJSONObject(i);
                        Tweet tweet = Tweet.fromJson(object);
                        list.add(tweet);
                    }

                    //todo add to database
                    if (DatabaseUtilities.insertToDatabase(getActivity(), list, Contract.MentionTweetEntry.TABLE_NAME) == 1)//if insert to database succeed?
                    {
                        mAdapter.insertDataset(list,0);
                        mRecyclerView.smoothScrollToPosition(0);
                    }
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

    @Override
    public void getMoreTweets(long maxId) {
        //call when user hit the end of the list
        if (!Utilities.isNetworkAvailable(getContext())) {
            Snackbar.make(mRecyclerView, R.string.no_internet_warning, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
//            Toast.makeText(TimelineActivity.this,"no internet connection available...",Toast.LENGTH_LONG).show();
            return;
        }
        Log.i(LOG_TAG,"loading more - maxId: "+getMaxId());
        mSwipeRefreshLayout.setRefreshing(true);
        mClient.getMention(-1, maxId, COUNT, new JsonHttpResponseHandler() {
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

                    //todo add to database
                    if (DatabaseUtilities.insertToDatabase(getActivity(), list, Contract.MentionTweetEntry.TABLE_NAME) == 1)//if insert to database succeed?
                    {
                        int endPosition = mAdapter.getItemCount();
                        mAdapter.insertDataset(list,endPosition);
                    } else
                        Log.e(LOG_TAG, "faild to insert data to database! ");
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
