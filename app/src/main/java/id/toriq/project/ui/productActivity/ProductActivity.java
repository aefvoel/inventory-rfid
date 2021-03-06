package id.toriq.project.ui.productActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

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
import id.toriq.project.ui.scanActivity.ScanActivity;
import id.toriq.project.ui.scanActivity.WriteTagFragment;

public class ProductActivity extends AppCompatActivity
{
    private DatabaseReference mDatabase;
    public RFIDWithUHF mReader;
    private ScanListAdapter routineListAdapter;
    private List<DataList> dataList;
    private ProgressDialog mypDialog;

    @BindView(R.id.search_view)
    SearchView searchView;

    @BindView(R.id.product_list)
    RecyclerView scanList;

    @BindView(R.id.card)
    CardView cardInfo;
    @BindView(R.id.txt_prod)
    TextView txtProd;
    @BindView(R.id.fab_add)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);
        mypDialog = new ProgressDialog(ProductActivity.this);
        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mypDialog.setMessage("Retrieving Data");
        cardInfo.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);
        mypDialog.setCanceledOnTouchOutside(false);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
            displayData(bundle.getString("artikelId"));
        else
            displayData();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    routineListAdapter.filter(query);
                } catch (NullPointerException e){
                    Utils.ToastMessage(getApplicationContext(), "data not found");
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    routineListAdapter.filter(newText);
                } catch (NullPointerException e){
                    Utils.ToastMessage(getApplicationContext(), "data not found");
                }
                return true;
            }
        });
    }
    @OnClick(R.id.fab_add)
    public void onFabClick(){
        startActivity(new Intent(ProductActivity.this, CompareActivity.class));
    }

    /************************
     * FragmentTransaction for Dialog
     * **********************/
    public FragmentTransaction getDialogFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);

        return ft;
    }

    private void displayData(String artikelId){
        mypDialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference("data-product-registered");
        mDatabase.orderByChild("kodeArtikel").equalTo(artikelId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                dataList = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    DataList value = dataSnapshot1.getValue(DataList.class);
                    DataList data = new DataList();
                    String rfid = value.getRfid();
                    String kodeArtikel = value.getKodeArtikel();
                    String ukuran = value.getUkuran();
                    String lastUpdate = value.getLastUpdate();
                    String petugas = value.getPetugas();

                    data.setRfid(rfid);
                    data.setUkuran(ukuran);
                    data.setLastUpdate(lastUpdate);
                    data.setPetugas(petugas);
                    data.setKodeArtikel(kodeArtikel);
                    dataList.add(data);

                }


                GridLayoutManager mGridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                scanList.setLayoutManager(mGridLayoutManager);
                routineListAdapter = new ScanListAdapter(dataList, getApplicationContext(), item -> {
                    try {
                        WriteTagFragment newFragment = WriteTagFragment.newInstance(prepareList(item));
                        newFragment.show(getDialogFragment(), "dialog_fragment_teacher_detail");
                    } catch (Exception e) {
                        Utils.ToastMessage(getApplicationContext(), "dialog teacher detail error");
                    }
                });
                scanList.setAdapter(routineListAdapter);
                searchView.setQueryHint("search product ...");
                cardInfo.setVisibility(View.VISIBLE);
                txtProd.setText(String.valueOf(dataList.size()));
                mypDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mypDialog.dismiss();
                Log.w("Hello", "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void displayData(){
        mypDialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference("data-product-registered");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                dataList = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    DataList value = dataSnapshot1.getValue(DataList.class);
                    DataList data = new DataList();
                    String rfid = value.getRfid();
                    String kodeArtikel = value.getKodeArtikel();
                    String ukuran = value.getUkuran();
                    String lastUpdate = value.getLastUpdate();
                    String petugas = value.getPetugas();

                    data.setRfid(rfid);
                    data.setUkuran(ukuran);
                    data.setLastUpdate(lastUpdate);
                    data.setPetugas(petugas);
                    data.setKodeArtikel(kodeArtikel);
                    dataList.add(data);

                }


                GridLayoutManager mGridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                scanList.setLayoutManager(mGridLayoutManager);
                routineListAdapter = new ScanListAdapter(dataList, getApplicationContext(), item -> {
                    try {
                        WriteTagFragment newFragment = WriteTagFragment.newInstance(prepareList(item));
                        newFragment.show(getDialogFragment(), "dialog_fragment_teacher_detail");
                    } catch (Exception e) {
                        Utils.ToastMessage(getApplicationContext(), "dialog teacher detail error");
                    }
                });
                scanList.setAdapter(routineListAdapter);
                searchView.setQueryHint("search product ...");
                cardInfo.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                txtProd.setText(String.valueOf(dataList.size()));
                mypDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mypDialog.dismiss();
                Log.w("Hello", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public ArrayList<String> prepareList(DataList data) {
        ArrayList<String> value = new ArrayList<>();
        value.add(data.getRfid());
        value.add(data.getKodeArtikel());
        value.add(data.getUkuran());
        value.add(data.getLastUpdate());
        value.add(data.getPetugas());

        return value;
    }

}
