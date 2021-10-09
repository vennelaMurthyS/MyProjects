package com.example.photoeditor;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static  final int CAMERA_REQUEST = 9999;
    ImageButton picImage, camera, saveImage;
    ImageView setImage;
    Uri uri;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        addressViews();
    }

    private void addressViews() {
        picImage = (ImageButton) findViewById(R.id.pic_image);
        camera = (ImageButton) findViewById(R.id.camera);
        saveImage = (ImageButton) findViewById(R.id.save);
        setImage  = (ImageView) findViewById(R.id.image_view);

        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(setImage.getResources().equals(R.drawable.ic_image_black_)) {
                    Toast.makeText(MainActivity.this,"No data to save",Toast.LENGTH_LONG).show();
                }else {
                    saveImageToExternalDirectory();
                }
            }
        });
    }

    private void saveImageToExternalDirectory() {

        BitmapDrawable drawable = (BitmapDrawable) setImage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        File sdCardDirectory = Environment.getExternalStorageDirectory();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File image = new File(sdCardDirectory, fname);

        boolean success = false;

        // Encode the file as a PNG image.
        FileOutputStream outStream;
        try {

            outStream = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            /* 100 to keep full quality of the image */

            outStream.flush();
            outStream.close();
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (success) {
            setImage.setImageResource(R.drawable.ic_image_black_);
            Toast.makeText(getApplicationContext(), "Image saved with success",
                    Toast.LENGTH_LONG).show();
        } else {
            setImage.setImageResource(R.drawable.ic_image_black_);
            Toast.makeText(getApplicationContext(),
                    "Error during image saving", Toast.LENGTH_LONG).show();
        }
    }

    public void picImage(View view) {
        CropImage.startPickImageActivity(MainActivity.this);
    }

    public void camera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAMERA_REQUEST);
    }

    @SuppressLint("MissingSuperCall")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageuri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageuri)) {
                uri = imageuri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                        , 0);
            } else {
                startCrop(imageuri);
            }
        } else if(requestCode == CAMERA_REQUEST) {
            bitmap = (Bitmap)data.getExtras().get("data");
            Log.i("ImageSetting","image set done");
            setImage.setImageBitmap(bitmap);

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (requestCode == RESULT_OK) {
                setImage.setImageURI(result.getUri());
                Log.i(" IMAGE UPDATE","IMage updated successfully!!!");
                Toast.makeText(MainActivity.this, "IMage updated successfully!!!", Toast.LENGTH_LONG).show();
//            }
        }
         else {
             setImage.setImageResource(R.drawable.ic_image);
             Toast.makeText(MainActivity.this,"Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    private void startCrop(Uri imageuri) {
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
