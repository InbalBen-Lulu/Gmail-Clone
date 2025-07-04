package com.example.mail_app.ui.mail;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.viewmodel.MailViewModel;
import java.util.ArrayList;
import java.util.List;

public class MailListActivity extends AppCompatActivity {

    private MailViewModel viewModel;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private com.example.mail_app.app.adapter.MailListAdapter adapter;
    private boolean isLoading = false;
    private int offset = 0;
    private static final int LIMIT = 20; // אפשר לשנות לפי הצורך

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_list);

        recyclerView = findViewById(R.id.mailRecyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        adapter = new com.example.mail_app.app.adapter.MailListAdapter(new ArrayList<>(), new com.example.mail_app.app.adapter.MailListAdapter.OnMailClickListener() {
            @Override
            public void onClick(FullMail mail) {
                onMailClick(mail); // את הפונקציה שלך
            }

            @Override
            public void onLongClick(FullMail mail) {
                onMailLongClick(mail); // את הפונקציה שלך
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(MailViewModel.class);

        // רענון מלמעלה
        swipeRefresh.setOnRefreshListener(() -> {
            offset = 0;
            viewModel.loadInboxMails(); // אפשר לשנות לקטגוריה אחרת
        });

        // תצפית על רשימת המיילים
        viewModel.getMails().observe(this, mails -> {
            swipeRefresh.setRefreshing(false);
            if (offset == 0) {
                adapter.setMails(mails);
            } else {
                adapter.appendMails(mails);
            }
            isLoading = false;
        });

        // טעינה ראשונית
        viewModel.loadInitialMails();

        // גלילה אינסופית
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (!rv.canScrollVertically(1) && !isLoading) {
                    isLoading = true;
                    offset += LIMIT;
                    viewModel.scrollLoadInboxMails(offset, LIMIT); // לפי הקטגוריה
                }
            }
        });
    }

    private void onMailClick(FullMail mail) {
        // פותח את המייל במסך אחר (תכף נממש)
        MailDetailsActivity.open(this, mail.getMail().getId());
    }

    private void onMailLongClick(FullMail mail) {
        // תפריט בחירה על מייל – תכף נוסיף גם את זה
    }
}
