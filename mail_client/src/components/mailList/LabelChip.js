const LabelChip = ({ name, color }) => {
  return (
    <span
      className="label-chip"
      style={{ backgroundColor: color || '#e0e0e0', color: '#202124' }}
      title={name}
    >
      {name}
    </span>
  );
};

export default LabelChip;
