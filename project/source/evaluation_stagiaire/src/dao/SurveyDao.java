package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Survey;
import model.Status;


public class SurveyDao implements ApiDao<Survey> {
	private DAOFactory daoFactory;
	
	SurveyDao(DAOFactory daoFactory){
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
	
	
	private Survey map(ResultSet rs) throws SQLException {
		Survey survey = new Survey();
		
		// un questionnaire peut être liée à plusieurs lignes car plusieurs questions
		// => il faut boucler sur le ResultSet tant que surveyId ne change pas pour avoir toutes les questions!
		
		// maj attributs surveyId, subject, status_survey
		survey.setId(rs.getInt("surveyId"));
		survey.setThemeId(rs.getInt("themeId"));
		survey.setSubject(rs.getString("subject"));
		survey.setStatus(Status.fromString(rs.getString("status_survey")));
		
		do {
			Survey.Ordered_question ordered_question = survey.new Ordered_question();
			ordered_question.setQuestionId(rs.getInt("questionId"));
			if(rs.wasNull()) {
				continue;
			}
			ordered_question.setOrder(rs.getInt("rank"));
			ordered_question.setStatus(Status.fromString(rs.getString("status_question")));
			survey.addQuestion(ordered_question);
			
		} while(rs.next() && rs.getInt("surveyId") == survey.getId());
		
		// on fait un rs.previous() pour ne pas perdre de ligne quand retour sur fonction principale
		rs.previous();
		
		return survey;
	}
	
	@Override
	public Survey get(int id) {
	    Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    Survey survey = null;

	    try {
	        // rï¿½cupï¿½ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        
	        // prï¿½paration requï¿½te
	        preparedStatement = connexion.prepareStatement(
	        		"SELECT survey.surveyId as surveyId, themeId, subject, status_survey, questionId, rank, status_question "
	        		+ "FROM survey LEFT JOIN order_question ON survey.surveyId = order_question.surveyId "
	        		+ "WHERE survey.surveyId = ?"
	        		);
	        preparedStatement.setInt(1, id);
	        
	        resultSet = preparedStatement.executeQuery();
	        /* Parcours de la ligne de donnï¿½es de l'ï¿½ventuel ResulSet retournï¿½ */
	        if ( resultSet.next() ) {
	        	survey = map(resultSet);
	        }
	    } catch ( SQLException e ) {
        	e.printStackTrace();
	    } finally {
	        closeAll(resultSet, preparedStatement, connexion );
	    }

	    return survey;
	}

	@Override
	public List<Survey> getAll(Survey survey) {
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    List<Survey> surveyList = new ArrayList<Survey>();

	    try {
	        // rï¿½cupï¿½ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        String req = "SELECT survey.surveyId as surveyId, themeId, subject, status_survey, questionId, rank, status_question "
	        		+ "FROM survey LEFT JOIN order_question ON survey.surveyId = order_question.surveyId";
	        
	        // prï¿½paration requï¿½te
	        preparedStatement = connexion.prepareStatement(req);
	        resultSet = preparedStatement.executeQuery();
	        
	        /* Parcours des lignes resultSet retournï¿½es */
	        while ( resultSet.next() ) {
	        	surveyList.add(map(resultSet));
	        }
	    } catch ( SQLException e ) {
        	e.printStackTrace();
	    } finally {
	        closeAll(resultSet, preparedStatement, connexion );
	    }

	    return surveyList;
	}

	@Override
	public Integer save(Survey survey) {
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
	        		"INSERT INTO survey (themeId, subject, status_survey) VALUES(?, ?, ?)",
	        		PreparedStatement.RETURN_GENERATED_KEYS);
	        statementsList.add(preparedStatement);

	        preparedStatement.setInt(1, survey.getThemeId());
	        preparedStatement.setString(2, survey.getSubject());
	        preparedStatement.setString(3, survey.getStatus().name());

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
	            for(Survey.Ordered_question oq : survey.getQuestions()) {
	            	preparedStatement = connexion.prepareStatement(
							"INSERT INTO order_question (surveyId, questionId, rank, status_question) VALUES (?, ?, ?, ?)"
							);
	            	statementsList.add(preparedStatement);
	            	
	    	        preparedStatement.setInt(1, generatedKey);
	    	        preparedStatement.setInt(2, oq.getQuestionId());
	    	        preparedStatement.setInt(3, oq.getOrder());
	    	        preparedStatement.setString(4, oq.getStatus().name());
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
	public boolean update(Survey survey) {
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
	        		"UPDATE survey SET themeId=?, subject=?, status_survey=? WHERE surveyId=?");
	        statementsList.add(preparedStatement);

	        preparedStatement.setInt(1, survey.getThemeId());
	        preparedStatement.setString(2, survey.getSubject());
	        preparedStatement.setString(3, survey.getStatus().name());
	        preparedStatement.setInt(4, survey.getId());

	        // Exï¿½cution de la requï¿½te => OK = valeur retournï¿½e non nulle
	        status = preparedStatement.executeUpdate() != 0;
	        
	        // suppression des anciens ordered_answer
	        preparedStatement = connexion.prepareStatement("DELETE FROM order_question WHERE surveyId=?");
	        statementsList.add(preparedStatement);

	        preparedStatement.setInt(1, survey.getId());
	        preparedStatement.executeUpdate();

            // ajout des nouveaux ordered_answer
            for(Survey.Ordered_question oq : survey.getQuestions()) {
            	preparedStatement = connexion.prepareStatement(
						"INSERT INTO order_question (surveyId, questionId, rank, status_question) VALUES (?, ?, ?, ?)"
						);
            	statementsList.add(preparedStatement);
    	        
    	        preparedStatement.setInt(1, survey.getId());
    	        preparedStatement.setInt(2, oq.getQuestionId());
    	        preparedStatement.setInt(3, oq.getOrder());
    	        preparedStatement.setString(4, oq.getStatus().name());
    	        status = status && preparedStatement.executeUpdate() != 0;
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
	        	        	        
	        preparedStatementList = connexion.prepareStatement("DELETE FROM order_question WHERE surveyId=?");
	        preparedStatementList.setInt(1, id);
	        preparedStatementList.executeUpdate();
	        
	        preparedStatement = connexion.prepareStatement("DELETE FORM survey WHERE surveyId = ?");
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
