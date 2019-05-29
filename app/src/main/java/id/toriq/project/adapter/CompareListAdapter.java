package id.toriq.project.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.toriq.project.R;
import id.toriq.project.model.CompareResultList;
import id.toriq.project.model.DataList;

public class CompareListAdapter extends RecyclerView.Adapter<CompareListAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(CompareResultList item);
    }
    private final List<CompareResultList> routineDataList;
    private final List<CompareResultList> mFilteredList;
    private final LayoutInflater mLayoutInflater;
    private final CompareListAdapter.OnItemClickListener listener;
    private final Context mContext;

    public CompareListAdapter(List<CompareResultList> routineDataList, Context mContext, OnItemClickListener listener) {
        this.routineDataList = routineDataList;
        this.listener = listener;
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mFilteredList = new ArrayList<>();
        mFilteredList.addAll(routineDataList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mLayoutInflater.inflate(R.layout.item_compare, viewGroup, false);
        return new CompareListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(routineDataList.get(i), listener);
    }
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        routineDataList.clear();
        if (charText.isEmpty()) {
            routineDataList.addAll(mFilteredList);
        } else {
            for (CompareResultList data : mFilteredList) {
                if (data.getArtikelId().toLowerCase().contains(charText) || data.getJumlah().toLowerCase().contains(charText)) {
                    routineDataList.add(data);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return routineDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView subjectSold;
        final TextView subject;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectSold = itemView.findViewById(R.id.subject_sold);
            subject = itemView.findViewById(R.id.subject);
        }
        public void bind(final CompareResultList item, final OnItemClickListener listener) {

            subjectSold.setText(item.getJumlah());
            subject.setText(item.getArtikelId());
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
