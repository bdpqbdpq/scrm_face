package com.itheima.crm.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itheima.crm.pojo.User;

@Controller
@RequestMapping(value ="/register")
public class RegisterController {
	@RequestMapping(value ="/one")
	public String  register( User formData){
		System.out.println(formData);
		String pictrue="二维码";
		return pictrue;
		
		
	}
}
