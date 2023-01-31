package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.androidproject.AddRepo.AddRepoAdapter;
import com.example.androidproject.AddRepo.AddRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button addRepository;
    RecyclerView recyclerView;
    List<String> nameOfRepo = new ArrayList<>();
    List<String> ownerName = new ArrayList<>();
    List<String> descriptionOfRepo = new ArrayList<>();
    List<String> urlToRepo = new ArrayList<>();
    SharedPreferences listOfSavedRepo;
    TextView noDataAvailable;
    Gson gson;
    SharedPreferences.Editor listEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseViews();

        //fetching data from storage/shared pref
        if(listOfSavedRepo.contains("data")){
            Type type = new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType();
            //looping through hashmap to retrieve stored data

            HashMap<String,HashMap<String,String>> map = new HashMap<>(gson.fromJson(listOfSavedRepo.getString("data",""),type));
            for(Map.Entry<String,HashMap<String,String>> hashMapEntry : map.entrySet()){
                String key = hashMapEntry.getKey();
                for(Map.Entry<String,String> innerMap : hashMapEntry.getValue().entrySet()){
                    switch (innerMap.getKey()){
                        case "ownerName":
                            ownerName.add(innerMap.getValue());
                            break;
                        case "description":
                            descriptionOfRepo.add(innerMap.getValue());
                            break;
                        case "repoName":
                            nameOfRepo.add(innerMap.getValue());
                            break;
                        case "url":
                            urlToRepo.add(innerMap.getValue());
                            break;
                    }
                }
            }
            recyclerView.setAdapter(new AddRepoAdapter(nameOfRepo,ownerName,descriptionOfRepo,urlToRepo));
        }else{
            noDataAvailable.setVisibility(View.VISIBLE);
            addRepository.setVisibility(View.VISIBLE);
        }

        addRepository.setOnClickListener(click -> {
            Intent intent = new Intent(MainActivity.this, AddRepository.class);
            startActivityForResult(intent,1);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 4){
            noDataAvailable.setVisibility(View.INVISIBLE);
            addRepository.setVisibility(View.INVISIBLE);
            nameOfRepo.clear();
            urlToRepo.clear();
            descriptionOfRepo.clear();
            ownerName.clear();
            Type type = new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType();

            HashMap<String,HashMap<String,String>> map = new HashMap<>(gson.fromJson(listOfSavedRepo.getString("data",""),type));
            for(Map.Entry<String,HashMap<String,String>> hashMapEntry : map.entrySet()){
                String key = hashMapEntry.getKey();
                for(Map.Entry<String,String> innerMap : hashMapEntry.getValue().entrySet()){
                    switch (innerMap.getKey()){
                        case "ownerName":
                            ownerName.add(innerMap.getValue());
                            break;
                        case "description":
                            descriptionOfRepo.add(innerMap.getValue());
                            break;
                        case "repoName":
                            nameOfRepo.add(innerMap.getValue());
                            break;
                        case "url":
                            urlToRepo.add(innerMap.getValue());
                            break;
                    }
                }
            }


            recyclerView.setAdapter(new AddRepoAdapter(nameOfRepo,ownerName,descriptionOfRepo,urlToRepo));

        }
    }

    private void initialiseViews() {
        //binding all views
        addRepository = findViewById(R.id.addNewRepository);
        recyclerView = findViewById(R.id.recyclerView);
        noDataAvailable = findViewById(R.id.textView);
        listOfSavedRepo = getSharedPreferences("SavedRepository",MODE_PRIVATE);
        listEditor = listOfSavedRepo.edit();
        gson = new Gson();
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.actionbar_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(MainActivity.this,AddRepository.class);
        startActivityForResult(intent,2);
        return true;
    }
}