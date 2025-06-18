import { useState } from 'react';
import ComposeTextbox from './ComposeTextBox';
import TextButton from '../common/button/TextButton';
import { X, MinusIcon, Maximize2, Trash2 } from "lucide-react";
import { SmallIconButton } from '../common/button/IconButtons';
import { useMailService } from '../../services/mailService';

import './ComposeForm.css';

/**
 * ComposeForm renders a compose mail interface.
 * It supports minimized/expanded modes, live subject updates in the header,
 * and shows editable fields for "To", "Subject", and "Body".
 * The form can optionally be pre-filled using props.
 */
const ComposeForm = ({ to = '', subject = '', body = '', onClose }) => {
    const [toField, setToField] = useState(to);
    const [subjectField, setSubjectField] = useState(subject);
    const [displayedSubject, setDisplayedSubject] = useState(subject);
    const [bodyField, setBodyField] = useState(body);
    const [minimized, setMinimized] = useState(false);

    const { sendMail, saveDraft } = useMailService();

    const handleSend = async () => {
    try {
        const toList = toField.trim().split(/\s+/);
        await sendMail({
        to: toList,
        subject: subjectField,
        body: bodyField
        });
        alert('Mail sent successfully');
        if (onClose) onClose(); // Close the form
    } catch (err) {
        alert('Failed to send mail: ' + err.message);
    }
    };

    const handleDelete = () => {
        setToField('');
        setSubjectField('');
        setBodyField('');
        setDisplayedSubject('');
        onClose?.(); // Close form after deleting
    };

    const handleClose = async () => {
    try {
        const toList = toField.trim().split(/\s+/);
        await saveDraft({
        to: toList,
        subject: subjectField,
        body: bodyField
        });
        alert('Draft saved');
        if (onClose) onClose();
    } catch (err) {
        alert('Failed to save draft: ' + err.message);
    }
    };

    return (
        <div className={`compose-mail ${minimized ? 'minimized' : ''}`}>
            <div className="compose-header">
                <div className="header-buttons">
                    <SmallIconButton icon={<X size={16} strokeWidth={1.5} color='var(--compose-icon-color)' />} ariaLabel="Close" onClick={handleClose} />
                    <SmallIconButton
                        icon={
                            minimized ? (
                                <Maximize2 size={16} strokeWidth={1.5} color='var(--compose-icon-color)' />
                            ) : (
                                <MinusIcon size={16} strokeWidth={1.5} color='var(--compose-icon-color)' />
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
                        <SmallIconButton icon={<Trash2 size={16} strokeWidth={1.5} color='var(--compose-icon-color)' />} ariaLabel="Delete" onClick={handleDelete} />
                    </div>
                </>
            )}
        </div>
    );
};

export default ComposeForm;
