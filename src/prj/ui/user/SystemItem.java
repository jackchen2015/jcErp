/*
 * Copyright 2009 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */
package com.hongxin.omc.protocol;

/**
 * 系统字典表表项数据对象。
 * @author fanhuigang
 */
public class SystemItem
{
	/**
	 * 条目id。
	 */
    private int itemId;
	/**
	 * 条目名称。
	 */
    private String itemName;
	/**
	 * 条目类型id。
	 */
    private int typeId;
	/**
	 * 条目类型名称。
	 */
    private String typeName;
	/**
	 * 条目读标志。
	 */
    private int readFlag;
	/**
	 * 条目写标志。
	 */
    private int writeFlag;
	/**
	 * 条目显示序号。
	 */
	private int orderNo;
	/**
	 * 条目别名。
	 */
    private String itemAlias;

	@Override
	public String toString()
	{
		StringBuilder sbf = new StringBuilder();

		sbf.append("SystemItem[itemId=");
		sbf.append(itemId);
		sbf.append(",itemName=");
		sbf.append(itemName);
		sbf.append(",typeId=");
		sbf.append(typeId);
		sbf.append(",typeName=");
		sbf.append(typeName);
		sbf.append(",readFlag=");
		sbf.append(readFlag);
		sbf.append(",writeFlag=");
		sbf.append(writeFlag);
		sbf.append(",orderNo=");
		sbf.append(orderNo);
		sbf.append(",itemAlias=");
		sbf.append(getItemAlias());
		sbf.append("]");

		return sbf.toString();
	}

	/**
	 * 获取条目id。
	 * @return 字典条目id
	 */
    public int getItemId()
    {
        return itemId;
    }

	/**
	 * 设置条目id。
	 * @param itemId 字典条目id
	 */
    public void setItemId(int itemId)
    {
        this.itemId = itemId;
    }

	/**
	 * 获取条目显示序号。
	 * @return 条目显示序号
	 */
    public int getOrderNo()
    {
        return orderNo;
    }

	/**
	 * 设置条目显示序号。
	 * @param orderNo 条目显示序号
	 */
    public void setOrderNo(int orderNo)
    {
        this.orderNo = orderNo;
    }

	/**
	 * 获取条目名称。
	 * @return 条目名称
	 */
    public String getItemName()
    {
        return itemName;
    }

	/**
	 * 设置条目名称。
	 * @param itemName 条目名称
	 */
    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

	/**
	 * 获取条目类型id。
	 * @return 字典条目类型id
	 */
    public int getTypeId()
    {
        return typeId;
    }

	/**
	 * 设置条目类型id。
	 * @param typeId 条目类型id
	 */
    public void setTypeId(int typeId)
    {
        this.typeId = typeId;
    }

	/**
	 * 获取条目类型名称。
	 * @return 条目类型名称
	 */
    public String getTypeName()
    {
        return typeName;
    }

	/**
	 * 设置条目类型名称。
	 * @param typeName 条目类型名称
	 */
    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

	/**
	 * 获取条目读取标志。
	 * @return 读取标志
	 */
    public int getReadFlag()
    {
        return readFlag;
    }

	/**
	 * 设置条目读取标志。
	 * @param readFlag 读取标志
	 */
    public void setReadFlag(int readFlag)
    {
        this.readFlag = readFlag;
    }

	/**
	 * 获取条目写入标志。
	 * @return 条目写入标志
	 */
    public int getWriteFlag()
    {
        return writeFlag;
    }

	/**
	 * 设置条目写入标志。
	 * @param writeFlag 条目写入标志
	 */
    public void setWriteFlag(int writeFlag)
    {
        this.writeFlag = writeFlag;
    }

	/**
	 * 条目别名。
	 * @return the itemAlias
	 */
	public String getItemAlias()
	{
		return itemAlias;
	}

	/**
	 * 条目别名。
	 * @param itemAlias the itemAlias to set
	 */
	public void setItemAlias(String itemAlias)
	{
		this.itemAlias = itemAlias;
	}
}
