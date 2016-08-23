

--Lists all download requests
select sub.id, sub.title, st.email, st.date 
from st_downreq st 
left join submission sub 
on st.submission_id = sub.id 
order by st.date;

--Counts of unique downloads ( i.e. by unique email per report )
select sub.id submission_id, sub.title, dr.cnt
from(
select distinct_requests.subid, count(distinct_requests.eml) cnt
from (
select distinct st.submission_id subid, st.email eml from st_downreq st
) distinct_requests
group by distinct_requests.subid
) dr
left join submission sub on dr.subid = sub.id
order by dr.cnt desc;