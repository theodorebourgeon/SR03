<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %> 
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Inscription</title>
	<link rel="stylesheet" href="./mystyle.css" media="screen" />
</head>
<body>
	<jsp:include page="../WEB-INF/menu.jsp"/>
	<div id="content">
		<h1>Inscription</h1>
		<s:form action="creer_utilisateur" class="form" labelposition="top"> 
			<tr><td><h2>Obligatoire</h2></td></tr>
			<s:textfield name="user.mail" label="Email"/>
			<s:textfield name="user.name" label="Nom"/>
			<s:password name="user.pwd" label="Mot de passe"/>	
	
			<tr><td><h2>Facultatif</h2></td></tr>
			<s:textfield name="user.corporation" label="Société"/>
			<s:textfield name="user.phone" label="Téléphone"/>
	
				
         	<tr><td><h2>Temp</h2></td></tr>
			<s:checkbox name="user.admin" label="Administrateur" />
			
			<s:submit value="Inscrire" />
		</s:form>
	</div>
</body>
</html>