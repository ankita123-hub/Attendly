package com.example.attendly;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {

    private List<AttendanceRecord> records = new ArrayList<>();

    public void setRecords(List<AttendanceRecord> newList) {
        this.records = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        AttendanceRecord r = records.get(position);
        holder.tvName.setText(r.getStudentName());
        holder.tvRoll.setText("Roll No: " + r.getRollNo());
        if (r.isAbsent()) {
            holder.tvStatus.setText("Absent");
            holder.tvStatus.setTextColor(Color.parseColor("#D32F2F"));
        } else {
            holder.tvStatus.setText("Present");
            holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"));
        }
    }

    @Override
    public int getItemCount() { return records.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvRoll, tvStatus;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvRoll = itemView.findViewById(R.id.tvRoll);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}