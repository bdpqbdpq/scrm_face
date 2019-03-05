package com.itheima.crm.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itheima.crm.mapper.XiaoChengXuDao;
import com.itheima.crm.pojo.XiaoChengXu;
@Service
public class XiaoChengXuServiceImpl implements XiaoChengXuService{
    
	@Autowired
	private XiaoChengXuDao xiaoChengXuDao;
	
	public void insertXiaoChengXu(XiaoChengXu xiaoChengXu) {
		try {
			xiaoChengXuDao.insertXiaoChengXu(xiaoChengXu);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public List<XiaoChengXu> selectXiaoChengXu() {
		List<XiaoChengXu>list =new ArrayList<>();
		try {
			list=xiaoChengXuDao.selectXiaoChengXu();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

}
