package model;

import java.util.NoSuchElementException;

public enum Status {
	ON, OFF;

	public static Status fromString(String string) {
		  for (Status status : values()) {
		    if (status.name().equalsIgnoreCase(string)) {
		      return status;
		    }
		  }
		  throw new NoSuchElementException("Element with string " + string + " has not been found");
		}
	
	public static Status switchStatus(Status st) {
		if(st == Status.ON)
			return Status.OFF;
		return Status.ON;
	}
}
