package com.example.mail_app.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mail_app.LocalDatabase;
import com.example.mail_app.MyApp;
import com.example.mail_app.app.mapper.MailDataMapper;
import com.example.mail_app.app.service.MailAPI;
import com.example.mail_app.data.dao.MailDao;
import com.example.mail_app.data.dao.MailLabelCrossRefDao;
import com.example.mail_app.data.dao.MailRecipientCrossRefDao;
import com.example.mail_app.data.dto.MailFromServer;
import com.example.mail_app.data.entity.Mail;
import com.example.mail_app.data.entity.MailLabelCrossRef;
import com.example.mail_app.data.entity.MailRecipientCrossRef;
import com.example.mail_app.data.entity.MailWithRecipientsAndLabels;
import com.example.mail_app.data.model.MailboxType;

import java.util.ArrayList;
import java.util.List;

public class MailRepository {
    private final MailDao mailDao;
    private final MailLabelCrossRefDao labelRefDao;
    private final MailRecipientCrossRefDao recipientRefDao;
    private final MailListData mailListData;
    private final MailAPI api;

    private MailboxType currentType = MailboxType.INBOX;

    public MailRepository(Context context) {
        LocalDatabase db = MyApp.getInstance().getDatabase();
        this.mailDao = db.mailDao();
        this.labelRefDao = db.mailLabelCrossRefDao();
        this.recipientRefDao = db.mailRecipientCrossRefDao();
        this.mailListData = new MailListData();
        this.api = new MailAPI(mailListData, this);
    }

    class MailListData extends MutableLiveData<List<MailWithRecipientsAndLabels>> {
        public MailListData() {
            super();
            setValue(new ArrayList<>());
        }

        @Override
        protected void onActive() {
            super.onActive();
            new Thread(() -> postValue(mailDao.getAllMailsWithRecipientsAndLabels())).start();
        }
    }

    public LiveData<List<MailWithRecipientsAndLabels>> getAll() {
        return mailListData;
    }

    public void saveMany(List<MailFromServer> dtos) {
        new Thread(() -> {
            mailDao.clear();
            for (MailFromServer dto : dtos) {
                saveFullMail(dto);
            }
            mailListData.postValue(mailDao.getAllMailsWithRecipientsAndLabels());
        }).start();
    }

    public void saveFullMail(MailFromServer dto) {
        Mail mail = MailDataMapper.toMail(dto);
        List<MailRecipientCrossRef> recipients = MailDataMapper.toRecipients(dto.id, dto.to);
        List<MailLabelCrossRef> labels = MailDataMapper.toLabelCrossRefs(dto.id, dto.labels);

        mailDao.insertMail(mail);
        for (MailRecipientCrossRef r : recipients) recipientRefDao.insertMailRecipientCrossRef(r);
        for (MailLabelCrossRef l : labels) labelRefDao.insertMailLabelCrossRef(l);
    }

    public void add(final Mail mail) {
        new Thread(() -> {
            mailDao.insertMail(mail);
            reload(currentType);
        }).start();
    }

    public void delete(final Mail mail) {
        new Thread(() -> {
            mailDao.deleteMail(mail);
            reload(currentType);
        }).start();
    }

    public void reload(MailboxType type) {
        currentType = type;
        api.loadByType(type);
    }

    public void search(String query) {
        api.search(query);
    }

    public void loadByLabel(String labelId) {
        api.getByLabel(labelId);
    }

    public void sendDraft(String mailId, Mail mail, List<String> to) {
        api.sendDraft(mailId, mail, to);
    }

    public void updateDraft(String mailId, Mail mail, List<String> to) {
        api.updateDraft(mailId, mail, to);
    }

    public void toggleStar(String mailId) {
        api.toggleStar(mailId);
    }

    public void setSpam(String mailId, boolean isSpam) {
        api.setSpam(mailId, isSpam);
    }

    public void addLabelToMail(String mailId, String labelId) {
        api.addLabelToMail(mailId, labelId);
    }

    public void removeLabelFromMail(String mailId, String labelId) {
        api.removeLabelFromMail(mailId, labelId);
    }

    public void deleteMailById(String mailId) {
        api.deleteMail(mailId);
    }
}