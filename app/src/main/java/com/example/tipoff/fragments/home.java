package com.example.tipoff.fragments;

import static com.example.tipoff.Baseurl.baseURL;
import static com.example.tipoff.MainActivity.client;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tipoff.MainActivity;
import com.example.tipoff.R;
import com.example.tipoff.RetrofitAPI;
import com.example.tipoff.adapters.TipoffAdapter;
import com.example.tipoff.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home extends Fragment {
    FragmentHomeBinding homeBinding;
    JSONArray unfilter_tipoffs;
    JSONArray filtered_tippoffs;
    TipoffAdapter adapter;
    Handler handler;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home.
     */
    // TODO: Rename and change types and number of parameters
    public static home newInstance(String param1, String param2) {
        home fragment = new home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeBinding=FragmentHomeBinding.inflate(inflater,container,false);
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                System.out.println(manager.getBackStackEntryCount());

                if (manager.getBackStackEntryCount() !=0) {
                    // If there are back-stack entries, leave the FragmentActivity
                    // implementation take care of them.
                    manager.popBackStack();

                } else {
                    // Otherwise, ask user if he wants to leave :)
                    new AlertDialog.Builder(getContext())
                            .setTitle("Really Exit?")
                            .setMessage("Are you sure you want to exit?")
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface arg0, int arg1) {
                                    // MainActivity.super.onBackPressed();
                                    getActivity().finish();
                                    getActivity().moveTaskToBack(true);
                                }
                            }).create().show();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        homeBinding.newTipoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                LayoutInflater li = LayoutInflater.from(getContext().getApplicationContext());
                View mView = li.inflate(R.layout.add_tip,null);
                // final EditText method = (EditText)mView.findViewById(R.id.method);
                final EditText newtipoff = (EditText)mView.findViewById(R.id.tip);
                ImageButton btn_cancel = (ImageButton) mView.findViewById(R.id.cancel);
                ImageButton btn_okay = (ImageButton) mView.findViewById(R.id.send_tip);
                alert.setView(mView);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                btn_okay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        homeBinding.progress.setVisibility(View.VISIBLE);
                        String tipoff=newtipoff.getText().toString();
                        Retrofit retrofit = new Retrofit.Builder().client(client).baseUrl(baseURL).build();
                        RetrofitAPI retrofitservice = retrofit.create(RetrofitAPI.class);
                        Call<ResponseBody> addtipoff = retrofitservice.addtip(
                                tipoff
                        );

                        addtipoff.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                homeBinding.progress.setVisibility(View.GONE);
                                try {
                                    JSONObject serverResponse = new JSONObject(response.body().string());
                                    if (serverResponse.getString("data").equals("created")){

                                        View view = getLayoutInflater().inflate(R.layout.done,null);

                                        ImageView icon = view.findViewById(R.id.icon);
                                        TextView title,message;
                                        AppCompatButton ok;
                                        title= view.findViewById(R.id.title);
                                        message = view.findViewById(R.id.message);
                                        ok = view.findViewById(R.id.ok);

                                        icon.setImageDrawable(getResources().getDrawable(R.drawable.completed));
                                        title.setText("Tip received");
                                        message.setText("Tip Saved Successfully. Thank you");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.setView(view);
                                        ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                alertDialog.dismiss();
                                                getParentFragmentManager().popBackStackImmediate();
                                            }
                                        });

                                        alertDialog.show();
                                    }
                                    else if(serverResponse.getString("data").equals("error")){
                                        View view = getLayoutInflater().inflate(R.layout.done,null);
                                        ImageView icon = view.findViewById(R.id.icon);
                                        TextView title,message;
                                        AppCompatButton ok;
                                        title= view.findViewById(R.id.title);
                                        message = view.findViewById(R.id.message);
                                        ok = view.findViewById(R.id.ok);
                                        icon.setImageDrawable(getResources().getDrawable(R.drawable.red_x));
                                        title.setText("Message");
                                        message.setText("Error Saving your tip please try again");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

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
                                homeBinding.progress.setVisibility(View.GONE);
                                    View view = getLayoutInflater().inflate(R.layout.done,null);

                                    ImageView icon = view.findViewById(R.id.icon);
                                    TextView title,message;
                                    AppCompatButton ok;
                                    title= view.findViewById(R.id.title);
                                    message = view.findViewById(R.id.message);
                                    ok = view.findViewById(R.id.ok);
                                    icon.setImageDrawable(getResources().getDrawable(R.drawable.red_x));
                                    title.setText("Message");
                                    message.setText("Network error please try again");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

        homeBinding.searchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                try {
                    filter(editable.toString(),adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        getTippoffs();
        handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getTippoffs2();
                handler.postDelayed(this::run,5000);
            }

        },5000);
        return homeBinding.getRoot();
    }
    void getTippoffs(){
        homeBinding.progress.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseURL).client(MainActivity.client).build();
        RetrofitAPI service = retrofit.create(RetrofitAPI.class);
        Call<ResponseBody> gettipoffs = service.getTipoffs();

        gettipoffs.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                homeBinding.progress.setVisibility(View.GONE);
                if (response.code()==200){

                    try {
                        JSONObject server_response= new JSONObject(response.body().string().toString());
                        unfilter_tipoffs=server_response.getJSONArray("data");
                        adapter = new TipoffAdapter(unfilter_tipoffs,getContext());
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                        homeBinding.tipoffs.setLayoutManager(mLayoutManager);
                        homeBinding.tipoffs.setAdapter(adapter);

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    Toast.makeText(getContext(), "Error.", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    void getTippoffs2(){
        // binding.progress.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseURL).client(MainActivity.client).build();
        RetrofitAPI service = retrofit.create(RetrofitAPI.class);
        Call<ResponseBody> gettipoffs = service.getTipoffs();

        gettipoffs.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //     binding.progress.setVisibility(View.GONE);
                if (response.code()==200){

                    try {
                        JSONObject server_response= new JSONObject(response.body().string().toString());
                        unfilter_tipoffs=server_response.getJSONArray("data");
                        adapter = new TipoffAdapter(unfilter_tipoffs,getContext());
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                        homeBinding.tipoffs.setLayoutManager(mLayoutManager);
                        homeBinding.tipoffs.setAdapter(adapter);

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                }
                else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    void filter(String query,TipoffAdapter adapter) throws JSONException {
        filtered_tippoffs= new JSONArray();
        for (int i = 0; i< unfilter_tipoffs.length();i++) {
            if (unfilter_tipoffs.getString(i).toLowerCase().trim().contains(query.toLowerCase())) {
                filtered_tippoffs.put(unfilter_tipoffs.getJSONObject(i));
            }
        }

        adapter.tipoffs=filtered_tippoffs;
        adapter.notifyChange();
    }
}