package com.sublimeIT.hisabkhata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SetUserInfoActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG="RegisterActivity";
    Context context=SetUserInfoActivity.this;

    EditText shopName_reg,username_reg,phone_reg,address_reg;
    Button save;

    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_info);

        mAuth=FirebaseAuth.getInstance();
        fields();
        init();
    }

    private void fields(){
        shopName_reg=findViewById(R.id.shopName_reg);
        username_reg=findViewById(R.id.username_reg);
        phone_reg=findViewById(R.id.phone_reg);
        address_reg=findViewById(R.id.address);
        save=findViewById(R.id.save);
    }
    private void init(){

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd=new ProgressDialog(context);
                pd.setMessage("Please wait...");
                pd.show();

                checkDataEntered();

            }
        });
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
    void checkDataEntered() {
        String str_username=username_reg.getText().toString();
        String str_shopname=shopName_reg.getText().toString();
        String str_phone=phone_reg.getText().toString();
        String str_address=address_reg.getText().toString();


        if (isEmpty(shopName_reg)) {
            pd.dismiss();
            shopName_reg.setError("must enter Shop Name");
            Toast t = Toast.makeText(this, "You must enter shop name!", Toast.LENGTH_SHORT);
            t.show();
        }

        else if (isEmpty(username_reg)) {
            pd.dismiss();
            username_reg.setError("User name is required!");
            Toast t = Toast.makeText(this, "You must enter User name!", Toast.LENGTH_SHORT);
            t.show();
        }
        else if (phone_reg.length()<11) {
            pd.dismiss();
            Toast t = Toast.makeText(this, "Phone number must have 11 digit", Toast.LENGTH_SHORT);
            t.show();
        }
        else if (isEmpty(address_reg)) {
            pd.dismiss();
            address_reg.setError("User address is required!");
            Toast t = Toast.makeText(this, "You must enter User address!", Toast.LENGTH_SHORT);
            t.show();
        }
        else {
            register(str_username,str_shopname,str_phone,str_address);
        }


    }
    private void register(final String username, final String shopname,String phone,String address){

        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        String userid=firebaseUser.getUid();

        reference= FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("userId",userid);
        hashMap.put("userName",username);
        hashMap.put("shopname",shopname);
        hashMap.put("userPhone",firebaseUser.getPhoneNumber());
        hashMap.put("address",address);

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    pd.dismiss();
                    Intent intent=new Intent(context, UserProfile.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}