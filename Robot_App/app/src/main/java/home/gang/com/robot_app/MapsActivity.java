package home.gang.com.robot_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private Socket socket;
    private BufferedWriter writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        socket = Network_Handler.get_socket();
        writer = Network_Handler.get_writer();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Add a marker in Sydney and move the camera
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                try {
                    LatLng current_location = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(current_location, 14));
                } catch (NullPointerException ex) {
                    new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("Error")
                            .setMessage("can't get your gps location.\ndid you enable location services ?!")
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                            .show();
                }
                return true;
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                try {
                    map.clear();
                    map.addMarker(new MarkerOptions().position(latLng));

                    writer.write("lat" + latLng.latitude, 0, String.valueOf("lat" + latLng.latitude).length());
                    writer.flush();

                    writer.write("lon" + latLng.longitude, 0, String.valueOf("lon" + latLng.longitude).length());
                    writer.flush();
                } catch (IOException e) {
                    Log.e("IOEx", e.getMessage());
                } catch (NullPointerException ex) {
                    map.clear();
                    new AlertDialog.Builder(MapsActivity.this)
                            .setTitle("Error")
                            .setMessage("can't send location to robot.\nare you connected to the server ?!")
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                            .show();
                }
            }
        });
    }
}
