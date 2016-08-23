--How to look at question set answers for a given paerent question id and submission id

select qsa.questionid, 
       qsa.response,
       qsa.suggestedresponse
from answer qsa
join question_set qs 
on qsa.question_set_id = qs.id
where qs.parent_answer_id= (select pa.id from answer pa where pa.submission_id=58991 and pa.questionid='13.4'); 


insert into publication_log
(id,
 minted_doi,
 doi_time,
 doi_success,
 index_time,
 index_error,
 index_success,
 info,
 log_time,
 submission_id )
values( 1, '10.4227/05/53869A9434A46',
         '2014-05-28 20:59:09.22',
         't','2014-05-28 20:59:10.22',
         'Error Creating Convex Hull for Polygon',
         'f',
         'info',
         '2014-05-28 20:59:11.22',
         58991
      );