package com.sublimeIT.hisabkhata.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.sublimeIT.hisabkhata.Customer.CustomerProfile;
import com.sublimeIT.hisabkhata.Model.Customer;
import com.sublimeIT.hisabkhata.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder>{

    public Context mContext;
    public List<Customer> mCustomers;

    FirebaseUser firebaseUser;

    public CustomerAdapter(Context mContext, List<Customer> mCustomers) {
        this.mContext = mContext;
        this.mCustomers = mCustomers;
    }

    // ProgressDialog
    ProgressDialog pd;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.customer_list,parent,false);
        return new CustomerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final Customer customer=mCustomers.get(position);

        Picasso.get().load(customer.getImageurl()).into(holder.image_customer);
        holder.customerName.setText(customer.getCustomerName());
        holder.customerPhone.setText(customer.getCustomerPhone());
        holder.address.setText(customer.getAddress());

//        holder.editBTn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
//
//                view= LayoutInflater.from(mContext).inflate(R.layout.activity_add_customer,null);
//                builder.setView(view);
//
//                AlertDialog dialog=builder.create();
//                dialog.show();
//
//                //initial customer data
//                TextView textViewId=view.findViewById(R.id.textViewId);
//                EditText cName=view.findViewById(R.id.cutomerName);
//                EditText Customer_phone=view.findViewById(R.id.Customer_phone);
//                EditText address=view.findViewById(R.id.address);
//                Button save=view.findViewById(R.id.save);
//
//                // set
//                textViewId.setText(String.valueOf("Update info"));
//                cName.setText(customer.getCustomerName());
//                Customer_phone.setText(customer.getCustomerPhone());
//                address.setText(customer.getAddress());
//                save.setText(String.valueOf("Update"));
//
//                //clickList
////                save.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View view) {
////                        pd=new ProgressDialog(mContext);
////                        pd.setMessage("Please wait...");
////                        pd.show();
////
////                        Map<String,Object> info=new HashMap<>();
////                        info.put("customerName",cName.getText().toString());
////                        info.put("customerPhone",Customer_phone.getText().toString());
////                        info.put("address",address.getText().toString());
////                        info.put("shopOwner",firebaseUser.getUid());
////
////                        DatabaseReference cUpdateRef= FirebaseDatabase.getInstance().getReference("Customers").child(String.valueOf(position));
////
////                        cUpdateRef.keepSynced(true);
////                        cUpdateRef.push().setValue(info);
////                        pd.dismiss();
////                    }
////                });
//
//            }
//        });

        //go to customer profile activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileId",customer.getCustomerId());
                editor.apply();
                Intent intent=new Intent(mContext, CustomerProfile.class);
                intent.putExtra("customerId",customer.getCustomerId());
                intent.putExtra("shopOwner",customer.getShopOwner());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mCustomers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView image_customer;
        public TextView customerName,customerPhone,address;
//        public ImageButton editBTn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_customer=itemView.findViewById(R.id.image_customer);
            customerName=itemView.findViewById(R.id.customer_name);
            customerPhone=itemView.findViewById(R.id.customer_phoneNo);
            address=itemView.findViewById(R.id.customer_address);
//            editBTn=itemView.findViewById(R.id.editBTn);

        }
    }
}
