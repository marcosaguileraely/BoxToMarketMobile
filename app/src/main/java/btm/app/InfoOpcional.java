package btm.app;

import android.content.Context;
import android.util.Base64;

import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class InfoOpcional {
	
	public static Context contextTW;
	private static final String RANDOM_ALGORITHM = "SHA1PRNG";

/*		public static byte[] encrypt(String toEncrypt) throws Exception{
	        byte [] key = {	(byte)0x98, (byte)0xb4, (byte)0x96, (byte)0x0c,
			        		(byte)0xd7, (byte)0xf5, (byte)0x04, (byte)0x11,
			        		(byte)0x80, (byte)0x0c, (byte)0x1b, (byte)0x54,
			        		(byte)0x70, (byte)0x6a, (byte)0x23, (byte)0xfe };
	        
	        SecretKeySpec keyspec = new SecretKeySpec(key, "AES");
	
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE,keyspec);
	      
	        AlgorithmParameters params = cipher.getParameters();
	        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
	        byte[] encrypted = cipher.doFinal(toEncrypt.getBytes());
	        
	        byte[] infoEncrip = new byte[iv.length + encrypted.length];
	        
	        System.arraycopy(iv, 0, infoEncrip, 0, iv.length);
	        System.arraycopy(encrypted, 0, infoEncrip, iv.length, encrypted.length);
	        
	        return infoEncrip;
	        
	    }*/

	  /**
	   * Desencripta info base64
	   * @param txtDecrypt
	   * @return
	   * @throws Exception
	   */
	  public static byte[] decrypt(String txtDecrypt) throws Exception {
		  byte[] toDecrypt = Base64.decode(txtDecrypt, Base64.DEFAULT);
	      byte[] iv = new byte[16];
	      System.arraycopy(toDecrypt, 0, iv, 0, iv.length);
	      byte[] encripData = new byte[toDecrypt.length-iv.length];
	      System.arraycopy(toDecrypt, iv.length, encripData, 0, encripData.length);
	      byte [] key = hexStringToByteArray(new String(Base64.decode(contextTW.getResources().
				  getStringArray(R.array.opcional)[3], Base64.DEFAULT)));
	      /*
	      byte [] key = {	(byte)0x98, (byte)0xb4, (byte)0x96, (byte)0x0c,
	      		(byte)0xd7, (byte)0xf5, (byte)0x04, (byte)0x11,
	      		(byte)0x80, (byte)0x0c, (byte)0x1b, (byte)0x54,
	      		(byte)0x70, (byte)0x6a, (byte)0x23, (byte)0xfe };
	      */
	      SecretKeySpec keyspec = new SecretKeySpec(key, "AES");
	      IvParameterSpec ivspec = new IvParameterSpec(iv);
	
	      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	      cipher.init(Cipher.DECRYPT_MODE,keyspec,ivspec);
	      byte[] decrypted = cipher.doFinal(encripData);
	
	      return decrypted;
	  }
	  
	  /**
	   * Encripcion del pin
	   * @param toEncrypt
	   * @return
	   * @throws Exception
	   */
	  public static String encrypt(String toEncrypt) throws Exception {
	        byte [] key = hexStringToByteArray(new String(decrypt(contextTW.getResources().
					getStringArray(R.array.opcional)[4])));
	        /*"98b4960cd7f50411800c1b54706a23fe"*/
	        /*{	(byte)0x98, (byte)0xb4, (byte)0x96, (byte)0x0c,
			        		(byte)0xd7, (byte)0xf5, (byte)0x04, (byte)0x11,
			        		(byte)0x80, (byte)0x0c, (byte)0x1b, (byte)0x54,
			        		(byte)0x70, (byte)0x6a, (byte)0x23, (byte)0xfe };
	        */
	        SecretKeySpec keyspec = new SecretKeySpec(key, "AES");
	        byte[] iv = generateIv();
	        IvParameterSpec ivspec = new IvParameterSpec(iv);
	
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE,keyspec,ivspec);
	      
	        AlgorithmParameters params = cipher.getParameters();
	        //byte[] iv = cipher.getIV();//params.getParameterSpec(IvParameterSpec.class).getIV();
	        byte[] encrypted = cipher.doFinal(toEncrypt.getBytes());
	        
	        byte[] infoEncrip = new byte[iv.length + encrypted.length];
	        
	        System.arraycopy(iv, 0, infoEncrip, 0, iv.length);
	        System.arraycopy(encrypted, 0, infoEncrip, iv.length, encrypted.length);
	        
	        //String encriptado = new String(Base64.encode(infoEncrip, Base64.DEFAULT),"UTF-8");
	        String encriptado = bytesToHex(infoEncrip);
	        //System.out.println(encriptado);
	        return encriptado;
	        
	    }
	  
	  private static byte[] generateIv() throws NoSuchAlgorithmException {
	        SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM);
	        byte[] iv = new byte[16];
	        random.nextBytes(iv);
	        return iv;
	    }
	  
	  public static String bytesToHex(byte[] data) {
	    if (data==null)
	    {
	      return null;
	    }
	    else
	    {
	      int len = data.length;
	      String str = "";
	      for (int i=0; i<len; i++)
	      {
	        if ((data[i]&0xFF)<16)
	          str = str + "0" + java.lang.Integer.toHexString(data[i]&0xFF);
	        else
	          str = str + java.lang.Integer.toHexString(data[i]&0xFF);
	      }
	      return str.toUpperCase();
	    }
	  }
	  
	  public static byte[] hexStringToByteArray(String s) {
	        int len = s.length();
	        byte[] data = new byte[len / 2];
	        try {
	            for (int i = 0; i < len; i += 2) {
	                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                                     + Character.digit(s.charAt(i + 1), 16));
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return data;
	    }
	  /*
	  public static String decrypt_(String to_Decrypt) throws Exception{
		  byte[] toDecrypt = hexStringToByteArray(to_Decrypt);
		  
	      byte[] iv = new byte[16];
	      System.arraycopy(toDecrypt, 0, iv, 0, iv.length);
	      byte[] encripData = new byte[toDecrypt.length-iv.length];
	      System.arraycopy(toDecrypt, iv.length, encripData, 0, encripData.length);
	      
	      byte [] key = {	(byte)0x98, (byte)0xb4, (byte)0x96, (byte)0x0c,
	      		(byte)0xd7, (byte)0xf5, (byte)0x04, (byte)0x11,
	      		(byte)0x80, (byte)0x0c, (byte)0x1b, (byte)0x54,
	      		(byte)0x70, (byte)0x6a, (byte)0x23, (byte)0xfe };
	      
	      SecretKeySpec keyspec = new SecretKeySpec(key, "AES");
	      IvParameterSpec ivspec = new IvParameterSpec(iv);
	
	      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	      cipher.init(Cipher.DECRYPT_MODE,keyspec,ivspec);
	      byte[] decrypted = cipher.doFinal(encripData);
	
	      return new String(decrypted);
	  }*/
	  
	  
}
