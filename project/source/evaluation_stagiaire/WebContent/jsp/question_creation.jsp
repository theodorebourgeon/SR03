<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %> 
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Ajouter question</title>
	<link rel="stylesheet" href="./mystyle.css" media="screen" />
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
	<script type="text/javascript">
		function moveUp(clickedId) {
			tmp = document.getElementById("ans" + (clickedId-1));
	     	if(typeof(tmp) != 'undefined' && tmp != null){
	    		tmp = tmp.value;
		    	document.getElementById("ans" + (clickedId-1)).value = document.getElementById("ans" + clickedId).value;
		    	document.getElementById("ans" + clickedId).value = tmp;
	     	}
	     	
		    radio1 = document.getElementById("cor" + clickedId);
	     	radio2 = document.getElementById("cor" + (clickedId-1));
			if(radio1.checked){
				radio2.checked = true;
			}
			else if (radio2.checked){
				radio1.checked = true;
			}
	  	}
		
		function moveDown(clickedId) {
			tmp = document.getElementById("ans" + (clickedId+1));
	     	if(typeof(tmp) != 'undefined' && tmp != null){
	    		tmp = tmp.value;
		    	document.getElementById("ans" + (clickedId+1)).value = document.getElementById("ans" + clickedId).value;
		    	document.getElementById("ans" + clickedId).value = tmp;
	     	}
	     	
		    radio1 = document.getElementById("cor" + clickedId);
	     	radio2 = document.getElementById("cor" + (clickedId+1));
			if(radio1.checked){
				radio2.checked = true;
			}
			else if (radio2.checked){
				radio1.checked = true;
			}
	  	}
		
		function remove(clickedId) {
			tmp = document.getElementById("ans" + (clickedId+1));
			
	     	while(typeof(tmp) != 'undefined' && tmp != null){
	    		tmp = tmp.value;
		    	document.getElementById("ans" + clickedId).value = tmp;
				document.getElementById("cor" + clickedId).checked = document.getElementById("cor" + (clickedId+1)).checked;
				
				clickedId++;
				tmp = document.getElementById("ans" + (clickedId+1));
	     	}
	     	tr_to_del = document.getElementById("ans" + (clickedId)).parentNode.parentNode;
	     	tr_to_del.parentNode.removeChild(tr_to_del.previousElementSibling);
	     	tr_to_del.parentNode.removeChild(tr_to_del);
	  	}
	</script>
</head>
<body>
	<jsp:include page="../WEB-INF/menu.jsp"/>
	<s:set var="hidden_id" scope="page ">${param.id}</s:set>
	<div id="content">
		<h1>Ajouter une question</h1>
		<s:form class="form" theme="simple"><table class="form">
    		<s:hidden name="id" value="%{#hidden_id}"/>
			<tr><td colspan=2><h2>Intitulé:</h2></td></tr>
			<tr>
				<td colspan=2 class="tdErrorMessage"><s:fielderror fieldName="content" theme="simple" /></td>
			</tr>
    		<tr><td colspan=2 class="tdInput">
				<s:textfield name="content" label="Intitulé"/>
			</td></tr>
			
			<tr><td colspan=2><h2>Réponses:</h2></td></tr>	
			<tr>
				<td colspan=2 class="tdErrorMessage"><s:fielderror fieldName="answers" theme="simple" /></td>
			</tr>
			<s:iterator value="answers" status="ans">
				<tr>
					<td class="tdLabelTop addAnswer">
						<input type="radio" name="correctAnswerIndex" id="cor${ans.index}" value="${ans.index}" ${correctAnswerIndex == ans.index ? 'checked':''} /> 
						Réponse ${ans.index+1}:
					</td>
					<td class="right">
						<a href="#" onclick="moveUp(${ans.index}); return false;"><i class="fa fa-chevron-up small"></i></a> 
						<a href="#" onclick="moveDown(${ans.index}); return false;"><i class="fa fa-chevron-down small"></i></a> 
						<a href="#" onclick="remove(${ans.index}); return false;"><i class="fa fa-times small"></i></a> 
					</td>
				</tr>
				<tr><td colspan="2" class="tdInput">
			    	<s:textfield name="answers[%{#ans.index}]" id="ans%{#ans.index}" placeholder="Réponse..."/>
			    </td></tr>
			</s:iterator>
			
			<tr><td colspan="2" >
				<s:submit style="float: right" type="button" action="nouvelle_reponse">
					<i class="fa fa-plus"></i> Réponse
				</s:submit>
			</td></tr>
			
			<tr><td colspan="2" >
				<s:submit value="Ajouter cette question"  action="ajouter_question"/>
			</td></tr>
		</table></s:form>
	</div>
</body>
</html>
