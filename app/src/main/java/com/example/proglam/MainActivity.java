package com.example.proglam;

import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Location;
import com.google.android.gms.maps.GoogleMap;
import android.content.Context;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.PolygonOptions;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inizializza il LocationManager e il LocationListener
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Aggiorna la mappa in base alla posizione del dispositivo
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                // Colora l'area sulla mappa con un riquadro blu
                PolygonOptions options = new PolygonOptions()
                        .add(new LatLng(location.getLatitude() - 0.001, location.getLongitude() - 0.001),
                                new LatLng(location.getLatitude() + 0.001, location.getLongitude() - 0.001),
                                new LatLng(location.getLatitude() + 0.001, location.getLongitude() + 0.001),
                                new LatLng(location.getLatitude() - 0.001, location.getLongitude() + 0.001))
                        .strokeColor(Color.RED)
                        .fillColor(Color.BLUE);
                mMap.addPolygon(options);
            }
        };

        // Richiede l'accesso alla posizione del dispositivo
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        // Inizializza la mappa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Imposta il tipo di mappa
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
}

