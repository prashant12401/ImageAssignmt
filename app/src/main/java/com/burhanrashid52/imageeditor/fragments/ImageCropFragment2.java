package com.burhanrashid52.imageeditor.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.ImageView;

import com.burhanrashid52.imageeditor.R;
import com.burhanrashid52.imageeditor.utils.Helper;
import com.edmodo.cropper.CropImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCropFragment2 extends BottomSheetDialogFragment implements View.OnClickListener {
    CropImageView civ;
    ImageView btnSave, btnDelete, btnRotate;
    public ImageCropFragment2() {
        // Required empty public constructor
    }


   CropImageListner mCropImageListner;
    public  interface CropImageListner
    {
        void onCropImage(String path);
    }
    public void setCropImageListner(CropImageListner cropImageListner) {
        mCropImageListner = cropImageListner;
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
        View contentView = View.inflate(getContext(), R.layout.image_crop_fragment, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            //((BottomSheetBehavior) behavior).setPeekHeight(contentView.getMeasuredHeight());
        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        civ = (CropImageView)contentView.findViewById(R.id.CropImageView);
        if (getArguments().getBoolean("isCamera", true)) {
            previewCapturedImage(getArguments().getString("filepath"));
        } else {
            previewPickedImage(getArguments().getString("filepath"));
        }
        btnSave = (ImageView)contentView.findViewById(R.id.btnSave);
        btnDelete = (ImageView)contentView.findViewById(R.id.btnDelete);
        btnRotate = (ImageView)contentView.findViewById(R.id.btnRotate);

        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnRotate.setOnClickListener(this);

        civ.setAspectRatio(3, 3);
        civ.setFixedAspectRatio(true);

    }






    private void previewCapturedImage(String path) {
        try {
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            civ.setImageBitmap(decodeScaledBitmapFromSdCard(path, 1200, 600), ei);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void previewPickedImage(String path) {
        ExifInterface ei = null;
        try {
            if (path != null) {
                ei = new ExifInterface(path);
            } else {
                //   Helper.showToast(getString(R.string.failed_please_pick_image),ImageCropFragment.this );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        civ.setImageBitmap(decodeScaledBitmapFromSdCard(path, 1200, 600), ei);
    }

    File f = null;

    @SuppressLint("SdCardPath")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                try {
                    Bitmap croppedImage = civ.getCroppedImage();
                    f = new File("/sdcard/" + Helper.getFileName(Helper.getOutputMediaFileUri().getPath()));
                    if (f.exists()) {
                        f.delete();
                    }
                    writeExternalToCache(croppedImage, f);
                    //setResult(RESULT_OK, new Intent().putExtra("filepath", f.getPath()));
                    mCropImageListner.onCropImage(f.getPath());


                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btnDelete:
                //setResult(RESULT_CANCELED);
                dismiss();
                break;

            case R.id.btnRotate:
                civ.rotateImage(90);
                break;
        }
    }

    public static Bitmap decodeScaledBitmapFromSdCard(String filePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static final int BUFFER_SIZE = 1024 * 8;

    void writeExternalToCache(Bitmap bitmap, File file) {
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            final BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE);
            //Bitmap.createScaledBitmap(bitmap, 648, 452, true).compress(CompressFormat.JPEG, 80, bos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, bos);
            bos.flush();
            bos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }
    }

}