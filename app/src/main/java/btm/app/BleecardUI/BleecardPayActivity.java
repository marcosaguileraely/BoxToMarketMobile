package btm.app.BleecardUI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import btm.app.DataHolder.DataHolderBleData;
import btm.app.R;

public class BleecardPayActivity extends AppCompatActivity {
    private final static String TAG = "Dev -> Bleecard Pay ";


    //Data type variables
    String macAddr = DataHolderBleData.getMac();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bleecard_pay);

        Log.d(TAG, "Mac Address: " + macAddr);



    }
}
