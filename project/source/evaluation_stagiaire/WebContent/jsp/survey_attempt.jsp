<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Évaluation</title>
	<link rel="stylesheet" href="./mystyle.css" media="screen" />
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
	<script>
	    var startDate = new Date("${evaluation.dateStart}").getTime();
	    setInterval(function () { refreshChrono(new Date().getTime() - startDate); }, 1000);
	    
	    function refreshChrono(distance){
	        var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
	        var minutes = ("0" + Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60))).slice(-2);
	        var seconds = ("0" + Math.floor((distance % (1000 * 60)) / 1000)).slice(-2);
	        
	    	// Seconds
	        if (distance <= 60000) {
	            document.getElementById("chrono").innerHTML = seconds + "s ";
	        }
	        // Minutes
	        else if (distance <= 3600000) {
	            document.getElementById("chrono").innerHTML = minutes + "m " + seconds + "s ";
	        }
	        // Hours
	        else if (distance <= 86400000) {
	            document.getElementById("chrono").innerHTML = hours + "h " + minutes + "m " + seconds + "s ";
	        }
	    }
	</script>
</head>
<body>
	<jsp:include page="../WEB-INF/menu.jsp"/>
	<div id="content">
		<h1>Évaluation: ${questionnaire.subject}</h1>
		
		<s:form action="tentative?questionnaire=%{questionnaire.id}" class="form" labelposition="top"> 	
			<tr>
				<td>
			    	<h3 class="center">Temps: &nbsp &nbsp &nbsp <span id="chrono"></span></h3>
				</td>
			</tr>
			<tr>
				<td>
			    	<h2>Question:</h2>
					<h3><i><s:property value="title"/></i></h3>
				</td>
			</tr>
			<s:hidden name="questionId"/>
			<s:iterator value="answers" var="a">
				<tr><td><s:radio theme="simple" name="answerId" list="#{#a.id:#a.content}" /></td></tr>
			</s:iterator>
			
			<s:submit value="Enregistrer" />
			
		</s:form>
	</div>
</body>
</html>