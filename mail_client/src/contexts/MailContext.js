import { createContext, useContext, useState, useCallback } from 'react';
import { useMailService } from '../services/useMailService';
import { useLabelService } from '../services/useLabelService';
import { useLabels } from './LabelContext';

// Context for accessing and managing mail data
const MailContext = createContext();

export const MailProvider = ({ children }) => {
    const { labels } = useLabels();
    const [mails, setMails] = useState([]);
    const [totalCount, setTotalCount] = useState(0);
    const [offset, setOffset] = useState(0);
    const [limit, setLimit] = useState(50);

    const {
        fetchMails,
        fetchMailById,
        deleteMail: deleteMailService,
        toggleStar: toggleStarService,
        setSpamStatus: setSpamStatusService
    } = useMailService();

    const {
        addLabelToMail: addLabelToMailService,
        removeLabelFromMail: removeLabelFromMailService
    } = useLabelService();

    // Loads mails for a given page and optionally ensures a specific mail is included
    const loadMails = useCallback(async (newOffset, newLimit, category, mailId = null) => {
        try {
            const response = await fetchMails(newOffset, newLimit, category);
            const mailsList = [...response.mails];

            if (mailId) {
                const mailById = await fetchMailById(mailId);
                if (mailById) {
                    const index = mailsList.findIndex(m => m.id === mailId);
                    if (index >= 0) {
                        mailsList[index] = mailById;
                    } else {
                        mailsList.push(mailById);
                    }
                }
            }

            setOffset(newOffset);
            setLimit(newLimit);
            setMails(mailsList);
            setTotalCount(response.total);
        } catch (err) {
            console.error('Failed to fetch mails:', err);
        }
    }, []);



    // Refreshes a specific mail by ID
    const refreshMail = async (mailId) => {
        const updated = await fetchMailById(mailId);
        setMails(prev =>
            prev.map(m => m.id === updated.id ? updated : m)
        );
    };

    // Deletes a mail by ID
    const deleteMail = async (id) => {
        await deleteMailService(id);
        setMails(prev => prev.filter(mail => mail.id !== id));
    };

    // Toggles the star status of a mail
    const toggleStar = async (id) => {
        await toggleStarService(id);
        setMails(prev =>
            prev.map(mail =>
                mail.id === id ? { ...mail, isStar: !mail.isStar } : mail
            )
        );
    };

    // Sets or unsets a mail as spam
    const setSpamStatus = async (id, isSpam) => {
        await setSpamStatusService(id, isSpam);
        setMails(prev =>
            prev.map(mail =>
                mail.id === id ? { ...mail, isSpam } : mail
            )
        );
    };

    // Toggles a label on a specific mail (adds or removes)
    const toggleLabel = async (mailId, labelId) => {
        const mailIdNum = mailId;
        const mail = mails.find(m => m.id === mailIdNum);
        if (!mail) {
            console.error('Mail not found:', mailId);
            return;
        }

        const hasLabel = mail.labels?.some(l => l.id === labelId);
        const label = labels.find(l => l.id === labelId);

        if (!label) {
            console.error('Label not found:', labelId);
            return;
        }

        if (hasLabel) {
            await removeLabelFromMailService(mailIdNum, labelId);
        } else {
            await addLabelToMailService(mailIdNum, labelId);
        }

        setMails(prev =>
            prev.map(m =>
                m.id === mailIdNum
                    ? {
                        ...m,
                        labels: hasLabel
                            ? m.labels.filter(l => l.id !== labelId)
                            : [...m.labels, label]
                    }
                    : m
            )
        );
    };


    return (
        <MailContext.Provider
            value={{
                mails,
                totalCount,
                offset,
                limit,
                loadMails,
                refreshMail,
                deleteMail,
                toggleStar,
                setSpamStatus,
                toggleLabel
            }}
        >
            {children}
        </MailContext.Provider>
    );
};

export const useMail = () => useContext(MailContext);
