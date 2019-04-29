package id.toriq.project.ui.historyActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.toriq.project.R;
import id.toriq.project.adapter.HistoryListAdapter;;
import id.toriq.project.model.InfoList;

public class HistoryActivity extends AppCompatActivity
{
    private DatabaseReference mDatabase;

    @BindView(R.id.history_list)
    RecyclerView scanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        displayData();
    }

    private void displayData(){
        mDatabase = FirebaseDatabase.getInstance().getReference("history");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                List<InfoList> dataList = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    InfoList value = dataSnapshot1.getValue(InfoList.class);
                    InfoList data = new InfoList();
                    String product = value.getProducts();
                    String lastUpdate = value.getLastUpdate();
                    String petugas = value.getPetugas();

                    data.setProducts(product);
                    data.setLastUpdate(lastUpdate);
                    data.setPetugas(petugas);
                    dataList.add(data);

                }


                GridLayoutManager mGridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                scanList.setLayoutManager(mGridLayoutManager);
                HistoryListAdapter routineListAdapter = new HistoryListAdapter(dataList, getApplicationContext());
                scanList.setAdapter(routineListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
// Failed to read value
                Log.w("Hello", "Failed to read value.", databaseError.toException());
            }
        });
    }
}
