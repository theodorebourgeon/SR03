package action;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math; 

import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Person;

public class ListUsersAction extends ActionSupport{

	private static final long serialVersionUID = 2929396299155686964L;
	
	private static final int NB_USERS = 10;
	
	public List<Person> users = new ArrayList<Person>();
	public String filtre;
	public int page=1;
	public int nbPages;
	
	public int getNbPages() {
		return this.nbPages;
	}
	
	public Integer getPage() {
		return this.page;
	}
	
	public void setPage(String page) {
		this.page = 1;
	    try {
	        this.page = Integer.parseInt(page);
	      } catch (NumberFormatException nfe){}
	}
	
	public String execute() {
		ApiDao<Person> personDao = DAOFactory.getInstance().getPersonDao();
		this.users =personDao.getAll(null);
		
		String f = filtre != null ? filtre.toUpperCase() : "";
		this.users.removeIf(u -> {
			return !u.getMail().toUpperCase().contains(f) && !u.getName().contains(f);
		});
		
		this.nbPages = (int) Math.ceil(this.users.size() * 1.0 / NB_USERS);
        if(this.page > this.nbPages)
        	this.page = this.nbPages;
		
		int start = NB_USERS*((this.page < 1 ? 1 : this.page) - 1);
		int stop = Math.min(start+NB_USERS, this.users.size());
		
	    try {
			this.users = this.users.subList(start, stop);
	      } catch (IndexOutOfBoundsException e){System.out.print(e.getMessage());}
		
		return ActionSupport.SUCCESS;
	}
}
