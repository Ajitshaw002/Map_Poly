package com.example.ajit.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

	//AJIT SHAW
//Commit

    private GoogleMap mMap;
    private Location location;
    private GPSTracker gps;
    private double my_longitude = 0.0;
    private double my_latitude = 0.0;
    MarkerOptions markerOptions, markerOptionstwo;
    private ProgressBar progressBar;

    public GoogleMap googleMap1;
    private Button btn_go;
    public boolean keyValue;
    public boolean polyValue;
    public Marker marker;
    private ProgressBar progress;
    public LatLng currentlatlang, differentLatlng;
    HashMap<String, Marker> hashMapMarker = new HashMap<>();
    List<Polyline> polylines = new ArrayList<Polyline>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        HashMap<String, Marker> hashMapMarker = new HashMap<>();
        btn_go = (Button) findViewById(R.id.btn_go);
        btn_go.setOnClickListener(this);
        progress = (ProgressBar) findViewById(R.id.progress);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            } else {
                setCurrentMarker(mMap);

            }
        } else {
            setCurrentMarker(mMap);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (keyValue == false) {
                    setTouchMarker(latLng);
                } else {
                    Marker marker = hashMapMarker.get("key");
                    marker.remove();
                    hashMapMarker.remove("key");
                    setTouchMarker(latLng);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setCurrentMarker(mMap);
                } else {
                    Toast.makeText(MapsActivity.this, "Please give permisssion to set current marker", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void setCurrentMarker(GoogleMap map) {
        if (mMap == null)
            return;
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        gps = new GPSTracker(MapsActivity.this);
        if (gps.canGetLocation) {
            my_longitude = gps.getLongitude();
            my_latitude = gps.getLatitude();
            GPSTracker obj = new GPSTracker(this);
            String addres = obj.getAddressLine(this);

            currentlatlang = new LatLng(my_latitude, my_longitude);
            markerOptions = new MarkerOptions();
            markerOptions.position(currentlatlang);
            markerOptions.title(addres);
            mMap.addMarker(markerOptions);

            CameraUpdate move = CameraUpdateFactory.newLatLng(currentlatlang);
            mMap.moveCamera(move);

            CameraUpdate zoome = CameraUpdateFactory.zoomTo(16);
            mMap.animateCamera(zoome);
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(new LatLng(my_latitude, my_longitude));
            circleOptions.radius(500);
            circleOptions.strokeColor(getResources().getColor(R.color.lightBlue));
            circleOptions.fillColor(getResources().getColor(R.color.blue));
            mMap.addCircle(circleOptions);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            markerOptions.position(mMap.getCameraPosition().target);


        }

    }

    public void setTouchMarker(LatLng latLng) {

        markerOptionstwo = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.pngg));
        markerOptionstwo.position(latLng);
        markerOptionstwo.title(latLng.latitude + " :" + latLng.longitude);
        differentLatlng = new LatLng(latLng.latitude, latLng.longitude);
        marker = mMap.addMarker(markerOptionstwo);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        hashMapMarker.put("key", marker);
        keyValue = true;

    }

    public void setPolyline() {
        polyValue = true;
        DownloadTask downloadTask = new DownloadTask();
        String url = getDirectionsUrl(currentlatlang, differentLatlng);

// Start downloading json data from Google Directions API
        downloadTask.execute(url);
//        PolylineOptions pOptions = new PolylineOptions()
//                .add(currentlatlang)
//                .add(differentLatlng)
//                .width(10)
//                .color(Color.BLUE)
//                .geodesic(true);
//        polylines.add(mMap.addPolyline(pOptions));

        // mMap.addPolyline(pOptions);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_go:
                if (polyValue == false) {
                    setPolyline();
                } else {
                    for (Polyline line : polylines) {
                        line.remove();
                    }
                    polylines.clear();
                    setPolyline();
                }

                break;
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            // Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                    progress.setVisibility(View.INVISIBLE);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(getResources().getColor(R.color.bluees));
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions == null) {
                Toast.makeText(getApplicationContext(), "NO Location Received", Toast.LENGTH_SHORT).show();
            } else {
                polylines.add(mMap.addPolyline(lineOptions));
            }

        }
    }
}


