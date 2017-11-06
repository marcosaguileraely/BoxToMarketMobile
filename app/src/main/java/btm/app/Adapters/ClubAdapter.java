package btm.app.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import btm.app.R;

/**
 * Created by maguilera on 11/5/17.
 */

public class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.ViewHolder> {

    private ArrayList<String> dataSet;

    public ClubAdapter(ArrayList<String> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public ClubAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                 .inflate(R.layout.list_items_clubs, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ClubAdapter.ViewHolder holder, int position) {
        holder.title.setText(dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);


        }
    }
}
