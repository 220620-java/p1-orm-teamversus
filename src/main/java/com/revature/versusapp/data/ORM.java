package com.revature.versusapp.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.revature.versusapp.utils.ConnectionUtil;

public class ORM implements DataAccessObject<Object> {
	private ConnectionUtil connUtil = ConnectionUtil.getConnectionUtil();
	@Override
	public Object create(Object object,Class<?> type) {
		try (Connection conn = connUtil.getConnection()){
			conn.setAutoCommit(false);
			StringBuilder sql = new StringBuilder();
			Class objectClass = object.getClass();
			sql.append("insert into " + objectClass.getSimpleName() + " values (");
			Annotation primaryKey = (Annotation) type.getAnnotation(PrimaryKey.class);
			System.out.println(primaryKey.getClass().toGenericString());
			
			String[] strArray = (String[])primaryKey.getClass().getDeclaredMethod("name").invoke(primaryKey);
			for(Field field : objectClass.getDeclaredFields()) {
				field.setAccessible(true);
				if (field.getName().equals(strArray[0])) {
					sql.append("default, ");
				} else if (field.getType().isPrimitive()) {
					sql.append(field.get(object) + ", ");
				} else {
					sql.append("'" + field.get(object) + "', ");
				}
			}
			sql.delete(sql.length()-2, sql.length());
			sql.append(");");
			String[] keys = null;
            try {
                keys = (String[]) primaryKey.getClass().getDeclaredMethod("name").invoke(primaryKey);
                
                System.out.println("in ORM create keys  = " + Arrays.toString(keys));
            } catch (InvocationTargetException | NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println(sql.toString());
			PreparedStatement stmt = conn.prepareStatement(sql.toString(), keys);
			int rowsAffected = stmt.executeUpdate();
			ResultSet resultSet = stmt.getGeneratedKeys();
			if (resultSet.next() && rowsAffected == 1) {
				System.out.println(rowsAffected);
				for (String key : strArray ) {
					Field field = objectClass.getDeclaredField(key);
					field.setAccessible(true);
					field.set(object, resultSet.getInt(key));
				}
				conn.commit();
			} else {
				conn.rollback();
				return null;
			}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return null;
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (NoSuchMethodException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
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

	@SuppressWarnings("unchecked")
    @Override
	public List<Object> findAll(Class<?> type) {
		List<Object> allRecords = new ArrayList<>();
		
		try (Connection conn = connUtil.getConnection()){
			conn.setAutoCommit(false);
			StringBuilder sql = new StringBuilder();
			Annotation primaryKey = (Annotation) type.getAnnotation(PrimaryKey.class);
			sql.append("select * from " + type.getSimpleName());
			String[] keys = (String[]) primaryKey.getClass().getDeclaredMethod("name").invoke(primaryKey);
			PreparedStatement stmt = conn.prepareStatement(sql.toString(), keys);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				Object newObject = type.getConstructor().newInstance();
				for (Field field : type.getDeclaredFields()) {
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
			PrimaryKey primaryKey = (PrimaryKey) objectClass.getAnnotation(PrimaryKey.class);
			sql.append("update " + objectClass.getSimpleName() + " set ");
			for(Field field : objectClass.getDeclaredFields()) {
				field.setAccessible(true);
				if (field.getName().equals(primaryKey.name()[0])) {
				} else if (field.getType().isPrimitive()) {
					sql.append(getSnakeCase(field.getName()) + "=" + field.get(object) + ", ");
				} else {
					sql.append(getSnakeCase(field.getName()) + "=" + "'" + field.get(object) + "', ");
				}
			}
			sql.delete(sql.length()-2, sql.length());
			sql.append(" where ");
			for (String key : primaryKey.name()) {
				Field field = objectClass.getDeclaredField(key);
				System.out.println(field.getName());
				field.setAccessible(true);
				sql.append(key + "=" + field.get(object));
			}

			String[] keys = primaryKey.name();
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
			sql.append("delete from " + objectClass.getSimpleName() + " where ");
			PrimaryKey primaryKey = (PrimaryKey) objectClass.getAnnotation(PrimaryKey.class);
//			if (primaryKey.equals(null)) {
//				for (Field field : objectClass.getDeclaredFields()) {
//					field.setAccessible(true);
//					if (field.get(object) != null ) {
//					    sql.append(field.getName() + " = " + field.get(object) + ", ");
//					}
//				}
//				sql.delete(sql.length()-2, sql.length());
//			} else {
                for (Field field : objectClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.get(object) != null && (int) field.get(object) != 0 ) {
                        sql.append(getSnakeCase( field.getName()) + " = " + field.get(object) + " and ");
                    }
                }
                sql.delete(sql.length()-4, sql.length());
//				for (String key : primaryKey.name()) {
//					Field field = objectClass.getDeclaredField(key);
//					System.out.println(field.getName());
//					field.setAccessible(true);
//					if (field.get(object) != null ) {
//					    sql.append(key + "=" + field.get(object));
//					}
//				}
//			}
			
			System.out.println(sql.toString());
			Statement stmt = conn.createStatement();
			int rowsAffected = stmt.executeUpdate(sql.toString());
			conn.commit();
		} catch (SQLException  | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}

}
