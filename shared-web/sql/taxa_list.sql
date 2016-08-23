select namewithoutauthorship from r_speciesconcept where speciesconcepttype like 'NOM%' or speciesconcepttype like 'UNDER%' order by namewithoutauthorship limit 1000;

select distinct name from r_speciesconceptfaunashared where rank in ('Genus','Species') order by name;

select distinct 
a.iname as name
from (
 select 
    initcap(replace(replace(name,'"',''), '''' , '' )) as iname, 
    upper(name) as upname, 
    name 
 from r_speciesconceptfaunashared 
 where name not like '% %' 
   and name not like '%?%' 
   and name not like '[%'
   and name not like '(%' 
   and rank != 'SubFamily' 
 UNION
 select  
    replace(replace(name,'"',''), '''' , '' ) as iname, 
    upper(name) as upname, 
    name 
 from r_speciesconceptfaunashared 
 where name like '% %' 
   and name not like '%?%' 
   and name not like '[%'
   and name not like '(%'
   and rank != 'SubFamily' 
 ) a
 order by name;