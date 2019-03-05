package com.itheima.crm.mapper;

import java.util.List;

import com.itheima.crm.pojo.XiaoChengXu;

public interface XiaoChengXuDao {
   public void insertXiaoChengXu(XiaoChengXu xiaoChengXu) throws Exception;
   public List<XiaoChengXu> selectXiaoChengXu()throws Exception;
}
