package mobile.smartmarket.smartmarket.beans;

/**
 * Created by omar on 12/6/16.
 */
public class BasicDataBean {
    private String message;
    private String title;
    private String idcard;
    private String vendedor;
    private String monto;

    public BasicDataBean(String message, String title, String idcard, String vendedor, String monto) {
        this.message = message;
        this.title = title;
        this.idcard = idcard;
        this.vendedor = vendedor;
        this.monto = monto;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BasicDataBean{");
        sb.append("message='").append(message).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", idcard='").append(idcard).append('\'');
        sb.append(", vendedor='").append(vendedor).append('\'');
        sb.append(", monto='").append(monto).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
