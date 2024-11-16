package com.example.asm.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.asm.Activity.EditCarActivity;
import com.example.asm.Activity.MainActivity;
import com.example.asm.Interface.APIsevice;
import com.example.asm.Model.CarModel;
import com.example.asm.R;

import java.io.IOException;
import java.util.List;

public class CarAdapter extends BaseAdapter {

    List<CarModel> carModelList;

    Context context;

    //    public CarAdapter (Context context, List<CarModel> carModelList) {
//        this.context = context;
//        this.carModelList = carModelList;
//    }
    private APIsevice apiService;

    public CarAdapter(Context context, List<CarModel> carModelList, APIsevice apiService) {
        this.context = context;
        this.carModelList = carModelList;
        this.apiService = apiService;
    }

    @Override
    public int getCount() {
        return carModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return carModelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    public List<CarModel> getCarModelList() {
        return carModelList;
    }

    public void setCarModelList(List<CarModel> carModelList) {
        this.carModelList = carModelList;
    }
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_car, viewGroup, false);

        // Gán view
        TextView tvName = rowView.findViewById(R.id.tvName);
        TextView tvNamSX = rowView.findViewById(R.id.tvNamSX);
        TextView tvHang = rowView.findViewById(R.id.tvHang);
        TextView tvGia = rowView.findViewById(R.id.tvGia);
        Button btnEdit = rowView.findViewById(R.id.btnEdit);
        Button btnDelete = rowView.findViewById(R.id.btnDelete);

        // Set dữ liệu
        CarModel car = carModelList.get(position);
        tvName.setText(car.getTen());
        tvNamSX.setText(String.valueOf(car.getNamSX()));
        tvHang.setText(car.getHang());
        tvGia.setText(String.valueOf(car.getGia()));

        // Xử lý sự kiện nút Sửa
        btnEdit.setOnClickListener(v -> {
            Log.d("CarAdapter", "Edit button clicked");

            // Truyền context đúng (context phải là Activity)
            Intent intent = new Intent(v.getContext(), EditCarActivity.class); // v.getContext() trả về Activity context
            intent.putExtra("car_id", car.get_id());  // Truyền ID của xe
            intent.putExtra("car_name", car.getTen());  // Truyền tên xe
            intent.putExtra("car_year", car.getNamSX());  // Truyền năm sản xuất
            intent.putExtra("car_brand", car.getHang());  // Truyền hãng xe
            intent.putExtra("car_price", car.getGia());  // Truyền giá xe

            // Kiểm tra context có phải là Activity không
            if (v.getContext() instanceof Activity) {
                v.getContext().startActivity(intent); // Nếu context là Activity, mở activity mới
            } else {
                // Nếu không phải Activity, bạn có thể thêm FLAG_NEW_TASK như sau
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });


//xử lý sk nút xóa
        btnDelete.setOnClickListener(v -> {
            String idToDelete = String.valueOf(car.get_id()); // Chắc chắn `get_id()` trả về đúng giá trị
            Call<List<CarModel>> callDelete = apiService.deleteCar(idToDelete);
            callDelete.enqueue(new Callback<List<CarModel>>() {
                @Override
                public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                    if (response.isSuccessful()) {
                        // Cập nhật lại danh sách sau khi xóa
                        carModelList.remove(position);
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<CarModel>> call, Throwable t) {
                    Log.e("CarAdapter", t.getMessage());
                }
            });
        });


        return rowView;
    }

//    private void showEditDialog(CarModel car, int position) {
//        Log.d("CarAdapter", "showEditDialog called");
//
//        // Kiểm tra xem Activity có còn hoạt động không
//        if (!(context instanceof Activity)) {
//            Log.d("CarAdapter", "Context is not an Activity");
//            return;
//        }
//
//        Activity activity = (Activity) context;
//        if (activity.isFinishing() || activity.isDestroyed()) {
//            Log.d("CarAdapter", "Activity is finishing or destroyed");
//            return;
//        }
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//
//        // Inflate layout cho dialog
//        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_car, null);
//        EditText edtName = dialogView.findViewById(R.id.edtName);
//        EditText edtYear = dialogView.findViewById(R.id.edtNamSX);
//        EditText edtBrand = dialogView.findViewById(R.id.edtHang);
//        EditText edtPrice = dialogView.findViewById(R.id.edtGia);
//
//        // Gán giá trị vào các EditText
//        edtName.setText(car.getTen());
//        edtYear.setText(String.valueOf(car.getNamSX()));
//        edtBrand.setText(car.getHang());
//        edtPrice.setText(String.valueOf(car.getGia()));
//
//        builder.setView(dialogView);
//
//        // Cấu hình các nút của dialog
//        builder.setPositiveButton("Lưu", (dialog, which) -> {
//            // Cập nhật thông tin xe
//            car.setTen(edtName.getText().toString());
//            car.setNamSX(Integer.parseInt(edtYear.getText().toString()));
//            car.setHang(edtBrand.getText().toString());
//            car.setGia(Double.parseDouble(edtPrice.getText().toString()));
//
//            // Kiểm tra apiService trước khi gọi
//            if (apiService != null) {
//                apiService.updateCar(car.get_id(), car).enqueue(new Callback<List<CarModel>>() {
//                    @Override
//                    public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
//                        if (response.isSuccessful() && response.body() != null) {
//                            carModelList.set(position, response.body().get(0));  // Cập nhật danh sách xe
//                            notifyDataSetChanged();  // Cập nhật lại adapter
//                            Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(context, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<CarModel>> call, Throwable t) {
//                        Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } else {
//                Toast.makeText(context, "API service chưa được khởi tạo", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
//
//        // Kiểm tra lại trạng thái Activity trước khi hiển thị Dialog
//        if (!activity.isFinishing() && !activity.isDestroyed()) {
//            builder.create().show();  // Hiển thị dialog
//        } else {
//            Log.d("CarAdapter", "Activity is not running");
//        }
//    }
public void updateData(List<CarModel> newCarList) {
    this.carModelList = newCarList;
    notifyDataSetChanged();  // Cập nhật lại giao diện
}


}