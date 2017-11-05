package btm.app.Network;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.android.volley.Request.Method.GET;

/**
 * Created by maguilera on 10/12/17.
 */

public class NetActions {
    Context context;

    public NetActions(Context context) {
        this.context = context;
    }

    public void resetPassword(String metodo, String datos, Response.Listener<String> response, final ProgressDialog pd){
        pd.show();
        String url = "https://www.boxtomarket.com/index.php?r=app/" + metodo + "&datos="+this.datoBase64(datos) + "&token=" + this.tkTime();
        Log.d("Url Response", url.toString());

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(GET, url, response,
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        pd.dismiss();
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(context ,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    public String tkTime(){
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String tk_ = ("aplicacionbtm1"+formatter.format(date));
        return Base64.encodeToString(tk_.getBytes(), Base64.DEFAULT);
    }

    public String datoBase64(String datos){
        return Base64.encodeToString(datos.getBytes(), Base64.DEFAULT);
    }

    public void listSubscriptions(String username, Response.Listener<String> response, final ProgressDialog pd){
        pd.show();

        Log.d("DEV -> NetActions ", this.tkTime());

        String url = "https://www.boxtomarket.com/index.php?r=app/listadosuscripciones&username=" + username + "&token=" + this.tkTime();
        Log.d("DEV -> NetActions ", url);

        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest Request = new StringRequest(GET, url,
                response,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("DEV -> NetActions ", "That didn't work!");
                        Toast.makeText(context ,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
        });

        queue.add(Request.setRetryPolicy(new DefaultRetryPolicy(30000,
                                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    public void listSubscriptionsPublic(){

    }

    public void listClubsPublic(String username, Response.Listener<String> response, final ProgressDialog pd){
        pd.show();

        Log.d("DEV -> NetActions ", this.tkTime());
        //listadoclubes
        String url = "https://www.boxtomarket.com/index.php?r=app/listadoclubes&username=" + username + "&token=" + this.tkTime();
        Log.d("DEV -> NetActions ", url);

        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest Request = new StringRequest(GET, url,
                response,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("DEV -> NetActions ", "That didn't work!");
                        Toast.makeText(context ,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(Request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }





}
