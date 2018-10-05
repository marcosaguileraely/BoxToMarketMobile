package btm.app.Model;

/**
 * Created by maguilera on 10/4/18.
 */

public class QRHistory {

    private String serial;
    private String created_at;
    private String consumed_at;
    private String total;
    private String img_qr_src;

    public QRHistory(String serial, String created_at, String consumed_at, String total, String img_qr_src) {
        this.serial = serial;
        this.created_at = created_at;
        this.consumed_at = consumed_at;
        this.total = total;
        this.img_qr_src = img_qr_src;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getConsumed_at() {
        return consumed_at;
    }

    public void setConsumed_at(String consumed_at) {
        this.consumed_at = consumed_at;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getImg_qr_src() {
        return img_qr_src;
    }

    public void setImg_qr_src(String img_qr_src) {
        this.img_qr_src = img_qr_src;
    }

}
