package com.revature.versusapp.data;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.revature.versusapp.models.User;
import com.revature.versusapp.utils.ConnectionUtil;

public class UserORM implements UserDAO{
	private ConnectionUtil connUtil = ConnectionUtil.getConnectionUtil();
	@Override
	public User create(User newUser) {
		
		try (Connection conn = connUtil.getConnection()){
			conn.setAutoCommit(false);
			StringBuilder sql = new StringBuilder();
			Class objectClass = newUser.getClass();
			sql.append("insert into person values (");
			for(Field field : objectClass.getDeclaredFields()) {
				field.setAccessible(true);
				if (field.getName().equals("id")) {
					sql.append("default, ");
				} else if (field.getType().isPrimitive()) {
					sql.append(field.get(newUser) + ", ");
				} else {
					sql.append("'" + field.get(newUser) + "', ");
				}
			}
			sql.delete(sql.length()-2, sql.length());
			sql.append(");");
			System.out.println(sql);
			PreparedStatement stmt = conn.prepareStatement(sql.toString());
			int rowsAffected = stmt.executeUpdate();
			ResultSet resultSet = stmt.getGeneratedKeys();
			if (resultSet.next() && rowsAffected == 1) {
				newUser.setId(resultSet.getInt("id"));
				conn.commit();
			} else {
				conn.rollback();
				return null;
			}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return newUser;
	}

	@Override
	public User findById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(User t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(User t) {
		// TODO Auto-generated method stub
		
	}

}
