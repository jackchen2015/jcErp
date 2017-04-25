/*
 * Copyright 2013 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

package com.hongxin.omc.ui.common;

/**
 * 节点索引类。
 * @author fanhuigang
 * Created on 2014-5-27, 18:10:48
 */
public class NodeIndex
{
	/**
	 * 对象id。
	 */
	private int id;
	/**
	 * 节点类型。
	 */
	private int type;
	/**
	 * 用户对象。
	 */
	private Object userObject;
	
	public NodeIndex()
	{}

	public NodeIndex(int id, int type)
	{
		this(id, type, null);
	}
	
	public NodeIndex(int id, int type, Object userObject)
	{
		this.id = id;
		this.type = type;
		this.userObject = userObject;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof NodeIndex)
		{
			NodeIndex that = (NodeIndex)o;
			return this.id == that.id 
					&& this.type == that.type
					&& (this.userObject != null ? this.userObject.equals(that.userObject) : that.userObject == null);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 73 * hash + this.id;
		hash = 73 * hash + (this.userObject != null ? this.userObject.hashCode() : 0);
		hash = 73 * hash + this.type;
		return hash;
	}

	@Override
	public String toString()
	{
		StringBuilder sbd = new StringBuilder();
		sbd.append("NodeIndex[type=");
		sbd.append(type);
		sbd.append(",id=");
		sbd.append(id);
		sbd.append(",userObject=");
		sbd.append(userObject);
		sbd.append(",hashcode=");
		sbd.append(hashCode());
		sbd.append("]");
		return sbd.toString();
	}

	/**
	 * 对象ID。
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * 对象ID。
	 * @param id the id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * 节点类型。
	 * @return the type
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * 节点类型。
	 * @param type the type to set
	 */
	public void setType(int type)
	{
		this.type = type;
	}

	/**
	 * 用户对象。
	 * @return the userObject
	 */
	public Object getUserObject()
	{
		return userObject;
	}

	/**
	 * 用户对象。
	 * @param userObject the userObject to set
	 */
	public void setUserObject(Object userObject)
	{
		this.userObject = userObject;
	}
}
