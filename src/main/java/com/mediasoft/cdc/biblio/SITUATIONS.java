package com.mediasoft.cdc.biblio;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */

/**
 *
 * @author inganzamarumpu
 */
public enum SITUATIONS {
    PRETE("prêté"),
    RESERVE("réservé"),
    DISPONIBLE("disponible"),
    EN_REPARATION("en réparation"),
    NON_RETOURNE("non retourné"),
    EXCLU("exclu");

    private final String displayName;

    SITUATIONS(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Optional: Method to get enum from display name
    public static SITUATIONS fromDisplayName(String displayName) {
        for (SITUATIONS role : SITUATIONS.values()) {
            if (role.getDisplayName().equalsIgnoreCase(displayName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name " + displayName);
    }
}
