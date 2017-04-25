/*
 * Copyright 2013 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */
package prj.ui.user;

/**
 * 服务器连接.
 * @author fanhuigang Created on 2014-5-13, 12:55:49
 */
public class ServerConnection
{
	/**
	 * 连接名称。
	 */
	private String name;
	/**
	 * 服务器地址。
	 */
	private String address;
	/**
	 * 端口号。
	 */
	private int port;
	/**
	 * 备注。
	 */
	private String remark;

	/**
	 * 连接名称。
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * 连接名称。
	 *
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * 服务器地址。
	 *
	 * @return the address
	 */
	public String getAddress()
	{
		return address;
	}

	/**
	 * 服务器地址。
	 *
	 * @param address the address to set
	 */
	public void setAddress(String address)
	{
		this.address = address;
	}

	/**
	 * 端口号。
	 *
	 * @return the port
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * 端口号。
	 *
	 * @param port the port to set
	 */
	public void setPort(int port)
	{
		this.port = port;
	}

	/**
	 * 备注。
	 *
	 * @return the remark
	 */
	public String getRemark()
	{
		return remark;
	}

	/**
	 * 备注。
	 *
	 * @param remark the remark to set
	 */
	public void setRemark(String remark)
	{
		this.remark = remark;
	}
}
