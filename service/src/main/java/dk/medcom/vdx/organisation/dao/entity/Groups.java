package dk.medcom.vdx.organisation.dao.entity;

public record Groups(Long groupId, String groupName, int groupType, Long parentId, String createdBy) {
    public static Groups createInstance(String groupName, int groupType, Long parentId, String createdBy) {
        return new Groups(null, groupName, groupType, parentId, createdBy);
    }
}
