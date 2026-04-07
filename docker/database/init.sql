DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_user WHERE usename = 'gym_auth_user') THEN
        CREATE USER gym_auth_user WITH ENCRYPTED PASSWORD 'LiGzbc7J0ZJza6pp';
END IF;
END
$$;

CREATE SCHEMA IF NOT EXISTS gym_auth_schema AUTHORIZATION gym_auth_user;

GRANT ALL PRIVILEGES ON SCHEMA gym_auth_schema TO gym_auth_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA gym_auth_schema GRANT ALL ON TABLES TO gym_auth_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA gym_auth_schema GRANT ALL ON SEQUENCES TO gym_auth_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA gym_auth_schema GRANT ALL ON TABLES TO gym_auth_user;
