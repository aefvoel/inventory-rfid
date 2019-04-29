package id.toriq.project.ui.productActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rscja.deviceapi.RFIDWithUHF;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.toriq.project.R;
import id.toriq.project.adapter.ScanListAdapter;
import id.toriq.project.helper.Utils;
import id.toriq.project.model.DataList;
import id.toriq.project.ui.scanActivity.WriteTagFragment;

public class ProductActivity extends AppCompatActivity
{
    private DatabaseReference mDatabase;
    public RFIDWithUHF mReader;

    @BindView(R.id.product_list)
    RecyclerView scanList;
    @BindView(R.id.txt_stock)
    TextView txtStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);
        displayData();
    }
    @OnClick(R.id.fab_add)
    public void onFabClick(){
        try {
            WriteTagFragment newFragment = new WriteTagFragment();
            newFragment.show(getDialogFragment(), "dialog_fragment_teacher_detail");
        } catch (Exception e) {
            Utils.ToastMessage(getApplicationContext(), "dialog teacher detail error");
        }
    }

    /************************
     * FragmentTransaction for Dialog
     * **********************/
    public FragmentTransaction getDialogFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);

        return ft;
    }

    private void displayData(){
        mDatabase = FirebaseDatabase.getInstance().getReference("data");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                List<DataList> dataList = new ArrayList<>();
                List<String> stringList = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    DataList value = dataSnapshot1.getValue(DataList.class);
                    DataList data = new DataList();
                    String rfid = value.getRfid();
                    String jenisBaju = value.getJenisBaju();
                    String ukuran = value.getUkuran();
                    String warna = value.getWarna();
                    String brand = value.getBrand();
                    String production = value.getProduction();
                    String supervisor1 = value.getSupervisor1();
                    String supervisor2 = value.getSupervisor2();
                    String qc = value.getQc();
                    String lastUpdate = value.getLastUpdate();
                    String petugas = value.getPetugas();
                    String sku = value.getSku();
                    String proDate = value.getProductionDate();

                    data.setRfid(rfid);
                    data.setJenisBaju(jenisBaju);
                    data.setUkuran(ukuran);
                    data.setWarna(warna);
                    data.setBrand(brand);
                    data.setProduction(production);
                    data.setSupervisor1(supervisor1);
                    data.setSupervisor2(supervisor2);
                    data.setQc(qc);
                    data.setLastUpdate(lastUpdate);
                    data.setPetugas(petugas);
                    data.setSku(sku);
                    data.setProductionDate(proDate);
                    dataList.add(data);

                }


                GridLayoutManager mGridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                scanList.setLayoutManager(mGridLayoutManager);
                ScanListAdapter routineListAdapter = new ScanListAdapter(dataList, getApplicationContext(), item -> {
                    try {
                        WriteTagFragment newFragment = WriteTagFragment.newInstance(prepareList(item));
                        newFragment.show(getDialogFragment(), "dialog_fragment_teacher_detail");
                    } catch (Exception e) {
                        Utils.ToastMessage(getApplicationContext(), "dialog teacher detail error");
                    }
                });
                scanList.setAdapter(routineListAdapter);
                txtStock.setText(String.valueOf(dataList.size()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
// Failed to read value
                Log.w("Hello", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public ArrayList<String> prepareList(DataList data) {
        ArrayList<String> value = new ArrayList<>();
        value.add(data.getRfid());
        value.add(data.getJenisBaju());
        value.add(data.getUkuran());
        value.add(data.getWarna());
        value.add(data.getBrand());
        value.add(data.getProduction());
        value.add(data.getSupervisor1());
        value.add(data.getSupervisor2());
        value.add(data.getQc());
        value.add(data.getSku());
        value.add(data.getProductionDate());

        return value;
    }

}
