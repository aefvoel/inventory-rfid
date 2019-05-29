package id.toriq.project.ui.scanActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.button.MaterialButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rscja.deviceapi.RFIDWithUHF;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.toriq.project.R;
import id.toriq.project.adapter.ScanListAdapter;
import id.toriq.project.helper.Constant;
import id.toriq.project.helper.Utils;
import id.toriq.project.model.DataList;
import id.toriq.project.model.InfoList;


public class ScanAllActivity extends KeyDown {

    private boolean loopFlag = false;
    Handler handler;


    @BindView(R.id.scan_list)
    RecyclerView scanList;

    @BindView(R.id.btnScan)
    MaterialButton btScan;
    @BindView(R.id.btnStop)
    MaterialButton btStop;

    @BindView(R.id.txt_date)
    TextView txtDate;

    @BindView(R.id.txt_product)
    TextView txtProduct;

    @BindView(R.id.card_info)
    CardView cardInfo;

    @BindView(R.id.card_scan)
    CardView cardScan;

    private DatabaseReference mDatabase;
    private SharedPreferences sharedPreferences;
    private RFIDWithUHF mReader;
    private List<DataList> dataList;
    private ScanListAdapter routineListAdapter;
    private ProgressDialog mypDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_all);
        ButterKnife.bind(this);
        dataList = new ArrayList<>();

        mypDialog = new ProgressDialog(ScanAllActivity.this);
        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mypDialog.setMessage("Scanning");
        mypDialog.setCanceledOnTouchOutside(false);

        cardInfo.setVisibility(View.INVISIBLE);
        cardScan.setVisibility(View.INVISIBLE);
        sharedPreferences = getSharedPreferences(Constant.SHAREDPREF_NAME, MODE_PRIVATE);

        initUHF();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.obj + "";
                String[] strs = result.split("@");
                displayData(strs[0]);
            }
        };

    }

    @OnClick(R.id.btnScan)
    public void onBtnScan() {
        readTag();

//        String[] title = {
//                "E200001A310102152300C29B",
//                "E200001A310102152130C253",
//                "E200001A310102152400C2CF",
//                "E200001A310102152140C25B",
//                "E200001A310102152240C28F",
//                "E200001A310102152290C293",
//                "E200001A310102152390C2C7",
//                "E200001A310102152460C2DB",
//                "E200001A310102152560C30F",
//                "E200001A310102152570C303",
//                "E28011700000020A88114AE6",
//                "E28011700000020A88114BE4",
//                "E28011700000020A881142F9"
//        };
//        for (int i = 0; i < title.length; i++) {
//            displayData(title[i]);
//
//        }
    }

    @OnClick(R.id.btnStop)
    public void onBtnStop() {
        stopInventory();
    }

    @OnClick(R.id.btnClear)
    public void onClear() {
        dataList.clear();
        routineListAdapter.notifyDataSetChanged();
        cardInfo.setVisibility(View.INVISIBLE);
        cardScan.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.btnSubmit)
    public void onSubmitInfo() {
        for (int i = 0; i < dataList.size(); i++) {
            DataList data = new DataList(dataList.get(i).getRfid(), dataList.get(i).getKodeArtikel(),
                    dataList.get(i).getUkuran(), txtDate.getText().toString(), sharedPreferences.getString(Constant.NAMA, ""));
            mDatabase = FirebaseDatabase.getInstance().getReference("data-product-scanned-all").child(dataList.get(i).getRfid());
            mDatabase.setValue(data)
                    .addOnSuccessListener(aVoid -> {
                        // Write was successful!
                    })
                    .addOnFailureListener(e -> {
                        // Write failed
                        Utils.ToastMessage(getApplicationContext(), e.getMessage());
                    });
        }
        InfoList data = new InfoList(txtDate.getText().toString(), sharedPreferences.getString(Constant.NAMA, ""), txtProduct.getText().toString());
        mDatabase = FirebaseDatabase.getInstance().getReference("history");
        mDatabase.push().setValue(data)
                .addOnSuccessListener(aVoid -> {
                    // Write was successful!
                    Utils.ToastMessage(getApplicationContext(), "Data Stored Successfully");
                })
                .addOnFailureListener(e -> {
                    // Write failed
                    Utils.ToastMessage(getApplicationContext(), e.getMessage());
                });

    }

    @Override
    public void onPause() {
        Log.i("MY", "UHFReadTagFragment.onPause");
        super.onPause();
        stopInventory();
    }

    private void displayData(String epc) {


        mypDialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference("data-product-registered");
        mDatabase.orderByKey().equalTo(epc).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DataList data = new DataList();
                if (!isExist(epc)) {
                    if (dataSnapshot.exists()) {

                        //Key exists
                        DataList value = dataSnapshot.getChildren().iterator().next().getValue(DataList.class);

                        String rfid = value.getRfid();
                        String kodeArtikel = value.getKodeArtikel();
                        String ukuran = value.getUkuran();
                        String lastUpdate = value.getLastUpdate();
                        String petugas = value.getPetugas();


                        data.setRfid(rfid);
                        data.setKodeArtikel(kodeArtikel);
                        data.setUkuran(ukuran);
                        data.setLastUpdate(lastUpdate);
                        data.setPetugas(petugas);
                        dataList.add(data);

                    }else {
                        //Key does not exist
                        data.setRfid(epc);
                        data.setKodeArtikel("");
                        data.setUkuran("");
                        data.setLastUpdate("");
                        data.setPetugas("");
                        dataList.add(data);
                        Log.w("stock", "data not found.");
                    }
                    GridLayoutManager mGridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                    scanList.setLayoutManager(mGridLayoutManager);
                    Set<DataList> hashSet = new LinkedHashSet<>(dataList);
                    dataList.clear();
                    dataList.addAll(hashSet);
                    routineListAdapter = new ScanListAdapter(dataList, getApplicationContext(), item -> {
                        try {
                            WriteTagFragment newFragment = WriteTagFragment.newInstance(prepareList(item));
                            newFragment.show(getDialogFragment(), "dialog_fragment_teacher_detail");
                        } catch (Exception e) {
                            Utils.ToastMessage(getApplicationContext(), "dialog teacher detail error");
                        }
                    });

                    mypDialog.dismiss();
                    scanList.setAdapter(routineListAdapter);
                    cardScan.setVisibility(View.VISIBLE);
                    cardInfo.setVisibility(View.VISIBLE);
                    txtDate.setText(Utils.getCurrentTimeStamp());
                    txtProduct.setText(String.valueOf(dataList.size()));
                } else {
                    mypDialog.dismiss();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mypDialog.dismiss();
                Log.w("Hello", "Failed to read value.", databaseError.toException());
            }
        });

    }

    public boolean isExist(String rfid) {

        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getRfid().equals(rfid)) {
                return true;
            }
        }

        return false;
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


    private void readTag() {
        if (mReader.startInventoryTag(0, 0)) {
            loopFlag = true;
            btScan.setVisibility(View.INVISIBLE);
            btStop.setVisibility(View.VISIBLE);
            new TagThread().start();
        } else {
            mReader.stopInventory();
            Toast.makeText(this, "fail", Toast.LENGTH_LONG).show();
        }
    }

    private void stopInventory() {
        if(loopFlag){
            loopFlag = false;
            btScan.setVisibility(View.VISIBLE);
            btStop.setVisibility(View.INVISIBLE);
            mReader.stopInventory();
        }
    }

    class TagThread extends Thread {
        public void run() {
            String strTid;
            String strResult;
            String[] res = null;
            while (loopFlag) {
                res = mReader.readTagFromBuffer();
                if (res != null) {
                    strTid = res[0];
                    if (strTid.length() != 0 && !strTid.equals("0000000" +
                            "000000000") && !strTid.equals("000000000000000000000000")) {
                        strResult = "TID:" + strTid + "\n";
                    } else {
                        strResult = "";
                    }
                    Log.i("data", "EPC:" + res[1] + "|" + strResult);
                    Message msg = handler.obtainMessage();
                    msg.obj = mReader.convertUiiToEPC(res[1]) + "@" + res[2];

                    handler.sendMessage(msg);
                }
            }
        }
    }

    public void initUHF() {

        try {
            mReader = RFIDWithUHF.getInstance();
        } catch (Exception ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();


            return;
        }

        if (mReader != null) {
            new InitTask().execute();
        }
    }


    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            return mReader.init();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            mypDialog.cancel();

            if (!result) {
                Toast.makeText(ScanAllActivity.this, "init fail",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(ScanAllActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }
    }

    public FragmentTransaction getDialogFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);

        return ft;
    }

    @Override
    protected void onDestroy() {

        if (mReader != null) {
            mReader.free();
        }
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void myOnKeyDown() {
        readTag();
    }
}
