package com.example.mail_app.ui.mail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mail_app.R;
import com.example.mail_app.ui.view.LabelSidebarItem;
import com.example.mail_app.ui.view.SidebarAdapter;
import com.example.mail_app.ui.view.SidebarItem;
import com.example.mail_app.viewmodel.LabelViewModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment that displays the sidebar drawer with mail categories and labels.
 * Allows selecting categories like Inbox, Starred, Sent, etc.,
 * or selecting user-defined labels to filter mails.
 */
public class SidebarFragment extends Fragment {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private SidebarAdapter adapter;
    private LabelViewModel labelViewModel;
    private MailPageActivity parentActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sidebar, container, false);

        drawerLayout = requireActivity().findViewById(R.id.app_drawer_layout);
        recyclerView = view.findViewById(R.id.sidebar_recycler);
        parentActivity = (MailPageActivity) requireActivity();

        labelViewModel = new ViewModelProvider(requireActivity()).get(LabelViewModel.class);
        labelViewModel.reloadLabels();

        setupRecyclerView();
        observeLabels();

        return view;
    }

    /**
     * Sets up the RecyclerView with SidebarAdapter and click listeners.
     */
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SidebarAdapter(new ArrayList<>(), new SidebarAdapter.OnItemClickListener() {
            @Override
            public void onCategoryClick(SidebarItem item) {
                parentActivity.loadCategoryMails(item.getTitle());
                drawerLayout.closeDrawer(GravityCompat.START);
            }

            @Override
            public void onLabelClick(LabelSidebarItem label) {
                parentActivity.loadLabelMails(label.getId());
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    /**
     * Observes label data from the ViewModel and updates the sidebar list.
     */
    private void observeLabels() {
        List<Object> items = new ArrayList<>(Arrays.asList(
                new SidebarItem(getString(R.string.sidebar_inbox), R.drawable.baseline_inbox_24),
                new SidebarItem(getString(R.string.sidebar_starred), R.drawable.outline_star_outline_24),
                new SidebarItem(getString(R.string.sidebar_sent), R.drawable.outline_send_24),
                new SidebarItem(getString(R.string.sidebar_drafts), R.drawable.outline_draft_24),
                new SidebarItem(getString(R.string.sidebar_all_mail), R.drawable.outline_stacked_email_24),
                new SidebarItem(getString(R.string.sidebar_spam), R.drawable.outline_report_24)
        ));

        labelViewModel.getLabels().observe(getViewLifecycleOwner(), labels -> {
            List<Object> updatedItems = new ArrayList<>(items);
            if (labels != null) {
                for (com.example.mail_app.data.entity.Label label : labels) {
                    updatedItems.add(new LabelSidebarItem(label.getId(), label.getName(), label.getColor()));
                }
            }
            adapter.updateItems(updatedItems);
        });
    }
}
