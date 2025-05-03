package ro.negru.mihai.application.schema.administrativeunits.featuretype;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.MultiPolygon;
import ro.negru.mihai.application.schema.administrativeunits.codelist.AdministrativeHierarchyLevel;
import ro.negru.mihai.application.schema.administrativeunits.datatype.ResidenceOfAuthority;
import ro.negru.mihai.application.schema.geographicalnames.datatype.GeographicalName;
import ro.negru.mihai.base.featuretype.Feature;
import ro.negru.mihai.base.stereotype.Voidable;
import ro.negru.mihai.base.types.datatype.Identifier;
import ro.negru.mihai.base.types2.codelist.CountryCode;
import ro.negru.mihai.base.types2.datatype.LocalisedCharacterString;
import ro.negru.mihai.xml.namespace.InspireNamespaces;

import java.time.LocalDateTime;
import java.util.List;

@JacksonXmlRootElement(localName = "AdministrativeUnit", namespace = InspireNamespaces.AU)
@Getter
@Setter
public class AdministrativeUnit implements Feature {

    @Getter
    @Setter
    public static class Holder {
        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        private CountryCode country;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        private MultiPolygon geometry;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        private Identifier inspireId;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        @Size(min = 1)
        private List<GeographicalName> name;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        private String nationalCode;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        private AdministrativeHierarchyLevel nationalLevel;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        private Voidable<LocalDateTime> beginLifespanVersion;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        private Voidable<LocalDateTime> endLifespanVersion;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        @JacksonXmlElementWrapper(useWrapping = false)
        @Size(min = 1)
        private List<Voidable<LocalisedCharacterString>> nationalLevelName;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        @JacksonXmlElementWrapper(useWrapping = false)
        @Size(min = 1)
        private List<Voidable<ResidenceOfAuthority>> residenceOfAuthority;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        private Voidable<AdministrativeUnit> upperLevelUnit;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        @JacksonXmlElementWrapper(useWrapping = false)
        @Size
        private List<Voidable<AdministrativeUnit>> lowerLevelUnit;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        @JacksonXmlElementWrapper(useWrapping = false)
        @Size
        private List<Voidable<AdministrativeUnit>> coAdminister;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        @JacksonXmlElementWrapper(useWrapping = false)
        @Size
        private List<Voidable<AdministrativeUnit>> administeredBy;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        @JacksonXmlElementWrapper(useWrapping = false)
        @Size(min = 1)
        private List<Voidable<AdministrativeBoundary>> boundary;

        @JacksonXmlProperty(namespace = InspireNamespaces.AU)
        @JacksonXmlElementWrapper(useWrapping = false)
        @Size
        private List<Voidable<Condominium>> condominium;
    }

    @JacksonXmlProperty(localName = "AdministrativeUnit", namespace = InspireNamespaces.AU)
    private Holder holder;

    public boolean validate() {
        return validateCondominiumsAtCountryLevel()
                && validateHighestLevelUnit()
                && validateLowestLevelUnit();
    }

    private boolean validateCondominiumsAtCountryLevel() {
        return (holder.condominium == null || holder.condominium.isEmpty())
                || AdministrativeHierarchyLevel.Enum.FIRST_ORDER.equals(holder.nationalLevel.getCode());
    }

    private boolean validateHighestLevelUnit() {
        if (!AdministrativeHierarchyLevel.Enum.FIRST_ORDER.equals(holder.nationalLevel.getCode())) return true;

        boolean hasNoUpper = holder.upperLevelUnit == null || holder.upperLevelUnit.isVoid() || holder.upperLevelUnit.getValue() == null;
        boolean hasLower = holder.lowerLevelUnit != null && !holder.lowerLevelUnit.isEmpty();

        return hasNoUpper && hasLower;
    }

    private boolean validateLowestLevelUnit() {
        if (!AdministrativeHierarchyLevel.Enum.SIXTH_ORDER.equals(holder.nationalLevel.getCode())) return true;

        boolean hasNoUpper = holder.upperLevelUnit == null || holder.upperLevelUnit.isVoid() || holder.upperLevelUnit.getValue() == null;
        boolean hasLower = holder.lowerLevelUnit != null && !holder.lowerLevelUnit.isEmpty();

        return hasNoUpper && hasLower;
    }
}
