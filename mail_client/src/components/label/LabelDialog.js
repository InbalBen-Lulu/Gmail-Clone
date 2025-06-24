import './LabelDialog.css';
import Overlay from '../overlay/Overlay';
import TextButton from '../common/button/TextButton';
import ErrorMessage from '../common/input/ErrorMessage';
import { useEffect, useState, useRef } from 'react';

/**
 * LabelDialog – modal dialog for creating or editing a label.
 *
 * Props:
 * - mode: "new" | "edit"
 * - initialName: string – optional, used in edit mode to prefill input
 * - existingLabels: string[] – all current label names, for validation
 * - onCancel: function – called when user clicks outside or on Cancel
 * - onCreate: function(labelName: string) – called when user confirms
 * - externalError: string – optional error message from outside
 * - clearExternalError: function – clears external error
 */
const LabelDialog = ({
  mode = 'new',
  initialName = '',
  existingLabels = [],
  onCancel,
  onCreate,
  externalError = '',
  clearExternalError = () => {},
}) => {
  const [labelName, setLabelName] = useState('');
  const [error, setError] = useState('');
  const inputRef = useRef(null);

  // Prefill input in edit mode
  useEffect(() => {
    if (mode === 'edit') {
      setLabelName(initialName);
    }
  }, [mode, initialName]);

  // Autofocus the input when dialog opens
  useEffect(() => {
    if (inputRef.current) inputRef.current.focus();
  }, []);

  const handleConfirm = () => {
    const trimmed = labelName.trim();

    if (!trimmed) {
      setError('Label name cannot be empty.');
      return;
    }

    // Case-insensitive duplicate check
    const normalized = trimmed.toLowerCase();
    const isDuplicate = existingLabels
      .map((name) => name.toLowerCase())
      .includes(normalized);

    if (mode === 'new' && isDuplicate) {
      setError('A label with this name already exists.');
      return;
    }

    setError('');
    clearExternalError();
    onCreate(trimmed);
  };

  const title = mode === 'edit' ? 'Edit label' : 'New label';
  const subtitle = mode === 'edit'
    ? 'Please edit your label name:'
    : 'Please enter a new label name:';
  const actionText = mode === 'edit' ? 'Save' : 'Create';

  return (
    <>
      <Overlay onClick={onCancel} />

      <div className="new-label-dialog">
        <h2 className="dialog-title">{title}</h2>
        <p className="dialog-subtitle">{subtitle}</p>

        <input
          type="text"
          className="simple-textbox"
          value={labelName}
          ref={inputRef}
          onChange={(e) => {
            setLabelName(e.target.value);
            setError('');
            clearExternalError();
          }}
          maxLength={20}
        />

        {/* Inline error below the input */}
        <ErrorMessage message={error || externalError} />

        <div className="dialog-actions">
          <TextButton variant="ghost" onClick={onCancel}>
            Cancel
          </TextButton>
          <TextButton
            variant="primary"
            onClick={handleConfirm}
            disabled={!labelName.trim()}
          >
            {actionText}
          </TextButton>
        </div>
      </div>
    </>
  );
};

export default LabelDialog;
