package com.sublimeIT.hisabkhata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.sublimeIT.hisabkhata.Adapter.CustomerAdapter;
import com.sublimeIT.hisabkhata.Customer.AddCustomer;
import com.sublimeIT.hisabkhata.Model.Customer;
import com.sublimeIT.hisabkhata.Model.User;
import com.sublimeIT.hisabkhata.Register.LoginPhone;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    private InterstitialAd mInterstitialAd;

    CircleImageView ownerImage;

    Button addcustomer;

    RecyclerView recyclerView;
    private CustomerAdapter customerAdapter;
    private List<Customer> customerList;
    EditText search_bar;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                loadAd();
            }
        });

        addcustomer=findViewById(R.id.addCustomer);
        search_bar=findViewById(R.id.search_bar);
        ownerImage=findViewById(R.id.ownerImage);

        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        toolbar=findViewById(R.id.toolbar);

        //drawer navigation
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_open,R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //
        Bundle intent=getIntent().getExtras();
        if (intent!=null){
            String shopOwner=intent.getString("shopOwner");
            SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
            editor.putString("customerId",shopOwner);
            editor.apply();
        }
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        customerList=new ArrayList<>();
        customerAdapter=new CustomerAdapter(this,customerList);
        recyclerView.setAdapter(customerAdapter);

        uploadHeader();
        showUser();
        showCustomer();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                searchUser(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addcustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddCustomer.class));
            }
        });

    }

    private void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3396034573752814/9165025767", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                // Log.i(TAG, "onAdLoaded");

                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("TAG", "The ad was dismissed.");
                        finish();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                // Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });
    }

    @Override
    public void finish() {

        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
            super.finish();
        }

    }

    private void showUser() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                User user=snapshot.getValue(User.class);
                if (user!=null){
                    if (user.getUserName()==null){
                        startActivity(new Intent(MainActivity.this,SetUserInfoActivity.class));
                    }

                    //loading profile pic
                    String imageuri =user.getImageurl();
                    Picasso.get().load(imageuri).into(ownerImage);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUser(String s){
        Query query= FirebaseDatabase.getInstance().getReference("Customers").orderByChild("customerName")
                .startAt(s).endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                customerList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Customer customer=dataSnapshot.getValue(Customer.class);
                    if (customer.getShopOwner().equals(firebaseUser.getUid()))
                        customerList.add(customer);
                }
                customerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showCustomer(){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Customers");

        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (search_bar.getText().toString().equals("")) {
                    customerList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Customer customer = dataSnapshot.getValue(Customer.class);
                        if (customer.getShopOwner().equals(firebaseUser.getUid()))
                            customerList.add(customer);
                    }
                    customerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_home:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.nav_profile:
                startActivity(new Intent(MainActivity.this, UserProfile.class));
                finish();
                break;
            case R.id.nav_newCustomer:
                startActivity(new Intent(MainActivity.this, AddCustomer.class));
                finish();
                break;
            case R.id.nav_logOut:
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(MainActivity .this, LoginPhone.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.nav_share:

                Intent intentt=new Intent(Intent.ACTION_SEND);
                intentt.setType("text/plain");
                String shareBoody="Download "+getString(R.string.app_name)
                        +" app from play store\n"+
                        "https://play.google.com/store/apps/details?id="
                        +BuildConfig.APPLICATION_ID+"\n";
                intentt.putExtra(Intent.EXTRA_TEXT,shareBoody);
                startActivity(Intent.createChooser(intentt,"Share by"));
                break;
        }
        return true;
    }

    public void uploadHeader(){
        View headerView=navigationView.getHeaderView(0);
        TextView Nav_userName,Nav_userPhone;
        CircleImageView ownerImage;

        //init
        Nav_userName=headerView.findViewById(R.id.Nav_userName);
        Nav_userPhone=headerView.findViewById(R.id.Nav_userPhone);
        ownerImage=headerView.findViewById(R.id.ownerImage);

        //setData
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                User user=snapshot.getValue(User.class);
                if (user!=null){

                    Nav_userName.setText(user.getUserName());
                    Nav_userPhone.setText(user.getUserPhone());
                    //loading profile pic
                    String imageuri =user.getImageurl();
                    Picasso.get().load(imageuri).into(ownerImage);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}