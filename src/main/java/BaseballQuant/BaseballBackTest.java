package BaseballQuant;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class BaseballBackTest {

    public static void main(String[] args) {


        System.out.println(checkIfDatabaseIsInLocation());
        SpringApplication.run(BaseballBackTest.class, args);


    }

    public static boolean checkIfDatabaseIsInLocation(){
        return false;
    }
}
