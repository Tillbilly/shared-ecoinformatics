    
INSERT INTO public.shared_user ( username, emailaddress, enabled, password )     
    VALUES ('admin', 'benjamin.till@adelaide.edu.au', true, 'aSxIDQE2aiOKvVsJwTvBbFzo19BHJc1AWsrzSE6PGZMoBDHOjjzW+w8x7oBh3exU9yhEyXOPBlkTk/oOAK0DMg==' );   
 
INSERT INTO public.shared_authorities ( id, authority, username )
    VALUES ( 66, 'ROLE_USER', 'admin' );

INSERT INTO public.shared_authorities (id, authority, username )
    VALUES ( 67, 'ROLE_REVIEWER', 'admin' );
    
INSERT INTO public.shared_authorities ( id, authority, username )
    VALUES ( 68, 'ROLE_DEVELOPER', 'admin' ); 

INSERT INTO public.shared_authorities ( id, authority, username )
    VALUES ( 69, 'ROLE_ADMIN', 'admin' );
    
   
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES('admin',66);
    
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES('admin',67);
    
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES('admin',68);
    
INSERT INTO public.shared_user_shared_authorities (shared_user_username,roles_id)
    VALUES('admin',69);    