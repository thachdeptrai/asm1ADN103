package com.example.asm.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asm.Adapter.CarAdapter;
import com.example.asm.Interface.APIsevice;
import com.example.asm.Model.CarModel;
import com.example.asm.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class MainActivity extends AppCompatActivity {

    ListView lvMain;
    List<CarModel> listCarModel;

    CarAdapter carAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lvMain = findViewById(R.id.listviewMain);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIsevice.DOMAN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIsevice apiService = retrofit.create(APIsevice.class);

        Call<List<CarModel>> call = apiService.getcars();

        call.enqueue(new Callback<List<CarModel>>() {
            @Override
            public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                if (response.isSuccessful()) {
                    listCarModel = response.body();
                    carAdapter = new CarAdapter(getApplicationContext(), listCarModel, apiService);
                    lvMain.setAdapter(carAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<CarModel>> call, Throwable t) {
                Log.e("Main", t.getMessage());
            }
        });

        findViewById(R.id.btn_add).setOnClickListener(v -> {
            CarModel xe = new CarModel("Xe 1411", 2023, "Toyota", 1200);

            Call<List<CarModel>> callAddXe = apiService.addCar(xe);


            callAddXe.enqueue(new Callback<List<CarModel>>() {
                @Override
                public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                    if (response.isSuccessful()) {

                        listCarModel.clear();

                        listCarModel.addAll(response.body());

                        carAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<CarModel>> call, Throwable t) {
                    Log.e("Main", t.getMessage());
                }
            });
        });

    }
}