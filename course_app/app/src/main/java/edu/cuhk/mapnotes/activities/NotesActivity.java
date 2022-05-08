package edu.cuhk.mapnotes.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import edu.cuhk.mapnotes.databinding.ActivityNotesBinding;
import edu.cuhk.mapnotes.R;
import edu.cuhk.mapnotes.datatypes.NoteEntry;

public class NotesActivity extends AppCompatActivity {

    private ActivityNotesBinding binding;

    private boolean isEditing = false;

    private int noteEntryUid;

    private EditText inputRenameTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent invokerIntent = getIntent();
        if (invokerIntent != null) {
            // has invoker
            this.noteEntryUid = invokerIntent.getIntExtra("noteUid", -1);
            if (this.noteEntryUid < 0) {
                // well this cannot be good
                throw new RuntimeException("noteUid is invalid");
            }
            Log.d("TAG", "Pin notes from intent: UID " + this.noteEntryUid);
        }

        binding = ActivityNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        this.properlySetToolbarTitle(getTitle());
        NoteEntry noteEntry = MapsActivity.noteDatabase.noteEntryDao().getNoteEntry(this.noteEntryUid);
        if (noteEntry != null) {
            this.properlySetToolbarTitle(noteEntry.noteTitle);
        }

        stopEditText(true);

        FloatingActionButton fabEditText = binding.fabEditText;
        fabEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditing) {
                    stopEditText(false);
                    Toast.makeText(getApplicationContext(), "New changes saved", Toast.LENGTH_LONG).show();
                } else {
                    startEditText();
                }
            }
        });

        // edit title
        // first prepare the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.renaming_note_title);
        EditText inputRenameTitle = new EditText(this);
        inputRenameTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        // should have already loaded a valid note
        if (noteEntryUid >= 0) {
            NoteEntry entry = MapsActivity.noteDatabase.noteEntryDao().getNoteEntry(noteEntryUid);
            inputRenameTitle.setText(entry.noteTitle);
        }
        builder.setView(inputRenameTitle);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (noteEntryUid >= 0) {
                    // put the updated title back to the DB
                    NoteEntry noteEntry = MapsActivity.noteDatabase.noteEntryDao().getNoteEntry(noteEntryUid);
                    noteEntry.noteTitle = inputRenameTitle.getText().toString();
                    MapsActivity.noteDatabase.noteEntryDao().updateNoteEntry(noteEntry);
                    properlySetToolbarTitle(noteEntry.noteTitle);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog properDialog = builder.create();

        // then link the dialog to the UI element
        FloatingActionButton fabEditTitle = binding.fabEditRenameTitle;
        fabEditTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                properDialog.show();
            }
        });

        // todo edit tags

        // enable/disable reminder
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle(R.string.config_reminder_title);
        builder2.setView(this.inflateAndInitReminderDialog());
        builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog properDialog2 = builder2.create();
        FloatingActionButton fabEditReminder = binding.fabEditReminders;
        fabEditReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                properDialog2.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.noteEntryUid >= 0) {
            // put the note content into the UI
            EditText noteTextContentEditText = findViewById(R.id.note_edittext);
            // load the notes
            NoteEntry noteEntry = MapsActivity.noteDatabase.noteEntryDao().getNoteEntry(this.noteEntryUid);
            // todo check is text or audio
            noteTextContentEditText.setText(noteEntry.noteText);
        }
    }

    void startEditText() {
        isEditing = true;
        EditText editText = findViewById(R.id.note_edittext);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setEnabled(true);
        editText.requestFocus();

        // Show keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        FloatingActionButton editTextFab = findViewById(R.id.fab_edit_text);
        editTextFab.setImageResource(R.drawable.ic_baseline_check_24);
    }

    void stopEditText(boolean doNotSave) {
        isEditing = false;
        EditText editText = findViewById(R.id.note_edittext);
        editText.setFocusable(false);

        // Collapse keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        FloatingActionButton editTextFab = findViewById(R.id.fab_edit_text);
        editTextFab.setImageResource(R.drawable.ic_baseline_edit_24);

        // save it to the db
        if (!doNotSave) {
            NoteEntry entry = MapsActivity.noteDatabase.noteEntryDao().getNoteEntry(this.noteEntryUid);
            entry.noteText = editText.getText().toString();
            MapsActivity.noteDatabase.noteEntryDao().updateNoteEntry(entry);
        }
    }

    private void properlySetToolbarTitle(CharSequence charSequence) {
        CollapsingToolbarLayout ctl = findViewById(R.id.toolbar_layout);
        ctl.setTitle(charSequence);
    }

    private View inflateAndInitReminderDialog() {
        // inflates and configures the dialog layout etc
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_set_reminder, null);
        EditText reminderTextEdit = dialogView.findViewById(R.id.editTextTextPersonName);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hours, int minutes) {
                // we cannot allow the date+time to be set to "before now"

                // check against system time
                LocalDateTime ldt = LocalDateTime.now();
                DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String nowDateTimeString = ldt.format(myFormatObj);

                String dateTimePickerString = "" + datePicker.getYear() + "-" + String.format("%02d", datePicker.getMonth()) + "-" + String.format("%02d", datePicker.getDayOfMonth());
                dateTimePickerString += " " + String.format("%02d", timePicker.getHour()) + ":" + String.format("%02d", timePicker.getMinute());

                if (dateTimePickerString.compareTo(nowDateTimeString) < 0) {
                    // not OK!
                    datePicker.updateDate(ldt.getYear(), ldt.getMonthValue() - 1, ldt.getDayOfMonth());
                    timePicker.setHour(ldt.getHour());
                    timePicker.setMinute(ldt.getMinute());
                }
            }
        });
        SwitchCompat reminderSwitch = dialogView.findViewById(R.id.switchEnableReminder);
        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                reminderTextEdit.setEnabled(isChecked);
                datePicker.setEnabled(isChecked);
                timePicker.setEnabled(isChecked);
            }
        });
        boolean isChecked = false;
        // todo check should check or not
        reminderSwitch.setChecked(isChecked);
        if (!isChecked) {
            // we must manually apply the changes of states here for the initialization
            reminderTextEdit.setEnabled(false);
            datePicker.setEnabled(false);
            timePicker.setEnabled(false);
        }

        return dialogView;
    }
}
