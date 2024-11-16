package com.example.asm.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm.Adapter.CarAdapter;
import com.example.asm.Interface.APIsevice;
import com.example.asm.Model.CarModel;
import com.example.asm.R;
import com.example.asm.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCarActivity extends AppCompatActivity {
    private CarAdapter carAdapter;
    private EditText edtName, edtYear, edtBrand, edtPrice;
    private Button btnSave;
    private ProgressBar progressBar; // Progress bar để hiển thị quá trình tải
    private APIsevice apiService;

    // Khởi tạo API service
    private void initApiService() {
        apiService = RetrofitClient.getRetrofitInstance().create(APIsevice.class);
    }

    // Lưu dữ liệu vào Adapter
    private void updateCarList(CarModel updatedCar) {
        if (carAdapter != null) {
            List<CarModel> carList = carAdapter.getCarModelList();
            for (int i = 0; i < carList.size(); i++) {
                if (carList.get(i).get_id().equals(updatedCar.get_id())) {
                    // Cập nhật thông tin xe trong danh sách
                    carList.set(i, updatedCar);
                    break;
                }
            }
            // Cập nhật lại dữ liệu trong Adapter
            carAdapter.notifyDataSetChanged();
        }
    }

    // Kiểm tra tính hợp lệ của các trường đầu vào
    private boolean isValidInput(String name, String year, String brand, String price) {
        if (name.isEmpty() || year.isEmpty() || brand.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            Integer.parseInt(year);  // Kiểm tra năm là số hợp lệ
            Double.parseDouble(price);  // Kiểm tra giá là số hợp lệ
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Năm sản xuất hoặc giá không hợp lệ!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_car);

        // Lấy dữ liệu xe từ Intent
        String carId = getIntent().getStringExtra("car_id");
        String carName = getIntent().getStringExtra("car_name");
        int carYear = getIntent().getIntExtra("car_year", 0);
        String carBrand = getIntent().getStringExtra("car_brand");
        double carPrice = getIntent().getDoubleExtra("car_price", 0.0);

        // Ánh xạ các EditText và Button
        edtName = findViewById(R.id.edtName);
        edtYear = findViewById(R.id.edtNamSX);
        edtBrand = findViewById(R.id.edtHang);
        edtPrice = findViewById(R.id.edtGia);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);  // Ánh xạ ProgressBar

        // Gán giá trị vào các EditText
        edtName.setText(carName);
        edtYear.setText(String.valueOf(carYear));
        edtBrand.setText(carBrand);
        edtPrice.setText(String.valueOf(carPrice));

        // Khởi tạo API service
        initApiService();

        // Lưu khi nhấn nút "Lưu"
        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String year = edtYear.getText().toString().trim();
            String brand = edtBrand.getText().toString().trim();
            String price = edtPrice.getText().toString().trim();

            if (isValidInput(name, year, brand, price)) {
                CarModel updatedCar = new CarModel(carId, name, Integer.parseInt(year), brand, Double.parseDouble(price));

                // Hiển thị ProgressBar trong khi gửi yêu cầu
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                apiService.updateCar(carId, updatedCar).enqueue(new Callback<CarModel>() {
                    @Override
                    public void onResponse(Call<CarModel> call, Response<CarModel> response) {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);  // Ẩn ProgressBar khi có phản hồi
                        }

                        if (response.isSuccessful()) {
                            Toast.makeText(EditCarActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            updateCarList(updatedCar);  // Cập nhật dữ liệu vào adapter
                            finish();  // Đóng Activity khi cập nhật thành công
                        } else {
                            Toast.makeText(EditCarActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CarModel> call, Throwable t) {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);  // Ẩn ProgressBar khi có lỗi
                        }
                        Toast.makeText(EditCarActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
