package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Question;
import model.Status;


public class QuestionDao implements ApiDao<Question> {
	private DAOFactory daoFactory;
	
	QuestionDao(DAOFactory daoFactory){
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
	
	
	private Question map(ResultSet rs) throws SQLException {
		Question question = new Question();
		
		// une question peut �tre li�e � plusieurs lignes car plusieurs r�ponses
		// => il faut boucler sur le ResultSet tant que questionId ne change pas pour avoir toutes les r�ponses!
		
		question.setId(rs.getInt("questionId"));
		question.setContent(rs.getString("content"));
		
		do {
			Question.Ordered_answer ordered_answer = question.new Ordered_answer();
			ordered_answer.setAnswerId(rs.getInt("answerId"));
			if(rs.wasNull()) {
				continue;
			}
			ordered_answer.setOrder(rs.getInt("rank"));
			ordered_answer.setCorrect(rs.getBoolean("correct"));
			ordered_answer.setStatus(Status.fromString(rs.getString("status_answer")));
			question.addAnswer(ordered_answer);
			
		} while(rs.next() && rs.getInt("questionId") == question.getId());
		
		// on fait un rs.previous() pour ne pas perdre de ligne quand retour sur fonction principale
		rs.previous();
		
		return question;
	}
	
	@Override
	public Question get(int id) {
	    Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    Question question = null;

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        
	        // pr�paration requ�te
	        preparedStatement = connexion.prepareStatement(
	        		"SELECT question.questionId as questionId, content, answerId, rank, correct, status_answer "
	        		+ "FROM question LEFT JOIN order_answer ON question.questionId = order_answer.questionId "
	        		+ "WHERE question.questionId = ?"
	        		);
	        preparedStatement.setInt(1, id);
	        
	        resultSet = preparedStatement.executeQuery();
	        /* Parcours de la ligne de donn�es de l'�ventuel ResulSet retourn� */
	        if ( resultSet.next() ) {
	        	question = map(resultSet);
	        }
	    } catch ( SQLException e ) {
        	e.printStackTrace();
	    } finally {
	        closeAll(resultSet, preparedStatement, connexion );
	    }

	    return question;
	}

	@Override
	public List<Question> getAll(Question question) {
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    List<Question> questionList = new ArrayList<Question>();

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        String req = "SELECT question.questionId as questionId, content , answerId, rank, correct, status_answer "
	        		+ "FROM question LEFT JOIN order_answer ON question.questionId = order_answer.questionId";
	        
	        // pr�paration requ�te
	        preparedStatement = connexion.prepareStatement(req);
	        resultSet = preparedStatement.executeQuery();
	        
	        /* Parcours des lignes resultSet retourn�es */
	        while (resultSet.next() ) {
	        	questionList.add(map(resultSet));
	        }
	    } catch ( SQLException e ) {
        	e.printStackTrace();
	    } finally {
	        closeAll(resultSet, preparedStatement, connexion );
	    }

	    return questionList;
	}

	@Override
	public Integer save(Question question) {
		Integer generatedKey = null;
	    Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ArrayList<PreparedStatement> statementsList = new ArrayList<PreparedStatement>();
	    ResultSet rs = null;

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        connexion.setAutoCommit(false); // d�but transaction
	        
	        // pr�paration requ�te
	        preparedStatement = connexion.prepareStatement(
	        		"INSERT INTO question (content) VALUES(?)",
	        		PreparedStatement.RETURN_GENERATED_KEYS);
	        statementsList.add(preparedStatement);

	        preparedStatement.setString(1, question.getContent());

	        /* Ex�cution de la requ�te */
	        int statut = preparedStatement.executeUpdate();
	        

	        /* Si la requ�te s'est bien �x�cut�e */
	        if ( statut != 0 ) {
	        	// on r�cup�re cl�e g�n�r�e
	        	rs = preparedStatement.getGeneratedKeys();

	            if (rs.next()) {
	            	generatedKey = rs.getInt(1);
	            }

	            // ajout des ordered_answer
	            for(Question.Ordered_answer oa : question.getAnswers()) {
	            	preparedStatement = connexion.prepareStatement(
							"INSERT INTO order_answer (questionId, answerId, rank, correct, status_answer) VALUES (?, ?, ?, ?, ?)"
							);
	            	statementsList.add(preparedStatement);
	    	        
	    	        preparedStatement.setInt(1, generatedKey);
	    	        preparedStatement.setInt(2, oa.getAnswerId());
	    	        preparedStatement.setInt(3, oa.getOrder());
	    	        preparedStatement.setBoolean(4,  oa.isCorrect());
	    	        preparedStatement.setString(5, oa.getStatus().name());
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
	public boolean update(Question question) {
		boolean status = false;
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ArrayList<PreparedStatement> statementsList = new ArrayList<PreparedStatement>();

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        connexion.setAutoCommit(false); // d�but transaction
	        
	        // pr�paration requ�te
	        preparedStatement = connexion.prepareStatement(
	        		"UPDATE question SET content=? WHERE questionId=?");
	        statementsList.add(preparedStatement);

	        preparedStatement.setString(1, question.getContent());
	        preparedStatement.setInt(2, question.getId());

	        // Ex�cution de la requ�te => OK = valeur retourn�e non nulle
	        status = preparedStatement.executeUpdate() != 0;
	        
	        // suppression des anciens ordered_answer
	        preparedStatement = connexion.prepareStatement("DELETE FROM order_answer WHERE questionId=?");
	        statementsList.add(preparedStatement);

	        preparedStatement.setInt(1, question.getId());
	        preparedStatement.executeUpdate();

            // ajout des nouveaux ordered_answer
            for(Question.Ordered_answer oa : question.getAnswers()) {
            	preparedStatement = connexion.prepareStatement(
						"INSERT INTO order_answer (questionId, answerId, rank, correct, status_answer) VALUES (?, ?, ?, ?, ?)"
						);
            	statementsList.add(preparedStatement);
    	        
    	        preparedStatement.setInt(1, question.getId());
    	        preparedStatement.setInt(2, oa.getAnswerId());
    	        preparedStatement.setInt(3, oa.getOrder());
    	        preparedStatement.setBoolean(4,  oa.isCorrect());
    	        preparedStatement.setString(5,  oa.getStatus().name());
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
	    ArrayList<PreparedStatement> statementsList = new ArrayList<PreparedStatement>();

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        connexion.setAutoCommit(false); // d�but transaction

	        // suppression des ordered_answer avant question (pb cl� �trang�re)
	        preparedStatement = connexion.prepareStatement("DELETE FROM order_answer WHERE questionId=?");
	        statementsList.add(preparedStatement);

	        preparedStatement.setInt(1, id);
	        preparedStatement.executeUpdate();
	        
	        
	        // suppression de la question
	        preparedStatement = connexion.prepareStatement("DELETE FORM question WHERE questionId = ?");
	        statementsList.add(preparedStatement);

	        preparedStatement.setInt(1, id);
	        status = status && preparedStatement.executeUpdate() != 0;

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
}
