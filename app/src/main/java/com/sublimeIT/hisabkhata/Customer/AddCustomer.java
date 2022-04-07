package com.sublimeIT.hisabkhata.Customer;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sublimeIT.hisabkhata.MainActivity;
import com.sublimeIT.hisabkhata.R;

import java.util.HashMap;

public class AddCustomer extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG="RegisterActivity";
    Context context= AddCustomer.this;

    EditText customerName,Customer_phone,address;
    Button save;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        mAuth = FirebaseAuth.getInstance();
        fields();
        init();
    }
    private void fields(){
        customerName=findViewById(R.id.cutomerName);
        Customer_phone=findViewById(R.id.Customer_phone);
        address=findViewById(R.id.address);
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

        if (isEmpty(customerName)) {
            pd.dismiss();
            customerName.setError("Customer name is required!");
            Toast t = Toast.makeText(this, "Enter customer name!", Toast.LENGTH_SHORT);
            t.show();
        } else if (isEmpty(Customer_phone)) {
            pd.dismiss();
            Customer_phone.setError("Phone number is required!");
            Toast t = Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT);
            t.show();
        }else if (Customer_phone.length()<11) {
            pd.dismiss();
            Toast t = Toast.makeText(this, "Phone number must have 11 characters", Toast.LENGTH_SHORT);
            t.show();
        }
        else if (isEmpty(address)) {
            pd.dismiss();
            address.setError("Address is required!");
            Toast t = Toast.makeText(this, "Password must have 6 characters", Toast.LENGTH_SHORT);
            t.show();
        }else {

            pd.dismiss();
            addCustomer();
        }
    }

    private void addCustomer() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Customers");

        String customerId=reference.push().getKey();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("customerId", customerId);
        hashMap.put("customerName", customerName.getText().toString());
        hashMap.put("customerPhone", Customer_phone.getText().toString());
        hashMap.put("address", address.getText().toString());
//        hashMap.put("imageurl","");
        hashMap.put("shopOwner", FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.keepSynced(true);
        assert customerId != null;
        reference.child(customerId).setValue(hashMap);
        Toast.makeText(context,"Customer add successful",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}