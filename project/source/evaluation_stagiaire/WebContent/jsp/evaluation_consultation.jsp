<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Consulter évaluation</title>
	<link rel="stylesheet" href="./mystyle.css" media="screen" />
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>
	<jsp:include page="../WEB-INF/menu.jsp"/>
	<div id="content">
		<h1>Consulter une évaluation:</h1>
		
		<br/>
		<h2>Résumé:</h2>
		<table class="liste">
			<tr>
				<th class="minwidth">Stagiaire :</th>
				<td>${trainee}</td>
			</tr>
			<tr>
				<th class="minwidth">Sujet :</th>
				<td>${title}</td>
			</tr>
			<tr>
				<th class="minwidth">Theme :</th>
				<td>${theme}</td>
			</tr>
			<tr>
				<th class="minwidth">Score :</th>
				<td>${evaluation.score * 100} %</td>
			</tr>
			<tr>
				<th class="minwidth">Durée :</th>
				<td>${evaluation.duration}</td>
			</tr>
		</table>
		
		<br/>
		<h2>Questions:</h2>
		<table class="liste"> 			
			<s:iterator value="questions" var="q">
				<tr>
					<th colspan=2><s:property value="#q.content"/></th>
				</tr>
				<s:iterator value="#q.answers" var="a">
					<tr>
						<c:choose>
							<c:when test="${q.idRightAnswer == a.answerId && choicesMap[q.id] == a.answerId}">
								<td class="minwidth"><i class="fa fa-check green"></i></td>
								<td class="bggreen"><s:property value="answersMap[#a.answerId]"/></td>
							</c:when>
							<c:when test="${q.idRightAnswer == a.answerId}">
								<td class="minwidth"></td>
								<td class="bggreen"><s:property value="answersMap[#a.answerId]"/></td>
							</c:when>
							<c:when test="${choicesMap[q.id] == a.answerId}">
								<td class="minwidth"><i class="fa fa-times red"></i></td>
								<td class="bgred"><s:property value="answersMap[#a.answerId]"/></td>
							</c:when>
							<c:otherwise>
								<td class="minwidth"></td>
								<td><s:property value="answersMap[#a.answerId]"/></td>
							</c:otherwise>
						</c:choose> 
					</tr>
				</s:iterator>
				<tr><td colspan=2><br/></td></tr>
			</s:iterator>
		</table>	
	</div>
</body>
</html>