package id.toriq.project.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.toriq.project.R;
import id.toriq.project.model.DataList;

public class ScanListAdapter extends RecyclerView.Adapter<ScanListAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(DataList item);
    }
    private final List<DataList> routineDataList;
    private final LayoutInflater mLayoutInflater;
    private final ScanListAdapter.OnItemClickListener listener;
    private final Context mContext;

    public ScanListAdapter(List<DataList> routineDataList, Context mContext, OnItemClickListener listener) {
        this.routineDataList = routineDataList;
        this.listener = listener;
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mLayoutInflater.inflate(R.layout.item_scan, viewGroup, false);
        return new ScanListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(routineDataList.get(i), listener);
    }

    @Override
    public int getItemCount() {
        return routineDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView subjectInit;
        final TextView subjectName;
        final TextView subjectTime;
        final TextView subject;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectInit = itemView.findViewById(R.id.subject_init);
            subject = itemView.findViewById(R.id.subject);
            subjectName = itemView.findViewById(R.id.subject_name);
            subjectTime = itemView.findViewById(R.id.subject_rfid);
        }
        public void bind(final DataList item, final OnItemClickListener listener) {

            subject.setText(item.getBrand());
            subjectName.setText(item.getJenisBaju());
            subjectTime.setText(item.getRfid());
            if(subject.getText().equals("")){
                subjectInit.setBackgroundColor(mContext.getResources().getColor(R.color.colorText));
            }
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
