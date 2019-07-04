package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.Evaluation;


public class EvaluationDao implements ApiDao<Evaluation> {
	private DAOFactory daoFactory;
	
	EvaluationDao(DAOFactory daoFactory){
		this.daoFactory = daoFactory;
	}
	
	private static void closeResult(ResultSet result) {
	    if ( result != null ) {
	        try {
	            /* On commence par fermer le ResultSet */
	            result.close();
	        } catch ( SQLException ignore ) { }
	    }
	}
	
	private static void closePS(PreparedStatement statement) {
	    if ( statement != null ) {
	        try {
	            /* Puis on ferme le Statement */
	            statement.close();
	        } catch ( SQLException ignore ) { }
	    }
	}
	
	private static void closeConnexion(Connection connexion) {
	    if ( connexion != null ) {
	        try {
	            /* Et enfin on ferme la connexion */
	            connexion.close();
	        } catch ( SQLException ignore ) { }
	    }			
	}
	
	private static void closeAll(ResultSet result, PreparedStatement statement, Connection connexion) {
	    closeResult(result);
	    closePS(statement);
	    closeConnexion(connexion);
	}
	
	
	private Evaluation map(ResultSet rs) throws SQLException {
		Evaluation evaluation = new Evaluation();
		
		// une évaluation peut être liée à plusieurs lignes car plusieurs questions
		// => il faut boucler sur le ResultSet tant que evaluationId ne change pas pour avoir toutes les questions!
		
		// maj attributs surveyId, subject, status_survey
		evaluation.setId(rs.getInt("evaluationId"));
		evaluation.setPersonId(rs.getInt("personId"));
		evaluation.setSurveyId(rs.getInt("surveyId"));
		evaluation.setDateStart(rs.getTimestamp("dateStart").toLocalDateTime());
		evaluation.setDateStop(rs.getTimestamp("dateStop").toLocalDateTime());
		evaluation.setScore(rs.getDouble("score"));
		
		do {
			int questionId = rs.getInt("questionId");
			if(rs.wasNull()) {
				continue;
			}
			
			int answerId = rs.getInt("answerId");
			evaluation.addChoice(questionId, answerId);
			
		} while(rs.next() && rs.getInt("evaluationId") == evaluation.getId());
		
		// on fait un rs.previous() pour ne pas perdre de ligne quand retour sur fonction principale
		rs.previous();
		
		return evaluation;
	}
	
	@Override
	public Evaluation get(int id) {
	    Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    Evaluation evaluation = null;

	    try {
	        // rï¿½cupï¿½ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        
	        // prï¿½paration requï¿½te
	        preparedStatement = connexion.prepareStatement(
	        		"SELECT evaluation.evaluationId as evaluationId, personId, surveyId, dateStart, dateStop, score, questionId, answerId "
	        		+ "FROM evaluation LEFT JOIN choice ON evaluation.evaluationId = choice.evaluationId "
	        		+ "WHERE evaluation.evaluationId = ?"
	        		);
	        preparedStatement.setInt(1, id);
	        
	        resultSet = preparedStatement.executeQuery();
	        /* Parcours de la ligne de donnï¿½es de l'ï¿½ventuel ResulSet retournï¿½ */
	        if ( resultSet.next() ) {
	        	evaluation = map(resultSet);
	        }
	    } catch ( SQLException e ) {
        	e.printStackTrace();
	    } finally {
	        closeAll(resultSet, preparedStatement, connexion );
	    }

	    return evaluation;
	}

	@Override
	public List<Evaluation> getAll(Evaluation model) {
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    List<Evaluation> evaluationList = new ArrayList<Evaluation>();

	    try {
	        // rï¿½cupï¿½ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        String req = "SELECT evaluation.evaluationId as evaluationId, personId, surveyId, dateStart, dateStop, score, questionId, answerId "
	        		+ "FROM evaluation LEFT JOIN choice ON evaluation.evaluationId = choice.evaluationId ";
	        
	        // build on model
        	List<String> filters = new ArrayList<String>();
	        if (model != null) {
	        	if(model.getPersonId() != null) {
	        		if (filters.isEmpty()) { req += " WHERE";}
	        		else { req += ", AND";}
	        		filters.add("personId");
	        		req += " personId=?";
	        		
	        	}
	        	if(model.getSurveyId() != null) {
	        		if (filters.isEmpty()) { req += " WHERE";}
	        		else { req += " AND";}
	        		req += " surveyId=?";
	    	        filters.add("surveyId");
	        	}
	        }

	        req += " ORDER BY evaluationId";
	        
	        // préparation requête
	        preparedStatement = connexion.prepareStatement(req);
	        int index = 1;
	        for (String filter: filters) {
	        	switch(filter) {
	        		case "personId" :
	        			preparedStatement.setInt(index, model.getPersonId());
	        			break;
	        		case "surveyId" :
	        			preparedStatement.setInt(index, model.getSurveyId());
	        			break;	
	        	}
	        	index += 1;
	        }
	        
	        resultSet = preparedStatement.executeQuery();
	        
	        /* Parcours des lignes resultSet retournï¿½es */
	        while ( resultSet.next() ) {
	        	evaluationList.add(map(resultSet));
	        }
	    } catch ( SQLException e ) {
        	e.printStackTrace();
	    } finally {
	        closeAll(resultSet, preparedStatement, connexion );
	    }

	    return evaluationList;
	}

	@Override
	public Integer save(Evaluation evaluation) {
		Integer generatedKey = null;
	    Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ArrayList<PreparedStatement> statementsList = new ArrayList<PreparedStatement>();
	    ResultSet rs = null;

	    try {
	        // rï¿½cupï¿½ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        connexion.setAutoCommit(false); // début transaction
	        
	        // prï¿½paration requï¿½te
	        preparedStatement = connexion.prepareStatement(
	        		"INSERT INTO evaluation (personId, surveyId, dateStart, dateStop, score) VALUES(?,?, ?, ?, ?)",
	        		PreparedStatement.RETURN_GENERATED_KEYS);
	        statementsList.add(preparedStatement);

	        preparedStatement.setInt(1, evaluation.getPersonId());
	        preparedStatement.setInt(2, evaluation.getSurveyId());
	        preparedStatement.setTimestamp(3, Timestamp.valueOf(evaluation.getDateStart()));
	        preparedStatement.setTimestamp(4, Timestamp.valueOf(evaluation.getDateStop()));
	        preparedStatement.setDouble(5, evaluation.getScore());

	        /* Exï¿½cution de la requï¿½te */
	        int statut = preparedStatement.executeUpdate();

	        /* Si la requï¿½te s'est bien ï¿½xï¿½cutï¿½e */
	        if ( statut != 0 ) {
	        	// on rï¿½cupï¿½re clï¿½e gï¿½nï¿½rï¿½e
	        	rs = preparedStatement.getGeneratedKeys();

	            if (rs.next()) {
	            	generatedKey = rs.getInt(1);
	            } 

	            // ajout des nouveaux ordered_answer
	            for(HashMap.Entry<Integer, Integer> choice : evaluation.getChoices().entrySet()) {
	            	preparedStatement = connexion.prepareStatement(
							"INSERT INTO choice (evaluationId, questionId, answerId) VALUES (?, ?, ?)"
							);
	            	statementsList.add(preparedStatement);
	    	        
	    	        preparedStatement.setInt(1, generatedKey);
	    	        preparedStatement.setInt(2, choice.getKey().intValue());
	    	        preparedStatement.setInt(3, choice.getValue().intValue());
	    	        preparedStatement.executeUpdate();
	            } 
	        }
	        connexion.commit(); // fin transaction
		    
	    } catch ( SQLException e ) {
        	e.printStackTrace();
        	try {
				connexion.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
	    } 
	    finally {
            closeResult(rs);
            for (PreparedStatement ps: statementsList) {
            	closePS(ps);
            }
	        closeConnexion(connexion);
	    }
		return generatedKey;
	}

	@Override
	public boolean update(Evaluation evaluation) {
		boolean status = false;
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ArrayList<PreparedStatement> statementsList = new ArrayList<PreparedStatement>();

	    try {
	        // rï¿½cupï¿½ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        connexion.setAutoCommit(false); // début transaction
	        
	        // prï¿½paration requï¿½te
	        preparedStatement = connexion.prepareStatement(
	        		"UPDATE evaluation SET personId=?, surveyId=?, dateStart=?, dateStop=?, score=? WHERE surveyId=?");
	        statementsList.add(preparedStatement);

	        preparedStatement.setInt(1, evaluation.getPersonId());
	        preparedStatement.setInt(2, evaluation.getSurveyId());
	        preparedStatement.setTimestamp(3, Timestamp.valueOf(evaluation.getDateStart()));
	        preparedStatement.setTimestamp(4, Timestamp.valueOf(evaluation.getDateStop()));
	        preparedStatement.setDouble(5, evaluation.getScore());
	        preparedStatement.setInt(6, evaluation.getId());

	        // Exï¿½cution de la requï¿½te => OK = valeur retournï¿½e non nulle
	        status = preparedStatement.executeUpdate() != 0;
	        
	        // suppression des anciens ordered_answer
	        preparedStatement = connexion.prepareStatement("DELETE FROM choice WHERE evaluationId=?");
	        statementsList.add(preparedStatement);

	        preparedStatement.setInt(1, evaluation.getId());
	        preparedStatement.executeUpdate();

            // ajout des nouveaux ordered_answer

            for(HashMap.Entry<Integer, Integer> choice : evaluation.getChoices().entrySet()) {
            	preparedStatement = connexion.prepareStatement(
						"INSERT INTO choice (evaluationId, questionId, answerId) VALUES (?, ?, ?)"
						);
            	statementsList.add(preparedStatement);
    	        
    	        preparedStatement.setInt(1, evaluation.getId());
    	        preparedStatement.setInt(2, choice.getKey().intValue());
    	        preparedStatement.setInt(3, choice.getValue().intValue());
    	        preparedStatement.executeUpdate();
            }
	        
	        connexion.commit(); // fin transaction

	    } catch ( SQLException e ) {
        	e.printStackTrace();
        	try {
				connexion.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
	    } finally {
            for (PreparedStatement ps: statementsList) {
            	closePS(ps);
            }
	        closeConnexion(connexion);
	    }
		
		return status;
	}

	@Override
	public boolean delete(int id) {
		boolean status = false;
	    Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    PreparedStatement preparedStatementList = null;

	    try {
	        // rï¿½cupï¿½ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        connexion.setAutoCommit(false); // début transaction

	        preparedStatementList = connexion.prepareStatement("DELETE FROM choice WHERE evaluationId=?");
	        preparedStatementList.setInt(1, id);
	        preparedStatementList.executeUpdate();
	        
	        preparedStatement = connexion.prepareStatement("DELETE FROM evaluation WHERE evaluationId=?");
	        preparedStatement.setInt(1, id);
	        status = preparedStatement.executeUpdate() != 0;

	        connexion.commit(); // fin transaction

	    } catch ( SQLException e ) {
        	e.printStackTrace();
        	try {
				connexion.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
	    } finally {
            closePS(preparedStatement);
            closePS(preparedStatementList);
	        closeConnexion(connexion);
	    }

		return status;
	}
}
