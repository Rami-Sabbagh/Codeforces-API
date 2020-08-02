import com.github.rami_sabbagh.codeforces.api.Codeforces;
import com.github.rami_sabbagh.codeforces.api.objects.User;

public class Main {
    public static void main(String[] args) {
        Codeforces cf = new Codeforces();

        System.out.println("Requesting user information...");
        try {
            User[] users = cf.requestUsersInformation("Rami_Sabbagh");
            System.out.println("Got the data back!");

            for (User user : users) {
                System.out.println();
                System.out.println("===[ " + user.handle + " ]===");
                System.out.println("user.firstName = " + user.firstName);
                System.out.println("user.lastName = " + user.lastName);
                System.out.println("user.lastOnlineTimeSeconds = " + user.lastOnlineTimeSeconds);
                System.out.println("user.rating = " + user.rating);
                System.out.println("user.rank = " + user.rank);
                System.out.println("user.email = " + user.email);
                System.out.println("user.avatar = " + user.avatar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
