package id.toriq.project.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DataList {
    @SerializedName("rfid")
    @Expose
    private String rfid;
    @SerializedName("kode_artikel")
    @Expose
    private String kodeArtikel;
    @SerializedName("ukuran")
    @Expose
    private String ukuran;
    @SerializedName("last_update")
    @Expose
    private String lastUpdate;
    @SerializedName("petugas")
    @Expose
    private String petugas;

    public DataList() {
    }

    public DataList(String rfid, String kodeArtikel, String ukuran, String lastUpdate, String petugas) {
        this.rfid = rfid;
        this.kodeArtikel = kodeArtikel;
        this.ukuran = ukuran;
        this.lastUpdate = lastUpdate;
        this.petugas = petugas;
    }

    public DataList(String rfid, String kodeArtikel, String lastUpdate, String petugas) {
        this.rfid = rfid;
        this.kodeArtikel = kodeArtikel;
        this.lastUpdate = lastUpdate;
        this.petugas = petugas;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getKodeArtikel() {
        return kodeArtikel;
    }

    public void setKodeArtikel(String kodeArtikel) {
        this.kodeArtikel = kodeArtikel;
    }

    public String getUkuran() {
        return ukuran;
    }

    public void setUkuran(String ukuran) {
        this.ukuran = ukuran;
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
}
