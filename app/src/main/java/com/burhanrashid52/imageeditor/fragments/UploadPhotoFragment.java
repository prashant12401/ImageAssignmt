package com.burhanrashid52.imageeditor.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.burhanrashid52.imageeditor.R;
import com.burhanrashid52.imageeditor.utils.Helper;

import java.io.IOException;
import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.PhotoEditor;

import static android.app.Activity.RESULT_OK;

public class UploadPhotoFragment extends BottomSheetDialogFragment implements View.OnClickListener {


    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    private static final int CROP_IMAGE = 98;
    String imagePath;
    public UploadPhotoFragment() {
        // Required empty public constructor
    }


    private UploadPhotoListner uploadPhotoListner;



    public interface UploadPhotoListner {
        void onPhotoUpload(Bitmap bitmap,String imagePath,String loadBy);
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

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_upload_photo_dialog, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        TextView tvGallery = contentView.findViewById(R.id.tvGallery);
        TextView tvCamera = contentView.findViewById(R.id.tvCamera);

        tvGallery.setOnClickListener(this);
        tvCamera.setOnClickListener(this);


    }
    //on click listner
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.tvGallery:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
                break;
            case R.id.tvCamera:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;
        }
    }
    public void setOnPhotoUpload(UploadPhotoListner onPhotoUpload) {
        uploadPhotoListner = onPhotoUpload;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:

                    //mPhotoEditor.clearAllViews();
                    Uri uri2 = data.getData();
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imagePath= Helper.getPath(uri2, getActivity());

                    if (TextUtils.isEmpty(imagePath)) {
                        imagePath = Helper.getPath(getActivity(), uri2);
                    }
                    // Intent intentGallery = new Intent(getActivity(), ImageCropFragment.class);
                    if (imagePath != null && !imagePath.isEmpty()) {
                        // intentGallery.putExtra("filepath", imagePath);
                    } else {
                        imagePath = Helper.getImagePathFromGalleryAboveKitkat(getActivity(), uri2);
                        if (imagePath == null) {
                            // Helper.showToast(getString(R.string.select_image_from_device_folders), activityWeakReference.get());
                            return;
                        }
                        //intentGallery.putExtra("filepath", imagePath);
                    }

                    uploadPhotoListner.onPhotoUpload(photo,imagePath,"camera");
                    dismiss();
                    //mPhotoEditorView.getSource().setImageBitmap(photo);
                    break;
                case PICK_REQUEST:
                    try {
                      //  mPhotoEditor.clearAllViews();

                        Uri uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                        imagePath= Helper.getPath(uri, getActivity());

                        if (TextUtils.isEmpty(imagePath)) {
                            imagePath = Helper.getPath(getActivity(), uri);
                        }
                       // Intent intentGallery = new Intent(getActivity(), ImageCropFragment.class);
                        if (imagePath != null && !imagePath.isEmpty()) {
                           // intentGallery.putExtra("filepath", imagePath);
                        } else {
                            imagePath = Helper.getImagePathFromGalleryAboveKitkat(getActivity(), uri);
                            if (imagePath == null) {
                               // Helper.showToast(getString(R.string.select_image_from_device_folders), activityWeakReference.get());
                                return;
                            }
                            //intentGallery.putExtra("filepath", imagePath);
                        }

                        uploadPhotoListner.onPhotoUpload(bitmap,imagePath,"gallery");
                        dismiss();
                        //mPhotoEditorView.getSource().setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    }
}