import React from "react";
import styles from "../css/GroupIconWithText.module.css";

function GroupIconWithText({ imgSrc, groupName }) {
  return (
    <div className={styles.group}>
      <div className={styles.avatar}>
        <img className={styles.groupIcon} alt="" src={imgSrc} />
      </div>
      <div className={styles.groupName}>{groupName}</div>
    </div>
  );
}

export default GroupIconWithText;
