package com.example.expensemanager.ui.slideshow;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;

    //Firebase..
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;


    //Recyvlerview
    private RecyclerView recyclerView;

    private TextView expenseTotalSum;

    //Edt data item
    private EditText edtAmmount;
    private EditText edtType;
    private EditText edtNote;

    private Button btnUpdate;
    private Button btnDelete;

    //Data variable..
    private String type;
    private String note;
    private int ammount;


    private String post_key;







    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel = new ViewModelProvider(this).get(SlideshowViewModel.class);
        View myview = inflater.inflate(R.layout.fragment_slideshow, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser muser=mAuth.getCurrentUser();

        String uid=muser.getUid();

        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        expenseTotalSum=myview.findViewById(R.id.expense_txt_result);




        recyclerView=myview.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int sum=0;

                for (DataSnapshot mysanapshot:snapshot.getChildren()){

                    Map<String,Object> map=(Map<String, Object>) mysanapshot.getValue();
                    Object amount=map.get("amount");
                    int pValue= Integer.parseInt(String.valueOf(amount));
                    sum+=pValue;
                    expenseTotalSum.setText(String.valueOf(sum));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
        return myview;
    }




    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options=new FirebaseRecyclerOptions.Builder<Data>().setQuery(mExpenseDatabase, Data.class).setLifecycleOwner(this).build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {


                holder.setDate(model.getDate());
                holder.setAmmount(model.getAmount());
                holder.setNote(model.getNote());
                holder.setType(model.getType());


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key=getRef(position).getKey();
                        type=model.getType();
                        note=model.getNote();
                        ammount=model.getAmount();
                        updateDataItem();
                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data,parent,false);
                return new MyViewHolder(v);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);



    }


    private static class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public MyViewHolder(View itemView){
            super(itemView);
            mView=itemView;

        }

        private void setDate(String date){

            TextView mDate=mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }

        private void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }

        private void setNote(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }

        private void setAmmount(int ammount){
            TextView mAmount=mView.findViewById(R.id.ammount_txt_expense);
            String strammount=String.valueOf(ammount);
            mAmount.setText(strammount);
        }
    }

    private void updateDataItem(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.update_data_item,null);
        mydialog.setView(myview);

        edtAmmount=myview.findViewById(R.id.amount_edt);
        edtNote=myview.findViewById(R.id.note_edt);
        edtType=myview.findViewById(R.id.type_edt);

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmmount.setText(String.valueOf(ammount));
        edtAmmount.setSelection(String.valueOf(ammount).length());


        btnUpdate=myview.findViewById(R.id.btn_upd_Update);
        btnDelete=myview.findViewById(R.id.btnuPD_Delete);


        final AlertDialog dialog=mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type=edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();

                String stammount=String.valueOf(ammount);

                stammount=edtAmmount.getText().toString().trim();

                int intamount=Integer.parseInt(stammount);

                String mDate= DateFormat.getDateInstance().format(new Date());
                Data data=new Data(intamount,post_key,type,note,mDate);

                mExpenseDatabase.child(post_key).setValue(data);

                dialog.dismiss();

            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mExpenseDatabase.child(post_key).removeValue();

                dialog.dismiss();

            }
        });

        dialog.show();


    }
}