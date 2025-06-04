package ro.negru.mihai.base.featuretype.features.administrativeunits;

import ro.negru.mihai.application.schema.administrativeunits.featuretype.AdministrativeBoundary;
import ro.negru.mihai.base.featuretype.FeatureCollection;
import ro.negru.mihai.base.featuretype.features.EtsFamily;

public class FCAdministrativeBoundary extends FeatureCollection<AdministrativeBoundary> {
    @Override
    public String etsFamily() {
        return EtsFamily.ADM_UNITS.toString();
    }
}
