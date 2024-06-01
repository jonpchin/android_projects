package com.goplaychess.gobible;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jonc on 5/22/2016.
 */
public class ReadBook extends AppCompatActivity implements View.OnTouchListener {

    //keeps track of what chapter is being read
    int chapter = 1;
    int totalChapters;
    TextView textView;
    String bookTitle = ""; // or other values
    String bibleVersion = "ASV";
    Boolean isDarkTheme = false;
    private GestureDetectorCompat gestureDetector;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_book);

        Activity activity = (Activity)ReadBook.this;
        textView = (TextView)activity.findViewById(R.id.book_text_read);
        Bundle b = getIntent().getExtras();

        if(b != null){
            bookTitle = b.getString("key");
            totalChapters = b.getInt("total");
            bibleVersion = b.getString("version");
            isDarkTheme = b.getBoolean("theme");
        }
        getSupportActionBar().setTitle(bookTitle + " " + 1);

        //load the initial chapter of the book
        loadNextChapter(1);

        OnSwipeListener onSwipeListener = new OnSwipeListener() {

            @Override
            public boolean onSwipe(Direction direction) {

                // Possible implementation
                if (direction == Direction.left && chapter < totalChapters) {
                    chapter++;
                    loadNextChapter(chapter);
                    return true;
                } else if (direction == Direction.right && chapter > 1) {
                    chapter--;
                    loadNextChapter(chapter);
                    return true;
                }

                return super.onSwipe(direction);
            }
        };

        if (isDarkTheme){
            setDarkTheme();
        }else {
            setLightTheme();
        }

        gestureDetector = new GestureDetectorCompat(getApplicationContext(), onSwipeListener);
        textView.setOnTouchListener(this);
    }

    public void setDarkTheme(){
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(Color.BLACK);
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.relativelayoutBook);
        rl.setBackgroundColor(Color.BLACK);
    }

    public void setLightTheme(){
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundColor(Color.parseColor("#FAFAFA"));
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.relativelayoutBook);
        rl.setBackgroundColor(Color.parseColor("#FAFAFA"));
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        super.dispatchTouchEvent(ev);
        return gestureDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.book_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       if (id == R.id.plus && TextSize.textSize < 40) {

           ++TextSize.textSize;
           textView.setTextSize((TextSize.textSize));
           return true;
       } else  if (id == R.id.minus && TextSize.textSize > 10) {

           --TextSize.textSize;
           textView.setTextSize((TextSize.textSize));
           return true;
       }else if (id == R.id.action_left && chapter > 1) {
            chapter--;
            loadNextChapter(chapter);
            return true;

        }else if(id == R.id.action_right && chapter < totalChapters){
            chapter++;
            loadNextChapter(chapter);
            return true;
        }else if(id == R.id.search_chapter) {
           AlertDialog.Builder builder = new AlertDialog.Builder(this);
           builder.setTitle("Search Chapter");

           // Set up the input
           final EditText input = new EditText(this);
           // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
           input.setInputType(InputType.TYPE_CLASS_NUMBER);
           builder.setView(input);

           // Set up the buttons
           builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   int specifiedChapter = Integer.parseInt(input.getText().toString());
                   if (specifiedChapter > 0 && specifiedChapter <= totalChapters) {
                       loadNextChapter(specifiedChapter);
                   }

               }
           });
           builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   dialog.cancel();
               }
           });

           builder.show();

        }
        return super.onOptionsItemSelected(item);
    }
    //loads the specified chapter of the bible when user presses next or previous button
    public void loadNextChapter(int target){
        //reading from file and displaying in the textview
        StringBuilder stringBuilder = new StringBuilder();
        String bookText = bibleVersion + "/" + bookTitle + "/" + bookTitle + target + ".txt";
        InputStream inputStream = null;
        try {
            inputStream = getApplicationContext().getAssets().open(bookText);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            bufferedReader.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        getSupportActionBar().setTitle(bookTitle + " " + target);
        textView.setText(stringBuilder.toString());
        textView.setTextSize(TextSize.textSize);
        chapter = target;
    }
}