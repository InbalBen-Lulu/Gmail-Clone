import React from "react";

// Import all SVGs as React components
import { ReactComponent as AddPhoto } from "./add_a_photo.svg";
import { ReactComponent as ArrowBack } from "./arrow_back.svg";
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
import { ReactComponent as Report } from "./report.svg";
import { ReactComponent as Send } from "./send.svg";
import { ReactComponent as Settings } from "./settings.svg";
import { ReactComponent as StackedEmail } from "./stacked_email.svg";
import { ReactComponent as Star } from "./star.svg";
import { ReactComponent as Check } from "./check.svg";
import { ReactComponent as Search } from "./search.svg";

// Map of icon name → SVG component
const icons = {
  add_a_photo: AddPhoto,
  arrow_back: ArrowBack,
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
  report: Report,
  send: Send,
  settings: Settings,
  stacked_email: StackedEmail,
  star: Star,
  check: Check,
  search: Search,
};

/**
 * General-purpose icon component.
 *
 * Props:
 * - name: string key from the icon map (e.g., "edit", "send")
 * - size: number in px (default 18)
 * - color: any CSS color (default "currentColor")
 * - className: optional class for styling
 */
const Icon = ({ name, size = 18, className = "" }) => {
  const SvgIcon = icons[name];
  if (!SvgIcon) return null;

  return (
    <SvgIcon
      width={size}
      height={size}
      fill="currentColor"
      stroke="currentColor"
      className={className}
    />
  );
};


export default Icon;
