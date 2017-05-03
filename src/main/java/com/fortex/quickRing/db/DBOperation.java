package com.fortex.quickRing.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;

import com.fortex.lib.globalservices.GlobalRuntime;
 
import com.fortex.simulator.utils.ConfigSetting;
import com.fortex.simulator.utils.FxTableModel;

public class DBOperation {
	private static BasicDataSource dataSource;
	static {
		dataSource = new BasicDataSource();
		dataSource.setDriverClassName(ConfigSetting.getDatabaseProperty("driverClass"));
		dataSource.setUrl(ConfigSetting.getDatabaseProperty("url"));
		dataSource.setUsername(ConfigSetting.getDatabaseProperty("username"));
		dataSource.setPassword(ConfigSetting.getDatabaseProperty("password"));
		dataSource.setValidationQuery(ConfigSetting.getDatabaseProperty("validationQuery"));
		dataSource.setInitialSize(5);
	}
	
	/**
	 * 
	 * <p>Description:Check if the account exists and the password match</p> 
	 *
	 * @author Patrick Chi
	 * @date 2016-08-05 
	 * @param username
	 * @param password
	 * @return
	 * @throws SQLException
	 */
	public static boolean accountIsExist(String username, String password, String targetId, int loginType) throws SQLException {
		Connection conn = dataSource.getConnection();
		String hashUserName= GlobalRuntime.hashPwd(username);
		String hashPassword= GlobalRuntime.hashPwd(password);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try{
			stmt = conn.prepareStatement("select count(1) from loginTable where LoginHash = ? and PasswordHash = ? and TargetID=? and loginType=?");
			stmt.setString(1, hashUserName);
			stmt.setString(2, hashPassword);
			stmt.setString(3, targetId);
			stmt.setInt(4, loginType);
			rs = stmt.executeQuery();
			int num = 0;
			if(rs.next()) {
				num = rs.getInt(1);
			}
			return num > 0;
		}finally{
			close(rs,stmt,conn);
		}
	}
	
	/**
	 * <p>Description:Get domain by account.</p>
	 * @author Ivan Huo
	 * @date 2016-08-25	
	 * @return
	 * @throws SQLException
	 */ 
	public static int getDomainByAccount(String account) throws SQLException {
		Connection conn = dataSource.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement("SELECT domain FROM LoginTable WHERE login = ?");
			stmt.setString(1, account);
			rs = stmt.executeQuery();
			int domain = 0;
			if (rs.next())
				domain = rs.getInt(1);
			return domain;
		} finally {
			close(rs,stmt,conn);
		}
	}
	
	
	/**
	 * <p>Description:Get all symbols from database.</p>
	 * @author Ivan Huo
	 * @date 2016-08-25	
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, FxTableModel> getSymbols() throws SQLException {
		Map<String, FxTableModel> symbols = new HashMap<String, FxTableModel>(); 
		Connection conn = dataSource.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement("SELECT FXSymbol,DECIMAL FROM fxtable");			
			rs = stmt.executeQuery();				 
 			while(rs.next()){
 				FxTableModel model = new FxTableModel();
 				model.setSymbol(rs.getString("FXSymbol"));
 				if(model.getSymbol() != null && model.getSymbol().startsWith("#"))
 					model.setSymbol(model.getSymbol().substring(1));
 				model.setDecimal(rs.getInt("DECIMAL"));
				symbols.put(model.getSymbol(), model);
			}
			return symbols;
		} finally {
			close(rs,stmt,conn);
		}
	}
	
	/**
	 * <p>Description:close ResultSet,Statement and Connection.</p>
	 * @author Ivan Huo
	 * @date 2016-08-25	
	 * @return
	 * @throws SQLException
	 */
	private static void close(ResultSet rs,Statement stmt,Connection conn) {
		if(rs != null)
			try {rs.close();} catch (SQLException e) {}
		if(stmt != null)
			try {stmt.close();} catch (SQLException e) {}
		if(conn != null)
			try {conn.close();} catch (SQLException e) {}
	}
}

