package com.example.proglam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.location.LocationManager;
import android.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;





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

 SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()

.findFragmentById(R.id.map);

mapFragment.getMapAsync(this);
 }
@Override

public void onMapReady(GoogleMap googleMap) {

 mMap = googleMap;



 // Imposta il tipo di mappa

 mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);



// Imposta la suddivisione della mappa in aree quadrate o esagonali
 // in base alle coordinate GPS
PolygonOptions options = new PolygonOptions()

 .add(new LatLng(41.9028, 12.4964), new LatLng(41.9035, 12.4985), new LatLng(41.9025, 12.4992), new LatLng(41.9018, 12.4971))

 .strokeColor(Color.RED)

.fillColor(Color.BLUE);

 mMap.addPolygon(options);

 }

}
