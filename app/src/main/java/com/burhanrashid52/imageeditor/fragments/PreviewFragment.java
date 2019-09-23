package com.burhanrashid52.imageeditor.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.burhanrashid52.imageeditor.R;
import com.burhanrashid52.imageeditor.activities.OrderActivity;
import com.burhanrashid52.imageeditor.utils.Helper;
import com.edmodo.cropper.CropImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PreviewFragment extends BottomSheetDialogFragment  {
    ImageView ivPrevieImage;
    ImageView btnDownload;
    Button btnPlaceOrder;
    String filePath="";
    public PreviewFragment() {
        // Required empty public constructor
    }


   SaveImageListner saveImageListner;
    public  interface SaveImageListner
    {
        void onSaveImage();
    }
    public void setSeveImageListner(SaveImageListner seveImageListner) {
        saveImageListner = seveImageListner;
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
        View contentView = View.inflate(getContext(), R.layout.preview_fragment, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            //((BottomSheetBehavior) behavior).setPeekHeight(contentView.getMeasuredHeight());
        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        ivPrevieImage = (ImageView)contentView.findViewById(R.id.ivPrevieImage);
        if(null!=getArguments().getString("filepath"))
        {
            filePath=getArguments().getString("filepath");
            if(null!=filePath)
            {

                File image = new File(filePath);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
                ivPrevieImage.setImageBitmap(bitmap);


            }

        }
         /*   previewCapturedImage(getArguments().getString("filepath"));
        } else {
            previewPickedImage(getArguments().getString("filepath"));
        }*/
        btnDownload = (ImageView)contentView.findViewById(R.id.btnDownload);
        btnPlaceOrder = (Button)contentView.findViewById(R.id.btnPlaceOrder);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImageListner.onSaveImage();
                dismiss();
            }
        });

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_placeOrder=new Intent(getActivity(), OrderActivity.class);
                i_placeOrder.putExtra("filepath",filePath);
                getActivity().startActivity(i_placeOrder);
            }
        });


    }








}