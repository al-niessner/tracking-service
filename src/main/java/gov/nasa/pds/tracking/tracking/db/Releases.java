/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.db;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;

@XmlRootElement(name = "releases")
/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
public class Releases implements Serializable  {

	private static final long serialVersionUID = 1L;

	public static Logger logger = Logger.getLogger(Releases.class);

	private String logIdentifier = null;
	private String version = null;
	private Timestamp release_date = null;
	private Timestamp announcement_date = null;
	private String name = null;
	private String description = null;
	private String email = null;
	private String comment = null;
	
	public Releases(String logIdenf, String ver, Timestamp date, Timestamp announceMDate, String name, String desc, String email, String comm){
		this.logIdentifier = logIdenf;
		this.version = ver;
		this.release_date = date;
		this.announcement_date = announceMDate;
		this.name = name;
		this.description = desc;
		this.email = email;
		this.comment = comm;		
	}
	
	/**
	 * @return the logIdentifier
	 */
	public String getLogIdentifier() {
		return logIdentifier;
	}
	/**
	 * @param logIdentifier, the logIdentifier to set
	 */
	public void setLogIdentifier(String logIdentifier) {
		this.logIdentifier = logIdentifier;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version, the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * @return the date
	 */
	public Timestamp getDate() {
		return release_date;
	}
	/**
	 * @param date, the date to set
	 */
	public void setDate(Timestamp date) {
		this.release_date = date;
	}
	/**
	 * @return the announcement_date
	 */
	public Timestamp getAnnouncement_date() {
		return announcement_date;
	}

	/**
	 * @param announcement_date the announcement_date to set
	 */
	public void setAnnouncement_date(Timestamp announcement_date) {
		this.announcement_date = announcement_date;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name, the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description, the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email, the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment, the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Releases(){
		// TODO Auto-generated constructor stub
	}
	
}
