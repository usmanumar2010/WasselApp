package solutions.webdealer.project.wassel.activities.verifications;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.Utility;

import static android.R.attr.path;
import static com.facebook.FacebookSdk.getApplicationContext;

public class LisenceVehicleVerification extends AppCompatActivity {

    private Button btnSelect_1, btnSelect_2, uploadImages;
    private ImageView lisence, registration;
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    public static final int gallery_lisence = 33;
    public static final int gallery_driver = 34;
    String lisURL = "";
    String regURL = "";
    int checkStatus = 0;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressBar progressBar;
    String Id;


    ProgressDialog progressDialog;
    public static final int popup_camera_lisence = 26;
    public static final int popup_camera_driver = 27;
    String mCurrentPhotoPath;
    public String pathlicense="";
    public String pathDriver="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lisence_vehicle_verification);

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        Id = sharedPreferences.getString("UserId", null);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading picture...");
        progressDialog.setCancelable(false);

        btnSelect_1 = (Button) findViewById(R.id.btnSelect_1);
        btnSelect_1.getBackground().setAlpha(30);
        btnSelect_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                checkStatus = 111;
                if(isNetworkAvailable()) {
                    selectImage("lisence");
                }else{
                    Toast.makeText(LisenceVehicleVerification.this, "Kindly check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSelect_2 = (Button) findViewById(R.id.btnSelect_2);
        btnSelect_2.getBackground().setAlpha(30);
        btnSelect_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                checkStatus = 222;
                if(isNetworkAvailable()) {
                    selectImage("vehicle");
                }else {
                    Toast.makeText(LisenceVehicleVerification.this, "Kindly check your internet connection", Toast.LENGTH_SHORT).show();

                }
            }
        });




        uploadImages = (Button) findViewById(R.id.bt_uploadImages);
        uploadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pathlicense.equalsIgnoreCase("") || pathDriver.equalsIgnoreCase("") ) {
                    Toast.makeText(getApplicationContext(), "please upload both pictures", Toast.LENGTH_SHORT).show();
                } else {

                    sharedPreferences = getSharedPreferences("ApprovalStatus", MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.remove("Status");
                    editor.putString("Status", "2");
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), CheckApprovedStatus.class);
                    startActivity(intent);
                }
            }
        });


    }//on create end


    private void selectImage(final String selectionButtonIs) {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add Photo!");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Take Photo")) {
                        userChoosenTask = "Take Photo";
//                            cameraIntent();


                            try {
                                if (ContextCompat.checkSelfPermission(LisenceVehicleVerification.this,
                                        android.Manifest.permission.CAMERA)
                                        != PackageManager.PERMISSION_GRANTED) {


                                    ActivityCompat.requestPermissions(LisenceVehicleVerification.this,
                                            new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                            0);

                                } else {
                                    //yeah !we got the permission

//
                                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                    if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                                        // Create the File where the photo should go
                                        File photoFile = null;
                                        try {
                                            photoFile = createImageFile();
                                        } catch (Exception ex) {
                                            // Error occurred while creating the File
                                            Toast.makeText(LisenceVehicleVerification.this, ex.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                        // Continue only if the File was successfully created
                                        if (photoFile != null) {
                                            if (selectionButtonIs.equalsIgnoreCase("lisence")) {
                                                try {
                                                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                                            "com.example.android.fileprovider",
                                                            photoFile);
                                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                                    startActivityForResult(takePictureIntent, popup_camera_lisence);
                                                } catch (Exception e) {
                                                    Toast.makeText(LisenceVehicleVerification.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            } else if (selectionButtonIs.equalsIgnoreCase("vehicle")) {
                                                try {
                                                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                                            "com.example.android.fileprovider",
                                                            photoFile);
                                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                                    startActivityForResult(takePictureIntent, popup_camera_driver);
                                                } catch (Exception e) {
                                                    Toast.makeText(LisenceVehicleVerification.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                        }

                                    }


                                }


                            } catch (Exception e) {
                                e.printStackTrace();

                            }


                    } else if (items[item].equals("Choose from Library")) {
                        userChoosenTask = "Choose from Library";
//                            galleryIntent();

                            Toast.makeText(LisenceVehicleVerification.this, "gallery is clicked ", Toast.LENGTH_SHORT).show();


                            if (selectionButtonIs.equalsIgnoreCase("lisence")) {
                                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, gallery_lisence);
                            } else if (selectionButtonIs.equalsIgnoreCase("vehicle")) {

                                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, gallery_driver);
                            }

                    } else if (items[item].equals("Cancel"))
                    {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        } catch (
                Exception exception)

        {
            Log.v("Here", "MyError", exception);
        }

    }


    public File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = null;
//        if (ContextCompat.checkSelfPermission(getActivity(),
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//
//
//        }else {
        File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );


        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();


//        }

        return image;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == popup_camera_lisence && resultCode == RESULT_OK) {


            galleryAddPic();
            try {
                setReducedImageSize("lisence");
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (requestCode == popup_camera_driver && resultCode == RESULT_OK) {

            galleryAddPic();
            try {
                setReducedImageSize("vehicle");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_OK && requestCode == gallery_lisence && data != null) {

            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);


//                final ImageView imageView = (ImageView) findViewById(R.id.imageProfile);

                final ImageView lisenceImageView= (ImageView ) findViewById(R.id.iv_lisence);

                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();

//                }
                String filePath = getApplicationContext().getFilesDir().getPath().toString();

                final File file2 = new File(getApplicationContext().getCacheDir(), bitmap + ".png");

                file2.createNewFile();

                ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream2);
                byte[] bytesArray2 = stream2.toByteArray();

                //write the bytes in file
                FileOutputStream fos2 = new FileOutputStream(file2);
                fos2.write(bytesArray2);
                fos2.flush();
                fos2.close();

//

                String lisenceURL = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/driverLicenseimage";

                progressDialog.show();
                Ion.with(getApplicationContext())
                        .load("POST", lisenceURL)
                        .setTimeout(60 * 60 * 1000)
                        .setMultipartFile("licenseimage", "application/x-www-form-urlencoded", file2)
                        .setMultipartParameter("driver_id", Id)
                        .setMultipartParameter("type", "driver")
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                try {

                                    JSONObject jsonObject = new JSONObject(String.valueOf(result));
                                    Toast.makeText(LisenceVehicleVerification.this, result.toString(), Toast.LENGTH_SHORT).show();
                                    String status = jsonObject.get("status").toString();


                                    if (status.equalsIgnoreCase("true")) {

                                        pathlicense = jsonObject.get("path").toString();
                                        Toast.makeText(LisenceVehicleVerification.this, pathlicense, Toast.LENGTH_SHORT).show();
                                        Log.d("usman", "***************************************");
                                        Log.d("usman", "***************************************");
                                        Log.d("usman", "***************************************");
                                        Log.d("usman", pathlicense);



                                        Picasso.with(getApplicationContext())
                                                .load(pathlicense)
                                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                                .into(lisenceImageView);


                                        progressDialog.dismiss();
                                    } else if (status.equalsIgnoreCase("false")) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Image not uploading", Toast.LENGTH_SHORT).show();
                                    }
                                    //   Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e1) {
                                    Toast.makeText(LisenceVehicleVerification.this, e1.toString(), Toast.LENGTH_SHORT).show();
                                    e1.printStackTrace();
                                    progressDialog.dismiss();

                                }
                            }
                        });

//*******************************************************************************************************************************//
                //Camera


            } catch (IOException e) {
                Toast.makeText(LisenceVehicleVerification.this, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                progressDialog.dismiss();
            }
        } else if (resultCode == RESULT_OK && requestCode == gallery_driver && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);


                final ImageView imageViewVehicle = (ImageView) findViewById(R.id.iv_registration);

                String extStorageDirectory = Environment.getExternalStorageDirectory()
                        .toString();


//                }
                String filePath = getApplicationContext().getFilesDir().getPath().toString();

                final File file2 = new File(getApplicationContext().getCacheDir(), bitmap + ".png");

                file2.createNewFile();

                ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream2);
                byte[] bytesArray2 = stream2.toByteArray();

                //write the bytes in file
                FileOutputStream fos2 = new FileOutputStream(file2);
                fos2.write(bytesArray2);
                fos2.flush();
                fos2.close();

//

                String vehicleURL = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/driverRegistionimage";

                progressDialog.show();
                Ion.with(getApplicationContext())
                        .load("POST", vehicleURL)
                        .setTimeout(60 * 60 * 1000)
                        .setMultipartFile("registionimage", "application/x-www-form-urlencoded", file2)
                        .setMultipartParameter("driver_id", Id)
                        .setMultipartParameter("type", "driver")
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                try {

                                    JSONObject jsonObject = new JSONObject(String.valueOf(result));
                                    Toast.makeText(LisenceVehicleVerification.this, result.toString(), Toast.LENGTH_SHORT).show();
                                    String status = jsonObject.get("status").toString();


                                    if (status.equalsIgnoreCase("true")) {

                                        pathDriver = jsonObject.get("path").toString();
                                        Toast.makeText(LisenceVehicleVerification.this, pathDriver, Toast.LENGTH_SHORT).show();
                                        Log.d("usman", "***************************************");
                                        Log.d("usman", "***************************************");
                                        Log.d("usman", "***************************************");
                                        Log.d("usman", pathDriver);

                                        Picasso.with(getApplicationContext())
                                                .load(pathDriver)
                                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                                .into(imageViewVehicle);


                                        progressDialog.dismiss();
                                    } else if (status.equalsIgnoreCase("false")) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Image not uploading", Toast.LENGTH_SHORT).show();
                                    }
                                    //   Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e1) {
                                    Toast.makeText(LisenceVehicleVerification.this, e1.toString(), Toast.LENGTH_SHORT).show();
                                    e1.printStackTrace();
                                    progressDialog.dismiss();

                                }
                            }
                        });

//*******************************************************************************************************************************//
                //Camera


            } catch (IOException e) {
                Toast.makeText(LisenceVehicleVerification.this, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                progressDialog.dismiss();
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    void setReducedImageSize(String whichImage) throws IOException {


        String lisenceURL = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/driverLicenseimage";
        String vehicleURL = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/driverRegistionimage";

        if (whichImage.equalsIgnoreCase("lisence")) {
            // Get the dimensions of the View
            final ImageView imageViewlisence = (ImageView) findViewById(R.id.iv_lisence);
            int targetW = imageViewlisence.getWidth();
            int targetH = imageViewlisence.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;


            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

            final File file3 = new File(getApplicationContext().getCacheDir(), bitmap + ".jpeg");

            file3.createNewFile();

            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream2);
            byte[] bytesArray2 = stream2.toByteArray();

            //write the bytes in file
            FileOutputStream fos2 = new FileOutputStream(file3);
            fos2.write(bytesArray2);
            fos2.flush();
            fos2.close();


            progressDialog.show();
            Ion.with(getApplicationContext())
                    .load("POST", lisenceURL)
                    .setTimeout(60 * 60 * 1000)
                    .setMultipartFile("licenseimage", "application/x-www-form-urlencoded", file3)
                    .setMultipartParameter("driver_id", Id)
                    .setMultipartParameter("type", "driver")
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            try {

                                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                                Toast.makeText(LisenceVehicleVerification.this, result.toString(), Toast.LENGTH_SHORT).show();
                                String status = jsonObject.get("status").toString();


                                if (status.equalsIgnoreCase("true")) {

                                    pathlicense = jsonObject.get("path").toString();
                                    Toast.makeText(LisenceVehicleVerification.this, pathlicense, Toast.LENGTH_SHORT).show();

                                    Log.d("usman", "************************************************");

                                    Picasso.with(getApplicationContext())
                                            .load(pathlicense)
                                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                                            .networkPolicy(NetworkPolicy.NO_CACHE)
                                            .into(imageViewlisence);


                                    progressDialog.dismiss();
                                } else if (status.equalsIgnoreCase("false")) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Image not uploading", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
        } else if (whichImage.equalsIgnoreCase("vehicle")) {
            final ImageView imageViewVehicle = (ImageView) findViewById(R.id.iv_registration);

            int targetW = imageViewVehicle.getWidth();
            int targetH = imageViewVehicle.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;


            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

            final File file3 = new File(getApplicationContext().getCacheDir(), bitmap + ".jpeg");

            file3.createNewFile();

            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream2);
            byte[] bytesArray2 = stream2.toByteArray();

            //write the bytes in file
            FileOutputStream fos2 = new FileOutputStream(file3);
            fos2.write(bytesArray2);
            fos2.flush();
            fos2.close();


            progressDialog.show();
            Ion.with(getApplicationContext())
                    .load("POST", vehicleURL)
                    .setTimeout(60 * 60 * 1000)
                    .setMultipartFile("registionimage", "application/x-www-form-urlencoded", file3)
                    .setMultipartParameter("driver_id", Id)
                    .setMultipartParameter("type", "driver")
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            try {

                                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                                Toast.makeText(LisenceVehicleVerification.this, result.toString(), Toast.LENGTH_SHORT).show();
                                String status = jsonObject.get("status").toString();



                                if (status.equalsIgnoreCase("true")) {

                                    pathDriver = jsonObject.get("path").toString();
                                    Toast.makeText(LisenceVehicleVerification.this, pathDriver, Toast.LENGTH_SHORT).show();


                                    Picasso.with(getApplicationContext())
                                            .load(pathDriver)
                                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                                            .networkPolicy(NetworkPolicy.NO_CACHE)
                                            .into(imageViewVehicle);


                                    progressDialog.dismiss();
                                } else if (status.equalsIgnoreCase("false")) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Image not uploading", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
        }
    }

//        uploadImages = (Button) findViewById(R.id.bt_uploadImages);
//        uploadImages.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (lisURL.equalsIgnoreCase("") || regURL.equalsIgnoreCase("")) {
//                    Toast.makeText(getApplicationContext(), "please upload both pictures", Toast.LENGTH_SHORT).show();
//                } else {
//
//                    sharedPreferences = getSharedPreferences("ApprovalStatus", MODE_PRIVATE);
//                    editor = sharedPreferences.edit();
//                    editor.remove("Status");
//                    editor.putString("Status", "2");
//                    editor.commit();
//
//                    Intent intent = new Intent(getApplicationContext(), CheckApprovedStatus.class);
//                    startActivity(intent);
//                }
//            }
//        });
//
//        lisence = (ImageView) findViewById(R.id.iv_lisence);
//        lisence.getBackground().setAlpha(30);
//
//        registration = (ImageView) findViewById(R.id.iv_registration);
//        registration.getBackground().setAlpha(30);
//
//    }
//
//    public interface OnFragmentInteractionListener {
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (userChoosenTask.equals("Take Photo"))
//                        cameraIntent();
//                    else if (userChoosenTask.equals("Choose from Library"))
//                        galleryIntent();
//                } else {
//                    //code for deny
//                }
//                break;
//        }
//    }
//

//
//    private void galleryIntent() {
//
//        Intent intent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        try {
//            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),
//                    SELECT_FILE);
//        } catch (android.content.ActivityNotFoundException ex) {
//            // Potentially direct the user to the Market with a Dialog
//            Toast.makeText(getApplicationContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void cameraIntent() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, REQUEST_CAMERA);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == SELECT_FILE) {
//                onSelectFromGalleryResult(data);
//                Uri uri = data.getData();
//                String path = null;
//                try {
//                    path = getPath(getApplicationContext(), uri);
//                    Log.d("path", path);
//                } catch (Exception e) {
//                    Log.d("Path Exception", e.getMessage());
//                }
//            } else if (requestCode == REQUEST_CAMERA) {
//                onCaptureImageResult(data);
//                Uri uri = data.getData();
//                String path = null;
//                try {
//                    path = getPath(getApplicationContext(), uri);
//                    Log.d("path", path);
//                } catch (Exception e) {
//                    Log.d("Path Exception", e.getMessage());
//                }
//            }
//        }
//    }
//
//    private void onCaptureImageResult(Intent data) {
//        Bundle extras = data.getExtras();
//        Bitmap photo = (Bitmap) extras.get("data");
//        Uri uri = getImageUri(getApplicationContext(), photo);
//        String path = null;
//        try {
//            path = getPath(getApplicationContext(), uri);
//        } catch (Exception e) {
//            Log.d("Path Exception", e.getMessage());
//        }
//
//        File file = new File(path);
//        Long fileLength = (file.length()) / 1024;
//        Toast.makeText(getApplicationContext(), "File size " + fileLength.toString() + "KB", Toast.LENGTH_SHORT).show();
//
//        final ProgressDialog progressDialog;
//        progressDialog = new ProgressDialog(getApplicationContext());
//        progressDialog.setMessage("loading picture...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        String param = null;
//        String lisenceURL = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/driverLicenseimage";
//        String vehicleURL = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/driverRegistionimage";
//        String url = null;
//
//        if (checkStatus == 111) {
//            url = lisenceURL;
//            param = "licenseimage";
//        } else if (checkStatus == 222) {
//            url = vehicleURL;
//            param = "registionimage";
//        }
//
//        Ion.with(getApplicationContext())
//                .load(url)
//                .setMultipartFile(param, "application/x-www-form-urlencoded", file)
//                .setMultipartParameter("driver_id", Id)
//                .setMultipartParameter("type", "driver")
//                .asJsonObject()
//                .setCallback(new FutureCallback<JsonObject>() {
//                    @Override
//                    public void onCompleted(Exception e, JsonObject result) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(result.toString());
//                            String status = jsonObject.getString("status");
//                            String path = null;
//                            if (status.equalsIgnoreCase("true")) {
//                                path = jsonObject.getString("path");
//                            }
//                            if (checkStatus == 111) {
//                                lisURL = path;
//                                Picasso.with(getApplicationContext())
//                                        .load(path)
//                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
//                                        .networkPolicy(NetworkPolicy.NO_CACHE)
//                                        .into(lisence);
//                                progressDialog.dismiss();
//                            } else if (checkStatus == 222) {
//                                regURL = path;
//                                Picasso.with(getApplicationContext())
//                                        .load(path)
//                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
//                                        .networkPolicy(NetworkPolicy.NO_CACHE)
//                                        .into(registration);
//                                progressDialog.dismiss();
//                            }
//                        } catch (JSONException e1) {
//                            progressDialog.dismiss();
//                            e1.printStackTrace();
//                        }
//                    }
//                });
//    }
//
//    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }
//
//    @SuppressWarnings("deprecation")
//    private void onSelectFromGalleryResult(Intent data) {
//
//        if (!isNetworkAvailable()) {
//            Toast.makeText(getApplicationContext(), "network error", Toast.LENGTH_SHORT).show();
//        } else {
//            Uri uri = data.getData();
//            String path = null;
//
//            try {
//                path = getPath(getApplicationContext(), uri);
//            } catch (Exception e) {
//                Log.d("Path Exception", e.getMessage());
//            }
//
//            File file = new File(path);
//
//            Long fileLength = (file.length()) / 1024;
//            Toast.makeText(getApplicationContext(), "File size " + fileLength.toString() + "KB", Toast.LENGTH_SHORT).show();
//
//            String param = null;
//            String lisenceURL = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/driverLicenseimage";
//            String vehicleURL = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/driverRegistionimage";
//            String url = null;
//
//            if (checkStatus == 111) {
//                url = lisenceURL;
//                param = "licenseimage";
//            } else if (checkStatus == 222) {
//                url = vehicleURL;
//                param = "registionimage";
//            }
//
//            progressDialog.show();
//            Ion.with(getApplicationContext())
//                    .load(url)
//                    .setMultipartFile(param, "application/x-www-form-urlencoded", file)
//                    .setMultipartParameter("driver_id", Id)
//                    .setMultipartParameter("type", "driver")
//                    .asJsonObject()
//                    .setCallback(new FutureCallback<JsonObject>() {
//                        @Override
//                        public void onCompleted(Exception e, JsonObject result) {
//                            try {
//                                JSONObject jsonObject = new JSONObject(result.toString());
//                                String status = jsonObject.getString("status");
//                                String path = null;
//                                if (status.equalsIgnoreCase("true")) {
//                                    path = jsonObject.getString("path");
//                                }
//                                if (checkStatus == 111) {
//                                    lisURL = path;
//                                    Picasso.with(getApplicationContext())
//                                            .load(path)
//                                            .memoryPolicy(MemoryPolicy.NO_CACHE)
//                                            .networkPolicy(NetworkPolicy.NO_CACHE)
//                                            .into(lisence);
//                                    progressDialog.dismiss();
//                                } else if (checkStatus == 222) {
//                                    regURL = path;
//                                    Picasso.with(getApplicationContext())
//                                            .load(path)
//                                            .memoryPolicy(MemoryPolicy.NO_CACHE)
//                                            .networkPolicy(NetworkPolicy.NO_CACHE)
//                                            .into(registration);
//                                    progressDialog.dismiss();
//                                }
//                            } catch (JSONException e1) {
//                                progressDialog.dismiss();
//                                e1.printStackTrace();
//                            }
//                        }
//                    });
//        }
//
//    }
//
//    public static String getPath(Context context, Uri uri) throws URISyntaxException {
//        if ("content".equalsIgnoreCase(uri.getScheme())) {
//            String[] projection = {"_data"};
//            Cursor cursor = null;
//
//            try {
//                cursor = context.getContentResolver().query(uri, projection, null, null, null);
//                int column_index = cursor.getColumnIndexOrThrow("_data");
//                if (cursor.moveToFirst()) {
//                    return cursor.getString(column_index);
//                }
//            } catch (Exception e) {
//            }
//        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            return uri.getPath();
//        }
//
//        return null;
//    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
