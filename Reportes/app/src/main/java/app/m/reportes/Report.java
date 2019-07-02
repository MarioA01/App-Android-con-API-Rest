package app.m.reportes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Report {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("descripcion")
    @Expose
    private String descripcion;

    @SerializedName("date_time")
    @Expose
    private String date_time;

    @SerializedName("productImage")
    @Expose
    private String productImage;

    @SerializedName("usuario_id")
    @Expose
    private String usuario_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(String usuario_id) {
        this.usuario_id = usuario_id;
    }
}
