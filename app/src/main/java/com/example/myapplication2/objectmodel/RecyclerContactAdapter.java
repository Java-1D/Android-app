package com.example.myapplication2.objectmodel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication2.R;
import com.google.android.gms.cast.framework.media.MediaQueueRecyclerViewAdapter;

import java.util.ArrayList;

public class RecyclerContactAdapter extends RecyclerView.Adapter<RecyclerContactAdapter.ViewHolder> {
    Context context;
    ArrayList<RecyclerViewModel> modules;
    public RecyclerContactAdapter(Context context, ArrayList<RecyclerViewModel> modules) {
        this.context = context;
        this.modules = modules;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_profile_page, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imgName.setImageResource(modules.get(position).img);
        holder.subjectName.setText(modules.get(position).subject);
        holder.moduleName.setText(modules.get(position).module);
    }

    @Override
    public int getItemCount() {
        return modules.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgName;
        TextView subjectName, moduleName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.Subject);
            moduleName = itemView.findViewById(R.id.moduleName);
            imgName = itemView.findViewById(R.id.imageTop);
        }
    }
}