package com.redkb.graphviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TabHost;

import java.util.Random;

public class CustomViewActivity extends AppCompatActivity {

    GraphView2 mGraphView2;
    GraphView3 mGraphView3;
    GraphView4 mGraphView4;
    GraphView5 mGraphView5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_view);

        final EditText numberEditText = findViewById(R.id.numberPicker);
        mGraphView2 = findViewById(R.id.graphView2);
        Button generateButton = findViewById(R.id.generateButton);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGraphView2.setData(getRandomData(Integer.valueOf(numberEditText.getText().toString())));
            }
        });

        final EditText numberEditText3 = findViewById(R.id.numberPicker3);
        mGraphView3 = findViewById(R.id.graphView3);
        Button generateButton3 = findViewById(R.id.generateButton3);
        generateButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGraphView3.setData(getRandomData(Integer.valueOf(numberEditText3.getText().toString())));
            }
        });

        final EditText numberEditText4 = findViewById(R.id.numberPicker4);
        mGraphView4 = findViewById(R.id.graphView4);
        Button generateButton4 = findViewById(R.id.generateButton4);
        generateButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGraphView4.setData(getRandomData(Integer.valueOf(numberEditText4.getText().toString())));
            }
        });

        final EditText numberEditText5 = findViewById(R.id.numberPicker5);
        mGraphView5 = findViewById(R.id.graphView5);
        Button generateButton5 = findViewById(R.id.generateButton5);
        generateButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGraphView5.setData(getRandomData(Integer.valueOf(numberEditText5.getText().toString())));
            }
        });
        final Switch lineBarSwitch = findViewById(R.id.lineBarSwitch5);
        lineBarSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mGraphView5.setGraphType(GraphView5.GraphType.BAR);
                } else {
                    mGraphView5.setGraphType(GraphView5.GraphType.LINE);
                }
            }
        });

        TabHost host = findViewById(R.id.tabHost);
        host.setup();

        //Tab 5
        TabHost.TabSpec spec = host.newTabSpec("5");
        spec.setContent(R.id.tab5);
        spec.setIndicator("5");
        host.addTab(spec);

        //Tab 4
        spec = host.newTabSpec("4");
        spec.setContent(R.id.tab4);
        spec.setIndicator("4");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("3");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("2");
        host.addTab(spec);

        //Tab 1
        spec = host.newTabSpec("1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("1");
        host.addTab(spec);
    }

    private float[] getRandomData(int dataSize) {
        float[] randomData = new float[dataSize];
        Random random = new Random();
        randomData[0] = 5 + random.nextFloat();
        for (int i = 1; i < randomData.length; i++) {
            randomData[i] = randomData[i - 1] + random.nextFloat() - .47f;
        }
        return randomData;
    }

}
