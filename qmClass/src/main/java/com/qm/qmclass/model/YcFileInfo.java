package com.qm.qmclass.model;

/**
 * Created by lz on 2020/11/12.
 */
public class YcFileInfo {
    private int id;
    private int teacherId;
    private int courseId;
    private int appId;
    private String title;
    private String sourceUrl;
    private String txUrl;
    private int pageNum;
    private String txResolution;
    private String txThumPre;
    private String txStatus;
    private String txTaskId;
    private String createTime;
    private String taskId;
    private String fileType;
    private boolean isAdd=false;
    private String fileId;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getTxUrl() {
        return txUrl;
    }

    public void setTxUrl(String txUrl) {
        this.txUrl = txUrl;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getTxResolution() {
        return txResolution;
    }

    public void setTxResolution(String txResolution) {
        this.txResolution = txResolution;
    }

    public String getTxThumPre() {
        return txThumPre;
    }

    public void setTxThumPre(String txThumPre) {
        this.txThumPre = txThumPre;
    }

    public String getTxStatus() {
        return txStatus;
    }

    public void setTxStatus(String txStatus) {
        this.txStatus = txStatus;
    }

    public String getTxTaskId() {
        return txTaskId;
    }

    public void setTxTaskId(String txTaskId) {
        this.txTaskId = txTaskId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
