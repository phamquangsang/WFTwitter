package app.com.phamsang.wftwitter.Activities;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.com.phamsang.wftwitter.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class FollowingActivityFragment extends Fragment {

    public FollowingActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_following, container, false);
    }
}
