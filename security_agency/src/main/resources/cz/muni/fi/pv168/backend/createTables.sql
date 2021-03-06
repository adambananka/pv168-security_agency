CREATE TABLE "AGENT" (
    "ID" BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "NAME" VARCHAR(50) NOT NULL,
    "RANK" INTEGER NOT NULL,
    "ALIVE" BOOLEAN
);

CREATE TABLE "MISSION" (
    "ID" BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "NAME" VARCHAR(50) NOT NULL,
    "AGENTID" BIGINT,
    "STATUS" VARCHAR(12) NOT NULL,
    "REQUIRED_RANK" INTEGER NOT NULL
);