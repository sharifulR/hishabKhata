package com.sublimeIT.hisabkhata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.sublimeIT.hisabkhata.Model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    CircleImageView profileImageView;
    TextView editTV,userName,phoneNo,address;

    //FirebaseUser
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        editTV=findViewById(R.id.editTV);
        userName=findViewById(R.id.userName);
        phoneNo=findViewById(R.id.phoneNo);
        address=findViewById(R.id.address);

        profileImageView=findViewById(R.id.circleImageView);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfile.this, UserPic.class));
                finish();
            }
        });

        editTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfile.this, SetUserInfoActivity.class));
            }
        });
        //
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


        userInfo();
    }

    private void userInfo() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                User user=snapshot.getValue(User.class);
                if (user!=null){
                    userName.setText(user.getUserName());
                    phoneNo.setText(user.getUserPhone());
                    address.setText(user.getAddress());

                    //loading profile pic
                    String imageuri =user.getImageurl();
                    Picasso.get().load(imageuri).into(profileImageView);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}