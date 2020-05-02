package Api.StepDefs;

//import org.testng.annotations.AfterMethod;
//import org.testng.annotations.BeforeMethod;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {

	@Before
	public void beforeHooks() {
		System.out.println("INSIDE BEFORE HOOKS NOW!!");
	}	
	
	@Before("@pokeapi")
	public void beforePokeAPIHooks() {
		System.out.println("INSIDE BEFORE HOOKS for pokeapi!!");
	}
	
	
	@After
	public void afterHooks() {
		System.out.println("INSIDE AFTER HOOKS NOW!!");
	}
		
	@After("@reqre")
	public void afterReqreHooks() {
		System.out.println("INSIDE AFTER HOOKS for reqre!!");
	}
	
}
