package btm.app.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import com.bumptech.glide.Glide;

import btm.app.Model.Clubs;
import btm.app.R;

/**
 * Created by maguilera on 11/5/17.
 */

public class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.ViewHolder> {

    private ArrayList<String> dataSet;
    ArrayList<Clubs> clubsArrayList;

    public ClubAdapter(ArrayList<Clubs> clubsArrayList) {
        this.clubsArrayList = clubsArrayList;
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
        Clubs clubs = (Clubs) clubsArrayList.get(position);
        holder.title.setText(String.valueOf(clubs.getTitle()));
        Glide.with(holder.itemView.getContext()).load(clubs.getImg_uri()).into(holder.img_uri);
    }

    @Override
    public int getItemCount() {
        return clubsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public ImageView img_uri;

        public ViewHolder(View itemView) {
            super(itemView);
            title   = (TextView) itemView.findViewById(R.id.title);
            img_uri = (ImageView) itemView.findViewById(R.id.img_url);
        }
    }
}
