package edu.skku.jangdongjoo.skkuproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    private ArrayList<String> contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView contentlist;
        contentlist = findViewById(R.id. List_view);
        contents = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout. simple_list_item_single_choice , contents);
        contentlist.setAdapter(adapter);

        Button addButton = (Button)findViewById(R.id.addBtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText= MainActivity.this.findViewById(R.id.content);
                contents.add(editText.getText().toString());
                if(editText.length() <= 0) {
                    contents.remove(editText.getText().toString());
                }
                editText.getText().clear();
                adapter.notifyDataSetChanged();
            }
        });

        Button removeButton = (Button)findViewById(R.id.remBtn) ;
        removeButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = contentlist.getCheckedItemPosition();
                if(pos>=0 && pos<contents.size()){
                    contents.remove(pos);
                    contentlist.clearChoices();
                    adapter.notifyDataSetChanged();
                }
            }
        }) ;

        Button recipientButton = (Button)findViewById(R.id.contentBtn);
        recipientButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
    }
}