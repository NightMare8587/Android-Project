package com.example.androidproject.AddRepo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidproject.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddRepository extends AppCompatActivity {

    EditText searchName;
    EditText ownerNameEditText;
    Button executeSearch;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_repository);

        initialise();

        executeSearch.setOnClickListener(click -> {
            if(TextUtils.isEmpty(searchName.getText().toString()) || TextUtils.isEmpty(ownerNameEditText.getText().toString())){
                Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            String ownerNameStr = ownerNameEditText.getText().toString();
            String repoNameStr = searchName.getText().toString();

            String urlToQuery =  "https://api.github.com/repos/" + ownerNameStr + "/" + repoNameStr;
            RequestQueue requestQueue = Volley.newRequestQueue(AddRepository.this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToQuery, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    if(response.length() == 2){
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddRepository.this);
                        builder.setTitle("Error")
                                .setMessage("No repository found. Please check your input and try again")
                                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).create();
                        builder.show();
                    }else{
                        Gson gson = new Gson();
                        //checking if data already exist
                        // if exist then add new data to it
                        if(sharedPreferences.contains("data")){
                            Type type = new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType();
                            String str = ownerNameStr + "_" + repoNameStr;
                            HashMap<String, HashMap<String, String>> mainMap = new HashMap<>(gson.fromJson(sharedPreferences.getString("data",""),type));
                            HashMap<String, String> innerMap = new HashMap<>();
                            innerMap.put("ownerName", ownerNameStr);
                            innerMap.put("repoName", repoNameStr);
                            try {
                                innerMap.put("description", response.getString("description"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            innerMap.put("url", "https://github.com/" + ownerNameStr + "/" + repoNameStr);
                            mainMap.put(str, innerMap);


                            editor.putString("data", gson.toJson(mainMap));
                            editor.apply();
                        }else {
                            //no previous data exists so adding new data
                            String str = ownerNameStr + "_" + repoNameStr;
                            HashMap<String, HashMap<String, String>> mainMap = new HashMap<>();
                            HashMap<String, String> innerMap = new HashMap<>();
                            innerMap.put("ownerName", ownerNameStr);
                            innerMap.put("repoName", repoNameStr);
                            try {
                                innerMap.put("description", response.getString("description"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            innerMap.put("url", "https://github.com/" + ownerNameStr + "/" + repoNameStr);
                            mainMap.put(str, innerMap);


                            editor.putString("data", gson.toJson(mainMap));
                            editor.apply();
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(AddRepository.this);
                        builder.setCancelable(false);
                        builder.setTitle("Success")
                                .setMessage("Repository Added Successfully")
                                .setPositiveButton("Exit", (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                    setResult(4);
                                    finish();
                                }).create();
                        builder.show();
                    }

                }
            }, error -> {
                Toast.makeText(this, "Error :(", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(AddRepository.this);
                builder.setTitle("Error")
                        .setMessage("No repository found. Please check your input and try again")
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create();
                builder.show();
            });

            requestQueue.add(jsonObjectRequest);
        });
    }

    private void initialise() {
        sharedPreferences = getSharedPreferences("SavedRepository",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        searchName = findViewById(R.id.searchRepoName);
        ownerNameEditText = findViewById(R.id.searchOwnerName);
        executeSearch = findViewById(R.id.executeSearchButton);
    }
}