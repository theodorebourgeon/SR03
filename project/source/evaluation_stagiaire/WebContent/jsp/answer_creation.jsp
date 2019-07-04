<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %> 
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Ajouter une réponse</title>
	<link rel="stylesheet" href="./mystyle.css" media="screen" />
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>
	<jsp:include page="../WEB-INF/menu.jsp"/>
	<s:set var="hidden_id" scope="page ">${param.id}</s:set>
	<div id="content">
		<h1>Ajouter une réponse</h1>
		<s:form action="ajouter_reponse?id=%{#hidden_id}" class="form" labelposition="top">
			<s:textfield name="content" label="Réponse"/>
			<s:submit value="Ajouter cette réponse"/>
		</s:form>
	</div>
</body>
</html>
