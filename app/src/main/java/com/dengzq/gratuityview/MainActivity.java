package com.dengzq.gratuityview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dengzq.gratuityview.GratuityView.GratuityView;

public class MainActivity extends AppCompatActivity {

    private GratuityView gview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gview= (GratuityView) findViewById(R.id.gratuity_view);

        gview.setBaseText("Android","dengzq");
        gview.setChildText("x1",1);
        gview.setChildText("x10",2);
        gview.setChildText("Android",0);
        gview.setChildText("x100",3);
        gview.setChildText("x1000",4);
        gview.setAnimDuration(800);
        gview.setAutoCollapse(true);
        gview.setChildCount(5);

        gview.setChildgroundColor(0xFFEE6E50,0);
        gview.setChildgroundColor(0xFFEEB422,1);
        gview.setChildgroundColor(0xFFEEEE00,2);
        gview.setChildgroundColor(0xFF7CCD7C,3);
        gview.setChildgroundColor(0xFF1C86EE,4);

        gview.setOnItemClickListener(new GratuityView.OnItemClickListener() {
            @Override
            public void onItemClick(int index) {
                if(index==0){
                    gview.setChildText("哎哟",0);
                    gview.setChildTextColor(Color.parseColor("#FF69B4"),0);
                    gview.setChildTextSize(18,0);

                }
                Toast.makeText(MainActivity.this,"我是第"+index+"个",Toast.LENGTH_SHORT).show();

            }
        });
    }
}
