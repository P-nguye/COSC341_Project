package com.example.cosc341_project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cosc341_project.Crop;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CropAdapter extends RecyclerView.Adapter<CropAdapter.CropViewHolder> {
    private Context context;
    private List<Crop> cropList;
    private String userKey;

    public CropAdapter(Context context, List<Crop> cropList, String userKey) {
        this.context=context;
        this.cropList = cropList;
        this.userKey=userKey;
    }

    @NonNull
    @Override
    public CropViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crop, parent, false);
        return new CropViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CropViewHolder holder, int position) {
        Crop crop = cropList.get(position);

        holder.textCropName.setText(crop.getName());
        holder.textCropType.setText(crop.getType());
        holder.textCropQuantity.setText("Quantity: " + crop.getQuantity());
        holder.btnEditCrop.setOnClickListener(v ->{
            Context context = v.getContext();
            Intent intent = new Intent(context, CreateCrop.class);

            //Launch an editable intent that when flagged: Prepopulates the values
            intent.putExtra("Editing", true);
            intent.putExtra("Crop", crop.getId());
            intent.putExtra("userKey", userKey);
            context.startActivity(intent);
        });
        holder.btnDeleteCrop.setOnClickListener(v ->{
            // Show confirmation dialog
            new AlertDialog.Builder(context)
                    .setTitle("Delete Crop")
                    .setMessage("Are you sure you want to delete this crop?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteCrop(crop.getId(), holder.getAdapterPosition());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
    private void deleteCrop(String cropId, int position) {
        // Delete crop from Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userKey).child("crops").child(cropId);
        ref.removeValue().addOnSuccessListener(aVoid -> {
            // Remove crop from the list and update RecyclerView
            if (position < cropList.size()) {

                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cropList.size() - position);

            } else {
                notifyDataSetChanged(); // Full refresh if the last item is deleted
            }

        }).addOnFailureListener(e -> {
            // Handle failure
            new AlertDialog.Builder(context)
                    .setTitle("Error")
                    .setMessage("Failed to delete crop: " + e.getMessage())
                    .setPositiveButton("OK", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return cropList != null ? cropList.size() : 0;
    }

    static class CropViewHolder extends RecyclerView.ViewHolder {
        TextView textCropName, textCropType, textCropQuantity;
        Button btnEditCrop, btnDeleteCrop;

        public CropViewHolder(@NonNull View itemView) {
            super(itemView);
            textCropName = itemView.findViewById(R.id.crop_name_tv);
            textCropType = itemView.findViewById(R.id.cropType_tv);
            textCropQuantity = itemView.findViewById(R.id.crop_qty_tv);
            btnEditCrop= itemView.findViewById(R.id.edit_crop_btn);
            btnDeleteCrop=itemView.findViewById(R.id.delete_crop_btn);
        }
    }
}