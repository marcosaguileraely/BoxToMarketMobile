package btm.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by aple on 9/02/17.
 */

public class Request {
    Context context;

    public Request(Context context) {
        this.context = context;
    }

    public void http_get(String metodo, String datos, Response.Listener<String> response){

        String url = "https://www.boxtomarket.com/index.php?r=app/"
                + metodo
                + datos;

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
                response,
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(context ,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    public void http_get(String metodo, String datos, Response.Listener<String> response, final ProgressDialog pd){

        pd.show();
        String url = "https://www.boxtomarket.com/index.php?r=app/"
                + metodo
                + datos;

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
                response,
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        pd.dismiss();
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(context ,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    public static String tkTime(){
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String tk_ = ("aplicacionbtm1"+formatter.format(date));
        return Base64.encodeToString(tk_.getBytes(), Base64.DEFAULT);
    }

    public String tk(){
        SharedPreferences shPref = context.getSharedPreferences(context.getResources().getString(R.string.preferencias), Context.MODE_PRIVATE);
        String ret = shPref.getString(LoginActivity.TOKEN,tkTime());
        return Base64.encodeToString(("aplicacionbtm1"+ret).getBytes(), Base64.DEFAULT);
    }


}
