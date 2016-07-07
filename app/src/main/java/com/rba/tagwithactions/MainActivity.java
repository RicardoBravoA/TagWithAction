package com.rba.tagwithactions;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.rba.tagwithactions.control.tag.TagGroup;
import com.rba.tagwithactions.control.tag.listener.OnTagClickListener;
import com.rba.tagwithactions.control.tag.listener.OnTagDeleteListener;
import com.rba.tagwithactions.model.TagEntity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnTagDeleteListener, OnTagClickListener {

    private TagGroup tagGroup;
    private ArrayList<TagEntity> tagList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tagGroup = (TagGroup) findViewById(R.id.tag_group);

        tagList = new ArrayList<>();

        for(int i = 0; i< 20; i++){
            tagList.add(new TagEntity(""+i, "Tag "+i));
        }

        tagGroup.addTags(tagList);

        tagGroup.setOnTagClickListener(this);
        tagGroup.setOnTagDeleteListener(this);


    }

    @Override
    public void onTagClick(TagGroup.Tag tag, int position) {
        Log.i("x- click", tagList.get(position).getDescription());
    }

    @Override
    public void onTagDeleted(final TagGroup view, final TagGroup.Tag tag, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("\"" + tag.text + "\" will be delete. Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                view.remove(position);
                Toast.makeText(MainActivity.this, "\"" + tag.text + "\" deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
}
