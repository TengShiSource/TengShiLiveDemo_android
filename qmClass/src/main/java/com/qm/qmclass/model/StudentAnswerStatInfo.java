package com.qm.qmclass.model;

import java.util.List;

/**
 * Created by lz on 2020/11/12.
 */
public class StudentAnswerStatInfo {
    private int questionType;
    private String questionOptions;
    private String questionAnswer;
    private List<AnswerStat> answerStat;
    private int answerCount;
    private String totalCount;
    private String correctPercent;

    public int getQuestionType() {
        return questionType;
    }

    public void setQuestionType(int questionType) {
        this.questionType = questionType;
    }

    public String getQuestionOptions() {
        return questionOptions;
    }

    public void setQuestionOptions(String questionOptions) {
        this.questionOptions = questionOptions;
    }

    public String getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(String questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    public List<AnswerStat> getAnswerStat() {
        return answerStat;
    }

    public void setAnswerStat(List<AnswerStat> answerStat) {
        this.answerStat = answerStat;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getCorrectPercent() {
        return correctPercent;
    }

    public void setCorrectPercent(String correctPercent) {
        this.correctPercent = correctPercent;
    }

    public class AnswerStat{
        private String option;
        private int studentNum;

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }

        public int getStudentNum() {
            return studentNum;
        }

        public void setStudentNum(int studentNum) {
            this.studentNum = studentNum;
        }
    }

}
