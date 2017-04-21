/*
 * Copyright 2015 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

package util;

import java.sql.*;  
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
  
public class SQLiteCRUD {  
    private Connection connection ;  
          
    public SQLiteCRUD(Connection connection) {    
        this.connection = connection ;    
    }   
      
    /**  
     * @param sql  
     * @return boolean  
     */    
    public boolean createTable(String sql){    
        Statement stmt = null ;    
        try{    
            stmt = this.connection.createStatement() ;   
            stmt.executeUpdate(sql) ;    
            return true ;    
        }catch (Exception e) {    
            System.out.println("create table: " + e.getLocalizedMessage());    
            connectionRollback(connection) ;    
            return false ;    
        }    
    }  
      
    /**  
     * insert一条多个字段值的数据 
     * @param table 表名 
     * @param params 多个字段值 
     * @return boolean  
     */    
    public boolean insert(String table, String[] params){    
        Statement stmt = null ;    
        String sql = "insert into " + table  + " values('";    
        for(int i = 0 ; i < params.length ;i++){    
            if(i == (params.length - 1)){    
                sql += (params[i] + "');") ;    
            }else{    
                sql += (params[i] + "', '") ;    
            }    
        }    
        System.out.println(sql);    
        try{    
            stmt = this.connection.createStatement() ;    
            stmt.executeUpdate(sql) ;  
            if(!connection.isClosed()){  
                connection.close();  
            }  
            return true ;    
        }catch (Exception e) {    
            System.out.println("" + table + ": " + e.getLocalizedMessage());    
            connectionRollback(connection) ;    
            return false ;    
        }    
    }    
        
    /**  
     * @param table  
     * @param keyParam  
     * @param keyField   
     * @param fields   
     * @param params   
     * @return boolean  
     */    
    public boolean update(String table, String keyParam, String keyField, String[] fields, String[] params){    
        Statement stmt = null ;    
        String sql = "update " + table + " set " ;    
        for(int i = 0 ; i < fields.length ; i++){    
            if(i == (fields.length - 1)){    
                sql += (fields[i] + "='" + params[i] + "' where " + keyField + "='" + keyParam +"';") ;    
            }else{    
                sql += (fields[i] + "='" + params[i] + "', ") ;    
            }    
        }    
        System.out.println(sql);    
        try{    
            stmt = this.connection.createStatement() ;    
            stmt.executeUpdate(sql) ;    
            return true ;    
        }catch (Exception e) {    
            System.out.println( e.getLocalizedMessage());    
            connectionRollback(connection) ;    
            return false ;    
        }    
            
    }    
        
    /**  
     * @param table  
     * @param key  
     * @param keyValue  
     * @return boolean  
     */    
    public boolean delete(String table, String key, String keyValue){    
        Statement stmt = null ;    
        String sql = "delete from " + table + " where " + key + "='" + keyValue + "';" ;    
        System.out.println(sql);    
        try{    
            stmt = this.connection.createStatement() ;    
            stmt.executeUpdate(sql) ;    
            return true ;    
        }catch (Exception e) {    
            System.out.println( e.getLocalizedMessage());    
            connectionRollback(connection) ;    
            return false ;    
        }    
    }
	
	public List<List> select(String table, String[] fields)
	{
		List<List> result = new ArrayList<List>();
        Statement stmt = null ;
		ResultSet rs =  null;
		StringBuffer sb = new StringBuffer();
		for(String s:fields)
		{
			sb.append(s+",");
		}
		if(fields.length>0)
		{
			sb.deleteCharAt(sb.length()-1);
		}
        String sql = "select "+sb.toString()+" from " + table;
        try{
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);
        }catch (Exception e) {
            System.out.println( e.getLocalizedMessage());    
            connectionRollback(connection) ;    
            return null; 
        }
		try
		{
			while(rs.next())
			{
				List<String> rowData = new ArrayList<String>();
				for(String str:fields)
				{
					rowData.add(rs.getString(str));
				}
				result.add(rowData);
			}
		}
		catch(SQLException ex)
		{
			Logger.getLogger(SQLiteCRUD.class.getName()).log(Level.SEVERE, null, ex);
		}
		return result;
	}
      
    private void connectionRollback(Connection connection){    
        try {    
            connection.rollback() ;    
        } catch (SQLException e) {    
            System.out.println(e.getLocalizedMessage()) ;    
        }    
    }    
}  
