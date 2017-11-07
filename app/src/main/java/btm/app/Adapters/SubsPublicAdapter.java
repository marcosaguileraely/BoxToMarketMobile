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

import btm.app.ForgotPassActivity;
import btm.app.MainActivity;
import btm.app.Model.SubscriptionsPublic;
import btm.app.R;

/**
 * Created by maguilera on 11/6/17.
 */

public class SubsPublicAdapter extends BaseAdapter{

    private Context context;
    ArrayList<SubscriptionsPublic> subscriptionsPublics;
    private ArrayList<String> arrayList;
    private LayoutInflater layoutInflater;

    public SubsPublicAdapter(Context context, ArrayList<SubscriptionsPublic> subscriptionsPublics) {
        this.context = context;
        this.subscriptionsPublics = subscriptionsPublics;
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
                int id = subscriptionsPublic.getId();
                Toast.makeText(context, "Button clicked "+title + " id: "+id, Toast.LENGTH_SHORT).show();
                Intent goToDetails = new Intent(context, ForgotPassActivity.class);
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
