import com.github.rami_sabbagh.codeforces.api.Codeforces;
import com.github.rami_sabbagh.codeforces.api.objects.User;

import java.time.Duration;

public class Main {
    public static void main(String[] args) {
        Codeforces cf = new Codeforces();
        cf.timeout = Duration.ofSeconds(30);

        System.out.println("Requesting user information...");
        try {
            User[] users = cf.requestUsersInformation("Rami_Sabbagh;YamanQD");
            System.out.println("Got the data back!");

            for (User user : users) {
                System.out.println(user.toStringPretty());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
