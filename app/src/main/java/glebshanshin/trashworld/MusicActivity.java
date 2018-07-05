package glebshanshin.trashworld;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class MusicActivity extends UniActivity {
    DiscreteSeekBar musics, effectes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_main);
        init();
    }

    private void init() {
        final TextView musicText = findViewById(R.id.music), effectsText = findViewById(R.id.effects);
        musicText.setTextSize(scale * 50f);
        effectsText.setTextSize(scale * 50f);
        musics = findViewById(R.id.musicbar);
        musics.setProgress((int) (music * 100));
        musicText.setText("Музыка " + musics.getProgress() + "%");
        musics.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {//измененение музыки
                musicText.setText("Музыка " + value + "%");
                menuPlayer.setVolume((float) value / 100, (float) value / 100);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                clickPlayer.start();
            }
        });
        effectes = findViewById(R.id.effectsbar);
        effectes.setProgress((int) (effects * 100));
        effectsText.setText("Звуки " + effectes.getProgress() + "%");
        effectes.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {//изменение музыки
                effectsText.setText("Звуки " + value + "%");
                clickPlayer.setVolume((float) value / 100, (float) value / 100);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                clickPlayer.start();
            }
        });
    }
    public void save(){
        if (notIntent) {
            music = (float) musics.getProgress() / 100;
            effects = (float) effectes.getProgress() / 100;
            ContentValues newValues = new ContentValues();
            newValues.put("music", music);
            newValues.put("effects", effects);
            db.update("Data", newValues, "_id = 1", null);
            transfer(SettingsActivity.class);
        }
    }
    public void toSettings(View view) {//переход в класс настроек
        save();
    }

    //выход в настройки через встроенную кнопку назад
    @Override
    public void onBackPressed() {
        save();
        super.onBackPressed();
    }
}