package com.example.myfotobook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {
    private List<Album> Albuns;
    public ListAdapter(List<Album> Albuns){
        this.Albuns = Albuns;
    }
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView image;
        public TextView chave;
        public TextView nome;
        public TextView descricao;
        public TextView criadoEm;
        public String idAlbum;


        public MyViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.recycleView_image);
            idAlbum = null;
            chave = v.findViewById(R.id.recycleViewChave);
            nome = v.findViewById(R.id.recycleView_NomeFoto);
            descricao = v.findViewById(R.id.recycleView_DescricaoFoto);
            criadoEm = v.findViewById(R.id.recycleView_DataFoto);
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapter(HomeActivity homeActivity, List<Album> Albuns) {
        this.Albuns = Albuns;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(final ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler_view, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.idAlbum = (Albuns.get(position).getidAlbum());
        holder.chave.setText(Albuns.get(position).getChaveFoto());
        holder.nome.setText(Albuns.get(position).getNomeFoto());
        holder.descricao.setText(Albuns.get(position).getDescricaoFoto());
        holder.criadoEm.setText(Albuns.get(position).getCriadoEm());

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference pathReference = mStorageRef.child(holder.chave.getText()+ ".JPEG");

        final long ONE_MEGABYTE = 1024 * 1024 * 1024;
        pathReference.getBytes(ONE_MEGABYTE).
                addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap photo = BitmapFactory.
                                decodeByteArray(bytes, 0, bytes.length);
                        holder.image.setImageBitmap(photo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

    }

    public void deletItem(String chDB, String chSR){

        DatabaseReference drBD = FirebaseDatabase.getInstance().getReference().child("Fotos/"+ chDB);
        drBD.removeValue();

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference drSF = mStorageRef.child( chSR+".JPEG");
        drSF.delete();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Albuns.size();
    }

}