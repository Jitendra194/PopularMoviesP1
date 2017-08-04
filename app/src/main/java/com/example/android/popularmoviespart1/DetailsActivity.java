package com.example.android.popularmoviespart1;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviespart1.MoviesData.MovieResponse;
import com.example.android.popularmoviespart1.data.MoviesContract.MoviesEntry;
import com.example.android.popularmoviespart1.utilities.MovieLoader;
import com.example.android.popularmoviespart1.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private static final int NULL_MODE = 0;
    private static final int INSERT_MODE = 1;
    private static final int UPDATE_MODE = 2;


    private int mMovieId;
    private Uri mUri;

    public static final int MOVIE_DETAIL_ID = 3;
    public static final int MOVIE_VIDEO_ID = 5;
    public static final int MOVIE_REVIEW_ID = 6;
    private static final int MOVIE_CURSOR_ID = 4;


    private static String YOUTUBE_KEY;
    private static String REVIEW_KEY;

    private MovieResponse mMovieData;
    private ArrayList<MovieResponse.Results> mMovieVideos = null;


    private ArrayList<MovieResponse.Results> mMovieReviews = null;

    private ProgressBar mLayoutProgressBar;
    private TextView mTrailerTitle;
    private TextView mReleaseDate;
    private TextView mMovieOverview;
    private TextView mRatingText;
    private TextView mDurationNumber;

    private TextView mBottomSheetTitle;
    private TextView mAuthorReview;

    private TextView mReviewAuthor;
    private TextView mErrorMessage4;

    private TextView mErrorMessage5;
    private ImageView mLargePoster;

    private ImageView mSmallPoster;

    private FloatingActionButton mFavStar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private BottomSheetBehavior bottomSheetBehavior;

    private View mShadow;

    private CoordinatorLayout layout;
    private LinearLayout mSingleTrailerCardLayout;

    private CardView mSingleReviewCardLayout;

    private int mFav;

    private Bundle bundle;
    private Button mRefreshButton;


    private boolean updateTask = false;
    private RecyclerView mBottomSheetRecyclerView;

    private VideosAndReviewsAdapter mVideoAdapter;
    private static final String[] MOVIE_FAVORITE_PROJECTION = {
            MoviesEntry.COLUMN_MOVIE_ID,
            MoviesEntry.COLUMN_MOVIE_TITLE,
            MoviesEntry.COLUMN_MOVIE_RATING,
            MoviesEntry.COLUMN_MOVIE_RELEASE_DATE,
            MoviesEntry.COLUMN_MOVIE_SYNOPSIS,
            MoviesEntry.COLUMN_MOVIE_DURATION,
            MoviesEntry.COLUMN_MOVIE_POSTER_IMAGE_PATH,
            MoviesEntry.COLUMN_MOVIE_BACKDROP_IMAGE_PATH,
            MoviesEntry.COLUMN_MOVIE_VIDEO_NAME,
            MoviesEntry.COLUMN_MOVIE_VIDEO_LINK_ID,
            MoviesEntry.COLUMN_MOVIE_REVIEW,
            MoviesEntry.COLUMN_MOVIE_REVIEW_AUTHOR,
            MoviesEntry.COLUMN_MOVIE_REVIEW_LINK
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.new_activity_detail_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setStatusBarTranslucent();


        mLayoutProgressBar = (ProgressBar) findViewById(R.id.detail_progress_bar);
        mLayoutProgressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY
        );

        mReleaseDate = (TextView) findViewById(R.id.release_date);
        mMovieOverview = (TextView) findViewById(R.id.overview);
        mRatingText = (TextView) findViewById(R.id.rating_number);
        mDurationNumber = (TextView) findViewById(R.id.duration_number);
        TextView mViewMoreVideos = (TextView) findViewById(R.id.view_more_trailers);
        mTrailerTitle = (TextView) findViewById(R.id.video_title);
        mAuthorReview = (TextView) findViewById(R.id.author_review);
        mReviewAuthor = (TextView) findViewById(R.id.review_author);
        TextView mViewMoreReviews = (TextView) findViewById(R.id.view_more_reviews);
        mBottomSheetTitle = (TextView) findViewById(R.id.bottom_sheet_title);

        mErrorMessage4 = (TextView) findViewById(R.id.error_message_4);
        mErrorMessage5 = (TextView) findViewById(R.id.error_message_5);

        mLargePoster = (ImageView) findViewById(R.id.detail_image_view);
        mSmallPoster = (ImageView) findViewById(R.id.small_image);

        mFavStar = (FloatingActionButton) findViewById(R.id.rating_star);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_bar);
        mSingleTrailerCardLayout = (LinearLayout) findViewById(R.id.trailer_card_layout1);
        mSingleReviewCardLayout = (CardView) findViewById(R.id.review_card_layout);

        layout = (CoordinatorLayout) findViewById(R.id.detail_layout);
        layout.setVisibility(View.INVISIBLE);

        mRefreshButton = (Button) findViewById(R.id.refresh_button);

        setUpBottomSheetRecyclerView();

        setUpBottomSheet();

        getMovieIdAndInitLoaders();
        mFavStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFav == 0) {
                    setFavButton(R.drawable.ic_star);
                    saveMovie();
                } else if (mFav == 1) {
                    setFavButton(R.drawable.ic_star_border_black_24dp);
                    deleteMovie();
                }
            }
        });

        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRefreshButton.setVisibility(View.INVISIBLE);
                getSupportLoaderManager().destroyLoader(MOVIE_DETAIL_ID);
                getSupportLoaderManager().destroyLoader(MOVIE_CURSOR_ID);
                getSupportLoaderManager()
                        .initLoader(MOVIE_CURSOR_ID, null, cursorLoaderCallbacks);
            }
        });

        mViewMoreVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkForInternetConnection()) {
                    if (mMovieVideos == null) {
                        Toast.makeText(DetailsActivity.this, R.string.no_videos_available, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mShadow.setVisibility(View.VISIBLE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    mVideoAdapter = new VideosAndReviewsAdapter(mMovieVideos, VideosAndReviewsAdapter.VIDEOS);
                    mVideoAdapter.getItemViewType(VideosAndReviewsAdapter.VIDEOS);
                    mBottomSheetRecyclerView.setAdapter(mVideoAdapter);
                    mBottomSheetTitle.setText(R.string.trailer_title);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    Toast.makeText(DetailsActivity.this, R.string.please_connect_to_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mShadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        mViewMoreReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkForInternetConnection()) {
                    if (mMovieReviews == null) {
                        Toast.makeText(DetailsActivity.this, R.string.no_reviews_available, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mShadow.setVisibility(View.VISIBLE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    mVideoAdapter = new VideosAndReviewsAdapter(mMovieReviews, VideosAndReviewsAdapter.REVIEWS);
                    mVideoAdapter.getItemViewType(VideosAndReviewsAdapter.REVIEWS);
                    mBottomSheetRecyclerView.setAdapter(mVideoAdapter);
                    mBottomSheetTitle.setText(R.string.reviews_title);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    Toast.makeText(DetailsActivity.this, R.string.please_connect_to_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUpBottomSheetRecyclerView() {
        mBottomSheetRecyclerView = (RecyclerView) findViewById(R.id.bottom_sheet_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBottomSheetRecyclerView.setLayoutManager(layoutManager);
    }

    private void setUpBottomSheet() {

        mShadow = findViewById(R.id.shadow);
        mShadow.setVisibility(View.INVISIBLE);
        ConstraintLayout llBottomSheet = (ConstraintLayout) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetBehavior.setHideable(true);

        bottomSheetBehavior.setPeekHeight(280);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset == -1) {
                    mShadow.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void deleteMovie() {
        mFav = 0;
        int rowsDeleted = getContentResolver().delete(mUri, null, null);
    }

    private void saveMovie() {
        new AsyncSaveTask(INSERT_MODE).execute();
    }

    private void setFavButton(int drawable) {
        mFavStar.setImageResource(drawable);
        mFavStar.getDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(),
                android.R.color.white), PorterDuff.Mode.SRC_ATOP);
    }

    private void getMovieIdAndInitLoaders() {
        mUri = getIntent().getData();
        mMovieId = getIntent().getIntExtra("MovieId", 0);
        bundle = new Bundle();
        bundle.putInt("MovieId", mMovieId);

        getSupportLoaderManager().initLoader(MOVIE_CURSOR_ID, null, cursorLoaderCallbacks);
    }

    private boolean checkForInternetConnection() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        return mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting();
    }

    private void setData() {
        mRatingText.setText(String.valueOf(mMovieData.getVote_average()));
        mMovieOverview.setText(mMovieData.getOverview());
        mReleaseDate.setText(dateformat());

        collapsingToolbarLayout.setTitle(mMovieData.getTitle());
        mDurationNumber.setText(mMovieData.getRuntime());
        setImages();
    }

    private void setTrailer() {
        if (mMovieVideos == null) {
            mTrailerTitle.setText(R.string.no_trailers_are_available);
            return;
        }
        mTrailerTitle.setText(mMovieVideos.get(0).name);
        mSingleTrailerCardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                youtubeLink();
            }
        });
        mLargePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                youtubeLink();
            }
        });
    }

    private void setReviews() {
        if (mMovieReviews == null) {
            mAuthorReview.setText(R.string.no_reviews_are_available);
            return;
        }
        mAuthorReview.setText(mMovieReviews.get(0).content);
        mReviewAuthor.setText(mMovieReviews.get(0).author);

        mSingleReviewCardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkForInternetConnection()) {
                    String key;
                    if (REVIEW_KEY != null) {
                        key = REVIEW_KEY;
                    } else {
                        key = mMovieReviews.get(0).url;
                    }
                    Uri webpage = Uri.parse(key);
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(DetailsActivity.this, R.string.please_connect_to_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void youtubeLink() {

        String key;
        if (checkForInternetConnection()) {
            if (YOUTUBE_KEY != null) {
                key = YOUTUBE_KEY;
            } else {
                key = mMovieVideos.get(0).getKey();
            }
            Uri youtubeUri = Uri.parse(NetworkUtils.YOUTUBE_VIDEO_LINK + key);
            startActivity(new Intent(Intent.ACTION_VIEW, youtubeUri));
        } else {
            Toast.makeText(DetailsActivity.this, R.string.please_connect_to_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private void setImages() {
        if (updateTask) {
            new AsyncSaveTask(UPDATE_MODE).execute();
        } else {
            new AsyncSaveTask(NULL_MODE).execute();
        }
    }

    private String dateformat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(String.valueOf(mMovieData.getRelease_date()));
            return DateFormat.getDateInstance(DateFormat.LONG).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("CanBeFinal")
    private LoaderManager.LoaderCallbacks<MovieResponse> movieResponseLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<MovieResponse>() {
                @Override
                public Loader<MovieResponse> onCreateLoader(int id, Bundle args) {
                    if (args == null) {
                        Log.v(TAG, "movie Id error");
                        return null;
                    } else {
                        mLayoutProgressBar.setVisibility(View.VISIBLE);
                        layout.setVisibility(View.INVISIBLE);
                        return new MovieLoader(DetailsActivity.this, args);
                    }
                }

                @Override
                public void onLoadFinished(Loader<MovieResponse> loader, MovieResponse data) {
                    if (data != null) {
                        if (loader.getId() == MOVIE_VIDEO_ID ||
                                loader.getId() == MOVIE_REVIEW_ID) {
                            if (data.getResults().size() != 0) {
                                int id = loader.getId();
                                switch (id) {
                                    case MOVIE_VIDEO_ID:
                                        mMovieVideos = new ArrayList<MovieResponse.Results>();
                                        mMovieVideos = data.getResults();
                                        setTrailer();
                                        break;
                                    case MOVIE_REVIEW_ID:
                                        mMovieReviews = new ArrayList<MovieResponse.Results>();
                                        mMovieReviews = data.getResults();
                                        setReviews();
                                        break;
                                    default:
                                        Log.v(TAG, "Unknown id = " + id);
                                }
                            }
                            return;
                        }
                        if (loader.getId() == MOVIE_DETAIL_ID) {
                            mMovieData = data;
                        }
                        setData();
                        mLayoutProgressBar.setVisibility(View.INVISIBLE);
                        layout.setVisibility(View.VISIBLE);
                    } else {
                        errorMessage();
                    }
                }

                @Override
                public void onLoaderReset(Loader<MovieResponse> loader) {

                }
            };

    @SuppressWarnings("CanBeFinal")
    private LoaderManager.LoaderCallbacks<Cursor> cursorLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    mErrorMessage5.setVisibility(View.INVISIBLE);
                    mErrorMessage4.setVisibility(View.INVISIBLE);
                    switch (id) {

                        case MOVIE_CURSOR_ID:

                            return new CursorLoader(DetailsActivity.this,
                                    mUri,
                                    MOVIE_FAVORITE_PROJECTION,
                                    null,
                                    null,
                                    null);

                        default:
                            throw new RuntimeException("Loader Not Implemented: " + id);
                    }
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    boolean cursorHasValidData = false;
                    if (data != null && data.moveToFirst()) {
                        cursorHasValidData = true;
                    }
                    if (!cursorHasValidData) {
                        if (checkForInternetConnection()) {
                            mFav = 0;
                            setFavButton(R.drawable.ic_star_border_black_24dp);
                            getSupportLoaderManager().initLoader(MOVIE_DETAIL_ID, bundle, movieResponseLoaderCallbacks);
                            getSupportLoaderManager().initLoader(MOVIE_VIDEO_ID, bundle, movieResponseLoaderCallbacks);
                            getSupportLoaderManager().initLoader(MOVIE_REVIEW_ID, bundle, movieResponseLoaderCallbacks);
                        } else {
                            errorMessage();
                        }
                    } else {
                        if (checkForInternetConnection()) {
                            getSupportLoaderManager().initLoader(MOVIE_VIDEO_ID, bundle, movieResponseLoaderCallbacks);
                            getSupportLoaderManager().initLoader(MOVIE_REVIEW_ID, bundle, movieResponseLoaderCallbacks);
                        }
                        mFav = 1;
                        setFavButton(R.drawable.ic_star);
                        setDataFromDatabase(data);
                        mLayoutProgressBar.setVisibility(View.INVISIBLE);
                        layout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {

                }
            };

    private void setDataFromDatabase(Cursor cursor) {

        String name = cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_TITLE));
        String id = cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_ID));
        double rating = cursor.getDouble(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_RATING));
        String date = cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_RELEASE_DATE));
        String overview = cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_SYNOPSIS));
        String duration = cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_DURATION));
        String poster = cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_POSTER_IMAGE_PATH));
        String backdrop = cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_BACKDROP_IMAGE_PATH));
        String trailerName = cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_VIDEO_NAME));
        String trailerKey = cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_VIDEO_LINK_ID));
        String authorName = cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_REVIEW_AUTHOR));
        String authorReview = cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_REVIEW));
        String reviewLink = cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_REVIEW_LINK));


        mSmallPoster.setImageBitmap(loadImageFromStorage(poster, mMovieId + "_1"));
        mLargePoster.setImageBitmap(loadImageFromStorage(backdrop, mMovieId + "_2"));

        mRatingText.setText(String.valueOf(rating));
        mMovieOverview.setText(overview);
        mReleaseDate.setText(date);
        collapsingToolbarLayout.setTitle(name);
        mDurationNumber.setText(duration);
        mTrailerTitle.setText(trailerName);
        YOUTUBE_KEY = trailerKey;
        mAuthorReview.setText(authorReview);
        mReviewAuthor.setText(authorName);
        REVIEW_KEY = reviewLink;
    }

    private String storeImage(Bitmap bitmap, String movieId) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, movieId + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private static Bitmap loadImageFromStorage(String path, String s) {

        try {
            File f = new File(path, s + ".jpg");
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void errorMessage() {
        mRefreshButton.setVisibility(View.VISIBLE);
        mLayoutProgressBar.setVisibility(View.INVISIBLE);
        layout.setVisibility(View.INVISIBLE);
        mErrorMessage4.setVisibility(View.VISIBLE);
        mErrorMessage5.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();
        switch (selectedItem) {
            case R.id.detail_refresh_action:
                updateMovieDetails();
                break;
            case R.id.action_share_youtube_link:
                shareYoutubeLink();
                break;
            default:
                Log.v(TAG, "menu error");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareYoutubeLink() {
        if (checkForInternetConnection()) {
            if (mMovieVideos.get(0).key != null) {
                Uri youtubeUri = Uri.parse(NetworkUtils.YOUTUBE_VIDEO_LINK + mMovieVideos.get(0).key);
                String title = "Share youtube video..";
                String mimeType = "text/plain";
                ShareCompat
                        .IntentBuilder
                        .from(this)
                        .setChooserTitle(title)
                        .setType(mimeType)
                        .setText(youtubeUri.toString())
                        .startChooser();
            } else {
                Toast.makeText(this, R.string.no_trailers_available_for_sharing, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveTask(int saveMode) {


        String posterImagePath = storeImage(((BitmapDrawable) mSmallPoster.getDrawable()).getBitmap(),
                mMovieId + "_1");
        String backdropImagePath = storeImage(((BitmapDrawable) mLargePoster.getDrawable()).getBitmap(),
                mMovieId + "_2");


        ContentValues values = new ContentValues();
        values.put(MoviesEntry.COLUMN_MOVIE_ID, mMovieId);
        if (collapsingToolbarLayout.getTitle() != null) {
            values.put(MoviesEntry.COLUMN_MOVIE_TITLE, collapsingToolbarLayout.getTitle().toString());
        }
        values.put(MoviesEntry.COLUMN_MOVIE_RATING, Double.parseDouble(mRatingText.getText().toString()));
        values.put(MoviesEntry.COLUMN_MOVIE_RELEASE_DATE, mReleaseDate.getText().toString());
        values.put(MoviesEntry.COLUMN_MOVIE_SYNOPSIS, mMovieOverview.getText().toString());
        values.put(MoviesEntry.COLUMN_MOVIE_DURATION, mDurationNumber.getText().toString());
        values.put(MoviesEntry.COLUMN_MOVIE_POSTER_IMAGE_PATH, posterImagePath);
        values.put(MoviesEntry.COLUMN_MOVIE_BACKDROP_IMAGE_PATH, backdropImagePath);
        values.put(MoviesEntry.COLUMN_MOVIE_VIDEO_NAME, mTrailerTitle.getText().toString());
        values.put(MoviesEntry.COLUMN_MOVIE_VIDEO_LINK_ID, YOUTUBE_KEY);
        values.put(MoviesEntry.COLUMN_MOVIE_REVIEW, mAuthorReview.getText().toString());
        values.put(MoviesEntry.COLUMN_MOVIE_REVIEW_AUTHOR, mReviewAuthor.getText().toString());
        values.put(MoviesEntry.COLUMN_MOVIE_REVIEW_LINK, REVIEW_KEY);

        if (saveMode == INSERT_MODE) {
            Uri uri = getContentResolver().insert(MoviesEntry.CONTENT_URI, values);
        } else if (saveMode == UPDATE_MODE) {
            int updatedRows = getContentResolver().update(mUri, values, null, null);
        }
    }

    private void updateMovieDetails() {
        if (!checkForInternetConnection()) {
            Toast.makeText(this, R.string.please_connect_to_internet, Toast.LENGTH_SHORT).show();
            return;
        }
        updateTask = true;
        getSupportLoaderManager().restartLoader(MOVIE_DETAIL_ID, bundle, movieResponseLoaderCallbacks);
        getSupportLoaderManager().restartLoader(MOVIE_VIDEO_ID, bundle, movieResponseLoaderCallbacks);
    }

    public class AsyncSaveTask extends AsyncTask<Void, Void, Bitmap[]> {

        @SuppressWarnings("CanBeFinal")
        private int mInsertMode;


        public AsyncSaveTask(int insertMode) {
            mInsertMode = insertMode;
        }

        @Override
        protected Bitmap[] doInBackground(Void... voids) {
            try {
                return new Bitmap[]{
                        Picasso.with(DetailsActivity.this).load(mMovieData.getPoster_path()).get(),
                        Picasso.with(DetailsActivity.this).load(mMovieData.getBackdrop_path()).get()};
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap[] bitmaps) {
            super.onPostExecute(bitmaps);
            mSmallPoster.setImageBitmap(bitmaps[0]);
            mLargePoster.setImageBitmap(bitmaps[1]);
            if (mInsertMode == INSERT_MODE) {
                saveTask(INSERT_MODE);
            } else if (mInsertMode == UPDATE_MODE) {
                saveTask(UPDATE_MODE);
            }
        }
    }


    private void setStatusBarTranslucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED ||
                bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            if (mShadow.getVisibility() == View.VISIBLE) {
                mShadow.setVisibility(View.INVISIBLE);
            }
            return;
        }
        super.onBackPressed();
    }
}