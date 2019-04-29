package id.toriq.project.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InfoList {
    @SerializedName("artikel")
    @Expose
    private String artikel;
    @SerializedName("lastUpdate")
    @Expose
    private String lastUpdate;
    @SerializedName("petugas")
    @Expose
    private String petugas;
    @SerializedName("products")
    @Expose
    private String products;

    public InfoList() {
    }

    public InfoList(String artikel, String lastUpdate, String petugas, String products) {
        this.artikel = artikel;
        this.lastUpdate = lastUpdate;
        this.petugas = petugas;
        this.products = products;
    }

    public String getArtikel() {
        return artikel;
    }

    public void setArtikel(String artikel) {
        this.artikel = artikel;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getPetugas() {
        return petugas;
    }

    public void setPetugas(String petugas) {
        this.petugas = petugas;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }
}
