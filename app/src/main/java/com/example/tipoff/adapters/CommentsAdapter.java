package com.example.tipoff.adapters;

import static com.example.tipoff.Baseurl.baseURL;
import static com.example.tipoff.MainActivity.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tipoff.R;
import com.example.tipoff.RetrofitAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    public JSONArray orders;
    public static ViewGroup parent;
    static Context context;
    String tipoff_id;
    // FragmentManager fragmentManager;

    Activity activity;
    public CommentsAdapter(JSONArray trips, Context context) {
        this.orders = trips;
        this.context = context;
        activity = (Activity) context;
        //  this.fragmentManager =fragmentManager;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        this.parent = parent;

        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View studentView = inflater.inflate(R.layout.comment, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(studentView);

        return viewHolder;

    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            JSONObject trip = this.orders.getJSONObject(position);
            holder.onBindView(trip,position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void notifyChange(){
        this.notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return orders.length();
    }
    public class ViewHolder extends RecyclerView.ViewHolder  {

        public TextView comment;
        public Button likes;
        public Button comments ;
        public Button share;
        //  public TextView from;

        View itemView_;
        ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView_=itemView;
            comment = itemView.findViewById(R.id.comment);
            likes = itemView.findViewById(R.id.likes);
            comments=itemView.findViewById(R.id.comments);
            share=itemView.findViewById(R.id.share);
            //from=itemView.findViewById(R.id.from);
        }

        public void onBindView(JSONObject request,int pos) throws JSONException {

            Activity activity = (Activity) context;

            comment.setText(request.getString("comment") );
            String commentid= request.getString("id");
            likes.setText((request.getString("likes")));
            share.setText((request.getString("share")));

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(baseURL).client(client).build();
                    RetrofitAPI service = retrofit.create(RetrofitAPI.class);
                    Call<ResponseBody> addshare = service.addcommentshare(
                            commentid
                    );

                    addshare.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                JSONObject serverResponse = new JSONObject(response.body().string());
                                if (serverResponse.getString("data").equals("created")){

                                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    String shareBody = null;
                                    try {
                                        shareBody = request.getString("tipoff");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Zim Tipoff");
                                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                    context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                                }
                                else if(serverResponse.getString("data").equals("error")) {
                                    Toast.makeText(context, "network error", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });

                }
            });
            likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(baseURL).client(client).build();
                    RetrofitAPI service = retrofit.create(RetrofitAPI.class);
                    Call<ResponseBody> addlike = service.addcommentlike(
                            commentid
                    );

                    addlike.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                JSONObject serverResponse = new JSONObject(response.body().string());
                                if (serverResponse.getString("data").equals("created")){

                                }
                                else if(serverResponse.getString("data").equals("error")) {
                                    Toast.makeText(context, "network error", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });

                }
            });
       }
    }
}
