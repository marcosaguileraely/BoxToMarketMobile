package btm.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import btm.app.Model.SubscriptionsPublic;
import btm.app.R;
import btm.app.SubscriptionsDetailsActivity;

/**
 * Created by maguilera on 11/6/17.
 */

public class SubsPublicAdapter extends BaseAdapter{

    private Context context;
    ArrayList<SubscriptionsPublic> subscriptionsPublics;
    private LayoutInflater layoutInflater;
    private static String username_global;
    public static final String USER_GLOBAL = "USERNAME";
    public static final String ID_GLOBAL   = "IDSUB";
    public static final String URI_IMG     = "URI_IMG";

    public SubsPublicAdapter(Context context, ArrayList<SubscriptionsPublic> subscriptionsPublics, String username_global) {
        this.context = context;
        this.subscriptionsPublics = subscriptionsPublics;
        this.username_global = username_global;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_items_subspublic, null);
        }

        final SubscriptionsPublic subscriptionsPublic = (SubscriptionsPublic) subscriptionsPublics.get(position);

        TextView title    = (TextView) convertView.findViewById(R.id.subs_title);
        TextView value    = (TextView) convertView.findViewById(R.id.value);
        ImageView img_uri = (ImageView) convertView.findViewById(R.id.subs_img_url);
        Button buy_btn    = (Button) convertView.findViewById(R.id.subs_buy);

        title.setText(subscriptionsPublic.getTitle());
        value.setText(subscriptionsPublic.getValue());
        Glide.with(context).load(subscriptionsPublic.getImg_uri()).into(img_uri);

        buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = subscriptionsPublic.getTitle();
                int id       = subscriptionsPublic.getId();

                Toast.makeText(context, "Button clicked "+title + " id: "+id, Toast.LENGTH_SHORT).show();

                Intent goToDetails = new Intent(context, SubscriptionsDetailsActivity.class);
                context.startActivity(goToDetails.putExtra(USER_GLOBAL, username_global));
                context.startActivity(goToDetails.putExtra(ID_GLOBAL, id));
                context.startActivity(goToDetails.putExtra(URI_IMG, subscriptionsPublic.getImg_uri()));
                context.startActivity(goToDetails);
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return subscriptionsPublics.size();
    }

    @Override
    public Object getItem(int position) {
        return subscriptionsPublics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
