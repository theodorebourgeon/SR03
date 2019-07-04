package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.Person;
import model.Status;

public class PersonDao implements ApiDao<Person> {
	private DAOFactory daoFactory;
	
	PersonDao(DAOFactory daoFactory){
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
	
	
	private Person map(ResultSet rs) throws SQLException {
		Person person = new Person();
				
		// maj attributs, mail, pwd, name, society, phone, status_person, creation_date
		person.setId(rs.getInt("personId"));
		person.setMail(rs.getString("mail"));
		person.setPwd(rs.getString("pwd"));
		person.setAdmin(rs.getBoolean("admin"));
		person.setName(rs.getString("name"));
		person.setCorporation(rs.getString("society"));
		person.setPhone(rs.getString("phone"));
		person.setStatus(Status.fromString(rs.getString("status_person")));
		person.setCreation_date(rs.getTimestamp("creation_date").toLocalDateTime());
		
		return person;
	}
		
	
	@Override
	public Person get(int id){
	    Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    Person person = null;

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        
	        // pr�paration requ�te
	        preparedStatement = connexion.prepareStatement(
	        		"SELECT personId, mail, pwd, admin, name, society, phone, status_person, creation_date "
	        		+ "FROM person WHERE personId = ?"
	        		);
	        preparedStatement.setInt(1, id);
	        
	        resultSet = preparedStatement.executeQuery();
	        /* Parcours de la ligne de donn�es de l'�ventuel ResulSet retourn� */
	        if ( resultSet.next() ) {
	        	person = map(resultSet);
	        }
	    } catch ( SQLException e ) {
        	e.printStackTrace();
	    } finally {
	        closeAll(resultSet, preparedStatement, connexion );
	    }

	    return person;
	}
	


	@Override
	public List<Person> getAll(Person model) {
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    List<Person> personList = new ArrayList<Person>();

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        String req = "SELECT personId, mail, pwd, admin, name, society, phone, status_person, creation_date FROM person";
	        
	        // build on model
        	List<String> filters = new ArrayList<String>();
	        if (model != null) {
	        	if(model.getMail() != null) {
	        		if (filters.isEmpty()) { req += " WHERE";}
	        		else { req += ", AND";}
	        		filters.add("mail");
	        		req += " mail=?";
	        		
	        	}
	        	if(model.getPwd() != null) {
	        		if (filters.isEmpty()) { req += " WHERE";}
	        		else { req += " AND";}
	        		req += " pwd=?";
	    	        filters.add("pwd");
	        	}
	        }

	        req += " ORDER BY personId";
	        
	        // pr�paration requ�te
	        preparedStatement = connexion.prepareStatement(req);
	        int index = 1;
	        for (String filter: filters) {
	        	switch(filter) {
	        		case "mail" :
	        			preparedStatement.setString(index, model.getMail());
	        			break;
	        		case "pwd" :
	        			preparedStatement.setString(index, model.getPwd());
	        			break;	
	        	}
	        	index += 1;
	        }
	        
	        resultSet = preparedStatement.executeQuery();
	        
	        /* Parcours des lignes resultSet retourn�es */
	        while ( resultSet.next() ) {
	        	personList.add(map(resultSet));
	        }
	    } catch ( SQLException e ) {
        	e.printStackTrace();
	    } finally {
	        closeAll(resultSet, preparedStatement, connexion );
	    }

	    return personList;
	}
	
	
	@Override
	public Integer save(Person person) {
		Integer generatedKey = null;
	    Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        
	        // pr�paration requ�te
	        preparedStatement = connexion.prepareStatement(
	        		"INSERT INTO person (mail, pwd, admin, name, society, phone, status_person, creation_date)"
	        		+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?);",
	        		PreparedStatement.RETURN_GENERATED_KEYS);

	        preparedStatement.setString(1, person.getMail());
	        preparedStatement.setString(2, person.getPwd());
	        preparedStatement.setBoolean(3, person.isAdmin());
	        preparedStatement.setString(4, person.getName());
	        preparedStatement.setString(5, person.getCorporation());
	        preparedStatement.setString(6, person.getPhone());
	        preparedStatement.setString(7, person.getStatus().name());
	        preparedStatement.setTimestamp(8, Timestamp.valueOf(person.getCreation_date()));

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
	public boolean update(Person person) {
		boolean status = false;
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        
	        // pr�paration requ�te
	        preparedStatement = connexion.prepareStatement(
	        		"UPDATE person SET mail=?, pwd=?, admin=?, name=?, society=?, phone=?, status_person=? WHERE personId = ?");

	        preparedStatement.setString(1, person.getMail());
	        preparedStatement.setString(2, person.getPwd());
	        preparedStatement.setBoolean(3, person.isAdmin());
	        preparedStatement.setString(4, person.getName());
	        preparedStatement.setString(5, person.getCorporation());
	        preparedStatement.setString(6, person.getPhone());
	        preparedStatement.setString(7, person.getStatus().name());
	        preparedStatement.setInt(8, person.getId());

	        // Ex�cution de la requ�te => OK = valeur retourn�ee non nulle
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
	        preparedStatement = connexion.prepareStatement("DELETE FORM person WHERE personId = ?");

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
