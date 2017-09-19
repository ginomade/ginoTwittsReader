package com.twitter.gino.twits;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.twitter.gino.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TweetLinkClickListener;
import com.twitter.sdk.android.tweetui.TweetMediaClickListener;
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;

/**
 * Created by Gino on 17/09/2017.
 */

public class MainFragment extends Fragment {

    private TwitterLoginButton loginButton;
    private String SEARCH_QUERY;
    public static final String TWEET_ID = "TWEET_ID";

    EditText vSearch;
    Context context;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginButton = (TwitterLoginButton) view.findViewById(R.id.twitter_login_button);
        vSearch = (EditText) view.findViewById(R.id.editTextSearch);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_timeline);
        SEARCH_QUERY = "android";
        setUpTimeline(SEARCH_QUERY);
        setupSearch();
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpTimeline(SEARCH_QUERY);
    }

    private void setupSearch() {

        vSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setUpTimeline(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setUpTimeline(String searchString) {

        SearchTimeline searchTimeline = new SearchTimeline.Builder().query(searchString).build();


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        CustomAdapter adapter = new CustomAdapter(getContext(), searchTimeline);
        recyclerView.setAdapter(adapter);
    }


    class CustomAdapter extends TweetTimelineRecyclerViewAdapter {

        public CustomAdapter(Context context, Timeline<Tweet> timeline) {
            super(context, timeline);
        }

        public CustomAdapter(Context context, Timeline<Tweet> timeline, int styleResId, Callback<Tweet> cb) {
            super(context, timeline, styleResId, cb);
        }

        @Override
        public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final Tweet tweet = new TweetBuilder().build();
            final CompactTweetView compactTweetView = new CompactTweetView(context, tweet, styleResId);
            compactTweetView.setOnActionCallback(actionCallback);
            compactTweetView.setTweetLinkClickListener(new TweetLinkClickListener() {
                @Override
                public void onLinkClick(Tweet tweet, String url) {
                    Log.d("TweetLinkClicked", "tweet = " + tweet.text + "url = " + url);
                }
            });
            compactTweetView.setTweetMediaClickListener(new TweetMediaClickListener() {
                @Override
                public void onMediaEntityClick(Tweet tweet, MediaEntity entity) {
                    Log.d("TweetMediaClicked", "tweet = " + tweet.text);
                }
            });

            compactTweetView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        DetailsFragment detailsFragment = DetailsFragment.newInstance();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            detailsFragment.setSharedElementEnterTransition(new DetailsTransition());
                            detailsFragment.setEnterTransition(new Fade());
                            detailsFragment.setExitTransition(new Fade());
                            detailsFragment.setSharedElementReturnTransition(new DetailsTransition());
                        }

                        Bundle bundle = new Bundle();
                        bundle.putLong(TWEET_ID, compactTweetView.getTweetId());
                        detailsFragment.setArguments(bundle);
                        compactTweetView.getChildAt(1).setTransitionName(String.valueOf(compactTweetView.getTweetId()));

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addSharedElement(compactTweetView.getChildAt(1), String.valueOf(compactTweetView.getTweetId()))
                                .replace(R.id.container, detailsFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                    return true;
                }
            });

            return new TweetViewHolder(compactTweetView);
        }

    }
}
