package com.daasuu.camerarecorder;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import com.kalasa.library.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
public class LikeButtonView extends FrameLayout implements View.OnClickListener {
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    private ImageView ivStar=(ImageView)findViewById(R.id.ivStar);
    private DotsView vDotsView=(DotsView)findViewById(R.id.vDotsView);
    private CircleView vCircle=(CircleView)findViewById(R.id.vCircle);
    private boolean isChecked;
    private AnimatorSet animatorSet;

    public LikeButtonView(Context context) {
        super(context);
        init();
    }

    public LikeButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public LikeButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ivStar=(ImageView)findViewById(R.id.ivStar);
        vDotsView=(DotsView)findViewById(R.id.vDotsView);
        CircleView vCircle=(CircleView)findViewById(R.id.vCircle);
        init();
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LikeButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_like_button, this, true);
        setOnClickListener(this);
        ivStar=(ImageView)findViewById(R.id.ivStar);
        vDotsView=(DotsView)findViewById(R.id.vDotsView);
        CircleView vCircle=(CircleView)findViewById(R.id.vCircle);
        ThisOnCLiked();
    }
    @Override
    public void onClick(View v) {
        ThisOnCLiked();
    }
void ThisOnCLiked()
{
    ivStar=(ImageView)findViewById(R.id.ivStar);
    vDotsView=(DotsView)findViewById(R.id.vDotsView);
    CircleView vCircle=(CircleView)findViewById(R.id.vCircle);
    isChecked = !isChecked;
    ivStar.setImageResource(isChecked ? R.drawable.ic_star_rate_on : R.drawable.ic_star_rate_off);
    if (animatorSet != null) {
        animatorSet.cancel();
    }

    if (isChecked) {
        ivStar.animate().cancel();
        ivStar.setScaleX(0);
        ivStar.setScaleY(0);
        vCircle.setInnerCircleRadiusProgress(0);
        vCircle.setOuterCircleRadiusProgress(0);
        vDotsView.setCurrentProgress(0);

        animatorSet = new AnimatorSet();

        ObjectAnimator outerCircleAnimator = ObjectAnimator.ofFloat(vCircle, CircleView.OUTER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
        outerCircleAnimator.setDuration(250);
        outerCircleAnimator.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator innerCircleAnimator = ObjectAnimator.ofFloat(vCircle, CircleView.INNER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
        innerCircleAnimator.setDuration(200);
        innerCircleAnimator.setStartDelay(200);
        innerCircleAnimator.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator starScaleYAnimator = ObjectAnimator.ofFloat(ivStar, ImageView.SCALE_Y, 0.2f, 1f);
        starScaleYAnimator.setDuration(350);
        starScaleYAnimator.setStartDelay(250);
        starScaleYAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator starScaleXAnimator = ObjectAnimator.ofFloat(ivStar, ImageView.SCALE_X, 0.2f, 1f);
        starScaleXAnimator.setDuration(350);
        starScaleXAnimator.setStartDelay(250);
        starScaleXAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator dotsAnimator = ObjectAnimator.ofFloat(vDotsView, DotsView.DOTS_PROGRESS, 0, 1f);
        dotsAnimator.setDuration(900);
        dotsAnimator.setStartDelay(50);
        dotsAnimator.setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR);

        animatorSet.playTogether(
                outerCircleAnimator,
                innerCircleAnimator,
                starScaleYAnimator,
                starScaleXAnimator,
                dotsAnimator
        );

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                vCircle.setInnerCircleRadiusProgress(0);
                vCircle.setOuterCircleRadiusProgress(0);
                vDotsView.setCurrentProgress(0);
                ivStar.setScaleX(1);
                ivStar.setScaleY(1);
            }
        });

        animatorSet.start();
    }
}
    public boolean isChecked() {
        return isChecked;
    }
}
