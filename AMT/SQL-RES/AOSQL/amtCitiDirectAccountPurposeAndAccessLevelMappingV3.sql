/* Author: FO34949 */
set serveroutput on DEFINE OFF sqlblanklines on ECHO ON;

alter session set current_schema = OBTAPP;

merge into OBTAPP.REFS dst
using (
    select
        rm.id as type_id,
        v.str1,
        v.str2,
        v.str3,
        v.str4
    from
        OBTAPP.REFS_META rm
        cross join (
            select 'Associated Accounts' as str1, 'CASHPI' as str2, 'View Only' as str3, 'Yes' as str4 from dual
            union all
            select 'Associated Accounts', 'CASHPI', 'View Only', 'No' from dual
            union all
            select 'Associated Accounts', 'PAYMENTS', 'View & Transact', 'Yes' from dual
            union all
            select 'Associated Accounts', 'PAYMENTS', 'View & Transact', 'No' from dual
            union all
            select 'Associated Accounts', 'LIQUIPI', 'View Only', 'Yes' from dual
            union all
            select 'Associated Accounts', 'LIQUIPI', 'View Only', 'No' from dual
            union all
            select 'Associated Accounts', 'DIRDEBTPI', 'View Only', 'Yes' from dual
            union all
            select 'Associated Accounts', 'DMANDATETI', 'View & Transact', 'Yes' from dual
            union all
            select 'Associated Accounts', 'CASHPI, LIQUIPI', 'View Only', 'No' from dual
            union all
            select 'Associated Accounts', 'CASHPI, LIQUIPI, DIRDEBTPI', 'View Only', 'Yes' from dual
            union all
            select 'Associated Accounts', 'CASHPI, LIQUIPI, PAYMENTS', 'View & Transact', 'No' from dual
            union all
            select 'Associated Accounts', 'CASHPI, LIQUIPI, DIRDEBTPI, PAYMENTS, DMANDATETI', 'View & Transact', 'Yes' from dual
            union all
            select 'Associated Accounts', 'CASHPI', 'View Only', 'No' from dual
            union all
            select 'Associated Accounts', 'CASHPI, DIRDEBTPI', 'View Only', 'Yes' from dual
            union all
            select 'Associated Accounts', 'CASHPI, PAYMENTS', 'View & Transact', 'No' from dual
            union all
            select 'Associated Accounts', 'CASHPI, DIRDEBTPI, PAYMENTS, DMANDATETI', 'View & Transact', 'Yes' from dual
            union all
            select 'Updated Accounts', 'CASHPI, LIQUIPI', 'View Only', 'No' from dual
            union all
            select 'Updated Accounts', 'CASHPI, LIQUIPI, DIRDEBTPI', 'View Only', 'Yes' from dual
            union all
            select 'Updated Accounts', 'CASHPI, LIQUIPI, PAYMENTS', 'View & Transact', 'No' from dual
            union all
            select 'Updated Accounts', 'CASHPI, LIQUIPI, DIRDEBTPI, PAYMENTS, DMANDATETI', 'View & Transact', 'Yes' from dual
        ) v
    where
        rm.name = 'amtCitiDirectAccountPurposeAndAccessLevelMapping'
) src
on (
    src.type_id = dst.type_id
    and src.str1 = dst.str1
    and src.str2 = dst.str2
    and src.str3 = dst.str3
    and src.str4 = dst.str4
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
    str4,
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
    src.str4,
    'FO34949'
);

commit;
/
