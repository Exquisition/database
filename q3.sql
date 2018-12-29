-- Participate

SET SEARCH_PATH TO parlgov;
drop table if exists q3 cascade;

-- You must not change this table definition.

create table q3(
        countryName varchar(50),
        year int,
        participationRatio real
);

-- You may find it convenient to do this for each of the views
-- that define your intermediate steps.  (But give them better names!)

DROP VIEW IF EXISTS Pratio CASCADE;
DROP VIEW IF EXISTS avgPratio CASCADE;
DROP VIEW IF EXISTS crossyears CASCADE;
DROP VIEW IF EXISTS ExcludedCountries CASCADE;
DROP VIEW IF EXISTS resultwithcountryid CASCADE;
DROP VIEW IF EXISTS result CASCADE;

-- Define views for your intermediate steps here.

CREATE VIEW Pratio AS

SELECT country_id, extract(year from e_date) AS year, id AS election_id, votes_cast, electorate AS total_votes, cast(votes_cast AS decimal) / electorate AS participation_ratio
FROM election
WHERE extract(year from e_date) BETWEEN 2001 AND 2016 AND votes_cast IS NOT NULL AND electorate IS NOT NULL ;





CREATE VIEW avgPratio AS

SELECT country_id, year, avg(participation_ratio) AS averageP_ratio
FROM Pratio
GROUP BY country_id, year
ORDER BY country_id;


CREATE VIEW crossyears AS

SELECT ap1.country_id, ap1.year AS Y1, ap2.year AS Y2, ap1.averageP_ratio AS PR1, ap2.averageP_ratio AS PR2
FROM avgPratio ap1 join avgPratio ap2 on ap1.country_id = ap2.country_id
WHERE ap1.year < ap2.year;



CREATE VIEW ExcludedCountries AS

SELECT distinct country_id
FROM crossyears 
WHERE PR1 > PR2;


CREATE VIEW resultwithcountryid AS

SELECT country_id, year, averageP_ratio 
FROM avgPratio
WHERE country_id NOT IN (SELECT * FROM ExcludedCountries);


CREATE VIEW result3 AS

SELECT country.name as countryName, year, averageP_ratio AS participationRatio
FROM resultwithcountryid join country on resultwithcountryid.country_id = country.id;




-- the answer to the query 
insert into q3 (SELECT * FROM result3)

