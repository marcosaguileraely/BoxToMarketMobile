package btm.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import btm.app.DataHolder.DataHolderQrActivesDetails;
import btm.app.Model.QrActives;
import btm.app.QrActiveDetailActivity;
import btm.app.R;
import btm.app.SubscriptionsDetailsActivity;

/**
 * Created by maguilera on 10/9/18.
 */

public class QrActivesAdapter extends BaseAdapter {

    private Context context;
    ArrayList<QrActives> qrActivesArrayList;
    private LayoutInflater layoutInflater;
    private static String username_global;
    public static final String USER_GLOBAL = "USERNAME";
    public static final String ID_GLOBAL   = "IDSUB";
    public static final String URI_IMG     = "URI_IMG";
    public static final String TITLE       = "TITLE";

    public QrActivesAdapter(Context context, ArrayList<QrActives> qrActivesArrayList, String username_global) {
        this.context = context;
        this.qrActivesArrayList = qrActivesArrayList;
        this.username_global = username_global;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_items_qr_actives, null);
        }

        final QrActives qrActives = (QrActives) qrActivesArrayList.get(position);

        TextView date     = (TextView) convertView.findViewById(R.id.qr_active_date);
        TextView serial   = (TextView) convertView.findViewById(R.id.qr_active_serial);
        TextView value    = (TextView) convertView.findViewById(R.id.qr_active_value);
        ImageView img_uri = (ImageView) convertView.findViewById(R.id.brand_img_url);
        Button view_qr    = (Button) convertView.findViewById(R.id.qr_active_view);

        date.setText(qrActives.getDate_time());
        serial.setText("Máquina # "+qrActives.getSerial());
        value.setText("$"+qrActives.getTotal());

        Log.w("Uri", "-->" + qrActives.getBrand_image_img());
        Glide.with(context).load("https://www.boxtomarket.com/img/logos_marcas_app/" + qrActives.getBrand_image_img()).into(img_uri);

        view_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serial = qrActives.getSerial();
                String qr_img = qrActives.getQr_code();
                String value  = qrActives.getTotal();
                String date   = qrActives.getDate_time();

                //Toast.makeText(context, "Button clicked "+ serial + " id: "+ qr_img, Toast.LENGTH_SHORT).show();

                DataHolderQrActivesDetails.setSerial(serial);
                DataHolderQrActivesDetails.setQr_image_ui(qr_img);
                DataHolderQrActivesDetails.setValue(value);
                DataHolderQrActivesDetails.setDate(date);

                Intent goToDetails = new Intent(context, QrActiveDetailActivity.class);
                //context.startActivity(goToDetails.putExtra(USER_GLOBAL, username_global));
                //context.startActivity(goToDetails.putExtra(ID_GLOBAL, id));
                //context.startActivity(goToDetails.putExtra(URI_IMG, subscriptionsPublic.getImg_uri()));
                //context.startActivity(goToDetails.putExtra(TITLE, subscriptionsPublic.getTitle()));
                context.startActivity(goToDetails);
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return qrActivesArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return qrActivesArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
