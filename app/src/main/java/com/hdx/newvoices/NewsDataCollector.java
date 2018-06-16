package com.hdx.newvoices;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsDataCollector extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final int RC_PHOTO_PICKER = 2;
    String userName;
    String postTitle;
    String postAddress;
    String postDate;
    String postCategory;
    String postDescription;
    String postImge;

    Spinner mySpinner;
    ArrayAdapter<CharSequence> arrayAdapter;

    Date date;
    Geocoder geocoder;
    Double latitude;
    Double longitude;
    List<Address> addresses;

    EditText titleEditText;
    EditText descriptionEditText;
    ImageButton publishImageButton;
    ImageButton postImageImageButton;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    NotificationManagerCompat notificationManager;
    NotificationCompat.Builder builder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_card);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Post");
        storageReference = firebaseStorage.getReference().child("Post_Images");

        titleEditText = findViewById(R.id.input_title_edit_text);
        descriptionEditText = findViewById(R.id.input_description_edit_text);
        publishImageButton = findViewById(R.id.publish_button);
        postImageImageButton = findViewById(R.id.input_add_image);

        mySpinner = findViewById(R.id.input_spinner);
        mySpinner.setOnItemSelectedListener(this);

        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.category_array, R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mySpinner.setAdapter(arrayAdapter);

        date = Calendar.getInstance().getTime();
        postDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
//        Toast.makeText(this, postDate, Toast.LENGTH_LONG).show();

        new RetrieveData().execute();

        postImageImageButton.setOnClickListener(view ->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/jpg");
            intent.putExtra(intent.EXTRA_LOCAL_ONLY, false);
            startActivityForResult(Intent.createChooser(intent, "Completa accion con"), RC_PHOTO_PICKER);

        });
        publishImageButton.setOnClickListener(view ->{
            postTitle = titleEditText.getText().toString();
            postDescription = descriptionEditText.getText().toString();

            Intent getUserName = getIntent();
            Bundle bundle = getUserName.getExtras();
            userName = (String) bundle.get("USER_NAME");

            NewsModel newsModel =
                    new NewsModel(postTitle, postAddress, postDate, postCategory, userName, postDescription, postImge);
            databaseReference.push().setValue(newsModel);
            pushNotification("Nuevo Contenido", postTitle);
//            Log.d("PostAddress", postImge);
            finish();
        });
    }

    public void pushNotification(String title, String description){

//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager = NotificationManagerCompat.from(this);
        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_image)
                .setContentTitle(title)
                .setContentText(description);
        Intent targetIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent
                .getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        notificationManager.notify(1, builder.build());

    }
    public void GetLocation(){

        FusedLocationProviderClient locationProviderClient =
                LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                List<Location> locations = locationResult.getLocations();
                Location lastLocation = locationResult.getLastLocation();
            }
        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                latitude = (Double) location.getLatitude();
                longitude = (Double) location.getLongitude();
                Log.d("NewsDataCollector", "1 location" + location.getLatitude() + " ," + location.getLongitude());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude,1);
                    String country = addresses.get(0).getCountryName();
                    String city = addresses.get(0).getAdminArea();

                    postAddress = city + ", " + country;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    public class RetrieveData extends android.os.AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            Looper.prepare();
            GetLocation();
            Looper.loop();
            return null;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        postCategory = parent.getItemAtPosition(position).toString();
//        Toast.makeText(this, postCategory, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            StorageReference photoRef =
                    storageReference.child(selectedImageUri.getLastPathSegment());
            photoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //When the image has successfully uploaded, get its download URL
                            photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    postImge =  uri.toString();
                                }
                            });
                        }
                    });
        }
    }
}


