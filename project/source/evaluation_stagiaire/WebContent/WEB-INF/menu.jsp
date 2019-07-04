<div id="menu">
	<%
	Integer user_id = (Integer) request.getSession().getAttribute("user_id");
	Boolean user_admin = (Boolean) request.getSession().getAttribute("user_admin");
	
	// liens admin
	if (user_admin != null && user_admin.booleanValue() == true) {
	%>
			<a href="utilisateurs"><button>Utilisateurs</button></a>
			<a href="questions"><button>Questions</button></a>	
	<% 
	}
	
	// liens déconnecté
	if (user_id == null){
		%>
			<a href="connexion"><button>Connexion</button></a>
		<%
	} else {
	// liens connecté
	%>
			<a href="questionnaires"><button>Questionnaires</button></a>
			<a href="evaluations"><button>Évaluations</button></a>
			<a href="profil"><button>Profil</button></a>
			<a href="deconnexion"><button>Déconnexion</button></a>		
	<%
	}
	%>


</div>