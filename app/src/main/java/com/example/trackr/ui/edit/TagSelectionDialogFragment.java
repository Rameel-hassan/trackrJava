package com.example.trackr.ui.edit;

/**
 * @author Rameel Hassan
 * Created 14/06/2023 at 4:33 PM
 */
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;



import com.example.trackr.shared.db.tables.Tag;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TagSelectionDialogFragment extends DialogFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private TaskEditViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(TaskEditViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<Tag> allTags = viewModel.getAllTags().blockingFirst();
        List<Tag> tags = viewModel.getTags();
        List<Boolean> checked = new ArrayList<>();
        for (Tag tag : allTags) {
            checked.add(tags != null && tags.contains(tag));
        }

        return new MaterialAlertDialogBuilder(requireContext())
                .setMultiChoiceItems(
                        getLabelsArray(allTags),
                        getBooleanArray(checked)
                        , new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    viewModel.addTag(allTags.get(which));
                                } else {
                                    viewModel.removeTag(allTags.get(which));
                                }
                            }
                        })
                .create();
    }

    private CharSequence[] getLabelsArray(List<Tag> tags) {
        CharSequence[] labels = new CharSequence[tags.size()];
        for (int i = 0; i < tags.size(); i++) {
            labels[i] = tags.get(i).getLabel();
        }
        return labels;
    }

    private boolean[] getBooleanArray(List<Boolean> booleans) {
        boolean[] array = new boolean[booleans.size()];
        for (int i = 0; i < booleans.size(); i++) {
            array[i] = booleans.get(i);
        }
        return array;
    }
}

