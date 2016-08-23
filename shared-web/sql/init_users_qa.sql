INSERT INTO public.shared_user ( username, emailaddress, enabled, password ) 
    VALUES ('shared', 'mosheh.eliyahu@adelaide.edu.au', true, 'shared' );
    
INSERT INTO public.shared_user ( username, emailaddress, enabled, password ) 
    VALUES ('review','mosheh.eliyahu@adelaide.edu.au', true, 'review' );
    
INSERT INTO public.shared_user ( username, emailaddress, enabled, password ) 
    VALUES ('review_casper','casper.yeow@adelaide.edu.au', true, 'review' );
    
INSERT INTO public.shared_user ( username, emailaddress, enabled, password ) 
    VALUES ('review_martin','martin.pullan@adelaide.edu.au', true, 'review' );
    
INSERT INTO public.shared_user ( username, emailaddress, enabled, password ) 
    VALUES ('review_matt','matt.schneider@adelaide.edu.au', true, 'review' );

INSERT INTO public.shared_authorities ( id, authority, username )
    VALUES ( 0, 'ROLE_USER', 'shared' );
    
INSERT INTO public.shared_authorities ( id, authority, username )
    VALUES ( 1, 'ROLE_USER', 'review' );

INSERT INTO public.shared_authorities (id, authority, username )
    VALUES ( 2, 'ROLE_REVIEWER', 'review' );
    
INSERT INTO public.shared_authorities ( id, authority, username )
    VALUES ( 3, 'ROLE_USER', 'review_casper' );

INSERT INTO public.shared_authorities (id, authority, username )
    VALUES ( 4, 'ROLE_REVIEWER', 'review_casper' );
    
INSERT INTO public.shared_authorities ( id, authority, username )
    VALUES ( 5, 'ROLE_USER', 'review_martin' );

INSERT INTO public.shared_authorities (id, authority, username )
    VALUES ( 6, 'ROLE_REVIEWER', 'review_martin' );
    
INSERT INTO public.shared_authorities ( id, authority, username )
    VALUES ( 7, 'ROLE_USER', 'review_matt' );

INSERT INTO public.shared_authorities (id, authority, username )
    VALUES ( 8, 'ROLE_REVIEWER', 'review_matt' );    

INSERT INTO public.shared_user_shared_authorities ( shared_user_username, roles_id )
    VALUES ( 'shared', 0 );
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES('review',1);
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES ( 'review', 2 );
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES('review_casper',3);
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES ( 'review_casper', 4 );
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES('review_martin',5);
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES ( 'review_martin', 6 );
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES('review_matt',7);
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES ( 'review_matt', 8 );    

    
DROP SEQUENCE hibernate_sequence;

CREATE SEQUENCE hibernate_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 9
  CACHE 1;
  
 grant all on hibernate_sequence to "shared-dev";
