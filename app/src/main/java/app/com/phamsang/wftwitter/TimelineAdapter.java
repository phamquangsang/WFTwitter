package app.com.phamsang.wftwitter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import app.com.phamsang.wftwitter.Activities.TimelineActivity;
import app.com.phamsang.wftwitter.Activities.TweetDetailActivity;
import app.com.phamsang.wftwitter.Activities.UserDetailActivity;
import app.com.phamsang.wftwitter.Object.Tweet;
import app.com.phamsang.wftwitter.data.Contract;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Quang Quang on 3/27/2016.
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
    private static final String LOG_TAG = TimelineAdapter.class.getSimpleName() ;
    private List<Tweet> mDataset = new ArrayList<Tweet>();
    private Context mContext;
    private long mSinceId;
    private long mMaxId;
    public TimelineAdapter(Context context) {
        mContext = context;
    }

    public List<Tweet> getDataset() {
        return mDataset;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Tweet data = mDataset.get(position);
        holder.mUserName.setText(data.getUser().getName());
        holder.mScreenName.setText("@" + data.getUser().getScreenName());
        holder.mTime.setText(Utilities.twitterTimeFormat(data.getTime()));
        holder.mLike.setText("" + data.getFavouriteCount());
        holder.mRetweet.setText("" + data.getRetweetCount());

        if (data.getImageUrl() != null) {
            holder.mImage.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(data.getImageUrl())
                    .placeholder(R.drawable.placeholder).dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.mImage);
        } else {
            holder.mImage.setVisibility(View.GONE);
        }

        holder.mTweetContent.setText(data.getText());
        Glide.with(mContext).load(data.getUser().getProfileUrl()).dontAnimate().into(holder.mProfileImageView);

        if (data.isFavorited()) {//if user like this tweet update color to blue
            Drawable image = ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_blue_24dp);
            holder.mLike.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
        } else {
            Drawable image = ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_black_24dp);
            holder.mLike.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
        }

        if (data.isRetweeted()) {//if user retweeted this tweet update color to blue
            Drawable image = ContextCompat.getDrawable(mContext, R.drawable.ic_autorenew_blue_24dp);
            holder.mRetweet.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
        } else {
            Drawable image = ContextCompat.getDrawable(mContext, R.drawable.ic_autorenew_black_24dp);
            holder.mRetweet.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
        }

        setUpButtonListioner(holder, data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tweet_layout, parent, false);

        return new ViewHolder(v);
    }

    public void swapdata(List<Tweet> newDataset) {
        mDataset = newDataset;
        if(mDataset!=null&&mDataset.size()!=0){
            updateMaxId(mDataset.get(mDataset.size()-1).getId());
            updateSinceId(mDataset.get(0).getId());
        }
        notifyDataSetChanged();
    }

    public void insertDataset(List<Tweet> data,int position){
        mDataset.addAll(position,data);
        if(mDataset!=null&&mDataset.size()!=0){
            updateMaxId(mDataset.get(mDataset.size()-1).getId());
            updateSinceId(mDataset.get(0).getId());
        }
        notifyItemRangeInserted(position,data.size());
    }


    private void setUpButtonListioner(final ViewHolder holder, final Tweet data) {
        holder.mRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwitterClient client = TwitterClient.getInstance(mContext);
                if (!data.isRetweeted()) {
                    client.reTweet(data.getId(), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
                                Drawable image = ContextCompat.getDrawable(mContext, R.drawable.ic_autorenew_blue_24dp);
                                holder.mRetweet.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
                                data.setRetweeted(true);
                                int retweet = data.getRetweetCount() + 1;
                                data.setRetweetCount(retweet);
                                holder.mRetweet.setText(String.valueOf(retweet));
                                int result = DatabaseUtilities.updateTweet(data.getId(), data.toContentValue(), mContext, Contract.TweetEntry.TABLE_NAME);
                                if (result == 0) {
                                    Log.i(LOG_TAG, "failed to update database, the tweet id: "+data.getId()+" maybe not in database");
                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                } else {
                    client.unRetweet(data.getId(), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
                                Drawable image = ContextCompat.getDrawable(mContext, R.drawable.ic_autorenew_black_24dp);
                                holder.mRetweet.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
                                data.setRetweeted(false);
                                int retweet = data.getRetweetCount() - 1;
                                data.setRetweetCount(retweet);
                                holder.mRetweet.setText(String.valueOf(retweet));
                                int result = DatabaseUtilities.updateTweet(data.getId(), data.toContentValue(), mContext, Contract.TweetEntry.TABLE_NAME);
                                if (result == 0) {
                                    Toast.makeText(mContext, "failed to update database", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                }

            }
        });

        holder.mLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwitterClient client = TwitterClient.getInstance(mContext);
                if (!data.isFavorited()) {
                    client.like(data.getId(), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
                                Drawable image = ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_blue_24dp);
                                holder.mLike.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
                                data.setFavorited(true);
                                int likesCount = data.getFavouriteCount() + 1;
                                data.setFavouriteCount(likesCount);
                                holder.mLike.setText(String.valueOf(likesCount));
                                int result = DatabaseUtilities.updateTweet(data.getId(), data.toContentValue(), mContext, Contract.TweetEntry.TABLE_NAME);
                                if (result == 0) {
                                    Log.i(LOG_TAG, "failed to update database, the tweet id: "+data.getId()+" maybe not in database");
                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                } else {
                    client.unLike(data.getId(), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
                                Drawable image = ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_black_24dp);
                                holder.mLike.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
                                data.setFavorited(false);
                                int likeCount = data.getFavouriteCount() - 1;
                                data.setFavouriteCount(likeCount);
                                holder.mLike.setText(String.valueOf(likeCount));
                                int result = DatabaseUtilities.updateTweet(data.getId(), data.toContentValue(), mContext, Contract.TweetEntry.TABLE_NAME);
                                if (result == 0) {
                                    Log.i(LOG_TAG, "failed to update database, the tweet id: "+data.getId()+" maybe not in database");
                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                }
            }
        });

        holder.mReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TweetComposerDialog dialog = TweetComposerDialog.newInstance(null, data);
                AppCompatActivity activity = (AppCompatActivity) mContext;
                dialog.show(activity.getSupportFragmentManager(), TweetComposerDialog.DIALOG_TAG);
            }
        });

        holder.mUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetailUserActivity(v,data);;
            }
        });
        holder.mProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetailUserActivity(v,data);
            }
        });
        holder.mScreenName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetailUserActivity(v,data);
            }
        });
        holder.mTweetContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetailTweetView(v,data, holder);
            }
        });
        holder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetailTweetView(v, data, holder);
            }
        });
    }

    private void startDetailUserActivity(View v, Tweet data) {
        Intent i = UserDetailActivity.getIntent(data.getUser(),mContext);
        AppCompatActivity activity = (AppCompatActivity)mContext;
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(activity, v, "profile");
        activity.startActivity(i,options.toBundle());
    }

    private void startDetailTweetView(View v, Tweet data, ViewHolder holder) {
        Intent i = TweetDetailActivity.createIntent(data,v.getContext());

        AppCompatActivity activity = (AppCompatActivity)mContext;
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(activity, (View)holder.mProfileImageView, "profile");
        if(mContext instanceof TimelineActivity){
            TimelineActivity timelineActivity = (TimelineActivity)activity;
            timelineActivity.startActivityForResult(i,TimelineActivity.TWEET_DETAILREQUEST_CODE,options.toBundle());
        }else
            activity.startActivity(i, options.toBundle());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView mProfileImageView;
        public TextView mUserName;
        public TextView mScreenName;
        public TextView mTime;
        public TextView mTweetContent;
        public ImageView mImage;
        public Button mReplyButton;
        public Button mRetweet;
        public Button mLike;
        public View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            mUserName = (TextView) v.findViewById(R.id.user_name_textView);
            mScreenName = (TextView) v.findViewById(R.id.screen_name_textView);
            mTime = (TextView) v.findViewById(R.id.time_textView);
            mTweetContent = (TextView) v.findViewById(R.id.tweet_content);
            mImage = (ImageView) v.findViewById(R.id.imageView_media);
            mReplyButton = (Button) v.findViewById(R.id.button_reply);

            mRetweet = (Button) v.findViewById(R.id.button_retweet);
            mLike = (Button) v.findViewById(R.id.buttom_like);
            mProfileImageView = (CircleImageView) v.findViewById(R.id.profile_image);
        }
    }
    private void updateSinceId(long sinceId) {
        mSinceId = sinceId;
        //mSetting.edit().putLong(SINCE_ID_KEY, sinceId).commit();
    }

    private void updateMaxId(long maxId) {
        mMaxId = maxId-1;
        //mSetting.edit().putLong(MAX_ID_KEY, maxId).commit();
    }

    public long getSinceId() {
        return mSinceId;
    }

    public long getMaxId() {
        return mMaxId;
    }
}
