<%@taglib prefix="ckeditor" uri="http://www.siberhus.com/taglibs/ckeditor"%>

<html>
	<head>
		<ckeditor:resources minified="true"/>
	</head>
	<body>
		
		<%
			/*
				userId is the name of the parameter that this plugin use
				to create directory in order to separate the folder for each user.
				So, other user will not see the image of another user.
				If you want they share the same directory, you just don't set the
				same value for userId or just don't set it.
				userId parameter isn't hardcode value. You can change it in ckeditor.properties
			*/
			session.setAttribute("userId", "2");
			
			request.setAttribute("name", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.");
		%>
		<!-- 
			Default setting can be defined in config tag.
			You can override the default in editor tag.
		 -->
		<ckeditor:config height="300px" width="50%" toolbar="Basic"></ckeditor:config>
		
		<form action="${pageContext.request.contextPath}/test/foo1" method="post">
			<ckeditor:editor id="myeditor" name="myeditor" height="400px" width="80%" toolbar="Full" >
			${name}
			</ckeditor:editor>
			<input type="submit" name="submit" value="submit"/>
		</form>
		
	</body>
</html>