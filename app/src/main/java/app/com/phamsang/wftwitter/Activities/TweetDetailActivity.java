package app.com.phamsang.wftwitter.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import app.com.phamsang.wftwitter.EndlessRecyclerViewScrollListener;
import app.com.phamsang.wftwitter.Object.Tweet;
import app.com.phamsang.wftwitter.R;
import app.com.phamsang.wftwitter.TimelineAdapter;
import app.com.phamsang.wftwitter.TwitterClient;
import app.com.phamsang.wftwitter.Utilities;
import de.hdodenhof.circleimageview.CircleImageView;

public class TweetDetailActivity extends AppCompatActivity {
    private static final int CHAR_LITMITED = 140 ;

    private static final String LOG_TAG = TweetDetailActivity.class.getSimpleName();
    public CircleImageView mProfileImageView;
    public TextView mUserName;
    public TextView mScreenName;
    public TextView mTime;
    public TextView mTweetContent;
    public ImageView mImage;
    public Button mReplyButton;
    public Button mRetweet;
    public Button mLike;
    public Tweet mTweet;

    private EditText mContent;
    private TextView mCharCounter;
    private Button mTweetButton;

    public static Intent createIntent(Tweet data, Context context){
        Intent i = new Intent(context, TweetDetailActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("TWEET_ARG",data);
        i.putExtras(b);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTweet = getIntent().getExtras().getParcelable("TWEET_ARG");
        setContentView(R.layout.activity_tweet_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUserName = (TextView)  findViewById(R.id.user_name_textView);
        mScreenName = (TextView)  findViewById(R.id.screen_name_textView);
        mTime = (TextView)  findViewById(R.id.time_textView);
        mTweetContent = (TextView)  findViewById(R.id.tweet_content);
        mImage = (ImageView)  findViewById(R.id.imageView_media);
        mReplyButton = (Button)  findViewById(R.id.button_reply);
        mReplyButton.setVisibility(View.GONE);
        mRetweet = (Button)  findViewById(R.id.button_retweet);
        mLike = (Button)  findViewById(R.id.buttom_like);
        mProfileImageView = (CircleImageView)  findViewById(R.id.profile_image);


        mCharCounter = (TextView)findViewById(R.id.textView_char_remain);
        mTweetButton = (Button)findViewById(R.id.button_post);
        mTweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo upload to twitter then notify timelineACtivity if succeed;
                TwitterClient client = TwitterClient.getInstance(TweetDetailActivity.this);
                Long replyId = 0L;
                replyId = mTweet.getId();

                String userMention="";
                if(mTweet.getUsersMention()!=null)
                     userMention = mTweet.getUsersMention()+" ";
                client.updateStatus("@"+mTweet.getUser().getScreenName()+" " + userMention+ mContent.getText().toString(), replyId, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if(statusCode==200){
                            //notify timeline activity
                            mContent.setText("");
                            mCharCounter.setText("");
                            TweetDetailActivity.this.setResult(RESULT_OK);
                        }else{
                            Toast.makeText(TweetDetailActivity.this,"failed to post the Tweet - status code: "+statusCode,Toast.LENGTH_LONG).show();
                            Log.e(LOG_TAG,"failed to post the Tweet - status code: "+statusCode);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
            }
        });
        mContent = (EditText)findViewById(R.id.editText_compose_content);
        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int remainCount = CHAR_LITMITED- s.length();
                mCharCounter.setText(String.valueOf(remainCount));
            }
        });

        bindView();
    }

    private void bindView() {
        final Tweet data = mTweet;
        mUserName.setText(data.getUser().getName());
        mScreenName.setText("@" + data.getUser().getScreenName());
        mTime.setText(Utilities.twitterTimeFormat(data.getTime()));
        mLike.setText("" + data.getFavouriteCount());
        mRetweet.setText("" + data.getRetweetCount());

        if (data.getImageUrl() != null) {
            mImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(data.getImageUrl())
                    .placeholder(R.drawable.placeholder).dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mImage);
        } else {
            mImage.setVisibility(View.GONE);
        }

        mTweetContent.setText(data.getText());
        Glide.with(this).load(data.getUser().getProfileUrl()).dontAnimate().into(mProfileImageView);

        if (data.isFavorited()) {//if user like this tweet update color to blue
            Drawable image = ContextCompat.getDrawable(this, R.drawable.ic_favorite_blue_24dp);
            mLike.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
        } else {
            Drawable image = ContextCompat.getDrawable(this, R.drawable.ic_favorite_black_24dp);
            mLike.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
        }

        if (data.isRetweeted()) {//if user retweeted this tweet update color to blue
            Drawable image = ContextCompat.getDrawable(this, R.drawable.ic_autorenew_blue_24dp);
            mRetweet.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
        } else {
            Drawable image = ContextCompat.getDrawable(this, R.drawable.ic_autorenew_black_24dp);
            mRetweet.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
        }

        setUpButtonListener(data);
    }

    public void setUpButtonListener(Tweet data) {
    }
}
