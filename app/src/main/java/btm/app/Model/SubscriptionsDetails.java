package btm.app.Model;

/**
 * Created by maguilera on 11/7/17.
 */

public class SubscriptionsDetails {

    private String type;
    private String category;
    private int qty;
    private String aux;
    private String price;
    private String seller;
    private String location;
    private String description;

    public SubscriptionsDetails(String type, String category, int qty, String aux, String price, String seller, String location, String description) {
        this.type = type;
        this.category = category;
        this.qty = qty;
        this.aux = aux;
        this.price = price;
        this.seller = seller;
        this.location = location;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getAux() {
        return aux;
    }

    public void setAux(String aux) {
        this.aux = aux;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



}
