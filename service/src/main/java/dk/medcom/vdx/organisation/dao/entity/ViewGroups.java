package dk.medcom.vdx.organisation.dao.entity;

import java.time.LocalDateTime;

public class ViewGroups {
    private Long group_id;
    private Long parent_id;
    private String group_name;
    private Integer group_type;
    private String group_type_name;
    private boolean Deleted;
    private Long organisation_id;
    private String organisation_id_name;
    private LocalDateTime created_time;
    private String created_by;
    private LocalDateTime updated_time;
    private String updated_by;
    private LocalDateTime deleted_time;
    private String deleted_by;

    public Long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }

    public long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public Integer getGroup_type() {
        return group_type;
    }

    public void setGroup_type(Integer group_type) {
        this.group_type = group_type;
    }

    public String getGroup_type_name() {
        return group_type_name;
    }

    public void setGroup_type_name(String group_type_name) {
        this.group_type_name = group_type_name;
    }

    public boolean isDeleted() {
        return Deleted;
    }

    public void setDeleted(boolean deleted) {
        Deleted = deleted;
    }

    public Long getOrganisation_id() {
        return organisation_id;
    }

    public void setOrganisation_id(Long organisation_id) {
        this.organisation_id = organisation_id;
    }

    public String getOrganisation_id_name() {
        return organisation_id_name;
    }

    public void setOrganisation_id_name(String organisation_id_name) {
        this.organisation_id_name = organisation_id_name;
    }

    public LocalDateTime getCreated_time() {
        return created_time;
    }

    public void setCreated_time(LocalDateTime created_time) {
        this.created_time = created_time;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public LocalDateTime getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(LocalDateTime updated_time) {
        this.updated_time = updated_time;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public LocalDateTime getDeleted_time() {
        return deleted_time;
    }

    public void setDeleted_time(LocalDateTime deleted_time) {
        this.deleted_time = deleted_time;
    }

    public String getDeleted_by() {
        return deleted_by;
    }

    public void setDeleted_by(String deleted_by) {
        this.deleted_by = deleted_by;
    }
}
