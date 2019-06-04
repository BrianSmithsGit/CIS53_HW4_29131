package edu.deanza.cis53_hw4_29131;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        String day = getIntent().getStringExtra("day");
        String weather = getIntent().getStringExtra("weather");
        TextView dayTV = findViewById(R.id.detail_day);
        TextView weatherTV = findViewById(R.id.detail_weather);
        dayTV.setText(day);
        weatherTV.setText(weather);
        return super.onCreateView(name, context, attrs);
    }
}
