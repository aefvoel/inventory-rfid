package id.toriq.project.ui.scanActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rscja.deviceapi.RFIDWithUHF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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


public class ScanActivity extends AppCompatActivity {

    private boolean loopFlag = false;
    private int inventoryFlag = 1;
    Handler handler;
    private ArrayList<HashMap<String, String>> tagList;
    SimpleAdapter adapter;

    @BindView(R.id.RgInventory)
    RadioGroup RgInventory;

    @BindView(R.id.RbInventorySingle)
    RadioButton RbInventorySingle;

    @BindView(R.id.RbInventoryLoop)
    RadioButton RbInventoryLoop;

    @BindView(R.id.scan_list)
    RecyclerView scanList;

    @BindView(R.id.btnScan)
    ImageView BtInventory;

    @BindView(R.id.tap_rfid)
    TextView tapScan;

    @BindView(R.id.txt_date)
    TextView txtDate;

    @BindView(R.id.txt_product)
    TextView txtProduct;

    @BindView(R.id.card_info)
    CardView cardInfo;

    @BindView(R.id.card_scan)
    CardView cardScan;

    @BindView(R.id.etArtikel)
    EditText etArtikel;
    @BindView(R.id.etUkuran)
    EditText etUkuran;
    @BindView(R.id.spinner_ukuran)
    MaterialSpinner spinnerUkuran;

    private String[] size = {"", "AS", "S", "M", "L", "XL", "XXL"};

    private HashMap<String, String> map;
    private DatabaseReference mDatabase;
    private SharedPreferences sharedPreferences;
    private RFIDWithUHF mReader;
    private List<DataList> dataList;
    private ScanListAdapter routineListAdapter;
    private ProgressDialog mypDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        tagList = new ArrayList<>();
        dataList = new ArrayList<>();

        mypDialog = new ProgressDialog(ScanActivity.this);
        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mypDialog.setMessage("Scanning");
        mypDialog.setCanceledOnTouchOutside(false);

        spinnerUkuran.setItems(size);
        spinnerUkuran.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view12, position, id, item) -> {
            etUkuran.setText(item);
            spinnerUkuran.setSelectedIndex(0);
        });
        etUkuran.setOnClickListener(v -> {
            spinnerUkuran.expand();
        });

        adapter = new SimpleAdapter(this, tagList, R.layout.listtag_items,
                new String[]{"tagUii", "tagLen", "tagCount", "tagRssi"},
                new int[]{R.id.TvTagUii, R.id.TvTagLen, R.id.TvTagCount,
                        R.id.TvTagRssi});

        RgInventory.setOnCheckedChangeListener(new RgInventoryCheckedListener());
        BtInventory.setOnClickListener(new BtInventoryClickListener());
        cardInfo.setVisibility(View.INVISIBLE);
        cardScan.setVisibility(View.INVISIBLE);
        sharedPreferences = getSharedPreferences(Constant.SHAREDPREF_NAME, MODE_PRIVATE);

        initUHF();
        clearData();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.obj + "";
                String[] strs = result.split("@");
//                addEPCToList(strs[0], strs[1]);
                displayData(strs[0]);
            }
        };

    }

    @OnClick(R.id.btnSubmit)
    public void onSubmitInfo() {
        if(etArtikel.getText().toString().equals("") || etUkuran.getText().toString().equals("")){
            Utils.ToastMessage(getApplicationContext(), "Form tidak boleh kosong!");
        }else {
            for(int i=0;i<dataList.size();i++){
                DataList data = new DataList(dataList.get(i).getRfid(), etArtikel.getText().toString(),
                        etUkuran.getText().toString(), txtDate.getText().toString(), sharedPreferences.getString(Constant.NAMA, ""));
                mDatabase = FirebaseDatabase.getInstance().getReference("data").child(dataList.get(i).getRfid());
                mDatabase.setValue(data)
                        .addOnSuccessListener(aVoid -> {
                            // Write was successful!
                        })
                        .addOnFailureListener(e -> {
                            // Write failed
                            Utils.ToastMessage(getApplicationContext(), e.getMessage());
                        });
            }
            InfoList data = new InfoList(etArtikel.getText().toString(), txtDate.getText().toString(), sharedPreferences.getString(Constant.NAMA, ""), txtProduct.getText().toString());
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

    }

    @Override
    public void onPause() {
        Log.i("MY", "UHFReadTagFragment.onPause");
        super.onPause();
        stopInventory();
    }

    private void displayData(String epc) {


        mypDialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference("data");
        mDatabase.orderByKey().equalTo(epc).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DataList data = new DataList();
                if(!isExist(epc)){
                    if (dataSnapshot.exists()) {

                        //Key exists
                        DataList value = dataSnapshot.getChildren().iterator().next().getValue(DataList.class);

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
                        Log.w("stock", "data found." + warna);

                    } else {
                        //Key does not exist
                        data.setRfid(epc);
                        data.setJenisBaju("");
                        data.setUkuran("");
                        data.setWarna("");
                        data.setBrand("");
                        data.setProduction("");
                        data.setSupervisor1("");
                        data.setSupervisor2("");
                        data.setQc("");
                        data.setLastUpdate("");
                        data.setPetugas("");
                        data.setSku("");
                        data.setProductionDate("");
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
                }
                else {
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

    private void addEPCToList(String epc, String rssi) {
        if (!TextUtils.isEmpty(epc)) {
            int index = checkIsExist(epc);

            map = new HashMap<>();

            map.put("tagUii", epc);
            map.put("tagCount", String.valueOf(1));
            map.put("tagRssi", rssi);

            // mContext.getAppContext().uhfQueue.offer(epc + "\t 1");

            if (index == -1) {
                tagList.add(map);
            } else {
                int tagcount = Integer.parseInt(
                        tagList.get(index).get("tagCount"), 10) + 1;

                map.put("tagCount", String.valueOf(tagcount));

                tagList.set(index, map);

            }

            adapter.notifyDataSetChanged();

        }
    }

    public class BtClearClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            clearData();

        }
    }


    private void clearData() {

        tagList.clear();

        Log.i("MY", "tagList.size " + tagList.size());

        adapter.notifyDataSetChanged();
    }

    public class RgInventoryCheckedListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == RbInventorySingle.getId()) {

                inventoryFlag = 0;
            } else if (checkedId == RbInventoryLoop.getId()) {
                inventoryFlag = 1;
            }
        }
    }


    public class BtInventoryClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
//            dataList.clear();
            readTag();
//            String[] title = {
//                    "E200001A310102152300C29B",
//                    "E200001A310102152130C253",
//                    "E200001A310102152400C2CF",
//                    "E200001A310102152140C25B",
//                    "E200001A310102152240C28F"
//                    "E200001A310102152290C293",
//                    "E200001A310102152390C2C7",
//                    "E200001A310102152460C2DB",
//                    "E200001A310102152560C30F",
//                    "E200001A310102152570C303"
//            };
//            for (int i = 0; i < title.length; i++) {
//                displayData(title[i]);
//
//            }
        }
    }

    private void readTag() {
        if (tapScan.getText().equals("Tap to Scan")) {
            switch (inventoryFlag) {
                case 0: {
                    String strUII = mReader.inventorySingleTag();
                    if (!TextUtils.isEmpty(strUII)) {
                        String strEPC = mReader.convertUiiToEPC(strUII);
                        displayData(strEPC);
//                        addEPCToList(strEPC, "N/A");
                    } else {
                        Toast.makeText(this, "fail", Toast.LENGTH_LONG).show();
                    }
                }
                break;
                case 1: {
                    //  mContext.mReader.setEPCTIDMode(true);
                    if (mReader.startInventoryTag(0, 0)) {
                        tapScan.setText("Tap to Stop");
                        loopFlag = true;
                        setViewEnabled(false);
                        new TagThread().start();
                    } else {
                        mReader.stopInventory();
                        Toast.makeText(this, "fail", Toast.LENGTH_LONG).show();
                    }
                }
                break;
                default:
                    break;
            }
        } else {
            stopInventory();
        }
    }

    private void setViewEnabled(boolean enabled) {
        RbInventorySingle.setEnabled(enabled);
        RbInventoryLoop.setEnabled(enabled);
    }

    private void stopInventory() {
        if (loopFlag) {
            loopFlag = false;
            setViewEnabled(true);
            if (mReader.stopInventory()) {
                tapScan.setText("Tap to Scan");
            } else {
                Toast.makeText(this, "fail", Toast.LENGTH_LONG).show();
            }
        }
    }


    public int checkIsExist(String strEPC) {
        int existFlag = -1;
        if (Utils.isEmpty(strEPC)) {
            return existFlag;
        }
        String tempStr = "";
        for (int i = 0; i < tagList.size(); i++) {
            HashMap<String, String> temp;
            temp = tagList.get(i);
            tempStr = temp.get("tagUii");
            if (strEPC.equals(tempStr)) {
                existFlag = i;
                break;
            }
        }
        return existFlag;
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
                Toast.makeText(ScanActivity.this, "init fail",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(ScanActivity.this);
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

}
