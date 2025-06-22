

// import './LabelDialog.css';
// import Overlay from '../overlay/Overlay';
// import TextButton from '../common/button/TextButton';
// import { useEffect, useState } from 'react';

// /**
//  * LabelDialog – modal dialog for creating or editing a label
//  *
//  * Props:
//  * - mode: "new" | "edit" – dialog mode
//  * - initialName: string – optional, used in edit mode to prefill input
//  * - onCancel: function – called when user clicks outside or on Cancel
//  * - onCreate: function(labelName) – called when user confirms
//  */
// const LabelDialog = ({ mode = 'new', initialName = '', onCancel, onCreate }) => {
//   const [labelName, setLabelName] = useState('');

//   // Prefill input if in edit mode
//   useEffect(() => {
//     if (mode === 'edit') {
//       setLabelName(initialName);
//     }
//   }, [mode, initialName]);

//   const handleCreate = () => {
//     if (labelName.trim()) {
//       onCreate(labelName.trim());
//     }
//   };

//   const title = mode === 'edit' ? 'Edit label' : 'New label';
//   const subtitle = mode === 'edit'
//     ? 'Please edit your label name:'
//     : 'Please enter a new label name:';
//   const actionText = mode === 'edit' ? 'Save' : 'Create';

//   return (
//     <>
//       <Overlay onClick={onCancel} />

//       <div className="new-label-dialog">
//         <h2 className="dialog-title">{title}</h2>
//         <p className="dialog-subtitle">{subtitle}</p>

//         <input
//           type="text"
//           className="simple-textbox"
//           value={labelName}
//           onChange={(e) => setLabelName(e.target.value)}
//           maxLength={20}
//         />

//         <div className="dialog-actions">
//           <TextButton variant="ghost" onClick={onCancel}>
//             Cancel
//           </TextButton>
//           <TextButton
//             variant="primary"
//             onClick={handleCreate}
//             disabled={!labelName.trim()}
//           >
//             {actionText}
//           </TextButton>
//         </div>
//       </div>
//     </>
//   );
// };

// export default LabelDialog;
import './LabelDialog.css';
import Overlay from '../overlay/Overlay';
import TextButton from '../common/button/TextButton';
import { useEffect, useState } from 'react';

/**
 * LabelDialog – modal dialog for creating or editing a label.
 *
 * Props:
 * - mode: "new" | "edit"
 * - initialName: string – optional, used in edit mode to prefill input
 * - onCancel: function – called when user clicks outside or on Cancel
 * - onCreate: function(labelName: string) – called when user confirms
 */
const LabelDialog = ({ mode = 'new', initialName = '', onCancel, onCreate }) => {
  const [labelName, setLabelName] = useState('');

  // Prefill the input with existing name when editing
  useEffect(() => {
    if (mode === 'edit') {
      setLabelName(initialName);
    }
  }, [mode, initialName]);

  const handleConfirm = () => {
    if (labelName.trim()) {
      onCreate(labelName.trim());
    }
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
          onChange={(e) => setLabelName(e.target.value)}
          maxLength={20}
        />

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
