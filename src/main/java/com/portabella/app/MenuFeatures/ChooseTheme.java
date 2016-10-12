package com.portabella.app.MenuFeatures;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.portabella.app.GuitarActivity.CordManager;
import com.portabella.app.GuitarActivity.GuitarActivity;
import com.portabella.app.R;

/**
 * This activity is used for the change theme menu in the app's menu.
 * Created by omerrom on 18/09/16.
 */
public class ChooseTheme extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        Button rollTheme = (Button)findViewById(R.id.rollBut);
        Button bluesTheme = (Button)findViewById(R.id.bluesBut);
        Button calmTheme = (Button)findViewById(R.id.calmBut);

        rollTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTheme(R.drawable.guitarscreenroll, GuitarActivity.ROCK_NOTES, getResources());
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        bluesTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTheme(R.drawable.guitarscreenblues, GuitarActivity.BLUES_NOTES, getResources());
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        calmTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTheme(R.drawable.guitarscreenbase, GuitarActivity.REG_NOTES, getResources());
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

    }

    public static void setTheme(int drawable, int[] notes, Resources res) {
        GuitarActivity.baseGuitarLayout.setBackground(res.getDrawable(drawable));
        CordManager.setNewCords(notes);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}
