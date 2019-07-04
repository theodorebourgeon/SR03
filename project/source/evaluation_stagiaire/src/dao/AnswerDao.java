package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Answer;


public class AnswerDao implements ApiDao<Answer> {
	private DAOFactory daoFactory;
	
	AnswerDao(DAOFactory daoFactory){
		this.daoFactory = daoFactory;
	}
	
	private static void closeAll(ResultSet result, PreparedStatement statement, Connection connexion) {
	    if ( result != null ) {
	        try {
	            /* On commence par fermer le ResultSet */
	            result.close();
	        } catch ( SQLException ignore ) { }
	    }
	    if ( statement != null ) {
	        try {
	            /* Puis on ferme le Statement */
	            statement.close();
	        } catch ( SQLException ignore ) { }
	    }
	    if ( connexion != null ) {
	        try {
	            /* Et enfin on ferme la connexion */
	            connexion.close();
	        } catch ( SQLException ignore ) { }
	    }		
	}
	
	
	private Answer map(ResultSet rs) throws SQLException {
		Answer answer = new Answer();
		
		answer.setId(rs.getInt("answerId"));
		answer.setContent(rs.getString("content"));
		
		return answer;
	}
	
	@Override
	public Answer get(int id) {
	    Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    Answer answer = null;

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        
	        // pr�paration requ�te
	        preparedStatement = connexion.prepareStatement(
	        		"SELECT answerId, content FROM answer WHERE answerId = ?"
	        		);
	        preparedStatement.setInt(1, id);
	        
	        resultSet = preparedStatement.executeQuery();
	        /* Parcours de la ligne de donn�es de l'�ventuel ResulSet retourn� */
	        if ( resultSet.next() ) {
	        	answer = map(resultSet);
	        }
	    } catch ( SQLException e ) {
        	e.printStackTrace();
	    } finally {
	        closeAll(resultSet, preparedStatement, connexion );
	    }

	    return answer;
	}

	@Override
	public List<Answer> getAll(Answer model) {
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    List<Answer> answerList = new ArrayList<Answer>();

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        String req = "SELECT answerId, content FROM answer";
	        
	     // build on model
        	List<String> filters = new ArrayList<String>();
	        if (model != null) {	        	
	        	if(model.getContent() != null && !model.getContent().isBlank()) {
	        		if (filters.isEmpty()) { req += " WHERE";}
	        		else { req += " AND";}
	        		req += " LOWER(content) LIKE ?";
	        		filters.add("content");
	        	}
	        	
		        preparedStatement = connexion.prepareStatement(req);
		        
		        int index = 1;
		        for (String filter: filters) {
		        	switch(filter) {
		        		case "content" :
		    		        preparedStatement.setString(index, model.getContent().toLowerCase());
		        			break;
		        	}
		        	index += 1;
		        }
	        }
	        else {
		        preparedStatement = connexion.prepareStatement(req);
	        }
	        
	        resultSet = preparedStatement.executeQuery();
	        
	        /* Parcours des lignes resultSet retourn�es */
	        while (resultSet.next() ) {
	        	answerList.add(map(resultSet));
	        }
	    } catch ( SQLException e ) {
        	e.printStackTrace();
	    } finally {
	        closeAll(resultSet, preparedStatement, connexion );
	    }

	    return answerList;
	}

	@Override
	public Integer save(Answer answer) {
		Integer generatedKey = null;
	    Connection connexion = null;
	    PreparedStatement preparedStatement = null;
        ResultSet rs = null;

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        
	        // pr�paration requ�te
	        preparedStatement = connexion.prepareStatement(
	        		"INSERT INTO answer (content) VALUES(?)",
	        		PreparedStatement.RETURN_GENERATED_KEYS);

	        preparedStatement.setString(1, answer.getContent());

	        /* Ex�cution de la requ�te */
	        int statut = preparedStatement.executeUpdate();
	        
	        /* Si la requ�te s'est bien �x�cut�e */
	        if ( statut != 0 ) {
	        	// on r�cup�re cl�e g�n�r�e
	        	rs = preparedStatement.getGeneratedKeys();

	            if (rs.next()) {
	            	generatedKey = rs.getInt(1);
	            } 
	        }
	    } catch ( SQLException e ) {
        	e.printStackTrace();
	    } finally {
	        closeAll( rs, preparedStatement, connexion );
	    }
		return generatedKey;
	}

	@Override
	public boolean update(Answer answer) {
		boolean status = false;
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        
	        // pr�paration requ�te
	        preparedStatement = connexion.prepareStatement(
	        		"UPDATE answer SET content=? WHERE answerId=?");

	        preparedStatement.setString(1, answer.getContent());
	        preparedStatement.setInt(2, answer.getId());

	        // Ex�cution de la requ�te => OK = valeur retourn�e non nulle
	        status = preparedStatement.executeUpdate() != 0;

	    } catch ( SQLException e ) {
        	e.printStackTrace();
	    } finally {
	        closeAll( null, preparedStatement, connexion );
	    }
		
		return status;
	}

	@Override
	public boolean delete(int id) {
		boolean status = false;
	    Connection connexion = null;
	    PreparedStatement preparedStatement = null;

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        
	        // pr�paration requ�te
	        preparedStatement = connexion.prepareStatement("DELETE FORM answer WHERE answerId = ?");

	        preparedStatement.setInt(1, id);

	        // Ex�cution de la requ�te => OK si valeur retourn�e non nulle
	        status = preparedStatement.executeUpdate() != 0;

	    } catch ( SQLException e ) {
        	e.printStackTrace();
	    } finally {
	        closeAll( null, preparedStatement, connexion );
	    }

		return status;
	}
}
