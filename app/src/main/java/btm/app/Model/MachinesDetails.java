package btm.app.Model;

import android.util.Log;

import btm.app.DataHolder.DataHolderMachineSearch;

/**
 * Created by maguilera on 9/15/18.
 */

public class MachinesDetails {

    // id,titulo,foto_app,precio,inventario,posicion|

    private String id;
    private String name;
    private String image;
    private String price;
    private int    stock;         // cantidad de productos disponibles
    private String position;      // es el box, lo que antes le llamábamos línea
    private int    CartQty = 0;
    private int    CartTotal = 0;

    public MachinesDetails(String id, String name, String image, String price, int stock, String position) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.stock = stock;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getCartQty() {
        return CartQty;
    }

    public void setCartQty(int cartQty) {
        CartQty = cartQty;
    }

    public int getCartTotal() {
        return CartTotal;
    }

    public void setCartTotal(int cartTotal) {
        CartTotal = cartTotal;
    }

    public void addToQuantity(){
        this.CartQty += 1;
    }

    public void removeFromQuantity(){
        if(this.CartQty > 0){
            this.CartQty -= 1;
        }
    }

    public void totalAmmount(){
        this.CartTotal += Integer.parseInt(this.price) * this.CartQty;
        DataHolderMachineSearch.setTotal_pay(this.CartTotal);
        Log.w("DEV ", " Total ammount: " + this.CartTotal);
    }

}
