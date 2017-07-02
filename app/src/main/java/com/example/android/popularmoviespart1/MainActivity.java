package com.example.android.popularmoviespart1;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmoviespart1.utilities.EndlessRecyclerViewScrollListener;
import com.example.android.popularmoviespart1.utilities.JsonUtils;
import com.example.android.popularmoviespart1.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ListItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private ProgressBar mProgress;
    private TextView mError1;
    private TextView mError2;
    private static final int LANDSCAPE_GRIDVIEW_COLUMNS = 3;
    private static final int PORTRAIT_GRIDVIEW_COLUMNS = 2;

    private EndlessRecyclerViewScrollListener listener;
    private boolean isLoadFirstPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        mProgress.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY
        );
        mError1 = (TextView) findViewById(R.id.error_message_1);
        mError2 = (TextView) findViewById(R.id.error_message_2);
        hideErrorMessage();

        loadMovieData();
    }

    private void loadMovieData() {
        new FetchMovieTask(0, null).execute();
    }

    private void loadViewBasedOnOrientation(ArrayList<Movie> movies) {

        int orientation = getResources().getConfiguration().orientation;
        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            createRecyclerView(LANDSCAPE_GRIDVIEW_COLUMNS, movies);
        } else if (Configuration.ORIENTATION_PORTRAIT == orientation) {
            createRecyclerView(PORTRAIT_GRIDVIEW_COLUMNS, movies);
        }
    }

    private void createRecyclerView(int columns, final ArrayList<Movie> movies) {

        GridLayoutManager layoutManager =
                new GridLayoutManager(this, columns, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(layoutManager);
        mMoviesAdapter = new MoviesAdapter(this);
        mMoviesAdapter.setMovies(movies);
        mRecyclerView.setAdapter(mMoviesAdapter);
        listener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                new FetchMovieTask(totalItemsCount, movies).execute(page);
            }
        };
        mRecyclerView.addOnScrollListener(listener);
    }

    @Override
    public void onListItemClick(Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra("Movie", movie);
        startActivity(intent);
    }


    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mError1.setVisibility(View.VISIBLE);
        mError2.setVisibility(View.VISIBLE);
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
            case R.id.action_popular_movies:
                NetworkUtils.chooseMode(NetworkUtils.POPULAR_MOVIES_MODE);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.app_name_popular_movies);
                }
                loadMoviesAfterModeChange();
                break;
            case R.id.action_top_rated_movies:
                NetworkUtils.chooseMode(NetworkUtils.TOP_RATED_MOVIES_MODE);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.app_name_top_rated_movies);
                }
                loadMoviesAfterModeChange();
                break;
            case R.id.action_refresh:
                refreshMethod();
            default:
                Log.v(TAG, "menu error");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshMethod() {
        hideErrorMessage();
        if (listener != null) {
            listener.resetState();
        }
        isLoadFirstPage = false;
        loadMovieData();
    }

    private void loadMoviesAfterModeChange() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        if (mMoviesAdapter != null) {
            mMoviesAdapter.setMovies(null);
        }
        if (listener != null) {
            listener.resetState();
        }
        isLoadFirstPage = false;
        loadMovieData();
    }

    public class FetchMovieTask extends AsyncTask<Integer, Void, ArrayList<Movie>> {

        final int mTotalItemsCount;
        final ArrayList<Movie> asyncMovies;

        public FetchMovieTask(int totalItemsCount, ArrayList<Movie> movies) {
            mTotalItemsCount = totalItemsCount;
            asyncMovies = movies;
        }

        @Override
        protected void onPreExecute() {
            if (!isLoadFirstPage) {
                mProgress.setVisibility(View.VISIBLE);
            } else {
                mMoviesAdapter.addLoadingFooter();
            }
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Movie> doInBackground(Integer... integers) {

            URL movieRequestUrl = null;
            int page = 1;
            if (!isLoadFirstPage) {
                movieRequestUrl = NetworkUtils.buildURL(page);
            } else if (integers != null) {
                page = integers[0];
                movieRequestUrl = NetworkUtils.buildURL(page);
            }
            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                return JsonUtils.convertJSONToData(jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                Log.e(TAG, "JSON ERROR");
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            mProgress.setVisibility(View.INVISIBLE);
            if (movies != null && !isLoadFirstPage) {
                isLoadFirstPage = true;
                loadViewBasedOnOrientation(movies);
            } else if (movies != null) {
                insertNewMoviesToRecyclerView(movies);
            } else {
                showErrorMessage();
            }
        }

        private void insertNewMoviesToRecyclerView(ArrayList<Movie> movies) {
            asyncMovies.addAll(movies);
            int curSize = mMoviesAdapter.getItemCount();
            mMoviesAdapter.removeLoadingFooter();
            mMoviesAdapter.notifyItemRangeInserted(curSize, mTotalItemsCount);
        }
    }
}
