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
 * Fragment that displays a list of mails based on current ViewModel state.
 * Supports infinite scroll and pull-to-refresh.
 */
public class MailListFragment extends Fragment {

    private MailViewModel viewModel;
    private MailListAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private boolean isLoading = false; // prevent double load
    private static final int VISIBLE_THRESHOLD = 4;

    private static final String ARG_CATEGORY = "category";
    private String category;

    private ActionMode actionMode;

    private FullMail selectedMail;

    public static MailListFragment newInstance(String category) {
        MailListFragment fragment = new MailListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    public MailListFragment() {
        super(R.layout.fragment_mail_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        category = getArguments() != null ? getArguments().getString(ARG_CATEGORY) : null;

        RecyclerView recyclerView = view.findViewById(R.id.mailRecyclerView);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        adapter = new MailListAdapter(new ArrayList<>(), new MailListAdapter.OnMailClickListener() {
            public void onClick(FullMail mail) {
                if (actionMode != null) {
                    actionMode.finish(); // לחיצה רגילה יוצאת ממצב הבחירה
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(MailViewModel.class);

        if (category != null) {
            viewModel.setCategory(category);
        }

        TextView emptyTextView = view.findViewById(R.id.emptyTextView);

        viewModel.getMails().observe(getViewLifecycleOwner(), mails -> {
            swipeRefresh.setRefreshing(false);
            isLoading = false;

            adapter.setMails(mails);

            if (mails.isEmpty()) {
                emptyTextView.setVisibility(View.VISIBLE);
            } else {
                emptyTextView.setVisibility(View.GONE);
            }
        });


        swipeRefresh.setOnRefreshListener(() -> {
            viewModel.reloadCurrentCategory(); // דורש שתהיה פונקציה כזו ב־ViewModel
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                    isLoading = true;
                    viewModel.loadMoreMails(); // יזהה לפי המצב הנוכחי (קטגוריה או label)
                }
            }
        });


    }

    private void onMailClick(FullMail mail) {
        String mailId = mail.getMail().getId();

        if (mail.getMail().isDraft()) {
            // פתח את Compose לעריכת טיוטה
            Intent intent = new Intent(requireContext(), ComposeActivity.class);
            intent.putExtra(ComposeActivity.EXTRA_MAIL_ID, mailId);
            startActivity(intent);
        } else {
            // פתח את המסך הרגיל של מייל
            MailDetailsActivity.open(requireContext(), mailId);
        }
    }
}
