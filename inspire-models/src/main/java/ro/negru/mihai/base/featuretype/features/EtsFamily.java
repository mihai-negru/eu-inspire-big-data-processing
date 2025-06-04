package ro.negru.mihai.base.featuretype.features;

public enum EtsFamily {
    ADM_UNITS("administrativeunits"),
    GEO_NAMES("geographicalnames");

    private final String value;

    EtsFamily(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
