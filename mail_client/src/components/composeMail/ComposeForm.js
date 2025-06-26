import { useState } from 'react';
import ComposeTextbox from './ComposeTextBox';
import TextButton from '../common/button/TextButton';
import Icon from "../../assets/icons/Icon";
import { SmallIconButton } from '../common/button/IconButtons';
import { useMailService } from '../../services/useMailService';
import { useCompose } from '../../contexts/ComposeContext';
import { useMail } from '../../contexts/MailContext';

import './ComposeForm.css';

/**
 * ComposeForm renders a compose mail interface.
 * Shows fields for recipients, subject, and body with live updates.
 * Supports minimizing, saving draft on close, and sending mails.
 * Uses ComposeContext to prefill existing draft data when available.
 */
const ComposeForm = () => {
    const {
        composeTo,
        composeSubject,
        composeBody,
        composeId,
        isDraft,
        closeCompose
    } = useCompose();

    const [toField, setToField] = useState(composeTo);
    const [subjectField, setSubjectField] = useState(composeSubject);
    const [displayedSubject, setDisplayedSubject] = useState(composeSubject);
    const [bodyField, setBodyField] = useState(composeBody);
    const [minimized, setMinimized] = useState(false);

    const { sendMail, saveDraft, updateDraft } = useMailService();
    const { deleteMail } = useMail();

    const handleSend = async () => {
        try {
            const toList = toField.trim().split(/[\s,]+/)
            const result = await sendMail({
                id: composeId,
                to: toList,
                subject: subjectField,
                body: bodyField,
                isDraft: isDraft
            });

            closeCompose();

            if (result.warning) {
                alert(`${result.warning}\n(${result.invalidEmails?.join(', ')})`);
            }
        } catch (err) {
            alert(err.message);
        }
    };

    const handleDelete = async () => {
        try {
            if (isDraft && composeId) {
                await deleteMail(composeId);
            }
            setToField('');
            setSubjectField('');
            setBodyField('');
            setDisplayedSubject('');
            closeCompose();
        } catch (err) {
            alert('Failed to delete draft: ' + err.message);
        }
    };


    const handleClose = async () => {
        try {
            const toList = toField.trim().split(/\s+/);
            const isEmpty =
                !toField.trim() &&
                !subjectField.trim() &&
                !bodyField.trim();

            if (isDraft && composeId) {
                await updateDraft(composeId, {
                    to: toList,
                    subject: subjectField,
                    body: bodyField
                });
            } else if (!isEmpty) {
                await saveDraft({
                    to: toList,
                    subject: subjectField,
                    body: bodyField
                });
            }

            closeCompose();
        } catch (err) {
            alert('Failed to save draft: ' + err.message);
        }
    };


    return (
        <div className={`compose-mail ${minimized ? 'minimized' : ''}`}>
            <div className="compose-header">
                <div className="header-buttons">
                    <SmallIconButton icon={<Icon name="close" size={16} strokeWidth={1.5} color='var(--compose-icon-color)' />} ariaLabel="Close" onClick={handleClose} />
                    <SmallIconButton
                        icon={
                            minimized ? (
                                <Icon name="open_in_full" size={16} strokeWidth={1.5} color='var(--compose-icon-color)' />
                            ) : (
                                <Icon name="remove" size={16} strokeWidth={1.5} color='var(--compose-icon-color)' />
                            )
                        }
                        ariaLabel={minimized ? "Expand" : "Minimize"}
                        onClick={() => setMinimized(!minimized)}
                    />
                </div>
                <span className="header-title">
                    {displayedSubject.trim() ? displayedSubject : 'New Message'}
                </span>
            </div>

            {!minimized && (
                <>
                    <div className="compose-fields">
                        <ComposeTextbox
                            name="To"
                            value={toField}
                            placeholder="To"
                            onChange={(e) => setToField(e.target.value)}
                        />
                        <ComposeTextbox
                            name="Subject"
                            value={subjectField}
                            placeholder="Subject"
                            onChange={(e) => setSubjectField(e.target.value)}
                            onBlur={() => {
                                if (subjectField.trim()) {
                                    setDisplayedSubject(subjectField);
                                }
                            }}
                        />
                        <ComposeTextbox
                            value={bodyField}
                            onChange={(e) => setBodyField(e.target.value)}
                            variant="compose body"
                        />
                    </div>

                    <div className="compose-actions">
                        <TextButton variant="primary" onClick={handleSend}>
                            Send
                        </TextButton>
                        <SmallIconButton icon={<Icon name="delete" size={16} strokeWidth={1.5} color='var(--compose-icon-color)' />} ariaLabel="Delete" onClick={handleDelete} />
                    </div>
                </>
            )}
        </div>
    );
};

export default ComposeForm;
