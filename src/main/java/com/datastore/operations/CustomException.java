package com.datastore.crd.operations;

public class CustomException extends Exception{
	
	private String message;
	CustomException(String s)
	{
		this.message=s;
		System.out.println(toString());
	}
	@Override
	public String toString() {
		return message;
	}
	

}
