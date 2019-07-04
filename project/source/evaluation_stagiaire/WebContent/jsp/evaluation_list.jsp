<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Évaluations</title>
	<link rel="stylesheet" href="./mystyle.css" media="screen" />
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>
	<c:set var="urlfilter"><c:if test="${not empty param.id}">id=${param.id}&</c:if><c:if test="${not empty param.quest}">quest=${param.quest}&</c:if></c:set>
	<c:if test="${not empty id}"><c:set var="filterurl" scope="page" value="filtre=${filtre}&"/></c:if>
	<jsp:include page="../WEB-INF/menu.jsp"/>
		<div id="content">
			<div style="float: right">
			<c:if test="${userIdToProcess != null}">
					<a href="profil?id=${userIdToProcess}"><button type="button" style="width:100pt;">Profile</button></a> &nbsp &nbsp 
			</c:if>	
			<c:if test="${sessionScope.user_admin == true and quest != null}">
					<a href="questionnaire?id=${quest}"><button type="button" style="width:100pt;">Questionnaire</button></a>
			</c:if>
		</div>
		<h1>
			Liste des évaluations 
			<c:if test="${quest != null}">
				: ${questName}
			</c:if>
			<c:if test="${userIdToProcess != null}">
				 de ${idName}
			</c:if>
		</h1>
		
		<br/><br/>
		<table class="liste">
			<tr>
				<c:if test="${userIdToProcess == null}">
					<th>Stagiaire</th>
				</c:if>
				<th style="min-width:150pt;" >Quizz</th>
				<th style="min-width:75pt;" >Theme</th>
				<th>Score</th>
				<th>Temps</th>
				<th class="minwidth">Action</th>
			</tr>
			<tr><td>
			<s:iterator value="evaluations" var="ev">
				<tr>
					<c:if test="${userIdToProcess == null}">
						<td><s:property value="usersMap[#ev.personId]"/></td>
					</c:if>
					<td><s:property value="surveysMap[#ev.surveyId]" />
					<td><s:property value="themesMap[#ev.surveyId]"/></td>
					<td><s:property value="#ev.score"/></td>
					<td><s:property value="#ev.duration"/></td>
					<td class="minwidth center"><a href="evaluation?id=${ev.id}"><i class="fa fa-eye"></i></a></td>
				</tr>
			</s:iterator>
			<tr>
				<td colspan=5>
					Pages : 
					<c:forEach begin="1" end="${nbPages}" varStatus="p">
						<c:if test="${p.index != 1}"> - </c:if>
						<c:if test="${p.index == page}"><b></c:if>
					    <a href="evaluations?${urlfilter}page=${p.index}">${p.index}</a>
						<c:if test="${p.index == page}"></b></c:if>
					</c:forEach> <br/>
					<c:if test="${1 != page}"><a href="evaluations?${urlfilter}page=${page - 1}">« précédent</a> -</c:if> 
					<c:if test="${nbPages != page}"><a href="evaluations?${urlfilter}page=${page + 1}">suivant »</a></c:if>
				</td>
			</tr>
		</table>
	</div>
</body>
</html>
