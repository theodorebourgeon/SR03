<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Modifier question</title>
	<link rel="stylesheet" href="./mystyle.css" media="screen" />
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>
	<jsp:include page="../WEB-INF/menu.jsp"/>
	<div id="content">
		<h1>Modifier une question</h1>
		
		<s:form action="question?id=%{question.id}" class="form" labelposition="top"> 
			<s:textfield name="question.content" label="Intitulé"/>
			
			<s:submit value="Enregistrer" />
			
			<tr><td>&nbsp;</td></tr>
			<tr><td><h2>
				Réponses :
				<a href="ajouter_reponse?id=${question.id}" style="float: right;">
					<i class="fa fa-plus green"></i>
				</a>
			</h2></td></tr>
			
			<s:iterator value="question.answers" var="a">
				<tr>
					<td>
					<div style="float: right;">
						<a href="reponse?id=${a.answerId}&question=${question.id}" ><i class="fa fa-eye"></i></a> 
						<a href="monter_reponse?question=${question.id}&reponse=${a.order}"><i class="fa fa-chevron-up"></i></a> 
						<a href="descendre_reponse?question=${question.id}&reponse=${a.order}"><i class="fa fa-chevron-down"></i></a> 
					</div>
					
					<c:choose>
						<c:when test="${a.correct == true}">
							<i class="fa fa-check green"></i>
						</c:when>
						<c:when test="${a.status == 'ON'}">
							<a href="status_reponse?question=${question.id}&reponse=${a.order}"><i class="fa fa-check grey"></i></a>
						</c:when>
						<c:otherwise>
							<a href="status_reponse?question=${question.id}&reponse=${a.order}"><i class="fa fa-times grey"></i></a>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${a.correct == true}"><b class="green"></c:when>
						<c:when test="${a.status == 'OFF'}"><span class="strikethrough"></c:when>
						<c:otherwise><a href="bonne_reponse?question=${question.id}&reponse=${a.order}"></c:otherwise>
					</c:choose>
						<c:out value="${answers[a.order].content}"/>
					<c:choose>
						<c:when test="${a.correct == true}"></b></c:when>
						<c:when test="${a.status == 'OFF'}"></span></c:when>
						<c:otherwise></a></c:otherwise>
					</c:choose>
					</td>
				</tr>
			</s:iterator>
		</s:form>
	</div>
</body>
</html>