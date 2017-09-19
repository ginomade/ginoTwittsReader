package com.twitter.gino.twits;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.gino.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;

public class DetailsFragment extends Fragment {

    public static DetailsFragment newInstance() {
        Bundle args = new Bundle();

        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        final ImageView vAvatar = (ImageView) view.findViewById(R.id.my_tweet_avatar);
        final ImageView vTweetImage = (ImageView) view.findViewById(R.id.my_tweet_image);
        final TextView vTweetUser = (TextView) view.findViewById(R.id.my_tweet_user);
        final TextView vTweetText = (TextView) view.findViewById(R.id.my_tweet_text);
        final RelativeLayout vHeader = (RelativeLayout) view.findViewById(R.id.my_tweet_header);

        final long tweetId = args.getLong(MainFragment.TWEET_ID);

        vHeader.setTransitionName(String.valueOf(tweetId));

        TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                final Tweet tweet = result.data;

                Picasso.with(getContext()).load(tweet.user.profileImageUrl).into(vAvatar);
                if (!tweet.entities.media.isEmpty()) {
                    Picasso.with(getContext()).load(tweet.entities.media.get(0).mediaUrl).into(vTweetImage);
                }
                vTweetUser.setText(tweet.user.name);
                vTweetText.setText(tweet.text);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.e("DetailsFragment", "error loading tweet details.");
            }
        });

    }
}