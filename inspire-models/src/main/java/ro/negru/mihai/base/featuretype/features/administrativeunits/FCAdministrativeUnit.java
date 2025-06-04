package ro.negru.mihai.base.featuretype.features.administrativeunits;

import ro.negru.mihai.application.schema.administrativeunits.featuretype.AdministrativeUnit;
import ro.negru.mihai.base.featuretype.FeatureCollection;
import ro.negru.mihai.base.featuretype.features.EtsFamily;

public class FCAdministrativeUnit extends FeatureCollection<AdministrativeUnit> {
    @Override
    public String etsFamily() {
        return EtsFamily.ADM_UNITS.toString();
    }
}
