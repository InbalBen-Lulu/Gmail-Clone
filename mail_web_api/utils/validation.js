/**
 * Checks if the provided string is a valid 6-digit hex color (e.g., #AABBCC).
 * Returns:
 *   - true if valid hex color
 *   - false otherwise
 */
function isValidHexColor(color) {
    const hexRegex = /^#[0-9A-Fa-f]{6}$/;
    return hexRegex.test(color);
}

module.exports = {
    isValidHexColor
};