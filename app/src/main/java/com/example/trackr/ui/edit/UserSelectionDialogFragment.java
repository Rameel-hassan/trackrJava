package com.example.trackr.ui.edit;

/**
 * @author Rameel Hassan
 * Created 14/06/2023 at 4:35 PM
 */

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.fragment.NavHostFragment;


import com.example.trackr.R;
import com.example.trackr.shared.db.tables.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserSelectionDialogFragment extends DialogFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private TaskEditViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        NavBackStackEntry backStackEntry = NavHostFragment.findNavController(this).getBackStackEntry(R.id.nav_task_edit_graph);
//        ViewModelProvider viewModelProvider = new ViewModelProvider(backStackEntry, getDefaultViewModelProviderFactory());
        viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(TaskEditViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<User> users = viewModel.getUsers().blockingFirst();

        return new MaterialAlertDialogBuilder(requireContext())
                .setItems(getUsernamesArray(users), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.updateOwner(users.get(which));
                    }
                })
                .create();
    }

    private CharSequence[] getUsernamesArray(List<User> users) {
        CharSequence[] usernames = new CharSequence[users.size()];
        for (int i = 0; i < users.size(); i++) {
            usernames[i] = users.get(i).getUsername();
        }
        return usernames;
    }
}
