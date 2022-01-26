package assortech.api;

public enum EnergyTier {
    LOW(32), MEDIUM(128), HIGH(512), EXTREME(2048);

    public final int transferRate;

    EnergyTier(int transferRate) {
        this.transferRate = transferRate;
    }
}
