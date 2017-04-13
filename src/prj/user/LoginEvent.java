/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prj.user;

import java.util.EventObject;

/**
 *
 * @author fanhuigang
 */
public class LoginEvent extends EventObject
{
	private Throwable cause;
	private String user;

	public LoginEvent(Object source)
	{
		this(source, null);
	}

	/** Creates a new instance of LoginEvent */
	public LoginEvent(Object source, Throwable cause)
	{
		super(source);
		this.cause = cause;
	}

	public Throwable getCause()
	{
		return cause;
	}

	public String getUser()
	{
		return user;
	}

	public LoginEvent setUser(String user)
	{
		this.user = user;
		return this;
	}
}
