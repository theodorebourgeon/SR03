<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %> 
    
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Connexion</title>
	<link rel="stylesheet" href="./mystyle.css" media="screen" />
</head>
<body>
	<jsp:include page="../WEB-INF/menu.jsp"/>
	<div id="content">
		<h1>Connexion</h1>
		<s:form action="connexion" class="form" > 
			<s:textfield name="user.mail" label="Email"/>
			<s:password name="user.pwd" label="Mot de passe"/>	
			<s:submit value="Connexion" />
		</s:form>
	</div>
</body>
</html>