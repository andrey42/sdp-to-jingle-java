/*
 * jSDP: A Java implementation of SDP protocol Copyright (C) 2007 Claudio Di
 * Vita
 */
package net.sourceforge.jsdp;

/**
 * A <tt>Version</tt> represents the <b>v=<i>&lt;field value&gt;</i></b>
 * field contained in a SDP message. The version field gives the version of the
 * Session Description Protocol: there is no minor version number and the only
 * allowed version value is <tt>0</tt>.
 * 
 * @since 0.1.0
 * 
 * @version 1.0
 * 
 * @author <a href="mailto:cdivita@users.sourceforge.net">Claudio Di Vita</a>
 */
public class Version implements Field {

    /** The class Stream Unique Identifier, SUID */
    private static final long serialVersionUID = 5365815747331173108L;

    /** The default SDP version */
    public static final Version DEFAULT_VERSION = new Version();

    /** The SDP protocol version */
    protected int version;

    /**
     * Creates a new <tt>Version</tt>. The version number is 0.
     */
    public Version() {
        this(0);
    }

	/**
	 * Creates a new <tt>Version</tt>.
	 *
	 * @param version the SDP protocol version
	 */
	public Version(int version) {
		this.version = version;
	}

    /**
     * Parse an input string and constructs the equivalent session name field.
     * 
     * @param field the string to parse
     * 
     * @return a new <tt>SessionName</tt> instance
     * 
     * @throws SDPParseException if an error occurs while parsing
     */
    public static Version parse(final String field) throws SDPParseException {

        if (!field.startsWith("v=")) {
            throw new SDPParseException("The string \"" + field + "\" isn't a version field");
        }

        try {
            /* Extract the protocol versio */
            int version = Integer.parseInt(field.substring(2));

            if (version != 0) {
                throw new SDPParseException("Invalid SDP protocol version: the only allowed value is 0");
            }
        }
        catch (NumberFormatException parseException) {
            throw new SDPParseException("The string \"" + field + "\" isn't a valid version field", parseException);
        }

        return DEFAULT_VERSION;
    }

    /**
     * Returns a clone of this field.
     * 
     * @return a clone of this field
     */
    public Object clone() {

        Version field = new Version();
        field.version = this.version;

        return field;
    }

    /**
     * Returns the type character for the field.
     * 
     * @return the field type character: <b>v</b>
     */
    public char getType() {

        return Field.VERSION_FIELD;
    }

    /**
     * Returns the SDP protocol version.
     * 
     * @return the protocol version
     */
    public int getValue() {

        return version;
    }

    /**
     * Sets the SDP protocol version
     * 
     * @param version the SDP protocol version
     * 
     * @throws SDPException if the version specified is not valid. At this time
     *         the only version allowed is <tt>0</tt>
     */
    protected void setValue(final int version) throws SDPException {

        if (version != 0) {
            throw new SDPException("Invalid SDP protocol version: the only allowed value is 0");
        }

        this.version = version;
    }

    /**
     * Returns a string representation of the field. The representation has the
     * form: <b>v=<i>&lt;version&gt;</i></b>.
     * 
     * @return the string representation of the field
     */
    public String toString() {

        return getType() + "=" + version;
    }
}