-- User: shared, pass: shared
INSERT INTO public.shared_user ( username, emailaddress, enabled, password ) 
    VALUES ('shared', 'mosheh.eliyahu@adelaide.edu.au', true, 'mg7BcRgRuJiuNTlbiu4jXICg+KxDMbfmzimj5ZrJDClaJZuq2h5kfuYwqXo/SwV2FEw8ZFvhN8EsVp9VKPr1+g==' );

-- User: review, pass: review    
INSERT INTO public.shared_user ( username, emailaddress, enabled, password ) 
    VALUES ('review','mosheh.eliyahu@adelaide.edu.au', true, 'oApxmo6Bzws+8QhmBpBKbq8RMatqDq2X+VusZvAGda2Jr1YrxU4ikclgVSlonTBMebdogsjqzvQZAk335XlsDA==' );
    
-- User: admin, pass: shared
INSERT INTO public.shared_user ( username, emailaddress, enabled, password )     
    VALUES ('admin', 'mosheh.eliyahu@adelaide.edu.au', true, 'aSxIDQE2aiOKvVsJwTvBbFzo19BHJc1AWsrzSE6PGZMoBDHOjjzW+w8x7oBh3exU9yhEyXOPBlkTk/oOAK0DMg==' );   
    
-- User: dev, pass: dev
INSERT INTO public.shared_user ( username, emailaddress, enabled, password )
    VALUES ('dev', 'kathryn.allard@anu.edu.au' , true, 'wpR1SOn2IG8Hnms5RBwaTtRICFkQ77qdp0DdWKlFyL5x84Tnj7yNZXNbbE0iUjMs+9FZKo/YtKrDCMVmhZqejg==');
    
INSERT INTO public.shared_authorities ( id, authority, username )
    VALUES ( 0, 'ROLE_USER', 'shared' );
    
INSERT INTO public.shared_authorities ( id, authority, username )
    VALUES ( 1, 'ROLE_USER', 'review' );

INSERT INTO public.shared_authorities (id, authority, username )
    VALUES ( 2, 'ROLE_REVIEWER', 'review' );
    
INSERT INTO public.shared_authorities ( id, authority, username )
    VALUES ( 3, 'ROLE_USER', 'dev' );

INSERT INTO public.shared_authorities (id, authority, username )
    VALUES ( 4, 'ROLE_REVIEWER', 'dev' );
    
INSERT INTO public.shared_authorities ( id, authority, username )
    VALUES ( 5, 'ROLE_DEVELOPER', 'dev' );
    
INSERT INTO public.shared_authorities ( id, authority, username )
    VALUES ( 6, 'ROLE_USER', 'admin' );

INSERT INTO public.shared_authorities (id, authority, username )
    VALUES ( 7, 'ROLE_REVIEWER', 'admin' );
    
INSERT INTO public.shared_authorities ( id, authority, username )
    VALUES ( 8, 'ROLE_DEVELOPER', 'admin' ); 

INSERT INTO public.shared_authorities ( id, authority, username )
    VALUES ( 9, 'ROLE_ADMIN', 'admin' );

INSERT INTO public.shared_authorities ( id, authority, username )
	VALUES ( 10, 'ROLE_VOCAB_MANAGER', 'admin' );
    

INSERT INTO public.shared_user_shared_authorities ( shared_user_username, roles_id )
    VALUES ( 'shared', 0 );
    
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES('review',1);
    
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES ( 'review', 2 );
    
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES('dev',3);
    
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES ( 'dev',4);
    
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES('dev',5);
    
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES('admin',6);
    
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES('admin',7);
    
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES('admin',8);
    
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES('admin',9);
    
DROP SEQUENCE hibernate_sequence;

CREATE SEQUENCE hibernate_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 10
  CACHE 1;
  
 grant all on hibernate_sequence to "shared-dev";