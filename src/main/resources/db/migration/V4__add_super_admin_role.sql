ALTER TABLE members DROP CONSTRAINT members_role_check;

ALTER TABLE members ADD CONSTRAINT members_role_check CHECK (((role)::text = ANY ((ARRAY['USER'::character varying, 'ADMIN'::character varying, 'SUPER_ADMIN'::character varying])::text[])));