import com.revature.versusapp.data.ORM;
import com.revature.versusapp.models.Person;

public class VersusAppDriver {
	private static ORM ORM = new ORM();
	public static void main (String[] args) throws NoSuchFieldException, SecurityException {
//		Person user = new Person("kevgeniy", "1234", "Evgeniy", "Ko");
//		Person person = (Person) ORM.create(user);
//		System.out.println(person);
//		Person test = new Person(100, "mbollinger", "123456", "Michael", "Bollinger");
//		ORM.delete(test);
//		ORM.update(test);
//		
//		System.out.println(ORM.findById(test));
//		
//		Person person = new Person();
//		Class objectClass = person.getClass();
//		
//		PrimaryKey primaryKey = (PrimaryKey) objectClass.getAnnotation(PrimaryKey.class);
//		System.out.println(primaryKey);
//		for (String key : primaryKey.name()) {
//			Field field = objectClass.getDeclaredField(key);
//			System.out.println(field.getName());
//			field.setAccessible(true);
//		}
//		ORM.delete(person);
		Person person = new Person("asdf", "asdf", "asdf", "asdf");
		//person = (Person) ORM.create(person);
		System.out.println(person);
		ORM.delete(person);
	}
	
}
