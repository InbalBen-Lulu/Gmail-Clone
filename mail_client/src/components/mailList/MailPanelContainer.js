import { useEffect } from 'react';
import { useParams, useSearchParams } from 'react-router-dom';
import MailPanel from './MailPanel';
import { useMail } from '../../contexts/MailContext';
import { useLabels } from '../../contexts/LabelContext';

/**
 * MailPanel is the main container for the mail content area.
 * 
 * - If a mail ID is in the URL and the mail exists, it shows the full mail view (MailDetails).
 * - If a draft mail is clicked, it opens the compose window prefilled.
 * - If no mail is selected, it displays the mail list (MailList) and a toolbar (MailToolbar).
 * - Handles pagination, refresh, and routing.
 */

const MailPanelContainer = () => {
    const { category, mailId } = useParams();
    const mailIdNum = Number(mailId);
    const [searchParams] = useSearchParams();
    const { refreshLabels } = useLabels();

    const offset = parseInt(searchParams.get('offset')) || 0;
    const limit = parseInt(searchParams.get('limit')) || 50;

    const { loadMails } = useMail();

    useEffect(() => {
        loadMails(offset, limit, category, mailIdNum);
    }, [offset, limit, category, mailIdNum, loadMails]);

    useEffect(() => {
        refreshLabels();
    }, [category, refreshLabels]);

    return (
        <div>
            <MailPanel />
        </div>
    );
};

export default MailPanelContainer;
