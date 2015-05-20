package awiidev.gdggulu.com.goafrican_uganda.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import awiidev.gdggulu.com.goafrican_uganda.DishFragment;
import awiidev.gdggulu.com.goafrican_uganda.R;

/**
 * Created by Eng FIDY on 3/7/2015.
 */
public class DishAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final TextView titleText;
        public final TextView descriptionText;


        public ViewHolder(View view) {
            titleText = (TextView) view.findViewById(R.id.list_view_dish_title);
            descriptionText = (TextView) view.findViewById(R.id.list_view_dish_description);
        }
    }

    public DishAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_dish, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String title =
                cursor.getString(DishFragment.COL_DISH_TITLE);
        viewHolder.titleText.setText(title);

        String description = cursor.getString(DishFragment.COL_DISH_DESC);
        viewHolder.descriptionText.setText(description);

    }

}