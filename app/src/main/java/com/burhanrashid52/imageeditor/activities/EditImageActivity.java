package com.burhanrashid52.imageeditor.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.burhanrashid52.imageeditor.R;
import com.burhanrashid52.imageeditor.base.BaseActivity;
import com.burhanrashid52.imageeditor.filters.FilterListener;
import com.burhanrashid52.imageeditor.filters.FilterViewAdapter;
import com.burhanrashid52.imageeditor.fragments.EmojiBSFragment;
import com.burhanrashid52.imageeditor.fragments.ImageCropFragment;
import com.burhanrashid52.imageeditor.fragments.ImageCropFragment2;
import com.burhanrashid52.imageeditor.fragments.PreviewFragment;
import com.burhanrashid52.imageeditor.fragments.PropertiesBSFragment;
import com.burhanrashid52.imageeditor.fragments.StickerBSFragment;
import com.burhanrashid52.imageeditor.fragments.TextEditorDialogFragment;
import com.burhanrashid52.imageeditor.fragments.UploadPhotoFragment;
import com.burhanrashid52.imageeditor.adapter.EditingToolsAdapter;
import com.burhanrashid52.imageeditor.fragments.UserListFragment;
import com.burhanrashid52.imageeditor.tools.ToolType;
import com.burhanrashid52.imageeditor.utils.Helper;

import java.io.File;
import java.io.IOException;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;

public class EditImageActivity extends BaseActivity implements OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        StickerBSFragment.StickerListener, EditingToolsAdapter.OnItemSelected, FilterListener, UploadPhotoFragment.UploadPhotoListner,  ImageCropFragment2.CropImageListner, PreviewFragment.SaveImageListner {

    private static final String TAG = EditImageActivity.class.getSimpleName();
    public static final String EXTRA_IMAGE_PATHS = "extra_image_paths";
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    private static final int CROP_IMAGE = 98;
    private PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private UploadPhotoFragment uploadPhotoFragment ;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private ImageCropFragment2 imageCropFragment2;
    private PreviewFragment previewFragment;
    private UserListFragment userListFragment;
    private TextView mTxtCurrentTool;
    private Typeface mWonderFont;
    private RecyclerView mRvTools, mRvFilters;
    private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private ConstraintLayout mRootView;
    private ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible;
    private String mainImagePath="",mainLoadBy="";
    String exicuteFlag="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_edit_image);

        initViews();

        mWonderFont = Typeface.createFromAsset(getAssets(), "beyond_wonderland.ttf");

        mPropertiesBSFragment = new PropertiesBSFragment();
        uploadPhotoFragment = new UploadPhotoFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        imageCropFragment2 = new ImageCropFragment2();
        previewFragment = new PreviewFragment();
        userListFragment = new UserListFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);
        uploadPhotoFragment.setOnPhotoUpload(this);
        imageCropFragment2.setCropImageListner(this);
        previewFragment.setSeveImageListner(this);
        LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);

        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(llmFilters);
        mRvFilters.setAdapter(mFilterViewAdapter);


        //Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);
        //Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);


    }

    private void initViews() {
        ImageView imgSave;
        ImageView imgClose;

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool);
        mRvTools = findViewById(R.id.rvConstraintTools);
        mRvFilters = findViewById(R.id.rvFilterView);
        mRootView = findViewById(R.id.rootView);



        imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);

        imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);

    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                styleBuilder.withTextColor(colorCode);

                mPhotoEditor.editText(rootView, inputText, styleBuilder);
                mTxtCurrentTool.setText(R.string.label_text);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imgSave:
                saveImage();
                break;

            case R.id.imgClose:
                onBackPressed();
                break;


        }
    }

    @SuppressLint("MissingPermission")
    private void saveImage() {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showLoading("Saving...");
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + ""
                    + System.currentTimeMillis() + ".png");
            try {
                file.createNewFile();

                SaveSettings saveSettings = new SaveSettings.Builder()
                        .setClearViewsEnabled(true)
                        .setTransparencyEnabled(true)
                        .build();

                mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        hideLoading();
                        showSnackbar("Image Saved Successfully");
                        mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        hideLoading();
                        showSnackbar("Failed to save Image");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                hideLoading();
                showSnackbar(e.getMessage());
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void saveTempImage(final String options) {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showLoading("wait...");
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "temp"
                     + ".png");
            try {
                file.createNewFile();

                SaveSettings saveSettings = new SaveSettings.Builder()
                        .setClearViewsEnabled(true)
                        .setTransparencyEnabled(true)
                        .build();

                mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {

                       // showSnackbar("Image Saved Successfully");

                        mainImagePath=imagePath;
                        hideLoading();
                        //fasflkajskdjfhakusf
                        mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                        switch (options)
                        {
                            case "text":
                                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(EditImageActivity.this);
                                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                                    @Override
                                    public void onDone(String inputText, int colorCode) {
                                        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                                        styleBuilder.withTextColor(colorCode);
                                        mPhotoEditorView.setBackgroundResource(R.drawable.image_border_transparent);
                                        mPhotoEditor.addText(inputText, styleBuilder);
                                        mTxtCurrentTool.setText(R.string.label_text);

                                    }
                                });
                                break;
                            case "crop":
                                mTxtCurrentTool.setText(R.string.label_crop);
                                Bundle bundle = new Bundle();
                                bundle.putString("filepath", mainImagePath);
                                if(mainLoadBy.equals("camera"))
                                {
                                    bundle.putBoolean("isCamera", true);
                                }else
                                {
                                    bundle.putBoolean("isCamera", false);
                                }
                                imageCropFragment2.setArguments(bundle);
                                imageCropFragment2.show(getSupportFragmentManager(), imageCropFragment2.getTag());
                                mPhotoEditorView.setBackgroundResource(R.drawable.image_border_transparent);
                                break;
                            case "preview":
                                Bundle bundle2 = new Bundle();
                                bundle2.putString("filepath", mainImagePath);
                                mTxtCurrentTool.setText(R.string.label_preview);
                                previewFragment.setArguments(bundle2);
                                previewFragment.show(getSupportFragmentManager(), previewFragment.getTag());
                                mPhotoEditorView.setBackgroundResource(R.drawable.image_border_transparent);
                                break;


                        }
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        hideLoading();
                        showSnackbar("Failed to save Image");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                hideLoading();
                showSnackbar(e.getMessage());
            }
        }
    }



    @Override
    public void onColorChanged(int colorCode) {
        //mPhotoEditor.setBrushColor(colorCode);
        if(getResources().getColor(R.color.blue_color_picker)==colorCode)
        {
            mPhotoEditorView.setBackgroundResource(R.drawable.image_border_blue);
        }else if(getResources().getColor(R.color.brown_color_picker)==colorCode)
        {
            mPhotoEditorView.setBackgroundResource(R.drawable.image_border_brown);
        }else if(getResources().getColor(R.color.green_color_picker)==colorCode)
        {
            mPhotoEditorView.setBackgroundResource(R.drawable.image_border_green);
        }else if(getResources().getColor(R.color.orange_color_picker)==colorCode)
        {
            mPhotoEditorView.setBackgroundResource(R.drawable.image_border_orange);
        }else if(getResources().getColor(R.color.red_color_picker)==colorCode)
        {
            mPhotoEditorView.setBackgroundResource(R.drawable.image_border_red);
        }else if(getResources().getColor(R.color.black)==colorCode)
        {
            mPhotoEditorView.setBackgroundResource(R.drawable.image_border_black);
        }else if(getResources().getColor(R.color.red_orange_color_picker)==colorCode)
        {
            mPhotoEditorView.setBackgroundResource(R.drawable.image_border_red_orange);
        }else if(getResources().getColor(R.color.sky_blue_color_picker)==colorCode)
        {
            mPhotoEditorView.setBackgroundResource(R.drawable.image_border_sky_blue);
        }else if(getResources().getColor(R.color.violet_color_picker)==colorCode)
        {
            mPhotoEditorView.setBackgroundResource(R.drawable.image_border_violet);
        }else if(getResources().getColor(R.color.white)==colorCode)
        {
            mPhotoEditorView.setBackgroundResource(R.drawable.image_border);
        }else if(getResources().getColor(R.color.yellow_color_picker)==colorCode)
        {
            mPhotoEditorView.setBackgroundResource(R.drawable.image_border_yellow);
        }else if(getResources().getColor(R.color.yellow_green_color_picker)==colorCode)
        {
            mPhotoEditorView.setBackgroundResource(R.drawable.image_border_yellow_green);
        }


//        String hexColor = String.format("#%06X", colorCode);
//        Log.e("ColorCodes",""+hexColor);
        mTxtCurrentTool.setText(R.string.label_frame);
    }


    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        mTxtCurrentTool.setText(R.string.label_emoji);

    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
        mTxtCurrentTool.setText(R.string.label_sticker);
    }

    @Override
    public void isPermissionGranted(boolean isGranted, String permission) {
        if (isGranted) {
            saveImage();
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you want to exit without saving image ?");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveImage();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();

    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }

    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {

            case UPLOAD:
                //mPhotoEditor.setBrushDrawingMode(true);
                mTxtCurrentTool.setText(R.string.label_upload);
                uploadPhotoFragment.show(getSupportFragmentManager(), uploadPhotoFragment.getTag());
                break;
            case FRAME:
                //mPhotoEditor.setBrushDrawingMode(true);
                if(validate())
                {
                    mTxtCurrentTool.setText(R.string.label_frame);
                    mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                }

                break;
            case TEXT:

                if(validate())
                {

                    saveTempImage("text");




                }

                break;
            case CROP:
                //mPhotoEditor.brushEraser();

                if(validate())
                {
                    saveTempImage("crop");





                }

                break;
            case PREVIEW:


                if(validate()) {
                    saveTempImage("preview");





                }


            case LIST:
                userListFragment.show(getSupportFragmentManager(), userListFragment.getTag());
                break;

        }
    }


    void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    @Override
    public void onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false);
            mTxtCurrentTool.setText(R.string.app_name);
        } else if (!mPhotoEditor.isCacheEmpty()) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }

    //Validation function
    private boolean validate()
    {
        if(mainImagePath.equals(""))
        {
            Toast.makeText(this,"Please select image from gallery or camera",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    //while click on the camera or gallery
    @Override
    public void onPhotoUpload(Bitmap image,String imagePath,String loadBy) {
        mPhotoEditor.clearAllViews();
        mPhotoEditorView.getSource().setImageBitmap(image);
        mainImagePath=imagePath;
        mainLoadBy=loadBy;
    }
    @Override
    public void fixeFrameChange() {
        mPhotoEditorView.setBackgroundResource(R.drawable.image_border);


    }


    @Override
    public void onCropImage(String path) {


        ///   String filename = photoPath.substring(photoPath.lastIndexOf("/") + 1);
        Helper.setImageToImageView(mPhotoEditorView, path);

    }

    //On click download button
    @Override
    public void onSaveImage() {
        saveImage();
    }
}
