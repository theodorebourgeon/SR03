package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import model.Person;
import model.Theme;
import model.Survey;
import model.Question;
import model.Answer;
import model.Evaluation;

public class DAOFactory {

	private static DAOFactory factory;
	
	private static final String DB_URL = "jdbc:mysql://localhost:3306/evaluation_stagiaire?serverTimezone=UTC";
	private static final String DB_USER = "www";
	private static final String DB_PSWD = "mdp_www";
	private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private DAOFactory() {
        try {
            Class.forName(DRIVER);
        } catch ( ClassNotFoundException e ) {
        	e.printStackTrace();
        }
    }

    /* r�cup�rer singleton factory */
    public static DAOFactory getInstance() {
    	if(factory == null) {
    		factory = new DAOFactory();
    	}
    	return factory;
    }

    /* M�thode charg�e de fournir une connexion � la base de donn�es */
     /* package */ Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PSWD );
    }

    /* M�thodes de r�cup�ration de l'impl�mentation des diff�rents DAO  */
    public ApiDao<Person> getPersonDao() {
        return new PersonDao( this );
    }
    
    public ApiDao<Theme> getThemeDao() {
        return new ThemeDao( this );
    }
    
    public ApiDao<Survey> getSurveyDao() {
        return new SurveyDao( this );
    }
    
    public ApiDao<Question> getQuestionDao() {
        return new QuestionDao( this );
    }
    
    public ApiDao<Answer> getAnswerDao() {
        return new AnswerDao( this );
    }
    
    public ApiDao<Evaluation> getEvaluationDao() {
        return new EvaluationDao( this );
    }
}