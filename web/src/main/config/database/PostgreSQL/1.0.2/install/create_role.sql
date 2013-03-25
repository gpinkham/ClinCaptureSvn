-- ClinCapture database role creation script
-- If the role already exists you will get : ERROR: role "clincapture" already exists SQL state: 42710
--
CREATE ROLE clincapture LOGIN
  ENCRYPTED PASSWORD 'clinica'
  SUPERUSER NOINHERIT NOCREATEDB NOCREATEROLE;