##按顺序清理表
truncate act_hi_actinst;
truncate act_hi_identitylink;
truncate act_hi_procinst;
truncate act_hi_taskinst;
truncate act_ru_actinst;
truncate act_ru_identitylink;
truncate act_ru_deadletter_job;
truncate act_hi_varinst;
truncate act_ru_variable;
delete from act_ru_task;
delete from  act_ge_bytearray;
delete from act_re_deployment;
delete from act_ru_execution;
delete from  act_re_procdef;
