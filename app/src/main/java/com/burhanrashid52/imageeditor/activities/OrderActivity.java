package com.burhanrashid52.imageeditor.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.burhanrashid52.imageeditor.R;
import com.burhanrashid52.imageeditor.model.User;
import com.burhanrashid52.imageeditor.utils.DatabaseClient;
import com.burhanrashid52.imageeditor.utils.Helper;

import java.io.File;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView ivFinalImage;
    EditText etEmailAddress,etFullName,etMobileNo,etAddress;
    Button btnSubmit;
    String filePath="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        init();

    }
    //initialize component
    private void init() {
        ivFinalImage=findViewById(R.id.ivFinalImage);
        etEmailAddress=findViewById(R.id.etEmailAddress);
        etFullName=findViewById(R.id.etFullName);
        etMobileNo=findViewById(R.id.etMobileNo);
        etAddress=findViewById(R.id.etAddress);
        btnSubmit=findViewById(R.id.btnSubmit);

        //set image using file path
        if (null!=getIntent().getStringExtra("filepath")) {
            filePath=getIntent().getStringExtra("filepath");
            File image = new File(filePath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
            //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
            ivFinalImage.setImageBitmap(bitmap);

        }

        btnSubmit.setOnClickListener(this);
    }

    //validation function
    public boolean validate()
    {
        if(TextUtils.isEmpty(etEmailAddress.getText().toString()))
        {
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Helper.isValidMail(etEmailAddress.getText().toString()))
        {
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(etFullName.getText().toString()))
        {
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(etMobileNo.getText().toString()))
        {
            Toast.makeText(this, "Please enter mobilenumber", Toast.LENGTH_SHORT).show();
            return false;
        }else if(etMobileNo.getText().toString().length()<10)
        {
            Toast.makeText(this, "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(etAddress.getText().toString()))
        {
            Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {

        if(validate())
        {
            final String emailAddress = etEmailAddress.getText().toString().trim();
            final String fullName = etFullName.getText().toString().trim();
            final String mobileNumber = etMobileNo.getText().toString().trim();
            final String address = etAddress.getText().toString().trim();

            saveTask(emailAddress,fullName,mobileNumber,address);


        }
    }



    //Save information in the local database
    private void saveTask(final String emailAddress, final String fullName, final String mobileNumber, final String address ) {
        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task
                User user = new User();
                user.setEmailAddress(emailAddress);
                user.setFirstName(fullName);
                user.setFilePath(filePath);
                user.setMobileNumber(mobileNumber);
                user.setAddress(address);

                //adding to database
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .taskDao()
                        .insert(user);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                sendMail(emailAddress,fullName,mobileNumber,address);

                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }

    //Send Mail function
    private void sendMail(final String emailAddress, final String fullName, final String mobileNumber,
                          final String address) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"pmmprashant961@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Application testing of image crop");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email :"+emailAddress+" fullName :"+fullName
        +" mobileNumber:"+mobileNumber+" Address:"+address);
        File root = Environment.getExternalStorageDirectory();
        String pathToMyAttachedFile = filePath;
        String filename=pathToMyAttachedFile.substring(pathToMyAttachedFile.lastIndexOf("/")+1);
        File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
        Uri path = Uri.fromFile((filelocation));
        /*if (!file.exists() || !file.canRead()) {
            return;
        }*/
       // Uri uri = Uri.fromFile(file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
        finish();

        startActivity(new Intent(getApplicationContext(), EditImageActivity.class));
    }
}
