package id.toriq.project.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DataList implements Parcelable {
    @SerializedName("rfid")
    @Expose
    private String rfid;
    @SerializedName("kode_artikel")
    @Expose
    private String kodeArtikel;
    @SerializedName("jenis_baju")
    @Expose
    private String jenisBaju;
    @SerializedName("ukuran")
    @Expose
    private String ukuran;
    @SerializedName("warna")
    @Expose
    private String warna;
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("production")
    @Expose
    private String production;
    @SerializedName("supervisor_1")
    @Expose
    private String supervisor1;
    @SerializedName("supervisor_2")
    @Expose
    private String supervisor2;
    @SerializedName("qc")
    @Expose
    private String qc;
    @SerializedName("sku")
    @Expose
    private String sku;
    @SerializedName("production_date")
    @Expose
    private String productionDate;
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

    public DataList(String rfid, String jenisBaju, String ukuran, String warna, String brand, String production, String supervisor1, String supervisor2, String qc, String sku, String productionDate, String lastUpdate, String petugas) {
        this.rfid = rfid;
        this.jenisBaju = jenisBaju;
        this.ukuran = ukuran;
        this.warna = warna;
        this.brand = brand;
        this.production = production;
        this.supervisor1 = supervisor1;
        this.supervisor2 = supervisor2;
        this.qc = qc;
        this.sku = sku;
        this.productionDate = productionDate;
        this.lastUpdate = lastUpdate;
        this.petugas = petugas;
    }

    protected DataList(Parcel in) {
        rfid = in.readString();
        jenisBaju = in.readString();
        ukuran = in.readString();
        warna = in.readString();
        brand = in.readString();
        production = in.readString();
        supervisor1 = in.readString();
        supervisor2 = in.readString();
        qc = in.readString();
        sku = in.readString();
        productionDate = in.readString();
        lastUpdate = in.readString();
        petugas = in.readString();
    }

    public static final Creator<DataList> CREATOR = new Creator<DataList>() {
        @Override
        public DataList createFromParcel(Parcel in) {
            return new DataList(in);
        }

        @Override
        public DataList[] newArray(int size) {
            return new DataList[size];
        }
    };

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

    public String getJenisBaju() {
        return jenisBaju;
    }

    public void setJenisBaju(String jenisBaju) {
        this.jenisBaju = jenisBaju;
    }

    public String getUkuran() {
        return ukuran;
    }

    public void setUkuran(String ukuran) {
        this.ukuran = ukuran;
    }

    public String getWarna() {
        return warna;
    }

    public void setWarna(String warna) {
        this.warna = warna;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public String getSupervisor1() {
        return supervisor1;
    }

    public void setSupervisor1(String supervisor1) {
        this.supervisor1 = supervisor1;
    }

    public String getSupervisor2() {
        return supervisor2;
    }

    public void setSupervisor2(String supervisor2) {
        this.supervisor2 = supervisor2;
    }

    public String getQc() {
        return qc;
    }

    public void setQc(String qc) {
        this.qc = qc;
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(rfid);
        dest.writeString(jenisBaju);
        dest.writeString(ukuran);
        dest.writeString(warna);
        dest.writeString(brand);
        dest.writeString(production);
        dest.writeString(supervisor1);
        dest.writeString(supervisor2);
        dest.writeString(qc);
        dest.writeString(sku);
        dest.writeString(productionDate);
        dest.writeString(lastUpdate);
        dest.writeString(petugas);
    }
}
