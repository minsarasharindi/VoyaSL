package com.cy.voyasl;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

public class ChooseRideActivity extends AppCompatActivity   implements OnMapReadyCallback  {


    private GoogleMap mMap;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    public static final int REQUEST_CHECK_SETTING = 1001;
    private Marker currentLocationMarker;
    double KLat;
    double KLng;
    String macLocationLatitude;




    private TextView mKm;
    private ImageView mBooknow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_ride);
       /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/


        initView();

        mBooknow.setOnClickListener(v -> {
            dialogs();
        });



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        locationRequest = com.google.android.gms.location.LocationRequest.create();
        locationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addAllLocationRequests(Collections.singleton(locationRequest));
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());


        String KLatLng = Prefs.getString("MKLat","") +","+Prefs.getString("MKLng","");


    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        if (currentLocationMarker != null) {
                            currentLocationMarker.remove();
                        }
                        currentLocationMarker = mMap.addMarker(new MarkerOptions()
                                .position(currentLocation)
                                .title("Current Location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_pointer)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Added Location/" + Prefs.getString("MKIDD", ""));
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String macLocationCoordinates = dataSnapshot.child("XLatLog").getValue(String.class);
                                    if (macLocationCoordinates != null && !macLocationCoordinates.isEmpty()) {
                                        String[] parts = macLocationCoordinates.split(",");
                                        if (parts.length == 2) {


                                           // Toast.makeText(ChooseRideActivity.this, macLocationCoordinates+"", Toast.LENGTH_SHORT).show();
                                            Log.d("ttt",macLocationCoordinates);
                                            String[] parts1 = macLocationCoordinates.split(",");
                                            // Trim any leading or trailing spaces that might be present
                                            String latitude = parts1[0].trim();
                                            String longitude = parts1[1].trim();

                                            if (latitude != null && longitude != null) {
                                                // Parse the latitude and longitude
                                                double KLat = Double.parseDouble(latitude);
                                                double KLng = Double.parseDouble(longitude);

                                                // Create a LatLng object for the mechanic's location
                                                LatLng mechanicLocation = new LatLng(KLat, KLng);

                                                // Add or update the mechanic's marker on the map
                                                // This example simply adds a new marker; you might want to update an existing marker instead
                                                mMap.clear(); // Clear the map to remove old markers
                                                mMap.addMarker(new MarkerOptions().position(mechanicLocation).title("Mechanic Location"));
                                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mechanicLocation, 15));

                                                LatLng destination = new LatLng(KLat, KLng);

                                                mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));


                                                // New code to calculate and toast the distance
                                                Location destinationLocation = new Location("");
                                                destinationLocation.setLatitude(KLat);
                                                destinationLocation.setLongitude(KLng);

                                                float distanceInMeters = location.distanceTo(destinationLocation);
                                                float distanceInKilometers = distanceInMeters / 1000;

                                              /*  try {
                                                    mKm.setText("Distance: "+distanceInKilometers + " km");
                                                } catch (Exception e) {
                                                    throw new RuntimeException(e);
                                                }*/


                                                //mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                                                String url = getDirectionsUrl(currentLocation, destination, getString(R.string.google_maps_key));
                                                fetchDirections(url);

                                            }
                                            // String url = getDirectionsUrl(currentLocation, destination, getString(R.string.google_maps_key));
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("Firebase", "Error getting data", databaseError.toException());
                            }
                        });
                    }
                }
            });

        } else {
            // Request permission.
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest, String apiKey) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Key
        String key = "key=" + apiKey;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + key;
        // Output format
        String output = "json";

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


    }

    private void fetchDirections(String url) {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parsing the response
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray routes = jsonResponse.getJSONArray("routes");
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                            String encodedString = overviewPolyline.getString("points");
                            List<LatLng> list = PolyUtil.decode(encodedString);
                            mMap.addPolyline(new PolylineOptions().addAll(list));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(stringRequest);

        dialogs();

    }

    private void dialogs() {
        final Dialog dialog = new Dialog(ChooseRideActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.buttom_sheet);

         LinearLayout mCompact;
         LinearLayout mFamily;
         LinearLayout mPremium;
        mCompact = dialog.findViewById  (R.id.Compact);
        mFamily = dialog.findViewById  (R.id.Family);
        mPremium = dialog.findViewById  (R.id.Premium);

        mCompact.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseRideActivity.this,SelectTaxiActivity.class);
            startActivity(intent);
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setCancelable(true);
    }


    private void initView() {

        mBooknow = findViewById  (R.id.booknow);

    }
}