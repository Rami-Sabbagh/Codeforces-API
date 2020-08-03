import com.github.rami_sabbagh.codeforces.api.CodeforcesClient;
import com.github.rami_sabbagh.codeforces.api.objects.User;

public class Main {
    public static void main(String[] args) {
        CodeforcesClient cf = CodeforcesClient.newBuilder().build();

        System.out.println("Requesting users information...");
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
