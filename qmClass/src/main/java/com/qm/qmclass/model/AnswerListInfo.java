package com.qm.qmclass.model;

/**
 * Created by lz on 2020/11/12.
 */
public class AnswerListInfo {
    private int studentId;
    private String avatarUrl;
    private String nickName;
    private int answerDuration;
    private String studentAnswer;
    private int answerFlag;

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

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

    public int getAnswerDuration() {
        return answerDuration;
    }

    public void setAnswerDuration(int answerDuration) {
        this.answerDuration = answerDuration;
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public int getAnswerFlag() {
        return answerFlag;
    }

    public void setAnswerFlag(int answerFlag) {
        this.answerFlag = answerFlag;
    }
}
