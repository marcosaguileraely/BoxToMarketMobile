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

import btm.app.Model.QRHistory;
import btm.app.Model.Subscriptions;
import btm.app.R;

/**
 * Created by maguilera on 10/5/18.
 */

public class QRHistoryAdapter extends ArrayAdapter<QRHistory> {

    LayoutInflater inflater;
    ArrayList<QRHistory> qrHistoryArrayList;
    private Context context;

    List list = new ArrayList();

    public QRHistoryAdapter(Context context, ArrayList<QRHistory> qrHistoryArrayList) {
        super(context, R.layout.list_items_qr_history , qrHistoryArrayList);
        this.qrHistoryArrayList = qrHistoryArrayList;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.list_items_qr_history, parent, false);

        // 3. Get the two text view from the rowView
        TextView created_at   = (TextView) rowView.findViewById(R.id.qr_created_at);
        TextView consumed_at  = (TextView) rowView.findViewById(R.id.qr_consumed_at);
        ImageView img         = (ImageView) rowView.findViewById(R.id.image_qr);

        QRHistory qrHistory = (QRHistory) qrHistoryArrayList.get(position);

        // 4. Set the text for textView
        created_at.setText(""+ qrHistory.getCreated_at());
        consumed_at.setText(""+ qrHistory.getConsumed_at());
        Glide.with(context).load(qrHistory.getImg_qr_src()).into(img);

        // 5. return rowView
        return rowView;
    }




}
