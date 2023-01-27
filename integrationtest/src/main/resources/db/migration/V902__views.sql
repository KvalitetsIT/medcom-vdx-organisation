CREATE OR REPLACE VIEW view_groups AS
select
    groups.group_id AS group_id,
    groups.parent_id AS parent_id,
    if((not(exists(
    select
        organisation.id
    from
        organisation
    where
        (groups.group_id = organisation.group_id)))),
    groups.group_name,
    convert(organisation.name
        using utf8mb4)) AS group_name,
    groups.group_type AS group_type,
    if((groups.group_type = 1),
    'group',
    if((groups.group_type = 2),
    'organisation',
    if((groups.group_type = 3),
    'praksis',
    ''))) AS group_type_name,
    if((groups.deleted_time > '0001-01-01'),
    1,
    0) AS Deleted,
    organisation.id AS organisation_id,
    organisation.organisation_id AS organisation_id_name,
    groups.created_time AS created_time,
    groups.created_by AS created_by,
    groups.updated_time AS updated_time,
    groups.updated_by AS updated_by,
    groups.deleted_time AS deleted_time,
    groups.deleted_by AS deleted_by
from
    (groups
left join organisation on
    ((groups.group_id = organisation.group_id)))
order by
    groups.group_id;
