/* Author: FO34949 */
set serveroutput on DEFINE OFF sqlblanklines on ECHO ON;

alter session set current_schema = OBTAPP;

merge into OBTAPP.refs dst using (
    select
        RM.id as type_id,
        'View only' as str1,
        'No' as str2,
        'CASHPI, LIQUIPI' as str3
    from
        OBTAPP.REFS_META RM
    where
        RM.NAME = 'amtCitiDirectAccountPurposeAndAccessLevelMapping'

    UNION ALL

    select
        RM.id as type_id,
        'View only' as str1,
        'Yes' as str2,
        'CASHPI, LIQUIPI, DIRDEBTPI' as str3
    from
        OBTAPP.REFS_META RM
    where
        RM.NAME = 'amtCitiDirectAccountPurposeAndAccessLevelMapping'

    UNION ALL

    select
        RM.id as type_id,
        'View and Transact' as str1,
        'No' as str2,
        'CASHPI, LIQUIPI, PAYMENTS' as str3
    from
        OBTAPP.REFS_META RM
    where
        RM.NAME = 'amtCitiDirectAccountPurposeAndAccessLevelMapping'

    UNION ALL

    select
        RM.id as type_id,
        'View and Transact' as str1,
        'Yes' as str2,
        'CASHPI, LIQUIPI, DIRDEBTPI, PAYMENTS, DMANDATETI' as str3
    from
        OBTAPP.REFS_META RM
    where
        RM.NAME = 'amtCitiDirectAccountPurposeAndAccessLevelMapping'
) src on (
    src.type_id = dst.type_id
    and src.str1 = dst.str1
    and src.str2 = dst.str2
    and src.str3 = dst.str3
)
when not matched then
insert
(
    id,
    type_id,
    status,
    is_mutable,
    locale,
    str1,
    str2,
    str3,
    last_modified_by
)
values
(
    OBTAPP.REFS_ID_SEQ.nextval,
    src.type_id,
    1,
    1,
    'en',
    src.str1,
    src.str2,
    src.str3,
    'FO34949'
);

commit;
/
