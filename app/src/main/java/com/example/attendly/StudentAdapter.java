package com.example.attendly;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.VH> {

    public interface OnDeleteClickListener {
        void onDeleteClick(Student student);
    }

    private List<Student> students = new ArrayList<>();
    private OnDeleteClickListener deleteListener;

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    public void setStudents(List<Student> newList) {
        this.students = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Student s = students.get(position);
        holder.tvName.setText(s.getName());
        holder.tvDetails.setText("Roll No: " + s.getRollNo() + "  •  Class: " + s.getClassName());

        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && deleteListener != null) {
                deleteListener.onDeleteClick(students.get(pos));
            }
        });
    }

    @Override
    public int getItemCount() { return students.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails;
        ImageView btnDelete;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}