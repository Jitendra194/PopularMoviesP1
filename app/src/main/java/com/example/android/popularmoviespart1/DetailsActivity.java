package com.example.android.popularmoviespart1;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmoviespart1.utilities.ExtractImageColor;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private Movie mMovie;

    private ProgressBar mImageProgressBar;

    private TextView mMovieTitle;
    private TextView mReleaseDate;
    private TextView mMovieOverview;
    private TextView mReleaseDateTitle;
    private TextView mOverviewTitle;
    private TextView mRatingText;
    private TextView mRatingTitle;

    private ImageView mLargePoster;

    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMovie = (Movie) getIntent().getSerializableExtra("Movie");
        setContentView(R.layout.activity_details);

        mImageProgressBar = (ProgressBar) findViewById(R.id.detail_progress_bar);

        mMovieTitle = (TextView) findViewById(R.id.detail_movie_name);
        mReleaseDate = (TextView) findViewById(R.id.release_date);
        mMovieOverview = (TextView) findViewById(R.id.overview);
        mOverviewTitle = (TextView) findViewById(R.id.overview_title);
        mReleaseDateTitle = (TextView) findViewById(R.id.release_date_title);
        mRatingText = (TextView) findViewById(R.id.rating_number);
        mRatingTitle = (TextView) findViewById(R.id.rating_title);

        mLargePoster = (ImageView) findViewById(R.id.detail_image_view);

        mLinearLayout = (LinearLayout) findViewById(R.id.detail_title_layout);

        setData();
    }

    private void setData() {
        mRatingText.setText(String.valueOf(mMovie.getMovieRating()));
        mMovieOverview.setText(mMovie.getOverview());
        mReleaseDate.setText(dateformat());
        mMovieTitle.setText(mMovie.getTitle());
        setColorToViewAndLoadImage();
    }

    private String dateformat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date = simpleDateFormat.parse(String.valueOf(mMovie.getDate()));
            return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private synchronized void setColorToViewAndLoadImage() {
        mImageProgressBar.setVisibility(View.VISIBLE);
        final ActionBar actionBar = getSupportActionBar();
        ExtractImageColor mExtractImageColor = new ExtractImageColor(mLargePoster) {
            @Override
            public void setSwatchToViews(Palette.Swatch swatch) {


                mRatingTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));

                mReleaseDateTitle.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.colorAccent));

                mOverviewTitle.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.colorAccent));

                mMovieTitle.setTextColor(swatch.getTitleTextColor());
                mLinearLayout.setBackgroundColor(swatch.getRgb());
                if (actionBar != null) {
                    actionBar.setBackgroundDrawable(new ColorDrawable(swatch.getRgb()));
                }
                titleColor(swatch);
                backArrow(swatch);
                statusBar(swatch);
            }

            @Override
            public void onSuccess() {
                mImageProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                Log.e(TAG, getString(R.string.detail_activity_error));
            }
        };
        Picasso.with(this).load(mMovie.getLargePosterUrl())
                .into(mExtractImageColor);
    }

    private void titleColor(Palette.Swatch swatch) {
        SpannableString s = new SpannableString(getString(R.string.movie_detail));
        s.setSpan(new ForegroundColorSpan(swatch.getTitleTextColor()), 0,
                getString(R.string.movie_detail).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(s);
        }
    }

    private void backArrow(Palette.Swatch swatch) {
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back);
        upArrow.setColorFilter(swatch.getTitleTextColor(), PorterDuff.Mode.SRC_ATOP);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
    }

    private void statusBar(Palette.Swatch swatch) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(manipulateColor(swatch.getRgb()));
        }
    }

    private int manipulateColor(int color) {
        float factor = 0.8f;
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }
}
