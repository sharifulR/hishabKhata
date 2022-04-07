package com.sublimeIT.hisabkhata.Register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.sublimeIT.hisabkhata.MainActivity;
import com.sublimeIT.hisabkhata.R;

import java.util.concurrent.TimeUnit;

public class LoginPhone extends AppCompatActivity {

    Context context=LoginPhone.this;

    private EditText phoneText,codeText;
    private Button verifyButton,sendButton,resendButton;
    private TextView statusText;
    String number;
    ProgressDialog pd;
    DatabaseReference reference;

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth mAuth;
    CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);
        //init
        phoneText=findViewById(R.id.phoneText);
        codeText=findViewById(R.id.codeText);
        verifyButton=findViewById(R.id.verifyButton);
        sendButton=findViewById(R.id.sendButton);
        resendButton=findViewById(R.id.resendButton);
        statusText=findViewById(R.id.statusText);

        ccp=findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneText);

        verifyButton.setEnabled(false);
        resendButton.setEnabled(false);
        statusText.setText("Signed Out");

        mAuth=FirebaseAuth.getInstance();
    }

    public void sendCode(View view) {
        pd=new ProgressDialog(LoginPhone.this);
        pd.setMessage("Please wait...");
        pd.show();
        number=ccp.getFullNumberWithPlus();
        setUpVerificationCallbacks();
        // [START start_phone_auth]
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(verificationCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [END start_phone_auth]
    }

    private void setUpVerificationCallbacks() {

        verificationCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                verifyButton.setEnabled(false);
                resendButton.setEnabled(false);
                statusText.setText("Signed In");
                codeText.setText("");
                signInWithPhoneAuth(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException){
                    pd.dismiss();

                    Toast.makeText(LoginPhone.this,"Invalid phone number",Toast.LENGTH_SHORT).show();

                }else if (e instanceof FirebaseTooManyRequestsException){
                    Toast.makeText(LoginPhone.this,"SMS Quota exceeded",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                phoneVerificationId=verificationId;
                resendToken=forceResendingToken;
                verifyButton.setEnabled(true);
                sendButton.setEnabled(false);
                resendButton.setEnabled(true);
            }
        };
    }

    public void resendCode(View view) {
        pd=new ProgressDialog(LoginPhone.this);
        pd.setMessage("Please wait...");
        pd.show();
        number=ccp.getFullNumberWithPlus();
        setUpVerificationCallbacks();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(verificationCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(resendToken)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void verifyCode(View view) {
        String code=codeText.getText().toString();

        PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(phoneVerificationId,code);
        signInWithPhoneAuth(phoneAuthCredential);
    }

    private void signInWithPhoneAuth(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            codeText.setText("");
                            statusText.setText("Signed In");
                            resendButton.setEnabled(false);
                            verifyButton.setEnabled(false);


                            DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users")
                                    .child(mAuth.getCurrentUser().getUid());
                            //Value event listener
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    pd.dismiss();
                                    FirebaseUser user=task.getResult().getUser();
                                    String phoneNumber=user.getPhoneNumber();

                                    Intent intent=new Intent(LoginPhone.this, MainActivity.class);
                                    intent.putExtra("phone",phoneNumber);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                    pd.dismiss();
                                }
                            });

                        }else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){

                            }
                        }
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(LoginPhone.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}