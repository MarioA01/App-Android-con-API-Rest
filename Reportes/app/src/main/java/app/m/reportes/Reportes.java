package app.m.reportes;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.m.reportes.Retrofit.MyService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.os.Environment.getExternalStoragePublicDirectory;


public class Reportes extends AppCompatActivity {

    MyService myService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    String currentPhotoPath;

    TextView titulo_fomulario;
    TextView descripcion_formulario;

    Intent takePictureIntent;
    Bitmap bmp;

    File file_image;

    ImageView img;
    Button foto;

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

                titulo_fomulario = form.findViewById(R.id.title_reporte);
                descripcion_formulario = form.findViewById(R.id.description_reporte);

                foto = form.findViewById(R.id.foto_reporte);
                img = form.findViewById(R.id.img_web);

                try{
                    foto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //tomarfoto();
                            System.out.println("ESTOY EN LA CAMARA XD");
                            dispatchTakePictureIntent(id_usuario);
                        }
                    });

                }catch (Exception ex){
                    System.out.println("ERROR AL SALIR DEL BOTON");
                }
                mBuilder.setPositiveButton("GUARDAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        generar_Reporte(titulo_fomulario.getText().toString(), descripcion_formulario.getText().toString(), id_usuario , file_image);
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

    private void generar_Reporte(String titulo, String descripcion, String id_usuario, File fotografia){
        String ruta_imagen = fotografia.toString();
        System.out.println("este es la rutal del archvio\n" + ruta_imagen);

        System.out.println(fotografia.isFile());
        compositeDisposable.add(myService.createReports(titulo, descripcion, ruta_imagen, id_usuario)
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
            //Bundle ext = data.getExtras();
            //bmp = (Bitmap) ext.get("data");
            //img.setImageBitmap(bmp);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent(String id_usuario) {
        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            file_image = null;
            try {
                file_image = createImageFile();
            } catch (IOException ex) {
                System.out.println("ERRO AL CREAR LA IMAGEN");
            }
            if (file_image != null) {
                Uri photoURI = FileProvider.getUriForFile(Reportes.this,
                        "app.m.reportes",
                        file_image);
                try{
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                }catch (Exception ex){
                    System.out.println("ERRO AL CARGAR LA VISTA");
                }
                try{
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }catch (Exception ex){
                    System.out.println("ERRR AL INICIAR LA VISTA DE LA FOTOGRAFIA ");
                }
            }
            //String titulo, String descripcion, String id_usuario, File fotografia
            System.out.println("LLEGAMOS AQUI PAPU?");
            //
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}
