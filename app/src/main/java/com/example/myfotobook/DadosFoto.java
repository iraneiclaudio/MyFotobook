package com.example.myfotobook;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class DadosFoto extends AppCompatActivity {

    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    final Integer CODIGO_CAMERA = 1;
    private ImageView imageView;
    private EditText nomeFoto;
    private EditText descricaoFoto;
    private Album album;

    public static String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhes_foto);
        imageView = findViewById(R.id.imageViewDetalhe);

        abrirCamera(new View(this));
    }

    public void abrirCamera(View view) {
        Intent intent = new Intent(
                "android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, CODIGO_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_CAMERA && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            imageView.setImageBitmap(photo); // printando a imagem na tela
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void salvarStorage(View v){

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String uniqueID = UUID.randomUUID().toString(); // criando um nome unico
        nomeFoto = (EditText)findViewById(R.id.nomeFotoDetalhe);
        descricaoFoto = (EditText)findViewById(R.id.descricaoFotoDetalhe);

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).
                getBitmap(); // convertendo a imagem pra bitmap

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] arquivo = baos.toByteArray(); // criando um array de bytes

        // envio do arquivo para o storage
        UploadTask uploadTask = mStorageRef.child(uniqueID + ".JPEG").putBytes(arquivo);

        String idUser = login;

        album = new Album("0", uniqueID, nomeFoto.getText().toString(), descricaoFoto.getText().toString(), idUser );
        //salvamento dados no bd do firebase
        mDatabase.child("Fotos" + idUser).push().setValue(album);

        // verifica se foi para o storage
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(),"Erro",
                        Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.
                TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot
                                          taskSnapshot) {
                Toast.makeText(getApplicationContext(),"Sucesso",
                        Toast.LENGTH_LONG).show();
            }
        });

        voltarMain(new View(this));

    }
    public void voltarMain(View view) {
        Intent voltarMain = new Intent(this, MainActivity.class);
        startActivity(voltarMain);
    }
}