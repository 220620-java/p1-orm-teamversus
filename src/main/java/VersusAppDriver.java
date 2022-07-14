import com.revature.versusapp.data.ORM;
import com.revature.versusapp.models.Person;

public class VersusAppDriver {
	private static ORM ORM = new ORM();
	public static void main (String[] args) {
//		Person user = new Person("Evgeniy", "1234", "Evgeniy", "Ko");
//		ORM.create(user);
		Person test = new Person(1, "asdfasdf", "fdsadf", "fdsadfasdf", "123123123");
//		ORM.delete(test);
		ORM.update(test);
		System.out.println(ORM.findById(test));
	}
	
}
