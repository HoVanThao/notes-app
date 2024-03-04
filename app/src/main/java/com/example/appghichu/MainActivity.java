package com.example.appghichu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appghichu.model.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseFirestore firestore;
    private RecyclerView rvNote;
    private FloatingActionButton btnAdd;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("posts");

        firestore = FirebaseFirestore.getInstance();

        rvNote = findViewById(R.id.rv_notes);
        rvNote.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        btnAdd = findViewById(R.id.btn_add);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>()
                        .setQuery(myRef, Post.class)
                        .build();

        FirebaseRecyclerAdapter<Post, PostHolder> adapter = new FirebaseRecyclerAdapter<Post, PostHolder>(options) {
            @NonNull
            @Override
            public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.note_item, parent, false);

                return new PostHolder(view);
            }

            @Override
            protected void onBindViewHolder(PostHolder holder, int position, Post model) {
                holder.tvTitle.setText(model.getTitle());
                holder.tvContent.setText(model.getContent());
                holder.linearLayoutNote.setBackgroundColor(Color.parseColor(model.getColor()));

                ImageView ivAction = holder.itemView.findViewById(R.id.iv_action);
                ivAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem item) {
                                EditNode(model);
                                return true;
                            }
                        });
                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem item) {
                                deleteItem(model);
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }
        };

        rvNote.setAdapter(adapter);
        adapter.startListening();

    }

    private void deleteItem(Post mmPost) {
        myRef.child(mmPost.getId()).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "DELETE success!!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "DELETE fails!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void EditNode(Post mPost) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.custom_dialog_layout_edit, null);
        mDialog.setView(mView);

        AlertDialog dialog = mDialog.create();
        dialog.setCancelable(true);

        AppCompatButton btnSaveEdit = mView.findViewById(R.id.btn_Save_edit);
        EditText edtTitleEdit = mView.findViewById(R.id.edt_title_edit);
        EditText edtContentEdit = mView.findViewById(R.id.edt_content_edit);

        edtTitleEdit.setText(mPost.getTitle());
        edtContentEdit.setText(mPost.getContent());

        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mPost.getId();
                String title = edtTitleEdit.getText().toString();
                String content = edtContentEdit.getText().toString();

                myRef.child(id).setValue(new Post(id, title, content, mPost.getColor())).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Edit Note success!!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Edit Note fails!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });
        dialog.show();
        
    }

    public void addNote(){
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mview = inflater.inflate(R.layout.custom_dialog_layout, null);

        mDialog.setView(mview);

        AlertDialog dialog = mDialog.create();
        dialog.setCancelable(true);
        dialog.show();

        AppCompatButton btnSave = mview.findViewById(R.id.btn_Save);
        EditText edtTitle  = mview.findViewById(R.id.edt_title);
        EditText edtContent  = mview.findViewById(R.id.edt_content);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = myRef.push().getKey();
                String title = edtTitle.getText().toString().trim();
                String content = edtContent.getText().toString().trim();

                myRef.child(id).setValue(new Post(id, title, content, getRandomColor()))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Add note sucessful!", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(MainActivity.this, "Add note fail!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                dialog.dismiss();

            }
        });



    }

    public static class PostHolder extends RecyclerView.ViewHolder{
        public TextView tvTitle;
        public TextView tvContent;
        public LinearLayout linearLayoutNote;
        public PostHolder(@NonNull View view) {
            super(view);
            tvTitle  = view.findViewById(R.id.tv_title);
            tvContent = view.findViewById(R.id.tv_content);
            linearLayoutNote = view.findViewById(R.id.layout_Note);

        }
    }

    private String getRandomColor(){
        ArrayList<String> color = new ArrayList<>();
        color.add("#35ad68");
        color.add("#c27ba0");
        color.add("#baa9aa");
        color.add("#bfbd97");
        color.add("#746cc0");
        color.add("#d5d2a8");
        color.add("#0affa0");
        color.add("#fc8eac");

        Random random = new Random();
        return color.get(random.nextInt(color.size()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        mAuth.signOut();
        finish();
        return true;
    }
}

