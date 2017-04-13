/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prj.user;

import java.util.EventListener;

/**
 *
 * @author fanhuigang
 */
public interface LoginListener extends EventListener
{
	public void loginFailed(LoginEvent source);

	public void loginStarted(LoginEvent source);

	public void loginCanceled(LoginEvent source);

	public void loginSucceeded(LoginEvent source);
}
