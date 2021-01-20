package com.example.expensemanager.ui.dashboard;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.R;
import com.example.expensemanager.ui.dashboard.Model.Data;
import com.example.expensemanager.ui.gallery.GalleryFragment;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;


public class DashboardFragment extends Fragment {

    //floating button
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //floatingbutton textview

    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    //boolean
    private boolean isOpen=false;


    //animation
    private Animation FadOpen,FadeClose,Rotate,Rotateback;


    //Dashboard income and expense result..

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    //firebase..
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //recycler view
    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase=FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);




        //connect floating button to layout
        fab_main_btn=(FloatingActionButton) root.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=(FloatingActionButton)root.findViewById((R.id.income_Ft_btn));
        fab_expense_btn=(FloatingActionButton)root.findViewById(R.id.expense_Ft_btn);

        //connect floating text.
        fab_income_txt=root.findViewById(R.id.income_ft_text);
        fab_expense_txt=root.findViewById(R.id.expense_ft_text);


        //Total income and expense set..
        totalIncomeResult=root.findViewById(R.id.income_set_result);
        totalExpenseResult=root.findViewById(R.id.expense_set_result);


        //Recycler
        //mRecyclerIncome=root.findViewById(R.id.recycler_income);
        //mRecyclerExpense=root.findViewById(R.id.recycler_epense);

        //Animation connect...
        FadOpen= AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadeClose=AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);
        Rotate=AnimationUtils.loadAnimation(getActivity(),R.anim.rotate);
        Rotateback=AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_back);



        fab_main_btn.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View view){

                if(isOpen){fab_main_btn.startAnimation(Rotateback);
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen=false;

                } else{
                    fab_main_btn.startAnimation(Rotate);

                    fab_income_btn.startAnimation(FadOpen);
                    fab_expense_btn.startAnimation(FadOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadOpen);

                    fab_expense_txt.startAnimation(FadOpen);

                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);


                    fab_income_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addData();
                        }
                    });

                    fab_expense_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { addData(); }
                    });


                    isOpen=true;
                }
            }

        });


        //Calculate total income..
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalsum=0;
                for (DataSnapshot mysnap:snapshot.getChildren()){
                    Data data=mysnap.getValue(Data.class);

                    totalsum+=data.getAmount();

                    String stResult=String.valueOf(totalsum);

                    totalIncomeResult.setText(stResult);



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Calculate total expense..
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalsum=0;
                for(DataSnapshot mysnapshot:snapshot.getChildren()){

                    Data data=mysnapshot.getValue(Data.class);
                    totalsum+=data.getAmount();




                }
                String stResult=String.valueOf(totalsum);

                totalExpenseResult.setText(stResult);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Recycler
        /*
        LinearLayoutManager layoutManagerIncome=new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);



        LinearLayoutManager layoutManagerExpense=new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false);
        layoutManagerExpense.setReverseLayout(true);
        layoutManagerExpense.setStackFromEnd(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);*/







        return root;
    }

    //Floating button animation..
    private void ftAnimation(){


        if(isOpen){fab_main_btn.startAnimation(Rotateback);
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen=false;

        } else{
            fab_main_btn.startAnimation(Rotate);

            fab_income_btn.startAnimation(FadOpen);
            fab_expense_btn.startAnimation(FadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadOpen);

            fab_expense_txt.startAnimation(FadOpen);

            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);

            isOpen=true;
        }

    }

    private void addData() {

        //fab button income..

        fab_income_btn.setOnClickListener(new View.OnClickListener()  {


            @Override
            public void onClick(View view){

                incomeDataInsert();

            }
        });


        fab_expense_btn.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {

                expenseDataInsert();


            }
        });


    }

    public void incomeDataInsert(){


        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myviewm=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myviewm);
        final AlertDialog dialog=mydialog.create();

        dialog.setCancelable(false);


        EditText edtAmount=myviewm.findViewById(R.id.amount_edt);
        EditText edtType=myviewm.findViewById(R.id.type_edt);
        EditText edtNote=myviewm.findViewById(R.id.note_edt);

        Button btnSave=myviewm.findViewById(R.id.btnSave);
        Button btnCansel=myviewm.findViewById(R.id.btnCancel);




        btnSave.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                String type=edtType.getText().toString().trim();
                String ammount=edtAmount.getText().toString().trim();
                String note=edtNote.getText().toString().trim();



                if(TextUtils.isEmpty(type)){
                    edtType.setError("Required Field...");
                }

                if(TextUtils.isEmpty(ammount)){
                    edtAmount.setError("Required Field...");
                }
                int ourammontint=Integer.parseInt(ammount);

                if(TextUtils.isEmpty(note)){
                    edtNote.setError("Required Field...");
                }


                String id=mIncomeDatabase.push().getKey();
                String mDate= DateFormat.getDateInstance().format(new Date());

                Data data=new Data(ourammontint,id,type,note,mDate);

                mIncomeDatabase.child(id).setValue(data);

                ftAnimation();

                Toast.makeText(getActivity(),"Data ADDED",Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        btnCansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();



    }

    public void expenseDataInsert(){


        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);


        final AlertDialog dialog=mydialog.create();

        dialog.setCancelable(false);

        EditText ammount=myview.findViewById(R.id.amount_edt);
        EditText type=myview.findViewById(R.id.type_edt);
        EditText note=myview.findViewById(R.id.note_edt);

        Button btnSave=myview.findViewById(R.id.btnSave);
        Button btnCansel=myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String tmAmmount=ammount.getText().toString().trim();
                String tmType=type.getText().toString().trim();
                String tmnote=note.getText().toString().trim();


                if(TextUtils.isEmpty(tmAmmount)){
                    ammount.setError("Required Field...");
                    return;
                }

                int inamount=Integer.parseInt(tmAmmount);

                if (TextUtils.isEmpty(tmType)){
                    type.setError("Required Field...");
                    return;
                }


                if(TextUtils.isEmpty(tmnote)){
                    note.setError("Required Field...");
                    return;
                }




                String id=mExpenseDatabase.push().getKey();
                String mDate=DateFormat.getDateInstance().format(new Date());
                Data data=new Data(inamount,id,tmType,tmnote,mDate);


                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"data ADDED",Toast.LENGTH_SHORT).show();

                ftAnimation();

                dialog.dismiss();




            }
        });


        btnCansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();

    }


    /*@Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions <Data> options=new FirebaseRecyclerOptions.Builder<Data>().setQuery(mIncomeDatabase, Data.class).setLifecycleOwner(this).build();

        FirebaseRecyclerAdapter<Data,IncomeViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull Data model) {
                holder.setIncomeType(model.getType());
                holder.setIncomeDate(model.getDate());
                holder.setincomeammount(model.getAmount());

            }

            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income,parent,false);
                return new DashboardFragment.IncomeViewHolder(v);
            }
        };

        mRecyclerIncome.setAdapter(firebaseRecyclerAdapter);



        FirebaseRecyclerOptions <Data> options1=new FirebaseRecyclerOptions.Builder<Data>().setQuery(mExpenseDatabase, Data.class).setLifecycleOwner(this).build();

        FirebaseRecyclerAdapter<Data,ExpenseViewHolder> firebaseRecyclerAdapter1=new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull Data model) {
                holder.setExpenseType(model.getType());
                holder.setExpenseDate(model.getDate());
                holder.setExpenseAmmount(model.getAmount());
            }

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense,parent,false);
                return new DashboardFragment.ExpenseViewHolder(v);
            }
        };

        mRecyclerExpense.setAdapter(firebaseRecyclerAdapter1);
    }






    //for income data

    public static class IncomeViewHolder extends RecyclerView.ViewHolder{


        View mIncomeView;
        public IncomeViewHolder(View itemView){

            super(itemView);
            mIncomeView=itemView;
        }

        public void setIncomeType(String type){

            TextView mtype=mIncomeView.findViewById(R.id.type_income_ds);
            mtype.setText(type);
        }

        public void setincomeammount(int ammount){

            TextView mAmmount=mIncomeView.findViewById(R.id.ammount_income_ds);
            String strAmmount=String.valueOf(ammount);
            mAmmount.setText(strAmmount);


        }


        public void setIncomeDate(String Date){
            TextView mDate=mIncomeView.findViewById(R.id.date_income_ds);
            mDate.setText(Date);
        }
    }

    //for expense data
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mExpenseView;

        public ExpenseViewHolder(View itemView){

            super(itemView);
            mExpenseView=itemView;
        }

        public void setExpenseType(String type){
            TextView mtype=mExpenseView.findViewById(R.id.type_expense_ds);
            mtype.setText(type);
        }

        public void setExpenseAmmount(int ammount){
            TextView mAmmount=mExpenseView.findViewById(R.id.ammount_expense_ds);
            String strAmmount=String.valueOf(ammount);
            mAmmount.setText(strAmmount);
        }

        public void setExpenseDate(String date){
            TextView mDate=mExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }



    }*/





}