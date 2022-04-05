package com.example.myapplication2.viewholder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication2.R;
import com.example.myapplication2.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.ViewHolder> {
    Context context;
    ArrayList<ProfileViewModel> modules;
    public ProfileRecyclerAdapter(Context context, ArrayList<ProfileViewModel> modules) {
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
        Picasso.get().load(modules.get(position).imagePath).resize(70, 70).centerCrop().transform(new Utils.CircleTransform()).into(holder.imgName);
        holder.subjectName.setText(modules.get(position).subject);
        holder.moduleName.setText(modules.get(position).module);
        Log.i("RecyclerView", "Module loaded");
    }

    @Override
    public int getItemCount() {
        return modules.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgName;
        TextView subjectName;
        TextView moduleName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.Subject);
            moduleName = itemView.findViewById(R.id.moduleName);
            imgName = itemView.findViewById(R.id.imageTop);
        }
    }
}