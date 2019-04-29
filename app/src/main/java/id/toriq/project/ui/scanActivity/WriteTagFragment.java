package id.toriq.project.ui.scanActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rscja.deviceapi.RFIDWithUHF;
import com.rscja.deviceapi.entity.SimpleRFIDEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.toriq.project.R;
import id.toriq.project.adapter.ScanListAdapter;
import id.toriq.project.helper.Constant;
import id.toriq.project.helper.Utils;
import id.toriq.project.model.DataList;
import id.toriq.project.ui.productActivity.ProductActivity;

import static android.content.Context.MODE_PRIVATE;

public class WriteTagFragment extends DialogFragment {

    @BindView(R.id.close_btn)
    ImageView closeBtn;
    @BindView(R.id.etJenisBaju)
    EditText etJenisBaju;
    @BindView(R.id.etBrand)
    EditText etBrand;
    @BindView(R.id.tap_rfid)
    TextView etRfid;
    @BindView(R.id.etUkuran)
    EditText etUkuran;
    @BindView(R.id.etProduction)
    EditText etProduction;
    @BindView(R.id.etQc)
    EditText etQc;
    @BindView(R.id.etWarna)
    EditText etWarna;
    @BindView(R.id.etSup1)
    EditText etSup1;
    @BindView(R.id.etSup2)
    EditText etSup2;
    @BindView(R.id.etSku)
    EditText etSku;
    @BindView(R.id.etProdDate)
    EditText etProDate;
    @BindView(R.id.spinner_ukuran)
    MaterialSpinner spinnerUkuran;
    @BindView(R.id.spinner_warna)
    MaterialSpinner spinnerWarna;

    private DatabaseReference databaseReference;
    private static SharedPreferences sharedPreferences;
    public RFIDWithUHF mReader;
    private Calendar myCalendar;
    private String[] size = {"", "S", "M", "L", "XL", "XXL"};
    private String[] warna = {"", "Merah", "Kuning", "Hijau", "Abu", "Biru"};

    public static WriteTagFragment newInstance(ArrayList<String> value) {
        WriteTagFragment fragment = new WriteTagFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constant.ARG_VALUE, value);
        fragment.setArguments(bundle);

        return fragment;
    }

    public WriteTagFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MY", "WriteTagFragment.onCreateView");
        View view = inflater
                .inflate(R.layout.dialog_product, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                //do your stuff
                dismiss();
            }
        };
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        sharedPreferences = getContext().getSharedPreferences(Constant.SHAREDPREF_NAME, MODE_PRIVATE);
        setStyle(DialogFragment.STYLE_NORMAL, 0);
        setCancelable(false);
        closeBtn.setOnClickListener(onClickListenerDialogClose);
        initUHF();
        spinnerUkuran.setItems(size);
        spinnerUkuran.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view12, position, id, item) -> {
            etUkuran.setText(item);
            spinnerUkuran.setSelectedIndex(0);
        });
        etUkuran.setOnClickListener(v -> {
            spinnerUkuran.expand();
        });

        spinnerWarna.setItems(warna);
        spinnerWarna.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view12, position, id, item) -> {
            etWarna.setText(item);
            spinnerWarna.setSelectedIndex(0);
        });
        etWarna.setOnClickListener(v -> {
            spinnerWarna.expand();
        });

        myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };
        etProDate.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            new DatePickerDialog(getContext(), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        if (getArguments() != null) {
            etRfid.setText(getArguments().getStringArrayList(Constant.ARG_VALUE).get(0));
            etJenisBaju.setText(getArguments().getStringArrayList(Constant.ARG_VALUE).get(1));
            etUkuran.setText(getArguments().getStringArrayList(Constant.ARG_VALUE).get(2));
            etWarna.setText(getArguments().getStringArrayList(Constant.ARG_VALUE).get(3));
            etBrand.setText(getArguments().getStringArrayList(Constant.ARG_VALUE).get(4));
            etProduction.setText(getArguments().getStringArrayList(Constant.ARG_VALUE).get(5));
            etSup1.setText(getArguments().getStringArrayList(Constant.ARG_VALUE).get(6));
            etSup2.setText(getArguments().getStringArrayList(Constant.ARG_VALUE).get(7));
            etQc.setText(getArguments().getStringArrayList(Constant.ARG_VALUE).get(8));
            etSku.setText(getArguments().getStringArrayList(Constant.ARG_VALUE).get(9));
            etProDate.setText(getArguments().getStringArrayList(Constant.ARG_VALUE).get(10));


        }


    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        etProDate.setText(sdf.format(myCalendar.getTime()));
    }

    private final View.OnClickListener onClickListenerDialogClose = view -> dismiss();

    @OnClick(R.id.btnScan)
    public void onScan() {
        readRFID();
    }

    @OnClick(R.id.btnSubmit)
    public void onSubmit() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            writeToDB(etRfid.getText().toString(),
                    etJenisBaju.getText().toString(),
                    etUkuran.getText().toString(),
                    etWarna.getText().toString(),
                    etBrand.getText().toString(),
                    etProduction.getText().toString(),
                    etSup1.getText().toString(),
                    etSup2.getText().toString(),
                    etQc.getText().toString(),
                    etSku.getText().toString(),
                    etProDate.getText().toString(),
                    Utils.getCurrentTimeStamp(),
                    sharedPreferences.getString(Constant.NAMA, ""));
        } else {
            // No user is signed in
            Utils.ToastMessage(getContext(), "Auth failed");
        }

    }

    private void writeToDB(String rfid, String jenisBaju, String ukuran, String warna, String brand,
                           String production, String supervisor1, String supervisor2, String qc, String sku, String productionDate,
                           String lastUpdate, String petugas) {
        DataList dataList = new DataList(rfid, jenisBaju, ukuran, warna, brand, production,
                supervisor1, supervisor2, qc, sku, productionDate, lastUpdate, petugas);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("data").child(rfid).setValue(dataList)
                .addOnSuccessListener(aVoid -> {
                    // Write was successful!
                    Utils.ToastMessage(getContext(), "Data Stored Successfully");
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    // Write failed
                    Utils.ToastMessage(getContext(), e.getMessage());
                });

    }

    private void readRFID() {

//        String ptrStr = "0";
//        String cntStr = "4";
//        String Bank = "UII";
//        String pwdStr = "00000000";
//
//        SimpleRFIDEntity entity;
//        entity = mReader.readData(pwdStr,
//                RFIDWithUHF.BankEnum.valueOf(Bank),
//                Integer.parseInt(ptrStr),
//                Integer.parseInt(cntStr));
        String strUII = mReader.inventorySingleTag();

        if (!TextUtils.isEmpty(strUII)) {
            String strEPC = mReader.convertUiiToEPC(strUII);
            etRfid.setText(strEPC);
            displayData(strEPC);
        } else {
            Utils.ToastMessage(getContext(), "fail");
        }
    }

    private void displayData(String epc) {
        databaseReference = FirebaseDatabase.getInstance().getReference("data");
        databaseReference.orderByKey().equalTo(epc).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

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


                    etRfid.setText(rfid);
                    etJenisBaju.setText(jenisBaju);
                    etUkuran.setText(ukuran);
                    etWarna.setText(warna);
                    etBrand.setText(brand);
                    etProduction.setText(production);
                    etSup1.setText(supervisor1);
                    etSup2.setText(supervisor2);
                    etQc.setText(qc);
                    etSku.setText(sku);
                    etProDate.setText(proDate);
                    Log.w("stock", "data found." + warna);

                } else {
                    //Key does not exist

                    Log.w("stock", "data not found.");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Hello", "Failed to read value.", databaseError.toException());
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    public void initUHF() {

        try {
            mReader = RFIDWithUHF.getInstance();
        } catch (Exception ex) {

            Utils.ToastMessage(getContext(), ex.getMessage());


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
                Utils.ToastMessage(getContext(), "init fail");
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(getContext());
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }
    }

    @Override
    public void onDestroy() {

        if (mReader != null) {
            mReader.free();
        }
        super.onDestroy();
    }


}
