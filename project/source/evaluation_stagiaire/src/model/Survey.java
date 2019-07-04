package model;

import java.util.Comparator;
import java.util.TreeSet;

public class Survey {
	private int id;
	private int themeId;
	private String subject;
	private Status status;
	private TreeSet<Ordered_question> questions; // key = order, value= questionId
	
	public Survey() {
		this.questions = new TreeSet<Ordered_question>( new Comparator<Ordered_question>() {
		    public  int compare(Ordered_question q1, Ordered_question q2) {
		    	return Integer.compare(q1.getOrder(), q2.getOrder());
		   }
		}) ;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getThemeId() {
		return themeId;
	}
	public void setThemeId(int themeId) {
		this.themeId = themeId;
	}
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public TreeSet<Ordered_question> getQuestions() {
		return questions;
	}
	public void setQuestions(TreeSet<Ordered_question> questions) {
		this.questions = questions;
	}
	public void addQuestion(Ordered_question question) {
		this.questions.add(question);
	}
	public void removeQuestion(int questionId) {
		this.questions.removeIf(q -> (q.questionId == questionId));
	}
	public int getLastOrder() {
		if(this.questions.isEmpty()) {
			return -1;
		}
		return this.questions.last().getOrder();
	}
	public int getNbActiveQuestion() {
		return (int) this.questions.stream().filter(q -> q.getStatus() == Status.ON).count();
	}
	
	public class Ordered_question {
		private int order;
		private Integer questionId;
		private Status status;
		
		public Ordered_question() {
			
		}
		
		public Ordered_question(int order, int questionId) {
			this.order = order;
			this.questionId = questionId;
		}
		
		public int getOrder() {
			return order;
		}
		public void setOrder(int order) {
			this.order = order;
		}
		public int getQuestionId() {
			return questionId;
		}
		public void setQuestionId(Integer questionId) {
			this.questionId = questionId;
		}
		public Status getStatus() {
			return status;
		}
		public void setStatus(Status status) {
			this.status = status;
		}
	}
}
