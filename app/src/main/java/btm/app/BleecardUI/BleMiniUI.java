package btm.app.BleecardUI;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import btm.app.R;

public class BleMiniUI extends AppCompatActivity {
    public static final String TAG = "DEV -> UI Mini ";

    Context context = this;
    Button connect_ble, disconnect_ble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_mini_ui);

        connect_ble = (Button) findViewById(R.id.connect);
        disconnect_ble = (Button) findViewById(R.id.disconnect);



    }

}