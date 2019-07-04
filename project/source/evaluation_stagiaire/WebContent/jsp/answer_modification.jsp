<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %> 
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Modifier une réponse</title>
	<link rel="stylesheet" href="./mystyle.css" media="screen" />
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>
	<jsp:include page="../WEB-INF/menu.jsp"/>
	<div id="content">
		<h1>Modifier une réponse</h1>
		<s:form action="reponse?id=%{id}&question=%{question}" class="form" labelposition="top">
			<tr>
				<td><i class="red">
					<b>Attention:</b><br/><br/>
					Dans le but de garder des formulaires et parcours cohérents, il vous est demandé de ne pas changer le sens de la réponse ci-dessous. <br/><br/>
					Cet outil doit servir à corriger une éventuelle faute ou incompréhension.<br/><br/>
				</i></td>
			</tr>
			<s:textfield name="content" label="Réponse"/>
			<s:submit value="Modifier cette réponse"/>
		</s:form>
	</div>
</body>
</html>
