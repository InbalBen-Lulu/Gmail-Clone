package com.example.mail_app.ui.mail;

import android.content.Context;
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
import com.example.mail_app.ui.view.SidebarAdapter;
import com.example.mail_app.ui.view.SidebarItem;
import com.example.mail_app.viewmodel.LabelViewModel;
import java.util.Arrays;
import java.util.List;

public class SidebarFragment extends Fragment {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private SidebarAdapter adapter;
    private List<SidebarItem> sidebarItems;
    private LabelViewModel labelViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sidebar, container, false);

        drawerLayout = requireActivity().findViewById(R.id.app_drawer_layout);
        recyclerView = view.findViewById(R.id.sidebar_recycler);

        labelViewModel = new ViewModelProvider(requireActivity()).get(LabelViewModel.class);

        setupSidebarItems();
        setupRecyclerView();

        return view;
    }

    private void setupSidebarItems() {
        Context context = requireContext();
        sidebarItems = new java.util.ArrayList<>(Arrays.asList(
                new SidebarItem(context.getString(R.string.sidebar_inbox), R.drawable.baseline_inbox_24),
                new SidebarItem(context.getString(R.string.sidebar_starred), R.drawable.outline_star_outline_24),
                new SidebarItem(context.getString(R.string.sidebar_sent), R.drawable.outline_send_24),
                new SidebarItem(context.getString(R.string.sidebar_drafts), R.drawable.outline_draft_24),
                new SidebarItem(context.getString(R.string.sidebar_all_mail), R.drawable.outline_stacked_email_24),
                new SidebarItem(context.getString(R.string.sidebar_spam), R.drawable.outline_report_24)
        ));

        // Observe labels
        labelViewModel.getLabels().observe(getViewLifecycleOwner(), labels -> {
            if (labels != null) {
                for (com.example.mail_app.data.entity.Label label : labels) {
                    sidebarItems.add(new SidebarItem(
                            label.getName(),
                            R.drawable.outline_label_24,
                            label.getColor()
                    ));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SidebarAdapter(sidebarItems, position -> {
            String selectedTitle = sidebarItems.get(position).getTitle();
            // לדוגמה: ביצוע פעולה לפי הבחירה

            drawerLayout.closeDrawer(GravityCompat.START);
        });
        recyclerView.setAdapter(adapter);
    }
}

