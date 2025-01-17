package org.wikipedia.editing.summaries;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import org.wikipedia.PageTitle;
import org.wikipedia.R;
import org.wikipedia.Utils;
import org.wikipedia.ViewAnimations;
import org.wikipedia.editing.EditSectionActivity;

public class EditSummaryFragment extends Fragment {
    private PageTitle title;
    private View editSummaryContainer;
    private EditSummaryHandler editSummaryHandler;
    private EditSectionActivity parentActivity;
    private AutoCompleteTextView summaryText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        editSummaryContainer = inflater.inflate(R.layout.fragment_preview_summary, container, false);
        summaryText = (AutoCompleteTextView) editSummaryContainer.findViewById(R.id.edit_summary_edit);

        // ...so that clicking the "Done" button on the keyboard will have the effect of
        // clicking the "Next" button in the actionbar:
        summaryText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if ((keyEvent != null
                        && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    parentActivity.clickNextButton();
                }
                return false;
            }
        });

        return editSummaryContainer;
    }

    public void setTitle(PageTitle title) {
        this.title = title;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            summaryText.setText(savedInstanceState.getString("summaryText"));
        }
        parentActivity = (EditSectionActivity)getActivity();
        editSummaryHandler = new EditSummaryHandler(getActivity(), editSummaryContainer, title);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * Shows (fades in) the custom edit summary fragment.
     * When fade-in completes, the keyboard is shown automatically, and the state
     * of the actionbar button(s) is updated.
     */
    public void show() {
        ViewAnimations.fadeIn(editSummaryContainer, new Runnable() {
            @Override
            public void run() {
                parentActivity.supportInvalidateOptionsMenu();
                summaryText.requestFocus();
                InputMethodManager imm = (InputMethodManager) parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(summaryText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    /**
     * Hides (fades out) the custom edit summary fragment.
     * When fade-out completes, the keyboard is hidden, and the state of the actionbar
     * button(s) is updated.
     */
    public void hide() {
        ViewAnimations.fadeOut(editSummaryContainer, new Runnable() {
            @Override
            public void run() {
                Utils.hideSoftKeyboard(parentActivity);
                parentActivity.supportInvalidateOptionsMenu();
            }
        });
    }

    public boolean isActive() {
        return editSummaryContainer.getVisibility() == View.VISIBLE;
    }

    public boolean handleBackPressed() {
        if (isActive()) {
            hide();
            return editSummaryHandler.handleBackPressed();
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("summaryText", summaryText.getText().toString());
    }

    /**
     * Gets the custom ("other") summary, if any, that the user has entered.
     * @return Custom summary of the edit.
     */
    public String getSummary() {
        return summaryText.getText().toString();
    }

    /**
     * Commits the custom ("other") edit summary that the user may have entered,
     * so that it remains saved in a drop-down list for future use.
     */
    public void saveSummary() {
        if(summaryText.length() > 0) {
            editSummaryHandler.persistSummary();
        }
    }

}