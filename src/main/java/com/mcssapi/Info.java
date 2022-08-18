package com.mcssapi;

import com.mcssapi.exceptions.APIVersionMismatchException;

import java.util.Objects;

public class Info {

    private boolean isDev = false;
    private String MCSSVersion = null;
    private String MCSSApiVersion = null;
    private String UniqueID = null;
    //will be set to true by the API
    private boolean youAreAwesome = false;

    protected Info (Boolean isDev, String MCSSVersion, String MCSSApiVersion, String UniqueID, Boolean youAreAwesome) throws APIVersionMismatchException {
        this.isDev = isDev;
        this.MCSSVersion = MCSSVersion;
        this.MCSSApiVersion = MCSSApiVersion;
        this.UniqueID = UniqueID;
        this.youAreAwesome = youAreAwesome;

        checkVersionMismatch();
    }

    /**
     * @return the true if MCSS is a devbuild, false if not
     */
    public boolean getIsDev() {
        return isDev;
    }

    /**
     * @return the MCSS version
     */
    public String getMCSSVersion() {
        return MCSSVersion;
    }

    /**
     * @return the MCSSApi version
     */
    public String getMCSSApiVersion() {
        return MCSSApiVersion;
    }

    /**
     * @return the UniqueID
     */
    public String getUniqueID() {
        return UniqueID;
    }

    public boolean getYouAreAwesome() {
        return youAreAwesome;
    }

    private void checkVersionMismatch() throws APIVersionMismatchException {
        if (!Objects.equals(MCSSApiVersion, "1.0.0")) {
            throw new APIVersionMismatchException("MCSSApi version mismatch. Expected 1.0.0, got " + MCSSApiVersion + "." +
                    "API Wrapper might have issues. Proceed with caution.");
        }
    }
}
