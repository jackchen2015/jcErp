/*
 * Copyright 2009 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */
package prj.user.po;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户组对象。
 * @author fanhuigang
 * Created on 2009-8-19, 15:26:30
 */
public class UserGroup implements Cloneable
{
	/**
	 * 用户组类型 - 普通。
	 */
	public static final int TYPE_NORMAL = 0;
	/**
	 * 用户组类型 - 超级。
	 */
	public static final int TYPE_SUPER = 1;
	/**
	 * 用户组类型 - 顶级。
	 */
	public static final int TYPE_TOP = 2;
	/**
	 * 用户组类型 - 预置。
	 */
	public static final int TYPE_PREP = 10;
	/**
	 * 用户组级别 - 市级。
	 */
	public static final int LEVEL_CITY = 0;
	/**
	 * 用户组级别 - 省级。
	 */
	public static final int LEVEL_PROVINCE = 1;
	/**
	 * 用户组id。
	 */
	private int id;
	/**
	 * 锁定状态。
	 */
	private int lockStatus;
	/**
	 * 用户组名称。
	 */
	private String name;
	/**
	 * 描述信息。
	 */
	private String description;
	/**
	 * 用户组类型，取值如下：
	 * <ul>
	 *	<li>{@link #TYPE_NORMAL} 普通</li>
	 *	<li>{@link #TYPE_SUPER} 超级</li>
	 *	<li>{@link #TYPE_TOP} 顶级</li>
	 * </ul>
	 */
	private int groupType;
	/**
	 * 用户组级别，取值如下：
	 * <ul>
	 *	<li>{@link #LEVEL_CITY} 市级</li>
	 *	<li>{@link #LEVEL_PROVINCE} 省级</li>
	 * </ul>
	 */
	private int groupLevel;
	/**
	 * 设备组id列表。
	 */
	private List<Integer> listGroupId;
	/**
	 * 角色id列表。
	 */
	private List<Integer> listRoleId;
	/**
	 * MML命令id列表。
	 */
	private List<Integer> commands;
	/**
	 * 设备id列表。
	 */
	private List<Integer> devices;
	/**
	 * 区域id列表。
	 */
	private List<Integer> districts;
	/**
	 * 用户总数。
	 */
	private int userCount;

	/**
	 * 初始化角色列表。
	 */
	private void initRoles()
	{
		if(listRoleId == null)
		{
			listRoleId = new ArrayList();
		}
	}

	/**
	 * 初始化命令列表。
	 */
	private void initCommands()
	{
		if(commands == null)
		{
			commands = new ArrayList();
		}
	}

	/**
	 * 初始化设备列表。
	 */
	private void initDevices()
	{
		if(devices == null)
		{
			devices = new ArrayList();
		}
	}

	/**
	 * 初始化设备组列表。
	 */
	private void initGroupId()
	{
		if(listGroupId == null)
		{
			listGroupId = new ArrayList();
		}
	}

	/**
	 * 初始化子网列表。
	 */
	private void initDistricts()
	{
		if(districts == null)
		{
			districts = new ArrayList(0);
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if(o == this)
		{
			return true;
		}
		if(o instanceof UserGroup)
		{
			UserGroup that = (UserGroup)o;
			// 只考虑id
			return this.id == that.id;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 19 * hash + this.id;
		return hash;
	}

	@Override
	public String toString()
	{
		StringBuilder sbd = new StringBuilder();
		sbd.append("UserGroup[id=");
		sbd.append(id);
		sbd.append(",name=");
		sbd.append(name);
		sbd.append(",lockStatus=");
		sbd.append(lockStatus);
		sbd.append(",description=");
		sbd.append(description);
		sbd.append(",groupType=");
		sbd.append(groupType);
		sbd.append("]");
		return sbd.toString();
	}

	@Override
	public UserGroup clone()
	{
		UserGroup copy;
		try
		{
			copy = (UserGroup)super.clone();
			// deep clone
			if(this.listGroupId != null)
			{
				copy.listGroupId = new ArrayList();
				copy.listGroupId.addAll(this.listGroupId);
			}
			if(this.listRoleId != null)
			{
				copy.listRoleId = new ArrayList();
				copy.listRoleId.addAll(this.listRoleId);
			}
			if(this.commands != null)
			{
				copy.commands = new ArrayList();
				copy.commands.addAll(this.commands);
			}
			if(this.devices != null)
			{
				copy.devices = new ArrayList();
				copy.devices.addAll(this.devices);
			}
			if(this.districts != null)
			{
				copy.districts = new ArrayList();
				copy.districts.addAll(this.districts);
			}
		}
		catch(CloneNotSupportedException ex)
		{
			copy = null;
		}
		return copy;
	}
	
	/**
	 * 获取用户组id。
	 * @return 用户组id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * 设置用户组id。
	 * @param id 用户组id
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * 获取用户组锁定状态。
	 * @return 锁定状态，取值如下：
	 * <ul>
	 *	<li>{@link com.hongxin.omc.util.OmcConstants#ugs_normal} 正常</li>
	 *	<li>{@link com.hongxin.omc.util.OmcConstants#ugs_locked} 锁定</li>
	 * </ul>
	 */
	public int getLockStatus()
	{
		return lockStatus;
	}

	/**
	 * 设置用户组锁定状态。
	 * @param lockStatus 锁定状态，取值如下：
	 * <ul>
	 *	<li>{@link com.hongxin.omc.util.OmcConstants#ugs_normal} 正常</li>
	 *	<li>{@link com.hongxin.omc.util.OmcConstants#ugs_locked} 锁定</li>
	 * </ul>
	 */
	public void setLockStatus(int lockStatus)
	{
		this.lockStatus = lockStatus;
	}

	/**
	 * 获取用户组名称。
	 * @return 用户组名称
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * 设置用户组名称。
	 * @param name 用户组名称
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * 获取用户组描述。
	 * @return 用户组描述
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * 设置用户组描述。
	 * @param description 用户组描述
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * 获取用户组对应的角色列表。
	 * @return 角色列表
	 */
	public List<Integer> getListRoleId()
	{
		initRoles();
		return listRoleId;
	}

	/**
	 * 设置用户组对应的权限列表。
	 * @param functions 权限列表
	 */
	public void setListRoleId(List<Integer> listRoleId)
	{
		if(listRoleId != null)
		{
			initRoles();
			this.listRoleId.clear();
			this.listRoleId.addAll(listRoleId);
		}
		else
		{
			if(this.listRoleId != null)
			{
				this.listRoleId.clear();
			}
		}
	}

	/**
	 * 添加角色。
	 * @param role 角色id
	 */
	public void addRole(Integer role)
	{
		initRoles();
		if(this.listRoleId.indexOf(role) == -1)
		{
			this.listRoleId.add(role);
		}
	}

	/**
	 * 获取用户组对应的MML命令列表。
	 * @return MML命令列表
	 */
	public List<Integer> getMMLCommands()
	{
		initCommands();
		return commands;
	}

	/**
	 * 设置用户组对应的MML命令列表。
	 * @param commands MML命令列表
	 */
	public void setMMLCommands(List<Integer> commands)
	{
		if(commands != null)
		{
			initCommands();
			this.commands.clear();
			this.commands.addAll(commands);
		}
		else
		{
			if(this.commands != null)
			{
				this.commands.clear();
			}
		}
	}

	/**
	 * 添加MML命令。
	 * @param command MML命令id
	 */
	public void addMMLCommand(Integer command)
	{
		initCommands();
		if(this.commands.indexOf(command) == -1)
		{
			this.commands.add(command);
		}
	}

	/**
	 * 清除MML命令列表。
	 */
	public void clearMMLCommands()
	{
		if(this.commands != null)
		{
			this.commands.clear();
		}
	}

	/**
	 * 获取用户组对应的设备列表。
	 * @return 设备列表
	 */
	public List<Integer> getDevices()
	{
		initDevices();
		return devices;
	}

	/**
	 * 设置用户组对应的设备列表。
	 * @param devices 设备列表
	 */
	public void setDevices(List<Integer> devices)
	{
		if(devices != null)
		{
			initDevices();
			this.devices.clear();
			this.devices.addAll(devices);
		}
		else
		{
			if(this.devices != null)
			{
				this.devices.clear();
			}
		}
	}

	/**
	 * 添加设备。
	 * @param device 设备id
	 */
	public void addDevice(Integer device)
	{
		initDevices();
		this.devices.add(device);
	}

	/**
	 * 清除设备列表。
	 */
	public void clearDevices()
	{
		if(this.devices != null)
		{
			this.devices.clear();
		}
	}

	/**
	 * @return the listGroupId
	 */
	public List<Integer> getListGroupId()
	{
		initGroupId();
		return listGroupId;
	}

	/**
	 * @param listGroupId the listGroupId to set
	 */
	public void setListGroupId(List<Integer> listGroupId)
	{
		if(listGroupId != null)
		{
			initGroupId();
			this.listGroupId.clear();
			this.listGroupId.addAll(listGroupId);
		}
		else
		{
			if(this.listGroupId != null)
			{
				this.listGroupId.clear();
			}
		}
	}
	
	/**
	 * 检查区域id列表是否为空。
	 * @return 检查结果
	 */
	public boolean isDistrictEmpty()
	{
		return districts == null || districts.isEmpty();
	}

	/**
	 * 获取区域组包含的区域id列表。
	 * @return 区域id列表
	 */
	public List<Integer> getDistricts()
	{
		initDistricts();
		return this.districts;
	}

	/**
	 * 设置区域组包含的区域id列表。
	 * @param districts 区域id列表
	 */
	public void setDistricts(List<Integer> districts)
	{
		initDistricts();
		this.districts.clear();
		this.districts.addAll(districts);
	}

	/**
	 * UserGroup。
	 * @param UserGroup UserGroup
	 */
	public UserGroup copyFrom(UserGroup user)
	{
		setId(user.getId());
		setLockStatus(user.getLockStatus());
		setName(user.getName());
		setDescription(user.getDescription());
		setGroupType(user.getGroupType());
		setGroupLevel(user.getGroupLevel());
		setListGroupId(user.getListGroupId());
		setListRoleId(user.getListRoleId());
		setMMLCommands(user.getMMLCommands());
		setDevices(user.getDevices());
		setDistricts(user.getDistricts());
		setUserCount(user.getUserCount());
		return this;
	}

	/**
	 * 用户组类型，取值如下：
	 * <ul>
	 * <li>{@link #TYPE_NORMAL} 普通</li>
	 * <li>{@link #TYPE_SUPER} 超级</li>
	 * <li>{@link #TYPE_TOP} 顶级</li>
	 * </ul>
	 * @return the groupType
	 */
	public int getGroupType()
	{
		return groupType;
	}

	/**
	 * 用户组类型，取值如下：
	 * <ul>
	 * <li>{@link #TYPE_NORMAL} 普通</li>
	 * <li>{@link #TYPE_SUPER} 超级</li>
	 * <li>{@link #TYPE_TOP} 顶级</li>
	 * </ul>
	 * @param groupType the groupType to set
	 */
	public void setGroupType(int groupType)
	{
		this.groupType = groupType;
	}

	/**
	 * 用户组级别，取值如下：
	 * <ul>
	 * <li>{@link #LEVEL_CITY} 市级</li>
	 * <li>{@link #LEVEL_PROVINCE} 省级</li>
	 * </ul>
	 * @return the groupLevel
	 */
	public int getGroupLevel()
	{
		return groupLevel;
	}

	/**
	 * 用户组级别，取值如下：
	 * <ul>
	 * <li>{@link #LEVEL_CITY} 市级</li>
	 * <li>{@link #LEVEL_PROVINCE} 省级</li>
	 * </ul>
	 * @param groupLevel the groupLevel to set
	 */
	public void setGroupLevel(int groupLevel)
	{
		this.groupLevel = groupLevel;
	}

	/**
	 * 获取用户总数。
	 * @return 用户总数
	 */
	public int getUserCount()
	{
		return userCount;
	}

	/**
	 * 设置用户总数。
	 * @param userCount 用户总数
	 */
	public void setUserCount(int userCount)
	{
		this.userCount = userCount;
	}
}
