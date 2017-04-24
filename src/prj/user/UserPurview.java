/*
 * Copyright 2009 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */
package prj.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 用户权限。
 * @author dangzerong
 */
public class UserPurview
{
	//系统管理相关操作(17100 - 17199)
	/**
	 * 用户管理
	 */
	public static final int System_UserMgmt = 17100;
	/**
	 * 新增用户
	 */
	public static final int System_AddUser = 17101;
	/**
	 * 修改用户
	 */
	public static final int System_ModifyUser = 17102;
	/**
	 * 删除用户
	 */
	public static final int System_DeleteUser = 17103;
	/**
	 * 重置用户密码
	 */
	public static final int System_ResetPassword = 17104;
	/**
	 * 用户组管理
	 */
	public static final int System_UserGroupMgmt = 17105;
	/**
	 * 添加用户组
	 */
	public static final int System_AddUserGroup = 17106;
	/**
	 * 修改用户组
	 */
	public static final int System_ModifyUserGroup = 17107;
	/**
	 * 删除用户组
	 */
	public static final int System_DeleteUserGroup = 17108;
	/**
	 * 锁定用户组
	 */
	public static final int System_LockUserGroup = 17109;
	/**
	 * 修改个人信息
	 */
	public static final int System_ModifyUserInfo = 17110;
	/**
	 * 软件升级
	 */
	public static final int System_SoftwareUpdate = 17111;
	/**
	 * 告警门限设置
	 */
	public static final int System_AlarmThreshold = 17112;
	/**
	 * 保存偏好设置
	 */
	public static final int System_SavePreferences = 17118;
	//告警相关操作(17000 - 17099)
	/**
	 * 告警转发配置
	 */
	public static final int Alarm_TransmitterConfig = 17001;
	/**
	 * 告警定义配置
	 */
	public static final int Alarm_DefineConfig = 17002;
	/**
	 * 告警过滤配置
	 */
	public static final int Alarm_FilterConfig = 17003;
	/**
	 * 告警自动确认配置
	 */
	public static final int Alarm_AffirmConfig = 17004;
	/**
	 * 告警自动清除配置
	 */
	public static final int Alarm_ClearConfig = 17005;
	/**
	 * 频繁告警判断条件配置
	 */
	public static final int Alarm_FreqentConfig = 17006;
	/**
	 * 告警声音配置
	 */
	public static final int Alarm_SoundConfig = 17007;
	/**
	 * 告警屏蔽
	 */
	public static final int Alarm_Shield = 17008;
	/**
	 * 衍生告警屏蔽
	 */
	public static final int Alarm_DerivedAlarmShield = 17009;
	/**
	 * 告警忽略
	 */
	public static final int Alarm_Ignore = 17010;
	/**
	 * 告警定制
	 */
	public static final int Alarm_Customize = 17011;
	/**
	 * 告警注释
	 */
	public static final int Alarm_Comment = 17032;
	/**
	 * 告警打印
	 */
	public static final int Alarm_Print = 17033;
	/**
	 * 告警解决方案
	 */
	public static final int Alarm_Advice = 17034;
	/**
	 * 告警超时
	 */
	public static final int Alarm_TimeOut = 17035;
	/**
	 * 告警依赖
	 */
	public static final int Alarm_Depend = 17036;
	/**
	 * 告警清除
	 */
	public static final int Alarm_Clear = 17037;
	/**
	 * 告警确认
	 */
	public static final int Alarm_Affirm = 17038;
	/**
	 * 获取登录前告警
	 */
	public static final int Alarm_GetHistoricalAlarms = 17039;
	/**
	 * 获取实时告警
	 */
	public static final int Alarm_GetRealtimeAlarms = 17040;
	//设备相关操作(17400 - 17499)
	/**
	 * 添加BBU设备
	 */
	public static final int Device_AddBBU = 17401;
	/**
	 * 修改BBU设备
	 */
	public static final int Device_ModifyBBU = 17402;
	/**
	 * 删除BBU设备
	 */
	public static final int Device_DeleteBBU = 17403;
	/**
	 * BBU参数设置
	 */
	public static final int Device_SetBBUParams = 17404;
	/**
	 * BBU参数查询
	 */
	public static final int Device_GetBBUParams = 17405;
	/**
	 * 批量导入BBU设备
	 */
	public static final int Device_BatchImport = 17406;
	/**
	 * 批量更新BBU设备
	 */
	public static final int Device_BatchUpdate = 17407;
	/**
	 * 移动BBU设备
	 */
	public static final int Device_MoveTo = 17408;
	/**
	 * 添加RRU设备
	 */
	public static final int Device_AddRRU = 17409;
	/**
	 * 修改RRU设备
	 */
	public static final int Device_ModifyRRU = 17410;
	/**
	 * 删除RRU设备
	 */
	public static final int Device_DeleteRRU = 17411;
	/**
	 * 添加子网
	 */
	public static final int Device_AddDistrict = 17412;
	/**
	 * 修改子网
	 */
	public static final int Device_ModifyDistrict = 17413;
	/**
	 * 删除子网
	 */
	public static final int Device_DeleteDistrict = 17414;
	/**
	 * 显示MML控制台
	 */
	public static final int Device_MMLConsole = 17415;
	/**
	 * 复位单板
	 */
	public static final int Device_ResetBoard = 17416;
	/**
	 * 查询单板状态
	 */
	public static final int Device_QryBoardState = 17417;
	/**
	 * 查询当前告警
	 */
	public static final int Device_QryCurAlarm = 17418;
	/**
	 * 设置小区闭塞
	 */
	public static final int Device_SetCellBlock = 17419;
	//配置相关操作
	/**
	 * 新建配置集
	 */
	public static final int Config_NewConfigSet = 17420;
	/**
	 * 修改配置集
	 */
	public static final int Config_ModifyConfigSet = 17421;
	/**
	 * 删除配置集
	 */
	public static final int Config_DeleteConfigSet = 17422;
	/**
	 * 复制配置集
	 */
	public static final int Config_CloneConfigSet = 17423;
	/**
	 * 从配置集模板新建配置集
	 */
	public static final int Config_NewConfigSetFromTempl = 17424;
	/**
	 * 设置主配置集
	 */
	public static final int Config_SetMainConfigSet = 17425;
	/**
	 * 保存配置集为模板
	 */
	public static final int Config_SaveConfigSetAsTempl = 17426;
	/**
	 * 备份配置集
	 */
	public static final int Config_BackupConfigSet = 17427;
	/**
	 * 恢复配置集
	 */
	public static final int Config_RestoreConfigSet = 17428;
	/**
	 * 同步配置集
	 */
	public static final int Config_SyncConfigSet = 17429;
	/**
	 * 配置集一致性检查
	 */
	public static final int Config_ValidateConfigSet = 17430;
	/**
	 * 新建配置集模板
	 */
	public static final int Config_NewConfigSetTempl = 17431;
	/**
	 * 修改配置集模板
	 */
	public static final int Config_ModifyConfigSetTempl = 17432;
	/**
	 * 删除配置集模板
	 */
	public static final int Config_DeleteConfigSetTempl = 17433;
	/**
	 * 复制配置集模板
	 */
	public static final int Config_CloneConfigSetTempl = 17434;
	/**
	 * 显示软件视图
	 */
	public static final int Config_ShowSoftwareView = 17435;
	/**
	 * 激活软件
	 */
	public static final int Config_ActivateSoftware = 17436;
	/**
	 * 下载软件
	 */
	public static final int Config_DownloadSoftware = 17437;
	/**
	 * 配置集批处理视图
	 */
	public static final int Config_ConfigSetBatchView = 17438;
	/**
	 * 软件批处理视图
	 */
	public static final int Config_SoftwareBatchView = 17439;
	//批处理相关操作(41-50)
	//性能管理任务(51-60)
	//日志管理相关操作(17200 - 17299)
	/**
	 * 告警日志
	 */
	public static final int Log_AlarmQry = 17201;
	/**
	 * 告警统计
	 */
	public static final int Log_AlarmStat = 17202;
	/**
	 * 安全日志
	 */
	public static final int Log_Security = 17203;
	/**
	 * 用户操作日志
	 */
	public static final int Log_Operation = 17204;
	/**
	 * 网元状态变更日志
	 */
	public static final int Log_StateChange = 17205;
	/**
	 * OMC运行日志
	 */
	public static final int Log_OmcRunning = 17206;
	/**
	 * 网元上报日志
	 */
	public static final int Log_Report = 17207;
	/**
	 * 日志定制
	 */
	public static final int Log_Customization = 17208;
	/**
	 * BBU历史日志
	 */
	public static final int Log_BBUHistory = 17209;
	/**
	 * RRU历史日志
	 */
	public static final int Log_RRUHistory = 17210;
	//网优网规相关操作(17600 - 17699)
	/**
	 * 修改地物衰减值
	 */
	public static final int NetPlan_ModifyClutterLoss = 17601;
	//工单相关操作
	/**
	 * 工单管理
	 */
	public static final int Workpaper_Management = 0;
	/**
	 * 视图权限 - 告警视图。
	 */
	public static final int View_Alarm = 117593;
	/**
	 * 视图权限 - 拓扑视图。
	 */
	public static final int View_Topo = 117594;
	/**
	 * 视图权限 - 配置视图。
	 */
	public static final int View_Config = 117595;
	/**
	 * 视图权限 - 文件视图。
	 */
	public static final int View_File = 117596;
	/**
	 * 视图权限 - 管理树视图。
	 */
	public static final int View_Manage = 117747;
	/**
	 * 视图权限 - 地图视图。
	 */
	public static final int View_Map = 117597;
	/**
	 * 视图权限 - 开站场景视图。
	 */
	public static final int View_OpenScene = 117598;
	/**
	 * 视图权限 - 系统状态视图。
	 */
	public static final int View_System = 117599;
	/**
	 * 视图权限 - 单板状态上报视图。
	 */
	public static final int View_BoardState = 117600;
	/**
	 * 视图权限 - 起始页视图。
	 */
	public static final int View_Dashboard = 117601;
	/**
	 * 视图权限 - 群组视图。
	 */
	public static final int View_Cluster = 117632;
	/**
	 * 告警关注
	 */
	public static final int View_AlarmAtt = 117682;
	/**
	 * 用户权限管理单例对象。
	 */
	private static UserPurview instance;
	/**
	 * 所有用户权限对象MAP。
	 */
	private Map<Integer, Purview> purviews;
	/**
	 * 当前登录用户权限信息。
	 */
	private List<Integer> userPurviews;

	/**
	 * 获取用户权限管理单例对象。
	 * @return 用户权限管理对象
	 */
	public synchronized static UserPurview getInstance()
	{
		if(instance == null)
		{
			instance = new UserPurview();
		}
		return instance;
	}
	
	/**
	 * 初始化用户权限。
	 * @param listPurview 权限id列表
	 */
	public void initPurview(List<Integer> listPurview)
	{
		if(userPurviews == null)
		{
			userPurviews = new ArrayList<Integer>();
		}
		else
		{
			userPurviews.clear();
		}
		if(listPurview != null && !listPurview.isEmpty())
		{
			userPurviews.addAll(listPurview);
		}
	}

	/**
	 * 初始化缓存。
	 */
	private void initialize()
	{
		if(purviews == null)
		{
			purviews = new TreeMap<Integer, Purview>();
//			List<SystemItem> listItem = OmcDictionary.getInstance().sys_getItemsByType(OmcDictionary.userPurview);
//			if(listItem != null)
//			{
//				for(SystemItem item : listItem)
//				{
//					Purview purview = new Purview();
//					purview.setId(item.getItemId());
//					purview.setGroup(item.getItemAlias());
//					purview.setName(item.getItemName());
//					purviews.put(item.getItemId(), purview);
//				}
//			}
		}
	}
	
	/**
	 * 清除资源。
	 */
	public void clear()
	{
		if(purviews != null)
		{
			purviews.clear();
			purviews = null;
		}
		if(userPurviews != null)
		{
			userPurviews.clear();
			userPurviews = null;
		}
	}

	/**
	 * 判断用户是否具有指定权限。
	 * @param id 权限id
	 * @return 判断结果
	 */
	public boolean hasPurview(int id)
	{
		getUserPurviews();
		return userPurviews.contains(id);
	}

	/**
	 * 获取指定权限的信息。
	 * @param id 权限id
	 * @return 权限对象
	 */
	public Purview getPurview(int id)
	{
		initialize();
		return purviews.get(id);
	}

	/**
	 * 获取所有权限模型信息。
	 * @return 权限对象列表
	 */
	public Map<Integer, Purview> getAllPurviews()
	{
		initialize();
		return purviews;
	}

	/**
	 * 获取当前用户具有的权限列表。
	 * @return 权限id列表
	 */
	private List<Integer> getUserPurviews()
	{
		if(userPurviews == null)
		{
			userPurviews = new ArrayList<Integer>(0);
//			ProcessData pd =
//					OmcProcessor.process(Command.user, Command.getUserFunctions, null);
//			if(pd.getData() != null)
//			{
//				userPurviews.addAll((List)pd.getData());
//			}
		}

		return userPurviews;
	}
}
