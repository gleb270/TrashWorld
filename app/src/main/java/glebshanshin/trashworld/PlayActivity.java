package glebshanshin.trashworld;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends Activity implements OnTouchListener {
    long mills = 500L;
    Vibrator vibrator;
    float music, effects;
    MediaPlayer clickPlayer, playPlayer;//два плеера один для щелчков другой для фоновой музыки
    long factory, robot, car, man;
    long TSH, Adder;
    int organicb, plasticb, metalb, glassb, notrecycleb, paperb;
    int organicc, plasticc, metalc, glassc, notrecyclec, paperc, mistakes, multi;
    DBHelper dbHelper;
    Cursor cursor;
    SQLiteDatabase db;
    TextView TSHv, TSHsv;
    int offset_x = 0;
    int offset_y = 0;
    boolean touchFlag = false;//переменная обозначающая наличие касания
    boolean dropFlag = false;//переменная обозначающая наличие касания в каком то из секторов
    boolean notIntent = true;
    LayoutParams imageParams;
    ImageView circle, trash;
    TextView plastic, glass, metal, organic, notrecycle, paper;
    String choice = "null", tag, tsh, postfix = " TSH";
    int eX, eY;
    int w, h;
    View root;

    public void increaseTSH(long Adder) {//увеличение очков
        //учет бонусов для каждого вида
        if (choice.equals("org")) {
            Adder *= organicb;
        } else if (choice.equals("ele")) {
            Adder *= notrecycleb;
        } else if (choice.equals("pap")) {
            Adder *= paperb;
        } else if (choice.equals("pla")) {
            Adder *= plasticb;
        } else if (choice.equals("gla")) {
            Adder *= glassb;
        } else if (choice.equals("met")) {
            Adder *= metalb;
        }
        TSH += Adder;
        String newa = getPrice(TSH);
        tsh = newa + postfix;
        TSHv.setText(tsh);

    }

    private String getPrice(long s) {//масштабирование цены
        String newa = "" + s;
        if (newa.length() > 12)
            newa = newa.substring(0, newa.length() - 12) + "T";
        else if (newa.length() > 9)
            newa = newa.substring(0, newa.length() - 9) + "B";
        else if (newa.length() > 6)
            newa = newa.substring(0, newa.length() - 6) + "M";
        else if (newa.length() > 3)
            newa = newa.substring(0, newa.length() - 3) + "K";

        return newa;
    }

    public void toMenu(View view) {//выход в главное меню
        if (notIntent) {
            notIntent = false;
            Intent intent1 = new Intent(PlayActivity.this, MainActivity.class);
            startActivity(intent1);
            update(db);
            finish1();
        }
    }

    private void finish1() {//отключение музыки при выходе из активности
        playPlayer.stop();
        clickPlayer.start();
        finish();
    }

    private void update(SQLiteDatabase db) {//обновление базы данных при переходе в другую активность
        ContentValues newValues = new ContentValues();
        newValues.put("TSH", TSH);
        newValues.put("man", man);
        newValues.put("car", car);
        newValues.put("robot", robot);
        newValues.put("factory", factory);

        newValues.put("paper", paperc);
        newValues.put("plastic", plasticc);
        newValues.put("metal", metalc);
        newValues.put("organic", organicc);
        newValues.put("notrecycle", notrecyclec);
        newValues.put("glass", glassc);
        newValues.put("mistakes", mistakes);
        db.update("Data", newValues, "_id = 1", null);
    }

    private void incCounter(String choice) {//изменение числа правильно отсортированного мусора
        switch (choice) {
            case "pap":
                paperc++;
                break;
            case "pla":
                plasticc++;
                break;
            case "met":
                metalc++;
                break;
            case "ele":
                notrecyclec++;
                break;
            case "org":
                organicc++;
                break;
            case "gla":
                glassc++;
                break;
        }
    }

    public void toStore(View view) {//переход в класс магазина
        if (notIntent) {
            notIntent = false;
            update(db);
            Intent intent = new Intent(PlayActivity.this, StoreActivity.class);
            startActivity(intent);
            finish1();
        }
    }

    public void game() {//метод для проверки правильности выбора сектора
        if (tag.equals(choice)) {
            increaseTSH(Adder);
            incCounter(choice);
        } else {
            vibrator.vibrate(mills);//вибрация при неправильном выборе
            mistakes++;
        }
        choice = "null";
        newTrash();
    }

    private String getTag(String id) {//определение вида мусора по номеру
        int id1 = Integer.parseInt(id);
        if ((id1 >= 1) & (id1 <= 3)) {
            return "gla";
        } else if ((id1 >= 4) & (id1 <= 9)) {
            return "met";
        } else if ((id1 >= 10) & (id1 <= 19)) {
            return "pap";
        } else if ((id1 >= 20) & (id1 <= 25)) {
            return "pla";
        } else if ((id1 >= 26) & (id1 <= 29)) {
            return "org";
        } else if ((id1 >= 30) & (id1 <= 33)) {
            return "ele";
        } else {
            return "none";
        }
    }

    private boolean check(TextView i) {//проверка на выбор сектора
        int topY = i.getTop();
        int leftX = i.getLeft();
        int rightX = i.getRight();
        int bottomY = i.getBottom();
        if (eX > leftX && eX < rightX && eY > topY && eY < bottomY) {
            if (!choice.equals("" + i.getTag())) {//проверка на повтор, чтобы не устанавливать одинаковую картинку по несколько раз
                clickPlayer.start();
                circle.setImageDrawable(getDrawable(getResources().getIdentifier(i.getTag() + "", "drawable", getPackageName())));
                dropFlag = true;
                choice = "" + i.getTag();
            }
            return true;
        }
        return false;
    }

    public void newTrash() {//генерация нового мусора
        String id = "" + ((int) (Math.random() * 32) + 1);
        tag = getTag(id);
        trash.setImageDrawable(getDrawable(getResources().getIdentifier("trash" + id, "drawable", getPackageName())));
    }

    public void init(SQLiteDatabase db) {//получение данных из базы данных
        cursor = db.query("Data", null, null, null, null, null, null);
        cursor.moveToFirst();
        TSH = cursor.getLong(1);
        man = cursor.getLong(2);
        car = cursor.getLong(3);
        robot = cursor.getLong(4);
        factory = cursor.getLong(5);
        paperc = cursor.getInt(6);
        plasticc = cursor.getInt(7);
        metalc = cursor.getInt(8);
        organicc = cursor.getInt(9);
        notrecyclec = cursor.getInt(10);
        glassc = cursor.getInt(11);
        mistakes = cursor.getInt(12);
        paperb = cursor.getInt(13);
        plasticb = cursor.getInt(14);
        metalb = cursor.getInt(15);
        organicb = cursor.getInt(16);
        notrecycleb = cursor.getInt(17);
        glassb = cursor.getInt(18);
        multi = cursor.getInt(19);
        music = cursor.getFloat(22);
        effects = cursor.getFloat(23);
        cursor.close();
        initTSHs();
    }

    private void initTSHs() {//инициализация "увеличителя", т.е. количество TSH/мусор
        Adder = 1 + man + car * 10 + robot * 50 + factory * 100;
        Adder *= multi;
        String newa = getPrice(Adder);
        TSHsv.setText(newa + " TSH/мусор");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.play_main);
        init1();
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        //масштабирование шрифтов
        float scale = 1 / getResources().getDisplayMetrics().density * 0.5f + getWindowManager().getDefaultDisplay().getHeight() * getWindowManager().getDefaultDisplay().getWidth() * 0.0000001f;
        TSHv.setTextSize(scale * 65f);
        TSHsv.setTextSize(scale * 65f);
        init(db);
        increaseTSH(0);
        newTrash();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        clickPlayer = MediaPlayer.create(this, R.raw.click);
        clickPlayer.setVolume(effects, effects);
        //получение размера экрана
        w = getWindowManager().getDefaultDisplay().getWidth() - 50;
        h = getWindowManager().getDefaultDisplay().getHeight() - 10;
        trash.setOnTouchListener(this);
        root.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (touchFlag) { //если есть касание
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_MOVE:
                            eX = (int) event.getX();
                            eY = (int) event.getY();
                            int x = (int) event.getX() - offset_x;
                            int y = (int) event.getY() - offset_y;
                            if (x > w) x = w;
                            if (y > h) y = h;
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(new ViewGroup.MarginLayoutParams(trash.getWidth(), trash.getHeight()));
                            lp.setMargins(x, y, 0, 0);//установка отступов у мусора согласно касанию
                            if (!((check(plastic)) | check(metal) | check(glass) | check(organic) | check(notrecycle) | check(paper)) && !choice.equals("")) {
                                choice = "";
                                circle.setImageDrawable(getDrawable(R.color.alpha1));
                                dropFlag = false;
                            }//проверка всех TextView
                            trash.setLayoutParams(lp);
                            break;
                        case MotionEvent.ACTION_UP:
                            touchFlag = false;
                            if (dropFlag) {
                                dropFlag = false;
                                circle.setImageDrawable(getDrawable(R.color.alpha1));
                                game();
                                trash.setLayoutParams(imageParams);
                            } else {
                                trash.setLayoutParams(imageParams);
                            }
                            break;
                        default:
                            break;
                    }
                }
                return true;
            }
        });
    }

    private void init1() {//инициализация ImageView и TextView
        circle = findViewById(R.id.circle);
        root = findViewById(android.R.id.content).getRootView();
        plastic = findViewById(R.id.plastic);
        glass = findViewById(R.id.glass);
        metal = findViewById(R.id.metal);
        organic = findViewById(R.id.organic);
        notrecycle = findViewById(R.id.notrecycle);
        paper = findViewById(R.id.paper);
        trash = findViewById(R.id.img);
        TSHv = findViewById(R.id.TSH);
        TSHsv = findViewById(R.id.TSHs);
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touchFlag = true;
                offset_x = (int) event.getX();
                offset_y = (int) event.getY();
                imageParams = v.getLayoutParams();
                break;
            case MotionEvent.ACTION_UP:
                touchFlag = false;
                break;
            default:
                break;
        }
        return false;
    }

    //включение и отключение музыки при выключении и выключении приложения
    @Override
    protected void onStop() {
        super.onStop();
        playPlayer.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        playPlayer = MediaPlayer.create(this, R.raw.play);
        playPlayer.setVolume(music, music);
        playPlayer.setLooping(true);
        playPlayer.start();
    }

    //выход в главное меню через встроенную кнопку назад
    @Override
    public void onBackPressed() {
        if (notIntent) {
            notIntent = false;
            Intent intent1 = new Intent(PlayActivity.this, MainActivity.class);
            startActivity(intent1);
            update(db);
            finish1();
        }
        super.onBackPressed();
    }
}