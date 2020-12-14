INSERT INTO sport(id, name) VALUES(1, 'Tennis');
INSERT INTO sport(id, name) VALUES(2, 'Basketball');
INSERT INTO sport(id, name) VALUES(3, 'Baseball');
INSERT INTO sport(id, name) VALUES(4, 'Soccer');
INSERT INTO sport(id, name) VALUES(5, 'Volleyball');
INSERT INTO sport(id, name) VALUES(6, 'Football');
INSERT INTO sport(id, name) VALUES(7, 'Hockey');
INSERT INTO sport(id, name) VALUES(8, 'Lacrosse');
INSERT INTO sport(id, name) VALUES(9, 'Chess');
INSERT INTO sport(id, name) VALUES(10, 'Ping-Pong');
INSERT INTO sport(id, name) VALUES(11, 'Billiard');

INSERT INTO counting(id, name, wins, loss, tie, wot, lot, otAllowed, tieAllowed, resultOnly) VALUES(1,'2-points',    2, 0, 1, 0, 0, 0, 1, 1);
INSERT INTO counting(id, name, wins, loss, tie, wot, lot, otAllowed, tieAllowed, resultOnly) VALUES(2,'3-points',    3, 0, 1, 0, 0, 0, 1, 1);
INSERT INTO counting(id, name, wins, loss, tie, wot, lot, otAllowed, tieAllowed, resultOnly) VALUES(3,'3-points-tb', 3, 0, 1, 2, 1, 1, 0, 1);
INSERT INTO counting(id, name, wins, loss, tie, wot, lot, otAllowed, tieAllowed, resultOnly) VALUES(4,'3-points-ot', 3, 0, 1, 3, 0, 1, 0, 1);
INSERT INTO counting(id, name, wins, loss, tie, wot, lot, otAllowed, tieAllowed, resultOnly) VALUES(5,'each',        0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO counting(id, name, wins, loss, tie, wot, lot, otAllowed, tieAllowed, resultOnly) VALUES(6,'tennis',      2, 0, 0, 0, 0, 0, 0, 1);
INSERT INTO counting(id, name, wins, loss, tie, wot, lot, otAllowed, tieAllowed, resultOnly) VALUES(7,'basket',      2, 0, 0, 0, 0, 1, 0, 1);

INSERT INTO sport_counting(sport, counting) VALUES(1, 6);
INSERT INTO sport_counting(sport, counting) VALUES(2, 7);
INSERT INTO sport_counting(sport, counting) VALUES(4, 2);
INSERT INTO sport_counting(sport, counting) VALUES(5, 5);
INSERT INTO sport_counting(sport, counting) VALUES(7, 3);
INSERT INTO sport_counting(sport, counting) VALUES(9, 1);
INSERT INTO sport_counting(sport, counting) VALUES(10, 6);
INSERT INTO sport_counting(sport, counting) VALUES(11, 6);
