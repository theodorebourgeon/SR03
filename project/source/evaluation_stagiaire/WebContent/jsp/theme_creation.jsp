<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %> 
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Ajouter thème</title>
	<link rel="stylesheet" href="./mystyle.css" media="screen" />
</head>
<body>
	<jsp:include page="../WEB-INF/menu.jsp"/>
	<div id="content">
		<h1>Ajouter un thème</h1>
		<s:form action="creer_theme" class="form" labelposition="top" theme="xhtml">
			<s:textfield name="theme.title" label="Titre"/>
			<s:submit value="Ajouter ce thème"/>
		</s:form>
	</div>
</body>
</html>