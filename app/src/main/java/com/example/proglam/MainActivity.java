package com.example.proglam;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private MapView mapView;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private MediaRecorder mediaRecorder;
    private boolean isMonitoring = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = new MapView(this);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        LinearLayout mapContainer = findViewById(R.id.mapContainer);
        mapContainer.addView(mapView);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMonitoring) {
                    stopMonitoring();
                    startButton.setText("Avvia Monitoraggio");
                } else {
                    startMonitoring();
                    startButton.setText("Interrompi Monitoraggio");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMonitoring();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Imposta le impostazioni della mappa, come il tipo di mappa e il controllo della posizione
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);

        // Inizializza il LocationManager per ottenere le coordinate GPS
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Richiede le autorizzazioni per l'accesso al GPS e al microfono
        requestPermissions();

        // Inizia a monitorare le coordinate GPS
        startLocationUpdates();
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO},
                    PERMISSIONS_REQUEST_CODE);
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
        }
    }

    private void startMonitoring() {
        isMonitoring = true;
        startLocationUpdates();

        // Inizializza il MediaRecorder per registrare l'audio
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile("/dev/null");

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopMonitoring() {
        isMonitoring = false;
        locationManager.removeUpdates(this);
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (googleMap != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

            // Ottiene la potenza del segnale WiFi
            int wifiSignalStrength = getWifiSignalStrength();

            // Ottiene la potenza del segnale LTE o UMTS
            int cellularSignalStrength = getCellularSignalStrength();

            // Ottiene l'intensità del rumore acustico
            int noiseLevel = getNoiseLevel();

            // Aggiorna la mappa con le nuove aree colorate
            updateMap(latLng, wifiSignalStrength, cellularSignalStrength, noiseLevel);
        }
    }

    private int getWifiSignalStrength() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getRssi();
    }

    private int getCellularSignalStrength() {
        int signalStrength = 0;
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Richiedi l'autorizzazione per l'accesso alla posizione precisa
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
                return signalStrength;
            }
            CellInfo cellInfo = telephonyManager.getAllCellInfo().get(0);
            if (cellInfo instanceof CellInfoLte) {
                CellSignalStrengthLte signalStrengthLte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                signalStrength = signalStrengthLte.getDbm();
            } else if (cellInfo instanceof CellInfoWcdma) {
                CellSignalStrengthWcdma signalStrengthWcdma = ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                signalStrength = signalStrengthWcdma.getDbm();
            }
        }
        return signalStrength;
    }

    private int getNoiseLevel() {
        // Implementa la logica per ottenere l'intensità del rumore acustico
        // Utilizza le API per la registrazione dell'audio

        int amplitude = 0;
        if (mediaRecorder != null) {
            amplitude = mediaRecorder.getMaxAmplitude();
        }
        return amplitude;
    }

    private void updateMap(LatLng latLng, int wifiSignalStrength, int cellularSignalStrength, int noiseLevel) {
        // Implementa la logica per la colorazione delle aree sulla mappa
        // in base alla potenza del segnale WiFi, alla potenza del segnale cellulare e
        // all'intensità del
        // Esempio di creazione di un poligono colorato sulla mappa
        PolygonOptions polygonOptions = new PolygonOptions()
                .add(new LatLng(latLng.latitude + 0.001, latLng.longitude + 0.001))
                .add(new LatLng(latLng.latitude + 0.001, latLng.longitude - 0.001))
                .add(new LatLng(latLng.latitude - 0.001, latLng.longitude - 0.001))
                .add(new LatLng(latLng.latitude - 0.001, latLng.longitude + 0.001))
                .strokeColor(Color.BLACK)
                .strokeWidth(2);

        // Colora il poligono in base alla potenza del segnale WiFi
        if (wifiSignalStrength >= -70) {
            polygonOptions.fillColor(Color.GREEN);
        } else if (wifiSignalStrength >= -80) {
            polygonOptions.fillColor(Color.YELLOW);
        } else {
            polygonOptions.fillColor(Color.RED);
        }

        // Aggiungi il poligono alla mappa
        Polygon polygon = googleMap.addPolygon(polygonOptions);
        polygon.setClickable(true);
        polygon.setTag("WiFi Signal Strength: " + wifiSignalStrength + "dBm");

        // Ripeti il processo per la potenza del segnale cellulare e l'intensità del rumore acustico

        // Aggiungi un marker per la posizione corrente
        googleMap.addMarker(new MarkerOptions().position(latLng));

        // Imposta il listener per i clic sui poligoni
        googleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                String tag = polygon.getTag().toString();
                Toast.makeText(MainActivity.this, tag, Toast.LENGTH_SHORT).show();
            }
        });
    }

// Implementa altri metodi di callback per la gestione delle autorizzazioni e gli eventi del GPS

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permessi necessari non concessi.", Toast.LENGTH_SHORT).show();
            }
        }
    }

// Implementa altri metodi di callback per la gestione degli eventi del GPS

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}