group EntryDAO;

ensureSchema() ::= <<
   create table if not exists entry (entry_number integer primary key, sha256hex varchar, timestamp timestamp default (now() at time zone 'utc'));
   create index if not exists entry_sha256hex_index on entry (sha256hex);
>>
