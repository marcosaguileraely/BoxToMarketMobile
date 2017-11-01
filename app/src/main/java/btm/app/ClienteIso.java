package btm.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.impl.SimpleTraceGenerator;
import com.solab.iso8583.parse.ConfigParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/** Implements a very simple TCP client application that connects to a server and sends some
 * requests, displaying the response codes and confirmations.
 * 
 * @author Martin F.
 */

public class ClienteIso implements Runnable {
	

	//private static final Logger log = LoggerFactory.getLogger(ClienteIso.class);

	private static MessageFactory mfact;

	private Socket sock;
	private boolean done = false;
	private static IsoMessage resp = null;
	private static boolean tarea = true; 
	public static WifiManager wifi;
	public static SharedPreferences Pref;
	public static Context contextTW;
	
	
	public ClienteIso(Socket socket, int modo) throws IOException {
		/*byte[] host_dir = {	0x31,0x38,0x34,0x2E,
							0x39,0x34,0x2E,
							0x31,0x35,0x38,0x2E,
							0x31,0x30,0x31 };*/
		//String host = Pref.getString("INFO_HOST", "184.94.158.101");
		//107.170.126.94
		byte[] host_dir = { 0x31, 0x30, 0x37, 0x2E,
							0x31, 0x37, 0x30, 0x2E,
							0x31, 0x32, 0x36, 0x2E,
							0x39, 0x34 };
		String host = new String(host_dir);
		int port = 12552;
		sock = socket; 
		sock.connect(new InetSocketAddress(host,port),20000);
		mfact.setUseBinaryMessages(true);

		resp = null;
		tarea = true;
	}

    /** This method is invoked from a dedicated thread, to read any response from the socket. */
	public void run() {
		byte[] lenbuf = new byte[2];
		try {
			while (sock != null && sock.isConnected()) {
				sock.getInputStream().read(lenbuf);
				int size = ((lenbuf[0] & 0xff) << 8) | (lenbuf[1] & 0xff);
				byte[] buf = new byte[size];
				//We're not expecting ETX in this case
				if (sock.getInputStream().read(buf) == size) {
					try {
						//We'll use this header length as a reference.
						//In practice, ISO headers for any message type are the same length.
						String respHeader = mfact.getIsoHeader(0x210);
						if(buf.length<15){
							//log.error("<--buffer menor a 15 bytes-->");
							tarea = false;
							return;
						}
						resp = mfact.parseMessage(buf,
							respHeader == null ? 12 : respHeader.length());
						//log.debug("Read response {} field resp {}: {}", new Object[]{
							//resp.getField(11), resp.getField(39), new String(buf)});
//						pending.remove(resp.getField(11).toString());
					
					} catch (ParseException ex) {
						//log.error("Parsing response", ex);
						tarea = false;
						return;
					} catch (Exception ex) {
						//log.error("Response Fail ", ex);
						tarea = false;
						return;
					}
				} else {
					tarea = false;
					return;
				}
			}
		} catch (IOException ex) {
			if (done) {
				//log.info("Socket closed because we're done");
			} else {
				//log.error("Reading response, ({})", ex);
				tarea = false;
				return;
			}
		} finally {
			if (sock != null) {
				try {
					sock.close();
				} catch (IOException ex) {};
			}
		}
	}

	protected void stop() {
		done = true;
		try {
			sock.close();
		} catch (IOException ex) {
			//log.error("Couldn't close socket");
		}
		sock = null;
	}

	public static String[] msjIso(int modo, String pcode, String cp_0, String cp_1, String cp_2, String cp_3, int monto) {
		
		String[] ret = new String[5];
		
		try{
			//log.debug("Reading config");
			//URL url_conf = new URL ("http://192.168.0.5/config.xml");
			//mfact = ConfigParser.createFromUrl(url_conf); 
			InfoOpcional.contextTW = contextTW;
			mfact = ConfigParser.createFromClasspathConfig(new String(InfoOpcional.decrypt(contextTW.getResources().
					getStringArray(R.array.opcional)[1])));
			mfact.setAssignDate(true);
			//mfact.setTraceNumberGenerator(new SimpleTraceGenerator((int)(System.currentTimeMillis() % 10000)));
			mfact.setTraceNumberGenerator(new SimpleTraceGenerator(Pref.getInt("STAN",1)));
			mfact.setCharacterEncoding("ISO-8859-1");
			//log.debug("Connecting to server");
			//Socket sock = new Socket();
			
			// load client certificate
		    KeyStore keyStore = null;
		    keyStore = KeyStore.getInstance("BKS");
		    InputStream keyStoreStream = contextTW.getResources().openRawResource(R.raw.client);
		    keyStore.load(keyStoreStream, new String(InfoOpcional.decrypt(contextTW.getResources().
					getStringArray(R.array.opcional)[2])).toCharArray());
		      
		    //TrustManager diferente que acepte todos los certificados y no nos genere un error 
		    //y utilizar este para la creaci�n del Socket.
		    X509TrustManager tm = new X509TrustManager() {
		    	public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException { }
		    	public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException { }
		    	public X509Certificate[] getAcceptedIssuers() { return null; }
		    	};            
	    	
	    	// Create an SSLContext that uses our TrustManager
		    SSLContext ctx = SSLContext.getInstance("TLS");
		    ctx.init(null, new TrustManager[]{tm}, null);
	
		    // Crea el socket
		    javax.net.ssl.SSLSocketFactory sslsocketfactory = ctx.getSocketFactory(); 
	    	SSLSocket sock = (SSLSocket) sslsocketfactory.createSocket();
			
			ClienteIso client = new ClienteIso(sock, modo);
			Thread reader = new Thread(client, "j8583-client");
			
			reader.start();
			IsoMessage req = mfact.newMessage(modo);
						
			req.setValue(12, req.getObjectValue(7), IsoType.TIME, 0);
			req.setValue(13, req.getObjectValue(7), IsoType.DATE4, 0);req.setEtx(0x00);
			req.setValue(41, macId(), IsoType.ALPHA, 8);
			req.setValue(3, pcode, IsoType.NUMERIC, 6);
			req.setValue(4, monto, IsoType.AMOUNT, 0);
			if(!cp_0.isEmpty()) req.setValue(60, cp_0, IsoType.LLLVAR, 0);
			if(!cp_1.isEmpty()) req.setValue(61, cp_1, IsoType.LLLVAR, 0);
			if(!cp_2.isEmpty()) req.setValue(62, cp_2, IsoType.LLLVAR, 0);
			if(!cp_3.isEmpty()) req.setValue(63, cp_3, IsoType.LLLVAR, 0);
							
				//log.debug("Sending request {}", req.getField(11));
				req.write(sock.getOutputStream(), 2);
			
			//log.debug("Waiting for responses");
			//client.stop();
			//boolean compareTo = true;
			while (resp == null){
				//rssi = wifiInf.getRssi()*-1;
				//compareTo = wifiInf.getSupplicantState() == android.net.wifi.SupplicantState.COMPLETED;
				if(!tarea /*|| ((rssi > rssiMax) || !compareTo) && rssiLimit*/){
					/*log.error("Estado de WIFI {}",  wifiInf.getSupplicantState());
					log.error("Se�al de WIFI {}", rssi);*/
					reader.join(2000);
					client.stop();
					ret[0] = "04";
					ret[1] = contextTW.getResources().getString(R.string.info_fallas_comunicacion);
					return ret;	
					
					//break;
				} 
			}
			
			reader.interrupt();
			reader.join(2000);
			
			client.stop();
			//log.debug("DONE.");
						
			int cp_39 =(resp.hasField(39))? Integer.parseInt(resp.getField(39).toString()):-1;
			//log.debug("Verificando campo 39: {}",cp_39);
			
			switch (cp_39) {
			case 0:
			case 1:
			case 2:
			case 3:
				stan();				
				break;

			default:
				break;
			}
						
			ret[0] = resp.getField(39).toString();
			ret[1] = (resp.hasField(60))?resp.getField(60).toString():"";
			ret[2] = (resp.hasField(61))?resp.getField(61).toString():"";
			ret[3] = (resp.hasField(62))?resp.getField(62).toString():"";
			ret[4] = (resp.hasField(63))?resp.getField(63).toString():"";
			
			return ret;
						
		} catch (ConnectException e){
			//tag = "";
			e.printStackTrace();
			ret[0] = "04";
			ret[1] = contextTW.getResources().getString(R.string.info_fallas_comunicacion)+" - C-01";
			return ret;	
		} catch (SocketException e){
			e.printStackTrace();
			ret[0] = "04";
			ret[1] = contextTW.getResources().getString(R.string.info_fallas_comunicacion)+" - C-02";
			return ret;	
		} catch (SocketTimeoutException e){
			e.printStackTrace();
			ret[0] = "04";
			ret[1] = contextTW.getResources().getString(R.string.info_fallas_comunicacion)+" - C-03";
			return ret;	
		} catch (Exception e){
			//tag = "";
			e.printStackTrace();
			ret[0] = "04";
			ret[1] = e.getLocalizedMessage();
			return ret;	
		}
		
		
	}
		
	static String macId(){
		try{
			WifiInfo wifiInf = wifi.getConnectionInfo();
			//int rssi = wifiInf.getRssi();
			//log.debug("se�al de WIFI {}", rssi);
			String macAddr = wifiInf.getMacAddress();
			return macAddr.replace(":", "").substring(4).toUpperCase();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "0000";		
	}
	
	static void stan(){
		SharedPreferences.Editor editor = Pref.edit();
		int i = Pref.getInt("STAN",1);
		if(i==999999)i=1;
		editor.putInt("STAN", ++i);
		editor.commit();
	}
	
	//CONEXION SIN SSL
	public ClienteIso(Socket socket, int modo, int port) throws IOException {
		/*byte[] host_dir = {	0x31,0x38,0x34,0x2E,
							0x39,0x34,0x2E,
							0x31,0x35,0x38,0x2E,
							0x31,0x30,0x31 };*/
		//String host = Pref.getString("INFO_HOST", "184.94.158.101");
		//69.175.118.243
		byte[] host_dir = { 0x31, 0x30, 0x37, 0x2E,
							0x31, 0x37, 0x30, 0x2E,
							0x31, 0x32, 0x36, 0x2E,
							0x39, 0x34 };
		String host = new String(host_dir);
		sock = socket; 
		sock.connect(new InetSocketAddress(host,port),20000);
		mfact.setUseBinaryMessages(true);

		resp = null;
		tarea = true;
	}
	

	public static String[] msjIso_h(int modo, String pcode, String cp_0, String cp_1, String cp_2, String cp_3, int monto, int puerto) {
		
		String[] ret = new String[5];
		
		try{
			//log.debug("Reading config");
			//URL url_conf = new URL ("http://192.168.0.5/config.xml");
			//mfact = ConfigParser.createFromUrl(url_conf); 
			InfoOpcional.contextTW = contextTW;
			mfact = ConfigParser.createFromClasspathConfig(new String(InfoOpcional.decrypt("wFET0oqRqoJNkIPA3xWnq7kPrADg9A2FK/artD/TNT6BLDRAu6sE0C4Kri6eCo5m")));

			//mfact = ConfigParser.createFromClasspathConfig(new String(InfoOpcional.decrypt(contextTW.getResources().
			//		getStringArray(R.array.opcional)[1])));

			mfact.setAssignDate(true);
			//mfact.setTraceNumberGenerator(new SimpleTraceGenerator((int)(System.currentTimeMillis() % 10000)));
			mfact.setTraceNumberGenerator(new SimpleTraceGenerator(Pref.getInt("STAN",1)));
			mfact.setCharacterEncoding("ISO-8859-1");
			//log.debug("Connecting to server");
			Socket sock = new Socket();
			
			ClienteIso client = new ClienteIso(sock, modo, puerto);
			Thread reader = new Thread(client, "j8583-client");
			
			reader.start();
			IsoMessage req = mfact.newMessage(modo);
						
			req.setValue(12, req.getObjectValue(7), IsoType.TIME, 0);
			req.setValue(13, req.getObjectValue(7), IsoType.DATE4, 0);req.setEtx(0x00);
			req.setValue(41, macId(), IsoType.ALPHA, 8);
			req.setValue(3, pcode, IsoType.NUMERIC, 6);
			req.setValue(4, monto, IsoType.AMOUNT, 0);
			if(!cp_0.isEmpty()) req.setValue(60, cp_0, IsoType.LLLVAR, 0);
			if(!cp_1.isEmpty()) req.setValue(61, cp_1, IsoType.LLLVAR, 0);
			if(!cp_2.isEmpty()) req.setValue(62, cp_2, IsoType.LLLVAR, 0);
			if(!cp_3.isEmpty()) req.setValue(63, cp_3, IsoType.LLLVAR, 0);
							
				//log.debug("Sending request {}", req.getField(11));
				req.write(sock.getOutputStream(), 2);
			
			//log.debug("Waiting for responses");
			//client.stop();
			//boolean compareTo = true;
			while (resp == null){
				//rssi = wifiInf.getRssi()*-1;
				//compareTo = wifiInf.getSupplicantState() == android.net.wifi.SupplicantState.COMPLETED;
				if(!tarea /*|| ((rssi > rssiMax) || !compareTo) && rssiLimit*/){
					/*log.error("Estado de WIFI {}",  wifiInf.getSupplicantState());
					log.error("Se�al de WIFI {}", rssi);*/
					reader.join(2000);
					client.stop();
					ret[0] = "04";
					ret[1] = contextTW.getResources().getString(R.string.info_fallas_comunicacion);
					return ret;	
					
					//break;
				} 
			}
			
			reader.interrupt();
			reader.join(2000);
			
			client.stop();
			//log.debug("DONE.");
						
			int cp_39 =(resp.hasField(39))? Integer.parseInt(resp.getField(39).toString()):-1;
			//log.debug("Verificando campo 39: {}",cp_39);
			
			switch (cp_39) {
			case 0:
			case 1:
			case 2:
			case 3:
				stan();				
				break;

			default:
				break;
			}
						
			ret[0] = resp.getField(39).toString();
			ret[1] = (resp.hasField(60))?resp.getField(60).toString():"";
			ret[2] = (resp.hasField(61))?resp.getField(61).toString():"";
			ret[3] = (resp.hasField(62))?resp.getField(62).toString():"";
			ret[4] = (resp.hasField(63))?resp.getField(63).toString():"";
			
			return ret;
						
		} catch (ConnectException e){
			//tag = "";
			e.printStackTrace();
			ret[0] = "04";
			ret[1] = contextTW.getResources().getString(R.string.info_fallas_comunicacion)+" - C-01";
			return ret;	
		} catch (SocketException e){
			e.printStackTrace();
			ret[0] = "04";
			ret[1] = contextTW.getResources().getString(R.string.info_fallas_comunicacion)+" - C-02";
			return ret;	
		} catch (SocketTimeoutException e){
			e.printStackTrace();
			ret[0] = "04";
			ret[1] = contextTW.getResources().getString(R.string.info_fallas_comunicacion)+" - C-03";
			return ret;	
		} catch (Exception e){
			//tag = "";
			e.printStackTrace();
			ret[0] = "04";
			ret[1] = e.getLocalizedMessage();
			return ret;	
		}
		
		
	}
	
	
}

