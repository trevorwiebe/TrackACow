package com.trevorwiebe.trackacow;

import android.app.DatePickerDialog;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.trackacow.objects.CowObject;
import com.trevorwiebe.trackacow.objects.DrugsGivenObject;
import com.trevorwiebe.trackacow.objects.PenObject;
import com.trevorwiebe.trackacow.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;

public class MarkACowDeadActivity extends AppCompatActivity {

    private TextInputEditText mTagNumber;
    private TextInputEditText mDate;
    private TextInputEditText mNotes;

    private DatePickerDialog.OnDateSetListener mStartDatePicker;
    private Calendar mCalendar = Calendar.getInstance();
    private PenObject mSelectedPen;
    private DatabaseReference mBaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_a_cow_dead);

        mSelectedPen = getIntent().getParcelableExtra("penObject");

        mBaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mTagNumber = findViewById(R.id.deads_tag_number);
        mDate = findViewById(R.id.deads_date);
        mNotes = findViewById(R.id.deads_notes);

        mDate.setText(Utility.convertMillisToDate(mCalendar.getTimeInMillis()));

        mDate.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                new DatePickerDialog(MarkACowDeadActivity.this,
                        mStartDatePicker,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        mStartDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mDate.setText(Utility.convertMillisToDate(mCalendar.getTimeInMillis()));
            }
        };
    }

    public void markAsDead(View view1){
        if(mTagNumber.length() == 0 || mDate.length() == 0 ){
            Snackbar.make(view1, "Please fill the blanks", Snackbar.LENGTH_LONG).show();
            return;
        }
        int tagNumber = Integer.parseInt(mTagNumber.getText().toString());
        String strDate = mDate.getText().toString();
        long date = Utility.convertDateToMillis(strDate);
        String notes = mNotes.getText().toString();

        DatabaseReference pushRef = mBaseRef.child(CowObject.COW).push();

        CowObject CowObject = new CowObject(tagNumber, pushRef.getKey(), mSelectedPen.getPenId(), notes, false, date, null);

        pushRef.setValue(CowObject);

        mTagNumber.setText("");
        mNotes.setText("");
        mCalendar = Calendar.getInstance();
        mDate.setText(Utility.convertMillisToDate(mCalendar.getTimeInMillis()));
        mTagNumber.requestFocus();

        Snackbar.make(view1, "Save successfully!", Snackbar.LENGTH_LONG).show();

    }
}
