package xyz.mainapi.dashboard.app;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import xyz.mainapi.dashboard.core.data.AuditableEntity;

@Entity
@Table(name = "mainapi_app")
class AppEntity extends AuditableEntity {
    @NotBlank
    private String appName;
    @NotBlank
    private String companyName;
    @NotBlank
    private String businessAddress;
    @NotBlank
    private String country;
    @NotBlank
    private String representativeName;
    private String website;
    private String csNumber;
    @Email
    @Nullable
    private String csEmail;

    public String getAppName() {
        return appName;
    }

    public AppEntity setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public AppEntity setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public AppEntity setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public AppEntity setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getRepresentativeName() {
        return representativeName;
    }

    public AppEntity setRepresentativeName(String representativeName) {
        this.representativeName = representativeName;
        return this;
    }

    public Optional<String> getWebsite() {
        return Optional.ofNullable(website);
    }

    public AppEntity setWebsite(String website) {
        this.website = website;
        return this;
    }

    public Optional<String> getCsNumber() {
        return Optional.ofNullable(csNumber);
    }

    public AppEntity setCsNumber(String csNumber) {
        this.csNumber = csNumber;
        return this;
    }

    public Optional<String> getCsEmail() {
        return Optional.ofNullable(csEmail);
    }

    public AppEntity setCsEmail(@Nullable String csEmail) {
        this.csEmail = csEmail;
        return this;
    }
}
