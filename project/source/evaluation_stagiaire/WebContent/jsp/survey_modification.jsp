<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Modifier questionnaire</title>
	<link rel="stylesheet" href="./mystyle.css" media="screen" />
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>
	<jsp:include page="../WEB-INF/menu.jsp"/>
	<div id="content">
		<div style="float: right">
			<a href="evaluations?quest=${param.id}"><button type="button" style="width:100pt;">Évaluations</button></a>
		</div>
		<h1>Modifier un questionnaire</h1>
		
		<s:form action="questionnaire?id=%{survey.id}" class="form" labelposition="top"> 
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
			<s:radio label="Actif" name="survey.status" list="@model.Status@values()" class="switch"/>
			
			<s:submit value="Enregistrer" />
			
			<tr><td>&nbsp;</td></tr>
			<tr><td><h2>
				Questions :
				<a href="ajouter_question?id=${survey.id}" style="float: right;">
					<i class="fa fa-plus green"></i> Nouvelle
				</a>
				<span style="float:right;">&nbsp; &nbsp; &nbsp;</span>
				<a href="questions?questionnaire=${survey.id}" style="float: right;">
					<i class="fa fa-plus green"></i> Existante 
				</a>
			</h2></td></tr>
			
			<s:iterator value="survey.questions" var="q">
				<tr>
					<td>
					<div style="float: right;">
						<a href="question?id=${q.questionId}" ><i class="fa fa-eye"></i></a> 
						<a href="monter_question?questionnaire=${survey.id}&question=${q.order}"><i class="fa fa-chevron-up"></i></a> 
						<a href="descendre_question?questionnaire=${survey.id}&question=${q.order}"><i class="fa fa-chevron-down"></i></a> 
					</div>
					<a href="status_question?questionnaire=${survey.id}&question=${q.order}">
					<c:choose>
						<c:when test="${q.status == 'ON'}">
							<i class="fa fa-check green"></i>
						</c:when>
						<c:otherwise>
							<i class="fa fa-times red"></i> 
						</c:otherwise>
					</c:choose>
					</a>
					<c:out value="${questions[q.order].content}"/>
					</td>
				</tr>
			</s:iterator>
		</s:form>
	</div>
</body>
</html>