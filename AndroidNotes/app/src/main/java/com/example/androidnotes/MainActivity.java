package com.example.androidnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    RecyclerView recyclerView;
    ActivityAdapter activityAdapter;
    List<Notes> notesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMain);
        activityAdapter = new ActivityAdapter(notesList, this);
        recyclerView.setAdapter(activityAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }


    public ArrayList<Notes> loadFile() {
        ArrayList<Notes> list = new ArrayList<>();
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            JSONArray jsonArray = new JSONArray(sb.toString());
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                String detail = jsonObject.getString("detail");
                String date = jsonObject.getString("date");
                list.add(new Notes(title, detail, date));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onStart() {
        notesList.clear();
        notesList.addAll(loadFile());
        setTitle("Android Notes (" + notesList.size() + ")");
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addMenu:
                Intent intent = new Intent(this, EditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("List",(Serializable) notesList);
                intent.putExtra("List",bundle);
                startActivity(intent);
                return true;
            case R.id.infoMenu:
                Intent info = new Intent(this,AboutActivity.class);
                startActivity(info);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNotes() {
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
            PrintWriter pw = new PrintWriter(fos);
            pw.print(notesList);
            pw.close();
            fos.close();
        } catch (Exception e){
            e.getStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        int position = recyclerView.getChildAdapterPosition(view);
        Notes notes = notesList.get(position);
        Intent intent = new Intent(this, EditActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtra("title", notes.getTitle());
        intent.putExtra("detail", notes.getDetail());
        bundle.putSerializable("List", (Serializable) notesList);
        intent.putExtra("List",bundle);
        intent.putExtra("position",position);
        startActivity(intent);

    }

    @Override
    public boolean onLongClick(View view) {
        int position = recyclerView.getChildAdapterPosition(view);
        new AlertDialog.Builder(this,R.style.MyDialogTheme)
                .setTitle("Delete Note '" + notesList.get(position).getTitle() + "'?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        notesList.remove(position);
                        saveNotes();
                        setTitle("Android Notes ("+notesList.size()+")");
                        activityAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No",null)
                .show();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        finishAffinity();
    }
}