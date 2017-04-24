/*
 * Copyright 2009 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

package prj.user;

/**
 * 用户权限对象。
 * @author dangzerong
 */
public class Purview
{
	/**
	 * 权限id。
	 */
	private int id;
	/**
	 * 权限分组名称。
	 */
	private String group;
	/**
	 * 权限名称。
	 */
	private String name;

	public Purview()
	{

	}

	@Override
	public String toString()
	{
		StringBuilder sbf = new StringBuilder();

		sbf.append("Purview[id=");
		sbf.append(id);
		sbf.append(",group=");
		sbf.append(group);
		sbf.append(",name=");
		sbf.append(name);
		sbf.append("]");

		return sbf.toString();
	}

	/**
	 * 获取权限id。
	 * @return 权限id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * 设置权限id。
	 * @param id 权限id
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * 获取权限所属组别。
	 * @return 权限组别
	 */
	public String getGroup()
	{
		return group;
	}

	/**
	 * 设置权限组别。
	 * @param group 权限组别
	 */
	public void setGroup(String group)
	{
		this.group = group;
	}

	/**
	 * 获取权限名称。
	 * @return 权限名称
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * 设置权限名称。
	 * @param name 权限名称
	 */
	public void setName(String name)
	{
		this.name = name;
	}
}
