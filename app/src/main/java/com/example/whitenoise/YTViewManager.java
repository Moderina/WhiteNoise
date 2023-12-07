package com.example.whitenoise;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class YTViewManager extends ConstraintLayout {

    Context context;
    ConstraintLayout playerView, miniPlayerView;
    ImageView background1, background2;
    Animation minimize, maximize;
    TextView playerCloseBtn;

    YouTubePlayerView yt_player;
    String url;



    public YTViewManager(@NonNull Context context, YouTubePlayerView yt, ConstraintLayout yt_player_view, ConstraintLayout miniPlayerView, ImageView background1, ImageView background2, TextView playerCloseBtn) {
        super(context);
        this.context = context;
        yt_player = yt;
        playerView = yt_player_view;
        this.miniPlayerView = miniPlayerView;
        this.background1 = background1;
        this.background2 = background2;
        this.playerCloseBtn = playerCloseBtn;
        minimize = AnimationUtils.loadAnimation(context, R.anim.minimize);
        maximize = AnimationUtils.loadAnimation(context, R.anim.maximize);
        playerControls();
        backgroundAnim();
        playerEvents();
    }

    public void playerEvents() {
    }

    private void playerControls() {
        playerCloseBtn.setOnClickListener(view -> exitPlayerView());
//        miniPlayerView.setOnClickListener(view -> openPlayerView(view));
    }

    private void exitPlayerView() {
        miniPlayerView.startAnimation(maximize);
        playerView.startAnimation(minimize);
        miniPlayerView.setVisibility(View.VISIBLE);
        playerView.setVisibility(View.GONE);
    }

    public void openPlayerView() {
//        ((MainActivity) context).searchView.setIconified(true);
//        ((MainActivity) context).searchView.onActionViewCollapsed();
//        StaticClass.hideKeyboardFrom(context, view);
        miniPlayerView.startAnimation(minimize);
        playerView.startAnimation(maximize);
        playerView.setVisibility(View.VISIBLE);
        miniPlayerView.setVisibility(GONE);
    }

    private void backgroundAnim()
    {
        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(100000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float height = background1.getHeight();
                final float translationY = height * progress;
                background1.setTranslationY(translationY);
                background2.setTranslationY(translationY - height + 20);
            }
        });
        animator.start();
    }
}
