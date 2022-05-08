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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import edu.cuhk.mapnotes.databinding.ActivityNotesBinding;
import edu.cuhk.mapnotes.R;
import edu.cuhk.mapnotes.datatypes.NoteEntry;
import edu.cuhk.mapnotes.datatypes.NoteReminder;
import edu.cuhk.mapnotes.util.NoteEntryUtil;
import edu.cuhk.mapnotes.util.NotificationUtil;

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
        View dialogView = this.inflateAndInitReminderDialog();
        builder2.setView(dialogView);
        builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                testNotification();
                // did the user say to enable reminders?
                SwitchCompat reminderSwitch = dialogView.findViewById(R.id.switchEnableReminder);
                if (!reminderSwitch.isChecked()) {
                    // no. remove any reminders
                    if (noteEntryUid >= 0) {
                        MapsActivity.noteDatabase.noteReminderDao().clearAllRemindersOfNote(noteEntryUid);
                    }
                    updateReminderDisplayText();
                    return;
                }
                // build a timestamp string and then convert it into a Date object
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
                TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
                String timestampString = "" + datePicker.getYear() + "-" + String.format("%02d", datePicker.getMonth() + 1) + "-" + String.format("%02d", datePicker.getDayOfMonth());
                timestampString += " " + String.format("%02d", timePicker.getHour()) + ":" + String.format("%02d", timePicker.getMinute());

                try {
                    Date reminderDate = formatter.parse(timestampString);
                    assert reminderDate != null;
                    long timestampMs = reminderDate.getTime();

                    // try to upsert
                    List<NoteReminder> reminderList = MapsActivity.noteDatabase.noteReminderDao().getAllNoteReminders(noteEntryUid);
                    NoteReminder reminder;
                    if (reminderList.size() > 0) {
                        reminder = reminderList.get(0);
                    } else {
                        reminder = new NoteReminder();
                        reminder.noteUid = noteEntryUid;
                    }
                    EditText reminderTextEditText = dialogView.findViewById(R.id.editTextReminderText);
                    reminder.reminderText = reminderTextEditText.getText().toString();
                    reminder.reminderTimestamp = timestampMs;
                    MapsActivity.noteDatabase.noteReminderDao().upsertNoteReminders(reminder);
                    updateReminderDisplayText();
                    scheduleNoteEntryReminder();
                } catch (ParseException x) {
                    Log.e("TAG", "Failed to parse date! I got: " + timestampString);
                }
            }
        });
        builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateReminderDisplayText();
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

            // check the reminder
            this.updateReminderDisplayText();
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
        EditText reminderTextEdit = dialogView.findViewById(R.id.editTextReminderText);
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

                String dateTimePickerString = "" + datePicker.getYear() + "-" + String.format("%02d", datePicker.getMonth() + 1) + "-" + String.format("%02d", datePicker.getDayOfMonth());
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
        NoteReminder reminder = NoteEntryUtil.getValidReminderOfNoteEntry(this.noteEntryUid, 0);
        if (reminder != null) {
            // valid time
            isChecked = true;
            // update the various values here
            LocalDateTime ldt = LocalDateTime.ofEpochSecond(reminder.reminderTimestamp / 1000, (int) (reminder.reminderTimestamp % 1000), OffsetDateTime.now().getOffset());
            datePicker.updateDate(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth());
            timePicker.setHour(ldt.getHour());
            timePicker.setMinute(ldt.getMinute());
        }
        reminderSwitch.setChecked(isChecked);
        if (!isChecked) {
            // we must manually apply the changes of states here for the initialization
            reminderTextEdit.setEnabled(false);
            datePicker.setEnabled(false);
            timePicker.setEnabled(false);
        }

        return dialogView;
    }

    private void updateReminderDisplayText() {
        // check reminder
        // this does not use the util method because we need to detect expiration
        List<NoteReminder> reminderList = MapsActivity.noteDatabase.noteReminderDao().getAllNoteReminders(this.noteEntryUid);
        TextView reminderText = findViewById(R.id.reminderText);
        if (reminderList.isEmpty()) {
            // no reminder; set early
            reminderText.setText(R.string.note_reminders_default);
            return;
        }
        // has reminder
        NoteReminder reminder = reminderList.get(0);
        // format: Reminder set: [date]
        Date reminderDate = new Date(reminder.reminderTimestamp);
        if (System.currentTimeMillis() > reminderDate.getTime()) {
            // expired!
            reminderText.setText("Reminder expired.");
            return;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String timestampString = format.format(reminderDate);
        reminderText.setText("Reminder set: " + timestampString);
    }

    private void testNotification() {
        NotificationUtil util = new NotificationUtil(this);
        long currentTimeMs = System.currentTimeMillis();
        long tenSeconds = 1000 * 10;
        long triggerTimeMs = currentTimeMs + tenSeconds; //triggers a reminder after 10 seconds.
        util.setReminder(triggerTimeMs);
    }

    private void scheduleNoteEntryReminder() {
        if (this.noteEntryUid < 0) {
            return;
        }
        List<NoteReminder> reminderList = MapsActivity.noteDatabase.noteReminderDao().getAllNoteReminders(this.noteEntryUid);
        if (reminderList.isEmpty()) {
            return;
        }
        NoteReminder reminder = reminderList.get(0);
        if (reminder.reminderTimestamp < System.currentTimeMillis()) {
            // invalid
            return;
        }
        //
        NotificationUtil util = new NotificationUtil(this);
        util.setNoteEntryReminder(reminder);
    }
}
