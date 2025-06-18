/**
 * Resizes an image file to 256x256 pixels and returns a base64-encoded PNG string.
 * @param {File} file – the original image file selected by the user
 * @returns {Promise<string>} base64 string of the resized image
 */
export const resizeAndConvertToBase64 = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();

    reader.onload = (event) => {
      const img = new Image();
      img.onload = () => {
        const canvas = document.createElement('canvas');
        canvas.width = 256;
        canvas.height = 256;
        const ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0, 256, 256);
        const base64 = canvas.toDataURL('image/png');
        resolve(base64);
      };
      img.onerror = reject;
      img.src = event.target.result;
    };

    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
};
