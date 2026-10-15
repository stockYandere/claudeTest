package com.example.claudetest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameView game = new GameView(this);

        Button jumpButton = new Button(this);
        jumpButton.setText("점프!");
        jumpButton.setTextSize(24);
        jumpButton.setOnClickListener(v -> game.jump());

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.addView(game, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        root.addView(jumpButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (96 * getResources().getDisplayMetrics().density)));
        setContentView(root);
    }
}
