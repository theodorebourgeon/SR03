<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Ajouter questionnaire</title>
	<link rel="stylesheet" href="./mystyle.css" media="screen" />
</head>
<body>
	<jsp:include page="../WEB-INF/menu.jsp"/>
	<div id="content">
		<h1>Ajouter un questionnaire</h1>
		
		<s:form action="creer_questionnaire" class="form" labelposition="top"> 
			<tr>
				<td class="right"><a href="creer_theme">Ajouter un thème</a></td>
			</tr>
			<s:select label="Thème"
			   name="survey.themeId"
			   listKey="id"
			   listValue="title"
			   list="%{themes}"
			/>
			<s:textfield name="survey.subject" label="Sujet"/>
			
			<s:submit value="Ajouter ce questionnaire" />
		</s:form>
	</div>
</body>
</html>