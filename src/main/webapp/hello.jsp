<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<form action="" id="loginUser" method="post">
    <div>
        <!--这里img标签的src属性的值为后台实现图片验证码方法的请求地址-->
        <label><img type="image" src="image-verification/generator.do" id="codeImage" title="图片看不清？点击重新得到验证码" style="cursor:pointer;"/></label>
    </div>
</form>
</body>
</html>