package assortech.api;

public enum EnergyTier {
    LV(32), MV(128), HV(512), EV(2048);

    public final int transferRate;

    EnergyTier(int transferRate) {
        this.transferRate = transferRate;
    }
}
