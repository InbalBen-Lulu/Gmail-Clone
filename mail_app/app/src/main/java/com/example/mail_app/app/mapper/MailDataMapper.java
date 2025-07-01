package com.example.mail_app.app.mapper;

import com.example.mail_app.data.dto.MailFromServer;
import com.example.mail_app.data.entity.Label;
import com.example.mail_app.data.entity.Mail;
import com.example.mail_app.data.entity.MailLabelCrossRef;
import com.example.mail_app.data.entity.MailRecipientCrossRef;

import java.util.ArrayList;
import java.util.List;

public class MailDataMapper {

    public static Mail toMail(MailFromServer dto) {
        return new Mail(
                dto.id,
                dto.from.userId,
                dto.subject,
                dto.body,
                dto.sentAt,
                dto.type,
                dto.isDraft,
                dto.isSpam,
                dto.isStar,
                dto.isRead
        );
    }

    public static List<MailRecipientCrossRef> toRecipients(String mailId, List<String> userIds) {
        List<MailRecipientCrossRef> list = new ArrayList<>();
        for (String userId : userIds) {
            list.add(new MailRecipientCrossRef(mailId, userId));
        }
        return list;
    }

    public static List<MailLabelCrossRef> toLabelCrossRefs(String mailId, List<Label> labels) {
        List<MailLabelCrossRef> list = new ArrayList<>();
        for (Label label : labels) {
            list.add(new MailLabelCrossRef(mailId, label.getId()));
        }
        return list;
    }
}