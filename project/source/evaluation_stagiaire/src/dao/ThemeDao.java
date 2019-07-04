package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Theme;


public class ThemeDao implements ApiDao<Theme> {
	private DAOFactory daoFactory;
	
	ThemeDao(DAOFactory daoFactory){
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
	
	
	private Theme map(ResultSet rs) throws SQLException {
		Theme theme = new Theme();
		
		theme.setId(rs.getInt("themeId"));
		theme.setTitle(rs.getString("title"));
		
		return theme;
	}
	
	@Override
	public Theme get(int id) {
	    Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    Theme theme = null;

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        
	        // pr�paration requ�te
	        preparedStatement = connexion.prepareStatement(
	        		"SELECT themeId, title FROM theme WHERE themeId = ?"
	        		);
	        preparedStatement.setInt(1, id);
	        
	        resultSet = preparedStatement.executeQuery();
	        /* Parcours de la ligne de donn�es de l'�ventuel ResulSet retourn� */
	        if ( resultSet.next() ) {
	        	theme = map(resultSet);
	        }
	    } catch ( SQLException e ) {
        	e.printStackTrace();
	    } finally {
	        closeAll(resultSet, preparedStatement, connexion );
	    }

	    return theme;
	}

	@Override
	public List<Theme> getAll(Theme theme) {
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
	    List<Theme> themeList = new ArrayList<Theme>();

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        String req = "SELECT themeId, title FROM theme";
	        
	        // pr�paration requ�te
	        preparedStatement = connexion.prepareStatement(req);
	        resultSet = preparedStatement.executeQuery();
	        
	        /* Parcours des lignes resultSet retourn�es */
	        while (resultSet.next() ) {
	        	themeList.add(map(resultSet));
	        }
	    } catch ( SQLException e ) {
        	e.printStackTrace();
	    } finally {
	        closeAll(resultSet, preparedStatement, connexion );
	    }

	    return themeList;
	}

	@Override
	public Integer save(Theme theme) {
		Integer generatedKey = null;
	    Connection connexion = null;
	    PreparedStatement preparedStatement = null;
        ResultSet rs = null;

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        
	        // pr�paration requ�te
	        preparedStatement = connexion.prepareStatement(
	        		"INSERT INTO theme (title) VALUES(?)",
	        		PreparedStatement.RETURN_GENERATED_KEYS);

	        preparedStatement.setString(1, theme.getTitle());

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
	public boolean update(Theme theme) {
		boolean status = false;
		Connection connexion = null;
	    PreparedStatement preparedStatement = null;

	    try {
	        // r�cup�ration connexion depuis la Factory
	        connexion = daoFactory.getConnection();
	        
	        // pr�paration requ�te
	        preparedStatement = connexion.prepareStatement(
	        		"UPDATE theme SET title=? WHERE themeId=?");

	        preparedStatement.setString(1, theme.getTitle());
	        preparedStatement.setInt(2, theme.getId());

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
	        preparedStatement = connexion.prepareStatement("DELETE FORM theme WHERE themeId = ?");

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
