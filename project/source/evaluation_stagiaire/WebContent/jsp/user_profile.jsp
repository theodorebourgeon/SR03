<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Profil</title>
	<link rel="stylesheet" href="./mystyle.css" media="screen" />
</head>
<script src="https://smtpjs.com/v3/smtp.js"></script>
<script type="text/javascript">
function sendMail(){
	var to = document.getElementById("user.mail").value
	var mdp = document.getElementById("user.pwd").value
	var body = "Voici les identifiants de votre compte SR03 evaluation_stagiaire : \n\n\t login : "+to+"\n\t mdp : "+mdp+" !"
	var alertmes = "Le mail a bien été envoyé à "+to+" !"
	Email.send({
	    SecureToken : "887278e4-4323-405d-aa2c-a69e3550a592",
	    To : to,
	    From : "contact@theodorebrgn.fr",
	    Subject : "Identifiants SR03",
	    Body : body,
	}).then(
	  (message) => {alert(alertmes)}
	);
}
</script>
<body>
	<s:set var="readonly">${empty param.edit}</s:set>
	<s:set var="edit">${not readonly}</s:set>
	<c:if test="${not empty param.id}">
		<s:set var="urlid">?id=${param.id}</s:set>
	</c:if>
	<s:set var="symb">${empty param.id ? "?" : "&"}</s:set>
	
	<jsp:include page="../WEB-INF/menu.jsp"/>
	<div id="content">
		<c:choose>
			<c:when test="${not empty param.id}">
				<div style="float: right">
					<a href="evaluations?id=${param.id}"><button type="button" style="width:100pt;">Évaluations</button></a>
				</div>
			</c:when>
			<c:otherwise>
				<div style="float: right">
					<a href="evaluations"><button type="button" style="width:100pt;">Évaluations</button></a>
				</div>
			</c:otherwise>
		</c:choose>
		<h1>Profil de <s:property value="user.name"/></h1>
				
		<s:form action="profil%{#urlid}" class="form" > 
			<s:textfield id="user.mail" name="user.mail" label="Email" required="true" readonly="true"/>
			
			<s:textfield name="user.name" label="Nom" required="true" readonly="%{#readonly}"/>
			<s:password  id="user.pwd" name="user.pwd" label="Mot de passe" required="true" showPassword="true" readonly="%{#readonly}"/>	
			<s:textfield name="user.corporation" label="Société" readonly="%{#readonly}"/>
			<s:textfield name="user.phone" label="Téléphone" readonly="%{#readonly}"/>

			<c:if test="${not empty sessionScope.user_admin && sessionScope.user_admin == true}">
				<s:radio label="Actif" name="user.status" list="@model.Status@values()" class="switch" onclick="return %{#edit};" readonly="%{#readonly}"/>
				<s:checkbox name="user.admin" label="Administrateur" readonly="%{#readonly}" onclick="return %{#edit};" />
			</c:if>

			<c:if test="${not empty sessionScope.user_admin && sessionScope.user_admin == true}">
				<s:submit type="button" onclick="sendMail(); return false;" value="Envoyer le mot de passe par mail"/>
			</c:if>
			
			<c:choose>
  				<c:when test="${edit}">
					<s:submit value="Mettre à jour" />
					<s:reset value="Annuler" onclick="location.href = './profil%{#urlid}'; return false;"/>
				</c:when>
				<c:otherwise>
					<s:submit value="Éditer"
						onclick="location.href = './profil%{#urlid}%{#symb}edit=true';
						return false;"/>
				</c:otherwise>
			</c:choose>	
		</s:form>
	</div>
</body>
</html>
