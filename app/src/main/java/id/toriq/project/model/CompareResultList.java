package id.toriq.project.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CompareResultList {
    @SerializedName("artikelId")
    @Expose
    private String artikelId;
    @SerializedName("jumlah")
    @Expose
    private String jumlah;

    public String getArtikelId() {
        return artikelId;
    }

    public void setArtikelId(String artikelId) {
        this.artikelId = artikelId;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public CompareResultList() {
    }

    public CompareResultList(String artikelId, String jumlah) {
        this.artikelId = artikelId;
        this.jumlah = jumlah;
    }
}
