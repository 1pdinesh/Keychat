package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    protected FirebaseUser firebaseUser;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this,"Map is Ready",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is ready");
        mMap = googleMap;

        if(mLocationPermissionsGranted)
        {
            getDeviceLocation();
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            mMap.setMyLocationEnabled(true);

        }

    }
    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef ;
    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public StorageReference locationStorage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getLocationPermission();

    }

    private void getDeviceLocation()
    {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try
        {
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Log.d(TAG, "onComplete: found location");
                        Location currentLocation = (Location)task.getResult();

                        moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM);
                        onLocation(currentLocation);
                    }
                    else
                    {
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(MapActivity.this,"unable to get current location",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        catch (SecurityException e)
        {
            Log.e(TAG, "getDeviceLocation: SecurityException:"+e.getMessage() );
        }

    }

    private void onLocation(final Location location) {

        Button bt=findViewById(R.id.button2);
        final  double lo=location.getLongitude();
        final  double la=location.getLatitude();





    }



    private void moveCamera(LatLng latLng, float zoom)
    {
        Log.d(TAG, "moveCamera: moving the camera to: lat:"+ latLng.latitude+", lng:"+ latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }



    private void initMap()
    {
        Log.d(TAG,"initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }

    private void getLocationPermission()
    {
        Log.d(TAG,"getLocationPermission: getting location permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                mLocationPermissionsGranted = true;
                initMap();
            }
            else
            {
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }

        }
        else
        {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG,"onRequestPermissionResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0)
                {
                    for (int i = 0; i < grantResults.length; i++)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }

                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;

                    //Initialize or map
                    initMap();

                }
            }
        }
    }

}
