package ro.negru.mihai.base.featuretype;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ro.negru.mihai.application.schema.administrativeunits.datatype.ResidenceOfAuthority;
import ro.negru.mihai.application.schema.administrativeunits.featuretype.AdministrativeBoundary;
import ro.negru.mihai.application.schema.administrativeunits.featuretype.AdministrativeUnit;
import ro.negru.mihai.application.schema.administrativeunits.featuretype.Condominium;
import ro.negru.mihai.application.schema.geographicalnames.datatype.GeographicalName;
import ro.negru.mihai.application.schema.geographicalnames.datatype.PronunciationOfName;
import ro.negru.mihai.application.schema.geographicalnames.datatype.SpellingOfName;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.WRAPPER_OBJECT
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SpellingOfName.class,            name = "SpellingOfName"),
        @JsonSubTypes.Type(value = PronunciationOfName.class,       name = "PronunciationOfName"),
        @JsonSubTypes.Type(value = GeographicalName.class,          name = "GeographicalName"),
        @JsonSubTypes.Type(value = Condominium.class,               name = "Condominium"),
        @JsonSubTypes.Type(value = AdministrativeUnit.class,        name = "AdministrativeUnit"),
        @JsonSubTypes.Type(value = AdministrativeBoundary.class,    name = "AdministrativeBoundary"),
        @JsonSubTypes.Type(value = ResidenceOfAuthority.class,      name = "ResidenceOfAuthority"),
})
public interface Feature {
}
