package com.revature.versusapp.data;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.revature.versusapp.models.Person;
import com.revature.versusapp.utils.ConnectionUtil;

public class ORM implements DataAccessObject<Object> {
	private ConnectionUtil connUtil = ConnectionUtil.getConnectionUtil();
	@Override
	public Object create(Object object) {
		try (Connection conn = connUtil.getConnection()){
			conn.setAutoCommit(false);
			StringBuilder sql = new StringBuilder();
			Class objectClass = object.getClass();
			sql.append("insert into " + objectClass.getSimpleName() + " values (");
			for(Field field : objectClass.getDeclaredFields()) {
				field.setAccessible(true);
				if (field.getName().equals("id")) {
					sql.append("default, ");
				} else if (field.getType().isPrimitive()) {
					sql.append(field.get(object) + ", ");
				} else {
					sql.append("'" + field.get(object) + "', ");
				}
			}
			sql.delete(sql.length()-2, sql.length());
			sql.append(");");
			System.out.println(sql);
			PreparedStatement stmt = conn.prepareStatement(sql.toString());
			int rowsAffected = stmt.executeUpdate();
			ResultSet resultSet = stmt.getGeneratedKeys();
			if (resultSet.next() && rowsAffected == 1) {
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
		
		return object;
	}
	
	public String getSnakeCase(String string) {
		StringBuilder temp = new StringBuilder();
		for(char character : string.toCharArray()) {
			if (Character.isUpperCase(character)) {
				temp.append("_" + character);
			} else {
				temp.append(character);
			}
		}
		return temp.toString();
	}
	
	@Override
	public Object findById(Object object) {
		try (Connection conn = connUtil.getConnection()) {
			conn.setAutoCommit(false);
			StringBuilder sql = new StringBuilder();
			Class objectClass = object.getClass();
			Field id = objectClass.getDeclaredField("id");
			id.setAccessible(true);
			
			sql.append("select * from " + objectClass.getSimpleName() + " where id=" + id.get(object));
			System.out.println(sql);
			PreparedStatement stmt = conn.prepareStatement(sql.toString());
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				for (Field fields : objectClass.getDeclaredFields()) {
					fields.setAccessible(true);
					String fieldName = getSnakeCase(fields.getName());
					fields.set(object, resultSet.getObject(fieldName));
					
				}
				
			}	
			
		} catch (SQLException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		} 
		return object;
	}

	@Override
	public List<Object> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Object object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Object object) {
		// TODO Auto-generated method stub
		
	}

}
