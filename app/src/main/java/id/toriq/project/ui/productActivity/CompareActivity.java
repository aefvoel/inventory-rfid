package id.toriq.project.ui.productActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rscja.deviceapi.RFIDWithUHF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.toriq.project.R;
import id.toriq.project.adapter.CompareListAdapter;
import id.toriq.project.adapter.ScanListAdapter;
import id.toriq.project.helper.Utils;
import id.toriq.project.model.CompareResultList;
import id.toriq.project.model.DataList;
import id.toriq.project.ui.scanActivity.WriteTagFragment;

public class CompareActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private List<DataList> dataList;
    private CompareListAdapter compareListAdapter;
    private List<DataList> scannedList;
    private List<CompareResultList> compareList;
    private ProgressDialog mypDialog;
    private ScanListAdapter scanListAdapter;

    @BindView(R.id.search_view)
    SearchView searchView;

    @BindView(R.id.product_list)
    RecyclerView scanList;

    @BindView(R.id.card)
    CardView cardInfo;
    @BindView(R.id.txt_prod)
    TextView txtProd;
    @BindView(R.id.txt_sisa)
    TextView txtSisa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        ButterKnife.bind(this);
        mypDialog = new ProgressDialog(CompareActivity.this);
        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mypDialog.setMessage("Analyzing Data");
        cardInfo.setVisibility(View.INVISIBLE);
        mypDialog.setCanceledOnTouchOutside(false);
        displayDataScanned();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    compareListAdapter.filter(query);
                } catch (NullPointerException e) {
                    Utils.ToastMessage(getApplicationContext(), "data not found");
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    compareListAdapter.filter(newText);
                } catch (NullPointerException e) {
                    Utils.ToastMessage(getApplicationContext(), "data not found");
                }
                return true;
            }
        });
    }

    /************************
     * FragmentTransaction for Dialog
     * **********************/
    public FragmentTransaction getDialogFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);

        return ft;
    }

    private void displayData() {
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
                    if (!isExist(rfid)) {
                        data.setRfid(rfid);
                        data.setUkuran(ukuran);
                        data.setLastUpdate(lastUpdate);
                        data.setPetugas(petugas);
                        data.setKodeArtikel(kodeArtikel);
                        dataList.add(data);
                    }
                }

                compareList = new ArrayList<>();
                Map<String, Integer> map = new HashMap<>();

                for (DataList temp : dataList) {
                    Integer count = map.get(temp.getKodeArtikel());
                    map.put(temp.getKodeArtikel(), (count == null) ? 1 : count + 1);
                }

                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    System.out.println("Key : " + entry.getKey() + " Value : "
                            + entry.getValue());
                    CompareResultList compareResultList = new CompareResultList();
                    compareResultList.setArtikelId(entry.getKey());
                    compareResultList.setJumlah(String.valueOf(entry.getValue()));
                    compareList.add(compareResultList);
                }
                Collections.sort(compareList, (p1, p2) -> {
                    return p2.getJumlah().compareTo(p1.getJumlah()); // if you want to short by name
                });
                GridLayoutManager mGridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                scanList.setLayoutManager(mGridLayoutManager);
                compareListAdapter = new CompareListAdapter(compareList, getApplicationContext(), item -> {
                    Intent intent = new Intent(CompareActivity.this, ProductActivity.class);
                    intent.putExtra("artikelId", item.getArtikelId());
                    startActivity(intent);
                });
                scanList.setAdapter(compareListAdapter);
                searchView.setQueryHint("search product ...");
                cardInfo.setVisibility(View.VISIBLE);
                txtProd.setText(String.valueOf(dataList.size()));
                txtSisa.setText(String.valueOf(scannedList.size()));
                mypDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mypDialog.dismiss();
                Log.w("Hello", "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void displayDataScanned() {
        mypDialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference("data-product-scanned");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                scannedList = new ArrayList<>();

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
                    scannedList.add(data);

                }
                displayData();
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

    public boolean isExist(String rfid) {

        for (int i = 0; i < scannedList.size(); i++) {
            if (scannedList.get(i).getRfid().equals(rfid)) {
                return true;
            }
        }

        return false;
    }

}
