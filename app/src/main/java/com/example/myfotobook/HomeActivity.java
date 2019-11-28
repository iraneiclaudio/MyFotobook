package com.example.myfotobook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myfotobook.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    public static final String GOOGLE_ACCOUNT = "google_account";
    public TextView profileName, profileEmail;
    private ImageView profileImage;
    private GoogleSignInClient mgoogleSignInClient;
    private Button signOut;
    private String idUser;

// Reclclerview
    RecyclerView recyclerView;
    ArrayList<Album> ListFotos;
    ListAdapter adapter;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

// cabeçalho
        profileName = findViewById(R.id.user_nome);
        profileEmail = findViewById(R.id.user_email);
        profileImage = findViewById(R.id.user_image);
        signOut=findViewById(R.id.sign_out);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mgoogleSignInClient = GoogleSignIn.getClient(this, gso);


        GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        Picasso.get().load(googleSignInAccount.getPhotoUrl()).centerInside().fit().into(profileImage);
        profileName.setText(googleSignInAccount.getDisplayName());
        profileEmail.setText(googleSignInAccount.getEmail());

        idUser = googleSignInAccount.getId();

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          /*
          Sign-out is initiated by simply calling the googleSignInClient.signOut API. We add a
          listener which will be invoked once the sign out is the successful
           */
                mgoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //On Succesfull signout we navigate the user back to LoginActivity
                        Intent intent=new Intent(HomeActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
        });
// fim cabeçalho
// Começo da recycler view

        recyclerView = (RecyclerView) findViewById(R.id.main_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ListFotos = new ArrayList<Album>();

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Fotos" + idUser); // idUser do email
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ListFotos.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Album album = dataSnapshot1.getValue(Album.class);
                    album.setidAlbum(dataSnapshot1.getKey());
                    ListFotos.add(album);
                }
                adapter = new ListAdapter(HomeActivity.this, ListFotos);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Deleção do do item

        final ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                position = viewHolder.getAdapterPosition();
                int a = 0;
                for (Album item : ListFotos){
                    if (a == viewHolder.getAdapterPosition()){
                        adapter.deletItem(idUser +"/"+ item.getidAlbum(), item.getChaveFoto());
                    }
                    a++;
                }
                ListFotos.remove(position);
                adapter.notifyDataSetChanged();

            }
        });
        helper.attachToRecyclerView(recyclerView);
    }

    public void criarFoto(View view) {
        DadosFoto.login = idUser; // idUser
        Intent dadosFoto = new Intent(this, DadosFoto.class);
        startActivity(dadosFoto);
    }
}