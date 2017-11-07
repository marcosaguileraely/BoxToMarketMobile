package btm.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import btm.app.Model.Subscriptions;
import btm.app.R;

/**
 * Created by maguilera on 10/23/17.
 */

public class SubscriptionAdapter extends ArrayAdapter<Subscriptions> {

    LayoutInflater inflater;
    ArrayList<Subscriptions> subscriptionsArrayList;
    private int resourceId;
    private Context context;

    List list = new ArrayList();

    public SubscriptionAdapter(Context context, ArrayList<Subscriptions> subscriptionsArrayList) {
        super(context, R.layout.list_items_subscriptions , subscriptionsArrayList);
        this.subscriptionsArrayList = subscriptionsArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.list_items_subscriptions, parent, false);

        // 3. Get the two text view from the rowView
        TextView total  = (TextView) rowView.findViewById(R.id.subs_total_txt);
        ImageView img   = (ImageView) rowView.findViewById(R.id.img_url);

        Subscriptions subscriptions = (Subscriptions) subscriptionsArrayList.get(position);

        // 4. Set the text for textView
        total.setText(String.valueOf(subscriptions.getQty()+ " Productos disponibles para reclamar en BtM"));
        Glide.with(context).load(subscriptions.getImg_uri()).into(img);

        // 5. return rowView
        return rowView;
    }

    public int getSubscriptionId(int position) {
        if (subscriptionsArrayList != null) {
            return subscriptionsArrayList.get(position).getId();
        }else{
            return 0;
        }
    }
}
