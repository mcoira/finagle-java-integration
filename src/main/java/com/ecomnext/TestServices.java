package com.ecomnext;

import com.ecomnext.clients.SecurityServiceClient;
import com.ecomnext.domain.User;

import java.util.ArrayList;
import java.util.List;

/**
 * We use this class as example of service client use.
 * We must run SecurityServiceServer first and then we can run this class.
 */
public class TestServices {
    public static void main(String[] args) {
        SecurityServiceClient client = new SecurityServiceClient("localhost", 7910, 1);

        System.out.println("getUser() 10 times");
        // call getUser 10 times to see how we receive null values for those that don't exist
        // and good users for those who exist.
        for (int i = 0; i < 10; i++) {
            User user = client.getUser("myuser" + i, 2000);
            if (user == null) {
                System.out.println(String.format("User%d doesn't exist", i));
            } else {
                System.out.println(user);
            }
        }

        System.out.println("\ngetUsers()");
        List<User> users = client.getUsers(2000);
        for (User user : users) {
            System.out.println(user);
        }

        System.out.println("\ncountUsersByRole()");
        List<String> roles = new ArrayList<String>();
        roles.add("ROLE_ADMIN");
        int count = client.countUsersByRole(true, roles, 2000);
        System.out.println(count);

        System.out.println("\ncleanUpOldUsers");
        client.cleanUpOldUsers(2000);

        client.close();
    }
}
