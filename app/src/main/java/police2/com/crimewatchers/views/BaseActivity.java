package police2.com.crimewatchers.views;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import police2.com.crimewatchers.R;
import police2.com.crimewatchers.api.ApiClient;
import police2.com.crimewatchers.api.ApiInterface;
import police2.com.crimewatchers.models.User;
import police2.com.crimewatchers.service.MyService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    View navHeader;
    private DatabaseReference mUserReference;
    private ValueEventListener mUserListener;
    private TextView tvUsername;
    private TextView tvEmail;
    private ImageView imageView, img_badge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }*/

        setContentView(getLayoutId());

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mUserReference = FirebaseDatabase.getInstance().getReference()
                .child("police").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }


    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.inflateMenu(R.menu.activity_main_drawer);
        navHeader = navigationView.inflateHeaderView(R.layout.nav_header_main);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                int id = menuItem.getItemId();
                switch (id) {

                    case R.id.nav_camer:
                        mUserReference.child("status").setValue(0);
                        stopService(new Intent(getActivity(), MyService.class));
                        updateStatus("offline");
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        finish();
                        break;

                    case R.id.nav_slideshow:
                        Intent webintent1 = new Intent(getActivity(), WebActivity.class);
                        webintent1.putExtra("TITLE", "Contact List");
                        webintent1.putExtra("URL", "http://www.wikihow.com/Handle-an-Emergency-Situation");
                        startActivity(webintent1);
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNavHeader();
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

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void updateNavHeader() {
        tvUsername = (TextView) navHeader.findViewById(R.id.tv_username);
        tvEmail = (TextView) navHeader.findViewById(R.id.tv_email);
        imageView = (ImageView) navHeader.findViewById(R.id.imageView);
        img_badge = (ImageView) navHeader.findViewById(R.id.img_badge);
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                tvUsername.setText(user.getUsername());
                tvEmail.setText(user.getRank());
                Glide.with(getActivity())
                        .load(user.getPhotoUrl())
                        .asBitmap()
                        .placeholder(R.mipmap.ic_launcher)
                        .into(new BitmapImageViewTarget(imageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                imageView.setImageDrawable(circularBitmapDrawable);
                            }
                        });

                Glide.with(getActivity())
                        .load("http://35.154.87.55/images/ranks/6.png")
                        .fitCenter()
                        .into(img_badge);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("vikrant", "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(getActivity(), "Failed to load post.", Toast.LENGTH_SHORT).show();
            }
        };
        mUserReference.addValueEventListener(userListener);
        mUserListener = userListener;
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mUserListener != null) {
            mUserReference.removeEventListener(mUserListener);
        }
    }

    protected abstract int getLayoutId();

    protected abstract Activity getActivity();


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}