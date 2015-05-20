package awiidev.gdggulu.com.goafrican_uganda;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.awiidev.gdggulu.goafrica_uganda.backend.myApi.MyApi;
import com.awiidev.gdggulu.goafrica_uganda.backend.myApi.model.DishBean;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.Random;


public class AddDish extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dish);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new AddDishFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_dish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class AddDishFragment extends Fragment {
        EditText etitle;
        EditText edescription;
        EditText eingredients;
        EditText esteps;

        public AddDishFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_dish, container, false);
            etitle = (EditText) rootView.findViewById(R.id.eTitle);
            edescription = (EditText) rootView.findViewById(R.id.eDescription);
            eingredients = (EditText) rootView.findViewById(R.id.eIngredients);
            esteps = (EditText) rootView.findViewById(R.id.eSteps);

            Button save = (Button) rootView.findViewById(R.id.save_btn);
            save.setOnClickListener(onSave);
            return rootView;
        }

        public View.OnClickListener onSave = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etitle.getText().toString();
                String description = edescription.getText().toString();
                String ingredients = eingredients.getText().toString();
                String steps = esteps.getText().toString();

                if (title.equals("") && description.equals("") && ingredients.equals("") && steps.equals("") ) {
                    Toast.makeText(getActivity(), "Please fill the required details. Thank you.", Toast.LENGTH_LONG).show();

                } else {
                    StoreDishTask storeDishTask = new StoreDishTask();
                    storeDishTask.execute();
                    etitle.setText("");
                    edescription.setText("");
                    eingredients.setText("");
                    esteps.setText("");
                    Toast.makeText(getActivity(), "Thank You! Your dish has been Successfully Submitted", Toast.LENGTH_LONG).show();

                }
            }
        };

        public class StoreDishTask extends AsyncTask<Void, Void, Void> {
            private MyApi dishApiService = null;
            @Override
            protected Void doInBackground(Void... params) {
                if (dishApiService == null) {

                    MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                            .setRootUrl("https://focused-century-87702/_ah/api/");
                    dishApiService = builder.build();
                }

                String title = etitle.getText().toString();
                String description = edescription.getText().toString();
                String ingredients = eingredients.getText().toString();
                String steps = esteps.getText().toString();

                Random r = new Random();
//            r.setSeed(20);
                int dishId = r.nextInt(Integer.MAX_VALUE);

                Long id = Long.valueOf(dishId);
                try {

                    DishBean dishBean = new DishBean();
                    dishBean.setSteps(steps);
                    dishBean.setIngredients(ingredients);
                    dishBean.setDescription(description);
                    dishBean.setTitle(title);
                    dishBean.setId(id);
                    dishApiService.storeDish(dishBean).execute();

                } catch (IOException e) {
                    Log.e(AddDishFragment.class.getSimpleName(), "Error when Adding dish", e);
                }

                return null;
            }

        }

    }
}