package com.example.proglam;

import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    //permessi "sensori"
    private static final int PERMISSION_RECORD_AUDIO_REQUEST_CODE = 100;
    private static final int PERMISSION_FOREGROUND_LOCATION_REQUEST_CODE = 200;
    private static final int PERMISSION_BACKGROUND_LOCATION_REQUEST_CODE = 300;
    private static final int PERMISSION_FILE_MEDIA_REQUEST_CODE = 400;
    private static final int CONNECTION_WIFI = 0;
    private static final int CONNECTION_CELLULAR = 1;

    //mappa
    private MappaFragment mappaFragment;
    private GoogleMap nMap;

    //localizzazione utente
    private LocationRequest requestLocation;
    private FusedLocationProviderClient locationProviderClient;
    private static boolean locationUpdatesRequested = false;
    private LocationCallback locationCallback;
    private Location currentLocation;

    // Impostazioni
    private Button btnImpostazioni;
    private boolean activeMode = true;
    private String notificationMode = "KILOMETER";
    private Button btnCloud;
    @Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

}


}