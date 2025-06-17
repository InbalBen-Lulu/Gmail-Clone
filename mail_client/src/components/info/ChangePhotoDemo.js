// import ProfilePictureDialog from './ProfilePictureDialog';
// import Overlay from '../overlay/Overlay';
// import ToggleSwitch from '../common/ToggleThemeButton';

// const ChangePhotoDemo = () => {
//   const handleFileSelect = (file) => {
//     console.log('File selected:', file.name);
//     alert('Selected: ${file.name}');
//   };

//   const handleClose = () => {
//     alert('Dialog closed');
//   };

//   return (
//     <>
//       <Overlay onClick={handleClose} />

//       <div style={{ position: 'fixed', top: '1rem', right: '1rem', zIndex: 1100 }}>
//         <ToggleSwitch />
//       </div>

//       <ProfilePictureDialog
//         onFileSelect={handleFileSelect}
//         onClose={handleClose}
//         imageSrc="/logo192.png"
//       />
//     </>
//   );
// };

// export default ChangePhotoDemo;

// import ChangePhotoButton from './ChangePhotoButton'; // ודא שהנתיב נכון
// import ToggleSwitch from '../common/ToggleThemeButton';
// import { useState } from 'react';

// const ChangePhotoButtonDemo = () => {
//   const [imageUrl, setImageUrl] = useState('/logo192.png');

//   const handleFileSelect = (file) => {
//     const newUrl = URL.createObjectURL(file);
//     setImageUrl(newUrl);
//     console.log('Selected file:', file);
//   };

//   return (
//     <div>
//       <ChangePhotoButton
//         src={imageUrl}
//         size="100px"
//         onFileSelect={handleFileSelect}
//       />
//     </div>
//   );
// };

// export default ChangePhotoButtonDemo;

import React from 'react';
import UserSection from './UserSection'; // עדכני את הנתיב לפי המיקום שלך

const ChangePhotoButtonDemo = () => {
  // נתוני משתמש לדוגמה
  const mockUser = {
    name: 'inbal ben lulu',
    email: 'inbal014789@gmail.com',
    phone: '050-681-5488',
    birthDate: 'August 15, 2000',
    gender: 'Female',
    profileImage: "/logo192.png" // אפשר להחליף בנתיב אמיתי
  };

  return (
    <div>
      <UserSection user={mockUser} />
    </div>
  );
};

export default ChangePhotoButtonDemo;

