package btm.app.BleecardUI;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

//import com.polidea.rxandroidble2.RxBleClient;
//import com.polidea.rxandroidble2.scan.ScanSettings;

//import org.reactivestreams.Subscription;

import btm.app.DataHolder.DataHolderBleData;
import btm.app.R;
//import io.reactivex.disposables.Disposable;

public class BleecardPayActivity extends AppCompatActivity {
    private final static String TAG = "Dev -> Bleecard Pay ";

    Context context = this;


    //Data type variables
    String macAddr = DataHolderBleData.getMac();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bleecard_pay);

        Log.d(TAG, "Mac Address: " + macAddr);

        //RxBleClient rxBleClient = RxBleClient.create(context);




    }
}
