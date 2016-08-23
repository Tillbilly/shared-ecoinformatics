INSERT INTO publication_log(id,                        
                            minted_doi, 
                            doi_error, 
                            doi_time, 
                            doi_success,
                            index_time,  
                            index_error, 
                            index_success, 
                            info, 
                            log_time, 
                            submission_id)
    VALUES (2, 
            '10.4227/05/53BA0C132D80A', 
            NULL, 
            '2014-07-05 23:24:47.919', 
            true, 
            '2014-07-05 23:24:47.919', 
            'Error adding field numberStudyLocation_i="4 " msg=For input string: "4 "', 
            false, 
            'Manually created log entry', 
            '2014-07-05 23:24:47.919',             
            115122);
            
            
            
            
INSERT INTO publication_log(id,                        
                            minted_doi, 
                            doi_error, 
                            doi_time, 
                            doi_success,
                            index_time,  
                            index_error, 
                            index_success, 
                            info, 
                            log_time, 
                            submission_id)
    VALUES (3, 
            '10.4227/05/53BB83CABCFFF', 
            NULL, 
            '2014-07-05 23:24:47.919', 
            true, 
            '2014-07-05 23:24:47.919', 
            'Error with TERN DOI MINTING - MANUALLY CREATED DOI', 
            false, 
            'Manually created log entry', 
            '2014-07-05 23:24:47.919',             
            119502);
      
shared=# INSERT INTO publication_log(id,                        
shared(#                             minted_doi, 
shared(#                             doi_error, 
shared(#                             doi_time, 
shared(#                             doi_success,
shared(#                             index_time,  
shared(#                             index_error, 
shared(#                             index_success, 
shared(#                             info, 
shared(#                             log_time, 
shared(#                             submission_id)
shared-#     VALUES (4, 
shared(#             '10.4227/05/53C76C49DDD20', 
shared(#             NULL, 
shared(#             '2014-07-17 23:24:47.919', 
shared(#             true, 
shared(#             '2014-07-17 23:24:47.919', 
shared(#             'Error with TERN DOI MINTING - MANUALLY CREATED DOI', 
shared(#             false, 
shared(#             'Manually created log entry', 
shared(#             '2014-07-17 23:24:47.919',             
shared(#             119999); 
            
            
            
update submission set doi='10.4227/05/53C76C49DDD20',publicationstatus='DOI_MINTED' where id=119999;

updated to 10.4227/05/53C76F01766C2

            