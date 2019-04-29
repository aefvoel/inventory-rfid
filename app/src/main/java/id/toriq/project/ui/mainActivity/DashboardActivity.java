package id.toriq.project.ui.mainActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import id.toriq.project.R;
import id.toriq.project.helper.Constant;
import id.toriq.project.ui.historyActivity.HistoryActivity;
import id.toriq.project.ui.productActivity.ProductActivity;
import id.toriq.project.ui.scanActivity.ScanActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardActivity extends AppCompatActivity {

    private static SharedPreferences sharedpref;
    @BindView(R.id.intersection)
    ConstraintLayout constraintLayout;
    @BindView(R.id.txt_user)
    TextView txtUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        sharedpref = getSharedPreferences(Constant.SHAREDPREF_NAME, MODE_PRIVATE);
        txtUser.setText(sharedpref.getString(Constant.NAMA, ""));
        constraintLayout.setLayoutParams(new ConstraintLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().heightPixels / 3));
    }

    @OnClick({R.id.btnQr, R.id.btnInfo, R.id.btnHistory, R.id.logout})
    public void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnQr:
                Intent in = new Intent(this, ScanActivity.class);
                startActivity(in);
                break;
            case R.id.btnInfo:
                Intent i = new Intent(this, ProductActivity.class);
                startActivity(i);
                break;
            case R.id.btnHistory:
                Intent inn = new Intent(this, HistoryActivity.class);
                startActivity(inn);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                SharedPreferences.Editor editor = sharedpref.edit();
                editor.clear();
                editor.apply();
                finish();
                break;
        }


    }

}
