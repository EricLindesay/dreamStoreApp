package com.android.androidTesting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.androidTesting.adapters.NoteListAdapter;
import com.android.androidTesting.adapters.TagListAdapter;
import com.android.androidTesting.db.AppDatabase;
import com.android.androidTesting.db.LinkTable;
import com.android.androidTesting.db.Note;
import com.android.androidTesting.db.Tag;

import java.util.ArrayList;
import java.util.List;

public class AddTagsActivity extends AppCompatActivity {
    private TagListAdapter tagListAdapter;
    private ArrayList<Tag> tags;
    RecyclerView recyclerView;
    TagList allTagList = new TagList();
    EditText searchBar;
    TextView addTagTV;
    int noteid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_tags_menu);

        Bundle extras = getIntent().getExtras();
        noteid = extras.getInt("noteid");

        searchBar = findViewById(R.id.searchTag);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                return;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                loadTagList(searchBar.getText().toString());
                changeAddTagVisibility();
            }
        });

        loadTagList();
        initRecyclerView();

        addTagTV = findViewById(R.id.addTag);  // starts as "GONE"
        changeAddTagVisibility();
    }

    void changeAddTagVisibility() {
        if (tags.size() <= 0) {
            addTagTV.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            addTagTV.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.tagList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        tagListAdapter = new TagListAdapter(tags, this, allTagList);
        recyclerView.setAdapter(tagListAdapter);
    }

    private void loadTagList() {
        Log.d("Recycle", "this opens exactly once");
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        List<Tag> tagList = db.tagDao().getAllTags();
        tags = new ArrayList<Tag>(tagList);
        if (noteid == -1) {
            allTagList.initialiseTagList(tags);
        }
        if (noteid != -1) {
            List<String> stringList = db.linkTableDao().getAllTagsForNote(noteid);
            allTagList.initialiseTagList(tags, new ArrayList<String>(stringList));
        }
    }

    private void loadTagList(String tagname) {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        List<Tag> tagList;
        if (tagname.isEmpty()) tagList = db.tagDao().getAllTags();
        else tagList = db.tagDao().getTagsByName("%"+tagname+"%");
        tags = new ArrayList<Tag>(tagList);
        tagListAdapter = new TagListAdapter(tags, this, allTagList);
        recyclerView.setAdapter(tagListAdapter);
    }

    private void createTag(String tagName) {
        Tag tag = new Tag();
        tag.tid = tagName;
        AppDatabase db  = AppDatabase.getDbInstance(this.getApplicationContext());

        db.tagDao().insertTag(tag);
    }

    @Override
    public void onBackPressed() {
        // if there is stuff in the search box, clear it.
        // otherwise, finish.
        String contents = searchBar.getText().toString();
        if (contents.isEmpty()) {
            finish();
        } else {
            searchBar.setText("");
        }
    }
}