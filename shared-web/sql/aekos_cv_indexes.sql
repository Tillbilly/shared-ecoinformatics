create index __lookup_codes_criteria_type_idx on __lookup_codes using btree ( criteria_type);

CREATE INDEX __lookup_codes_criteria_type_idx
  ON __lookup_codes
  USING btree
  (criteria_type COLLATE pg_catalog."default" );
  

  
  