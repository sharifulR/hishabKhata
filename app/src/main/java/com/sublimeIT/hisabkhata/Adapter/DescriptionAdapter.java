package com.sublimeIT.hisabkhata.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sublimeIT.hisabkhata.Model.Descripton;
import com.sublimeIT.hisabkhata.R;

import java.util.List;

public class DescriptionAdapter extends RecyclerView.Adapter<DescriptionAdapter.ViewHolder>{
    private Context dContext;
    private List<Descripton> dDescripton;
    FirebaseUser firebaseUser;

    public DescriptionAdapter(Context dContext, List<Descripton> dDescripton) {
        this.dContext = dContext;
        this.dDescripton = dDescripton;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(dContext).inflate(R.layout.description_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final Descripton descripton = dDescripton.get(position);

        holder.date.setText(descripton.getDate());
        holder.description.setText(descripton.getProductName());
        holder.amount.setText(descripton.getAmount());



    }

    @Override
    public int getItemCount() {
        return dDescripton.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date,description,amount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date=itemView.findViewById(R.id.date_tv);
            description=itemView.findViewById(R.id.product_TV_id);
            amount=itemView.findViewById(R.id.cAmount_TV_id);
        }
    }
}