/**
 * Checks if a given day, month, and year represent a valid, non-future date.
 * Returns true if the date is valid and not in the future, otherwise false.
 */
export function isValidDate(day, monthValue, year) {
  const monthIndex = parseInt(monthValue, 10) - 1;
  const dayNum = parseInt(day, 10);
  const yearNum = parseInt(year, 10);

  if (
    isNaN(dayNum) || dayNum < 1 || dayNum > 31 ||
    isNaN(monthIndex) || monthIndex < 0 || monthIndex > 11 ||
    isNaN(yearNum) || yearNum < 1900
  ) {
    return false;
  }

  const date = new Date(yearNum, monthIndex, dayNum);

  const now = new Date();
  now.setHours(0, 0, 0, 0);

  const isSameDate =
    date.getFullYear() === yearNum &&
    date.getMonth() === monthIndex &&
    date.getDate() === dayNum;

  const isNotFuture = date < now;

  return isSameDate && isNotFuture;
}
