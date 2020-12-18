package com.example.imageuploaddemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.imageuploaddemo.product.ProductDataResponse;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99 ;
    private static final int CAPTURE_REQUEST_CODE = 0;
    private static final int SELECT_REQUEST_CODE = 1;
    private Button captureImage,selectImage;
    private ImageView imageView;
    private OurRetrofitClient ourRetrofitClient;
    private ProgressDialog progressDialog;

    File file;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        captureImage = findViewById(R.id.capture_image);
        selectImage = findViewById(R.id.select_image);
        imageView = findViewById(R.id.Image_view);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://mern-pos.herokuapp.com/").addConverterFactory(GsonConverterFactory.create()).build();
        ourRetrofitClient = retrofit.create(OurRetrofitClient.class);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Image Upload....");

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckPermission()) {
                    Intent capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(capture, CAPTURE_REQUEST_CODE);
                }
            }
        });


        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(CheckPermission()) {
//                    Intent intent =new Intent();
//                    intent.setType("image/*");
//                    //intent.setAction(Intent.ACTION_GET_CONTENT);
//                    // cvb
//                     intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//
//                    startActivityForResult(intent,SELECT_REQUEST_CODE);

                    Intent select = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(select, SELECT_REQUEST_CODE);
                }
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case CAPTURE_REQUEST_CODE:

//                if(resultCode == RESULT_OK){
//
//                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                    imageView.setImageBitmap(bitmap);
//                    progressDialog.show();
//                    //ImageUpload(bitmap);
//
//                }


            break;

            case SELECT_REQUEST_CODE:

                if(resultCode == RESULT_OK && data!=null
                        && data.getData()!=null){

                    try {

                        imageUri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        imageView.setImageBitmap(bitmap);
                        progressDialog.show();

                        ImageUpload(imageUri);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(this, "url empty", Toast.LENGTH_SHORT).show();
                }


            break;
        }

    }




    private void ImageUpload(Uri imgUri) {
        file = new File(String.valueOf(imgUri));
        String path= getImagePath(imgUri);
        // final RequestBody image= RequestBody.create(MediaType.parse("multipart/form-data"), file);
        final RequestBody image= RequestBody.create(MediaType.parse("multipart/form-data"), path);

        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("image", "image", image);
        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), "laptop");
        RequestBody price = RequestBody.create(MediaType.parse("multipart/form-data"), "1200");
        RequestBody sellingPrice = RequestBody.create(MediaType.parse("multipart/form-data"), "1000");
        RequestBody unit = RequestBody.create(MediaType.parse("multipart/form-data"), "piece");
        RequestBody stock = RequestBody.create(MediaType.parse("multipart/form-data"), "12");
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), "product description laptop");
        String token="Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVmYTY2ODdiNzdkZmFjMDAxN2JkMzNkMSIsImlhdCI6MTYwODI5OTc5OSwiZXhwIjoxNjA4Mzg2MTk5fQ.W6nu_Q53NTFwkvOyq4JyotgM6cnXoO66mKnEXr2ntsg";

         ourRetrofitClient.uploadImage(token,multipartBody,name,price,sellingPrice,unit,stock,description).
                enqueue(new Callback<ProductDataResponse>() {
            @Override
            public void onResponse(Call<ProductDataResponse> call, Response<ProductDataResponse> response) {
                    Log.e("eroor",new Gson().toJson(response.body()));
                if (response.isSuccessful()){
                    Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
//                ProductDataResponse customerInformationDataResponse=response.body();
//                assert customerInformationDataResponse != null;
//                if (customerInformationDataResponse.getSuccess()){
//                    Log.e("asd","ssss");
//                }else {
//                    Log.e("asd","ffff");
//
//                }
                }else {
                    Log.e("ent",String.valueOf(response.message()));
                   // Toast.makeText(MainActivity.this, "ffff", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, String.valueOf(response.message()), Toast.LENGTH_SHORT).show();

                }
                progressDialog.dismiss();

            }
            @Override
            public void onFailure(Call<ProductDataResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                Log.e("asd",t.getMessage());
                progressDialog.dismiss();
            }
        });

    }



    public boolean CheckPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Permission")
                        .setMessage("Please accept the permissions")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_LOCATION);


                                startActivity(new Intent(MainActivity
                                        .this, MainActivity.class));
                                MainActivity.this.overridePendingTransition(0, 0);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }

            return false;
        } else {

            return true;

        }
    }


    public String getImagePath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}