-- This script runs once when the PostgreSQL container is first created.
-- It creates the additional databases needed by the microservices.

CREATE DATABASE sigepid_orders;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE sigepid_auth TO sigepid;
GRANT ALL PRIVILEGES ON DATABASE sigepid_orders TO sigepid;
