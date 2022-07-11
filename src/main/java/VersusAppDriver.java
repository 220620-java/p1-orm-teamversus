import com.revature.versusapp.data.UserORM;
import com.revature.versusapp.models.User;

public class VersusAppDriver {
	private static UserORM userORM = new UserORM();
	public static void main (String[] args) {
		User user = new User(1, "Yujiin", "1234", "Evgeniy", "Ko");
		userORM.create(user);
	}
	
	private static void setupVersusLib() {
		
	}
}
