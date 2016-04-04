package app.com.phamsang.wftwitter.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import app.com.phamsang.wftwitter.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MediaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MediaFragment extends Fragment {

    private static final String LOG_TAG = MediaFragment.class.getSimpleName() ;
    private String mScreenName;
    private RecyclerView mRecyclerView;
    private MediaAdapter mAdapter;
    private static final String USER_SCREEN_NAME_ARGS = "user_screen_name_arg" ;




    public MediaFragment() {
        // Required empty public constructor
    }

    public static MediaFragment newInstance(String screenName) {
        MediaFragment fragment = new MediaFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_media, container, false);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.media_fragment_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new MediaAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        return v;

    }

    class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder>{
        private Context mContext;
        public MediaAdapter(Context c) {
            super();
            mContext = c;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_media_item, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (position){
                case 1:
                    Glide.with(mContext).load(R.drawable.image1).into(holder.mImageView);
                    return;
                case 2:
                    Glide.with(mContext).load(R.drawable.image2).into(holder.mImageView);
                    return;
                case 3:
                    Glide.with(mContext).load(R.drawable.image3).into(holder.mImageView);
                    return;
                case 4:
                    Glide.with(mContext).load(R.drawable.image4).into(holder.mImageView);
                    return;
                case 5:
                    Glide.with(mContext).load(R.drawable.image5).into(holder.mImageView);
                    return;
                case 6:
                    Glide.with(mContext).load(R.drawable.image6).into(holder.mImageView);
                    return;
            }
        }

        @Override
        public int getItemCount() {
            return 6;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            ImageView mImageView;
            public ViewHolder(View itemView) {
                super(itemView);
                mImageView = (ImageView)itemView.findViewById(R.id.media_fragment_image);
            }
        }
    }
}
