package com.qm.qmclass.model;

import java.util.Date;

/**
 * Created by lz on 2020/11/12.
 */
public class YcFileInfo {
    private int teacherId;
    private int courseId;
    private String title;
    private String txUrl;
    private int pageNum;
    private String txResolution;
    private String txThumPre;
    private String txStatus;
    private Date createTime;
    private String fileType;
    private boolean isAdd=false;
    private String fileId;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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
