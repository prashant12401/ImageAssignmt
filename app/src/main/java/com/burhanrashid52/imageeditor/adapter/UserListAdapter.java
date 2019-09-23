package com.burhanrashid52.imageeditor.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.burhanrashid52.imageeditor.R;
import com.burhanrashid52.imageeditor.model.User;

import java.io.File;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder>{
   private Context context;
   public List<User> userList;

    public UserListAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_userlist, parent, false);

        UserListViewHolder userListViewHolder = new UserListViewHolder(view);
        return userListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int i) {
        holder.tvUserName.setText(userList.get(i).getFirstName());
        holder.tvUserAddress.setText(userList.get(i).getAddress());
        File image = new File(userList.get(i).getFilePath());
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
        holder.ivUserImage.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserAddress,tvUserName;
        private ImageView ivUserImage;

        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserAddress=itemView.findViewById(R.id.tvUserAddress);
            tvUserName=itemView.findViewById(R.id.tvUserName);
            ivUserImage=itemView.findViewById(R.id.ivUserImage);
        }
    }
}
