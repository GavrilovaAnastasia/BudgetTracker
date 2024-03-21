package com.example.testtestapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.MyViewHolder> {
    private Context context;
    private OnItemsClick onItemsClick;
    private List<ExpenseModel> expenseModelList;

    public ExpensesAdapter(Context context, OnItemsClick onItemsClick) {
        this.context = context;
        expenseModelList = new ArrayList<>();
        this.onItemsClick = onItemsClick;
    }
    public void add(ExpenseModel expenseModel) {
        expenseModelList.add(expenseModel);
        notifyDataSetChanged();
    }
    public void clear() {
        expenseModelList.clear();
        notifyDataSetChanged();
    }
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_cell,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        ExpenseModel expenseModel=expenseModelList.get(position);
        holder.note.setText(expenseModel.getNote());
        holder.category.setText(expenseModel.getCategory());
        holder.amount.setText(String.valueOf(expenseModel.getAmount()));
        SimpleDateFormat myFormat = new SimpleDateFormat("HH:mm' 'dd-MM-yyyy");
        String time = myFormat.format(expenseModel.getTime());
        holder.date.setText(String.valueOf(time));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemsClick.OnClick(expenseModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView note,category,amount,date;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            note=itemView.findViewById(R.id.note);
            category=itemView.findViewById(R.id.category);
            amount=itemView.findViewById(R.id.amount);
            date=itemView.findViewById(R.id.date);
        }

    }


}
