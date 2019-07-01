package app.m.reportes;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.Console;
import java.util.regex.Pattern;
import app.m.reportes.Retrofit.MyService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    TextView txt_create_account;
    MaterialEditText edt_login_email,edt_login_password;
    Button btn_login;
    MyService myService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myService = Connection.getServiceRemote();

        edt_login_email = findViewById(R.id.edt_email);
        edt_login_password = findViewById(R.id.edt_password);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(edt_login_email.getText().toString(),
                        edt_login_password.getText().toString());

            }
        });

        txt_create_account = findViewById(R.id.txt_create_account);
        txt_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View register_layout = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.register_layout, null);

                new MaterialStyledDialog.Builder(MainActivity.this)
                        .setIcon(R.drawable.ic_user)
                        .setTitle("REGISTRO")
                        .setDescription("Por favor llena todos los campos")
                        .setCustomView(register_layout)
                        .setNegativeText("CANCELAR")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveText("REGISTRAR")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                MaterialEditText edt_register_email = register_layout.findViewById(R.id.edt_email);
                                MaterialEditText edt_register_name = register_layout.findViewById(R.id.edt_name);
                                MaterialEditText edt_register_password = register_layout.findViewById(R.id.edt_password);

                                if(TextUtils.isEmpty(edt_register_email.getText().toString())){
                                    Toast.makeText(MainActivity.this, "Email cannot be null or empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }else if(!validarEmail(edt_register_email.getText().toString())){
                                    Toast.makeText(MainActivity.this, "Email no valido", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(TextUtils.isEmpty(edt_register_name.getText().toString())){
                                    Toast.makeText(MainActivity.this, "Name cannot be null or empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(TextUtils.isEmpty(edt_register_password.getText().toString())){
                                    Toast.makeText(MainActivity.this, "Password cannot be null or empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                registerUser(edt_register_email.getText().toString(),
                                        edt_register_name.getText().toString(),
                                        edt_register_password.getText().toString());
                            }
                        }).show();
            }
        });
    }

    private void registerUser(String email, String name, String password) {
        compositeDisposable.add(myService.registerUser(email, name, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        Toast.makeText(MainActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loginUser(String email, String password){
        if(TextUtils.isEmpty(email)){
            Toast.makeText(MainActivity.this, "Email cannot be null or empty", Toast.LENGTH_SHORT).show();
            return;
        }else if(!validarEmail(email)){
            Toast.makeText(MainActivity.this, "Email no valido", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(MainActivity.this, "Contraseña cannot be null or empty", Toast.LENGTH_SHORT).show();
            return;
        }

        compositeDisposable.add(myService.loginUser(email, password)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(String status) throws Exception {
                Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                if(status.isEmpty()){
                    Toast.makeText(MainActivity.this, "Correo o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Ingreso exitoso " + status, Toast.LENGTH_SHORT).show();
                    Intent pantalla2 = new Intent(MainActivity.this, MainReportActivity.class);
                    startActivity(pantalla2);
                }
            }
        }));
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
