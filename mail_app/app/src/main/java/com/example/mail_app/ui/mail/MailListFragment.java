package com.example.mail_app.ui.mail;

import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.mail_app.ui.view.MailListAdapter;
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

        // קבלת הקטגוריה מה־arguments
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
                    actionMode = requireActivity().startActionMode(actionModeCallback);
                }
            }

            @Override
            public void onToggleStar(String mailId) {
                viewModel.toggleStar(mailId);
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

        // עדכון המיילים
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


        // רענון ידני מלמעלה
        swipeRefresh.setOnRefreshListener(() -> {
            viewModel.reloadCurrentCategory(); // דורש שתהיה פונקציה כזו ב־ViewModel
        });

        // גלילה אינסופית
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
        MailDetailsActivity.open(requireContext(), mail.getMail().getId());
    }

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.mail_action_menu, menu);

            // ⬅️ הסתרת שורת החיפוש
            View searchBar = requireActivity().findViewById(R.id.search_bar_wrapper);
            if (searchBar != null) searchBar.setVisibility(View.GONE);

            return true;
        }


        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // אין שינוי
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (selectedMail != null) {
                int itemId = item.getItemId();

                if (itemId == R.id.action_delete) {
                    viewModel.deleteMail(selectedMail.getMail().getId());
                    mode.finish();
                    return true;
                } else if (itemId == R.id.action_label) {
                    Log.d("ActionMode", "Label clicked");
                    mode.finish();
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            selectedMail = null;
            adapter.clearSelection();

            // ⬅️ החזרת שורת החיפוש
            View searchBar = requireActivity().findViewById(R.id.search_bar_wrapper);
            if (searchBar != null) searchBar.setVisibility(View.VISIBLE);
        }
    };

}
