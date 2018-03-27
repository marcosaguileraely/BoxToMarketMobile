package btm.app.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import btm.app.Model.Bluethoot;
import btm.app.Model.Countries;
import btm.app.R;

/**
 * Created by maguilera on 3/26/18.
 */

public class CountriesAdapter extends ArrayAdapter<Countries> {
    LayoutInflater inflater;
    ArrayList<Countries> countriesArrayList;
    private Context context;

    public CountriesAdapter(Context context, int textViewResourceId, ArrayList<Countries> countriesArrayList) {
        super(context, textViewResourceId, countriesArrayList);
        this.context = context;
        this.countriesArrayList = countriesArrayList;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        // Inflating the layout for the custom Spinner
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.list_item_countries, parent, false);

        // Declaring and Typecasting the textview in the inflated layout
        TextView countryName = (TextView) layout.findViewById(R.id.country_name);

        Countries countries = (Countries) countriesArrayList.get(position);

        // Setting the text using the array
        countryName.setText(countries.getName());

        return layout;
    }

    // It gets a View that displays in the drop down popup the data at the specified position
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // It gets a View that displays the data at the specified position
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }


}
