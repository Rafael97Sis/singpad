package com.example.signpad;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kyanogen.signatureview.SignatureView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Bitmap bitmap;
    int path;
    private RecyclerView RecyclerView;
    SignatureView SignatureView;
    ImageView campo_imageView;
    private EditText campoNome ;
    private EditText campoCPF ;

    private static final int CREATEPDF = 1 ;

    //sigPad
    private static final String Imagem_DIRECTORY="/signdemo";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SignatureView = findViewById(R.id.campo_SignatureView);
        campo_imageView = findViewById(R.id.image_View);
        campoCPF = findViewById(R.id.editCPF);
        campoNome = findViewById(R.id.editNome);

      //  bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gfgimage);
       // scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);

      //  Pessoa NovaPessoa = new Pessoa(campo_imageView.getImageMatrix(),campo_SignatureView.gets);

        //Requisicao de permisao e acesso a Camera
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
    }
         public void criarPdf(String title ) {

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_TITLE, title);
            startActivityForResult(intent,CREATEPDF);

        }

            private String saveImage (Bitmap myBitmap){
                Toast.makeText(this, " estou aqui save img ", Toast.LENGTH_SHORT).show();

                      ByteArrayOutputStream bytes=new ByteArrayOutputStream();
                      myBitmap.compress(Bitmap.CompressFormat.JPEG, 100,bytes);
                      File wallpaperDirectory = new File(Environment.getExternalStorageDirectory()+ Imagem_DIRECTORY);

                   if (!wallpaperDirectory.exists()){
                       wallpaperDirectory.mkdir();
                       Log.d("captura",wallpaperDirectory.toString());
                        }
                       File f = new File (wallpaperDirectory, Calendar.getInstance().getTimeInMillis()+".jpg");
                     try {
                      f.createNewFile();
                      FileOutputStream fo=new FileOutputStream(f);
                      fo.write(bytes.toByteArray());
                      MediaScannerConnection.scanFile( MainActivity.this, new String[]{f.getPath()},new String[]{"image/jpeg"}, null);
                 fo.close();
                   f.getAbsolutePath();
                      return f.getAbsolutePath();
                        }catch (IOException e){
                          e.printStackTrace();
                  }
                    return "" ;
                      }

            private Bitmap fotoDoRecyclerView(RecyclerView view) {

                RecyclerView.Adapter adapter = view.getAdapter();

                Bitmap bitmapPronto = null;

                if (adapter!= null){
                    Paint paint = new Paint();
                    int tamanhoDaLista = adapter.getItemCount();
                    int altura = 0 ;
                    int alturaVolatil = 0 ;
                    final int tamanhoMaximoDoArquivo = ((int)Runtime.getRuntime().maxMemory()/1024);
                    final int tamanhoDoCache = tamanhoMaximoDoArquivo / 8;
                    LruCache<String, Bitmap> bitmapCache = new LruCache<>(tamanhoDoCache);

                    for (int x = 0 ; x < tamanhoDaLista; x++){

                        RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(x));
                        adapter.onBindViewHolder(holder, x);
                        holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(),View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));
                        holder.itemView.layout(0,0,
                                holder.itemView.getMeasuredWidth(),
                                holder.itemView.getMeasuredHeight());
                        holder.itemView.setDrawingCacheEnabled(true);
                        holder.itemView.buildDrawingCache();

                        Bitmap cacheDoBitMap = holder.itemView.getDrawingCache();
                        if(cacheDoBitMap != null){
                            bitmapCache.put(String.valueOf(x), cacheDoBitMap);
                        }

                        altura += holder.itemView.getMeasuredHeight();

                    }
                    bitmapPronto = bitmap.createBitmap(view.getMeasuredWidth(), altura,Bitmap.Config.ARGB_8888);

                    Canvas pagina = new Canvas(bitmapPronto);
                    pagina.drawColor(Color.WHITE);

                    for(int x = 0 ; x < tamanhoDaLista; x++){
                        Bitmap bitmap = bitmapCache.get(String.valueOf(x));
                        pagina.drawBitmap(bitmap, 0,alturaVolatil,paint);
                        alturaVolatil += bitmap.getHeight();
                        bitmap.recycle();
                    }
                }
                return bitmapPronto;
            }

           @Override
           public boolean onOptionsItemSelected(MenuItem item) {
               switch (item.getItemId()) {
                   case R.id.menu_apaga_assinatura:
                      SignatureView.clearCanvas();
                      Toast.makeText(this, " Campo Assinatura Limpo ", Toast.LENGTH_SHORT).show();
                      // path=Integer.parseInt(saveImage(bitmap));
                    break;

                   case R.id.menu_apaga_dados:
                       EditText nome = findViewById(R.id.editNome);
                       nome.setText("");
                       EditText cpf = findViewById(R.id.editCPF);
                       cpf.setText("");
                       Toast.makeText(getApplicationContext(), " Campos Nome e CPF Limpo ", Toast.LENGTH_SHORT).show();
                    break;

                   case R.id.menu_gerar_foto:
                       Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                       startActivityForResult(intent, 100);
                    break;

                   case R.id.menu_gerar_pdf:
                       // nome do Arquivo = nome + CPF
                       criarPdf(campoNome.getText().toString() + "-" + campoCPF.getText().toString());
                       Toast.makeText(this, "Gerando PDF", Toast.LENGTH_SHORT).show();
                    break;
                   }
                    // return  onCreateOptionsMenu(null);
                    //return false;
                    return super.onOptionsItemSelected(item);

                }
           @Override
           public boolean onCreateOptionsMenu(Menu menu){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
            return true;
            }

    @Override
    protected void onActivityResult (int resquestCode,int resultCode, @Nullable Intent data) {
        super.onActivityResult(resquestCode, resultCode, data);

        // Foto Capturada Retrona no ImagemViwer
        if (resquestCode == 100) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            campo_imageView.setImageBitmap(bitmap);
        }

        if (resquestCode == CREATEPDF) {

            if (data.getData() != null) {
                if (!(TextUtils.isEmpty(campoCPF.getText())) && !(TextUtils.isEmpty(campoNome.getText()))) {
                    Uri caminhoDoArquivo = data.getData();

                    String editCPF = campoCPF.getText().toString();
                    String editNome = campoNome.getText().toString();


                    generatePDFbtn = findViewById(R.id.idBtnGeneratePDF);

                    foto =    campo_imageView
                    bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_photo_camera);
                    scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);

                    // Retorna os Dados  da Assinatura
                    //bitmap = SignatureView.getSignatureBitmap();

                                        //ImageView imageView = campo_imageView.getImageMatrix().toString();
                    // String foto01 = campo_imageView.getImageMatrix().toString();
                    // ImageView imageView = campo_imageView.setImageBitmap(bitmap);
                    // SignatureView SignatureView2 = campo_SignatureView.getSignatureBitmap(bitmap);

                    // Cria Documento PDF
                    PdfDocument pdfDocument = new PdfDocument();
                    Paint paint = new Paint();                // Parametro Do Tamanho do Arquivo PDF - A4
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1240, 1754, 1).create();
                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                    Canvas canvas = page.getCanvas();

                    // Texto Incluido no PDF  - Cabecalho
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setTextSize(28f); // Fonte Do Cabeçalho
                    paint.setFakeBoldText(true);
                    canvas.drawText("TERMO DE CONSENTIMENTO DE COLETA DE DADOS", pageInfo.getPageWidth() / 2, 80, paint);

                    //Texto Incluido no PDF - Corpo
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setTextSize(24f);
                    paint.setFakeBoldText(false);
                    canvas.drawText("Este termo tem por finalidade registrar a manifestação livre, informada e inequivocada pela qual o titular  " , pageInfo.getPageWidth() /2 ,150,paint);
                    canvas.drawText("concorda com o termo de seu dados  pessoais para finalidades especificas em conformidade com a Lei ",pageInfo.getPageWidth() /2, 175, paint);
                    canvas.drawText("Nº 13.709 - Lei Geral de proteção de dados Pessoais (LGPD). Ao aceitar o presente termo, manifesta-se o ",pageInfo.getPageWidth() /2,200,paint);
                    canvas.drawText ("consentimento  de que a empresa Arcom S/A, inscrita no CNPJ sob o N° 25.769.266/0001-24, determina  ",pageInfo.getPageWidth()/2, 225,paint );
                    paint.setTextAlign(Paint.Align.LEFT);
                    paint.setTextSize(24f);
                    paint.setFakeBoldText(false);
                    canvas.drawText("os seguintes dados pessoais do Visitante: ", 50, 250,paint );

                    // Dados Incluido no Termo  Vindo dos campos Preenchidos.
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setTextSize(24f);
                    paint.setFakeBoldText(true);
                    canvas.drawText("Nome : "  +editNome , pageInfo.getPageWidth() /2, 300, paint);
                    canvas.drawText("CPF : " +editCPF,  pageInfo.getPageWidth() /2, 350, paint);
                   // Canvas = canvas
                    //Foto Aqui
                    canvas.
                   // canvas.drawBitmap(foto01, );

                   // canvas.drawBitmap(imageView.);
                    //canvas.drawPicture(SignatureView2);

                    //canvas.drawLine(48, 80, pageInfo.getPageWidth()-100,90,paint);
                   // canvas.drawLine(48, 110, pageInfo.getPageWidth()-100,120,paint);

                    //Texto Incluido no PDF - Corpo
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setTextSize(24f);
                    paint.setFakeBoldText(false);
                    canvas.drawText("Estes dados tem por finalidade unica o registro de acesso do individuo na portaria da empresa,  ", pageInfo.getPageWidth() / 2, 950,paint );
                    canvas.drawText("colaborando nas medidas de segurança interna adotadas por esta sociedade de empresa os dados ", pageInfo.getPageWidth() / 2, 975,paint );
                    canvas.drawText("não serão  compartilhados com outras empresas, permanecendo arquivos no banco de dados da ", pageInfo.getPageWidth() / 2, 1000,paint );
                    canvas.drawText("Arcom S/A seguindo os  protocolos de seguranca necessarios para a garantia de não violacao das ", pageInfo.getPageWidth() / 2, 1025,paint );
                    canvas.drawText("informacões existentes. Assim, pela  ciencia dos Termos deste documento, a Assinatura respresenta", pageInfo.getPageWidth() / 2, 1050,paint );

                    paint.setTextAlign(Paint.Align.LEFT);
                    paint.setTextSize(24f);
                    paint.setFakeBoldText(false);
                    canvas.drawText("a concordancia com informacões prestadas",70,1075,paint);

                    canvas.drawLine(50, 100, pageInfo.getPageWidth()-100,100,paint);
                    canvas.drawLine(70, 1500, pageInfo.getPageWidth()-100,1500,paint);

                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setTextSize(24f);
                    paint.setFakeBoldText(false);
                    canvas.drawText("Uberlândia",pageInfo.getPageWidth()/2,1525,paint);

                    pdfDocument.finishPage(page);
                    gravarPdf(caminhoDoArquivo,pdfDocument);
                }
            }
        }
    }
    private void gravarPdf(Uri caminhoDoArquivo, PdfDocument pdfDocument ){
        try {
           BufferedOutputStream stream = new BufferedOutputStream(getContentResolver().openOutputStream(caminhoDoArquivo));
           pdfDocument.writeTo(stream);
           pdfDocument.close();
           stream.flush();
            Toast.makeText(this, "pdf Gravado",Toast.LENGTH_LONG ).show();
        }catch (FileNotFoundException e){
            Toast.makeText(this, "erro De Arquivo",Toast.LENGTH_LONG).show();
        }catch (IOException e ){
            Toast.makeText(this, " Erro De Entrada e Saida", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, " Erro Desconhecido"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}