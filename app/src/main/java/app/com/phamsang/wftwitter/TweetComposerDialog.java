package app.com.phamsang.wftwitter;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import app.com.phamsang.wftwitter.Object.User;
import app.com.phamsang.wftwitter.data.Contract;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Quang Quang on 3/28/2016.
 */
public class TweetComposerDialog extends DialogFragment {
    private static final String USER_EXTRA = "user_extra";
    private static final String LOG_TAG = TweetComposerDialog.class.getSimpleName();
    private NoticeDialogListener mListener;
    private ImageView mCloseButton;
    private EditText mContent;
    private CircleImageView mProfileImage;
    private TextView mCharCounter;
    private Button mTweetButton;
    private User mUser;

    public TweetComposerDialog() {
        super();
    }

    public static TweetComposerDialog newInstance(User user){
        Bundle bundle = new Bundle();
        bundle.putParcelable(USER_EXTRA,user);
        TweetComposerDialog composer = new TweetComposerDialog();
        composer.setArguments(bundle);
        return composer;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle bundle = getArguments();
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View rootView = inflater.inflate(R.layout.tweet_composer, null);
        mCloseButton = (ImageView)rootView.findViewById(R.id.imageView_discard);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TweetComposerDialog.this.dismiss();
            }
        });
        mContent = (EditText)rootView.findViewById(R.id.editText_compose_content);
        mProfileImage = (CircleImageView)rootView.findViewById(R.id.imageView_profile_composer);
        mCharCounter = (TextView)rootView.findViewById(R.id.textView_char_remain);
        mTweetButton = (Button)rootView.findViewById(R.id.button_post);
        mTweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo upload to twitter then notify timelineACtivity if succeed;
                TwitterClient client = TwitterClient.getInstance(getActivity());
                client.updateStatus(mContent.getText().toString(), 0L, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if(statusCode==200){
                            mListener.onDialogPostButtonClick();
                            TweetComposerDialog.this.dismiss();
                        }else{
                            Toast.makeText(getActivity(),"failed to post the Tweet - status code: "+statusCode,Toast.LENGTH_LONG).show();
                            Log.e(LOG_TAG,"failed to post the Tweet - status code: "+statusCode);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
            }
        });
        builder.setView(rootView);

        Bundle arg = getArguments();
        mUser = arg.getParcelable(USER_EXTRA);
        if(mUser!=null){
            Glide.with(getActivity()).load(mUser.getProfileUrl()).into(mProfileImage);

        }
        return builder.create();
    }

    public interface NoticeDialogListener {
        public void onDialogPostButtonClick();
    }
    // Use this instance of the interface to deliver action events


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener=null;
    }
}
