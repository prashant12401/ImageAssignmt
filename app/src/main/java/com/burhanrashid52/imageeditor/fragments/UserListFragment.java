package com.burhanrashid52.imageeditor.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.burhanrashid52.imageeditor.R;
import com.burhanrashid52.imageeditor.activities.OrderActivity;
import com.burhanrashid52.imageeditor.adapter.UserListAdapter;
import com.burhanrashid52.imageeditor.model.User;
import com.burhanrashid52.imageeditor.utils.DatabaseClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends BottomSheetDialogFragment  {

    private RecyclerView rvUserList;
    private List<User> userList;
    private UserListAdapter userListAdapter;
     public UserListFragment() {
        // Required empty public constructor
    }



    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    /*@Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = View.inflate(getContext(), R.layout.activity_image_crop, null);

        return contentView;
    }*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.user_list_fragment, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            //((BottomSheetBehavior) behavior).setPeekHeight(contentView.getMeasuredHeight());
        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        rvUserList = (RecyclerView)contentView.findViewById(R.id.rvUserList);
        initArray();
        initializeRecyclerView();
        getTasks();



    }




    //initialize array
    private void initArray() {
        if(null==userList)
        {
            userList=new ArrayList<>();
        }
    }
    //Initialize recycler view
    private void initializeRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvUserList.setLayoutManager(linearLayoutManager);
        userListAdapter = new UserListAdapter(getActivity(), userList);
        rvUserList.setAdapter(userListAdapter);
    }
    //get saved info
    private void getTasks() {
        class GetTasks extends AsyncTask<Void, Void, List<User>> {

            @Override
            protected List<User> doInBackground(Void... voids) {
                List<User> taskList = DatabaseClient
                        .getInstance(getActivity())
                        .getAppDatabase()
                        .taskDao()
                        .getAll();
                return taskList;
            }

            @Override
            protected void onPostExecute(List<User> tasks) {
                super.onPostExecute(tasks);
              //  UserListAdapter adapter = new UserListAdapter(getActivity(), tasks);
                userListAdapter.userList=tasks;
                userListAdapter.notifyDataSetChanged();

            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

}