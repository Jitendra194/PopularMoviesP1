package com.example.android.popularmoviespart1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmoviespart1.MoviesData.Movie;
import com.example.android.popularmoviespart1.data.MoviesContract.MoviesEntry;
import com.example.android.popularmoviespart1.utilities.EndlessRecyclerViewScrollListener;
import com.example.android.popularmoviespart1.utilities.MoviesLoader;
import com.example.android.popularmoviespart1.utilities.NetworkUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.ListItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private ProgressBar mProgress;
    private TextView mError1;
    private TextView mError2;
    private TextView noFavMovies1;
    private TextView noFavMovies2;
    private static final int LANDSCAPE_GRIDVIEW_COLUMNS = 3;
    private static final int PORTRAIT_GRIDVIEW_COLUMNS = 2;


    private boolean isRefreshed = false;
    private boolean isClicked = false;

    public static int MOVIE_LOADER_ID = NetworkUtils.POPULAR_MOVIES_MODE;

    private int mPage = 1;

    private int mTotalItemsCount = 0;
    private ArrayList<Movie> asyncMovies;

    private ConnectivityManager mConnectivityManager;

    private GridLayoutManager layoutManager;

    private SwipeRefreshLayout refreshLayout;

    private NavigationView mNavigationView;

    private Toolbar mToolbar;

    private DrawerLayout mDrawerLayout;
    private Parcelable mListState;

    private final String LIST_STATE_KEY = "saveState";
    private final String LIST_DATA_KEY = "dataState";
    private final String LIST_PAGE_KEY = "pageState";


    private EndlessRecyclerViewScrollListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setStatusBarTranslucent();


        asyncMovies = new ArrayList<Movie>();
        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
            //noinspection unchecked
            asyncMovies = (ArrayList<Movie>) savedInstanceState.getSerializable(LIST_DATA_KEY);
            EndlessRecyclerViewScrollListener.currentPage = savedInstanceState.getInt(LIST_PAGE_KEY);
        }

        loadMoviesUsingOrientation();
        setUpSwipeToRefreshLayout();

        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        mProgress.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);

        noFavMovies1 = (TextView) findViewById(R.id.no_fav_movies);
        noFavMovies2 = (TextView) findViewById(R.id.no_fav_get_started);
        mError1 = (TextView) findViewById(R.id.error_message_1);
        mError2 = (TextView) findViewById(R.id.error_message_2);
        hideErrorMessage();

        mConnectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (checkForInternetConnection()) {
            loadMovieData();
        } else {
            showErrorMessage();
        }

        setUpNavigationDrawer();
    }

    private void loadMoviesUsingOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            setUpRecyclerView(LANDSCAPE_GRIDVIEW_COLUMNS);
        } else if (Configuration.ORIENTATION_PORTRAIT == orientation) {
            setUpRecyclerView(PORTRAIT_GRIDVIEW_COLUMNS);
        }
    }


    private void setUpRecyclerView(int columns) {
        layoutManager =
                new GridLayoutManager(this, columns, LinearLayoutManager.VERTICAL, false);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mMoviesAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mMoviesAdapter);

        listener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (MOVIE_LOADER_ID != NetworkUtils.FAVORITE_MOVIES_MODE) {
                    mPage = page;
                    mTotalItemsCount = totalItemsCount;
                    getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, arrayListLoaderCallbacks);
                }
            }
        };
        mRecyclerView.addOnScrollListener(listener);
    }

    private void setUpSwipeToRefreshLayout() {
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMethod();
            }
        });
    }

    private void setUpNavigationDrawer() {


        ActionBarDrawerToggle drawerToggle =
                new ActionBarDrawerToggle
                        (
                                MainActivity.this,
                                mDrawerLayout,
                                mToolbar,
                                R.string.drawer_open,
                                R.string.drawer_close
                        );

        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int selectedItem = item.getItemId();
                switch (selectedItem) {
                    case R.id.action_popular_movies:
                        MOVIE_LOADER_ID = NetworkUtils.POPULAR_MOVIES_MODE;
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(R.string.app_name_popular_movies);
                        }
                        refreshMethod();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.action_top_rated_movies:
                        MOVIE_LOADER_ID = NetworkUtils.TOP_RATED_MOVIES_MODE;
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(R.string.app_name_top_rated_movies);
                        }
                        refreshMethod();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.action_favorite_movies:
                        MOVIE_LOADER_ID = NetworkUtils.FAVORITE_MOVIES_MODE;
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(R.string.favorite_movies);
                        }
                        refreshMethod();
                        mDrawerLayout.closeDrawers();
                        break;
                    default:
                        Log.v(TAG, "Navigation drawer error");
                        break;
                }
                return true;
            }
        });
    }

    private boolean checkForInternetConnection() {
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        return mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting();
    }

    private void loadMovieData() {
        if (MOVIE_LOADER_ID == NetworkUtils.FAVORITE_MOVIES_MODE) {
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, cursorLoaderCallbacks);
        } else {
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, arrayListLoaderCallbacks);
        }
    }

    private void setDataToRecyclerView(Cursor cursor) {

        if (mProgress.getVisibility() == View.VISIBLE) {
            mProgress.setVisibility(View.INVISIBLE);
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mMoviesAdapter.setMovies(null, cursor);
        mRecyclerView.setAdapter(mMoviesAdapter);
    }

    @Override
    public void onListItemClick(int movieID) {

        isClicked = true;

        int movieId;
        movieId = movieID;
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra("MovieId", movieId);
        Uri uri = MoviesEntry.CONTENT_MOVIE_ID_URI.buildUpon()
                .appendPath(String.valueOf(movieId))
                .build();
        Log.v(TAG, uri.toString());
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onListItemLongClick(int movieId) {
        if (MOVIE_LOADER_ID == NetworkUtils.FAVORITE_MOVIES_MODE) {
            dialogOptions(movieId);
        }
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mError1.setVisibility(View.VISIBLE);
        mError2.setVisibility(View.VISIBLE);
    }

    private void showRecyclerView() {
        mProgress.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage() {
        mError1.setVisibility(View.INVISIBLE);
        mError2.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();
        switch (selectedItem) {
            case R.id.action_refresh:
                refreshMethod();
                break;
            case R.id.action_delete_all_favorites:
                deleteAllFavorites();
                break;
            default:
                Log.v(TAG, "menu error");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshMethod() {
        if (checkForInternetConnection() ||
                MOVIE_LOADER_ID == NetworkUtils.FAVORITE_MOVIES_MODE) {
            hideNoFavMoviesMessage();
            hideErrorMessage();
            completeRefresh();

        } else {
            refreshLayout.setRefreshing(false);
            showErrorMessage();
        }
    }

    private void restartLoader() {
        if (MOVIE_LOADER_ID == NetworkUtils.FAVORITE_MOVIES_MODE) {
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, cursorLoaderCallbacks);
        } else {
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, arrayListLoaderCallbacks);
        }
    }

    private void completeRefresh() {

        if (asyncMovies != null) {
            asyncMovies.clear();
            Log.v(TAG, "ASYNCMOVIES = " + asyncMovies.size());
        }
        if (mMoviesAdapter != null) {
            mMoviesAdapter.setMovies(null, null);
            isRefreshed = true;
        }
        if (listener != null) {
            listener.resetState();
        }
        mTotalItemsCount = 0;
        if (mListState != null) {
            mListState = null;
        }
        if (mPage > 1) {
            mPage = 1;
        }
        restartLoader();
    }

    @SuppressWarnings("CanBeFinal")
    private LoaderManager.LoaderCallbacks<ArrayList<Movie>> arrayListLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<ArrayList<Movie>>() {
                public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
                    if (mPage == 1) {
                        mProgress.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.INVISIBLE);
                    }
                    return new MoviesLoader(MainActivity.this, mPage);
                }

                @Override
                public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
                    Log.v(TAG, "LOADER");
                    showRecyclerView();
                    refreshLayout.setRefreshing(false);
                    insertNewMoviesToRecyclerView(data);
                }

                @Override
                public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
                }
            };

    private static final String[] MOVIE_FAVORITE_PROJECTION = {
            MoviesEntry.COLUMN_MOVIE_ID,
            MoviesEntry.COLUMN_MOVIE_TITLE,
            MoviesEntry.COLUMN_MOVIE_RATING,
            MoviesEntry.COLUMN_MOVIE_POSTER_IMAGE_PATH,
    };

    @SuppressWarnings("CanBeFinal")
    private LoaderManager.LoaderCallbacks<Cursor> cursorLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    mProgress.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    return new CursorLoader(MainActivity.this,
                            MoviesEntry.CONTENT_URI,
                            MOVIE_FAVORITE_PROJECTION,
                            null,
                            null,
                            null);
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    if (data.getCount() != 0) {
                        hideNoFavMoviesMessage();
                        showRecyclerView();
                        setDataToRecyclerView(data);
                    } else {
                        showNoFavMoviesMessage();
                    }
                    refreshLayout.setRefreshing(false);
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                }
            };

    private void showNoFavMoviesMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgress.setVisibility(View.INVISIBLE);
        noFavMovies1.setVisibility(View.VISIBLE);
        noFavMovies2.setVisibility(View.VISIBLE);
    }

    private void hideNoFavMoviesMessage() {
        noFavMovies1.setVisibility(View.GONE);
        noFavMovies2.setVisibility(View.GONE);
    }

    private void insertNewMoviesToRecyclerView(ArrayList<Movie> movies) {

        mRecyclerView.setLayoutManager(layoutManager);
        if (mListState != null && !isClicked) {
            layoutManager.onRestoreInstanceState(mListState);
            mMoviesAdapter.setMovies(asyncMovies, null);
            mListState = null;
            isClicked = false;
            return;
        }

        if (movies != null && checkForInternetConnection()) {
            asyncMovies.addAll(movies);
        } else {
            showErrorMessage();
        }

        if (isRefreshed) {
            mRecyclerView.setAdapter(mMoviesAdapter);
            isRefreshed = false;
        }

        if (mProgress.getVisibility() == View.VISIBLE) {
            mProgress.setVisibility(View.INVISIBLE);
        }
        mMoviesAdapter.setMovies(asyncMovies, null);
        int curSize = mMoviesAdapter.getItemCount();
        mMoviesAdapter.notifyItemRangeInserted(curSize, mTotalItemsCount);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mListState = layoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, mListState);
        outState.putSerializable(LIST_DATA_KEY, asyncMovies);
        outState.putSerializable(LIST_PAGE_KEY, mPage);
        super.onSaveInstanceState(outState);
        Log.v(TAG, "ON_SAVE_INSTANCE_STATE");
    }

    private void dialogOptions(final int movieId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setItems(R.array.options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = MoviesEntry.CONTENT_MOVIE_ID_URI.buildUpon()
                        .appendPath(String.valueOf(movieId))
                        .build();
                showDeleteConfirmationDialog(uri);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showDeleteConfirmationDialog(final Uri uri) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_remove_title);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteMovie(uri);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteMovie(Uri uri) {
        int rowsDeleted = getContentResolver().delete(uri, null, null);
    }

    private void deleteAllFavorites() {
        int rowsDeleted = getContentResolver().delete(MoviesEntry.CONTENT_URI, null, null);
    }

    private void setStatusBarTranslucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
