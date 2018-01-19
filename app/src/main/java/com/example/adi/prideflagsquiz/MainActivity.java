package com.example.adi.prideflagsquiz;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    // Arrays of IDs
    int[] definitionsArray = {R.id.asexual_definition, R.id.bisexual_definition, R.id.poly_definition,
            R.id.genderqueer_definition, R.id.intersex_definition, R.id.rainbow_definition, R.id.trans_definition};
    int[] checkBoxesArray = {R.id.rainbow_ans1, R.id.rainbow_ans2, R.id.rainbow_ans3, R.id.rainbow_ans4,
            R.id.rainbow_ans5, R.id.rainbow_ans6};
    int[] radioGroupIdArray = {R.id.asexual_group, R.id.bisexual_group, R.id.poly_group, R.id.genderqueer_group,
            R.id.trans_group};
    int[] radioGroupAns = {R.id.asexual_ans, R.id.bisexual_ans, R.id.poly_ans, R.id.genderqueer_ans,
            R.id.trans_ans};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an answer options array for the intersex question
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, identityTypes);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.intersex_ans);
        textView.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ScrollView mScrollView = (ScrollView) findViewById(R.id.questionsView);
        outState.putIntArray("ARTICLE_SCROLL_POSITION",
                new int[]{mScrollView.getScrollX(), mScrollView.getScrollY()});
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final ScrollView mScrollView = (ScrollView) findViewById(R.id.questionsView);
        final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
        if (position != null)
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(position[0], position[1]);
                }
            });
    }

    // Options for the intersex identity type question
    private static final String[] identityTypes = new String[]{
            "Gender", "Sexual"};

    /**
     * showing the questions and flags linearLayout instead of the opening view
     */
    public void startQuiz(View view) {
        // Make the opening layout dissappear
        setVisibility(R.id.openingView, View.GONE);
        // make the questions layout appear
        setVisibility(R.id.questionsView, View.VISIBLE);
    }

    /**
     * Checking answers and calculating the user's score
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void checkAnswers(View view) {
        int score = 0;
        // Check radioBox questions
        for (int i = 0; i <= 4; i++) {
            score += checkRadioGroup(radioGroupIdArray[i], radioGroupAns[i]);
        }
        // Check the intersex autoCompleteText question and lock it from further changes
        EditText intersexAns = (EditText) findViewById(R.id.intersex_ans);
        String userAns = intersexAns.getText().toString().toLowerCase();
        if (userAns.contains("gender")) {
            score += 1;
            intersexAns.setTextColor(getResources().getColor(R.color.correctAns));
        } else {
            intersexAns.setTextColor(getResources().getColor(R.color.wrongAns));
        }
        intersexAns.setEnabled(false);
        // Check the rainbow flag question checkBoxes
        int checkedBoxes = 0;
        for (int i = 0; i <= 5; i++) {
            checkedBoxes += isChecked(checkBoxesArray[i]);
        }
        if (checkedBoxes == 6) {
            score += 1;
        }
        // Show definitions
        for (int i = 0; i <= 6; i++) {
            setVisibility(definitionsArray[i], View.VISIBLE);
        }
        //Lock the radioBoxes and checkBoxes
        enableChanges(false);
        // Give feedback
        //Get name and lock it's editText view from changing
        EditText nameView = (EditText) findViewById(R.id.name);
        String name = nameView.getText().toString();
        nameView.setEnabled(false);
        // Device message according to score
        String message = "";
        if (score == 7) {
            message = "Excellent job " + name + "!";
        } else if (score >= 5) {
            message = "Nice job " + name + ", but there's still room for improvement.";
        } else {
            message = "You better brush up on your LGBT flags " + name + ".";
        }
        Toast.makeText(this, message + "\nYou got " + score + "/7 answers right.", Toast.LENGTH_LONG).show();
        // scroll to the top of the scrollView
        ScrollView scrollView = (ScrollView) findViewById(R.id.questionsView);
        scrollView.scrollTo(0, 0);
    }

    /**
     * Resetting the app
     */
    public void reset(View view) {
        // hide definitions
        for (int i = 0; i <= 6; i++) {
            setVisibility(definitionsArray[i], View.GONE);
        }
        // un-check boxes
        for (int i = 0; i <= 5; i++) { // gets each check box
            // creates an object for the checkbox and resets the color of the checkbox back to colorAccent
            CheckBox checkBox = (CheckBox) findViewById(checkBoxesArray[i]);
            stainBottun(R.color.colorAccent, checkBox);
            // unchecks the checkBox
            checkBox.setChecked(false);
        }
        // Clears and unlocks the name from the editText and the answer of the intersex question
        EditText nameText = (EditText) findViewById(R.id.name);
        //nameText.getText().clear();
        nameText.setEnabled(true);
        EditText intersexAns = (EditText) findViewById(R.id.intersex_ans);
        intersexAns.getText().clear();
        intersexAns.setTextColor(getResources().getColor(R.color.textDefault));
        intersexAns.setEnabled(true);
        // un-checks the radio buttons
        for (int i = 0; i <= 4; i++) {
            // Returns the RadioBox of the correct answer back to the original color
            RadioButton correctAns = (RadioButton) findViewById(radioGroupAns[i]);
            stainBottun(R.color.colorAccent, correctAns);
            // If wrong answer chosen, changes its radioButton color back to the original color as well
            int checkedRadioButtonId = getCheckedBox(radioGroupIdArray[i]);
            if (checkedRadioButtonId != radioGroupAns[i] & checkedRadioButtonId != -1) {
                RadioButton chosenAns = (RadioButton) findViewById(checkedRadioButtonId);
                stainBottun(R.color.colorAccent, chosenAns);
            }

            RadioGroup radioGroup = (RadioGroup) findViewById(radioGroupIdArray[i]);
            radioGroup.clearCheck();
        }
        // unlock the app
        enableChanges(true);
        //ScrollView scrollView = (ScrollView) findViewById(R.id.questionsView);
        //scrollView.scrollTo(0, 0);
        // go back to only showing the openingView and not the questionsView
        setVisibility(R.id.openingView, View.VISIBLE);
        setVisibility(R.id.questionsView, View.GONE);
    }

    /**
     * Checking the answer of a radio group, and changing the color of the ticked radioBox to
     * either green (if correct) or red (if incorrect).
     *
     * @param groupId      is the R.id.radioGroupId
     * @param correctAnsId is the R.id.correctAnsId
     * @return addToScore is either 1 (if correct) or 0 (if incorrect or unmarked)
     */
    public int checkRadioGroup(int groupId, int correctAnsId) {
        int addToScore = 0;
        int checkedRadioButtonId = getCheckedBox(groupId);
        RadioButton checkedAns = (RadioButton) findViewById(checkedRadioButtonId);
        if (checkedRadioButtonId == correctAnsId) { //correct ans was chosen
            // increase the score by 1  and changes the tick next to it to green
            addToScore += 1;
            stainBottun(R.color.correctAns, checkedAns);
        } else {
            RadioButton correctAns = (RadioButton) findViewById(correctAnsId);
            stainBottun(R.color.colorAccent, R.color.correctAns, correctAns);
            if (checkedRadioButtonId != -1) { //incorrect ans was chosen
                // changes the tick next to the chosen box to red
                stainBottun(R.color.wrongAns, checkedAns);
            }
        }
        return addToScore;
    }

    /**
     * Turns on the visibility of a text view
     *
     * @param viewID is the R.id.textViewId
     * @param visibility should either be View.VISIBLE or View.GONE
     */
    public void setVisibility(int viewID, int visibility) {
        View view = (View) findViewById(viewID);
        view.setVisibility(visibility);
    }

    /**
     * Checks if a checkbox is ticked
     *
     * @param checkBoxId is the R.id.checkBoxId
     * @return 0 is not checked and 1 if is checked
     */
    public int isChecked(int checkBoxId) {
        int ansChecked = 0;
        CheckBox checkBox = (CheckBox) findViewById(checkBoxId);
        if (checkBox.isChecked()) { // if checked
            // change ansChecked to 1 and change the checkbox color to green
            ansChecked = 1;
            stainBottun(R.color.correctAns, checkBox);
        } else { // if not checked
            // change the checkbox color to red and check it
            stainBottun(R.color.correctAns, R.color.correctAns, checkBox);
            //checkBox.setChecked(true);
        }
        return ansChecked;
    }

    /**
     * Get the int id of the checked radioBox and lock the radioButtons from being checked/unchecked
     *
     * @param groupId is the R.id.groupId
     * @return the id of the checked radioBox
     */
    public int getCheckedBox(int groupId) {
        RadioGroup radioGroup = (RadioGroup) findViewById(groupId);
        return radioGroup.getCheckedRadioButtonId();
    }

    /**
     * For radioBox: Makes a new ColorStateList for tint and sets the button tint with it.
     * Uses the textDefault color as the color for uncheked button state when not mentioned otherwise.
     *
     * @param colorResource  is the R.color.colorResource name of the color for when checked
     * @param compoundButton is the button object to change it's color
     * @return ColorStateList
     */
    public void stainBottun(int colorResource, CompoundButton compoundButton) {
        stainBottun(colorResource, R.color.textDefault, compoundButton);
    }


    /**
     * For unchecked radioBox: Makes a new ColorStateList for tint and sets the button tint with it.
     * Uses the textDefault color as the color for uncheked button state when not mentioned otherwise.
     *
     * @param colorResource1 is the R.color.colorResource name of the color for when checked
     * @param colorResource2 is the R.color.colorResource name of the color for when not checked (textDefault if not otherwise mentioned)
     * @param compoundButton is the button object to change it's color
     * @return ColorStateList
     */
    public void stainBottun(int colorResource1, int colorResource2, CompoundButton compoundButton) {
        int states[][] = {{android.R.attr.state_checked}, {}};
        int color1 = getResources().getColor(colorResource1);
        int color2 = getResources().getColor(colorResource2);
        int[] colorList = {color1, color2};
        CompoundButtonCompat.setButtonTintList(compoundButton, new ColorStateList(states, colorList));
    }

    /**
     * Disables the ability to change the chosen radioBox in the given radioGroup
     */
    public void enableChanges(boolean enable) {
        // Changing the enable settings of all the radioBoxes in all of the radioGroups
        for (int i = 0; i < radioGroupIdArray.length; i++) {
            RadioGroup radioGroup = (RadioGroup) findViewById(radioGroupIdArray[i]);
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                ((RadioButton) radioGroup.getChildAt(j)).setEnabled(enable);
            }
        }
        // Changing the enable settings of all checkBoxes
        for (int i = 0; i < checkBoxesArray.length; i++) {
            CheckBox checkBox = (CheckBox) findViewById(checkBoxesArray[i]);
            checkBox.setEnabled(enable);
        }
    }
}




