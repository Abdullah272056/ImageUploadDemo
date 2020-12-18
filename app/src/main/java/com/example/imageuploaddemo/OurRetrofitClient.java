package com.example.imageuploaddemo;

import com.example.imageuploaddemo.product.ProductDataResponse;


import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface OurRetrofitClient {



    @Multipart
    @POST("api/product")
    Call<ProductDataResponse> uploadImage(@Header("Authorization") String authorization,
                                          @Part MultipartBody.Part image,
                                          @Part("name") RequestBody name,
                                          @Part("price") RequestBody price,
                                          @Part("sellingPrice") RequestBody sellingPrice,
                                          @Part("unit") RequestBody unit,
                                          @Part("stock") RequestBody stock,
                                          @Part("description") RequestBody description
    );


//    @POST("api/product")
//    @FormUrlEncoded
//    Call<ProductDataResponse> UploadImage1(@Header("Authorization") String authorization,
//                                           @Field("image") String image,
//                                           @Field("name") String name,
//                                           @Field("price") Integer price,
//                                           @Field("sellingPrice") Integer sellingPrice,
//                                           @Field("unit") String unit,
//                                           @Field("stock") Integer stock,
//                                           @Field("description") String description
//    );




//    @POST("api/product")
//    @FormUrlEncoded
//    Call<ProductDataResponse> UploadImage2(@Header("Authorization") String authorization,
//                                           @Part File image,
//                                           @Field("name") String name,
//                                           @Field("price") Integer price,
//                                           @Field("sellingPrice") Integer sellingPrice,
//                                           @Field("unit") String unit,
//                                           @Field("stock") Integer stock,
//                                           @Field("description") String description
//
//    );



//    // get customer Count
//    @GET("api/customer/count")
//    Call<CustomerCountResponse> getCustomerCount(@Header("Authorization") String authorization);


}
