/**
 * Resizes an image file to 256x256 pixels after center-cropping it to a square,
 * and returns a base64-encoded PNG string.
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
        const ctx = canvas.getContext('2d');

        // Determine square crop from center
        const side = Math.min(img.width, img.height);
        const sx = (img.width - side) / 2;
        const sy = (img.height - side) / 2;

        // Set canvas to target size
        canvas.width = 256;
        canvas.height = 256;

        // Draw cropped + resized image
        ctx.drawImage(img, sx, sy, side, side, 0, 0, 256, 256);

        // Convert to base64 PNG
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
