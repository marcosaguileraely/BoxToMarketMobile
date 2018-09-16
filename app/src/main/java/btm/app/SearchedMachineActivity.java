package btm.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import btm.app.Adapters.MachinesSearchAdapter;
import btm.app.Adapters.SubscriptionAdapter;
import btm.app.DataHolder.DataHolder;
import btm.app.DataHolder.DataHolderMachineSearch;
import btm.app.Model.MachinesDetails;
import btm.app.Model.Subscriptions;
import btm.app.Utils.Utils;

import static btm.app.BuyActivity.USER_GLOBAL_SENDER;

public class SearchedMachineActivity extends AppCompatActivity {

    public static final String TAG = "DEV -> Searchs";
    public static final String MACHINEDATA = "";

    String searchedMachineData;
    String searchedMachineCode;
    String code;
    String name;
    String community;
    String location;

    ListView machineList;
    TextView total_pay;
    TextView machine_location;
    TextView machine_id;
    Button   pay;

    Utils utils = new Utils();

    View v;
    Context context = this;
    private MachinesSearchAdapter adapter;
    ArrayList<MachinesDetails> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_machine);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.search_machine_title));

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        orders = new ArrayList<MachinesDetails>();
        searchedMachineData = DataHolderMachineSearch.getSearch_data();
        searchedMachineCode = DataHolderMachineSearch.getMachine_code();

        Log.d(TAG, " Data: " + searchedMachineData);

        machineList      = (ListView) findViewById(R.id.machine_list);
        total_pay        = (TextView) findViewById(R.id.machine_search_total_ammount);
        machine_location = (TextView) findViewById(R.id.machine_location);
        machine_id       = (TextView) findViewById(R.id.machine_id);
        pay              = (Button) findViewById(R.id.machine_search_pay);

        String Machines = utils.getMachine();
        String MachinesReplaced = Machines.replace(",,", ",-,").replace(",|", ",-|");
        //Log.w(TAG, " Machine list: " + MachinesReplaced);
        String MachineArray = getArrayMachines(MachinesReplaced).toString();
        //Log.w(TAG, " Machine array: " + MachineArray);

        getMachineInf(MachineArray);

        machine_location.setText(location);
        machine_id.setText("#"+searchedMachineCode);

        if (searchedMachineData.length() > 0){
            this.MachineslistDetails(v, searchedMachineData);
        }else{
            Toast.makeText(context, "No hemos encontrado máquinas con el código: " + searchedMachineCode, Toast.LENGTH_LONG ).show();
            machine_location.setText("");
            machine_id.setText("#"+"");
        }

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToPay =  new Intent(context, SearchMachinePayActivity.class);
                startActivity(goToPay);
            }
        });
    }

    public void MachineslistDetails(View v, String data){
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.inf_dialog));

        adapter = new MachinesSearchAdapter(SearchedMachineActivity.this, getMachineSearchedList(data));
        adapter.notifyDataSetChanged();
        machineList.setAdapter(adapter);
        adapter.registerDataSetObserver(observer);
    }

    public ArrayList<MachinesDetails> getMachineSearchedList(String data){
        ArrayList<MachinesDetails> items = new ArrayList<MachinesDetails>();

        JSONArray jsonArray   = new JSONArray();
        JSONObject jsonObject = null;
        String[] mainToken    = data.split("\\|");

        for(int i=0 ; i < mainToken.length ; i++){
            String subToken[] = mainToken[i].split(",");
            jsonObject        = new JSONObject();
            try {
                jsonObject.put("id", subToken[0]);
                jsonObject.put("name", subToken[1]);
                jsonObject.put("image", subToken[2]);
                jsonObject.put("price", subToken[3]);
                jsonObject.put("stock", subToken[4]);
                jsonObject.put("position", subToken[5]);

                jsonArray.put(jsonObject);

                items.add(new MachinesDetails( subToken[0], subToken[1], subToken[2], subToken[3], Integer.parseInt(subToken[4]), subToken[5] ));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "-> "+jsonArray.toString());
        return items;
    }

    public JSONArray getArrayMachines(String data){
        JSONArray jsonArray   = new JSONArray();
        JSONObject jsonObject = null;
        JSONObject mainObject = null;
        String[] dataSet      = data.split("\\|");
        //Log.d(TAG, "-> "+data);

        for (int i = 0 ; i < dataSet.length ; i++){
            String[] subDataSet   = dataSet[i].split(",");
            //Log.w(TAG, "-->" + dataSet[i]);
            jsonObject            = new JSONObject();
            mainObject            = new JSONObject();

            try {
                jsonObject.put("machine_code", subDataSet[0]);
                jsonObject.put("machine_name", subDataSet[1]);
                jsonObject.put("machine_comunity", subDataSet[2]);
                jsonObject.put("machine_location", subDataSet[3]);

                mainObject.put(subDataSet[0], jsonObject);  // adding OBJECT of objects
                jsonArray.put(mainObject);                  // adding ARRAY of objects

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        Log.d(TAG, "-> "+jsonArray.toString());
        return jsonArray;
    }

    public void getMachineInf(String data){
        try {
            JSONArray jsonArray = new JSONArray(data);
            String[] strArr = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                strArr[i] = jsonArray.getString(i);
                //Log.w(TAG, "---> " + strArr[i].toString());

                if(strArr[i].contains(searchedMachineCode)){
                    //Log.w(TAG, "///////// " + strArr[i].toString());
                    JSONObject obj = new JSONObject(strArr[i]);
                    JSONObject _jObjectSub = obj.getJSONObject(searchedMachineCode);
                    code      = _jObjectSub.getString("machine_code");
                    name      = _jObjectSub.getString("machine_name");
                    community = _jObjectSub.getString("machine_comunity");
                    location  = _jObjectSub.getString("machine_location");
                    //Log.w(TAG, "///////// " + location);

                }else {
                    // Nothing to do here!
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            //Log.w(TAG, "Adapter change");
            setMealTotal();
        }
    };

    public int calculateTotal(){
        int orderTotal = 0;
        for(MachinesDetails order : orders){
            orderTotal += order.getCartQty() * Integer.parseInt(order.getPrice());
        }
        //Log.w(TAG, " Total change: " + orderTotal);
        return orderTotal;

    }

    public void setMealTotal(){
        //Log.w(TAG, " Setting value: " + DataHolderMachineSearch.getTotal_pay());
        total_pay.setText("Total: $" + DataHolderMachineSearch.getTotal_pay());
    }

    public void onBackPressed(){
        Intent gotoMain = new Intent(SearchedMachineActivity.this, MainActivity.class);
        gotoMain.putExtra(USER_GLOBAL_SENDER, DataHolder.getUsername());
        startActivity(gotoMain);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
