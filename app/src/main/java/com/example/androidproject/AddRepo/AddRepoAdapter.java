package com.example.androidproject.AddRepo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;

import java.util.List;

public class AddRepoAdapter extends RecyclerView.Adapter<AddRepoAdapter.Holder> {
    List<String> nameOfRepo;
    List<String> ownerName;
    List<String> descriptionOfRepo;
    List<String> urlToRepo;
    public AddRepoAdapter(List<String> nameOfRepo, List<String> ownerName, List<String> descriptionOfRepo, List<String> urlToRepo) {
        this.nameOfRepo = nameOfRepo;
        this.ownerName = ownerName;
        this.descriptionOfRepo = descriptionOfRepo;
        this.urlToRepo = urlToRepo;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_repo_layout_cardview,parent,false);
        return new Holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.repoText.setText("Repo Name: " + nameOfRepo.get(position));
        holder.description.setText("Description: " + descriptionOfRepo.get(position));
        //sending repo name and url through intent to external apps
        holder.share.setOnClickListener(click -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String dataToSend = "Repo Name: " + nameOfRepo.get(position) + "\nURL: " + urlToRepo.get(position);
            sendIntent.putExtra(Intent.EXTRA_TEXT, dataToSend);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            click.getContext().startActivity(shareIntent);
        });
        //opening repo url through browser using intent
        holder.cardView.setOnClickListener(click -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToRepo.get(position)));
            click.getContext().startActivity(browserIntent);
        });
    }

    @Override
    public int getItemCount() {
        return nameOfRepo.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        TextView repoText,description;
        Button share;
        CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            repoText = itemView.findViewById(R.id.repoNameAdapTextView);
            description = itemView.findViewById(R.id.descriptionAdapTextView);
            cardView = itemView.findViewById(R.id.cardviewAdap);
            share = itemView.findViewById(R.id.shareButtonAdap);
        }
    }
}
