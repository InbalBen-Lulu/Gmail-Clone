import { createContext, useContext, useState } from 'react';

const ComposeContext = createContext();

export const ComposeProvider = ({ children }) => {
  const [showCompose, setShowCompose] = useState(false);
  const [composeTo, setComposeTo] = useState('');
  const [composeSubject, setComposeSubject] = useState('');
  const [composeBody, setComposeBody] = useState('');
  const [composeId, setComposeId] = useState(null);
  const [isDraft, setIsDraft] = useState(false);

  // Opens the compose form with optional pre-filled values
  const openCompose = (to = '', subject = '', body = '', draftMode = false, id = null) => {
    setComposeTo(to);
    setComposeSubject(subject);
    setComposeBody(body);
    setIsDraft(draftMode);
    setComposeId(id);
    setShowCompose(true);
  };

  // Closes the compose form and resets all fields
  const closeCompose = () => {
    setShowCompose(false);
    setComposeTo('');
    setComposeSubject('');
    setComposeBody('');
    setComposeId(null);
    setIsDraft(false);
  };

  return (
    <ComposeContext.Provider value={{
      showCompose,
      composeTo,
      composeSubject,
      composeBody,
      composeId,
      isDraft,
      openCompose,
      closeCompose
    }}>
      {children}
    </ComposeContext.Provider>
  );
};

export const useCompose = () => useContext(ComposeContext);
