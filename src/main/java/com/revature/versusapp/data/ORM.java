package com.revature.versusapp.data;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.revature.versusapp.models.Person;
import com.revature.versusapp.models.PrimaryKey;
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
			String[] keys = {"id"};
			PreparedStatement stmt = conn.prepareStatement(sql.toString(), keys);
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

			sql.append("select * from " + objectClass.getSimpleName() + " where ");
			PrimaryKey primaryKey = (PrimaryKey) objectClass.getAnnotation(PrimaryKey.class);
			for (String key : primaryKey.name()) {
				Field field = objectClass.getDeclaredField(key);
				field.setAccessible(true);
				sql.append(key + "=" + field.get(object));
			}
			
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
	public List<Object> findAll(Object object) {
		List<Object> allRecords = new ArrayList<>();
		
		try (Connection conn = connUtil.getConnection()){
			conn.setAutoCommit(false);
			StringBuilder sql = new StringBuilder();
			Class objectClass = object.getClass();
			sql.append("select * from " + objectClass.getSimpleName());
			String[] keys = {"id"};
			PreparedStatement stmt = conn.prepareStatement(sql.toString(), keys);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				Object newObject = objectClass.getConstructor().newInstance();
				for (Field field : objectClass.getDeclaredFields()) {
					field.setAccessible(true);
					String columnName = getSnakeCase(field.getName());
					field.set(newObject, resultSet.getObject(columnName));
				}
				allRecords.add(newObject);
			}

			
		} catch (SQLException | SecurityException | IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		} 
		
		return allRecords;
	}

	@Override
	public void update(Object object) {
		try (Connection conn = connUtil.getConnection()) {
			conn.setAutoCommit(false);
			StringBuilder sql = new StringBuilder();
			Class objectClass = object.getClass();
			Field id = objectClass.getDeclaredField("id");
			id.setAccessible(true);

			sql.append("update " + objectClass.getSimpleName() + " set ");
			for(Field field : objectClass.getDeclaredFields()) {
				field.setAccessible(true);
				if (field.getName().contains("id")) {
				} else if (field.getType().isPrimitive()) {
					sql.append(getSnakeCase(field.getName()) + "=" + field.get(object) + ", ");
				} else {
					sql.append(getSnakeCase(field.getName()) + "=" + "'" + field.get(object) + "', ");
				}
			}
			sql.delete(sql.length()-2, sql.length());
			sql.append(" where ");
			PrimaryKey primaryKey = (PrimaryKey) objectClass.getAnnotation(PrimaryKey.class);
			for (String key : primaryKey.name()) {
				Field field = objectClass.getDeclaredField(key);
				System.out.println(field.getName());
				field.setAccessible(true);
				sql.append(key + "=" + field.get(object));
			}

			String[] keys = {"id"};
			PreparedStatement stmt = conn.prepareStatement(sql.toString(), keys);
			int rowsAffected = stmt.executeUpdate();
			ResultSet resultSet = stmt.getGeneratedKeys();
			if (resultSet.next() && rowsAffected == 1) {
				conn.commit();
			} else {
				conn.rollback();
			}
			
		} catch (SQLException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		} 
		
	}

	@Override
	public void delete(Object object) {
		try (Connection conn = connUtil.getConnection()) {
			conn.setAutoCommit(false);
			StringBuilder sql = new StringBuilder();
			Class objectClass = object.getClass();
			Field id = objectClass.getDeclaredField("id");
			id.setAccessible(true);
			sql.append("delete from " + objectClass.getSimpleName() + " where id=" + id.get(object));
			Statement stmt = conn.createStatement();
			int rowsAffected = stmt.executeUpdate(sql.toString());
			conn.commit();
		} catch (SQLException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}

}
