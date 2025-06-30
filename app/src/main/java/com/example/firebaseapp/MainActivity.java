package com.example.firebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.firebaseapp.Mahasiswa;
import com.example.firebaseapp.MahasiswaAdapter;
import com.example.firebaseapp.CreateActivity;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    FirebaseAuth auth;
    Button button, btnAdd;
    TextView textView;
    FirebaseUser user;
    FirebaseDatabase database;
    private ListView listView;
    private MahasiswaAdapter adapter;
    private ArrayList<Mahasiswa> mahasiswaList;
    DatabaseReference dbMahasiswa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase .getInstance();
        dbMahasiswa = database.getReference("mahasiswa");

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.btnLogout);
        textView = findViewById(R.id.tvHome);
        listView = findViewById(R.id.lv_list);
        btnAdd = findViewById(R.id.btn_add);
        mahasiswaList = new ArrayList<>();

        user = auth.getCurrentUser();
        if (user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            textView.setText(user.getEmail());
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                intent.putExtra(UpdateActivity.EXTRA_MAHASISWA, mahasiswaList.get(i));

                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onStart() {
        super.onStart();
        user = auth.getCurrentUser();

        dbMahasiswa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mahasiswaList.clear();
                for (DataSnapshot mahasiswaSnapshot : dataSnapshot.getChildren()) {
                    Mahasiswa mahasiswa = mahasiswaSnapshot.getValue(Mahasiswa.class);
                    mahasiswaList.add(mahasiswa);
                }
                MahasiswaAdapter adapter = new MahasiswaAdapter(MainActivity.this);
                adapter.setMahasiswaList(mahasiswaList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
            }
        });
   }
}