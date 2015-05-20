package awiidev.gdggulu.com.goafrican_uganda;

/**
 * Created by Eng FIDY on 12/21/2014.
 */

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.awiidev.gdggulu.goafrica_uganda.backend.myApi.MyApi;
import com.awiidev.gdggulu.goafrica_uganda.backend.myApi.model.ReviewBean;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.Random;

import awiidev.gdggulu.com.goafrican_uganda.data.DishContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private  static final String DISH_SHARE_HASHTAG = "#GoAfrican-Ugandan Dishes";
    public static final String DISH_KEY = "dishName";
    private String dish;
    private String dishStr;
    public Long dishKey;
    public int tag;
    private ShareActionProvider mShareActionProvider;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DISH_COLUMNS = {
            DishContract.DishEntry.DISH_COLUMN_DISH_ID,
            DishContract.DishEntry.DISH_COLUMN_DISH_KEY,
            DishContract.DishEntry.DISH_COLUMN_DISH_NAME,
            DishContract.DishEntry.DISH_COLUMN_DESCRIPTION,
            DishContract.DishEntry.DISH_COLUMN_INGREDIENTS,
            DishContract.DishEntry.DISH_COLUMN_STEPS
    };

    private TextView titleView;
    private TextView descriptionView;
    private TextView ingredientsView;
    private TextView stepsView;

    private EditText username;
    private EditText comment;
    private CheckBox star;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(DISH_KEY, dish);
        super.onSaveInstanceState(outState);
    }

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DishDetailActivity.DISH_KEY) &&
                dishStr != null) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            dishStr = arguments.getString(DishDetailActivity.DISH_KEY);
        }

        if (savedInstanceState != null) {
            dishStr = savedInstanceState.getString(DISH_KEY);
        }

      final View rootView = inflater.inflate(R.layout.fragment_dish_detail, container, false);

      titleView = (TextView) rootView.findViewById(R.id.detail_title);
        descriptionView = (TextView) rootView.findViewById(R.id.detail_description);
        ingredientsView = (TextView) rootView.findViewById(R.id.detail_ingredients);
       stepsView = (TextView) rootView.findViewById(R.id.detail_steps);
//        slidingImage = (ImageView) rootView.findViewById(R.id.ImageView3_Left);
        // Review form
        username = (EditText) rootView.findViewById(R.id.usernameEdit);
        comment = (EditText) rootView.findViewById(R.id.reviewEdit);

        for (int i = 1; i <= 5; i++) {
            star = (CheckBox) rootView.findViewWithTag(String.valueOf(i));
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   tag = Integer.valueOf((String) v.getTag());
//---check all the stars up to the one touched---
                    for (int i = 1; i <= tag; i++) {
                        star = (CheckBox) rootView.findViewWithTag(String.valueOf(i));
                        star.setChecked(true);
                    }
//---uncheck all remaining stars---
                    for (int i = tag + 1; i <= 5; i++) {
                        star = (CheckBox) rootView.findViewWithTag(String.valueOf(i));
                        star.setChecked(false);
                    }
                }
            });
        }

//       Save the review
        Button addReview = (Button) rootView.findViewById(R.id.addReviewBtn);
        addReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userText = username.getText().toString();
                String commentText = comment.getText().toString();
                if (userText.equals("") && commentText.equals("")) {
                    Toast.makeText(getActivity(), "Please provide a name and a comment.", Toast.LENGTH_LONG).show();
                } else {
                    AddReviewTask addReviewTask = new AddReviewTask();
                    addReviewTask.execute();
                    username.setText("");
                    comment.setText("");
                    Toast.makeText(getActivity(), "Thank you for the review.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            dishStr = savedInstanceState.getString(DISH_KEY);
        }

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DishDetailActivity.DISH_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (dish != null) {
            mShareActionProvider.setShareIntent(createShareDishIntent());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection = DishContract.DishEntry.DISH_COLUMN_DISH_NAME + "=\""
                + dishStr + "\" ";

        String sortOrder = DishContract.DishEntry.DISH_COLUMN_DISH_ID + " DESC";

        Uri dishUri = DishContract.DishEntry.CONTENT_URI;

        return new CursorLoader(
                getActivity(),
                dishUri,
                DISH_COLUMNS,
                selection,
                null,
                sortOrder
        );
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            dishKey = data.getLong(data.getColumnIndex(DishContract.DishEntry.DISH_COLUMN_DISH_KEY));

            String title = data.getString(data.getColumnIndex(DishContract.DishEntry.DISH_COLUMN_DISH_NAME));
            ((TextView) getView().findViewById(R.id.detail_title)).setText(title);

            String description = data.getString(data.getColumnIndex(DishContract.DishEntry.DISH_COLUMN_DESCRIPTION));
            ((TextView) getView().findViewById(R.id.detail_description)).setText(description);

            String ingredients = data.getString(data.getColumnIndex(DishContract.DishEntry.DISH_COLUMN_INGREDIENTS));
            ((TextView) getView().findViewById(R.id.detail_ingredients)).setText(ingredients);

            String steps = data.getString(data.getColumnIndex(DishContract.DishEntry.DISH_COLUMN_STEPS));
            ((TextView) getView().findViewById(R.id.detail_steps)).setText(steps);

            dish = String.format("%s - %s ", title, description);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareDishIntent());
            }
        }
    }

    private Intent createShareDishIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, dish + DISH_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public class AddReviewTask extends AsyncTask<Void, Void, Void> {
        private MyApi myApiService = null;

        @Override
        protected Void doInBackground(Void... params) {
            if (myApiService == null) {
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl("https://focused-century-87702.appspot.com/_ah/api/");
                myApiService = builder.build();
            }

            String userText = username.getText().toString();
            String commentText = comment.getText().toString();

//            Long dishId = Long.valueOf(dishID);
            Random r = new Random();
            int reviewId = r.nextInt(Integer.MAX_VALUE);
            Long id = Long.valueOf(reviewId);

            try {
                ReviewBean reviewBean = new ReviewBean();
                reviewBean.setRating(Long.valueOf(tag));
                reviewBean.setComment(commentText);
                reviewBean.setUsername(userText);
                reviewBean.setDishId(dishKey);
                reviewBean.setId(id);

                myApiService.saveReview(reviewBean).execute();
                tag = 0;

            } catch (IOException e) {
                Log.e(DetailFragment.class.getSimpleName(), "Error when saving review", e);
            }

            return null;
        }
    }

}