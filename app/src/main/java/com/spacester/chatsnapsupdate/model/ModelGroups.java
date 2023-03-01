package com.spacester.chatsnapsupdate.model;

public class ModelGroups {
    String groupId,gName,gIcon,timestamp,createdBy;

    public ModelGroups() {
    }

    public ModelGroups(String groupId, String gName, String gIcon, String timestamp, String createdBy) {
        this.groupId = groupId;
        this.gName = gName;
        this.gIcon = gIcon;
        this.timestamp = timestamp;
        this.createdBy = createdBy;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public String getgIcon() {
        return gIcon;
    }

    public void setgIcon(String gIcon) {
        this.gIcon = gIcon;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
