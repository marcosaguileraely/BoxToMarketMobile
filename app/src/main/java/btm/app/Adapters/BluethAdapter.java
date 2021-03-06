package btm.app.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import btm.app.Model.Bluethoot;
import btm.app.Model.Subscriptions;
import btm.app.R;

/**
 * Created by maguilera on 11/11/17.
 */

public class BluethAdapter extends ArrayAdapter<Bluethoot> {
    LayoutInflater inflater;
    ArrayList<Bluethoot> bluethootArrayList;
    private Context context;

    public BluethAdapter(Context context, ArrayList<Bluethoot> bluethootArrayList) {
        super(context, R.layout.list_items_bluethoots, bluethootArrayList);
        this.context = context;
        this.bluethootArrayList = bluethootArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_items_bluethoots, parent, false);

        TextView uuid = (TextView) rowView.findViewById(R.id.blue_uuid);
        TextView mac  = (TextView) rowView.findViewById(R.id.blue_mac_address);
        ImageView img = (ImageView) rowView.findViewById(R.id.imageView2);

        Bluethoot bluethoot = (Bluethoot) bluethootArrayList.get(position);
        uuid.setText(String.valueOf("" + fixingTypeName(bluethoot.getType())) + " # "+  bluethoot.getId().toUpperCase()   );
        //mac.setText(String.valueOf("#" +  +  " " + bluethoot.getName()) + " " + bluethoot.getMac());

        Glide.with(context)
                .load("https://www.bleecard.com/" + bluethoot.getImg())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img);

        return rowView;
    }

    private String fixingTypeName(String inDatum){
        String type_fixed = "";

        if(inDatum.equals("btm_mini")){
            type_fixed = "BtM Mini";
        }else if(inDatum.equals("vending")){
            type_fixed = "Vending  Machine";
        }else if(inDatum.equals("btm_power")){
            type_fixed = "BtM Power";
        }

        return type_fixed;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public Bluethoot getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable Bluethoot item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public String getId(int position) {
        if (bluethootArrayList != null) {
            return bluethootArrayList.get(position).getId();
        }else{
            return "no id found";
        }
    }

    public String getType(int position) {
        if (bluethootArrayList != null) {
            return bluethootArrayList.get(position).getType();
        }else{
            return "no type found";
        }
    }

    public String getAddress(int position) {
        if (bluethootArrayList != null) {
            return bluethootArrayList.get(position).getMac();
        }else{
            return "no type found";
        }
    }

    public String getImage(int position) {
        if (bluethootArrayList != null) {
            return bluethootArrayList.get(position).getImg();
        }else{
            return "no type found";
        }
    }

    public String getName(int position) {
        if (bluethootArrayList != null) {
            return bluethootArrayList.get(position).getName();
        }else{
            return "no type found";
        }
    }

}
