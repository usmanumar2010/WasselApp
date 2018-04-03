package solutions.webdealer.project.wassel.fragments.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.text.Text;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import solutions.webdealer.project.wassel.R;
import solutions.webdealer.project.wassel.Utility;

import static com.facebook.FacebookSdk.getApplicationContext;

public class UserProfile extends Fragment {

    EditText fName, lName, email, number;
    Button editSave;
    Button uploadPhoto;
    CircularImageView rounded_image;
    RatingBar userRating;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    ProgressDialog progressDialog;
    String userId = null;
    String imageUrl = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        progressDialog = new ProgressDialog(getActivity());

        rounded_image = (CircularImageView) view.findViewById(R.id.rounded_image);
        userRating = (RatingBar) view.findViewById(R.id.rb_userRating);
        userRating.setIsIndicator(true);

        uploadPhoto = (Button) view.findViewById(R.id.bt_uploadPhoto);
        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        editSave = (Button) view.findViewById(R.id.bt_editSave);
        editSave.setText("Edit");

        fName = (EditText) view.findViewById(R.id.et_fName);
        lName = (EditText) view.findViewById(R.id.et_lName);
        email = (EditText) view.findViewById(R.id.et_email);
        number = (EditText) view.findViewById(R.id.et_number);

        sharedPreferences = this.getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("UserId", null);
        imageUrl = sharedPreferences.getString("profileImage", null);

        progressDialog.setMessage("please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), R.string.networkError, Toast.LENGTH_SHORT).show();
        } else {
            setEditUserDetail(userId);
        }

        fName.setEnabled(false);
        lName.setEnabled(false);
        email.setEnabled(false);
        number.setEnabled(false);

        editSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (editSave.getText().toString()) {
                    case "Save":
                        progressDialog.show();

                        fName.setEnabled(false);
                        lName.setEnabled(false);
                        email.setEnabled(false);
                        number.setEnabled(false);
                        editSave.setText("Edit");

                        if (!isNetworkAvailable()) {
                            Toast.makeText(getApplicationContext(), "network error", Toast.LENGTH_SHORT).show();
                        } else {
                            String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/editUserDetail";
                            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener() {
                                @Override
                                public void onResponse(Object response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.toString());
                                        String status = jsonObject.get("status").toString();
                                        if (status.equalsIgnoreCase("true")) {
                                            if (!isNetworkAvailable()) {
                                                Toast.makeText(getApplicationContext(), R.string.networkError, Toast.LENGTH_SHORT).show();
                                            } else {
                                                setEditUserDetail(userId);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("user_id", userId);
                                    params.put("firstName", fName.getText().toString());
                                    params.put("lastName", lName.getText().toString());
                                    params.put("email", email.getText().toString());
                                    return params;
                                }
                            };

                            requestQueue.add(stringRequest);
                        }

                        break;
                    case "Edit":
                        fName.setEnabled(true);
                        lName.setEnabled(true);
                        email.setEnabled(true);
                        number.setEnabled(false);
                        editSave.setText("Save");
                        break;
                }
            }
        });
        return view;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Add Photo!");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    boolean result = Utility.checkPermission(getActivity());
                    if (items[item].equals("Take Photo")) {
                        userChoosenTask = "Take Photo";
                        if (result)
                            cameraIntent();

                    } else if (items[item].equals("Choose from Library")) {
                        userChoosenTask = "Choose from Library";
                        if (result)
                            galleryIntent();

                    } else if (items[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        } catch (Exception exception) {
            Log.v("Here", "MyError", exception);
        }
    }

    private void galleryIntent() {

        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),
                    SELECT_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getApplicationContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
                Uri uri = data.getData();
                String path = null;
                try {
                    path = getPath(getApplicationContext(), uri);
                    Log.d("path", path);
                } catch (Exception e) {
                    Log.d("Path Exception", e.getMessage());
                }
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
                Uri uri = data.getData();
                String path = null;
                try {
                    path = getPath(getApplicationContext(), uri);
                    Log.d("path", path);
                } catch (Exception e) {
                    Log.d("Path Exception", e.getMessage());
                }
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bundle extras = data.getExtras();
        Bitmap photo = (Bitmap) extras.get("data");
        Uri uri = getImageUri(getApplicationContext(), photo);
        String path = null;
        try {
            path = getPath(getApplicationContext(), uri);
        } catch (Exception e) {
            Log.d("Path Exception", e.getMessage());
        }

        File file = new File(path);
        Long fileLength = (file.length()) / 1024;
        Toast.makeText(getApplicationContext(), "File size " + fileLength.toString() + "KB", Toast.LENGTH_SHORT).show();

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("loading picture...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Ion.with(getApplicationContext())
                .load("http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/uploadUserPicture")
                .setMultipartFile("profilePicture", "application/x-www-form-urlencoded", file)
                .setMultipartParameter("id", userId)
                .setMultipartParameter("type", "user")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(result));
                            String status = jsonObject.get("status").toString();
                            String path;
                            if (status.equalsIgnoreCase("true")) {
                                path = jsonObject.get("path").toString();
                                sharedPreferences = getApplicationContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.remove("profileImage");
                                editor.putString("profileImage", path);
                                editor.commit();
                                imageUrl = sharedPreferences.getString("profileImage", null);
                                Picasso.with(getApplicationContext())
                                        .load(path)
                                        .placeholder(R.drawable.camera_icon)
                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                                        .networkPolicy(NetworkPolicy.NO_CACHE)
                                        .into(rounded_image);

                                CircularImageView userProfilePic = (CircularImageView) getActivity().findViewById(R.id.userProfilePic);

                                Picasso.with(getApplicationContext())
                                        .load(path)
                                        .placeholder(R.drawable.camera_icon)
                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                                        .networkPolicy(NetworkPolicy.NO_CACHE)
                                        .into(userProfilePic);

                                progressDialog.dismiss();
                            } else if (status.equalsIgnoreCase("false2")) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Image not uploading", Toast.LENGTH_SHORT).show();
                            }
                            //   Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Uri uri = data.getData();
        String path = null;

        try {
            path = getPath(getApplicationContext(), uri);
        } catch (Exception e) {
            Log.d("Path Exception", e.getMessage());
        }

        File file = new File(path);

        Long fileLength = (file.length()) / 1024;
        Toast.makeText(getApplicationContext(), "File size " + fileLength.toString() + "KB", Toast.LENGTH_SHORT).show();

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("loading picture...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Ion.with(getApplicationContext())
                .load("http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/uploadUserPicture")
                .setMultipartFile("profilePicture", "application/x-www-form-urlencoded", file)
                .setMultipartParameter("id", userId)
                .setMultipartParameter("type", "user")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(result));
                            String status = jsonObject.get("status").toString();
                            String path;
                            if (status.equalsIgnoreCase("true")) {
                                path = jsonObject.get("path").toString();
                                sharedPreferences = getApplicationContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.remove("profileImage");
                                editor.putString("profileImage", path);
                                editor.commit();
                                imageUrl = sharedPreferences.getString("profileImage", null);
                                Picasso.with(getApplicationContext())
                                        .load(path)
                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                                        .networkPolicy(NetworkPolicy.NO_CACHE)
                                        .into(rounded_image);

                                CircularImageView userProfilePic = (CircularImageView) getActivity().findViewById(R.id.userProfilePic);

                                Picasso.with(getApplicationContext())
                                        .load(path)
                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                                        .networkPolicy(NetworkPolicy.NO_CACHE)
                                        .into(userProfilePic);

                                progressDialog.dismiss();
                            } else if (status.equalsIgnoreCase("false2")) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Image not uploading", Toast.LENGTH_SHORT).show();
                            }
                            //   Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private void setEditUserDetail(final String userId) {

        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/editUserDetail";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String status = jsonResponse.get("status").toString();
                    if (status.equalsIgnoreCase("true")) {
                        JSONObject jsonUserData = (JSONObject) jsonResponse.get("user");
                        String image = jsonUserData.get("profilePicture").toString();

                        fName.setText(jsonUserData.get("firstName").toString());
                        lName.setText(jsonUserData.get("lastName").toString());
                        email.setText(jsonUserData.get("email").toString());
                        number.setText(jsonUserData.get("mobileNumber").toString());

/*                        float rating = Float.parseFloat(jsonUserData.getString("rating"));
                        userRating.setRating(rating);*/

                        if (image.equalsIgnoreCase("null")) {
                            Picasso.with(getApplicationContext())
                                    .load(R.drawable.applogo)
                                    .placeholder(R.drawable.applogo)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .into(rounded_image);
                        } else {
                            Picasso.with(getApplicationContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.applogo)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .into(rounded_image);
                        }

                        TextView firstName = (TextView) getActivity().findViewById(R.id.tv_firstName);
                        TextView lastName = (TextView) getActivity().findViewById(R.id.tv_lastName);
                        firstName.setText(jsonUserData.get("firstName").toString() + " ");
                        lastName.setText(jsonUserData.get("lastName").toString());
                        progressDialog.dismiss();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
