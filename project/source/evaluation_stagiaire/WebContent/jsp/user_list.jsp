<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Utilisateurs</title>
	<link rel="stylesheet" href="./mystyle.css" media="screen" />
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>
	<c:if test="${not empty filtre}"><c:set var="filterurl" scope="page" value="filtre=${filtre}&"/></c:if>
	<jsp:include page="../WEB-INF/menu.jsp"/>
	<div id="content">
		<h1>Liste des utilisateurs</h1>
		<table class="liste">
			<tr>
			<td colspan=3>
				<s:form action="utilisateurs" method="get" class="search" theme="simple" >
					<s:textfield name="filtre" placeholder="Rechercher..." />
					<s:submit type="button">
						<i class="fa fa-search"></i>
					</s:submit>
				</s:form>
			</td>
			<td style="width:50pt;"></td>
			<td><a href="creer_utilisateur"><button type="button">Ajouter un utilisateur</button></a></td>
			</tr>
			<tr>
				<th style="min-width:75pt;" >Nom</th>
				<th style="min-width:150pt;" >Email</th>
				<th>Type</th>
			</tr>
			<s:iterator value="users" var="user" >
				<tr>
					<td><s:property value="#user.name"/></td>
					<td><s:property value="#user.mail"/></td>
					<td>
						${user.admin == true ? 'Admin' : 'Stagiaire'} 
					</td>
					<td class="center">
						<c:choose>
							<c:when test="${user.status == 'ON'}">
								<i class="fa fa-check green"></i>
							</c:when>
							<c:otherwise>
								<i class="fa fa-times red"></i> 
							</c:otherwise>
						</c:choose> &nbsp;&nbsp;&nbsp;
						<a href="profil?id=${user.id}"><i class="fa fa-eye"></i></a> 
					</td>
				</tr>
			</s:iterator>
				<tr>
					<td colspan=5>
						Pages : 
						<c:forEach begin="1" end="${nbPages}" varStatus="p">
							<c:if test="${p.index != 1}"> - </c:if>
							<c:if test="${p.index == page}"><b></c:if>
						    <a href="utilisateurs?${filterurl}page=${p.index}">${p.index}</a>
							<c:if test="${p.index == page}"></b></c:if>
						</c:forEach> <br/>
					<c:if test="${1 != page}"><a href="utilisateurs?${filterurl}page=${page - 1}">« précédent</a> -</c:if> 
					<c:if test="${nbPages != page}"><a href="utilisateurs?${filterurl}page=${page + 1}">suivant »</a></c:if>
					</td>
				</tr>
		</table>
	</div>
</body>
</html>
