import com.revature.versusapp.data.ORM;
import com.revature.versusapp.models.Person;

public class VersusAppDriver {
	private static ORM ORM = new ORM();
	public static void main (String[] args) {
		Person user = new Person("Yujiin", "1234", "Evgeniy", "Ko");
		//ORM.create(user);
		Person test = new Person(4);
		test = (Person) ORM.findById(test);
	}
	
	private static void setupVersusLib() {
		
	}
}
