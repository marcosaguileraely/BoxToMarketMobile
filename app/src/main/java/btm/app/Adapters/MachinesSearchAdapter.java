package btm.app.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import btm.app.DataHolder.DataHolderMachineSearch;
import btm.app.Model.MachinesDetails;
import btm.app.Model.Subscriptions;
import btm.app.R;

/**
 * Created by maguilera on 9/15/18.
 */

public class MachinesSearchAdapter extends ArrayAdapter<MachinesDetails> {

    public static final String TAG = "DEV ";
    Context context;
    ArrayList<MachinesDetails> machinesDetailsArrayList;

    String searchedMachineData;
    Integer i = 1;
    Integer item_price = 0;

    Button   plus, less;
    TextView qty;
    TextView machine_name;
    TextView machine_price;
    TextView machine_position;
    ImageView img;

    String static_uri = "https://www.boxtomarket.com/img/beneficios/";

    public MachinesSearchAdapter(Context context, ArrayList<MachinesDetails> machinesDetailsArrayList) {
        super(context, R.layout.list_items_machine_search , machinesDetailsArrayList);
        this.machinesDetailsArrayList = machinesDetailsArrayList;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.list_items_machine_search, parent, false);

        // 3. Get the two text view from the rowView
        machine_name      = (TextView) rowView.findViewById(R.id.machine_name);
        machine_price     = (TextView) rowView.findViewById(R.id.machine_price);
        machine_position  = (TextView) rowView.findViewById(R.id.machine_box_position);
        qty               = (TextView) rowView.findViewById(R.id.machine_item_count);
        plus              = (Button) rowView.findViewById(R.id.machine_increase_item);
        less              = (Button) rowView.findViewById(R.id.machine_decrease_item);
        img               = (ImageView) rowView.findViewById(R.id.machine_image);

        final MachinesDetails machinesDetails = (MachinesDetails) machinesDetailsArrayList.get(position);

        // 4. Set the text for textView
        machine_name.setText(machinesDetails.getName());
        machine_price.setText("$" + machinesDetails.getPrice());
        machine_position.setText("Box #" + machinesDetails.getPosition());
        Glide.with(context).load(static_uri + machinesDetails.getImage()).into(img);
        qty.setText(""+machinesDetails.getCartQty());

        Log.w(TAG, " Position=" + position
                 + " Id=" + machinesDetails.getId()
                 + " Line=" + machinesDetails.getPosition()
                 + " Name=" + machinesDetails.getName()
                 + " Price= "+ machinesDetails.getPrice()
                 + " Qty=" + machinesDetails.getCartQty() );

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                machinesDetails.addToQuantity();
                qty.setText(""+machinesDetails.getCartQty());
                Log.w(TAG, " NEW GRAND TOTAL = " + getTotal(machinesDetailsArrayList));
                Log.w(TAG, " STRING CONCAT = " + getConcatLinesQty(machinesDetailsArrayList));
                notifyDataSetChanged();
            }
        });

        less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                machinesDetails.removeFromQuantity();
                qty.setText(""+machinesDetails.getCartQty());
                Log.w(TAG, " NEW GRAND TOTAL = " + getTotal(machinesDetailsArrayList));
                Log.w(TAG, " STRING CONCAT = " + getConcatLinesQty(machinesDetailsArrayList));
                notifyDataSetChanged();
            }
        });

        // 5. return rowView
        return rowView;
    }

    public int getTotal(ArrayList<MachinesDetails> list){

        int total = 0;
        for(int i = 0; i < list.size() ; i++){
            total = total + ( list.get(i).getCartQty() * Integer.parseInt(list.get(i).getPrice()) );
        }
        DataHolderMachineSearch.setTotal_pay(total);
        return total;
    }

    public String getConcatLinesQty(ArrayList<MachinesDetails> list){
        String concatCart = "";
        for(int i = 0; i < list.size() ; i++){
            if( list.get(i).getCartQty() > 0 ){
                concatCart = concatCart + ( list.get(i).getPosition() + "," + list.get(i).getCartQty() + "-");
            }else {
                Log.w(TAG, " Position=" + list.get(i).getPosition() + " has been ignored! ");
            }
        }
        String concatCartFixed = concatCart.substring(0, concatCart.length() - 1);
        DataHolderMachineSearch.setLines_to_pay(concatCartFixed);
        Log.w(TAG, "Cart= " + concatCartFixed);
        return concatCartFixed;
    }


}
