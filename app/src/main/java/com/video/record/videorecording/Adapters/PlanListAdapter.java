package com.video.record.videorecording.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.video.record.videorecording.Helper.OnSelectedItemListener;
import com.video.record.videorecording.Model.PlanModel;
import com.video.record.videorecording.R;

import java.util.ArrayList;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.PlanListHolder> {

    Context context;
    ArrayList<PlanModel> planModelArrayList = new ArrayList<>();
    OnSelectedItemListener onSelectedItemListener;


    public PlanListAdapter(Context context, ArrayList<PlanModel> planModelArrayList, OnSelectedItemListener onSelectedItemListener) {
        this.context=context;
        this.planModelArrayList = planModelArrayList;
        this.onSelectedItemListener = onSelectedItemListener;
    }

    @NonNull
    @Override
    public PlanListAdapter.PlanListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_paln_list_adapter,parent,false);
        return new PlanListAdapter.PlanListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanListAdapter.PlanListHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.txt_plan_name.setText(planModelArrayList.get(position).getName());
        holder.txt_plan_amount.setText("₹"+planModelArrayList.get(position).getAmount());
        holder.txt_plan_amount.setPaintFlags(holder.txt_plan_amount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.txt_discount_amount.setText("Discount : ₹"+planModelArrayList.get(position).getDiscount());
        holder.txt_amount.setText("₹"+planModelArrayList.get(position).getDiscounted_price());
        holder.txt_validity.setText("Validity : "+planModelArrayList.get(position).getDuration()+planModelArrayList.get(position).getType());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectedItemListener.setOnClick(planModelArrayList.get(position).getId(),position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return planModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    public class PlanListHolder extends RecyclerView.ViewHolder{

        TextView txt_plan_name,txt_plan_amount,txt_discount_amount,txt_amount,txt_validity;

        public PlanListHolder(@NonNull View itemView) {
            super(itemView);

            txt_plan_name = itemView.findViewById(R.id.txt_plan_name);
            txt_plan_amount = itemView.findViewById(R.id.txt_plan_amount);
            txt_discount_amount = itemView.findViewById(R.id.txt_discount_amount);
            txt_amount= itemView.findViewById(R.id.txt_amount);
            txt_validity = itemView.findViewById(R.id.txt_validity);

        }
    }

}