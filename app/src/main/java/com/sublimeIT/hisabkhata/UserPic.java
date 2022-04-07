package com.sublimeIT.hisabkhata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserPic extends AppCompatActivity {

    Context context= UserPic.this;

    private CircleImageView profileImageView;
    private TextView ProfileChangeTv,cancelTv,saveTv;

    private DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
//    String profileId;

    // set profile image
    private Uri imageUri;
    private String myUri="";
    private StorageTask uploadTask;
    private StorageReference storageProfilePicRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pic);

        // init
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

//        SharedPreferences sharedPreferences=context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
//        profileId=sharedPreferences.getString("profileId","none");

        databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        storageProfilePicRef= FirebaseStorage.getInstance().getReference().child("Profile Pic");
        profileImageView=findViewById(R.id.profile_image);
        ProfileChangeTv=findViewById(R.id.change_profile_btn);
        cancelTv=findViewById(R.id.cancel_TV);
        saveTv=findViewById(R.id.save_TV);

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(UserPic.this, UserProfile.class));
            }
        });
        saveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadProfileImage();
            }
        });
        ProfileChangeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setAspectRatio(1,1).start(UserPic.this);
            }
        });
        getUserInfo();
    }
    private void getUserInfo() {
        databaseReference.keepSynced(true);
        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    if (snapshot.hasChild("imageurl")){
                        String image=snapshot.child("imageurl").getValue().toString();
                        if (image == null)
                            Picasso.get().load(image).into(profileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data !=null)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            profileImageView.setImageURI(imageUri);
        }else {
            Toast.makeText(this,"Error, Try again",Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProfileImage() {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Set your profile");
        progressDialog.setMessage("Please wait, while we are setting your data");
        progressDialog.show();

        if (imageUri !=null){
            final StorageReference fileRef=storageProfilePicRef
                    .child(firebaseUser.getUid()+".jpg");
            uploadTask=fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        myUri=downloadUri.toString();
                        HashMap<String,Object> userMap=new HashMap<>();
                        userMap.put("imageurl",myUri);
                        databaseReference.child(firebaseUser.getUid()).updateChildren(userMap);
                        progressDialog.dismiss();
                        Toast.makeText(context,"Image uploaded successful",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UserPic.this, UserProfile.class));
                        finish();
                    }
                }
            });
        }else {
            progressDialog.dismiss();
            Toast.makeText(this,"Image not selected",Toast.LENGTH_SHORT).show();
        }
    }
}