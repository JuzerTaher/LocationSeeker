package com.example.juzer.linking1and2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    Button btnCheck;
    Intent i;

    public static final String TAG="TAG";
    private static final int REQUEST_CODE=1000;

    private GoogleApiClient googleApiClient;
    private Location location;
    private TextView txtLocation;

    static double latitude;
    static double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLocation=(TextView) findViewById(R.id.txtLocation);

        googleApiClient=new GoogleApiClient.Builder(MainActivity.this)
                .addConnectionCallbacks(MainActivity.this)
                .addOnConnectionFailedListener(MainActivity.this)
                .addApi(LocationServices.API).build();

        btnCheck=(Button) findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(MainActivity.this);

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.d(TAG,"Connection Established");
        showUserLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"Your connection is suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed..!");

        /*In case, the user is not connected to google play services, then this code will be
        executed...*/

        if(connectionResult.hasResolution()){

            try{
                connectionResult.startResolutionForResult(MainActivity.this,REQUEST_CODE);
            }catch(Exception e){
                Log.d(TAG,e.getStackTrace().toString());
            }
        }else{
            Toast.makeText(MainActivity.this,"PLay services not working",
                    Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE && requestCode==RESULT_OK){
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(googleApiClient!=null)
            googleApiClient.connect();
    }

    private void showUserLocation(){

        int permissionCheck= ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck== PackageManager.PERMISSION_GRANTED){

            FusedLocationProviderApi fusedLocationProviderApi=LocationServices.FusedLocationApi;

            location=fusedLocationProviderApi.getLastLocation(googleApiClient);


            if(location!=null){

                latitude=location.getLatitude();
                longitude=location.getLongitude();
                txtLocation.setText(latitude+","+longitude);

            }else
                txtLocation.setText("Can't access your location, try again later...");

        }
        else{
            txtLocation.setText("Location permission not granted");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{"Manifest.permission.ACCESS_FINE_LOCATION"},1);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCheck:
                i=new Intent(MainActivity.this,MapsActivity.class);
                startActivity(i);
                break;
        }
    }
}
