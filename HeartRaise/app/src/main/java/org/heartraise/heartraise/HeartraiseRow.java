package org.heartraise.heartraise;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class HeartraiseRow extends AppCompatActivity {


    TextView mtext;
   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.heartraise_row);

        mtext = (TextView) findViewById(R.id.post_story);
        Typeface myCustom_font = Typeface.createFromAsset(getAssets(),  "fonts/Balthazar Regular.ttf");
        mtext.setTypeface(myCustom_font);

        mtext = (TextView) findViewById(R.id.post_title);
        Typeface myCustom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/Xoxoxa.ttf");
        mtext.setTypeface(myCustom_font2);


    }
}
