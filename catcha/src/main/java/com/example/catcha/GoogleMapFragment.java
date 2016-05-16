package com.example.catcha;


import android.Manifest;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.catcha.provider.Departure;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoogleMapFragment extends Fragment implements OnMapReadyCallback,
        FragmentCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = GoogleMapFragment.class.getSimpleName();

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;

    private ScheduledFuture updateFuture;

    @BindView(R.id.mapview)
    MapView mapView;
    private GoogleMap googleMap;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location currentLocation;

    private List<Departure> departures;

    private List<LatLng> latLngList;
    private List<Marker> markerList;

    private boolean firstView = false;

    public GoogleMapFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        }

        buildGoogleApiClient();
        departures = new ArrayList<>();

        getLoaderManager().initLoader(0, null, this);

        latLngList = new ArrayList<>();
        markerList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        firstView = true;

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        enableMyLocation();
        scheduleMapUpdate();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && googleMap != null) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    private void scheduleMapUpdate() {
        if (updateFuture == null) {
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            long currentTimeMillis = System.currentTimeMillis();
            long secondsUntilNextMinute = ((60 * 1000 + currentTimeMillis + 59999) / 60000 * 60000 - (60 * 1000 + currentTimeMillis)) / 1000;
            updateFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            checkAndEnableMyLocation();
                            drawDepartureMarkers();
                        }
                    });
                }
            }, secondsUntilNextMinute, 60, TimeUnit.SECONDS);
        }
    }

    private void checkAndEnableMyLocation() {
        if (!googleMap.isMyLocationEnabled()) {
            enableMyLocation();
        }
    }

    private Bitmap createMarkerBitmap() {
        int timeWidth = getResources().getDimensionPixelSize(R.dimen.departure_marker_time_width);
        int destinationWidth = getResources().getDimensionPixelSize(R.dimen.departure_marker_destination_width);
        int height = getResources().getDimensionPixelSize(R.dimen.departure_marker_height);
        Bitmap.Config config = Bitmap.Config.ARGB_4444;
        Drawable timeShape = getResources().getDrawable(R.drawable.departure_marker_time);
        Drawable destinationShape = getResources().getDrawable(R.drawable.departure_marker_destination);
        Bitmap bitmap = Bitmap.createBitmap(timeWidth + destinationWidth, height + 25, config);
        timeShape.setBounds(0, 0, bitmap.getWidth() - destinationWidth, height);
        destinationShape.setBounds(timeWidth, 0, bitmap.getWidth(), height);

        Canvas canvas = new Canvas(bitmap);

        timeShape.draw(canvas);
        destinationShape.draw(canvas);

        return bitmap;
    }

    private void drawDepartureMarkers() {

        Paint timeColor = new Paint();
        timeColor.setTextSize(getResources().getDimension(R.dimen.departure_marker_font_size));
        timeColor.setColor(getResources().getColor(R.color.colorPrimary));

        Paint destinationColor = new Paint();
        destinationColor.setTextSize(getResources().getDimension(R.dimen.departure_marker_font_size));
        destinationColor.setColor(getResources().getColor(R.color.white));

        Paint triangleColor = new Paint();
        triangleColor.setTextSize(50);
        triangleColor.setColor(getResources().getColor(R.color.colorPrimary));
        triangleColor.setTextAlign(Paint.Align.CENTER);

        latLngList.clear();

        for (Marker m : markerList) {
            m.remove();
        }

        int timeWidth = getResources().getDimensionPixelSize(R.dimen.departure_marker_time_width);
        int height = getResources().getDimensionPixelSize(R.dimen.departure_marker_height);

        for (Departure d : departures) {
            Bitmap bitmap = createMarkerBitmap();
            Canvas canvas = new Canvas(bitmap);

            canvas.drawText(d.getDepartureTime1AsFormattedString(), 20, textCenterVerticalPos(height, timeColor), timeColor);
            canvas.drawText(String.valueOf(d.destBp), 20 + timeWidth, textCenterVerticalPos(height, destinationColor), destinationColor);
            canvas.drawText(getResources().getString(R.string.map_marker_triangle), bitmap.getWidth() / 2, height + 28, triangleColor);

            com.example.catcha.provider.Location location = com.example.catcha.provider.Location.getLocation(getActivity().getContentResolver(), d.locationId);
            if (location != null) {
                LatLng markerLocation = location.getLatLng();
                latLngList.add(markerLocation);

                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(markerLocation)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .anchor(0.5f, 1.25f));
                markerList.add(marker);

                bitmap.recycle();
            }
        }
    }

    private int textCenterVerticalPos(int height, Paint textPaint) {
        return (int) ((height / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableMyLocation();
            startLocationUpdates();
        } else {
            permissionDenied = true;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog.newInstance(true).show(getFragmentManager(), "dialog");
    }

    private void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
        if (permissionDenied) {
            showMissingPermissionError();
            permissionDenied = false;
        }
        mapView.onResume();

        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }

        super.onResume();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (googleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updateFuture != null) {
            updateFuture.cancel(true);
            updateFuture = null;
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");

        if (currentLocation == null) {

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            }
        }
        startLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;

        if (firstView) {

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(getLatLngBounds(), 300);
            googleMap.moveCamera(cameraUpdate);

            float currentZoom = googleMap.getCameraPosition().zoom;
            if (currentZoom > 16) {
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(16));
            }

            firstView = false;
        }
    }

    private LatLngBounds getLatLngBounds() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : latLngList) {
            builder.include(latLng);
            builder.include(mirrorLatLngAtCurrentPosition(latLng));
        }
        builder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));

        return builder.build();
    }

    private LatLng mirrorLatLngAtCurrentPosition(LatLng latLng) {
        double currentLatitude = currentLocation.getLatitude();
        double currentLongitude = currentLocation.getLongitude();

        double deltaLat = currentLatitude - latLng.latitude;
        double deltaLon = currentLongitude - latLng.longitude;

        return new LatLng(currentLatitude + deltaLat, currentLongitude + deltaLon);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    /**
     * Loading Departures
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return Departure.getDeparturesCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        while (cursor.moveToNext()) {
            departures.add(new Departure(cursor));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        departures.clear();
    }
}
