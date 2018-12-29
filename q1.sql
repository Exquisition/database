-- VoteRange

SET SEARCH_PATH TO parlgov;
drop table if exists q1 cascade;

-- You must not change this table definition.

create table q1(
year INT,
countryName VARCHAR(50),
voteRange VARCHAR(20),
partyName VARCHAR(100)
);


-- You may find it convenient to do this for each of the views
-- that define your intermediate steps.  (But give them better names!)
DROP VIEW IF EXISTS joinedtables CASCADE;
DROP VIEW IF EXISTS percentvalid CASCADE;
DROP VIEW IF EXISTS hasname CASCADE;
DROP VIEW IF EXISTS result CASCADE;

-- Define views for your intermediate steps here.

--country id (election), party id (election_result), year of the election (election), votes for a party (election result), total valid votes going into the election (votes_valid) (election)

create view joinedtables as

SELECT election.country_id, election_result.party_id, extract(year from election.e_date) AS year, election.id AS electionID, election_result.votes as valid_votes, election.votes_valid as total_votes, 
cast(election_result.votes as decimal) * 100 / election.votes_valid as percentage
 
FROM election join election_result on election.id = election_result.election_id

WHERE extract(year from election.e_date) BETWEEN 1996 AND 2016 AND election_result.votes is not null AND election.votes_valid is not null;



create view percentvalid as

SELECT country_id, party_id, year, avg(percentage) as Avg_percent
FROM joinedtables
GROUP BY country_id, party_id, year
ORDER BY avg(percentage) DESC;


create view description as

SELECT year, country_id,   
		CASE 
			when Avg_percent > 0 and Avg_percent <=5 then '(0-5]'
			when Avg_percent > 5 and Avg_percent <= 10 then '(5-10]'
			when Avg_percent > 10 and Avg_percent <= 20 then '(10-20]'
			when Avg_percent > 20 and Avg_percent <= 30 then '(20-30]'
			when Avg_percent > 30 and Avg_percent <= 40 then '(30-40]'
			when Avg_percent > 40 then '(40-100]'
			
		end as VoteRange
		, party_id 

FROM percentvalid
ORDER BY year, country_id;


CREATE VIEW result1 AS

SELECT description.year, country.name AS countryName, description.voterange, party.name_short

FROM description JOIN country ON description.country_id = country.id
JOIN party ON description.party_id = party.id;

			  


-- the answer to the query 
insert into q1 (SELECT * FROM result1)

