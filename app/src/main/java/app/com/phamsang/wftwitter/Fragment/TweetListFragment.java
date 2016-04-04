package app.com.phamsang.wftwitter.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.com.phamsang.wftwitter.EndlessRecyclerViewScrollListener;
import app.com.phamsang.wftwitter.Object.Tweet;
import app.com.phamsang.wftwitter.R;
import app.com.phamsang.wftwitter.TimelineAdapter;
import app.com.phamsang.wftwitter.TwitterClient;
import app.com.phamsang.wftwitter.Utilities;


public abstract class TweetListFragment extends Fragment {
    public static final String HOME_TIMELINE_FRAGMENT_TAG = "timeline_fragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String LOG_TAG = TweetListFragment.class.getSimpleName();
    protected static final int COUNT =  25;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    protected OnFragmentInteractionListener mListener;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mRecyclerView;
    protected View mContainer;
    protected LinearLayoutManager mLayoutManager;
    protected TimelineAdapter mAdapter;
    protected SharedPreferences mSetting;
    protected TwitterClient mClient ;

    public long getMaxId() {
        return mAdapter.getMaxId();
    }

    public long getSinceId() {
        return mAdapter.getSinceId();
    }

    public TweetListFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.tweet_recycler_view,null,false);
        mContainer = container;
        mSwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLatestTweets(mAdapter.getSinceId());
            }
        });
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TimelineAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(long sinceId, long max) {
                Toast.makeText(getContext(), "loading more...", Toast.LENGTH_SHORT).show();
                getMoreTweets(getMaxId());

            }
        });
        mClient = TwitterClient.getInstance(getActivity());


        loadingTweets();
        return v;
    }


    abstract protected void loadingTweets() ;

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    abstract public void getLatestTweets(final long sinceId);

    abstract public void getMoreTweets(long maxId) ;


}
