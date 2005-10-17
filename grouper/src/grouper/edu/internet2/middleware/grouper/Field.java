package edu.internet2.middleware.grouper;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** 
 * Schema specification for a Group attribute or list.
 * @author blair christensen.
 *     
*/
public class Field implements Serializable {

    /** identifier field */
    private String id;

    /** persistent field */
    private String name;

    /** persistent field */
    private boolean is_list;

    /** nullable persistent field */
    private Integer version;

    /** nullable persistent field */
    private edu.internet2.middleware.grouper.Type type_id;

    /** nullable persistent field */
    private edu.internet2.middleware.grouper.Privilege read_privilege_id;

    /** nullable persistent field */
    private edu.internet2.middleware.grouper.Privilege write_privilege_id;

    /** full constructor */
    public Field(String name, boolean is_list, Integer version, edu.internet2.middleware.grouper.Type type_id, edu.internet2.middleware.grouper.Privilege read_privilege_id, edu.internet2.middleware.grouper.Privilege write_privilege_id) {
        this.name = name;
        this.is_list = is_list;
        this.version = version;
        this.type_id = type_id;
        this.read_privilege_id = read_privilege_id;
        this.write_privilege_id = write_privilege_id;
    }

    /** default constructor */
    public Field() {
    }

    /** minimal constructor */
    public Field(String name, boolean is_list) {
        this.name = name;
        this.is_list = is_list;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /** 
     * Get field name.
     *       
     */
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** 
     * Get whether field is a list.
     *       
     */
    public boolean isIs_list() {
        return this.is_list;
    }

    public void setIs_list(boolean is_list) {
        this.is_list = is_list;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public edu.internet2.middleware.grouper.Type getType_id() {
        return this.type_id;
    }

    public void setType_id(edu.internet2.middleware.grouper.Type type_id) {
        this.type_id = type_id;
    }

    /** 
     * Get read privilege.
     *       
     */
    public edu.internet2.middleware.grouper.Privilege getRead_privilege_id() {
        return this.read_privilege_id;
    }

    public void setRead_privilege_id(edu.internet2.middleware.grouper.Privilege read_privilege_id) {
        this.read_privilege_id = read_privilege_id;
    }

    /** 
     * Get write privilege.
     *       
     */
    public edu.internet2.middleware.grouper.Privilege getWrite_privilege_id() {
        return this.write_privilege_id;
    }

    public void setWrite_privilege_id(edu.internet2.middleware.grouper.Privilege write_privilege_id) {
        this.write_privilege_id = write_privilege_id;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("name", getName())
            .append("is_list", isIs_list())
            .append("type_id", getType_id())
            .append("read_privilege_id", getRead_privilege_id())
            .append("write_privilege_id", getWrite_privilege_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof Field) ) return false;
        Field castOther = (Field) other;
        return new EqualsBuilder()
            .append(this.getName(), castOther.getName())
            .append(this.isIs_list(), castOther.isIs_list())
            .append(this.getType_id(), castOther.getType_id())
            .append(this.getRead_privilege_id(), castOther.getRead_privilege_id())
            .append(this.getWrite_privilege_id(), castOther.getWrite_privilege_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getName())
            .append(isIs_list())
            .append(getType_id())
            .append(getRead_privilege_id())
            .append(getWrite_privilege_id())
            .toHashCode();
    }

}
