import com.revature.versusapp.data.ORM;
import com.revature.versusapp.models.Person;

public class VersusAppDriver {
	private static ORM ORM = new ORM();
	public static void main (String[] args) {
		Person user = new Person("kevgeniy", "1234", "Evgeniy", "Ko");
		Person person = (Person) ORM.create(user);
		System.out.println(person);
		Person test = new Person(100, "mbollinger", "123456", "Michael", "Bollinger");
//		ORM.delete(test);
//		ORM.update(test);
		
		System.out.println(ORM.findById(test));
	}
	
}
