package police2.com.crimewatchers.views;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import okhttp3.ResponseBody;
import police2.com.crimewatchers.R;
import police2.com.crimewatchers.api.ApiClient;
import police2.com.crimewatchers.api.ApiInterface;
import police2.com.crimewatchers.models.Ticket;
import police2.com.crimewatchers.models.Victim;
import police2.com.crimewatchers.service.MyService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * An activity that displays a map showing places around the device's current location.
 */
public class MainActivity extends BaseActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int DEFAULT_ZOOM = 16;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean mLocationPermissionGranted;
    private Location mCurrentLocation;
    private Switch toggleStatus;
    private DatabaseReference mDatabase;
    private DatabaseReference mAlertReference;
    private FirebaseUser firebaseUser;
    private ValueEventListener mAlertListener;
    TextView tv_type, name, age;
    RelativeLayout relativeLayout;
    ImageView image;
    boolean flag = true;
    private LinearLayout viewInflate;
    private Button btn_assist, btn_navigate, btn_contact, btn_deny, btn_confirm;
    private View two_button, three_button;
    private boolean firstFlag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_maps);
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAlertReference = mDatabase.child("activeTask").child(getUid());

        relativeLayout = (RelativeLayout) findViewById(R.id.rl_task);
        tv_type = (TextView) relativeLayout.findViewById(R.id.tv_type);
        name = (TextView) relativeLayout.findViewById(R.id.name);
        age = (TextView) relativeLayout.findViewById(R.id.age);
        image = (ImageView) relativeLayout.findViewById(R.id.image);
        viewInflate = (LinearLayout) relativeLayout.findViewById(R.id.linearLayout);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        three_button = inflater.inflate(R.layout.three_button, null);
        two_button = inflater.inflate(R.layout.two_button, null);

        btn_assist = (Button) three_button.findViewById(R.id.btn_assist);
        btn_contact = (Button) three_button.findViewById(R.id.btn_contact);
        btn_navigate = (Button) three_button.findViewById(R.id.btn_navigate);
        btn_deny = (Button) two_button.findViewById(R.id.btn_deny);
        btn_confirm = (Button) two_button.findViewById(R.id.btn_confirm);


        btn_assist.setOnClickListener(this);
        btn_contact.setOnClickListener(this);
        btn_navigate.setOnClickListener(this);
        btn_deny.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);


        firstFlag = false;
        mAlertReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (firstFlag) {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.ic_menu_gallery);
                    builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                    Uri alarmSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification);
                    builder.setSound(alarmSound);
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(100, builder.build());
                }
                firstFlag = true;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_confirm:
                mAlertReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> children = dataSnapshot.getChildren().iterator();
                        String ticketID = children.next().getKey();
                        mAlertReference.child(ticketID).child("confirm").setValue(1);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;

            case R.id.btn_deny:

                mAlertReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> children = dataSnapshot.getChildren().iterator();
                        String ticketID = children.next().getKey();
                        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                        Call<ResponseBody> call = apiService.reassignTask(getResources().getString(R.string.API_KEY), ticketID, getUid());
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
            case R.id.btn_navigate:
                mAlertReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> children = dataSnapshot.getChildren().iterator();
                        Ticket ticket = children.next().getValue(Ticket.class);

                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + ticket.getLatitude() + "," + ticket.getLongitude());
                        // Uri gmmIntentUri = Uri.parse("google.navigation:q=12.899098, 77.658145");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;

            case R.id.btn_contact:
                mAlertReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Iterator<DataSnapshot> children = dataSnapshot.getChildren().iterator();
                        Ticket ticket = children.next().getValue(Ticket.class);
                        mDatabase.child("users").child(ticket.getUserid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Victim victim = dataSnapshot.getValue(Victim.class);
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + victim.getMobile()));
                                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                startActivity(callIntent);
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

                break;

            case R.id.btn_assist:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Connecting to nearest control room");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();


                break;

            default:
                Toast.makeText(this, "something", Toast.LENGTH_SHORT).show();
                break;

        }


    }


    @Override
    public void onConnected(Bundle connectionHint) {
        getDeviceLocation();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }


    @Override
    public void onMapReady(GoogleMap map) {
        Log.i(TAG, "onMapReady: ");
        mMap = map;
        updateLocationUI();
        if (mCurrentLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude()), DEFAULT_ZOOM));
        }


        ValueEventListener alertListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: ");
                if (dataSnapshot.hasChildren()) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    Iterator<DataSnapshot> children = dataSnapshot.getChildren().iterator();
                    Ticket ticket = children.next().getValue(Ticket.class);
                    tv_type.setText(ticket.getType());
                    if (ticket.getConfirm() == 0) {
                        viewInflate.removeAllViews();
                        viewInflate.addView(two_button);
                    } else {
                        viewInflate.removeAllViews();
                        viewInflate.addView(three_button);
                    }

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.valueOf(ticket.getLatitude()), Double.valueOf(ticket.getLongitude())))
                            .title("Victim Location"));
                    mDatabase.child("users").child(ticket.getUserid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Victim victim = dataSnapshot.getValue(Victim.class);
                            name.setText(victim.getName());
                            age.setText(victim.getGender());
                            Glide.with(getActivity())
                                    .load(victim.getPhotoUrl())
                                    .asBitmap()
                                    .placeholder(R.drawable.ic_face_black_24dp)
                                    .into(new BitmapImageViewTarget(image) {
                                        @Override
                                        protected void setResource(Bitmap resource) {
                                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                                            circularBitmapDrawable.setCircular(true);
                                            image.setImageDrawable(circularBitmapDrawable);
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    relativeLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("vikrant", "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(MainActivity.this, "Failed to load post.", Toast.LENGTH_SHORT).show();
            }
        };
        mAlertReference.addValueEventListener(alertListener);
        mAlertListener = alertListener;

    }


    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        CameraPosition camPos;
        if (mMap == null) {
            return;
        }
        if (flag) {
            camPos = CameraPosition.builder(mMap.getCameraPosition())
                    .target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                    .bearing(location.getBearing())
                    .zoom(DEFAULT_ZOOM)
                    .build();
            flag = false;
        } else {
            camPos = CameraPosition.builder(mMap.getCameraPosition())
                    .target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                    .bearing(location.getBearing())
                    .build();
        }
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (mLocationPermissionGranted) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                showGPSDisabledAlertToUser();
            }
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    } else {
                        showGPSDisabledAlertToUser();
                    }

                }
            }
        }
        updateLocationUI();
    }

    @SuppressWarnings("MissingPermission")
    private void updateLocationUI() {
        Log.i(TAG, "updateLocationUI: ");
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            Log.i(TAG, "updateLocationUI: " + "location enabled");
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mCurrentLocation = null;
        }
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        toggleStatus = (Switch) menu.findItem(R.id.myswitch)
                .getActionView().findViewById(R.id.actionbar_service_toggle);

        mDatabase.child("police").child(getUid()).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer status = dataSnapshot.getValue(Integer.class);
                if (status == 1) {
                    toggleStatus.setChecked(true);
                    startService(new Intent(MainActivity.this, MyService.class));
                    updateStatus("online");
                } else {
                    toggleStatus.setChecked(false);
                    stopService(new Intent(MainActivity.this, MyService.class));
                    updateStatus("offline");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        toggleStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mDatabase.child("police").child(getUid()).child("status").setValue(1);
                    startService(new Intent(MainActivity.this, MyService.class));
                    updateStatus("online");
                } else {
                    mDatabase.child("police").child(getUid()).child("status").setValue(0);
                    stopService(new Intent(MainActivity.this, MyService.class));
                    updateStatus("offline");
                }
            }
        });
        return true;
    }

    private void updateStatus(String status) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiService.setStatus(getResources().getString(R.string.API_KEY), getUid(), status);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        if (mAlertListener != null) {
            mAlertReference.removeEventListener(mAlertListener);
        }
    }


}