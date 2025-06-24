// Import all SVGs as React components
import { ReactComponent as AddPhoto } from "./add_a_photo.svg";
import { ReactComponent as ArrowBack } from "./arrow_back.svg";
import { ReactComponent as ArrowBack2 } from "./arrow_back_2.svg";
import { ReactComponent as ArrowForward } from "./arrow_forward.svg";
import { ReactComponent as Close } from "./close.svg";
import { ReactComponent as Delete } from "./delete.svg";
import { ReactComponent as Draft } from "./draft.svg";
import { ReactComponent as Edit } from "./edit.svg";
import { ReactComponent as ErrorIcon } from "./error.svg";
import { ReactComponent as Label } from "./label.svg";
import { ReactComponent as Logout } from "./logout.svg";
import { ReactComponent as Mail } from "./mail.svg";
import { ReactComponent as More } from "./more.svg";
import { ReactComponent as PhotoCamera } from "./photo_camera.svg";
import { ReactComponent as Spam } from "./spam.svg";
import { ReactComponent as Send } from "./send.svg";
import { ReactComponent as Settings } from "./settings.svg";
import { ReactComponent as AllMail } from "./all_mail.svg";
import { ReactComponent as Star } from "./star.svg";
import { ReactComponent as Check } from "./check.svg";
import { ReactComponent as Search } from "./search.svg";
import { ReactComponent as Inbox } from "./inbox.svg";
import { ReactComponent as Add } from "./add.svg";
import { ReactComponent as Report } from "./report.svg";
import { ReactComponent as Refresh } from "./refresh.svg";
import { ReactComponent as NotSpam } from "./not_spam.svg";
import { ReactComponent as OpenInFull } from "./open_in_full.svg";
import { ReactComponent as Remove } from "./remove.svg";

// Map of icon name → SVG component
const icons = {
  add_a_photo: AddPhoto,
  arrow_back: ArrowBack,
  arrow_back2: ArrowBack2,
  arrow_forward: ArrowForward,
  close: Close,
  delete: Delete,
  draft: Draft,
  edit: Edit,
  error: ErrorIcon,
  label: Label,
  logout: Logout,
  mail: Mail,
  more: More,
  photo_camera: PhotoCamera,
  Spam: Spam,
  send: Send,
  settings: Settings,
  all_mail: AllMail,
  star: Star,
  check: Check,
  search: Search,
  inbox: Inbox,
  add: Add,
  report: Report,
  refresh: Refresh,
  not_spam: NotSpam,
  open_in_full: OpenInFull,
  remove: Remove
};

/**
 * General-purpose icon component.
 * Props:
 * - name: string key from the icon map (e.g., "edit", "send")
 * - className: optional class for styling
 */
const Icon = ({ name, className = "" }) => {
  const SvgIcon = icons[name];
  if (!SvgIcon) return null;

  return (
    <SvgIcon
      fill="currentColor"
      className={`icon ${className}`.trim()}
    />
  );
};

export default Icon;
