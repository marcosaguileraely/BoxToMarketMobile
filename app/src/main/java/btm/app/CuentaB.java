package btm.app;

/**
 * Created by aple on 24/02/17.
 */

public class CuentaB {
    private String tipo;
    private String numero;
    private String banco;
    private String clase;

    public CuentaB(String cuenta) {
        String[] info = cuenta.replace("|", ";").split(";");
        this.numero = info[0];
        this.banco = info[1];
        this.clase = info[2];
    }

    public String getTipo() {
        return tipo;
    }

    public String getNumero() {
        return numero;
    }

    public String getBanco() {
        return banco;
    }

    public String getClase() {
        return clase;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return banco+" "+"# "+numero;
    }
}
