package app.com.phamsang.wftwitter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import app.com.phamsang.wftwitter.Object.Tweet;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Quang Quang on 3/27/2016.
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
    private List<Tweet> mDataset = new ArrayList<Tweet>();
    private Context mContext;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet data = mDataset.get(position);
        holder.mUserName.setText(data.getUser().getName());
        holder.mScreenName.setText(data.getUser().getScreenName());
        holder.mTime.setText(Utilities.twitterTimeFormat(data.getTime()));
        holder.mLike.setText(""+data.getFavouriteCount());
        holder.mRetweet.setText(""+data.getRetweetCount());
        if(data.getImageUrl()!=null){
            holder.mImage.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(data.getImageUrl()).placeholder(R.drawable.placeholder).dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.mImage);
        }else{
            holder.mImage.setVisibility(View.GONE);
        }
        holder.mTweetContent.setText(data.getText());
        Glide.with(mContext).load(data.getUser().getProfileUrl()).dontAnimate().into(holder.mProfileImageView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tweet_layout,parent,false);

        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
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
        public ViewHolder(View v){
            super(v);
            mView = v;
            mUserName = (TextView)v.findViewById(R.id.user_name_textView);
            mScreenName = (TextView)v.findViewById(R.id.screen_name_textView);
            mTime = (TextView)v.findViewById(R.id.time_textView);
            mTweetContent = (TextView)v.findViewById(R.id.tweet_content);
            mImage = (ImageView)v.findViewById(R.id.imageView_media);
            mReplyButton = (Button)v.findViewById(R.id.button_reply);
            mRetweet = (Button)v.findViewById(R.id.button_retweet);
            mLike = (Button)v.findViewById(R.id.buttom_like);
            mProfileImageView = (CircleImageView)v.findViewById(R.id.profile_image);
        }
    }

    public void swapdata(List<Tweet> newDataset){
        mDataset = newDataset;
        notifyDataSetChanged();
    }
    public void insertData(List<Tweet> newDataset){

//        mDataset.addAll(newDataset);
//        notifyItemRangeInserted();
    }
}
