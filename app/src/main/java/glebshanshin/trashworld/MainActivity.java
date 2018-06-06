package glebshanshin.trashworld;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    MediaPlayer menuPlayer;
    boolean notIntent = true;
    float music, effects;
    SQLiteDatabase db;
    DBHelper dbHelper;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        cursor = db.query("Data", null, null, null, null, null, null);
        cursor.moveToFirst();
        music = cursor.getFloat(22);
        effects = cursor.getFloat(23);
        cursor.close();
    }

    public void toStart(View view) {//переход в класс основного геймплея
        if (notIntent) {
            notIntent = false;
            Intent intent = new Intent(MainActivity.this, PlayActivity.class);
            startActivity(intent);
            finish1();
        }
    }

    private void finish1() { //отключение музыки при выходе из активности
        menuPlayer.stop();
        menuPlayer = MediaPlayer.create(this, R.raw.click);
        menuPlayer.setVolume(effects, effects);
        menuPlayer.setLooping(false);
        menuPlayer.start();
        finish();
    }

    public void toExit(View view) {//выход из приложения
        if (notIntent) {
            notIntent = false;
            finish1();
        }
    }

    public void toAchievements(View view) {//переход в класс Достижений
        if (notIntent) {
            notIntent = false;
            Intent intent = new Intent(MainActivity.this, AchievementsActivity.class);
            startActivity(intent);
            finish1();
        }
    }

    public void toSettings(View view) {//переход в класс Настроек
        if (notIntent) {
            notIntent = false;
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish1();
        }
    }
    //включение и отключение музыки при выключении и выключении приложения
    @Override
    protected void onStop() {
        super.onStop();
        menuPlayer.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        menuPlayer = MediaPlayer.create(this, R.raw.menu);
        menuPlayer.setVolume(music, music);
        menuPlayer.setLooping(true);
        menuPlayer.start();
    }
}