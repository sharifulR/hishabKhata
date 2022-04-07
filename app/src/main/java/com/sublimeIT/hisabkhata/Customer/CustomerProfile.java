package com.sublimeIT.hisabkhata.Customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.sublimeIT.hisabkhata.Adapter.DescriptionAdapter;
import com.sublimeIT.hisabkhata.Model.Customer;
import com.sublimeIT.hisabkhata.Model.Descripton;
import com.sublimeIT.hisabkhata.Model.PayedAmounts;
import com.sublimeIT.hisabkhata.Model.SaleProducts;
import com.sublimeIT.hisabkhata.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerProfile extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;

    Context context= CustomerProfile.this;

    protected static final int RESULT_SPEECH=1;
    protected static final int RESULT_SPEECHS=0;

    private static final int REQUEST_CALL=1;

    CircleImageView profileImageView;
    TextView customerName,phoneNo,date,mTotal;//,sale_sum,payed_sum;
    EditText product_ET,amount_ET;
    FloatingActionButton floatingActionButton;
    Button saleBTN,paidBTN;
    ImageButton speaker_tk,speaker_pName;

    private DatePickerDialog datePickerDialog;
    // RecyclerView
    private RecyclerView recyclerView;

    //FirebaseUser
    FirebaseUser firebaseUser;
    String customerId;
    String profileId;
    String shopOwner;

    // ProgressDialog
    ProgressDialog pd;


    private DescriptionAdapter descriptionAdapter;
    private List<Descripton> descriptonList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                loadAd();
            }
        });

        initial();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        //
        recyclerView=findViewById(R.id.recycler_view_cst_profile);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //
        descriptonList =new ArrayList<>();
        descriptionAdapter =new DescriptionAdapter(this, descriptonList);
        recyclerView.setAdapter(descriptionAdapter);

        //SMS permission
        //ActivityCompat.requestPermissions(CustomerProfile.this,new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);

        //
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        Intent intent=getIntent();
        customerId=intent.getStringExtra("customerId");
        shopOwner=intent.getStringExtra("shopOwner");

        SharedPreferences sharedPreferences=context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId=sharedPreferences.getString("profileId","none");


        DatabaseReference restReference= FirebaseDatabase.getInstance().getReference("SaleProduct").child(profileId);
        restReference.keepSynced(true);
        restReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int Sum=0;
                for (DataSnapshot ds: snapshot.getChildren()) {
                    SaleProducts saleProducts = ds.getValue(SaleProducts.class);
                    assert saleProducts != null;
                    String payed=String.valueOf(saleProducts.getSaleAmount());
                    int dValue = Integer.parseInt(payed);
                    Sum += dValue;

                    DatabaseReference restRef=FirebaseDatabase.getInstance().getReference("restAmount").child(customerId);
                    restRef.keepSynced(true);
                    Map<String,Object> Values=new HashMap<>();
                    Values.put("totalRest",Sum);
                    Values.put("customerId",customerId);
                    Values.put("shopOwner",firebaseUser.getUid());
//                    sale_sum.setText(String.valueOf(Sum+" tk"));
                    mTotal.setText(String.valueOf(Sum+" $"));

                    final int finalCount=Sum;
                    restRef.updateChildren(Values).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            upDateRemainingRest(finalCount);

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,ProfileImage.class));
            }
        });

        customerInfo();

        showDescription();

    }


    //ads loaded
    private void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        //adUnitId  ca-app-pub-6072533195292618/5473826257
        InterstitialAd.load(this,"ca-app-pub-6072533195292618/5473826257", adRequest, new InterstitialAdLoadCallback() {
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

    //ads finish
    @Override
    public void finish() {

        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
            super.finish();
        }

    }

    private void upDateRemainingRest(int finalCount) {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("PayedAmount").child(customerId);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int dSum = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PayedAmounts payedAmounts = ds.getValue(PayedAmounts.class);
                    assert payedAmounts != null;
                    String dpayed = String.valueOf(payedAmounts.getPayedAmount());
                    int dValue = Integer.parseInt(dpayed);
                    dSum += dValue;

                    int tDue = finalCount - dSum;
                    mTotal.setText(String.valueOf(tDue+" $"));
                    DatabaseReference debitReference = FirebaseDatabase.getInstance().getReference("TotalPayed").child(customerId);
                    Map<String, Object> dValues = new HashMap<>();
                    dValues.put("totalPayed", dSum);
                    dValues.put("customerId", customerId);
                    dValues.put("shopOwner", firebaseUser.getUid());


//                    payed_sum.setText(String.valueOf(dSum+" tk"));
                    debitReference.keepSynced(true);
                    debitReference.updateChildren(dValues).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDialog(){

        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        View view= LayoutInflater.from(this).inflate(R.layout.add_product,null);
        builder.setView(view);

        AlertDialog dialog=builder.create();
        dialog.show();

        product_ET=view.findViewById(R.id.product_ET);
        amount_ET=view.findViewById(R.id.amount_ET);
        date=view.findViewById(R.id.date);
        speaker_pName=view.findViewById(R.id.speaker_pName);
        speaker_tk=view.findViewById(R.id.speaker_tk);
        saleBTN=view.findViewById(R.id.addId);
        paidBTN=view.findViewById(R.id.subId);

        initDadePicker();

        date.setText(getTodaysDate());
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        // add sale product listener
        saleBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd=new ProgressDialog(context);
                pd.setMessage("Please wait...");
                pd.show();

                cCheckDataEntered();
            }

            boolean isEmpty(EditText text) {
                CharSequence str = text.getText().toString();
                return TextUtils.isEmpty(str);
            }
            boolean isFloat(EditText floats){
                CharSequence flt=floats.getText();
                return TextUtils.isDigitsOnly(flt) ;
            }
            void cCheckDataEntered() {

                if (isEmpty(product_ET)) {
                    pd.dismiss();
                    product_ET.setError("product name is required!");
                    dialog.show();
                } else if (isEmpty(amount_ET) || !isFloat(amount_ET)) {
                    pd.dismiss();
                    amount_ET.setError("Enter only number of digit");
                    dialog.show();
                }else {

                    addDescription();
                    saleProduct();
                    pd.dismiss();
                    dialog.dismiss();

                }
            }

        });

        // add paid amount listener
        paidBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd=new ProgressDialog(context);
                pd.setMessage("Please wait...");
                pd.show();

                dCheckDataEntered();
            }
            boolean isEmpty(EditText text) {
                CharSequence str = text.getText().toString();
                return TextUtils.isEmpty(str);
            }
            boolean isFloat(EditText floats){
                CharSequence flt=floats.getText();
                return TextUtils.isDigitsOnly(flt) ;
            }
            void dCheckDataEntered() {

                if (isEmpty(product_ET)) {
                    pd.dismiss();
                    product_ET.setError("product name is required!");
                    dialog.show();
                } else if (isEmpty(amount_ET) || !isFloat(amount_ET)) {
                    pd.dismiss();
                    amount_ET.setError("Enter only number of digit");
                    dialog.show();
                }else {

                    debitAccount();
                    addDescription();
                    pd.dismiss();
                    dialog.dismiss();

                }
            }
        });

        speaker_pName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");
                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    product_ET.setText("");
                }catch (ActivityNotFoundException e){
                    Toast.makeText(getApplicationContext(),"Your device doesn't support speech to text",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // add product price speak listener
        speaker_tk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                try {
                    startActivityForResult(intent, RESULT_SPEECHS);
                    amount_ET.setText("");
                }catch (ActivityNotFoundException e){
                    Toast.makeText(getApplicationContext(),"Your device doesn't support speech to text",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RESULT_SPEECH:
                if (resultCode==RESULT_OK && data !=null){
                    ArrayList<String>text=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    product_ET.setText(text.get(0));
                }break;
            case RESULT_SPEECHS:
                if (resultCode==RESULT_OK && data !=null){
                    ArrayList<String>texts=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    amount_ET.setText(texts.get(0));
                }break;

        }
    }

    private void addDescription() {
        DatabaseReference payedRef=FirebaseDatabase.getInstance().getReference("Description").child(customerId);
        HashMap<String,Object> payed=new HashMap<>();
        payed.put("date",date.getText().toString());
        payed.put("productName",product_ET.getText().toString());
        payed.put("Amount",amount_ET.getText().toString());
        payed.put("totalAmount",mTotal.getText().toString()+" $");
        payed.put("shopOwner",firebaseUser.getUid());

        payedRef.keepSynced(true);
        payedRef.push().setValue(payed);
    }

    private void saleProduct() {
        DatabaseReference saleRef=FirebaseDatabase.getInstance().getReference("SaleProduct").child(customerId);
        HashMap<String,Object> sale=new HashMap<>();
        sale.put("date",date.getText().toString());
        sale.put("productName",product_ET.getText().toString());
        sale.put("saleAmount",amount_ET.getText().toString());
        sale.put("totalAmount",mTotal.getText().toString()+" $");
        sale.put("shopOwner",firebaseUser.getUid());

        saleRef.keepSynced(true);
        saleRef.push().setValue(sale);
    }

    //call current date
    private String getTodaysDate() {
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        month=month+1;
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day,month,year);
    }

    private void initDadePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;
                String datee=makeDateString(day,month,year);
                date.setText(datee);
            }
        };

        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        int style= AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog=new DatePickerDialog(this,style,dateSetListener,year,month,day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year) {

        return getMonthFormat(month)+"/"+day+"/"+year;
    }

    private String getMonthFormat(int month) {
        if (month==1)
            return "01";
        if (month==2)
            return "02";
        if (month==3)
            return "03";
        if (month==4)
            return "04";
        if (month==5)
            return "05";
        if (month==6)
            return "06";
        if (month==7)
            return "07";
        if (month==8)
            return "08";
        if (month==9)
            return "09";
        if (month==10)
            return "10";
        if (month==11)
            return "11";
        if (month==12)
            return "12";
        //
        return "01";
    }


    void initial(){
        customerName=findViewById(R.id.customerName_profile);
        phoneNo=findViewById(R.id.Customer_phone_profile);
        profileImageView=findViewById(R.id.profileImg);
        mTotal=findViewById(R.id.sum);
        floatingActionButton=findViewById(R.id.floating);


    }

    //set customer info

    private void customerInfo(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Customers").child(profileId);
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                Customer customer=snapshot.getValue(Customer.class);
                customerName.setText(customer.getCustomerName());
                phoneNo.setText(customer.getCustomerPhone());

                //loading profile pic
                String imageuri =customer.getImageurl();
                Picasso.get().load(imageuri).into(profileImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //    private void creditAccount(){
//
//        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Description").child(customerId);
//
//        HashMap<String,Object> hashMap=new HashMap<>();
//        hashMap.put("date",date.getText().toString());
//        hashMap.put("productName",product_ET.getText().toString());
//        hashMap.put("saleAmount",amount_ET.getText().toString());
//        hashMap.put("totalAmount",mTotal.getText().toString()+" tk");
//        hashMap.put("shopOwner",firebaseUser.getUid());
//
//        databaseReference.keepSynced(true);
//        databaseReference.push().setValue(hashMap);
//
//    }
    private void debitAccount(){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("PayedAmount").child(customerId);

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("date",date.getText().toString());
        hashMap.put("productName",product_ET.getText().toString());
        hashMap.put("payedAmount",amount_ET.getText().toString());
        hashMap.put("totalAmount",mTotal.getText().toString()+" tk");
        hashMap.put("shopOwner",firebaseUser.getUid());


        databaseReference.keepSynced(true);
        databaseReference.push().setValue(hashMap);
    }

    private void showDescription(){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Description").child(profileId);

        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                descriptonList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Descripton descripton =dataSnapshot.getValue(Descripton.class);
                    descriptonList.add(descripton);
                }
                descriptionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // doing phoneCall
    public void phoneCall(View view) {

        makePhoneCall();
    }

    private void makePhoneCall() {
        if (phoneNo.length()>0){

            if (ContextCompat.checkSelfPermission(CustomerProfile.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(CustomerProfile.this,
                        new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
            }else{
                String no=phoneNo.getText().toString();
                String dial="tel:"+no;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
        else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_CALL){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                makePhoneCall();
            }else {

            }
        }
    }
}