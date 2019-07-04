<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Questions</title>
	<link rel="stylesheet" href="./mystyle.css" media="screen" />
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>
	<jsp:include page="../WEB-INF/menu.jsp"/>
	<div id="content">	
		<h1>Liste des questions</h1>
		<table class="liste">
			<tr>
				<td colspan=3>
					<s:form action="questions" method="get" class="search" theme="simple" >
						<c:if test="${not empty param.questionnaire}">
							<s:set var="qid" scope="page ">${param.questionnaire}</s:set>
							<s:hidden name="questionnaire" value="%{#qid}"/>
						</c:if>
						<s:textfield name="filtre" placeholder="Rechercher..." />
						<s:submit type="button">
							<i class="fa fa-search"></i>
						</s:submit>
					</s:form>
				</td>
			</tr>
			<tr>
				<th style="min-width:150pt;" >Titre</th>
				<th>Nb</th>
			</tr>
			<tr><td>
			<s:iterator var="index" begin="0" end="count -1">
				<tr>
					<td><s:property value="questions[#index].content"/></td>
					<td>NA</td>
					<td class="center">	
						<c:choose>
							<c:when test="${not empty param.questionnaire}">
								<a href="ajouter_question_existente?question=${questions[index].id}&questionnaire=${qid}">
									<i class="fa fa-check"></i>
								</a> 
							</c:when>
							<c:otherwise>
								<a href="question?id=${questions[index].id}"><i class="fa fa-eye"></i></a> 
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</s:iterator>
		</table>
	</div>
</body>
</html>
