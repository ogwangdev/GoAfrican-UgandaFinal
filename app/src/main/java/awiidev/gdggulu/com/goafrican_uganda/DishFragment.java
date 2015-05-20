package awiidev.gdggulu.com.goafrican_uganda;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import awiidev.gdggulu.com.goafrican_uganda.adapters.DishAdapter;
import awiidev.gdggulu.com.goafrican_uganda.data.DishContract;
import awiidev.gdggulu.com.goafrican_uganda.sync.DishSyncAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DishFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DishFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressWarnings("ALL")
public class DishFragment extends android.support.v4.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private DishAdapter dishAdapter;
    private ListView listView;
    private int mPosition = ListView.INVALID_POSITION;
    private static final int DISH_LOADER = 0;

    private static final String SELECTED_KEY = "selected_position";

    private static final String[] DISH_COLUMNS = {
            DishContract.DishEntry.DISH_COLUMN_DISH_ID,
            DishContract.DishEntry.DISH_COLUMN_DISH_KEY,
            DishContract.DishEntry.DISH_COLUMN_DISH_NAME,
            DishContract.DishEntry.DISH_COLUMN_DESCRIPTION,
            DishContract.DishEntry.DISH_COLUMN_INGREDIENTS,
            DishContract.DishEntry.DISH_COLUMN_STEPS
    };

    public static final int COL_DISH_KEY = 1;
    public static final int COL_DISH_TITLE = 2;
    public static final int COL_DISH_DESC = 3;


    public interface Callback {
        /**
         * DishDetailFragmentCallback for when an item has been selected.
         * @param dishName
         */
        public void onItemSelected(String dishName);
    }
    public DishFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

      dishAdapter = new DishAdapter(getActivity(), null, 0);
        View view = inflater.inflate(R.layout.fragment_dish, container, false);

        listView = (ListView) view.findViewById(R.id.list_view_dish);

        listView.setAdapter(dishAdapter);

//        listView.getAdapter().getView(1 , null, null).performClick();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = dishAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback)getActivity()).onItemSelected(cursor.getString(COL_DISH_TITLE));
                }
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DISH_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
        updateDishDb();
//        listView.getAdapter().getView(1 , null, null).performClick();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = DishContract.DishEntry.DISH_COLUMN_DISH_ID + " DESC";
        Uri dishUri = DishContract.DishEntry.CONTENT_URI;

        return new CursorLoader(
                getActivity(),
                dishUri,
                DISH_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }
    public void updateDishDb() {
        DishSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        dishAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            listView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        dishAdapter.swapCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dish_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_dish) {
            Intent i = new Intent(getActivity(), AddDish.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
