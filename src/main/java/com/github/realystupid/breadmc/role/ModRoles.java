package com.github.realystupid.breadmc.role;

import java.util.ArrayList;
import java.util.List;

public class ModRoles {
    private static final List<Role> ROLES = new ArrayList<>();

    static {
        // Register roles in ascending order of bread required!
        ROLES.add(new Role("Newbie", 0, 0xff818181));
        ROLES.add(new Role("Big Back", 10, 0xff0004d5));
        ROLES.add(new Role("Teto", 50, 0xffff004f));
        ROLES.add(new Role("Bread Muncher", 100, 0xffa66900));
    }

    // loops though the roles and finds the highest tier the player qualifies for
    public static Role getRoleForBreadCount(int breadCount){
        Role currentRole = ROLES.get(0); // default fallback obv
        
        for(Role role : ROLES){
            if (breadCount >= role.getBreadThreshold()) {
                currentRole = role;
            } else {
                break; // Since it's ordered, we can stop once we exceed their count
            }
        }

        return currentRole;
    }
}
