package com.smis.utilities;

import android.app.LauncherActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smis.R;

import java.util.List;

public class ClassroomAdapter extends RecyclerView.Adapter<ClassroomAdapter.ClassroomViewHolder> {
    private List<LauncherActivity.ListItem> listItems;
    private Context context;

    public ClassroomAdapter (List<LauncherActivity.ListItem> listItems, Context context){
        this.listItems = listItems;
        this.context = context;
    }

    public ClassroomAdapter() {

    }

    @NonNull
    @Override
    public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_classroom_listitem, parent, false);
        return new ClassroomViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull ClassroomViewHolder holder, int position) {
        final LauncherActivity.ListItem listItem = listItems.get(position);
        User user = new User();
        holder.creatorID.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ClassroomViewHolder extends RecyclerView.ViewHolder {
        TextView fileName, creatorID;
        ImageView thumbnail, download, delete;

        public ClassroomViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name_txt);
            creatorID = itemView.findViewById(R.id.creator_id);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            download = itemView.findViewById(R.id.download);
            delete = itemView.findViewById(R.id.delete);
        }

        public void bind() {
            User user = new User();
            creatorID.setText(user.getName());


        }
    }
}
