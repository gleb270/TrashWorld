package glebshanshin.trashworld;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.muddzdev.styleabletoastlibrary.StyleableToast;

public class SettingsActivity extends Activity {
    MediaPlayer menuPlayer;
    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.settings_main);
        menuPlayer = MediaPlayer.create(this, R.raw.menu);
        menuPlayer.start();
    }

    public void statistics(View view) {
        Intent intent = new Intent(SettingsActivity.this, StatisticsActivity.class);
        startActivity(intent);
        finish1();
    }

    private void finish1() {
        menuPlayer = MediaPlayer.create(this, R.raw.click);
        menuPlayer.setVolume(0.4f, 0.4f);
        menuPlayer.start();
        finish();
    }

    public void reset(View view) {
        setContentView(R.layout.check_main);
    }

    private void update(SQLiteDatabase db) {
        ContentValues newValues = new ContentValues();
        newValues.put("TSH", 0);
        newValues.put("man", 0);
        newValues.put("car", 0);
        newValues.put("robot", 0);
        newValues.put("factory", 0);

        newValues.put("paper", 0);
        newValues.put("plastic", 0);
        newValues.put("metal", 0);
        newValues.put("organic", 0);
        newValues.put("notrecycle", 0);
        newValues.put("glass", 0);
        newValues.put("mistakes", 0);

        newValues.put("paperb", 1);
        newValues.put("plasticb", 1);
        newValues.put("metalb", 1);
        newValues.put("organicb", 1);
        newValues.put("notrecycleb", 1);
        newValues.put("glassb", 1);

        newValues.put("multi", 1);

        newValues.put("qr1", 0);
        newValues.put("qr2", 0);

        db.update("Data", newValues, "_id = 1", null);
        StyleableToast.makeText(this, "✓  Все данные стерты", Toast.LENGTH_SHORT, R.style.Clear).show();
    }

    public void toMenu(View view) {
        Intent intent1 = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent1);
        finish1();
    }

    public void promo(View view) {
        Intent intent1 = new Intent(SettingsActivity.this, PromoActivity.class);
        startActivity(intent1);
        finish1();
    }

    public void Yes(View view) {
        setContentView(R.layout.settings_main);
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        update(db);
    }

    public void No(View view) {
        setContentView(R.layout.settings_main);
    }
}