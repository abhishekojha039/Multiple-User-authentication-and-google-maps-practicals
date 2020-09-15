package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    PlacesClient placesClient;
    SupportMapFragment mapFragment;
     FusedLocationProviderClient fusedLocationProviderClient;
     boolean trackingFlag;
     ListView lstvw;
     AutocompleteSupportFragment supportFragment;
     LocationCallback locationCallback;
     double currentLatitude,currentLongitude;
     public  static final int MAX=10;
     String mykey="AIzaSyCVJmdtagWVIliFMawGK1oLTuo-dwN49UY";
     String [] placeName,placeAddress;
     LatLng [] placeLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
         supportFragment=(AutocompleteSupportFragment) getSupportFragmentManager()
                 .findFragmentById(R.id.autocomplete_fragment);
         lstvw=findViewById(R.id.lstvw);
      //  mapFragment.getMapAsync(this);

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(trackingFlag)
                {
                    new LocationTaask().execute(locationResult.getLastLocation());
                }

            }
        };
        checkLocationPermission();
        if(!Places.isInitialized()){
            Places.initialize(this,mykey);
        }

         placesClient=Places.createClient(this);
        supportFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.NAME));
        supportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                if(place!=null)
                {
                    Toast.makeText(MapsActivity.this, ""+place.getName(), Toast.LENGTH_SHORT).show();
                    mMap.clear();
                    LatLng latLng=place.getLatLng();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,25f));

                    MarkerOptions markerPlace=new MarkerOptions();
                    markerPlace.position(latLng);
                    markerPlace.title("Searched Position");
                    mMap.addMarker(markerPlace);
                }
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });


    }
        private  void checkLocationPermission()
        {
         if(!trackingFlag)
         {
             startTrackingLocation();
         }
         else
         {
             stopTrackingLocation();
         }
        }

    private void startTrackingLocation()
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else
        {
            trackingFlag=true;
            Toast.makeText(this, "Location Permission is Granted", Toast.LENGTH_SHORT).show();
            fusedLocationProviderClient.requestLocationUpdates(getLocationReqeust(),locationCallback,null);
        }
    }
    private LocationRequest getLocationReqeust()
    {
        LocationRequest locationRequest= new LocationRequest();
        locationRequest.setInterval(50000);
        locationRequest.setFastestInterval(50000);
       // locationRequest.setExpirationDuration(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    private void stopTrackingLocation()
    {
      if(trackingFlag)
      {
          trackingFlag=false;
      }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            startTrackingLocation();
            else
            {
                Toast.makeText(this, "Permission Denied ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showPlaces(View view) {
       List<Place.Field> placeField=Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        FindCurrentPlaceRequest request=FindCurrentPlaceRequest.builder(placeField).build();
          if(trackingFlag) {
              @SuppressLint("MissingPermission")
              Task<FindCurrentPlaceResponse> responseTask = placesClient.findCurrentPlace(request);
              responseTask.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                  @Override
                  public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                     if(task.isSuccessful())
                     {
                         FindCurrentPlaceResponse placeResponse=task.getResult();
                         List<PlaceLikelihood> likelihoods=placeResponse.getPlaceLikelihoods();
                         int count;
                         if(likelihoods.size()<MAX)
                         {
                             count=likelihoods.size();
                         }
                         else
                         {
                             count=MAX;
                             placeName=new String[count];
                             placeAddress=new String[count];
                             placeLatLng=new LatLng[count];
                             int i=0;
                             for(PlaceLikelihood placeLikelihood:likelihoods)
                             {
                                 placeName[i]=placeLikelihood.getPlace().getName();
                                 placeAddress[i]=placeLikelihood.getPlace().getAddress();
                                 placeLatLng[i]=placeLikelihood.getPlace().getLatLng();
                                 i++;
                                 if(i==count)
                                 {
                                     break;
                                 }
                             }
                             fillPlaceDetails();
                         }
                     }
                     else
                     {
                         Toast.makeText(MapsActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                     }
                  }
              });
          }
    }

    private void fillPlaceDetails() {
        ArrayAdapter<String> adapter=new ArrayAdapter<>(MapsActivity.this,android.R.layout.simple_list_item_1,placeName);
        lstvw.setAdapter(adapter);
    }

    class LocationTaask extends AsyncTask<Location,Void,String>
    {
        @Override
        protected String doInBackground(Location... locations) {
            Location myLocation=locations[0];
            String msg="";
            if(myLocation!=null)
            {
                currentLatitude=myLocation.getLatitude();
                currentLongitude=myLocation.getLongitude();
                msg="done";
            }

            return msg;
        }

        @Override
        protected void onPostExecute(String s) {
           // super.onPostExecute(s);
            if(s.equals("done"))
            {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap=googleMap;
                        mMap.clear();
                        LatLng mylatlang=new LatLng(currentLatitude,currentLongitude);
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        mMap.setMyLocationEnabled(true);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylatlang,25f));
                        MarkerOptions currentMarker=new MarkerOptions();
                        currentMarker.position(mylatlang);
                        currentMarker.title("Current Position");
                        mMap.addMarker(currentMarker);
                    }
                });
            }
        }
    }


}