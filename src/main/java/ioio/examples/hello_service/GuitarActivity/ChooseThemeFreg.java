package ioio.examples.hello_service.GuitarActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ioio.examples.hello_service.R;

/**
 * Created by omerrom on 18/09/16.
 */
public class ChooseThemeFreg extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_theme,
                container, false);
    }
}