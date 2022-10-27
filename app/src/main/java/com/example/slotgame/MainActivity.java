package com.example.slotgame;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView _slot1View,_slot2View,_slot3View, _slot4View;
    Button _btStart;
    boolean isPlay=false;

    SlotTask _slottask1,_slottask2,_slottask3, _slottask4;
    ExecutorService _execService1, _execService2, _execService3, _execService4, _execServicePool;

    private static int[] _imgs = {R.drawable.slot1, R.drawable.slot2, R.drawable.slot3, R.drawable.slot4,
            R.drawable.slot5, R.drawable.slotbar};
    ArrayList<String> arrayUrl = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _slot1View = findViewById(R.id.id_Slot1);
        _slot2View = findViewById(R.id.id_Slot2);
        _slot3View = findViewById(R.id.id_Slot3);
        _slot4View = findViewById(R.id.id_slot4);

        _slot1View.setImageResource(R.drawable.slotbar);
        _slot2View.setImageResource(R.drawable.slotbar);
        _slot3View.setImageResource(R.drawable.slotbar);
        _slot4View.setImageResource(R.drawable.slotbar);

        _btStart = findViewById(R.id.id_BtPlay);
        _btStart.setOnClickListener(this);

        _execService1 = Executors.newSingleThreadExecutor();
        _execService2 = Executors.newSingleThreadExecutor();
        _execService3 = Executors.newSingleThreadExecutor();
        _execService4 = Executors.newSingleThreadExecutor();
        _execServicePool = Executors.newFixedThreadPool(4);

        _slottask1 = new SlotTask(_slot1View);
        _slottask2 = new SlotTask(_slot2View);
        _slottask3 = new SlotTask(_slot3View);
        _slottask4 = new SlotTask(_slot4View);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {


        if(v.getId()==_btStart.getId())
        {
            if(!isPlay){
                _slottask1._play = true;
                _slottask2._play = true;
                _slottask3._play = true;
                _slottask4._play = true;

                _execServicePool.execute(_slottask1);
                _execServicePool.execute(_slottask2);
                _execServicePool.execute(_slottask3);
                _execServicePool.execute(_slottask4);

                _btStart.setText("Stop");
            }
            else {
                ExecutorService execGetImage = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                _btStart.setOnClickListener(view -> execGetImage.execute(() -> {
                    try {
                        final String txt =
                                loadStringFromNetwork();
                        try {
                            JSONArray jsonArray = new
                                    JSONArray(txt);
                            for (int i = 0; i <
                                    jsonArray.length(); i++) {
                                JSONObject jsonObject =
                                        jsonArray.getJSONObject(i);

                                arrayUrl.add(jsonObject.getString("url"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.post(() -> {

                            Glide.with(MainActivity.this)

                                    .load(arrayUrl.get(0))
                                    .into(_slot1View);

                            Glide.with(MainActivity.this)

                                    .load(arrayUrl.get(1))
                                    .into(_slot2View);

                            Glide.with(MainActivity.this)

                                    .load(arrayUrl.get(2))
                                    .into(_slot3View);
                            Glide.with(MainActivity.this)

                                    .load(arrayUrl.get(0))
                                    .into(_slot4View);
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));

                _slottask1._play = false;
                _slottask2._play = false;
                _slottask3._play = false;
                _slottask4._play = false;
                _btStart.setText("Play");
            }
            isPlay=!isPlay;

        }

    }

    class SlotTask implements Runnable{
        ImageView _slotImg;
        Random _random = new Random();
        public boolean _play;
        int i;

        public SlotTask(ImageView _slotImg){
           this._slotImg = _slotImg;
           i = 0;
           _play=true;
        }

        @Override
        public void run(){
            while (_play){
                i = _random.nextInt(6);

                runOnUiThread(() -> _slotImg.setImageResource(_imgs[i]));

                try {
                    sleep(_random.nextInt(500));
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                }
            }
        }

    private String loadStringFromNetwork() throws IOException {
        final URL myUrl = new URL("https://mocki.io/v1/821f1b13-fa9a-43aa-ba9a-9e328df8270e");
        final InputStream in = myUrl.openStream();
        final StringBuilder out = new StringBuilder();
        final byte[] buffer = new byte[1024];
        try {
            for (int ctr; (ctr = in.read(buffer)) != -1; ) {
                out.append(new String(buffer, 0, ctr));
            }
        } catch (IOException e) {
            throw new RuntimeException("Gagal mendapatkan text", e);
        }
        return out.toString();
    }
    }