INSERT INTO Agent (name, rank, alive) VALUES ('Adam the King', 1, TRUE);
INSERT INTO Agent (name, rank, alive) VALUES ('Daniel the Boss', 3, FALSE);
INSERT INTO Agent (name, rank, alive) VALUES ('Petr', 4, TRUE);
INSERT INTO Agent (name, rank, alive) VALUES ('Blazon', 4, TRUE);
INSERT INTO Agent (name, rank, alive) VALUES ('Chuck Norris', 2, TRUE);
INSERT INTO Agent (name, rank, alive) VALUES ('Deadpool', 10, FALSE);
INSERT INTO Agent (name, rank, alive) VALUES ('Flash', 9, TRUE);
INSERT INTO Agent (name, rank, alive) VALUES ('Superman', 8, TRUE);
INSERT INTO Agent (name, rank, alive) VALUES ('Michael', 3, TRUE);

INSERT INTO Mission (name, status, agentID, required_rank) VALUES ('Java Mission', 'IN_PROGRESS', 5, 3);
INSERT INTO Mission (name, status, agentID, required_rank) VALUES ('Nuclear attack', 'FAILED', 3, 5);
INSERT INTO Mission (name, status, agentID, required_rank) VALUES ('Java is life', 'ACCOMPLISHED', 9, 1);
INSERT INTO Mission (name, status, required_rank) VALUES ('Effective Java', 'NOT_ASSIGNED', 1);
INSERT INTO Mission (name, status, required_rank) VALUES ('Rabbit hunt', 'NOT_ASSIGNED', 8);
INSERT INTO Mission (name, status, required_rank) VALUES ('Mission impossible', 'NOT_ASSIGNED', 6);
INSERT INTO Mission (name, status, required_rank) VALUES ('Save the bird', 'NOT_ASSIGNED', 1);
INSERT INTO Mission (name, status, required_rank) VALUES ('Shoot the parrot', 'NOT_ASSIGNED', 1);
INSERT INTO Mission (name, status, required_rank) VALUES ('Tame the wolf', 'NOT_ASSIGNED', 1);
