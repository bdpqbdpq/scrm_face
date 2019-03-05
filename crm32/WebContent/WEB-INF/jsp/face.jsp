<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%  
   String path = request.getContextPath();
  String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<form id="up" enctype="multipart/form-data" method="post">
		<input type="file" onchange="change(this)" accept="image/gif,image/jpeg,image/png,image/jpg" id="upload" multiple="multiple">
		<input type="button" onclick="changeImg()" value="上传">
		<div id="image"></div>
</form>
  <h3>${success}</h3>
	<!-- jQuery -->
	<script src="<%=basePath%>js/jquery.min.js"></script>
<script type="text/javascript">
var imgFile = null;
//将图片进行编码后发送到后台
function getBase64Image(img) {
     var readImg = new FileReader();
     readImg.readAsDataURL(img);
     readImg.onload = function (readEvent){
    	var base64 = readEvent.target.result;
    	//console.log(base64);
    	$("#image").attr("src", base64);
    	$.ajax({
			  type:"post",
			  url:"<%=basePath%>multiImage/sendBase",
			  data:{
				  "bases":base64,
				  "name":img.name
				},
			  dataType:"json",
			  success:function(data){
				  //alert(data);
				  if(data == "1"){
				  	console.log("上传成功");
				  }else{
				  	console.log("上传失败");
				  }
			  }
		  });
     };
}
//循环选择的图片
function changeImg() {
	var read = new FileReader();
	var allFiles = imgFile.files;
	for(var i=0; i < allFiles.length; i++){
		var file = allFiles[i];
		getBase64Image(file);
	}
}
//当选择图片后，显示出图片
function change(img){
	imgFile = img;
	var allFiles = imgFile.files;
	for(var i=0; i < allFiles.length; i++){
		var img = allFiles[i];
		$("img").remove();
		var readImg = new FileReader();
	     readImg.readAsDataURL(img);
	     readImg.onload = function (readEvent){
	    	var base64 = readEvent.target.result;
	    	var appd =  "<img src=\""+base64+"\" width=\"200px\" height=\"200px\">";
	    	$("#image").append(appd);
	     };
	}
}
</script>
</body>
</html>