package com.example.android.wastemanagement;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.wastemanagement.Models.Bandwidth;
import com.example.android.wastemanagement.Models.Industry;
import com.example.android.wastemanagement.Models.Ngo;
import com.example.android.wastemanagement.Models.User;
import com.example.android.wastemanagement.Models.Volunteer;
import com.example.android.wastemanagement.Models.Zone;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity implements OnMapReadyCallback {

    android.support.v7.widget.Toolbar toolbar;
    public boolean closeview = false;
    private FirebaseAuth auth;
    private FirebaseUser userF;
    private TextView userName, userEmail;
    DatabaseReference dbuser, dbtoken;
    ImageView userImg;
    String userType, userCity, userCardinal;
    private GoogleMap mMap;
    LocationManager locationManager;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    Marker marker;
    LocationListener locationListener;
    Button donate, submitDonation, donorQR;
    LinearLayout donationView;
    ImageView aclothes,agrains,apacked,astationary,afurniture,aelectronic;
    ImageView mclothes,mgrains,mpacked,mstationary,mfurniture,melectronic;
    TextView qclothes,qgrains,qpacked,qstationary,qfurniture,qelectronic;
    TextView fromDate,fromTime;
    ImageView cancel;
    DatePickerDialog.OnDateSetListener fromDatepicker;
    Calendar myCalendar = Calendar.getInstance();
    String lat, lng, category;
    LatLng dangerous_area[] = new LatLng[20];
    GeoQuery geoQuery;
    GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.feature_req_toolbar);
        toolbar.setTitle("Home");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        donate = findViewById(R.id.donate);
        submitDonation = findViewById(R.id.submitDonation);
        donationView = findViewById(R.id.donation_view);
        donorQR = findViewById(R.id.donorQRgenr);
        aclothes = findViewById(R.id.add_clothes);agrains = findViewById(R.id.add_grains);
        apacked = findViewById(R.id.add_packed);astationary = findViewById(R.id.add_stationary);
        afurniture = findViewById(R.id.add_furniture);aelectronic = findViewById(R.id.add_electronic);
        mclothes = findViewById(R.id.minus_clothes);mgrains = findViewById(R.id.minus_grains);
        mpacked = findViewById(R.id.minus_packed);mstationary = findViewById(R.id.minus_stationary);
        mfurniture = findViewById(R.id.minus_furniture);melectronic = findViewById(R.id.minus_electronic);
        qclothes = findViewById(R.id.qclothes);qgrains = findViewById(R.id.qgrains);
        qpacked = findViewById(R.id.qpacked);qstationary = findViewById(R.id.qstationary);
        qfurniture = findViewById(R.id.qfurniture);qelectronic = findViewById(R.id.qelectronic);

        fromDate = findViewById(R.id.fromDate);
        fromTime = findViewById(R.id.fromTime);
        cancel = findViewById(R.id.create_post_cancel);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        auth = FirebaseAuth.getInstance();

        //adding quantity
        aclothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = Integer.parseInt(qclothes.getText().toString())+1;
                qclothes.setText(String.valueOf(v));
            }
        });
        agrains.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = Integer.parseInt(qgrains.getText().toString())+1;
                qgrains.setText(String.valueOf(v));
            }
        });
        apacked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = Integer.parseInt(qpacked.getText().toString())+1;
                qpacked.setText(String.valueOf(v));
            }
        });
        astationary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = Integer.parseInt(qstationary.getText().toString())+1;
                qstationary.setText(String.valueOf(v));
            }
        });
        afurniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = Integer.parseInt(qfurniture.getText().toString())+1;
                qfurniture.setText(String.valueOf(v));
            }
        });
        aelectronic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = Integer.parseInt(qelectronic.getText().toString())+1;
                qelectronic.setText(String.valueOf(v));
            }
        });

        //removing quantity
        mclothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = Integer.parseInt(qclothes.getText().toString())-1;
                qclothes.setText(String.valueOf(v));
            }
        });
        mgrains.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = Integer.parseInt(qgrains.getText().toString())-1;
                qgrains.setText(String.valueOf(v));
            }
        });
        mpacked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = Integer.parseInt(qpacked.getText().toString())-1;
                qpacked.setText(String.valueOf(v));
            }
        });
        mstationary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = Integer.parseInt(qstationary.getText().toString())-1;
                qstationary.setText(String.valueOf(v));
            }
        });
        mfurniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = Integer.parseInt(qfurniture.getText().toString())-1;
                qfurniture.setText(String.valueOf(v));
            }
        });
        melectronic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = Integer.parseInt(qelectronic.getText().toString())-1;
                qelectronic.setText(String.valueOf(v));
            }
        });

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(Home.this,fromDatepicker, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        fromDatepicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                fromDate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        fromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Home.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        fromTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               donationView.setVisibility(View.GONE);
               donate.setVisibility(View.VISIBLE);
            }
        });
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                donationView.setVisibility(View.VISIBLE);
                donate.setVisibility(View.GONE);
                category = "ngo";
            }
        });
        submitDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DB store code
                long vclothes = Long.valueOf(qclothes.getText().toString());
                long vgrains = Long.valueOf(qgrains.getText().toString());
                long vpacked = Long.valueOf(qpacked.getText().toString());
                long vstationary = Long.valueOf(qstationary.getText().toString());
                long vfurniture = Long.valueOf(qfurniture.getText().toString());
                long velectronic = Long.valueOf(qelectronic.getText().toString());
                if(vclothes < 0 || vgrains < 0 || vpacked < 0 || vstationary < 0 || vfurniture < 0 || velectronic < 0 ){
                    Toast.makeText(Home.this, "Cannot accept negative quantity", Toast.LENGTH_SHORT).show();
                }else{
                    Zone zone = new Zone(auth.getUid(), lat, lng, category, userCity, userCardinal,
                            fromDate.getText().toString(), fromTime.getText().toString(),
                            new Bandwidth(vclothes,vpacked,vgrains,vstationary,0,vfurniture,velectronic));
                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Zones");
                    String ZoneKey = dbref.push().getKey();
                    dbref.child(ZoneKey).setValue(zone);
                    Toast.makeText(Home.this, "Items added successfully", Toast.LENGTH_SHORT).show();
                    donationView.setVisibility(View.GONE);
                    donorQR.setVisibility(View.VISIBLE);
                }
            }
        });


        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawer.closeDrawers();

                        if(menuItem.getItemId() == R.id.logout){
                            /*dbtoken = FirebaseDatabase.getInstance().getReference().child("user_details")
                                    .child(auth.getUid()).child("token");
                            dbtoken.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        dataSnapshot.getRef().removeValue();
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });*/
                            auth.signOut();
                            userF = FirebaseAuth.getInstance().getCurrentUser();
                            if (userF == null) {
                                // user auth state is changed - user is null
                                // launch login activity
                                Intent intent = new Intent(Home.this, Login.class);
                                startActivity(intent);
                                finish();
                            }
                            return true;
                        }

                        if(menuItem.getItemId() == R.id.accSetting){
                            Intent intent = new Intent(Home.this, AccountSetting.class);
                            startActivity(intent);
                        }
                        if(menuItem.getItemId() == R.id.approve_volunteers){
                            Intent intent = new Intent(Home.this, ApproveVolunteer.class);
                            intent.putExtra("userType", userType);
                            startActivity(intent);
                        }
                        if(menuItem.getItemId() == R.id.wall){
                            Intent intent = new Intent(Home.this, Wall.class);
                            intent.putExtra("userType", userType);
                            startActivity(intent);
                        }
                        if(menuItem.getItemId() == R.id.map){
                            Intent intent = new Intent(Home.this, MapsActivity.class);
                            startActivity(intent);
                        }
                        if(menuItem.getItemId() == R.id.scan){
                            Intent intent = new Intent(Home.this, Scan.class);
                            startActivity(intent);
                        }

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        navigationView.setItemIconTintList(null);
        userName = navigationView.getHeaderView(0).findViewById(R.id.head_name);
        userEmail = navigationView.getHeaderView(0).findViewById(R.id.head_email);
        userImg = navigationView.getHeaderView(0).findViewById(R.id.user_image);

        Log.d("auth_id",auth.getUid());
        dbuser = FirebaseDatabase.getInstance().getReference().child("user_details").child(auth.getUid());
        dbuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userType = dataSnapshot.getValue(String.class);
                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child(userType).child(auth.getUid());
                dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {
                        if(userType.equals("donor")){
                            User user = dataSnapshot1.getValue(User.class);
                            userName.setText(user.getName());
                            userEmail.setText(user.getEmail());
                            userCity = user.getUserCity();
                            userCardinal = user.getUserCardinality();
                            if(!user.getUserImgUrl().equals("no")){
                                Glide.with(getApplicationContext()).load(user.getUserImgUrl()).into(userImg);
                            }
                            if(user.getReg_status()==0){
                                //open applyAsDonor form
                                Intent intent = new Intent(Home.this, ApplyAsDonor.class);
                                startActivity(intent);
                            }
                        }else if(userType.equals("volunteer")){
                            donate.setVisibility(View.GONE);
                            Volunteer user = dataSnapshot1.getValue(Volunteer.class);
                            userName.setText(user.getName());
                            userEmail.setText(user.getVolunteerEmail());
                            if(!user.getUserImgUrl().equals("no")){
                                Glide.with(getApplicationContext()).load(user.getUserImgUrl()).into(userImg);
                            }
                            if(user.getReg_status()==0){
                                //open applyAsVolunteer form
                                Intent intent = new Intent(Home.this, ApplyAsVolunteer.class);
                                startActivity(intent);
                            }
                        }else if(userType.equals("ngo")){
                            donate.setVisibility(View.GONE);
                            Ngo user = dataSnapshot1.getValue(Ngo.class);
                            userName.setText(user.getName());
                            userEmail.setText(user.getNgoEmail());
                            if(!user.getUserImgUrl().equals("no")){
                                Glide.with(getApplicationContext()).load(user.getUserImgUrl()).into(userImg);
                            }
                            if(user.getReg_status()==0){
                                //open applyAsNgo form
                                Intent intent = new Intent(Home.this, ApplyAsNgo.class);
                                startActivity(intent);
                            }else{
                                navigationView.getMenu().findItem(R.id.approve_volunteers).setVisible(true);
                            }
                        }else if(userType.equals("industry")){
                            donate.setVisibility(View.GONE);
                            Industry user = dataSnapshot1.getValue(Industry.class);
                            userName.setText(user.getName());
                            userEmail.setText(user.getIndustryEmail());
                            if(!user.getUserImgUrl().equals("no")){
                                Glide.with(getApplicationContext()).load(user.getUserImgUrl()).into(userImg);
                            }
                            if(user.getReg_status()==0){
                                //open applyAsIndustry form
                            }else{
                                navigationView.getMenu().findItem(R.id.approve_volunteers).setVisible(true);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        loadFirstMap();
        if(userType.equals("volunteer")){
            loadGeoFence();
        }
    }

    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else if (closeview) {
            closeview = false;
            finish();
            startActivity(getIntent());

        } else {
            super.onBackPressed();
        }
    }

    public void closeview(Boolean value) {
        closeview = value;
    }

    public void loadGeoFence(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Zones");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int i = 0;

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    Zone zone = ds.getValue(Zone.class);
                    double latZone = Double.parseDouble(zone.getZoneLat().toString().trim());
                    double lonZone = Double.parseDouble(zone.getZoneLong().toString().trim());
                    //String zoneId = zone.getZoneID().toString().trim();
                    //String dangerType = zone.getZoneTitle().toString().trim();

                    int c=0;

                    dangerous_area[i] = new LatLng(latZone, lonZone);

                    mMap.addCircle(new CircleOptions()
                            .center(dangerous_area[i])
                            .radius(75)
                            .strokeColor(c)
                            .fillColor(0x220000ff)
                            .strokeWidth(5.0f));

                    /*geoFire.setLocation(zoneId, new GeoLocation(latZone, lonZone), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {

                        }
                    });*/
                    i++;
                }
                /*geoQuery = geoFire.queryAtLocation(new GeoLocation(latti,longi),0.075);
                Log.d("Before_geofire", geoQuery.toString());

                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {


                    public void onKeyEntered(String key, GeoLocation location) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            sendNotification("DangerZone -"+key,String.format("%s Entered into the ZoneArea",key));
                        }
                    }


                    public void onKeyExited(String key) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            sendNotification("DangerZone",String.format("%s Exited from the ZoneArea",key));
                        }

                    }


                    public void onKeyMoved(String key, GeoLocation location) {
                        Log.d("MOVE",String.format("%s Moving within the dangerous area[%f/%f]",key,location.latitude,location.longitude));
                    }

                    public void onGeoQueryReady() {
                    }

                    public void onGeoQueryError(DatabaseError error) {
                        Log.e("Error","check:"+error);
                    }
                });
                Log.d("After_geofire", geoQuery.toString());*/
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void loadFirstMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                lat = String.valueOf(latitude);
                lng = String.valueOf(longitude);
                //get the location name from latitude and longitude
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> addresses =
                            geocoder.getFromLocation(latitude, longitude, 1);
                    String result = addresses.get(0).getLocality()+":";
                    result += addresses.get(0).getCountryName();
                    //Toast.makeText(Home.this, "result is ="+result, Toast.LENGTH_LONG).show();
                    LatLng latLng = new LatLng(latitude, longitude);
                    if (marker != null){
                        marker.remove();
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result));
                        mMap.setMaxZoomPreference(20);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                    }
                    else{
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result));
                        mMap.setMaxZoomPreference(20);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
