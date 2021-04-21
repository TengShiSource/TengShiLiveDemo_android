package com.qm.qmclass.model;

import java.sql.Time;

/**
 * Created by lz on 2020/11/12.
 */
public class CourseInfo {
    private Course course;
    private Options options;
    private int txAppId;
    private LvbPush lvbPush;
    private Teacher teacher;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public int getTxAppId() {
        return txAppId;
    }

    public void setTxAppId(int txAppId) {
        this.txAppId = txAppId;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public LvbPush getLvbPush() {
        return lvbPush;
    }

    public void setLvbPush(LvbPush lvbPush) {
        this.lvbPush = lvbPush;
    }

    public class Course{
        private String searchValue;
        private String createBy;
        private String createTime;
        private String updateBy;
        private String updateTime;
        private String remark;
        private Params params;
        private int id;
        private long appId;
        private String courseName;
        private String startTime;
        private String endTime;
        private String courseOptions;
        private String teacherPwd;
        private String studentPwd;
        private String mobile;
        private String verifyCode;

        public String getSearchValue() {
            return searchValue;
        }

        public void setSearchValue(String searchValue) {
            this.searchValue = searchValue;
        }

        public String getCreateBy() {
            return createBy;
        }

        public void setCreateBy(String createBy) {
            this.createBy = createBy;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateBy() {
            return updateBy;
        }

        public void setUpdateBy(String updateBy) {
            this.updateBy = updateBy;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public Params getParams() {
            return params;
        }

        public void setParams(Params params) {
            this.params = params;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public long getAppId() {
            return appId;
        }

        public void setAppId(long appId) {
            this.appId = appId;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getCourseOptions() {
            return courseOptions;
        }

        public void setCourseOptions(String courseOptions) {
            this.courseOptions = courseOptions;
        }

        public String getTeacherPwd() {
            return teacherPwd;
        }

        public void setTeacherPwd(String teacherPwd) {
            this.teacherPwd = teacherPwd;
        }

        public String getStudentPwd() {
            return studentPwd;
        }

        public void setStudentPwd(String studentPwd) {
            this.studentPwd = studentPwd;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getVerifyCode() {
            return verifyCode;
        }

        public void setVerifyCode(String verifyCode) {
            this.verifyCode = verifyCode;
        }
    }
    public class Params{

    }
    public class LvbPush{
        private String licenceURL;
        private String licenceKey;

        public String getLicenceURL() {
            return licenceURL;
        }

        public void setLicenceURL(String licenceURL) {
            this.licenceURL = licenceURL;
        }

        public String getLicenceKey() {
            return licenceKey;
        }

        public void setLicenceKey(String licenceKey) {
            this.licenceKey = licenceKey;
        }
    }
    public class Options{
        private String teacherCreateClass;
        private String teacherTransferDirector;
        private String teacherMaintenanceClass;
        private String teacherManageMembers;
        private String teacherManageCourse;
        private int recMethod;
        private int courseMaxMembers;
        private String expValueLevels;

        public String getTeacherCreateClass() {
            return teacherCreateClass;
        }

        public void setTeacherCreateClass(String teacherCreateClass) {
            this.teacherCreateClass = teacherCreateClass;
        }

        public String getTeacherTransferDirector() {
            return teacherTransferDirector;
        }

        public void setTeacherTransferDirector(String teacherTransferDirector) {
            this.teacherTransferDirector = teacherTransferDirector;
        }

        public String getTeacherMaintenanceClass() {
            return teacherMaintenanceClass;
        }

        public void setTeacherMaintenanceClass(String teacherMaintenanceClass) {
            this.teacherMaintenanceClass = teacherMaintenanceClass;
        }

        public String getTeacherManageMembers() {
            return teacherManageMembers;
        }

        public void setTeacherManageMembers(String teacherManageMembers) {
            this.teacherManageMembers = teacherManageMembers;
        }

        public String getTeacherManageCourse() {
            return teacherManageCourse;
        }

        public void setTeacherManageCourse(String teacherManageCourse) {
            this.teacherManageCourse = teacherManageCourse;
        }

        public int getRecMethod() {
            return recMethod;
        }

        public void setRecMethod(int recMethod) {
            this.recMethod = recMethod;
        }

        public int getCourseMaxMembers() {
            return courseMaxMembers;
        }

        public void setCourseMaxMembers(int courseMaxMembers) {
            this.courseMaxMembers = courseMaxMembers;
        }

        public String getExpValueLevels() {
            return expValueLevels;
        }

        public void setExpValueLevels(String expValueLevels) {
            this.expValueLevels = expValueLevels;
        }
    }
    public class Teacher{
        private String avatarUrl;
        private String nickName;
        private int userId;
        private String userCode;

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }
    }
}
