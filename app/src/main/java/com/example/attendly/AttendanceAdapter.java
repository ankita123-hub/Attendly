package com.example.attendly;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.VH> {

    private List<Student> students = new ArrayList<>();
    private final Set<Integer> absentIds = new HashSet<>();

    /** Replaces the list. absent = ids of students who start as Absent. */
    public void setData(List<Student> newList, Set<Integer> absent) {
        this.students = newList;
        this.absentIds.clear();
        this.absentIds.addAll(absent);
        notifyDataSetChanged();
    }

    public List<Student> getStudents() { return students; }

    public Set<Integer> getAbsentIds() { return absentIds; }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Student s = students.get(position);
        holder.tvName.setText(s.getName());
        holder.tvRoll.setText("Roll No: " + s.getRollNo());

        // remove old listener first so recycled rows don't trigger it
        holder.rgStatus.setOnCheckedChangeListener(null);
        holder.rgStatus.check(absentIds.contains(s.getId())
                ? R.id.rbAbsent : R.id.rbPresent);

        holder.rgStatus.setOnCheckedChangeListener((group, checkedId) -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            int id = students.get(pos).getId();
            if (checkedId == R.id.rbAbsent) {
                absentIds.add(id);
            } else {
                absentIds.remove(id);
            }
        });
    }

    @Override
    public int getItemCount() { return students.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvRoll;
        RadioGroup rgStatus;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvRoll = itemView.findViewById(R.id.tvRoll);
            rgStatus = itemView.findViewById(R.id.rgStatus);
        }
    }
}