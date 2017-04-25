/*
 * Copyright 2013 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

package prj.user.po;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色对象。
 * @author chenwei
 * Created on 2017-4-20, 11:31:24
 */
public class Role implements Cloneable
{
	/**
	 * 角色id。
	 */
	private int id;
	/**
	 * 角色名称。
	 */
	private String name;
	/**
	 * 角色描述。
	 */
	private String description;
	/**
	 * 权限id列表。
	 */
	private List<Integer> listFunction;
	
	@Override
	public Role clone()
	{
		try
		{
			return (Role)super.clone();
		}
		catch(Exception exp)
		{
			return null;
		}
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 11 * hash + this.id;
		return hash;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Role)
		{
			return this.id == ((Role)o).id;
		}
		return false;
	}
	
	/**
	 * 初始化权限列表。
	 */
	private void initFunctions()
	{
		if(listFunction == null)
		{
			listFunction = new ArrayList();
		}
	}

	/**
	 * 角色id。
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * 角色id。
	 * @param id the id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * 角色名称。
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * 角色名称。
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * 角色描述。
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * 角色描述。
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * 检查权限列表是否为空。
	 * @return 检查结果
	 */
	public boolean isFunctionEmpty()
	{
		return listFunction == null || listFunction.isEmpty();
	}

	/**
	 * 权限id列表。
	 * @return the listFunction
	 */
	public List<Integer> getListFunction()
	{
		initFunctions();
		return listFunction;
	}

	/**
	 * 权限id列表。
	 * @param listFunction the listFunction to set
	 */
	public void setListFunction(List<Integer> listFunction)
	{
		initFunctions();
		this.listFunction.clear();
		this.listFunction.addAll(listFunction);
	}
}
