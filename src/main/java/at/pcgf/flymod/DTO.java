package at.pcgf.flymod;

public class DTO {
    private static boolean isInvulnerableToFallDamage = false;

    public static boolean isIsInvulnerableToFallDamage() {
        return isInvulnerableToFallDamage;
    }

    public static void setIsInvulnerableToFallDamage(boolean isInvulnerableToFallDamage) {
        DTO.isInvulnerableToFallDamage = isInvulnerableToFallDamage;
    }
}
