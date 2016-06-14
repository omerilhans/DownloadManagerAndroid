package android.projects.downloadmanager;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    EditText et1, et2;
    ProgressBar pb;
    TextView tv;
    Button btn;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et1 = (EditText) findViewById(R.id.editText);
        et2 = (EditText) findViewById(R.id.editText2);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        tv = (TextView) findViewById(R.id.textView);
        btn = (Button) findViewById(R.id.button);
    }

    public void doOp(View v)
    {
        new AsyncTask<String, Integer, String>()
        {
            int fileSize = 0, okunan = 0, toplamIndirilen = 0;

            protected void onPreExecute()
            {
                btn.setEnabled(false);
                btn.setText("Lütfen Bekleyin");
            }

            protected String doInBackground(String... params)
            {
                try
                {
                    String adr = params[0];
                    String loc = params[1];
                    URL url = new URL(adr);
                    URLConnection uc = url.openConnection();

                    fileSize = uc.getContentLength();

                    InputStream is = uc.getInputStream();
                    File f = new File(Environment.getExternalStorageDirectory(), loc);
                    FileOutputStream fos = new FileOutputStream(f);

                    byte tmp[]= new byte[4096];

                    while ( (okunan = is.read(tmp)) != -1)
                    {
                        toplamIndirilen += okunan;
                        fos.write(tmp, 0,okunan);

                        publishProgress();
                    }

                    fos.close();
                    is.close();
                    return "OK";
                } catch (Exception e)
                {
                    Log.e("x","Download Error : "+e.toString());
                    return e.toString();
                }
            }

            protected void onPostExecute(String s)
            {
                btn.setEnabled(true);
                btn.setText("Başla");

                if (s.equals("OK"))
                {
                    Toast.makeText(MainActivity.this,"İşlem Tamam",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                    adb.setTitle("Hata")
                            .setMessage("Detay : "+s)
                            .setPositiveButton("OK",null)
                            .show();
                }
            }

            protected void onProgressUpdate(Integer... values)
            {
                pb.setMax(fileSize);
                pb.setProgress(toplamIndirilen);
                tv.setText(toplamIndirilen+" Bytes");
            }
        }.execute(et1.getText().toString(), et2.getText().toString());
    }
}
