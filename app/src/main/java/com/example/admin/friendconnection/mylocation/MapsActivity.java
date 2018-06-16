package com.example.admin.friendconnection.mylocation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.admin.friendconnection.R;
import com.example.admin.friendconnection.login.LoginFragment;
import com.example.admin.friendconnection.object.Account;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, SearchView.OnQueryTextListener {

    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private MapFragment mapFragment;
    private String idMe;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private DatabaseReference databaseReference;
    private Button btnHome;
    private Button btnUpdate;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private Account account;
    private CircleImageView imgAva;
    private TextView txtName;
    private int mapType;
    public static final String MAPTYPE = "MapType";
    private SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);
        MenuItem itemSearch = menu.findItem(R.id.search_view);
        searchView = (SearchView) itemSearch.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        toolbar = findViewById(R.id.toolbarMap);
        drawerLayout = findViewById(R.id.drawerLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        sharedPreferences = getSharedPreferences(LoginFragment.FILE, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        idMe = sharedPreferences.getString(LoginFragment.ID, "");
        mapType = sharedPreferences.getInt(MAPTYPE, 1);

        btnHome = findViewById(R.id.btnHomeMap);
        btnUpdate = findViewById(R.id.btnUpdate);
        radioButton1 = findViewById(R.id.typeMap1);
        radioButton2 = findViewById(R.id.typeMap2);
        radioButton3 = findViewById(R.id.typeMap3);
        btnUpdate.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        imgAva = findViewById(R.id.imgAvaMyMap);
        txtName = findViewById(R.id.txtNameMyMap);
        radioButton1.setOnCheckedChangeListener(this);
        radioButton2.setOnCheckedChangeListener(this);
        radioButton3.setOnCheckedChangeListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Account");
        databaseReference.child(idMe).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                account = dataSnapshot.getValue(Account.class);
                Picasso.get().load(account.getLinkAvatar()).placeholder(R.drawable.user).error(R.drawable.user).into(imgAva);
                txtName.setText(account.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        initFragment();

        changeMapType();
    }

    private void changeMapType() {
        switch (mapType) {
            case 1:
                radioButton1.setChecked(true);
                break;
            case 2:
                radioButton2.setChecked(true);
                break;
            case 3:
                radioButton3.setChecked(true);
                break;
        }
    }

    private void initFragment() {
        mapFragment = new MapFragment(idMe);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frMap, mapFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnHomeMap) {
            this.finish();
        } else if (v.getId() == R.id.btnUpdate) {
            mapFragment.showMakerFriend();
            drawerLayout.closeDrawers();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.typeMap1 && isChecked) {
            mapFragment.changeMapStyle(1);
            mapType = 1;
        } else if (buttonView.getId() == R.id.typeMap2 && isChecked) {
            mapFragment.changeMapStyle(2);
            mapType = 2;
        } else if (buttonView.getId() == R.id.typeMap3 && isChecked) {
            mapFragment.changeMapStyle(3);
            mapType = 3;
        }
        drawerLayout.closeDrawers();
        editor.putInt(MAPTYPE, mapType);
        editor.commit();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mapFragment.searchMap(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_l, R.anim.slide_out_r);
    }
}
