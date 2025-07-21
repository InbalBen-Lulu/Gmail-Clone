package com.example.mail_app.ui.mail;

import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.ui.mail.action.MailActionModeCallback;
import com.example.mail_app.ui.mail.adapter.MailListAdapter;
import com.example.mail_app.utils.UiUtils;
import com.example.mail_app.viewmodel.MailViewModel;

import java.util.ArrayList;

/**
 * Fragment that displays a list of mails (inbox, sent, drafts, etc.)
 * Supports infinite scroll, pull-to-refresh, and long-click actions.
 */
public class MailListFragment extends Fragment {

    private MailViewModel viewModel;
    private MailListAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private boolean isLoading = false; // Prevents duplicate loading
    private static final int VISIBLE_THRESHOLD = 4; // When to trigger "load more"

    private static final String ARG_CATEGORY = "category";
    private String category;

    private ActionMode actionMode; // Action bar for long-press actions
    private FullMail selectedMail;

    /**
     * Factory method to create a new instance with a specific category.
     */
    public static MailListFragment newInstance(String category) {
        MailListFragment fragment = new MailListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Constructor that inflates the fragment layout.
     */
    public MailListFragment() {
        super(R.layout.fragment_mail_list);
    }

    /**
     * Called after the view is created – initializes UI components and listeners.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        category = getArguments() != null ? getArguments().getString(ARG_CATEGORY) : null;

        RecyclerView recyclerView = view.findViewById(R.id.mailRecyclerView);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        // Setup RecyclerView adapter with mail click listeners
        adapter = new MailListAdapter(new ArrayList<>(), new MailListAdapter.OnMailClickListener() {
            @Override
            public void onClick(FullMail mail) {
                if (actionMode != null) {
                    actionMode.finish(); // Exit selection mode on normal click
                } else {
                    onMailClick(mail);
                }
            }

            @Override
            public void onLongClick(FullMail mail) {
                if (actionMode == null) {
                    selectedMail = mail;
                    adapter.setSelectedMailId(mail.getMail().getId());

                    actionMode = requireActivity().startActionMode(
                            new MailActionModeCallback(
                                    requireContext(),
                                    selectedMail,
                                    viewModel,
                                    adapter,
                                    () -> {
                                        actionMode = null;
                                        selectedMail = null;
                                    }
                            )
                    );
                }
            }

            @Override
            public void onToggleStar(String mailId) {
                viewModel.toggleStar(mailId, msg -> UiUtils.showMessage(requireActivity(), msg));
            }
        });

        // Setup RecyclerView layout manager and attach adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(MailViewModel.class);

        if (category != null) {
            viewModel.setCategory(category); // Filters mails by category
        }

        TextView emptyTextView = view.findViewById(R.id.emptyTextView);

        // Observe mails from ViewModel and update UI accordingly
        viewModel.getMails().observe(getViewLifecycleOwner(), mails -> {
            swipeRefresh.setRefreshing(false);
            isLoading = false;

            adapter.setMails(mails);

            emptyTextView.setVisibility(mails.isEmpty() ? View.VISIBLE : View.GONE);
        });

        // Enable pull-to-refresh
        swipeRefresh.setOnRefreshListener(() -> {
            viewModel.reloadCurrentCategory(); // Reload current mail category or label
        });

        // Add scroll listener to detect when reaching the end of the list
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                    isLoading = true;
                    viewModel.loadMoreMails(); // Loads more based on current state
                }
            }
        });
    }

    /**
     * Handles mail item click.
     * If it's a draft, opens ComposeActivity to edit.
     * Otherwise, opens MailDetailsActivity to view mail.
     */
    private void onMailClick(FullMail mail) {
        String mailId = mail.getMail().getId();

        if (mail.getMail().isDraft()) {
            // Open Compose screen for editing draft
            Intent intent = new Intent(requireContext(), ComposeActivity.class);
            intent.putExtra(ComposeActivity.EXTRA_MAIL_ID, mailId);
            startActivity(intent);
        } else {
            // Open full mail details screen
            MailDetailsActivity.open(requireContext(), mailId);
        }
    }
}
