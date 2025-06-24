/**
 * Formats a date like Gmail:
 * - If today: show "HH:mm"
 * - If this year: show "D Mon" (e.g., "4 Jul")
 * - Else: show "D Mon YYYY" (e.g., "4 Jul 2023")
 * @param {string|Date} dateInput - ISO string or Date object
 * @returns {string} formatted string
 */
export function formatMailDate(dateInput) {
  const date = new Date(dateInput);
  const now = new Date();

  const isToday =
    date.getDate() === now.getDate() &&
    date.getMonth() === now.getMonth() &&
    date.getFullYear() === now.getFullYear();

  if (isToday) {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  const isSameYear = date.getFullYear() === now.getFullYear();
  const day = date.getDate();
  const monthShort = date.toLocaleString('en-US', { month: 'short' }); // e.g., "Jul"

  if (isSameYear) {
    return `${day} ${monthShort}`;
  }

  const year = date.getFullYear();
  return `${day} ${monthShort} ${year}`;
}
