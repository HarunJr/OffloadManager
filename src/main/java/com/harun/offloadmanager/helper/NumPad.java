package com.harun.offloadmanager.helper;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.harun.offloadmanager.R;
import com.harun.offloadmanager.fragments.ExpenseFragment;
import com.harun.offloadmanager.fragments.IncomeFragment;

/**
 * Created by HARUN on 7/4/2016.
 * The Numeric keypad Control Class
 */
public class NumPad {
    private static final String LOG_TAG = NumPad.class.getSimpleName();
    Activity mHostActivity;
    KeyboardView mKeyboardView;
    int position;
    EditText mEditText;

    public NumPad(Activity activity, KeyboardView keyboard_view, int num_pad) {
        mHostActivity = activity;
        mKeyboardView = keyboard_view; //Lookup the KeyboardView
        mKeyboardView.setKeyboard(new Keyboard(activity, num_pad)); // Attach the keyboard to the view
        mKeyboardView.setPreviewEnabled(false); // Do not show the preview balloons
        KeyboardView.OnKeyboardActionListener mOnKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onPress(int i) {
            }

            @Override
            public void onRelease(int i) {
            }

            @Override
            public void onKey(int primaryCode, int[] ints) {
                getEditTextInFocus(primaryCode);
            }

            @Override
            public void onText(CharSequence charSequence) {
            }

            @Override
            public void swipeLeft() {
            }

            @Override
            public void swipeRight() {
            }

            @Override
            public void swipeDown() {
            }

            @Override
            public void swipeUp() {
            }
        };
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        mHostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.w(LOG_TAG, "OnKeyboardActionListener:" + mHostActivity +", "+ activity);
    }

    public void registerEditText(EditText edittext, int mType) {
        position = mType;
        mEditText = edittext;
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showCustomKeyboard(v);
                else hideCustomKeyboard();
            }
        });
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomKeyboard(v);
            }
        });
        // Disable standard keyboard hard way
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                return true; // Consume touch event
            }
        });
        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        Log.w(LOG_TAG, "registerEditText: " + edittext.toString());
    }

    public void getEditTextInFocus(int primaryCode) {
        // Get the EditText and its Editable
        View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
        if (focusCurrent == null || !(focusCurrent instanceof EditText)) {
            Log.w(LOG_TAG, "getEditTextInFocus:" + primaryCode +", "+ focusCurrent +", "+ mHostActivity);
            return;
        }
        getKeyCodeActions(primaryCode, focusCurrent);
    }

    public void getKeyCodeActions(int primaryCode, View focusCurrent) {
        // This wouldn't work for subclasses of EditText.
        //if (focusCurrent == null || focusCurrent.getClass() != EditText.class ) { return; }
        EditText edittext = (EditText) focusCurrent;
        Editable editable = edittext.getText();
        int start = edittext.getSelectionStart();

        EditText incomeEditText = (EditText) focusCurrent.findViewById(R.id.income_input);
        EditText expenseEditText = (EditText) focusCurrent.findViewById(R.id.expense_input);

        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                if (editable != null && start > 0) editable.delete(start - 1, start);
                Log.w(LOG_TAG, "KEYCODE_DELETE:" + primaryCode);
                break;
            case Keyboard.KEYCODE_DONE:
                Log.w(LOG_TAG, "KEYCODE_DONE:" + primaryCode);

                if (edittext.equals(incomeEditText)){
                    Log.w(LOG_TAG, "position:" + position +" "+ edittext);
                    new IncomeFragment().sendCollectionData(edittext);

                }else if (edittext.equals(expenseEditText)){
                    Log.w(LOG_TAG, "position:" + position +" "+ edittext);
                    new ExpenseFragment().openDescriptionDialogue(edittext);

                }else

                return;
//                switch (edittext){
//                    case incomeEditText: {
//                        Log.w(LOG_TAG, "position:" + position +" "+ edittext);
//                        new IncomeFragment().sendCollectionData(edittext);
//                    }
//                    case expenseEditText: {
//                        Log.w(LOG_TAG, "position:" + position +" "+ edittext);
//                        new ExpenseFragment().openDescriptionDialogue(edittext);
//                    }
//                }
//                IncomeFragment incomeFragment = new IncomeFragment();
//                hideCustomKeyboard();
                break;
            default:
                editable.insert(start, Character.toString((char) primaryCode));
                Log.w(LOG_TAG, "insert:" + primaryCode +" "+start+" "+ edittext+": "+ focusCurrent);
        }
    }

    public boolean showCustomKeyboard(View v) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if (v != null)
            ((InputMethodManager) mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        return true;
    }

    public void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

//    public boolean isCustomKeyboardVisible() {
//        Log.w(LOG_TAG, "onKey:" + mKeyboardView);
//        return mKeyboardView.getVisibility() == View.VISIBLE;
//    }
}
