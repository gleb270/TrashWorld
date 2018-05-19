package glebshanshin.trashworld;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StorageActivity extends Activity {
    LinearLayout lin1, lin2;
    TextView text1, text2;
    String code1, code2;
    DBHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;
    private final String server = "https://gleb2700.000webhostapp.com";
    private Gson gson = new GsonBuilder().create();
    private Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(server)
            .build();
    private check che = retrofit.create(check.class);
    private delete del = retrofit.create(delete.class);
    private int now;
    private long TSHc;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.storage_main);
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        init(db);
    }

    public void init(SQLiteDatabase db) {
        cursor = db.query("Data", null, null, null, null, null, null);
        cursor.moveToFirst();
        TSHc = cursor.getLong(1);
        code1 = cursor.getString(20);
        code2 = cursor.getString(21);
        cursor.close();
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        lin1 = findViewById(R.id.button2);
        lin2 = findViewById(R.id.button3);
        checkexistingall();
    }

    private void checkexistingall() {
        if (!code1.equals("0")) {
            checkexisting(code1, 1);
        }
        if (!code2.equals("0")) {
            checkexisting(code2, 2);
        }
        if (code1.equals("0")) {
            lin1.setBackground(getDrawable(R.drawable.emptyqr));
        } else {
            lin1.setBackground(getDrawable(R.drawable.lookqr));
            text1.setText(code1.substring(2, 6));
        }
        if (code2.equals("0")) {
            lin2.setBackground(getDrawable(R.drawable.emptyqr));
        } else {
            lin2.setBackground(getDrawable(R.drawable.lookqr));
            text2.setText(code2.substring(2, 6));
        }
    }

    private void checkexisting(final String QR, final int num) {
        HashMap<String, String> postDataParams = new HashMap<String, String>();
        postDataParams.put("code", QR);
        Call<Object> call = che.performPostCall(postDataParams);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                HashMap<String, String> map = gson.fromJson(response.body().toString(), HashMap.class);
                if (map.get("success").equals("good")) {

                } else {
                    delete(num);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                StyleableToast.makeText(getApplicationContext(), "Нет доступа к интернету", Toast.LENGTH_SHORT, R.style.wrong).show();
            }
        });
    }

    public void delete(int num) {
        if (num == 1) {
            code1 = "0";
            ContentValues newValues = new ContentValues();
            newValues.put("code1", 0);
            db.update("Data", newValues, "_id = 1", null);
        } else {
            code2 = "0";
            ContentValues newValues = new ContentValues();
            newValues.put("code2", 0);
            db.update("Data", newValues, "_id = 1", null);
        }
    }

    public void reload(View view) {
        checkexistingall();
    }

    public void toBack(View view) {
        Intent intent = new Intent(StorageActivity.this, PromoActivity.class);
        startActivity(intent);
        finish();
    }

    public void show1(View view) {
        if (code1.equals("0")) {
            Intent intent = new Intent(StorageActivity.this, QRActivity.class);
            intent.putExtra("code", 1);
            startActivity(intent);
            finish();
        } else {
            //change view to showing qr
        }
    }

    public void show2(View view) {
        if (code1.equals("0")) {
            Intent intent = new Intent(StorageActivity.this, QRActivity.class);
            intent.putExtra("code", 2);
            startActivity(intent);
            finish();
        } else {
            //change view to showing qr
        }
    }

    public void delete1(View view) {
        if (!code1.equals("0")) {
            setContentView(R.layout.check_main);
            LinearLayout lin = findViewById(R.id.set);
            lin.setBackground(getDrawable(R.drawable.checkpage2));
            now = 1;
        }
    }

    public void delete2(View view) {
        if (!code2.equals("0")) {
            setContentView(R.layout.check_main);
            LinearLayout lin = findViewById(R.id.set);
            lin.setBackground(getDrawable(R.drawable.checkpage2));
            now = 2;
        }
    }

    public void Yes(View view) {
        setContentView(R.layout.storage_main);
        checkexistingall();
        if (now == 1) {
            if (!code1.equals("0")) {
                obDelete(code1);
            }
        } else {
            if (!code2.equals("0")) {
                obDelete(code2);
            }
        }
    }

    private void obDelete(final String code) {
        HashMap<String, String> postDataParams = new HashMap<String, String>();
        postDataParams.put("code", code);
        Call<Object> call = del.performPostCall(postDataParams);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                delete(now);
                decodeMoney(code);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                StyleableToast.makeText(getApplicationContext(), "Нет доступа к интернету", Toast.LENGTH_SHORT, R.style.wrong).show();
            }
        });
    }

    private void decodeMoney(String a) {
        long b = Integer.parseInt(a.substring(2, 5));
        switch (a.substring(5, 6)) {
            case "K":
                b *= 1000;
                break;
            case "M":
                b *= 1000000;
                break;
            case "B":
                b *= 1000000000;
                break;
        }
        b /= 2;
        TSHc += b;
        StyleableToast.makeText(this, "Вам вернули  " + getPrice(b) + " TSH", Toast.LENGTH_SHORT, R.style.get).show();
    }

    private String getPrice(long s) {
        String newa = "" + s;
        if (newa.length() > 12)
            newa = newa.substring(0, newa.length() - 9) + "T";
        else if (newa.length() > 9)
            newa = newa.substring(0, newa.length() - 9) + "B";
        else if (newa.length() > 6)
            newa = newa.substring(0, newa.length() - 6) + "M";
        else if (newa.length() > 3)
            newa = newa.substring(0, newa.length() - 3) + "K";

        return newa;
    }

    public void No(View view) {
        setContentView(R.layout.storage_main);
    }
}