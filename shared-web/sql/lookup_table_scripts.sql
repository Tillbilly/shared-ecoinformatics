DROP TABLE lookup_codes;

CREATE TABLE lookup_codes
(
  parent_id character varying(150) ,
  criteria_id character varying(150) NOT NULL,
  criteria_type character varying(100),
  criteria_title character varying(255),
  criteria_abstract text,
  search_text character varying(150) NOT NULL
)
WITH (
  OIDS=TRUE
);



copy lookup_codes from '/Users/a1042238/__lookup_codes.csv' csv delimiter ',' quote '"'

select distinct criteria_type from lookup_codes;


--Indexes for Shared
create index criteria_type_ix on __lookup_codes ( criteria_type );