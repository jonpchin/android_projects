package com.goplaychess.gobible;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity{

    ListView listView;
    boolean sorted = false;
    ArrayAdapter<String> itemsAdapter;

    ArrayAdapter<String> itemsAdapterSorted;
    String bibleVersion = "ASV";
    ArrayList<String> sortedItems;
    ArrayList<String> items;
    boolean isDarkTheme = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        mainTextView = (TextView) findViewById(R.id.main_textview);
//       mainTextView.setText("This is the new updated text sweet!");
//        Button mainButton;
//        mainButton = (Button) findViewById(R.id.main_button);
//        mainButton.setOnClickListener(this);

        items = new ArrayList<String>();
        sortedItems = new ArrayList<>();

        //dynamically adding books into arrayList
//        AssetManager aMan = getApplicationContext().getAssets();
//        String[] listOfFiles = null;
//        try {
//            listOfFiles = aMan.list("");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        for (int i = 0; i < listOfFiles.length; i++) {

//            sortedItems.add(listOfFiles[i]);
//       }

        //constructing non alphabetically order of books
        items.add("Genesis");
        items.add("Exodus");
        items.add("Leviticus");
        items.add("Numbers");
        items.add("Deuteronomy");

        items.add("Joshua");
        items.add("Judges");
        items.add("Ruth");
        items.add("1 Samuel");
        items.add("2 Samuel");

        items.add("1 Kings");
        items.add("2 Kings");
        items.add("1 Chronicles");
        items.add("2 Chronicles");
        items.add("Ezra");

        items.add("Nehemiah");
        items.add("Esther");
        items.add("Job");
        items.add("Psalms");
        items.add("Proverbs");

        items.add("Ecclesiastes");
        items.add("Song of Solomon");
        items.add("Isaiah");
        items.add("Jeremiah");
        items.add("Lamentations");

        items.add("Ezekiel");
        items.add("Daniel");
        items.add("Hosea");
        items.add("Joel");
        items.add("Amos");

        items.add("Obadiah");
        items.add("Jonah");
        items.add("Micah");
        items.add("Nahum");
        items.add("Habakkuk");

        items.add("Zephaniah");
        items.add("Haggai");
        items.add("Zechariah");
        items.add("Malachi");

        items.add("Matthew");

        items.add("Mark");
        items.add("Luke");
        items.add("John");
        items.add("Acts of the Apostles");
        items.add("Romans");

        items.add("1 Corinthians");
        items.add("2 Corinthians");
        items.add("Galatians");
        items.add("Ephesians");
        items.add("Philippians");

        items.add("Colossians");
        items.add("1 Thessalonians");
        items.add("2 Thessalonians");
        items.add("1 Timothy");
        items.add("2 Timothy");

        items.add("Titus");
        items.add("Philemon");
        items.add("Hebrews");
        items.add("James");
        items.add("1 Peter");

        items.add("2 Peter");
        items.add("1 John");
        items.add("2 John");
        items.add("3 John");
        items.add("Jude");
        items.add("Revelation");

        //constructing second array by looping through first arraylist and adding each item
        for(String item : items){
            sortedItems.add(item);
        }
        Collections.sort(sortedItems, String.CASE_INSENSITIVE_ORDER);

        itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView = (ListView) findViewById(R.id.sampleListView);

        itemsAdapterSorted =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sortedItems);
        listView = (ListView) findViewById(R.id.sampleListView);

        listView.setAdapter(itemsAdapter);
        listView.setClickable(true);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String bookTitle = (listView.getItemAtPosition(position).toString());
                Snackbar.make(view, bookTitle, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                AssetManager aMan = getApplicationContext().getAssets();
                String[] listOfFiles = null;
                try {
                    listOfFiles = aMan.list(bibleVersion + "/" + bookTitle);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                int total = listOfFiles.length;

                Intent intent = new Intent(getApplicationContext(), ReadBook.class);
                Bundle b = new Bundle();
                b.putString("key", bookTitle);
                b.putInt("total", total);
                b.putString("version", bibleVersion);
                b.putBoolean("theme", isDarkTheme);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Books are now reordered.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if(sorted == false){
                    listView.setAdapter(itemsAdapterSorted);
                    sorted = true;
                }else{
                    listView.setAdapter(itemsAdapter);
                    sorted = false;
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_ASV) {
            bibleVersion = "ASV";
            getSupportActionBar().setTitle("Go Bible ASV");
            return true;
        }
        else if (id == R.id.action_KJV) {
            bibleVersion = "KJV";
            getSupportActionBar().setTitle("Go Bible KJV");
            return true;
        }
        else if (id == R.id.action_Webster) {
            bibleVersion = "Webster";
            getSupportActionBar().setTitle("Go Bible Webster");
            return true;
        }
        else if (id == R.id.action_World_English) {
            bibleVersion = "WorldEnglish";
            getSupportActionBar().setTitle("Go Bible WE");
            return true;
        }else if (id == R.id.action_Dark_Theme){
            RelativeLayout rl = (RelativeLayout)findViewById(R.id.relativelayoutListView);
            rl.setBackgroundColor(Color.BLACK);
            isDarkTheme = true;

            itemsAdapterSorted =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sortedItems){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view =super.getView(position, convertView, parent);

                            TextView textView=(TextView) view.findViewById(android.R.id.text1);

                            /*YOUR CHOICE OF COLOR*/
                            textView.setTextColor(Color.WHITE);

                            return view;
                        }
                    };
            itemsAdapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view =super.getView(position, convertView, parent);

                            TextView textView=(TextView) view.findViewById(android.R.id.text1);

                            /*YOUR CHOICE OF COLOR*/
                            textView.setTextColor(Color.WHITE);

                            return view;
                        }
                    };

            listView.setAdapter(itemsAdapter);
        }else if (id == R.id.action_Light_Theme){
            RelativeLayout rl = (RelativeLayout)findViewById(R.id.relativelayoutListView);
            rl.setBackgroundColor(Color.parseColor("#FAFAFA"));
            isDarkTheme = false;

            itemsAdapterSorted =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sortedItems){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view =super.getView(position, convertView, parent);

                            TextView textView=(TextView) view.findViewById(android.R.id.text1);

                            /*YOUR CHOICE OF COLOR*/
                            textView.setTextColor(Color.BLACK);

                            return view;
                        }
                    };
            itemsAdapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view =super.getView(position, convertView, parent);

                            TextView textView=(TextView) view.findViewById(android.R.id.text1);

                            /*YOUR CHOICE OF COLOR*/
                            textView.setTextColor(Color.BLACK);

                            return view;
                        }
                    };

            listView.setAdapter(itemsAdapter);
        }
        return super.onOptionsItemSelected(item);
    }
}
