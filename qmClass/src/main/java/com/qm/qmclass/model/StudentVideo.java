package com.qm.qmclass.model;

import android.view.View;

/**
 * Created by lz on 2020/11/12.
 */
public class StudentVideo {
    private String name;
    private String studentId;
    private View view;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
