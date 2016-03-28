package app.com.phamsang.wftwitter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import app.com.phamsang.wftwitter.Object.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailActivity extends AppCompatActivity {
    private ImageView mBackground;
    private CircleImageView mProfile;
    private TextView mName;
    private TextView mScreenName;
    private TextView mDescription;
    private TextView mLink;
    private TextView mFollowing;
    private TextView mFollower;

    public static void startDetailView(User user, Context c) {
        Bundle b = new Bundle();
        b.putParcelable("USER_EXTRA", user);
        Intent i = new Intent(c, UserDetailActivity.class);
        i.putExtras(b);
        c.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arg = getIntent().getExtras();
        User user = arg.getParcelable("USER_EXTRA");

        setContentView(R.layout.activity_user_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(user.getName());
        mBackground = (ImageView)findViewById(R.id.background);
        Glide.with(this).load(user.getBackgroundUrl()).placeholder(R.drawable.placeholder).into(mBackground);
        mProfile = (CircleImageView)findViewById(R.id.imageView_profile);
        Glide.with(this).load(user.getProfileUrl()).into(mProfile);
        mName = (TextView)findViewById(R.id.textView_Name);
        mName.setText(user.getName());
        mScreenName= (TextView)findViewById(R.id.textView_ScreenName);
        mScreenName.setText("@"+user.getScreenName());
        mDescription = (TextView)findViewById(R.id.textView_description);
        mDescription.setText(user.getDescription());
        mLink = (TextView)findViewById(R.id.textView_link);

        mFollowing = (TextView)findViewById(R.id.textView_following);
        mFollowing.setText(user.getFriendCount()+ " Following");
        mFollower  = (TextView)findViewById(R.id.textView_follower);
        mFollower.setText(user.getFollowersCount()+" Followers");
    }
}
