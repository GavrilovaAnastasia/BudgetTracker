package com.example.testtestapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.testtestapp.databinding.ActivityMainBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnItemsClick{
    ActivityMainBinding binding;
    private ExpensesAdapter expensesAdapter;
    Intent intent;
    private String type;
    private long income = 0, expense = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expensesAdapter = new ExpensesAdapter(this, this);
        binding.recycler.setAdapter(expensesAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));

        intent = new Intent(MainActivity.this,AddExpenseActivity.class);

        binding.addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("type","Income");
                startActivity(intent);
            }
        });
        binding.addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("type","Expense");
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please");
        progressDialog.setMessage("Wait");
        progressDialog.setCancelable(false);

        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            Intent intent
                    = new Intent(MainActivity.this,
                    RegistrationActivity.class);
            startActivity(intent);
        }
       /* if(FirebaseAuth.getInstance().getCurrentUser()==null){
            progressDialog.show();
            FirebaseAuth.getInstance()
                    .signInAnonymously()
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            progressDialog.cancel();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            progressDialog.cancel();
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        income = 0;
        expense = 0;
        getData();
    }

    private void getData() {
        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .whereEqualTo("uid", FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        expensesAdapter.clear();
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot ds:dsList) {
                            System.out.println(ds);
                            ExpenseModel expenseModel = ds.toObject(ExpenseModel.class);
                            if (expenseModel.getType().equals("Income")) {
                                income += expenseModel.getAmount();
                            } else {
                                expense += expenseModel.getAmount();
                            }
                            expensesAdapter.add(expenseModel);
                        }
                        setGraph();
                    }
                });
    }

    private void setGraph() {
        List<PieEntry> pieEntryList = new ArrayList<>();
        List<Integer> colorsList = new ArrayList<>();
        if (income != 0) {
            pieEntryList.add(new PieEntry(income, "Income"));
            colorsList.add(getResources().getColor(R.color.black));
        }
        if (expense != 0) {
            pieEntryList.add(new PieEntry(expense, "Expense"));
            colorsList.add(getResources().getColor(R.color.biruse));
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntryList,String.valueOf(income=expense));
        pieDataSet.setColors(colorsList);
        pieDataSet.setValueTextColor(getResources().getColor(R.color.white));
        PieData pieData = new PieData(pieDataSet);
        binding.graph.setData(pieData);
        binding.graph.invalidate();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.log_out_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logOut) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            FirebaseAuth.getInstance().signOut();
            finish();
        }
        return true;
    }
    @Override
    public void OnClick(ExpenseModel expenseModel) {
        Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
        intent.putExtra("model",expenseModel);
        startActivity(intent);
    }
}