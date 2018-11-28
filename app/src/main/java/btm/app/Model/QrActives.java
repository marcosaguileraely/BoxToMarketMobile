package btm.app.Model;

/**
 * Created by maguilera on 10/9/18.
 */

public class QrActives {

    private String serial;
    private String date_time;
    private String brand_image_img;
    private String total;
    private String qr_code;

    public QrActives(String serial, String date_time, String brand_image_img, String total, String qr_code) {
        this.serial = serial;
        this.date_time = date_time;
        this.brand_image_img = brand_image_img;
        this.total = total;
        this.qr_code = qr_code;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getBrand_image_img() {
        return brand_image_img;
    }

    public void setBrand_image_img(String brand_image_img) {
        this.brand_image_img = brand_image_img;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }
}
