<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Questionnaire</title>
	<link rel="stylesheet" href="./mystyle.css" media="screen" />
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>
	<c:if test="${not empty filtre}"><c:set var="filterurl" scope="page" value="filtre=${filtre}&"/></c:if>
	<jsp:include page="../WEB-INF/menu.jsp"/>
	<div id="content">
		<h1>Liste des questionnaires</h1>
		<table class="liste">
			<tr>
				<td colspan=3>
					<s:form action="questionnaires" method="get" class="search" theme="simple" >
						<s:textfield name="filtre" placeholder="Rechercher..." />
						<s:submit type="button">
							<i class="fa fa-search"></i>
						</s:submit>
					</s:form>
				</td>
				<td style="width: 50pt;"></td>
				<td>
					<c:if test="${sessionScope.user_admin == true}">
						<a href="creer_questionnaire"><button type="button">Ajouter un questionnaire</button></a>
					</c:if>
				</td>
			</tr>
			<tr>
				<th style="min-width:150pt;" >Quizz</th>
				<th style="min-width:75pt;" >Theme</th>
				<th>Nombre de tentative</th>
			</tr>
			<tr><td>
			<s:iterator var="index" begin="0" end="count -1">
				<c:if test="${sessionScope.user_admin == true or surveys[index].status == 'ON'}">
					<tr>
						<td>
							<c:if test="${sessionScope.user_admin == true}">
								<c:choose>
									<c:when test="${surveys[index].status == 'ON'}">
										<i class="fa fa-check green"></i>
									</c:when>
									<c:otherwise>
										<i class="fa fa-times red"></i>
									</c:otherwise>
								</c:choose> 
							</c:if>
							<s:property value="surveys[#index].subject" /></td>
						<td><s:property value="themes[#index].title"/></td>
						<td>
							<c:if test="${sessionScope.user_admin == true}">
								${NbSurveyAdminList[index]}
							</c:if>
							<c:if test="${sessionScope.user_admin == false}">
								${NbSurveyUserList[index]}
							</c:if>
						</td>
						<td class="center">	
							<c:if test="${sessionScope.user_admin == true}">
								<a href="questionnaire?id=${surveys[index].id}"><i class="fa fa-eye"></i></a> &nbsp;
							</c:if>
							<a href="tentative?questionnaire=${surveys[index].id}"><i class="fa fa-play"></i></a> 
						</td>
					</tr>
				</c:if>
			</s:iterator>
			<tr>
				<td colspan=5>
					Pages : 
					<c:forEach begin="1" end="${nbPages}" varStatus="p">
						<c:if test="${p.index != 1}"> - </c:if>
						<c:if test="${p.index == page}"><b></c:if>
					    <a href="questionnaires?${urlfilter}page=${p.index}">${p.index}</a>
						<c:if test="${p.index == page}"></b></c:if>
					</c:forEach> <br/>
					<c:if test="${1 != page}"><a href="questionnaires?${filterurl}page=${page - 1}">« précédent</a> -</c:if> 
					<c:if test="${nbPages != page}"><a href="questionnaires?${filterurl}page=${page + 1}">suivant »</a></c:if>
				</td>
			</tr>
		</table>
	</div>
</body>
</html>
