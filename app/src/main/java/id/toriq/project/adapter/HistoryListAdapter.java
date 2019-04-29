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
import id.toriq.project.model.InfoList;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {
    private final List<InfoList> routineDataList;
    private final LayoutInflater mLayoutInflater;

    public HistoryListAdapter(List<InfoList> routineDataList, Context mContext) {
        this.routineDataList = routineDataList;
        mLayoutInflater = LayoutInflater.from(mContext);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mLayoutInflater.inflate(R.layout.item_history, viewGroup, false);
        return new HistoryListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        InfoList routineData = routineDataList.get(i);
        viewHolder.subject.setText(routineData.getProducts());
        viewHolder.subjectName.setText(routineData.getPetugas());
        viewHolder.subjectTime.setText(routineData.getLastUpdate());
    }

    @Override
    public int getItemCount() {
        return routineDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView subjectName;
        final TextView subjectTime;
        final TextView subject;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTime = itemView.findViewById(R.id.txt_date);
            subject = itemView.findViewById(R.id.txt_product);
            subjectName = itemView.findViewById(R.id.txt_petugas);
        }
    }
}

