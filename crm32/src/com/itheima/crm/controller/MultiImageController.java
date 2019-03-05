package com.itheima.crm.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sun.misc.BASE64Decoder;
@Controller
@RequestMapping("/multiImage")
public class MultiImageController {
	//list存储接收到的图片路径
	private List<String> list=new ArrayList<>();
	//i记录每次请求所图片生成的数量
	private int i=0;
	//把list中存储到接受图片的地址存放到str数组中，传递给testface函数进行测试
	private String[] str=new String[5];
	//存放每个图片的原始名称，先存入listName,再存入strName中
	private List<String> listName=new ArrayList<>();
	private String[] strName=new String[5];
	//原来打算存放testface函数处理结果的
	//private String[] testResult=new String[2];
	//查看系统运行时间
	private Date datetimeOne,datetimeTwo;
	//存储每张人脸的编号以及辅助计算人脸识别的特征参数
	private HashMap<String,Integer> countMap=new HashMap<>();
 	private HashMap<String, Float> map=new HashMap<>();
 	//存人脸编号的
 	private List<String> keyList=new ArrayList<>();
 	//存储人脸返回编号，以及返回判别值
 	private String facekey=null;
 	private float  facevalue=0.000f; 
 	//读取record.txt（文件中存储的是陌生脸的facekey）,number来接收这个值
 	private String number=null;
 	//进入上传人脸页面
    @RequestMapping(value="/face")
	public String face(){
    	return "face";
    }
    //接收前端发送图片的请求
    @RequestMapping("/sendBase")
	public @ResponseBody String sendBase(String bases,String name){
     	if(i==0){
     		datetimeOne=new Date(System.currentTimeMillis());
     	}
		//System.out.println("bases:"+bases);
//		String before = bases.substring(bases.indexOf("/") + 1, bases.indexOf(";"));
//		System.out.println("before:"+before);
		//System.out.println("bases:====== "+bases);
		String substring = bases.substring(bases.indexOf(",")+1);
     	//System.out.println("sub:"+substring);
		
		boolean generateImage = GenerateImage(substring,name);
		//每生成一张图片，i的值加1
		++i;
		/*if(generateImage == true){
			model.addAttribute("success", true);
			
		}else{
			model.addAttribute("success", false);
		}*/
		return "face";
	}
	//base64字符串转化成图片  
    public  boolean GenerateImage(String imgStr,String name) {   
    	//将前端传过来的Base64解码并生成图片  
    	//判断图像数据为空
        if (imgStr == null) return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try{
            //Base64解码  
            byte[] b = decoder.decodeBuffer(imgStr);  
            for(int i=0;i<b.length;++i){  
                if(b[i]<0){
                    b[i]+=256;  
                }
            }
            //生成图片
            String imgFilePath = "E:\\作业\\实训\\minglei\\照片\\temp\\"+name; 
            //把生成的图片路径存入list中
            list.add(imgFilePath);
            //把图片原始名称存入listName中
            listName.add(name);
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            
            out.flush();
            out.close();
            while(i==4){
            	showPath();
            	//调用人脸特征测试函数
            	testface(str);
            	//把基数图片张数的值恢复成零
            	i=0;
            	System.out.println(facekey+":"+facevalue);
            	//获取系统运行时间
            	datetimeTwo=new Date(System.currentTimeMillis());
            	long runTime=datetimeTwo.getTime()-datetimeOne.getTime();
            	System.out.println("运行时间： "+runTime);
            	//对人脸测试的结果进行下一步操作
            	if((facevalue-0.006f)>0.000f){
            		//case1:facevalue>0.007说明是熟脸，把图片存入facekey所对应的文件夹
            		//设置转存途径
            		String turnStorage ="E:\\作业\\实训\\minglei\\照片\\temp\\picture\\"+facekey+"\\";
            		//五张图片转存
            		for(int i=0;i<5;i++){
            			//传入要存储的目录，原路径名，图片名；
            			imageoperation(turnStorage,str[i],strName[i]);
            		}
            	}else{
            		//case2:facevalue<0.007说明是生脸，1.读一个本地文件face_id（facekey）从500开始，每读取一次，facekey增加1存入本地文件中，接着返回face_id，
            		//2.新建文件夹（|facekey命名）3.把图片存入新的文件夹（face_id |facekey）
            		//读取record.txt文件
            		facekey=readFile();
            		//设置转存途径
            		String turnStorage ="E:\\作业\\实训\\minglei\\照片\\temp\\picture\\"+facekey+"\\";
            		//五张图片转存
            		for(int i=0;i<5;i++){
            			//传入要存储的目录，原路径名，图片名；
            			imageoperation(turnStorage,str[i],strName[i]);
            		}
            		//存完之后写入
            		writeFile(facekey);
            	}
            	
            }
            return true;
        }catch (Exception e){
            //e.printStackTrace();
            return false;
        }
    }
    //把list中存储到接受图片的地址存放到str数组中，传递给testface函数进行测试
    public void showPath(){
    	//System.out.println("list.size:"+list.size());
    	for(int i=0;i<5;i++){
    		str[i]=list.get(i);
    		strName[i]=listName.get(i);
    	}
    	}
    //校验人脸,把返回值String改成了void,strr字符串数组是保存图片的路径
    public void testface(String[] strr) throws IOException{
        String exe = "python";
        //python鐩稿叧鏍圭洰褰�
        String rootpath="E:\\作业\\实训\\minglei\\facenet2\\";
        //涓诲嚱鏁�
        String command = rootpath+"predict.py";
        //鏀逛负鍥剧墖璺緞
        String pic1 = strr[0];//rootpath+"huge160.png";
        String pic2 = strr[1];//rootpath+"huge160.png";
        String pic3 = strr[2];//rootpath+"huge160.png";
        String pic4 = strr[3];//rootpath+"huge160.png";
        String pic5 = strr[4];//rootpath+"huge160.png";
       
        String arg1 = rootpath+"20190221-021555";
        String arg2 = rootpath+"20190221-021555.pkl";
        String[] cmdArr = new String[] {exe, command, pic1,pic2,pic3,pic4,pic5,arg1,arg2};
        Process process = Runtime.getRuntime().exec(cmdArr);
//      process.waitFor();
        InputStream is = process.getInputStream();
        DataInputStream dis = new DataInputStream(is);
        
        String line="";
        while((line=dis.readLine())!=null) {
        	//暂时存储人脸特征
        	String[] ss=line.split(": ");
        	String key=ss[0];
        	float value=Float.parseFloat(ss[1]);
        	if(map.containsKey(key)){
        		int count=countMap.get(key);
        		value+=map.get(key);
        		map.put(key, value);
        		countMap.put(key, count+1);
        		if(!keyList.contains(key)){
        			keyList.add(key);
        		}
        	}else{
        		map.put(key, value);
        		countMap.put(key, 1);
        		keyList.add(key);
        	}
        }
        solveMap();
        //return line;
    }
    //对map,以及countmap计算平均值结果进行处理
    public void  solveMap(){
    	//countMap:1=0:5,size=1;
    	if(countMap.size()==1){
    		//只要返回一个即可
    		 facekey=keyList.get(0);
    	     facevalue=(map.get(facekey))/5.000f;
    		 
    	}
    	//countMap中只存放两种；
    	if(countMap.size()==2){
    		//countMap:1=1:4;;2=2:3
    		String[] facekeylist=new String[2];
    		facekeylist[0]=keyList.get(0);
    		facekeylist[1]=keyList.get(1);
    		if((countMap.get(facekeylist[0])==1)||(countMap.get(facekeylist[0])==4)){
    			//1：4
    			if((countMap.get(facekeylist[0])==1)){
    				facekey=facekeylist[1];
    				facevalue=(map.get(facekey))/4.000f;
    				
    			}else{
    				facekey=facekeylist[0];
    				facevalue=(map.get(facekey))/4.000f;
    			}
    			
    		}else {
    			//2:3
    			if((countMap.get(facekeylist[0])==2)){
    				facekey=facekeylist[1];
    				facevalue=(map.get(facekey))/3.000f;
    			}else{
    				facekey=facekeylist[0];
    				facevalue=(map.get(facekey))/3.000f;
    			}
    			
    		}
    	}
    	//countMap中存放了五个1：1：1：1：1
    	/*if(countMap.size()==5){
    		String [] facekeylist=new String[5];
    		facekeylist[0]=keyList.get(0);
    		facekeylist[1]=keyList.get(1);
    		facekeylist[2]=keyList.get(2);
    		facekeylist[3]=keyList.get(3);
    		facekeylist[4]=keyList.get(4);
    		float[] facevaluelist=new float[5];
    		for(int i=0;i<5;i++){
    			facevaluelist[i]=map.remove(facekeylist[i]);
    		}
    		int maxindex=findMax(facevaluelist);
    		facekey=facekeylist[maxindex];
    		facevalue=facevaluelist[maxindex];
    		
    	}*/
    	
    }
    //在facevaluelist中查找最大人脸特征参数，并返回最大值下标
    public int findMax(float[] num){
 	   int index=0;
 	   float max=num[0];
 	   for(int i=1;i<num.length;i++){
 		   if(max-num[i]<0.001f){
 			   max=num[i];
 			   index=i;
 		   }
 	   }
 	   return index;
    }
    //读取图片，转储图片,turnStorage是目的路径，sourcePath是原始路径,sourceName是图片原始名称
    public void imageoperation(String turnStorage,String sourcePath,String sourceName){
    	//父目录不存在，就创建父目录fileOne
    	File  fileOne =new File(turnStorage);
		if(!fileOne.exists()){
			fileOne.mkdirs();
		}
        //File originalFile = new File("C:\\Users\\edu333\\Desktop\\彭浩西(1-5).jpg");
		File originalFile = new File(sourcePath);//指定要读取的图片
        try {
           // File result = new File("E:\\作业\\实训\\minglei\\照片\\test\\彭浩西(1-5).jpg");//要写入的图片
        	File result = new File(turnStorage+sourceName);//要写入的图片
            if (result.exists()) {//校验该文件是否已存在
                //result.delete();//删除对应的文件，从磁盘中删除
               // result = new File("E:\\作业\\实训\\minglei\\照片\\test\\彭浩西(1-5).jpg");//只是创建了一个File对象，并没有在磁盘下创建文件
            	result = new File(turnStorage+sourceName);//只是创建了一个File对象，并没有在磁盘下创建文件
            }
            if (!result.exists()) {//如果文件不存在
                result.createNewFile();//会在磁盘下创建文件，但此时大小为0K
            }
            FileInputStream in = new FileInputStream(originalFile);
            FileOutputStream out = new FileOutputStream(result);// 指定要写入的图片
            int n = 0;// 每次读取的字节长度
            byte[] bb = new byte[1024];// 存储每次读取的内容
            while ((n = in.read(bb)) != -1) {
                out.write(bb, 0, n);// 将读取的内容，写入到输出流当中
            }
            //执行完以上后，磁盘下的该文件才完整，大小是实际大小
            out.close();// 关闭输入输出流
            in.close();
            originalFile.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
    
    
    /**
     * 读入TXT文件
     */
    public String readFile() {
        String pathname = "E:\\作业\\实训\\minglei\\照片\\temp\\record.txt"; // 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        try (FileReader reader = new FileReader(pathname);
             BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {
            String localline="";
            //网友推荐更加简洁的写法
            while ((localline = br.readLine()) != null) {
                // 一次读入一行数据
                number=localline;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return number;
    }

    /**
     * 写入TXT文件
     */
    public  void writeFile(String number) {
        try {
        	Integer StrangeFaceKey=Integer.parseInt(number);
        	++StrangeFaceKey;
            File writeName = new File("E:\\作业\\实训\\minglei\\照片\\temp\\record.txt"); // 相对路径，如果没有则要建立一个新的output.txt文件
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(StrangeFaceKey+"\r\n"); // \r\n即为换行
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
       // System.out.println("插入成功");
    }
    
    
    
    }
    

