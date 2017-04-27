/*
 * Copyright 2010 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */
package util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import prj.PrjApp;

/**
 * 用户工具类。
 * @author chenwei
 * Created on 2017-4-23, 8:37:22
 */
public class UserUtil
{
	public Map<Integer, String> actMaps = new HashMap<Integer, String>();
	public String getActName(Integer actId)
	{
		if(actMaps.size()==0)
		{
			SQLiteCRUD sqlOpt = PrjApp.getApplication().getSQLiteCRUD();
			List<List> grpresults = sqlOpt.select("user_action", new String[]{"id","fullpath","actionname","actiondesc"});
			
		}
		return actMaps.get(actId);
	}
	
}
