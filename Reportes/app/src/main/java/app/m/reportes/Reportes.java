package app.m.reportes;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import app.m.reportes.Retrofit.MyService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class Reportes extends AppCompatActivity {

    MyService myService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd", Locale.getDefault());

    Intent i;
    Bitmap bmp;
    ImageView img;
    Button foto;

    final static int cons = 0;
    @Override

    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);

        final Date date = new Date();

        final String id_usuario = getIntent().getStringExtra("usuario");
        System.out.println("usuario" + id_usuario);

        myService = Connection.getServiceRemote();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Reportes.this);
                View form = getLayoutInflater().inflate(R.layout.formulario_reporte, null);

                final TextView titulo_fomulario = form.findViewById(R.id.title_reporte),
                        descripcion_formulario = form.findViewById(R.id.description_reporte);

                foto = form.findViewById(R.id.foto_reporte);
                img = form.findViewById(R.id.img_web);

                foto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        System.out.println("este esl id" + id);
                        switch (id){
                            case R.id.foto_reporte:
                                System.out.println("ENTRAMOS ACA WE??");
                                i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(i, cons);
                        }
                    }
                });


                mBuilder.setPositiveButton("GUARDAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String hora_fecha = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                    }
                });

                mBuilder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setView(form);

                AlertDialog dialog = mBuilder.create();
                dialog.show();

            }
        });
    }

    private void generar_Reporte(String titulo, String descripcion, File ruta, String id_usuario){
        compositeDisposable.add(myService.createReports(titulo, descripcion, ruta, id_usuario)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Toast.makeText(Reportes.this, "FORMULARIO CREADO", Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            Bundle ext = data.getExtras();
            bmp = (Bitmap) ext.get("data");
            img.setImageBitmap(bmp);
        }
    }

}
