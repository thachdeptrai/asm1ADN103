package com.example.asm.Interface;

import com.example.asm.Model.CarModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface APIsevice {
    public String DOMAN = "http://10.24.2.234:3000/";

    @GET("/api/list")
    Call<List<CarModel>> getcars();

    @POST("/api/add_xe")
    Call<List<CarModel>> addCar(@Body CarModel xe);


    @PUT("/api/update/{id}")
    Call<CarModel> updateCar(@Path("id") String carId, @Body CarModel updatedCar);

    @DELETE("/api/xoa/{id}")
    Call<List<CarModel>>  deleteCar(@Path("id") String id);

}
