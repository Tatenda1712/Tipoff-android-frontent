package com.example.tipoff;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitAPI {
    @POST("tipoff/create")
    @FormUrlEncoded
    Call<ResponseBody> addtip(@Field("tipoff") String tipoff);

    @GET("gettipoffs")
    Call<ResponseBody> getTipoffs();

    @POST("addshare")
    @FormUrlEncoded
    Call<ResponseBody> addshare(@Field("tipoff_id") String tipoff_id);

    @POST("addlike")
    @FormUrlEncoded
    Call<ResponseBody> addlike(@Field("tipoff_id") String tipoff_id);

    @POST("addcomment")
    @FormUrlEncoded
    Call<ResponseBody> addcomment(@Field("comment") String comment,@Field("tipoff_id") String tipoff_id);

    @POST("addcommentshare")
    @FormUrlEncoded
    Call<ResponseBody> addcommentshare(@Field("comment_id") String comment_id);

    @POST("addcommentlike")
    @FormUrlEncoded
    Call<ResponseBody> addcommentlike(@Field("comment_id") String comment_id);
}
