package model;

import java.util.Comparator;
import java.util.TreeSet;

public class Question {
	private int id;
	private String content;
	private TreeSet<Ordered_answer> answers;
	
	public Question() {
		this.answers = new TreeSet<Ordered_answer>( new Comparator<Ordered_answer>() {
		    public  int compare(Ordered_answer a1, Ordered_answer a2) {
		    	return Integer.compare(a1.getOrder(), a2.getOrder());
		   }
		}) ;		
	}
	
	public Question(int id, String content, Status status) {
		this.id = id;
		this.content = content;
		this.answers = new TreeSet<Ordered_answer>( new Comparator<Ordered_answer>() {
		    public  int compare(Ordered_answer a1, Ordered_answer a2) {
		    	return Integer.compare(a1.getOrder(), a2.getOrder());
		   }
		}) ;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public TreeSet<Ordered_answer> getAnswers() {
		return answers;
	}
	public void setAnswers(TreeSet<Ordered_answer> answers) {
		this.answers = answers;
	}
	public void addAnswer(Ordered_answer answer) {
		this.answers.add(answer);
	}
	public void removeAnswer(int answerId) {
		this.answers.removeIf(ans -> (ans.answerId == answerId));
	}
	public int getLastOrder() {
		if(this.answers.isEmpty()) {
			return -1;
		}
		return this.answers.last().getOrder();
	}
	public int getIdRightAnswer() {
		return this.answers.stream().filter(a -> a.isCorrect() == true).findAny().get().getAnswerId();
	}
	
	public class Ordered_answer {
		private int order;
		private int answerId;
		private boolean correct;
		private Status status;
		
		public Ordered_answer() {
			
		}
		
		public Ordered_answer(int order, int answerId, boolean correct) {
			this.order = order;
			this.answerId = answerId;
			this.correct = correct;
		}
		
		public int getOrder() {
			return order;
		}
		public void setOrder(int order) {
			this.order = order;
		}
		public int getAnswerId() {
			return answerId;
		}
		public void setAnswerId(int answerId) {
			this.answerId = answerId;
		}
		public boolean isCorrect() {
			return correct;
		}
		public void setCorrect(boolean correct) {
			this.correct = correct;
		}
		public Status getStatus() {
			return status;
		}
		public void setStatus(Status status) {
			this.status = status;
		}
	}
	
}
