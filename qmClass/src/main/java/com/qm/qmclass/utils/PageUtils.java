package com.qm.qmclass.utils;

import com.qm.qmclass.base.LiveDataManager;
import com.qm.qmclass.model.StudentInfor;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页工具
 */
public class PageUtils {
    private static Integer pageCount; // 页数
    /**
     * 开始分页
     * @param list
     * @param pageNum 页码
     * @param pageSize 每页多少条数据
     * @return
     */
    public static List<StudentInfor> getPage(List<StudentInfor> list, Integer pageNum,
                                               Integer pageSize) {
        if (list == null) {
            return null;
        }
        if (list.size() == 0) {
            return null;
        }

        Integer count = list.size(); // 记录总数
        pageCount = 0; // 页数
        if (count % pageSize == 0) {
            pageCount = count / pageSize;
        } else {
            pageCount = count / pageSize + 1;
        }
        LiveDataManager.getInstance().setPageCount(pageCount);
        int fromIndex = 0; // 开始索引
        int toIndex = 0; // 结束索引

        if (pageNum != pageCount) {
            fromIndex = (pageNum - 1) * pageSize;
            toIndex = fromIndex + pageSize;
        } else {
            fromIndex = (pageNum - 1) * pageSize;
            toIndex = count;
        }

        List<StudentInfor> pageList = list.subList(fromIndex, toIndex);

        return pageList;
    }
    /**
     * 监控指示器个数
     * @return
     */
    public static List getDianList() {
        List dianList=new ArrayList();
        if (pageCount>0){
            for (int i=0;i<pageCount;i++){
                dianList.add(i);
            }
        }
        return dianList;
    }
}
