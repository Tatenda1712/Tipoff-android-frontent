package com.example.tipoff.adapters;

import static com.example.tipoff.Baseurl.baseURL;
import static com.example.tipoff.MainActivity.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tipoff.R;
import com.example.tipoff.RetrofitAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TipoffAdapter extends RecyclerView.Adapter<TipoffAdapter.ViewHolder> {
    public JSONArray tipoffs;
    public static ViewGroup parent;
    static Context context;
    String tipoff_id;
    int commentcount=0;
    // FragmentManager fragmentManager;

    Activity activity;
    public TipoffAdapter(JSONArray tipoffs, Context context) {
        this.tipoffs = tipoffs;
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
        View tipoffview = inflater.inflate(R.layout.tippoff_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(tipoffview);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            JSONObject trip = this.tipoffs.getJSONObject(position);
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
        return tipoffs.length();
    }
    public class ViewHolder extends RecyclerView.ViewHolder  {

        public TextView tipoff;
        public Button likes;
        public Button comments ;
        public Button share;
        //  public TextView from;

        View itemView_;
        ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView_=itemView;
            tipoff =itemView.findViewById(R.id.tipoff);
            likes = itemView.findViewById(R.id.likes);
            comments=itemView.findViewById(R.id.comments);
            share=itemView.findViewById(R.id.share);
            //from=itemView.findViewById(R.id.from);
        }

        public void onBindView(JSONObject request,int pos) throws JSONException {

            Activity activity = (Activity) context;

            tipoff.setText(request.getString("tipoff") );
            String id= request.getString("id");
            likes.setText((request.getString("likes")));
            share.setText((request.getString("share")));
            tipoff_id = request.getString("id");
            JSONArray tipoffcomments = request.getJSONArray("comments");

            for (int i=0;i<tipoffcomments.length();i++){
                    commentcount++;
            }
            comments.setText(Integer.toString(commentcount) );
                    share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(baseURL).client(client).build();
                    RetrofitAPI service = retrofit.create(RetrofitAPI.class);
                    Call<ResponseBody> addshare = service.addshare(
                            id
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
                    Call<ResponseBody> addlike = service.addlike(
                            id
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
            comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity) context;
              //      View view = activity.getLayoutInflater().inflate(R.layout.comment_item,null);
                    LayoutInflater inflater = activity.getLayoutInflater();
                    View view = inflater.inflate(R.layout.comment_item, null);
                    TextView tipoff=view.findViewById(R.id.tipoff);
                    EditText mycomment=view.findViewById(R.id.addcomment);
                    ImageButton sendmycomment=view.findViewById(R.id.send_comment);
                    Button tiplikes=view.findViewById(R.id.likes);
                    Button comments =view.findViewById(R.id.comments);
                    Button tipsshare=view.findViewById(R.id.share);
                    FloatingActionButton back= view.findViewById(R.id.back);
                    RecyclerView commen =view.findViewById(R.id.tip_comments);
                    comments.setText(Integer.toString(commentcount));
                    try {
                        tipoff.setText(request.getString("tipoff") );
                        String id= request.getString("id");
                        tiplikes.setText((request.getString("likes")));
                        tipsshare.setText((request.getString("share")));
                        tipoff_id = request.getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                CommentsAdapter    adapter = new CommentsAdapter(tipoffcomments,context);
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                    commen.setLayoutManager(mLayoutManager);
                    commen.setAdapter(adapter);
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.setView(view);
                  //  AlertDialog dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.myFullscreenAlertDialogStyle);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setView(view);
                    alertDialog.setCancelable(false);
                    back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    tipsshare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Retrofit retrofit = new Retrofit.Builder().baseUrl(baseURL).client(client).build();
                            RetrofitAPI service = retrofit.create(RetrofitAPI.class);
                            Call<ResponseBody> addshare = service.addshare(
                                    id
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
                    tiplikes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Retrofit retrofit = new Retrofit.Builder().baseUrl(baseURL).client(client).build();
                            RetrofitAPI service = retrofit.create(RetrofitAPI.class);
                            Call<ResponseBody> addlike = service.addlike(
                                    id
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
                    sendmycomment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String comment=mycomment.getText().toString();
                            Retrofit retrofit = new Retrofit.Builder().client(client).baseUrl(baseURL).build();
                            RetrofitAPI retrofitservice = retrofit.create(RetrofitAPI.class);
                            Call<ResponseBody> addcomment = retrofitservice.addcomment(
                                    comment,id
                            );

                            addcomment.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    try {
                                        JSONObject serverResponse = new JSONObject(response.body().string());
                                        if (serverResponse.getString("data").equals("created")){

                                            View view = activity.getLayoutInflater().inflate(R.layout.done,null);

                                            ImageView icon = view.findViewById(R.id.icon);
                                            TextView title,message;
                                            AppCompatButton ok;
                                            title= view.findViewById(R.id.title);
                                            message = view.findViewById(R.id.message);
                                            ok = view.findViewById(R.id.ok);

                                            icon.setImageDrawable(activity.getResources().getDrawable(R.drawable.completed));
                                            title.setText("Comment received");
                                            message.setText("Comment Saved Successfully. Thank you");
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            AlertDialog alertDialog = builder.create();
                                            alertDialog.setView(view);
                                            ok.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    alertDialog.dismiss();
                                                }
                                            });

                                            alertDialog.show();
                                        }
                                        else if(serverResponse.getString("data").equals("error")){
                                            View view = activity.getLayoutInflater().inflate(R.layout.done,null);
                                            ImageView icon = view.findViewById(R.id.icon);
                                            TextView title,message;
                                            AppCompatButton ok;
                                            title= view.findViewById(R.id.title);
                                            message = view.findViewById(R.id.message);
                                            ok = view.findViewById(R.id.ok);
                                            icon.setImageDrawable(activity.getResources().getDrawable(R.drawable.red_x));
                                            title.setText("Message");
                                            message.setText("Error Saving your tip please try again");
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                            AlertDialog alertDialog = builder.create();

                                            alertDialog.setView(view);

                                            ok.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    alertDialog.dismiss();
                                                }
                                            });

                                            alertDialog.show();
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    t.printStackTrace();
                                    View view = activity.getLayoutInflater().inflate(R.layout.done,null);

                                    ImageView icon = view.findViewById(R.id.icon);
                                    TextView title,message;
                                    AppCompatButton ok;
                                    title= view.findViewById(R.id.title);
                                    message = view.findViewById(R.id.message);
                                    ok = view.findViewById(R.id.ok);
                                    icon.setImageDrawable(activity.getResources().getDrawable(R.drawable.red_x));
                                    title.setText("Message");
                                    message.setText("Network error please try again");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.setView(view);
                                    ok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alertDialog.dismiss();
                                        }
                                    });
                                    alertDialog.show();
                                }
                            });
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();

                }

            });


        }


    }


}
