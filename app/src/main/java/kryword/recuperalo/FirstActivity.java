package kryword.recuperalo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class FirstActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            Toast.makeText(this, "Se ha conseguido loguear de forma correcta", Toast.LENGTH_SHORT).show();
            TextView tv = findViewById(R.id.userName);
            tv.setText("Conectado como " + currentUser.getDisplayName());
        }else{
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build()
                    )).build(),123);
        }
    }

    public void encontrado(View view){
        Intent intent = new Intent(getApplicationContext(), MapFoundActivity.class);
        startActivity(intent);
    }

    public void perdido(View view){
        Intent intent = new Intent(getApplicationContext(), MapLostActivity.class);
        startActivity(intent);
    }

    public void cerrarSesion(View view){
        AuthUI.getInstance().signOut(getApplicationContext());
        currentUser = null;
        recreate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK){
            Toast.makeText(this, "Se ha conseguido loguear de forma correcta", Toast.LENGTH_SHORT).show();
            TextView tv = findViewById(R.id.userName);
            currentUser = mAuth.getCurrentUser();
            tv.setText("Conectado como " + currentUser.getDisplayName());
        }
    }

    public void listaChats(View view){
        Intent intent = new Intent(getApplicationContext(), ChatListActivity.class);
        startActivity(intent);
    }
}
