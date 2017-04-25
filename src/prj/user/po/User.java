/*
 * Copyright 2009 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */
package prj.user.po;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户对象。
 * @author chenwei
 * Created on 2017-4-12, 15:14:30
 */
public class User implements Cloneable
{
	/**
	 * 用户id。
	 */
	private int id;
	/**
	 * 用户名称。
	 */
	private String name;
	/**
	 * 真实名称。
	 */
	private String realName;
	/**
	 * 别名。
	 */
	private String aliasName;
	/**
	 * 在线状态。
	 */
	private int status;
	/**
	 * 启用状态。
	 */
	private int state;
	/**
	 * 一次性密码。
	 */
	private int oneoffPassword;
	/**
	 * 密码。
	 */
	private String password;
	/**
	 * 有效期
	 */
	private String validTime;
	/**
	 * 密码有效期
	 */
	private int pwdValid;
	/**
	 * 所属用户组。
	 */
	private List<Integer> groups;
	/**
	 * 最后登录时间。
	 */
	private String lastLoginTime;
	/**
	 * 工作单位。
	 */
	private String workplace;

	private void initGroups()
	{
		if(groups == null)
		{
			groups = new ArrayList<Integer>(0);
		}
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof User)
		{
			User that = (User)o;
			return this.id == that.id;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 3;
		hash = 79 * hash + this.id;
		return hash;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sbd = new StringBuilder();
		
		sbd.append("User[id=");
		sbd.append(id);
		sbd.append(",name=");
		sbd.append(name);
		sbd.append(",realName=");
		sbd.append(realName);
		sbd.append(",aliasName=");
		sbd.append(aliasName);
		sbd.append(",status=");
		sbd.append(status);
		sbd.append(",state=");
		sbd.append(state);
		sbd.append(",oneoffPassword=");
		sbd.append(oneoffPassword);
		sbd.append(",password=");
		sbd.append(password);
		sbd.append(",validTime=");
		sbd.append(validTime);
		sbd.append(",pwdValid=");
		sbd.append(pwdValid);
		sbd.append(",lastLoginTime=");
		sbd.append(lastLoginTime);
		sbd.append(",groups=");
		sbd.append(groups);
		sbd.append("]");
		
		return sbd.toString();
	}

	/**
	 * 获取用户id。
	 * @return 用户id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * 设置用户id。
	 * @param id 用户id
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * 获取用户名称。
	 * @return 用户名称
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * 设置用户名称。
	 * @param name 用户名称
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * 获取用户真实姓名。
	 * @return 真实姓名
	 */
	public String getRealName()
	{
		return realName;
	}

	/**
	 * 设置用户真实姓名。
	 * @param realName 真实姓名
	 */
	public void setRealName(String realName)
	{
		this.realName = realName;
	}

	/**
	 * 获取别名。
	 * @return 别名
	 */
	public String getAliasName()
	{
		return aliasName;
	}

	/**
	 * 设置用户别名。
	 * @param aliasName 别名
	 */
	public void setAliasName(String aliasName)
	{
		this.aliasName = aliasName;
	}

	/**
	 * 获取用户在线状态。
	 * @return 在线状态，取值如下：
	 * <ul>
	 *	<li>{@link com.hongxin.omc.util.OmcConstants#us_online}，在线</li>
	 *	<li>{@link com.hongxin.omc.util.OmcConstants#us_offline}，不在线</li>
	 * </ul>
	 */
	public int getStatus()
	{
		return status;
	}

	/**
	 * 设置用户在线状态。
	 * @param status 在线状态，取值如下：
	 * <ul>
	 *	<li>{@link com.hongxin.omc.util.OmcConstants#us_online}，在线</li>
	 *	<li>{@link com.hongxin.omc.util.OmcConstants#us_offline}，不在线</li>
	 * </ul>
	 */
	public void setStatus(int status)
	{
		this.status = status;
	}

	/**
	 * 获取用户组id列表。
	 * @return 用户组id列表
	 */
	public List<Integer> getGroups()
	{
		initGroups();
		return groups;
	}

	/**
	 * 设置用户组id列表。
	 * @param groups 用户组id列表
	 */
	public void setGroups(List<Integer> groups)
	{
		if(groups != null)
		{
			this.groups = groups;
		}
		else
		{
			if(this.groups != null)
			{
				this.groups.clear();
			}
		}
	}

	/**
	 * 添加用户组。
	 * @param group 用户组id
	 */
	public void addGroup(Integer group)
	{
		initGroups();
		if(this.groups.indexOf(group) == -1)
		{
			this.groups.add(group);
		}
	}

	/**
	 * 获取用户启用状态。
	 * @return 启用状态，取值如下：
	 * <ul>
	 *	<li>{@link com.hongxin.omc.util.OmcConstants#as_enable}，启用</li>
	 *	<li>{@link com.hongxin.omc.util.OmcConstants#as_disable}，禁用</li>
	 * </ul>
	 */
	public int getState()
	{
		return state;
	}

	/**
	 * 设置用户启用状态。
	 * @param state 启用状态，取值如下：
	 * <ul>
	 *	<li>{@link com.hongxin.omc.util.OmcConstants#as_enable}，启用</li>
	 *	<li>{@link com.hongxin.omc.util.OmcConstants#as_disable}，禁用</li>
	 * </ul>
	 */
	public void setState(int state)
	{
		this.state = state;
	}

	/**
	 * 获取用户密码是否一次性密码。
	 * @return 是否一次性密码，取值如下：
	 * <ul>
	 *	<li>{@link com.hongxin.omc.util.OmcConstants#as_isoneoffpassword}，一次性密码</li>
	 *	<li>{@link com.hongxin.omc.util.OmcConstants#as_notoneoffpassword}，非一次性密码</li>
	 * </ul>
	 */
	public int getOneoffPassword()
	{
		return oneoffPassword;
	}

	/**
	 * 设置用户密码是否一次性密码。
	 * @param oneoffPassword 是否一次性密码，取值如下：
	 * <ul>
	 *	<li>{@link com.hongxin.omc.util.OmcConstants#as_isoneoffpassword}，一次性密码</li>
	 *	<li>{@link com.hongxin.omc.util.OmcConstants#as_notoneoffpassword}，非一次性密码</li>
	 * </ul>
	 */
	public void setOneoffPassword(int oneoffPassword)
	{
		this.oneoffPassword = oneoffPassword;
	}

	/**
	 * 密码。
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * 密码。
	 * @param password the password to set
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	public int getPwdValid()
	{
		return pwdValid;
	}

	public void setPwdValid(int pwdValid)
	{
		this.pwdValid = pwdValid;
	}

	public String getValidTime()
	{
		return validTime;
	}

	public void setValidTime(String validTime)
	{
		this.validTime = validTime;
	}

	@Override
	public User clone()
	{
		try
		{
			User copy = (User)super.clone();
			if(this.groups != null)
			{
				copy.groups = new ArrayList();
				copy.groups.addAll(this.groups);
			}
			return copy;
		}
		catch(Exception exp)
		{
			return null;
		}
	}

	/**
	 * 从User对象拷贝数据。
	 * @param User User对象
	 */
	public User copyFrom(User user)
	{
		setId(user.getId());
		setName(user.getName());
		setAliasName(user.getAliasName());
		setOneoffPassword(user.getOneoffPassword());
		setPassword(user.getPassword());
		setPwdValid(user.getPwdValid());
		setRealName(user.getRealName());
		setState(user.getState());
		setStatus(user.getStatus());
		setValidTime(user.getValidTime());
		setLastLoginTime(user.getLastLoginTime());
		setWorkplace(user.getWorkplace());
		getGroups().clear();
		getGroups().addAll(user.groups);
		
		return this;
	}

	/**
	 * 最后登录时间。
	 * @return the lastLoginTime
	 */
	public String getLastLoginTime()
	{
		return lastLoginTime;
	}

	/**
	 * 最后登录时间。
	 * @param lastLoginTime the lastLoginTime to set
	 */
	public void setLastLoginTime(String lastLoginTime)
	{
		this.lastLoginTime = lastLoginTime;
	}

	/**
	 * 工作单位。
	 * @return the workplace
	 */
	public String getWorkplace()
	{
		return workplace;
	}

	/**
	 * 工作单位。
	 * @param workplace the workplace to set
	 */
	public void setWorkplace(String workplace)
	{
		this.workplace = workplace;
	}
}
