package com.example.campus_space_scheduler.csed_office;

public class KeyRecordModel {
    private String id;
    private String spaceId;
    private String spaceName;
    private String facultyName;
    private String contactNumber;
    private String status; // "HANDED OUT" or "RETURNED"
    private long handoutTime;
    private long returnTime;

    // Default constructor for Firebase Realtime Database
    public KeyRecordModel() {
    }

    public KeyRecordModel(String id, String spaceId, String spaceName, String facultyName, String contactNumber, String status, long handoutTime, long returnTime) {
        this.id = id;
        this.spaceId = spaceId;
        this.spaceName = spaceName;
        this.facultyName = facultyName;
        this.contactNumber = contactNumber;
        this.status = status;
        this.handoutTime = handoutTime;
        this.returnTime = returnTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getHandoutTime() {
        return handoutTime;
    }

    public void setHandoutTime(long handoutTime) {
        this.handoutTime = handoutTime;
    }

    public long getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(long returnTime) {
        this.returnTime = returnTime;
    }
}
