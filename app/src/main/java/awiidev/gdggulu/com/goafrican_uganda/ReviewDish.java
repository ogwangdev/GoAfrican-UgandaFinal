package awiidev.gdggulu.com.goafrican_uganda;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;

import com.awiidev.gdggulu.goafrica_uganda.backend.myApi.MyApi;
import com.awiidev.gdggulu.goafrica_uganda.backend.myApi.model.ReviewBean;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import awiidev.gdggulu.com.goafrican_uganda.adapters.ReviewAdapter;
import awiidev.gdggulu.com.goafrican_uganda.data.DishDbHelper;
import awiidev.gdggulu.com.goafrican_uganda.data.ReviewRecord;


public class ReviewDish extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_dish);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ReviewFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_review_dish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ReviewFragment extends Fragment {
        DishDbHelper dbHelper;
        ReviewAdapter reviewAdapter;
        CheckBox star;
        Long dishKey;
        public int tag;
        private ListView reviewList;

        public ReviewFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_review_dish, container, false);
            dbHelper = new DishDbHelper(getActivity().getBaseContext());

            reviewList = (ListView) rootView.findViewById(R.id.list_view_reviews);
            reviewAdapter = new ReviewAdapter(getActivity().getBaseContext(),
                    dbHelper.getReviews(),
                    0
            );

            reviewList.setAdapter(reviewAdapter);
            return rootView;
        }
    }

    public class FetchReviewsTask extends AsyncTask<Long, Void, List<ReviewBean>> {
        private Context context;

        public FetchReviewsTask(Context mContext) {
            context = mContext;
        }

        @Override
        protected List<ReviewBean> doInBackground(Long... params) {
            MyApi myApiService = null;

            if (myApiService == null) {
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl("https://ace-fiber-802.appspot.com/_ah/api/");
                myApiService = builder.build();
            }
            Long dishId = params[0];
            DishDbHelper dbHelper = new DishDbHelper(context);
            try {
                List<ReviewBean> remoteReviews = myApiService.getReviewsForDish(dishId).execute().getItems();

                if (remoteReviews != null) {
                    List<ReviewRecord> reviewList = new ArrayList<ReviewRecord>();
                    for (ReviewBean reviewBean : remoteReviews) {
                        reviewList.add(new ReviewRecord(reviewBean.getDishId(), reviewBean.getUsername(),
                                reviewBean.getComment(), reviewBean.getRating()));
                    }

                    for (int i = 0; i < reviewList.size(); i++) {
                        ReviewRecord reviewRecord = reviewList.get(i);
                        dbHelper.saveReview(reviewRecord.getDishID(), reviewRecord.getUsername(),
                                reviewRecord.getComment(), reviewRecord.getRating());
                    }
                }

            } catch (IOException e) {
                Log.e(FetchReviewsTask.class.getSimpleName(), "Error when retrieving reviews", e);
            }

            return null;
        }
    }
}
