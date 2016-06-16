--
-- PostgreSQL database dump
--

-- Dumped from database version 8.4.3
-- Dumped by pg_dump version 9.2.0
-- Started on 2016-06-15 15:16:53

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 1130 (class 2612 OID 16386)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

--
-- TOC entry 366 (class 1255 OID 143845135)
-- Name: disable_event_crf(integer, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION disable_event_crf(ec_id integer, u_id integer, pos integer, state integer) RETURNS void
    LANGUAGE plpgsql
    AS 'DECLARE
            BEGIN
                update event_crf set update_id = u_id, date_updated = now(), states = update_states(states, pos, state), old_status_id = case when status_id != 0 and status_id != 5 and status_id != 6 and status_id != 7 then status_id else old_status_id end, status_id = get_from_states(states, status_id, pos, state) where event_crf_id = ec_id;
                update item_data idt set update_id = u_id, date_updated = now(), old_status_id = case when status_id != 0 and status_id != 5 and status_id != 6 and status_id != 7 then status_id else old_status_id end, status_id = (select ec.status_id from event_crf ec where ec.event_crf_id = idt.event_crf_id) where idt.event_crf_id = ec_id;
                update coded_item ci set status = (select case when idt.status_id = 5 or idt.status_id = 7 then ''REMOVED'' else (case when idt.status_id = 6 then ''LOCKED'' else ci.status end) end from item_data idt where idt.item_data_id = ci.item_id) where ci.item_id in (select idt.item_data_id from item_data idt where idt.event_crf_id = ec_id);
                update item_data itdt set update_id = u_id, date_updated = now(), value = (select cie.item_code from coded_item_element cie where cie.item_data_id = itdt.item_data_id) where itdt.item_data_id in (select cie.item_data_id from coded_item_element cie join coded_item ci on ci.id = cie.coded_item_id join item_data idt on idt.item_data_id = ci.item_id and idt.event_crf_id = ec_id);
            END;
            ';


ALTER FUNCTION public.disable_event_crf(ec_id integer, u_id integer, pos integer, state integer) OWNER TO clincapture;

--
-- TOC entry 367 (class 1255 OID 143845136)
-- Name: disable_event_crfs_by_crf_version(integer, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION disable_event_crfs_by_crf_version(cv_id integer, u_id integer, pos integer, state integer) RETURNS void
    LANGUAGE plpgsql
    AS 'DECLARE
            BEGIN
                update event_crf set update_id = u_id, date_updated = now(), states = update_states(states, pos, state), old_status_id = case when status_id != 0 and status_id != 5 and status_id != 6 and status_id != 7 then status_id else old_status_id end, status_id = get_from_states(states, status_id, pos, state) where event_crf_id in (select event_crf_id from event_crf where crf_version_id = cv_id);
                update item_data idt set update_id = u_id, date_updated = now(), old_status_id = case when status_id != 0 and status_id != 5 and status_id != 6 and status_id != 7 then status_id else old_status_id end, status_id = (select ec.status_id from event_crf ec where ec.event_crf_id = idt.event_crf_id) where idt.event_crf_id in (select event_crf_id from event_crf where crf_version_id = cv_id);
                update coded_item ci set status = (select case when idt.status_id = 5 or idt.status_id = 7 then ''REMOVED'' else (case when idt.status_id = 6 then ''LOCKED'' else ci.status end) end from item_data idt where idt.item_data_id = ci.item_id) where ci.item_id in (select idt.item_data_id from item_data idt join event_crf ec on ec.event_crf_id = idt.event_crf_id and ec.crf_version_id = cv_id);
                update item_data itdt set update_id = u_id, date_updated = now(), value = (select cie.item_code from coded_item_element cie where cie.item_data_id = itdt.item_data_id) where itdt.item_data_id in (select cie.item_data_id from coded_item_element cie join coded_item ci on ci.id = cie.coded_item_id join item_data idt on idt.item_data_id = ci.item_id join event_crf ec on ec.event_crf_id = idt.event_crf_id and ec.crf_version_id = cv_id);
            END;
            ';


ALTER FUNCTION public.disable_event_crfs_by_crf_version(cv_id integer, u_id integer, pos integer, state integer) OWNER TO clincapture;

--
-- TOC entry 368 (class 1255 OID 143845137)
-- Name: disable_event_crfs_by_study_event(integer, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION disable_event_crfs_by_study_event(se_id integer, u_id integer, pos integer, state integer) RETURNS void
    LANGUAGE plpgsql
    AS 'DECLARE
            BEGIN
                update event_crf set update_id = u_id, date_updated = now(), states = update_states(states, pos, state), old_status_id = case when status_id != 0 and status_id != 5 and status_id != 6 and status_id != 7 then status_id else old_status_id end, status_id = get_from_states(states, status_id, pos, state) where event_crf_id in (select event_crf_id from event_crf where study_event_id = se_id);
                update item_data idt set update_id = u_id, date_updated = now(), old_status_id = case when status_id != 0 and status_id != 5 and status_id != 6 and status_id != 7 then status_id else old_status_id end, status_id = (select ec.status_id from event_crf ec where ec.event_crf_id = idt.event_crf_id) where idt.event_crf_id in (select event_crf_id from event_crf where study_event_id = se_id);
                update coded_item ci set status = (select case when idt.status_id = 5 or idt.status_id = 7 then ''REMOVED'' else (case when idt.status_id = 6 then ''LOCKED'' else ci.status end) end from item_data idt where idt.item_data_id = ci.item_id) where ci.item_id in (select idt.item_data_id from item_data idt join event_crf ec on ec.event_crf_id = idt.event_crf_id and ec.study_event_id = se_id);
                update item_data itdt set update_id = u_id, date_updated = now(), value = (select cie.item_code from coded_item_element cie where cie.item_data_id = itdt.item_data_id) where itdt.item_data_id in (select cie.item_data_id from coded_item_element cie join coded_item ci on ci.id = cie.coded_item_id join item_data idt on idt.item_data_id = ci.item_id join event_crf ec on ec.event_crf_id = idt.event_crf_id and ec.study_event_id = se_id);
            END;
            ';


ALTER FUNCTION public.disable_event_crfs_by_study_event(se_id integer, u_id integer, pos integer, state integer) OWNER TO clincapture;

--
-- TOC entry 364 (class 1255 OID 143845249)
-- Name: disable_event_crfs_by_study_event_and_crf_oid(integer, text, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION disable_event_crfs_by_study_event_and_crf_oid(se_id integer, poid text, u_id integer, pos integer, state integer) RETURNS void
    LANGUAGE plpgsql
    AS 'DECLARE
            BEGIN
                update event_crf set update_id = u_id, date_updated = now(), states = update_states(states, pos, state), old_status_id = case when status_id != 0 and status_id != 5 and status_id != 6 and status_id != 7 then status_id else old_status_id end, status_id = get_from_states(states, status_id, pos, state) where event_crf_id in (select ec.event_crf_id from event_crf ec join crf_version cv on cv.crf_version_id = ec.crf_version_id join crf c on c.crf_id = cv.crf_id and c.oc_oid = poid where ec.study_event_id = se_id);
                update item_data idt set update_id = u_id, date_updated = now(), old_status_id = case when status_id != 0 and status_id != 5 and status_id != 6 and status_id != 7 then status_id else old_status_id end, status_id = (select ec.status_id from event_crf ec where ec.event_crf_id = idt.event_crf_id) where idt.event_crf_id in (select ec.event_crf_id from event_crf ec join crf_version cv on cv.crf_version_id = ec.crf_version_id join crf c on c.crf_id = cv.crf_id and c.oc_oid = poid where ec.study_event_id = se_id);
                update coded_item ci set status = (select case when idt.status_id = 5 or idt.status_id = 7 then ''REMOVED'' else (case when idt.status_id = 6 then ''LOCKED'' else ci.status end) end from item_data idt where idt.item_data_id = ci.item_id) where ci.item_id in (select idt.item_data_id from item_data idt join event_crf ec on ec.event_crf_id = idt.event_crf_id and ec.study_event_id = se_id join crf_version cv on cv.crf_version_id = ec.crf_version_id join crf c on c.crf_id = cv.crf_id and c.oc_oid = poid);
                update item_data itdt set update_id = u_id, date_updated = now(), value = (select cie.item_code from coded_item_element cie where cie.item_data_id = itdt.item_data_id) where itdt.item_data_id in (select cie.item_data_id from coded_item_element cie join coded_item ci on ci.id = cie.coded_item_id join item_data idt on idt.item_data_id = ci.item_id join event_crf ec on ec.event_crf_id = idt.event_crf_id and ec.study_event_id = se_id join crf_version cv on cv.crf_version_id = ec.crf_version_id join crf c on c.crf_id = cv.crf_id and c.oc_oid = poid);
            END;
            ';


ALTER FUNCTION public.disable_event_crfs_by_study_event_and_crf_oid(se_id integer, poid text, u_id integer, pos integer, state integer) OWNER TO clincapture;

--
-- TOC entry 369 (class 1255 OID 143845139)
-- Name: enable_event_crf(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION enable_event_crf(ec_id integer, u_id integer, pos integer) RETURNS void
    LANGUAGE plpgsql
    AS 'DECLARE
            BEGIN
                update event_crf set update_id = u_id, date_updated = now(), states = update_states(states, pos, 0), status_id = revert_from_states(states, old_status_id, date_completed, pos) where event_crf_id = ec_id;
                update item_data idt set update_id = u_id, date_updated = now(), status_id = enable_item_data(old_status_id, (select ec.status_id from event_crf ec where ec.event_crf_id = idt.event_crf_id)) where idt.event_crf_id = ec_id;
                update coded_item ci set status = (select case when idt.status_id = 5 or idt.status_id = 7 then ''REMOVED'' else (case when idt.status_id = 6 then ''LOCKED'' else (case when ci.http_path is null or ci.http_path = '''' then ''NOT_CODED'' else ''CODED'' end) end) end from item_data idt where idt.item_data_id = ci.item_id) where ci.item_id in (select idt.item_data_id from item_data idt where idt.event_crf_id = ec_id);
                update item_data itdt set update_id = u_id, date_updated = now(), value = (select cie.item_code from coded_item_element cie where cie.item_data_id = itdt.item_data_id) where itdt.item_data_id in (select cie.item_data_id from coded_item_element cie join coded_item ci on ci.id = cie.coded_item_id join item_data idt on idt.item_data_id = ci.item_id and idt.event_crf_id = ec_id);
            END;
            ';


ALTER FUNCTION public.enable_event_crf(ec_id integer, u_id integer, pos integer) OWNER TO clincapture;

--
-- TOC entry 370 (class 1255 OID 143845140)
-- Name: enable_event_crfs_by_crf_version(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION enable_event_crfs_by_crf_version(cv_id integer, u_id integer, pos integer) RETURNS void
    LANGUAGE plpgsql
    AS 'DECLARE
            BEGIN
                update event_crf set update_id = u_id, date_updated = now(), states = update_states(states, pos, 0), status_id = revert_from_states(states, old_status_id, date_completed, pos) where event_crf_id in (select event_crf_id from event_crf where crf_version_id = cv_id);
                update item_data idt set update_id = u_id, date_updated = now(), status_id = enable_item_data(old_status_id, (select ec.status_id from event_crf ec where ec.event_crf_id = idt.event_crf_id)) where idt.event_crf_id in (select event_crf_id from event_crf where crf_version_id = cv_id);
                update coded_item ci set status = (select case when idt.status_id = 5 or idt.status_id = 7 then ''REMOVED'' else (case when idt.status_id = 6 then ''LOCKED'' else (case when ci.http_path is null or ci.http_path = '''' then ''NOT_CODED'' else ''CODED'' end) end) end from item_data idt where idt.item_data_id = ci.item_id) where ci.item_id in (select idt.item_data_id from item_data idt join event_crf ec on ec.event_crf_id = idt.event_crf_id and ec.crf_version_id = cv_id);
                update item_data itdt set update_id = u_id, date_updated = now(), value = (select cie.item_code from coded_item_element cie where cie.item_data_id = itdt.item_data_id) where itdt.item_data_id in (select cie.item_data_id from coded_item_element cie join coded_item ci on ci.id = cie.coded_item_id join item_data idt on idt.item_data_id = ci.item_id join event_crf ec on ec.event_crf_id = idt.event_crf_id and ec.crf_version_id = cv_id);
            END;
            ';


ALTER FUNCTION public.enable_event_crfs_by_crf_version(cv_id integer, u_id integer, pos integer) OWNER TO clincapture;

--
-- TOC entry 371 (class 1255 OID 143845141)
-- Name: enable_event_crfs_by_study_event(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION enable_event_crfs_by_study_event(se_id integer, u_id integer, pos integer) RETURNS void
    LANGUAGE plpgsql
    AS 'DECLARE
            BEGIN
                update event_crf set update_id = u_id, date_updated = now(), states = update_states(states, pos, 0), status_id = revert_from_states(states, old_status_id, date_completed, pos) where event_crf_id in (select event_crf_id from event_crf where study_event_id = se_id);
                update item_data idt set update_id = u_id, date_updated = now(), status_id = enable_item_data(old_status_id, (select ec.status_id from event_crf ec where ec.event_crf_id = idt.event_crf_id)) where idt.event_crf_id in (select event_crf_id from event_crf where study_event_id = se_id);
                update coded_item ci set status = (select case when idt.status_id = 5 or idt.status_id = 7 then ''REMOVED'' else (case when idt.status_id = 6 then ''LOCKED'' else (case when ci.http_path is null or ci.http_path = '''' then ''NOT_CODED'' else ''CODED'' end) end) end from item_data idt where idt.item_data_id = ci.item_id) where ci.item_id in (select idt.item_data_id from item_data idt join event_crf ec on ec.event_crf_id = idt.event_crf_id and ec.study_event_id = se_id);
                update item_data itdt set update_id = u_id, date_updated = now(), value = (select cie.item_code from coded_item_element cie where cie.item_data_id = itdt.item_data_id) where itdt.item_data_id in (select cie.item_data_id from coded_item_element cie join coded_item ci on ci.id = cie.coded_item_id join item_data idt on idt.item_data_id = ci.item_id join event_crf ec on ec.event_crf_id = idt.event_crf_id and ec.study_event_id = se_id);
            END;
            ';


ALTER FUNCTION public.enable_event_crfs_by_study_event(se_id integer, u_id integer, pos integer) OWNER TO clincapture;

--
-- TOC entry 365 (class 1255 OID 143845250)
-- Name: enable_event_crfs_by_study_event_and_crf_oid(integer, text, integer, integer); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION enable_event_crfs_by_study_event_and_crf_oid(se_id integer, poid text, u_id integer, pos integer) RETURNS void
    LANGUAGE plpgsql
    AS 'DECLARE
            BEGIN
                update event_crf set update_id = u_id, date_updated = now(), states = update_states(states, pos, 0), status_id = revert_from_states(states, old_status_id, date_completed, pos) where event_crf_id in (select ec.event_crf_id from event_crf ec join crf_version cv on cv.crf_version_id = ec.crf_version_id join crf c on c.crf_id = cv.crf_id and c.oc_oid = poid where ec.study_event_id = se_id);
                update item_data idt set update_id = u_id, date_updated = now(), status_id = enable_item_data(old_status_id, (select ec.status_id from event_crf ec where ec.event_crf_id = idt.event_crf_id)) where idt.event_crf_id in (select ec.event_crf_id from event_crf ec join crf_version cv on cv.crf_version_id = ec.crf_version_id join crf c on c.crf_id = cv.crf_id and c.oc_oid = poid where ec.study_event_id = se_id);
                update coded_item ci set status = (select case when idt.status_id = 5 or idt.status_id = 7 then ''REMOVED'' else (case when idt.status_id = 6 then ''LOCKED'' else (case when ci.http_path is null or ci.http_path = '''' then ''NOT_CODED'' else ''CODED'' end) end) end from item_data idt where idt.item_data_id = ci.item_id) where ci.item_id in (select idt.item_data_id from item_data idt join event_crf ec on ec.event_crf_id = idt.event_crf_id and ec.study_event_id = se_id join crf_version cv on cv.crf_version_id = ec.crf_version_id join crf c on c.crf_id = cv.crf_id and c.oc_oid = poid);
                update item_data itdt set update_id = u_id, date_updated = now(), value = (select cie.item_code from coded_item_element cie where cie.item_data_id = itdt.item_data_id) where itdt.item_data_id in (select cie.item_data_id from coded_item_element cie join coded_item ci on ci.id = cie.coded_item_id join item_data idt on idt.item_data_id = ci.item_id join event_crf ec on ec.event_crf_id = idt.event_crf_id and ec.study_event_id = se_id join crf_version cv on cv.crf_version_id = ec.crf_version_id join crf c on c.crf_id = cv.crf_id and c.oc_oid = poid);
            END;
            ';


ALTER FUNCTION public.enable_event_crfs_by_study_event_and_crf_oid(se_id integer, poid text, u_id integer, pos integer) OWNER TO clincapture;

--
-- TOC entry 373 (class 1255 OID 143845144)
-- Name: enable_item_data(integer, integer); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION enable_item_data(old_status integer, ev_status integer) RETURNS integer
    LANGUAGE plpgsql
    AS 'DECLARE
            val integer;
            BEGIN
                IF ev_status != 0 AND ev_status != 5 AND ev_status != 6 AND ev_status != 7 AND old_status != 0 AND old_status != 5 AND old_status != 6 AND old_status != 7 THEN
                    val = old_status;
                ELSE
                    val = ev_status;
                END IF;
                RETURN val;
            END;
            ';


ALTER FUNCTION public.enable_item_data(old_status integer, ev_status integer) OWNER TO clincapture;

--
-- TOC entry 361 (class 1255 OID 143842717)
-- Name: event_crf_trigger(); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION event_crf_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS 'DECLARE
            pk INTEGER;
            entity_name_value TEXT;
            crfName TEXT;
            crfVersion TEXT;
            sId INTEGER;
            crfId INTEGER;
            edcId INTEGER;
            sedId INTEGER;
            BEGIN
            IF (TG_OP = ''DELETE'') THEN
            IF (OLD.not_started = false) THEN
            SELECT INTO sId ss.study_id FROM study_subject ss WHERE ss.study_subject_id = OLD.study_subject_id;
            SELECT INTO crfId cv.crf_id FROM crf_version cv WHERE cv.crf_version_id = OLD.crf_version_id;
            SELECT INTO sedId se.study_event_definition_id FROM study_event se WHERE se.study_event_id = OLD.study_event_id;
            SELECT INTO edcId edc.event_definition_crf_id FROM event_definition_crf edc, study s WHERE s.study_id = sId AND edc.study_event_definition_id = sedId AND edc.crf_id = crfId AND ((s.parent_study_id is null and edc.study_id = s.study_id) or (not(s.parent_study_id is null) and (edc.study_id = s.study_id or edc.study_id = s.parent_study_id) and edc.event_definition_crf_id not in (select parent_id from event_definition_crf edc where edc.study_id = s.study_id)));
            SELECT INTO crfName c.name FROM crf_version cv, crf c WHERE c.crf_id = cv.crf_id AND cv.crf_version_id = OLD.crf_version_id;
            SELECT INTO crfVersion cv.name FROM crf_version cv WHERE cv.crf_version_id = OLD.crf_version_id;
            INSERT INTO audit_log_event(audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id, study_event_id, event_crf_version_id, date_interviewed, interviewer_name, event_definition_crf_id, study_event_definition_id, study_subject_id, name, version)
            VALUES (''54'', now(), OLD.update_id, ''event_crf'', OLD.event_crf_id, ''Status'', OLD.status_id, 5, OLD.event_crf_id, OLD.study_event_id, OLD.crf_version_id, OLD.date_interviewed, OLD.interviewer_name, edcId, sedId, OLD.study_subject_id, crfName, crfVersion);
            END IF;
            RETURN NULL;
            ELSIF (TG_OP = ''UPDATE'') THEN
            IF(OLD.status_id <> NEW.status_id) THEN
            /*---------------*/
            /*Event CRF status changed*/
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            SELECT INTO entity_name_value ''Status'';
            IF(OLD.status_id = ''1'' AND NEW.status_id = ''2'') THEN
            IF (NEW.electronic_signature_status) THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
            VALUES (pk, ''14'', now(), NEW.update_id, ''event_crf'', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
            ELSE
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
            VALUES (pk, ''8'', now(), NEW.update_id, ''event_crf'', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
            END IF;
            ELSIF (OLD.status_id = ''1'' AND NEW.status_id = ''4'') THEN
            IF (NEW.electronic_signature_status) THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
            VALUES (pk, ''15'', now(), NEW.update_id, ''event_crf'', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
            ELSE
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
            VALUES (pk, ''10'', now(), NEW.update_id, ''event_crf'', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
            END IF;
            ELSIF (OLD.status_id = ''4'' AND NEW.status_id = ''2'') THEN
            IF (NEW.electronic_signature_status) THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
            VALUES (pk, ''16'', now(), NEW.update_id, ''event_crf'', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
            ELSE
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
            VALUES (pk, ''11'', now(), NEW.update_id, ''event_crf'', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
            END IF;
            END IF;
            /*---------------*/
            END IF;

            IF(OLD.date_interviewed <> NEW.date_interviewed) THEN
            /*---------------*/
            /*Event CRF date interviewed*/
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            SELECT INTO entity_name_value ''Date interviewed'';
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
            VALUES (pk, ''9'', now(), NEW.update_id, ''event_crf'', NEW.event_crf_id, entity_name_value, OLD.date_interviewed, NEW.date_interviewed, NEW.event_crf_id);
            /*---------------*/
            END IF;

            IF((OLD.interviewer_name <> NEW.interviewer_name) AND (OLD.interviewer_name <> '''')) THEN
            /*---------------*/
            /*Event CRF interviewer name*/
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            SELECT INTO entity_name_value ''Interviewer Name'';
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
            VALUES (pk, ''9'', now(), NEW.update_id, ''event_crf'', NEW.event_crf_id, entity_name_value, OLD.interviewer_name, NEW.interviewer_name, NEW.event_crf_id);
            /*---------------*/
            END IF;

            IF(OLD.sdv_status <> NEW.sdv_status) THEN
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            SELECT INTO entity_name_value ''EventCRF SDV Status'';
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
            VALUES (pk, ''32'', now(), NEW.sdv_update_id, ''event_crf'', NEW.event_crf_id, entity_name_value,
            (select case when OLD.sdv_status is true then TRUE else FALSE end),
            (select case when NEW.sdv_status is true then TRUE else FALSE end), NEW.event_crf_id);
            /*---------------*/
            END IF;
            RETURN NULL;  /*return values ignored for ''after'' triggers*/
            END IF;
            END;
            ';


ALTER FUNCTION public.event_crf_trigger() OWNER TO clincapture;

--
-- TOC entry 353 (class 1255 OID 143843141)
-- Name: event_definition_crf_trigger(); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION event_definition_crf_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS 'DECLARE
            pk INTEGER;
            se_id INTEGER;
            cv_id INTEGER;
            entity_name_value TEXT;
            BEGIN
                IF (TG_OP = ''UPDATE'') THEN
                    IF(OLD.status_id <> NEW.status_id) THEN
                        /*---------------*/
                        /*Event CRF status changed*/
                        SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
                        SELECT INTO entity_name_value ''Status'';
                        IF(NEW.status_id = ''5'') THEN
                            SELECT INTO se_id se.study_event_id FROM study_event se WHERE se.study_event_definition_id = NEW.study_event_definition_id;
                            SELECT INTO cv_id ec.crf_version_id FROM event_crf ec, study_event se WHERE se.study_event_definition_id = NEW.study_event_definition_id and ec.study_event_id = se.study_event_id;

                            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, study_event_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id, event_crf_version_id)
                                        VALUES (pk, ''13'',se_id, now(), NEW.update_id, ''event_definition_crf'', NEW.event_definition_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_definition_crf_id, cv_id);
                        END IF;
                    END IF;
                    RETURN NULL;  /*return values ignored for ''after'' triggers*/
                END IF;
            END;
        ';


ALTER FUNCTION public.event_definition_crf_trigger() OWNER TO clincapture;

--
-- TOC entry 356 (class 1255 OID 143843160)
-- Name: fix_duplicates_in_study_defs(); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION fix_duplicates_in_study_defs() RETURNS void
    LANGUAGE plpgsql
    AS 'DECLARE
		    maxOrdinal INTEGER DEFAULT 1;
		    mviews RECORD;
		    mviews2 RECORD;
		
		    BEGIN
			FOR mviews2 in select ordinal, count(*) as cnt from study_event_definition sed group by ordinal
				LOOP
				IF mviews2.cnt > 1 THEN
		
					FOR mviews in select study_event_definition_id as sid from study_event_definition sed order by sed.study_event_definition_id
						LOOP
						UPDATE study_event_definition set ordinal = maxOrdinal where study_event_definition_id = mviews.sid;
						
						maxOrdinal := maxOrdinal + 1;
			
						END LOOP;
					EXIT;
				END IF;
				END LOOP;
		    END;
		    ';


ALTER FUNCTION public.fix_duplicates_in_study_defs() OWNER TO clincapture;

--
-- TOC entry 360 (class 1255 OID 143844115)
-- Name: fix_orders(); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION fix_orders() RETURNS void
    LANGUAGE plpgsql
    AS 'DECLARE rec RECORD;
                     studies RECORD;
                        BEGIN
                            FOR studies in SELECT DISTINCT study_id as sid FROM study_event_definition
                                LOOP
                                   FOR rec in select ordinal, ROW_NUMBER() OVER (ORDER BY ordinal) as cnt, sed.study_event_definition_id as id from study_event_definition sed where sed.study_id = studies.sid order by ordinal
                                       LOOP
                                          UPDATE study_event_definition set ordinal = rec.cnt where rec.id = study_event_definition.study_event_definition_id;
                                       END LOOP;
                                END LOOP;
                        END;';


ALTER FUNCTION public.fix_orders() OWNER TO clincapture;

--
-- TOC entry 355 (class 1255 OID 143843159)
-- Name: fix_rule_referencing_cross_study(); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION fix_rule_referencing_cross_study() RETURNS void
    LANGUAGE plpgsql
    AS 'DECLARE
		    
		     newExpressionId INTEGER DEFAULT 0;
		     newRuleId INTEGER DEFAULT 0;
		     mviews RECORD;
		
		    BEGIN
		
		    FOR mviews in select r.rule_expression_id as rrule_expression_id, rs.study_id as rsstudy_id, rsr.rule_id as rsrrule_id, rsr.id as rsrid  from rule_set rs, rule r,rule_set_rule rsr where  rsr.rule_set_id = rs.id and rule_id = r.id and  rs.study_id != r.study_id 
		    LOOP
		        newExpressionId := NEXTVAL(''rule_expression_id_seq'');
		        newRuleId := NEXTVAL(''rule_id_seq'');
		        INSERT INTO rule_expression select newExpressionId,value,context,owner_id,date_created,date_updated,update_id,status_id,0 from rule_expression where id = mviews.rrule_expression_id;
		        INSERT INTO rule SELECT newRuleId,name,description,oc_oid,enabled,newExpressionId,owner_id,date_created,date_updated,update_id,status_id,0,mviews.rsstudy_id FROM rule WHERE id = mviews.rsrrule_id ;
		        UPDATE rule_set_rule rsr set rule_id = newRuleId where rsr.id = mviews.rsrid;
		    END LOOP;
		
		    END;
		    ';


ALTER FUNCTION public.fix_rule_referencing_cross_study() OWNER TO clincapture;

--
-- TOC entry 372 (class 1255 OID 143845143)
-- Name: get_from_states(text, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION get_from_states(states text, current_status integer, pos integer, status integer) RETURNS integer
    LANGUAGE plpgsql
    AS 'DECLARE
            arr integer[];
            val integer;
            BEGIN
                val = 0;
                arr = regexp_split_to_array(states, '','');
                arr[pos] = status;
                FOR i IN REVERSE array_length(arr, 1)..1 LOOP
                    IF val = 0 THEN
                        val = arr[i];
                    END IF;
                END LOOP;
                IF val = 0 THEN
                    val = current_status;
                END IF;
                RETURN val;
            END;
            ';


ALTER FUNCTION public.get_from_states(states text, current_status integer, pos integer, status integer) OWNER TO clincapture;

--
-- TOC entry 349 (class 1255 OID 143842718)
-- Name: global_subject_trigger(); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION global_subject_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS 'DECLARE
			pk INTEGER;
			entity_name_value TEXT;
		BEGIN
			IF (TG_OP = ''INSERT'') THEN
				/*---------------*/
				 /*Subject created*/
				SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
				INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id)
					VALUES (pk, ''5'', now(), NEW.owner_id, ''subject'', NEW.subject_id);
				RETURN NULL; /*return values ignored for ''after'' triggers*/
				/*---------------*/
			ELSIF (TG_OP = ''UPDATE'') THEN
				IF(OLD.status_id <> NEW.status_id) THEN
				/*---------------*/
				 /*Subject status changed*/
				SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
				SELECT INTO entity_name_value ''Status'';
				INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
					VALUES (pk, ''6'', now(), NEW.update_id, ''subject'', NEW.subject_id, entity_name_value, OLD.status_id, NEW.status_id);
				/*---------------*/
				END IF;
		
				IF(OLD.unique_identifier <> NEW.unique_identifier) THEN
				/*---------------*/
				/*Subject value changed*/
				SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
				SELECT INTO entity_name_value ''Person ID'';
				INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
					VALUES (pk, ''7'', now(), NEW.update_id, ''subject'', NEW.subject_id, entity_name_value, OLD.unique_identifier, NEW.unique_identifier);
				/*---------------*/
				END IF;
		
				IF(OLD.date_of_birth <> NEW.date_of_birth) THEN
				/*---------------*/
				 /*Subject value changed*/
				SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
				SELECT INTO entity_name_value ''Date of Birth'';
				INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
					VALUES (pk, ''7'', now(), NEW.update_id, ''subject'', NEW.subject_id, entity_name_value, OLD.date_of_birth, NEW.date_of_birth);
				/*---------------*/
				END IF;
		
		        IF(OLD.gender <> NEW.gender) THEN
		   		/*---------------*/
		   		/*Subject value changed*/
		   		SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		   		SELECT INTO entity_name_value ''Gender'';
		   		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
				VALUES (pk, ''7'', now(), NEW.update_id, ''subject'', NEW.subject_id, entity_name_value, OLD.gender, NEW.gender);
		   		/*---------------*/
		   		END IF;
				
			RETURN NULL;  /*return values ignored for ''after'' triggers*/
			END IF;
		END;
		';


ALTER FUNCTION public.global_subject_trigger() OWNER TO clincapture;

--
-- TOC entry 358 (class 1255 OID 143843549)
-- Name: item_data_initial_trigger(); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION item_data_initial_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS 'DECLARE
            pk INTEGER;
            entity_name_value TEXT;
            std_evnt_id INTEGER;
            crf_version_id INTEGER;
            BEGIN
            IF (TG_OP = ''INSERT'' and length(NEW.value)>0) THEN
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            SELECT INTO entity_name_value item.name FROM item WHERE item.item_id = NEW.item_id;
            SELECT INTO std_evnt_id ec.study_event_id FROM event_crf ec WHERE ec.event_crf_id = NEW.event_crf_id;
            SELECT INTO crf_version_id ec.crf_version_id FROM event_crf ec WHERE ec.event_crf_id = NEW.event_crf_id;
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, reason_for_change, new_value, event_crf_id, study_event_id, event_crf_version_id, ordinal)
            VALUES (pk, ''1'', now(), NEW.owner_id, ''item_data'', NEW.item_data_id, entity_name_value, ''initial value'', NEW.value, NEW.event_crf_id, std_evnt_id, crf_version_id, NEW.ordinal);
            RETURN NULL;
            END IF;
            RETURN NULL;
            END;
            ';


ALTER FUNCTION public.item_data_initial_trigger() OWNER TO clincapture;

--
-- TOC entry 359 (class 1255 OID 143842719)
-- Name: item_data_trigger(); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION item_data_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS 'DECLARE
            pk INTEGER;
            entity_name_value TEXT;
            std_evnt_id INTEGER;
            crf_version_id INTEGER;
            coded_item_id INTEGER;

            BEGIN
            IF (TG_OP = ''DELETE'') THEN
            /*---------------*/
            /*Item data deleted (by deleting an event crf)*/
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            SELECT INTO entity_name_value item.name FROM item WHERE item.item_id = OLD.item_id;
            SELECT INTO std_evnt_id ec.study_event_id FROM event_crf ec WHERE ec.event_crf_id = OLD.event_crf_id;
            SELECT INTO crf_version_id ec.crf_version_id FROM event_crf ec WHERE ec.event_crf_id = OLD.event_crf_id;
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, event_crf_id, study_event_id, event_crf_version_id, ordinal)
            VALUES (pk, ''13'', now(), OLD.update_id, ''item_data'', OLD.item_data_id, entity_name_value, OLD.value, OLD.event_crf_id, std_evnt_id, crf_version_id, OLD.ordinal);
            RETURN NULL; --return values ignored for ''after'' triggers

            ELSIF (TG_OP = ''UPDATE'') THEN

            IF(OLD.status_id <> NEW.status_id) THEN
            /*---------------*/
            /*Item data status changed (by removing an event crf)*/
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            SELECT INTO entity_name_value item.name FROM item WHERE item.item_id = NEW.item_id;
            SELECT INTO coded_item_id coded_item_element.coded_item_id FROM coded_item_element WHERE coded_item_element.item_data_id = NEW.item_data_id;
            IF (coded_item_id > 0) THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
            VALUES (pk, ''53'', now(), NEW.update_id, ''item_data'', NEW.item_data_id, entity_name_value, OLD.value, NEW.value, NEW.event_crf_id);
            ELSE
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
            VALUES (pk, ''12'', now(), NEW.update_id, ''item_data'', NEW.item_data_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
            END IF;
            /*---------------*/
            END IF;

            IF(OLD.value <> NEW.value) THEN
            /*---------------*/
            /*Item data updated*/
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            SELECT INTO entity_name_value item.name FROM item WHERE item.item_id = NEW.item_id;
            SELECT INTO coded_item_id coded_item_element.coded_item_id FROM coded_item_element WHERE coded_item_element.item_data_id = NEW.item_data_id;
            IF (coded_item_id > 0) THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
            VALUES (pk, ''53'', now(), NEW.update_id, ''item_data'', NEW.item_data_id, entity_name_value, OLD.value, NEW.value, NEW.event_crf_id);
            ELSE
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
            VALUES (pk, ''1'', now(), NEW.update_id, ''item_data'', NEW.item_data_id, entity_name_value, OLD.value, NEW.value, NEW.event_crf_id);
            END IF;
            DELETE FROM rule_action_run_log where item_data_id = NEW.item_data_id;
            /*---------------*/
            END IF;
            RETURN NULL;  /*return values ignored for ''after'' triggers*/
            END IF;
            RETURN NULL;  /*return values ignored for ''after'' triggers*/
            END;
            ';


ALTER FUNCTION public.item_data_trigger() OWNER TO clincapture;

--
-- TOC entry 354 (class 1255 OID 143843143)
-- Name: populate_ssid_in_didm_trigger(); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION populate_ssid_in_didm_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS '
        BEGIN
            update dn_item_data_map  set study_subject_id = 
            (
                select DISTINCT se.study_subject_id from study_event se, event_crf ec, item_data id where 
                id.event_crf_id = ec.event_crf_id and ec.study_event_id = se.study_event_id and id.item_data_id = dn_item_data_map.item_data_id
            ) where study_subject_id is null;
        RETURN NULL;    
        END;
        ';


ALTER FUNCTION public.populate_ssid_in_didm_trigger() OWNER TO clincapture;

--
-- TOC entry 357 (class 1255 OID 143842723)
-- Name: repeating_item_data_trigger(); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION repeating_item_data_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS 'DECLARE
            pk INTEGER;
            entity_name_value TEXT;
            std_evnt_id INTEGER;
            crf_version_id INTEGER;
            validator_id INTEGER;
            event_crf_status_id INTEGER;
            BEGIN
            IF (TG_OP = ''INSERT'') THEN
            /*---------------*/
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            SELECT INTO entity_name_value item.name FROM item WHERE item.item_id = NEW.item_id;
            SELECT INTO std_evnt_id ec.study_event_id FROM event_crf ec WHERE ec.event_crf_id = NEW.event_crf_id;
            SELECT INTO crf_version_id ec.crf_version_id FROM event_crf ec WHERE ec.event_crf_id = NEW.event_crf_id;
            SELECT INTO validator_id ec.validator_id FROM event_crf ec WHERE ec.event_crf_id = NEW.event_crf_id;
            SELECT INTO event_crf_status_id ec.status_id FROM event_crf ec WHERE ec.event_crf_id = NEW.event_crf_id;

            IF (NEW.status_id = ''2'' AND NEW.ordinal > 1 AND validator_id > 0 AND event_crf_status_id  = ''4'') THEN  /*DDE*/

            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, new_value, event_crf_id, study_event_id, event_crf_version_id, ordinal)
            VALUES (pk, ''30'', now(), NEW.owner_id, ''item_data'', NEW.item_data_id, entity_name_value, NEW.value, NEW.event_crf_id, std_evnt_id, crf_version_id, NEW.ordinal);
            ELSE
            IF(NEW.status_id =''2'' AND NEW.ordinal > 1  AND event_crf_status_id  = ''2'') THEN /*ADE*/
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, new_value, event_crf_id, study_event_id, event_crf_version_id, ordinal)
            VALUES (pk, ''30'', now(), NEW.owner_id, ''item_data'', NEW.item_data_id, entity_name_value, NEW.value, NEW.event_crf_id, std_evnt_id, crf_version_id, NEW.ordinal);
            END IF;
            END IF;
            RETURN NULL;  /*return values ignored for ''after'' triggers*/

            END IF;
            RETURN NULL;  /*return values ignored for ''after'' triggers*/
            END;
            ';


ALTER FUNCTION public.repeating_item_data_trigger() OWNER TO clincapture;

--
-- TOC entry 374 (class 1255 OID 143845145)
-- Name: revert_from_states(text, integer, timestamp with time zone, integer); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION revert_from_states(states text, old_status integer, dt_completed timestamp with time zone, pos integer) RETURNS integer
    LANGUAGE plpgsql
    AS 'DECLARE
            arr integer[];
            val integer;
            BEGIN
                val = 0;
                arr = regexp_split_to_array(states, '','');
                arr[pos] = 0;
                FOR i IN REVERSE array_length(arr, 1)..1 LOOP
                    IF val = 0 THEN
                        val = arr[i];
                    END IF;
                END LOOP;
                IF val = 0 THEN
                    IF old_status != 0 AND old_status != 5 AND old_status != 6 AND old_status != 7 THEN
                        val = old_status;
                    ELSE
                        IF dt_completed IS NOT NULL THEN
                            val = 2;
                        ELSE
                            val = 1;
                        END IF;
                    END IF;
                END IF;
                RETURN val;
            END;
            ';


ALTER FUNCTION public.revert_from_states(states text, old_status integer, dt_completed timestamp with time zone, pos integer) OWNER TO clincapture;

--
-- TOC entry 363 (class 1255 OID 143845032)
-- Name: save_partial_section_info(integer, integer); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION save_partial_section_info(eventcrfid integer, sectionid integer) RETURNS void
    LANGUAGE plpgsql
    AS 'DECLARE
			  ecsId INTEGER DEFAULT 0;
			BEGIN
			  select id into ecsId from event_crf_section where event_crf_id = eventCrfId and section_id = sectionId;
			  IF ecsId > 0
			  THEN
			    update event_crf_section set partial_saved = true where event_crf_id = eventCrfId and section_id = sectionId;
			  ELSE
			    insert into event_crf_section (event_crf_id, section_id, "version", partial_saved) values (eventCrfId, sectionId, 0, true);
			  END IF;
			END;
			';


ALTER FUNCTION public.save_partial_section_info(eventcrfid integer, sectionid integer) OWNER TO clincapture;

--
-- TOC entry 351 (class 1255 OID 143842721)
-- Name: study_event_trigger(); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION study_event_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS 'DECLARE
            pk INTEGER;
            BEGIN
            IF (TG_OP = ''DELETE'') THEN
            INSERT INTO audit_log_event(audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, study_event_id, ordinal, location, date_start, study_event_definition_id, study_subject_id)
            VALUES (''51'', now(), OLD.update_id, ''study_event'', OLD.study_event_id, ''Status'', OLD.status_id, 10, OLD.study_event_id, OLD.sample_ordinal, OLD.location, OLD.date_start, OLD.study_event_definition_id, OLD.study_subject_id);
            RETURN NULL;
            ELSIF (TG_OP = ''INSERT'') THEN
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            IF(NEW.subject_event_status_id = ''1'') THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''17'', now(), NEW.owner_id, ''study_event'', NEW.study_event_id, ''Status'',''0'', NEW.subject_event_status_id);
            END IF;
            END IF;

            IF (TG_OP = ''UPDATE'') THEN
            IF(OLD.subject_event_status_id <> NEW.subject_event_status_id) THEN
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            IF(NEW.subject_event_status_id = ''1'') THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''17'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Status'', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = ''3'') THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''18'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Status'', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = ''4'') THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''19'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Status'', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = ''5'') THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''20'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Status'', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = ''6'') THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''21'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Status'', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = ''7'') THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''22'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Status'', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = ''8'') THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''31'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Status'', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = ''9'') THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''50'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Status'', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = ''10'') THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''51'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Status'', OLD.subject_event_status_id, NEW.subject_event_status_id);
            END IF;
            END IF;
            IF(OLD.status_id <> NEW.status_id) THEN
            IF(NEW.status_id = ''5'' or NEW.status_id = ''1'') THEN
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''23'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Status'', OLD.status_id, NEW.status_id);
            END IF;
            END IF;
            IF(OLD.date_start <> NEW.date_start) THEN
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''24'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Start date'', OLD.date_start, NEW.date_start);
            END IF;
            IF(OLD.date_end <> NEW.date_end) THEN
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''25'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''End date'', OLD.date_end, NEW.date_end);
            END IF;
            IF(OLD.location <> NEW.location) THEN
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''26'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Location'', OLD.location, NEW.location);
            END IF;
            RETURN NULL;  /*return values ignored for ''after'' triggers*/
            END IF;
            RETURN NULL;
            END;
            ';


ALTER FUNCTION public.study_event_trigger() OWNER TO clincapture;

--
-- TOC entry 350 (class 1255 OID 143842720)
-- Name: study_subject_trigger(); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION study_subject_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS 'DECLARE
			pk INTEGER;
			entity_name_value TEXT;
		    old_unique_identifier TEXT;
		    new_unique_identifier TEXT;
		
		BEGIN
			IF (TG_OP = ''INSERT'') THEN
				/*---------------*/
				 /*Study subject created*/
				SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
				INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id)
					VALUES (pk, ''2'', now(), NEW.owner_id, ''study_subject'', NEW.study_subject_id);
				RETURN NULL; /*return values ignored for ''after'' triggers*/
				/*---------------*/
			ELSIF (TG_OP = ''UPDATE'') THEN
				IF(OLD.status_id <> NEW.status_id) THEN
				 /*---------------*/
				/*Study subject status changed*/
				SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
				SELECT INTO entity_name_value ''Status'';
				INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
					VALUES (pk, ''3'', now(), NEW.update_id, ''study_subject'', NEW.study_subject_id, entity_name_value, OLD.status_id, NEW.status_id);
				/*---------------*/
				END IF;
		
				IF(OLD.label <> NEW.label) THEN
				/*---------------*/
				 /*Study subject value changed*/
				SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
				SELECT INTO entity_name_value ''Study Subject ID'';
				INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
					VALUES (pk, ''4'', now(), NEW.update_id, ''study_subject'', NEW.study_subject_id, entity_name_value, OLD.label, NEW.label);
				/*---------------*/
				END IF;
		
				IF(OLD.secondary_label <> NEW.secondary_label) THEN
				/*---------------*/
				/*Study subject value changed*/
				SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
				SELECT INTO entity_name_value ''Secondary Subject ID'';
				INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
					VALUES (pk, ''4'', now(), NEW.update_id, ''study_subject'', NEW.study_subject_id, entity_name_value, OLD.secondary_label, NEW.secondary_label);
				/*---------------*/
				END IF;
		
				IF(OLD.enrollment_date <> NEW.enrollment_date) THEN
				/*---------------*/
				/*Study subject value changed*/
				SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
				SELECT INTO entity_name_value ''Enrollment Date'';
				INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
					VALUES (pk, ''4'', now(), NEW.update_id, ''study_subject'', NEW.study_subject_id, entity_name_value, OLD.enrollment_date, NEW.enrollment_date);
				 /*---------------*/
				END IF;
		
		        IF(OLD.study_id <> NEW.study_id) THEN
		         /*---------------*/
		         /*Subject reassigned*/
		        SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		        SELECT INTO entity_name_value ''Study id'';
		        SELECT INTO old_unique_identifier study.unique_identifier FROM study study WHERE study.study_id = OLD.study_id;
		        SELECT INTO new_unique_identifier study.unique_identifier FROM study study WHERE study.study_id = NEW.study_id;
		        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
		            VALUES (pk, ''27'', now(), NEW.update_id, ''study_subject'', NEW.study_subject_id, entity_name_value, old_unique_identifier, new_unique_identifier);
		        /*---------------*/
		        END IF;
		
			RETURN NULL;  /*return values ignored for ''after'' triggers*/
			END IF;
		END;
		';


ALTER FUNCTION public.study_subject_trigger() OWNER TO clincapture;

--
-- TOC entry 352 (class 1255 OID 143842722)
-- Name: subject_group_assignment_trigger(); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION subject_group_assignment_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS 'DECLARE
			pk INTEGER;
			group_name TEXT;
			old_group_name TEXT;
			new_group_name TEXT;
		BEGIN
			IF (TG_OP = ''INSERT'') THEN
		        SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		        SELECT INTO group_name sg.name FROM study_group sg WHERE sg.study_group_id = NEW.study_group_id;
		        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
		        VALUES (pk, ''28'', now(), NEW.owner_id, ''subject_group_map'', NEW.study_subject_id, ''Status'','''', group_name);
		    END IF;
			IF (TG_OP = ''UPDATE'') THEN
				IF(OLD.study_group_id <> NEW.study_group_id) THEN
		            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		            SELECT INTO old_group_name sg.name FROM study_group sg WHERE sg.study_group_id = OLD.study_group_id;
		            SELECT INTO new_group_name sg.name FROM study_group sg WHERE sg.study_group_id = NEW.study_group_id;
		            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
		            VALUES (pk, ''29'', now(), NEW.update_id, ''subject_group_map'', NEW.study_subject_id, ''Status'',old_group_name, new_group_name);
			    END IF;
		    	RETURN NULL;  /*return values ignored for ''after'' triggers*/
			END IF;
			RETURN NULL;
		END;';


ALTER FUNCTION public.subject_group_assignment_trigger() OWNER TO clincapture;

--
-- TOC entry 362 (class 1255 OID 143844212)
-- Name: update_event_crf_status(); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION update_event_crf_status() RETURNS trigger
    LANGUAGE plpgsql
    AS '
				DECLARE pk INTEGER;
				BEGIN
					SELECT INTO pk OLD.event_crf_id;
					RAISE NOTICE ''Event CRF % was updated.'', pk;

					IF (OLD.status_id != NEW.status_id) THEN
						UPDATE event_crf SET date_updated = NOW() WHERE event_crf_id = pk;
					END IF;
					RETURN NULL;
				END;
			';


ALTER FUNCTION public.update_event_crf_status() OWNER TO clincapture;

--
-- TOC entry 375 (class 1255 OID 143845146)
-- Name: update_states(text, integer, integer); Type: FUNCTION; Schema: public; Owner: clincapture
--

CREATE FUNCTION update_states(states text, pos integer, status integer) RETURNS text
    LANGUAGE plpgsql
    AS 'DECLARE
            arr integer[];
            BEGIN
                arr = regexp_split_to_array(states, '','');
                arr[pos] = status;
                RETURN array_to_string(arr, '','');
            END;
            ';


ALTER FUNCTION public.update_states(states text, pos integer, status integer) OWNER TO clincapture;

SET default_tablespace = '';

SET default_with_oids = true;

--
-- TOC entry 147 (class 1259 OID 143841410)
-- Name: archived_dataset_file; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE archived_dataset_file (
    archived_dataset_file_id integer NOT NULL,
    name character varying(255),
    dataset_id integer,
    export_format_id integer,
    file_reference character varying(1000),
    run_time integer,
    file_size integer,
    date_created timestamp with time zone,
    owner_id integer
);


ALTER TABLE public.archived_dataset_file OWNER TO clincapture;

--
-- TOC entry 146 (class 1259 OID 143841408)
-- Name: archived_dataset_file_archived_dataset_file_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE archived_dataset_file_archived_dataset_file_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.archived_dataset_file_archived_dataset_file_id_seq OWNER TO clincapture;

--
-- TOC entry 3268 (class 0 OID 0)
-- Dependencies: 146
-- Name: archived_dataset_file_archived_dataset_file_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE archived_dataset_file_archived_dataset_file_id_seq OWNED BY archived_dataset_file.archived_dataset_file_id;


--
-- TOC entry 3269 (class 0 OID 0)
-- Dependencies: 146
-- Name: archived_dataset_file_archived_dataset_file_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('archived_dataset_file_archived_dataset_file_id_seq', 1, false);


--
-- TOC entry 149 (class 1259 OID 143841421)
-- Name: audit_event; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE audit_event (
    audit_id integer NOT NULL,
    audit_date timestamp without time zone NOT NULL,
    audit_table character varying(500) NOT NULL,
    user_id integer,
    entity_id integer,
    reason_for_change character varying(1000),
    action_message character varying(4000)
);


ALTER TABLE public.audit_event OWNER TO clincapture;

--
-- TOC entry 148 (class 1259 OID 143841419)
-- Name: audit_event_audit_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE audit_event_audit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.audit_event_audit_id_seq OWNER TO clincapture;

--
-- TOC entry 3270 (class 0 OID 0)
-- Dependencies: 148
-- Name: audit_event_audit_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE audit_event_audit_id_seq OWNED BY audit_event.audit_id;


--
-- TOC entry 3271 (class 0 OID 0)
-- Dependencies: 148
-- Name: audit_event_audit_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('audit_event_audit_id_seq', 1, false);


--
-- TOC entry 150 (class 1259 OID 143841430)
-- Name: audit_event_context; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE audit_event_context (
    audit_id integer,
    study_id integer,
    subject_id integer,
    study_subject_id integer,
    role_name character varying(200),
    event_crf_id integer,
    study_event_id integer,
    study_event_definition_id integer,
    crf_id integer,
    crf_version_id integer,
    study_crf_id integer,
    item_id integer
);


ALTER TABLE public.audit_event_context OWNER TO clincapture;

--
-- TOC entry 151 (class 1259 OID 143841433)
-- Name: audit_event_values; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE audit_event_values (
    audit_id integer,
    column_name character varying(255),
    old_value character varying(2000),
    new_value character varying(2000)
);


ALTER TABLE public.audit_event_values OWNER TO clincapture;

--
-- TOC entry 153 (class 1259 OID 143841441)
-- Name: audit_log_event; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE audit_log_event (
    audit_id integer NOT NULL,
    audit_date timestamp without time zone NOT NULL,
    audit_table character varying(500) NOT NULL,
    user_id integer,
    entity_id integer,
    entity_name character varying(500),
    reason_for_change character varying(1000),
    audit_log_event_type_id integer,
    old_value character varying(2000),
    new_value character varying(2000),
    event_crf_id integer,
    study_event_id integer,
    event_crf_version_id integer,
    ordinal integer,
    name character varying(255),
    version character varying(255),
    date_interviewed timestamp with time zone,
    interviewer_name character varying(255),
    event_definition_crf_id integer,
    location character varying(2000),
    date_start timestamp with time zone,
    study_event_definition_id integer,
    study_subject_id integer
);


ALTER TABLE public.audit_log_event OWNER TO clincapture;

--
-- TOC entry 152 (class 1259 OID 143841439)
-- Name: audit_log_event_audit_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE audit_log_event_audit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.audit_log_event_audit_id_seq OWNER TO clincapture;

--
-- TOC entry 3272 (class 0 OID 0)
-- Dependencies: 152
-- Name: audit_log_event_audit_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE audit_log_event_audit_id_seq OWNED BY audit_log_event.audit_id;


--
-- TOC entry 3273 (class 0 OID 0)
-- Dependencies: 152
-- Name: audit_log_event_audit_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('audit_log_event_audit_id_seq', 1, false);


--
-- TOC entry 155 (class 1259 OID 143841452)
-- Name: audit_log_event_type; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE audit_log_event_type (
    audit_log_event_type_id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE public.audit_log_event_type OWNER TO clincapture;

--
-- TOC entry 154 (class 1259 OID 143841450)
-- Name: audit_log_event_type_audit_log_event_type_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE audit_log_event_type_audit_log_event_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.audit_log_event_type_audit_log_event_type_id_seq OWNER TO clincapture;

--
-- TOC entry 3274 (class 0 OID 0)
-- Dependencies: 154
-- Name: audit_log_event_type_audit_log_event_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE audit_log_event_type_audit_log_event_type_id_seq OWNED BY audit_log_event_type.audit_log_event_type_id;


--
-- TOC entry 3275 (class 0 OID 0)
-- Dependencies: 154
-- Name: audit_log_event_type_audit_log_event_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('audit_log_event_type_audit_log_event_type_id_seq', 1, false);


--
-- TOC entry 336 (class 1259 OID 143845179)
-- Name: audit_log_randomization; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE audit_log_randomization (
    id integer NOT NULL,
    version integer,
    study_id integer,
    site_name text,
    study_subject_id integer,
    event_crf_id integer,
    user_id integer,
    audit_date timestamp with time zone,
    authentication_url text,
    randomization_url text,
    trial_id text,
    strata_variables text,
    response text,
    user_name text,
    success integer
);


ALTER TABLE public.audit_log_randomization OWNER TO clincapture;

--
-- TOC entry 335 (class 1259 OID 143845177)
-- Name: audit_log_randomization_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE audit_log_randomization_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.audit_log_randomization_id_seq OWNER TO clincapture;

--
-- TOC entry 3276 (class 0 OID 0)
-- Dependencies: 335
-- Name: audit_log_randomization_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE audit_log_randomization_id_seq OWNED BY audit_log_randomization.id;


--
-- TOC entry 3277 (class 0 OID 0)
-- Dependencies: 335
-- Name: audit_log_randomization_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('audit_log_randomization_id_seq', 1, false);


--
-- TOC entry 272 (class 1259 OID 143843036)
-- Name: audit_user_login; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE audit_user_login (
    id integer NOT NULL,
    user_name character varying(255),
    user_account_id integer,
    login_attempt_date timestamp without time zone,
    login_status_code integer,
    version integer
);


ALTER TABLE public.audit_user_login OWNER TO clincapture;

--
-- TOC entry 271 (class 1259 OID 143843034)
-- Name: audit_user_login_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE audit_user_login_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.audit_user_login_id_seq OWNER TO clincapture;

--
-- TOC entry 3278 (class 0 OID 0)
-- Dependencies: 271
-- Name: audit_user_login_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE audit_user_login_id_seq OWNED BY audit_user_login.id;


--
-- TOC entry 3279 (class 0 OID 0)
-- Dependencies: 271
-- Name: audit_user_login_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('audit_user_login_id_seq', 1, false);


--
-- TOC entry 258 (class 1259 OID 143842875)
-- Name: authorities; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE authorities (
    id integer NOT NULL,
    username character varying(64) NOT NULL,
    authority character varying(50) NOT NULL,
    version integer
);


ALTER TABLE public.authorities OWNER TO clincapture;

--
-- TOC entry 257 (class 1259 OID 143842873)
-- Name: authorities_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE authorities_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.authorities_id_seq OWNER TO clincapture;

--
-- TOC entry 3280 (class 0 OID 0)
-- Dependencies: 257
-- Name: authorities_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE authorities_id_seq OWNED BY authorities.id;


--
-- TOC entry 3281 (class 0 OID 0)
-- Dependencies: 257
-- Name: authorities_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('authorities_id_seq', 1, true);


--
-- TOC entry 314 (class 1259 OID 143843971)
-- Name: coded_item; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE coded_item (
    id integer NOT NULL,
    item_id integer,
    version integer,
    status character varying(255),
    dictionary character varying(255),
    event_crf_id integer,
    crf_version_id integer,
    subject_id integer,
    site_id integer,
    study_id integer,
    auto_coded boolean,
    preferred_term character varying(255),
    http_path character varying(255)
);


ALTER TABLE public.coded_item OWNER TO clincapture;

--
-- TOC entry 316 (class 1259 OID 143844003)
-- Name: coded_item_element; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE coded_item_element (
    id integer NOT NULL,
    coded_item_id integer,
    item_data_id integer,
    item_name character varying(255),
    item_code character varying(255),
    version integer
);


ALTER TABLE public.coded_item_element OWNER TO clincapture;

--
-- TOC entry 315 (class 1259 OID 143844001)
-- Name: coded_item_element_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE coded_item_element_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.coded_item_element_id_seq OWNER TO clincapture;

--
-- TOC entry 3282 (class 0 OID 0)
-- Dependencies: 315
-- Name: coded_item_element_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE coded_item_element_id_seq OWNED BY coded_item_element.id;


--
-- TOC entry 3283 (class 0 OID 0)
-- Dependencies: 315
-- Name: coded_item_element_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('coded_item_element_id_seq', 1, false);


--
-- TOC entry 313 (class 1259 OID 143843969)
-- Name: coded_item_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE coded_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.coded_item_id_seq OWNER TO clincapture;

--
-- TOC entry 3284 (class 0 OID 0)
-- Dependencies: 313
-- Name: coded_item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE coded_item_id_seq OWNED BY coded_item.id;


--
-- TOC entry 3285 (class 0 OID 0)
-- Dependencies: 313
-- Name: coded_item_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('coded_item_id_seq', 1, false);


--
-- TOC entry 157 (class 1259 OID 143841460)
-- Name: completion_status; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE completion_status (
    completion_status_id integer NOT NULL,
    status_id integer,
    name character varying(255),
    description character varying(1000)
);


ALTER TABLE public.completion_status OWNER TO clincapture;

--
-- TOC entry 156 (class 1259 OID 143841458)
-- Name: completion_status_completion_status_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE completion_status_completion_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.completion_status_completion_status_id_seq OWNER TO clincapture;

--
-- TOC entry 3286 (class 0 OID 0)
-- Dependencies: 156
-- Name: completion_status_completion_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE completion_status_completion_status_id_seq OWNED BY completion_status.completion_status_id;


--
-- TOC entry 3287 (class 0 OID 0)
-- Dependencies: 156
-- Name: completion_status_completion_status_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('completion_status_completion_status_id_seq', 1, false);


--
-- TOC entry 274 (class 1259 OID 143843076)
-- Name: configuration; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE configuration (
    id integer NOT NULL,
    key character varying(255),
    value character varying(255),
    description character varying(512),
    version integer
);


ALTER TABLE public.configuration OWNER TO clincapture;

--
-- TOC entry 273 (class 1259 OID 143843074)
-- Name: configuration_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE configuration_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.configuration_id_seq OWNER TO clincapture;

--
-- TOC entry 3288 (class 0 OID 0)
-- Dependencies: 273
-- Name: configuration_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE configuration_id_seq OWNED BY configuration.id;


--
-- TOC entry 3289 (class 0 OID 0)
-- Dependencies: 273
-- Name: configuration_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('configuration_id_seq', 10, true);


--
-- TOC entry 159 (class 1259 OID 143841471)
-- Name: crf; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE crf (
    crf_id integer NOT NULL,
    status_id integer,
    name character varying(255),
    description character varying(2048),
    owner_id integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    update_id integer,
    oc_oid character varying(40) NOT NULL,
    source_study_id integer,
    auto_layout boolean DEFAULT false,
    source character varying(50)
);


ALTER TABLE public.crf OWNER TO clincapture;

--
-- TOC entry 158 (class 1259 OID 143841469)
-- Name: crf_crf_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE crf_crf_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.crf_crf_id_seq OWNER TO clincapture;

--
-- TOC entry 3290 (class 0 OID 0)
-- Dependencies: 158
-- Name: crf_crf_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE crf_crf_id_seq OWNED BY crf.crf_id;


--
-- TOC entry 3291 (class 0 OID 0)
-- Dependencies: 158
-- Name: crf_crf_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('crf_crf_id_seq', 1, false);


--
-- TOC entry 295 (class 1259 OID 143843568)
-- Name: crf_oid_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE crf_oid_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.crf_oid_id_seq OWNER TO clincapture;

--
-- TOC entry 3292 (class 0 OID 0)
-- Dependencies: 295
-- Name: crf_oid_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('crf_oid_id_seq', 1, false);


--
-- TOC entry 161 (class 1259 OID 143841482)
-- Name: crf_version; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE crf_version (
    crf_version_id integer NOT NULL,
    crf_id integer NOT NULL,
    name character varying(255),
    description character varying(4000),
    revision_notes character varying(255),
    status_id integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    owner_id integer,
    update_id integer,
    oc_oid character varying(120) NOT NULL
);


ALTER TABLE public.crf_version OWNER TO clincapture;

--
-- TOC entry 160 (class 1259 OID 143841480)
-- Name: crf_version_crf_version_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE crf_version_crf_version_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.crf_version_crf_version_id_seq OWNER TO clincapture;

--
-- TOC entry 3293 (class 0 OID 0)
-- Dependencies: 160
-- Name: crf_version_crf_version_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE crf_version_crf_version_id_seq OWNED BY crf_version.crf_version_id;


--
-- TOC entry 3294 (class 0 OID 0)
-- Dependencies: 160
-- Name: crf_version_crf_version_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('crf_version_crf_version_id_seq', 1, false);


--
-- TOC entry 296 (class 1259 OID 143843570)
-- Name: crf_version_oid_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE crf_version_oid_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.crf_version_oid_id_seq OWNER TO clincapture;

--
-- TOC entry 3295 (class 0 OID 0)
-- Dependencies: 296
-- Name: crf_version_oid_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('crf_version_oid_id_seq', 1, false);


--
-- TOC entry 327 (class 1259 OID 143844252)
-- Name: crfs_masking; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE crfs_masking (
    id integer NOT NULL,
    version integer,
    study_id integer,
    study_event_definition_id integer,
    event_definition_crf_id integer,
    user_id integer,
    study_user_role_id integer,
    status_id integer DEFAULT 1
);


ALTER TABLE public.crfs_masking OWNER TO clincapture;

--
-- TOC entry 326 (class 1259 OID 143844250)
-- Name: crfs_masking_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE crfs_masking_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.crfs_masking_id_seq OWNER TO clincapture;

--
-- TOC entry 3296 (class 0 OID 0)
-- Dependencies: 326
-- Name: crfs_masking_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE crfs_masking_id_seq OWNED BY crfs_masking.id;


--
-- TOC entry 3297 (class 0 OID 0)
-- Dependencies: 326
-- Name: crfs_masking_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('crfs_masking_id_seq', 1, false);


SET default_with_oids = false;

--
-- TOC entry 141 (class 1259 OID 143841360)
-- Name: databasechangelog; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE databasechangelog (
    id character varying(255) NOT NULL,
    author character varying(255) NOT NULL,
    filename character varying(255) NOT NULL,
    dateexecuted timestamp without time zone NOT NULL,
    orderexecuted integer NOT NULL,
    exectype character varying(10) NOT NULL,
    md5sum character varying(35),
    description character varying(255),
    comments character varying(255),
    tag character varying(255),
    liquibase character varying(20),
    contexts character varying(255),
    labels character varying(255)
);


ALTER TABLE public.databasechangelog OWNER TO clincapture;

--
-- TOC entry 140 (class 1259 OID 143841355)
-- Name: databasechangeloglock; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE databasechangeloglock (
    id integer NOT NULL,
    locked boolean NOT NULL,
    lockgranted timestamp without time zone,
    lockedby character varying(255)
);


ALTER TABLE public.databasechangeloglock OWNER TO clincapture;

SET default_with_oids = true;

--
-- TOC entry 163 (class 1259 OID 143841493)
-- Name: dataset; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE dataset (
    dataset_id integer NOT NULL,
    study_id integer,
    status_id integer,
    name character varying(255),
    description character varying(2000),
    sql_statement text,
    num_runs integer,
    date_start timestamp with time zone,
    date_end timestamp with time zone,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    date_last_run timestamp with time zone,
    owner_id integer,
    approver_id integer,
    update_id integer,
    show_event_location boolean DEFAULT false,
    show_event_start boolean DEFAULT false,
    show_event_end boolean DEFAULT false,
    show_subject_dob boolean DEFAULT false,
    show_subject_gender boolean DEFAULT false,
    show_event_status boolean DEFAULT false,
    show_subject_status boolean DEFAULT false,
    show_subject_unique_id boolean DEFAULT false,
    show_subject_age_at_event boolean DEFAULT false,
    show_crf_status boolean DEFAULT false,
    show_crf_version boolean DEFAULT false,
    show_crf_int_name boolean DEFAULT false,
    show_crf_int_date boolean DEFAULT false,
    show_group_info boolean DEFAULT false,
    show_disc_info boolean DEFAULT false,
    odm_metadataversion_name character varying(255),
    odm_metadataversion_oid character varying(255),
    odm_prior_study_oid character varying(255),
    odm_prior_metadataversion_oid character varying(255),
    show_secondary_id boolean DEFAULT false,
    dataset_item_status_id integer,
    exclude_items text,
    sed_id_and_crf_id_pairs text
);


ALTER TABLE public.dataset OWNER TO clincapture;

--
-- TOC entry 164 (class 1259 OID 143841518)
-- Name: dataset_crf_version_map; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE dataset_crf_version_map (
    dataset_id integer,
    event_definition_crf_id integer
);


ALTER TABLE public.dataset_crf_version_map OWNER TO clincapture;

--
-- TOC entry 162 (class 1259 OID 143841491)
-- Name: dataset_dataset_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE dataset_dataset_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataset_dataset_id_seq OWNER TO clincapture;

--
-- TOC entry 3298 (class 0 OID 0)
-- Dependencies: 162
-- Name: dataset_dataset_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE dataset_dataset_id_seq OWNED BY dataset.dataset_id;


--
-- TOC entry 3299 (class 0 OID 0)
-- Dependencies: 162
-- Name: dataset_dataset_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('dataset_dataset_id_seq', 1, false);


--
-- TOC entry 270 (class 1259 OID 143843015)
-- Name: dataset_item_status; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE dataset_item_status (
    dataset_item_status_id integer NOT NULL,
    name character varying(50),
    description character varying(255)
);


ALTER TABLE public.dataset_item_status OWNER TO clincapture;

--
-- TOC entry 269 (class 1259 OID 143843013)
-- Name: dataset_item_status_dataset_item_status_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE dataset_item_status_dataset_item_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataset_item_status_dataset_item_status_id_seq OWNER TO clincapture;

--
-- TOC entry 3300 (class 0 OID 0)
-- Dependencies: 269
-- Name: dataset_item_status_dataset_item_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE dataset_item_status_dataset_item_status_id_seq OWNED BY dataset_item_status.dataset_item_status_id;


--
-- TOC entry 3301 (class 0 OID 0)
-- Dependencies: 269
-- Name: dataset_item_status_dataset_item_status_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('dataset_item_status_dataset_item_status_id_seq', 1, false);


--
-- TOC entry 165 (class 1259 OID 143841524)
-- Name: dataset_study_group_class_map; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE dataset_study_group_class_map (
    dataset_id integer NOT NULL,
    study_group_class_id integer NOT NULL
);


ALTER TABLE public.dataset_study_group_class_map OWNER TO clincapture;

--
-- TOC entry 310 (class 1259 OID 143843938)
-- Name: dictionary; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE dictionary (
    id integer NOT NULL,
    type integer,
    version integer,
    name character varying(255),
    description character varying(2048),
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    study integer
);


ALTER TABLE public.dictionary OWNER TO clincapture;

--
-- TOC entry 309 (class 1259 OID 143843936)
-- Name: dictionary_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE dictionary_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dictionary_id_seq OWNER TO clincapture;

--
-- TOC entry 3302 (class 0 OID 0)
-- Dependencies: 309
-- Name: dictionary_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE dictionary_id_seq OWNED BY dictionary.id;


--
-- TOC entry 3303 (class 0 OID 0)
-- Dependencies: 309
-- Name: dictionary_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('dictionary_id_seq', 1, false);


--
-- TOC entry 307 (class 1259 OID 143843705)
-- Name: discrepancy_description; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE discrepancy_description (
    id integer NOT NULL,
    name character varying(255),
    description character varying(2048),
    study_id integer,
    visibility_level character varying(255),
    type_id integer,
    version integer
);


ALTER TABLE public.discrepancy_description OWNER TO clincapture;

--
-- TOC entry 306 (class 1259 OID 143843703)
-- Name: discrepancy_description_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE discrepancy_description_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.discrepancy_description_id_seq OWNER TO clincapture;

--
-- TOC entry 3304 (class 0 OID 0)
-- Dependencies: 306
-- Name: discrepancy_description_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE discrepancy_description_id_seq OWNED BY discrepancy_description.id;


--
-- TOC entry 3305 (class 0 OID 0)
-- Dependencies: 306
-- Name: discrepancy_description_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('discrepancy_description_id_seq', 12, true);


--
-- TOC entry 167 (class 1259 OID 143841600)
-- Name: discrepancy_note; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE discrepancy_note (
    discrepancy_note_id integer NOT NULL,
    description character varying(255),
    discrepancy_note_type_id integer,
    resolution_status_id integer,
    detailed_notes character varying(1000),
    date_created timestamp with time zone,
    owner_id integer,
    parent_dn_id integer,
    entity_type character varying(30),
    study_id integer,
    assigned_user_id integer
);


ALTER TABLE public.discrepancy_note OWNER TO clincapture;

--
-- TOC entry 166 (class 1259 OID 143841598)
-- Name: discrepancy_note_discrepancy_note_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE discrepancy_note_discrepancy_note_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.discrepancy_note_discrepancy_note_id_seq OWNER TO clincapture;

--
-- TOC entry 3306 (class 0 OID 0)
-- Dependencies: 166
-- Name: discrepancy_note_discrepancy_note_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE discrepancy_note_discrepancy_note_id_seq OWNED BY discrepancy_note.discrepancy_note_id;


--
-- TOC entry 3307 (class 0 OID 0)
-- Dependencies: 166
-- Name: discrepancy_note_discrepancy_note_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('discrepancy_note_discrepancy_note_id_seq', 1, false);


--
-- TOC entry 169 (class 1259 OID 143841611)
-- Name: discrepancy_note_type; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE discrepancy_note_type (
    discrepancy_note_type_id integer NOT NULL,
    name character varying(50),
    description character varying(255)
);


ALTER TABLE public.discrepancy_note_type OWNER TO clincapture;

--
-- TOC entry 168 (class 1259 OID 143841609)
-- Name: discrepancy_note_type_discrepancy_note_type_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE discrepancy_note_type_discrepancy_note_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.discrepancy_note_type_discrepancy_note_type_id_seq OWNER TO clincapture;

--
-- TOC entry 3308 (class 0 OID 0)
-- Dependencies: 168
-- Name: discrepancy_note_type_discrepancy_note_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE discrepancy_note_type_discrepancy_note_type_id_seq OWNED BY discrepancy_note_type.discrepancy_note_type_id;


--
-- TOC entry 3309 (class 0 OID 0)
-- Dependencies: 168
-- Name: discrepancy_note_type_discrepancy_note_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('discrepancy_note_type_discrepancy_note_type_id_seq', 1, false);


--
-- TOC entry 328 (class 1259 OID 143844793)
-- Name: dn_age_days; Type: VIEW; Schema: public; Owner: clincapture
--

CREATE VIEW dn_age_days AS
    SELECT dn.discrepancy_note_id, CASE WHEN (dn.resolution_status_id = ANY (ARRAY[1, 2, 3])) THEN date_part('day'::text, (now() - (SELECT cdn.date_created FROM discrepancy_note cdn WHERE (cdn.discrepancy_note_id = (SELECT max(idn.discrepancy_note_id) AS max FROM discrepancy_note idn WHERE (idn.parent_dn_id = dn.discrepancy_note_id)))))) ELSE (NULL::integer)::double precision END AS days, CASE WHEN (dn.resolution_status_id = 4) THEN date_part('day'::text, ((SELECT cdn.date_created FROM discrepancy_note cdn WHERE (cdn.discrepancy_note_id = (SELECT max(idn.discrepancy_note_id) AS max FROM discrepancy_note idn WHERE (idn.parent_dn_id = dn.discrepancy_note_id)))) - dn.date_created)) WHEN (dn.resolution_status_id = ANY (ARRAY[1, 2, 3])) THEN date_part('day'::text, (now() - dn.date_created)) ELSE (NULL::integer)::double precision END AS age FROM discrepancy_note dn WHERE (dn.parent_dn_id IS NULL);


ALTER TABLE public.dn_age_days OWNER TO clincapture;

--
-- TOC entry 170 (class 1259 OID 143841617)
-- Name: dn_event_crf_map; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE dn_event_crf_map (
    event_crf_id integer,
    discrepancy_note_id integer,
    column_name character varying(255)
);


ALTER TABLE public.dn_event_crf_map OWNER TO clincapture;

--
-- TOC entry 171 (class 1259 OID 143841620)
-- Name: dn_item_data_map; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE dn_item_data_map (
    item_data_id integer,
    discrepancy_note_id integer,
    column_name character varying(255),
    study_subject_id integer
);


ALTER TABLE public.dn_item_data_map OWNER TO clincapture;

--
-- TOC entry 172 (class 1259 OID 143841623)
-- Name: dn_study_event_map; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE dn_study_event_map (
    study_event_id integer,
    discrepancy_note_id integer,
    column_name character varying(255)
);


ALTER TABLE public.dn_study_event_map OWNER TO clincapture;

--
-- TOC entry 173 (class 1259 OID 143841626)
-- Name: dn_study_subject_map; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE dn_study_subject_map (
    study_subject_id integer,
    discrepancy_note_id integer,
    column_name character varying(255)
);


ALTER TABLE public.dn_study_subject_map OWNER TO clincapture;

--
-- TOC entry 174 (class 1259 OID 143841629)
-- Name: dn_subject_map; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE dn_subject_map (
    subject_id integer,
    discrepancy_note_id integer,
    column_name character varying(255)
);


ALTER TABLE public.dn_subject_map OWNER TO clincapture;

--
-- TOC entry 288 (class 1259 OID 143843380)
-- Name: dyn_item_form_metadata; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE dyn_item_form_metadata (
    id integer NOT NULL,
    item_form_metadata_id integer,
    item_id integer,
    crf_version_id integer,
    show_item boolean DEFAULT true,
    event_crf_id integer,
    version integer,
    item_data_id integer,
    passed_dde integer DEFAULT 0
);


ALTER TABLE public.dyn_item_form_metadata OWNER TO clincapture;

--
-- TOC entry 287 (class 1259 OID 143843378)
-- Name: dyn_item_form_metadata_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE dyn_item_form_metadata_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dyn_item_form_metadata_id_seq OWNER TO clincapture;

--
-- TOC entry 3310 (class 0 OID 0)
-- Dependencies: 287
-- Name: dyn_item_form_metadata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE dyn_item_form_metadata_id_seq OWNED BY dyn_item_form_metadata.id;


--
-- TOC entry 3311 (class 0 OID 0)
-- Dependencies: 287
-- Name: dyn_item_form_metadata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('dyn_item_form_metadata_id_seq', 1, false);


--
-- TOC entry 290 (class 1259 OID 143843389)
-- Name: dyn_item_group_metadata; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE dyn_item_group_metadata (
    id integer NOT NULL,
    item_group_metadata_id integer,
    item_group_id integer,
    show_group boolean DEFAULT true,
    event_crf_id integer,
    version integer,
    passed_dde integer DEFAULT 0
);


ALTER TABLE public.dyn_item_group_metadata OWNER TO clincapture;

--
-- TOC entry 289 (class 1259 OID 143843387)
-- Name: dyn_item_group_metadata_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE dyn_item_group_metadata_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dyn_item_group_metadata_id_seq OWNER TO clincapture;

--
-- TOC entry 3312 (class 0 OID 0)
-- Dependencies: 289
-- Name: dyn_item_group_metadata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE dyn_item_group_metadata_id_seq OWNED BY dyn_item_group_metadata.id;


--
-- TOC entry 3313 (class 0 OID 0)
-- Dependencies: 289
-- Name: dyn_item_group_metadata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('dyn_item_group_metadata_id_seq', 1, false);


--
-- TOC entry 305 (class 1259 OID 143843674)
-- Name: dynamic_event; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE dynamic_event (
    dynamic_event_id integer NOT NULL,
    study_group_class_id integer,
    study_event_definition_id integer,
    study_id integer,
    ordinal integer,
    owner_id integer,
    update_id integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    name character varying(255),
    description character varying(2048)
);


ALTER TABLE public.dynamic_event OWNER TO clincapture;

--
-- TOC entry 304 (class 1259 OID 143843672)
-- Name: dynamic_event_dynamic_event_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE dynamic_event_dynamic_event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dynamic_event_dynamic_event_id_seq OWNER TO clincapture;

--
-- TOC entry 3314 (class 0 OID 0)
-- Dependencies: 304
-- Name: dynamic_event_dynamic_event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE dynamic_event_dynamic_event_id_seq OWNED BY dynamic_event.dynamic_event_id;


--
-- TOC entry 3315 (class 0 OID 0)
-- Dependencies: 304
-- Name: dynamic_event_dynamic_event_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('dynamic_event_dynamic_event_id_seq', 1, false);


--
-- TOC entry 334 (class 1259 OID 143845149)
-- Name: edc_item_metadata; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE edc_item_metadata (
    id integer NOT NULL,
    study_event_definition_id integer,
    event_definition_crf_id integer,
    crf_version_id integer,
    item_id integer,
    version integer,
    sdv_required character(1) DEFAULT '0'::bpchar
);


ALTER TABLE public.edc_item_metadata OWNER TO clincapture;

--
-- TOC entry 333 (class 1259 OID 143845147)
-- Name: edc_item_metadata_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE edc_item_metadata_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.edc_item_metadata_id_seq OWNER TO clincapture;

--
-- TOC entry 3316 (class 0 OID 0)
-- Dependencies: 333
-- Name: edc_item_metadata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE edc_item_metadata_id_seq OWNED BY edc_item_metadata.id;


--
-- TOC entry 3317 (class 0 OID 0)
-- Dependencies: 333
-- Name: edc_item_metadata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('edc_item_metadata_id_seq', 1, false);


--
-- TOC entry 176 (class 1259 OID 143841634)
-- Name: event_crf; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE event_crf (
    event_crf_id integer NOT NULL,
    study_event_id integer,
    crf_version_id integer,
    date_interviewed timestamp with time zone,
    interviewer_name character varying(255),
    completion_status_id integer,
    status_id integer,
    annotations character varying(4000),
    date_completed timestamp without time zone,
    validator_id integer,
    date_validate timestamp with time zone,
    date_validate_completed timestamp without time zone,
    validator_annotations character varying(4000),
    validate_string character varying(256),
    owner_id integer,
    date_created timestamp with time zone,
    study_subject_id integer,
    date_updated timestamp with time zone,
    update_id integer,
    electronic_signature_status boolean DEFAULT false,
    sdv_status boolean DEFAULT false NOT NULL,
    old_status_id integer DEFAULT 1,
    sdv_update_id integer DEFAULT 0,
    not_started boolean DEFAULT false NOT NULL,
    states character varying(11) DEFAULT '0,0,0,0'::character varying NOT NULL
);


ALTER TABLE public.event_crf OWNER TO clincapture;

--
-- TOC entry 175 (class 1259 OID 143841632)
-- Name: event_crf_event_crf_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE event_crf_event_crf_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.event_crf_event_crf_id_seq OWNER TO clincapture;

--
-- TOC entry 3318 (class 0 OID 0)
-- Dependencies: 175
-- Name: event_crf_event_crf_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE event_crf_event_crf_id_seq OWNED BY event_crf.event_crf_id;


--
-- TOC entry 3319 (class 0 OID 0)
-- Dependencies: 175
-- Name: event_crf_event_crf_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('event_crf_event_crf_id_seq', 1, false);


--
-- TOC entry 330 (class 1259 OID 143844997)
-- Name: event_crf_section; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE event_crf_section (
    id integer NOT NULL,
    version integer,
    section_id integer,
    event_crf_id integer,
    partial_saved boolean
);


ALTER TABLE public.event_crf_section OWNER TO clincapture;

--
-- TOC entry 329 (class 1259 OID 143844995)
-- Name: event_crf_section_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE event_crf_section_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.event_crf_section_id_seq OWNER TO clincapture;

--
-- TOC entry 3320 (class 0 OID 0)
-- Dependencies: 329
-- Name: event_crf_section_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE event_crf_section_id_seq OWNED BY event_crf_section.id;


--
-- TOC entry 3321 (class 0 OID 0)
-- Dependencies: 329
-- Name: event_crf_section_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('event_crf_section_id_seq', 1, false);


--
-- TOC entry 178 (class 1259 OID 143841646)
-- Name: event_definition_crf; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE event_definition_crf (
    event_definition_crf_id integer NOT NULL,
    study_event_definition_id integer,
    study_id integer,
    crf_id integer,
    required_crf boolean,
    double_entry boolean,
    require_all_text_filled boolean,
    decision_conditions boolean,
    null_values character varying(255),
    default_version_id integer,
    status_id integer,
    owner_id integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    update_id integer,
    ordinal integer,
    electronic_signature boolean DEFAULT false,
    hide_crf boolean DEFAULT false,
    source_data_verification_code integer,
    selected_version_ids character varying(150),
    parent_id integer,
    email_step character varying(50),
    email_to character varying(150),
    evaluated_crf boolean DEFAULT false,
    tabbing_mode character varying(255) DEFAULT 'leftToRight'::character varying,
    accept_new_crf_versions boolean DEFAULT false
);


ALTER TABLE public.event_definition_crf OWNER TO clincapture;

--
-- TOC entry 177 (class 1259 OID 143841644)
-- Name: event_definition_crf_event_definition_crf_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE event_definition_crf_event_definition_crf_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.event_definition_crf_event_definition_crf_id_seq OWNER TO clincapture;

--
-- TOC entry 3322 (class 0 OID 0)
-- Dependencies: 177
-- Name: event_definition_crf_event_definition_crf_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE event_definition_crf_event_definition_crf_id_seq OWNED BY event_definition_crf.event_definition_crf_id;


--
-- TOC entry 3323 (class 0 OID 0)
-- Dependencies: 177
-- Name: event_definition_crf_event_definition_crf_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('event_definition_crf_event_definition_crf_id_seq', 1, false);


--
-- TOC entry 180 (class 1259 OID 143841655)
-- Name: export_format; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE export_format (
    export_format_id integer NOT NULL,
    name character varying(255),
    description character varying(1000),
    mime_type character varying(255)
);


ALTER TABLE public.export_format OWNER TO clincapture;

--
-- TOC entry 179 (class 1259 OID 143841653)
-- Name: export_format_export_format_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE export_format_export_format_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.export_format_export_format_id_seq OWNER TO clincapture;

--
-- TOC entry 3324 (class 0 OID 0)
-- Dependencies: 179
-- Name: export_format_export_format_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE export_format_export_format_id_seq OWNED BY export_format.export_format_id;


--
-- TOC entry 3325 (class 0 OID 0)
-- Dependencies: 179
-- Name: export_format_export_format_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('export_format_export_format_id_seq', 1, false);


--
-- TOC entry 297 (class 1259 OID 143843572)
-- Name: generic_oid_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE generic_oid_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.generic_oid_id_seq OWNER TO clincapture;

--
-- TOC entry 3326 (class 0 OID 0)
-- Dependencies: 297
-- Name: generic_oid_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('generic_oid_id_seq', 1, false);


--
-- TOC entry 182 (class 1259 OID 143841680)
-- Name: group_class_types; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE group_class_types (
    group_class_type_id integer NOT NULL,
    name character varying(255),
    description character varying(1000)
);


ALTER TABLE public.group_class_types OWNER TO clincapture;

--
-- TOC entry 181 (class 1259 OID 143841678)
-- Name: group_class_types_group_class_type_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE group_class_types_group_class_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.group_class_types_group_class_type_id_seq OWNER TO clincapture;

--
-- TOC entry 3327 (class 0 OID 0)
-- Dependencies: 181
-- Name: group_class_types_group_class_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE group_class_types_group_class_type_id_seq OWNED BY group_class_types.group_class_type_id;


--
-- TOC entry 3328 (class 0 OID 0)
-- Dependencies: 181
-- Name: group_class_types_group_class_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('group_class_types_group_class_type_id_seq', 1, false);


--
-- TOC entry 184 (class 1259 OID 143841691)
-- Name: item; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE item (
    item_id integer NOT NULL,
    name character varying(255),
    description character varying(4000),
    units character varying(64),
    phi_status boolean,
    item_data_type_id integer,
    item_reference_type_id integer,
    status_id integer,
    owner_id integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    update_id integer,
    oc_oid character varying(40) NOT NULL,
    sas_name character varying(12)
);


ALTER TABLE public.item OWNER TO clincapture;

--
-- TOC entry 186 (class 1259 OID 143841702)
-- Name: item_data; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE item_data (
    item_data_id integer NOT NULL,
    item_id integer NOT NULL,
    event_crf_id integer,
    status_id integer,
    value character varying(4000),
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    owner_id integer,
    update_id integer,
    ordinal integer,
    old_status_id integer,
    sdv boolean DEFAULT false,
    partial_dde_value character varying(4000)
);


ALTER TABLE public.item_data OWNER TO clincapture;

--
-- TOC entry 185 (class 1259 OID 143841700)
-- Name: item_data_item_data_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE item_data_item_data_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.item_data_item_data_id_seq OWNER TO clincapture;

--
-- TOC entry 3329 (class 0 OID 0)
-- Dependencies: 185
-- Name: item_data_item_data_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE item_data_item_data_id_seq OWNED BY item_data.item_data_id;


--
-- TOC entry 3330 (class 0 OID 0)
-- Dependencies: 185
-- Name: item_data_item_data_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('item_data_item_data_id_seq', 1, false);


--
-- TOC entry 188 (class 1259 OID 143841713)
-- Name: item_data_type; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE item_data_type (
    item_data_type_id integer NOT NULL,
    code character varying(20),
    name character varying(255),
    definition character varying(1000),
    reference character varying(1000)
);


ALTER TABLE public.item_data_type OWNER TO clincapture;

--
-- TOC entry 187 (class 1259 OID 143841711)
-- Name: item_data_type_item_data_type_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE item_data_type_item_data_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.item_data_type_item_data_type_id_seq OWNER TO clincapture;

--
-- TOC entry 3331 (class 0 OID 0)
-- Dependencies: 187
-- Name: item_data_type_item_data_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE item_data_type_item_data_type_id_seq OWNED BY item_data_type.item_data_type_id;


--
-- TOC entry 3332 (class 0 OID 0)
-- Dependencies: 187
-- Name: item_data_type_item_data_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('item_data_type_item_data_type_id_seq', 1, false);


--
-- TOC entry 190 (class 1259 OID 143841724)
-- Name: item_form_metadata; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE item_form_metadata (
    item_form_metadata_id integer NOT NULL,
    item_id integer NOT NULL,
    crf_version_id integer,
    header character varying(2000),
    subheader character varying(240),
    parent_id integer,
    parent_label character varying(120),
    column_number integer,
    page_number_label character varying(5),
    question_number_label character varying(20),
    left_item_text character varying(4000),
    right_item_text character varying(2000),
    section_id integer NOT NULL,
    response_set_id integer NOT NULL,
    regexp character varying(1000),
    regexp_error_msg character varying(255),
    ordinal integer NOT NULL,
    required boolean,
    default_value character varying(4000),
    response_layout character varying(255),
    width_decimal character varying(10),
    show_item boolean DEFAULT true,
    code_ref character varying(255),
    pseudo_child integer DEFAULT 0
);


ALTER TABLE public.item_form_metadata OWNER TO clincapture;

--
-- TOC entry 189 (class 1259 OID 143841722)
-- Name: item_form_metadata_item_form_metadata_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE item_form_metadata_item_form_metadata_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.item_form_metadata_item_form_metadata_id_seq OWNER TO clincapture;

--
-- TOC entry 3333 (class 0 OID 0)
-- Dependencies: 189
-- Name: item_form_metadata_item_form_metadata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE item_form_metadata_item_form_metadata_id_seq OWNED BY item_form_metadata.item_form_metadata_id;


--
-- TOC entry 3334 (class 0 OID 0)
-- Dependencies: 189
-- Name: item_form_metadata_item_form_metadata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('item_form_metadata_item_form_metadata_id_seq', 1, false);


--
-- TOC entry 192 (class 1259 OID 143841735)
-- Name: item_group; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE item_group (
    item_group_id integer NOT NULL,
    name character varying(255),
    crf_id integer NOT NULL,
    status_id integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    owner_id integer,
    update_id integer,
    oc_oid character varying(120) NOT NULL
);


ALTER TABLE public.item_group OWNER TO clincapture;

--
-- TOC entry 191 (class 1259 OID 143841733)
-- Name: item_group_item_group_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE item_group_item_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.item_group_item_group_id_seq OWNER TO clincapture;

--
-- TOC entry 3335 (class 0 OID 0)
-- Dependencies: 191
-- Name: item_group_item_group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE item_group_item_group_id_seq OWNED BY item_group.item_group_id;


--
-- TOC entry 3336 (class 0 OID 0)
-- Dependencies: 191
-- Name: item_group_item_group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('item_group_item_group_id_seq', 1, false);


--
-- TOC entry 194 (class 1259 OID 143841743)
-- Name: item_group_metadata; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE item_group_metadata (
    item_group_metadata_id integer NOT NULL,
    item_group_id integer NOT NULL,
    header character varying(255),
    subheader character varying(255),
    layout character varying(100),
    repeat_number integer,
    repeat_max integer,
    repeat_array character varying(255),
    row_start_number integer,
    crf_version_id integer NOT NULL,
    item_id integer NOT NULL,
    ordinal integer NOT NULL,
    borders integer,
    show_group boolean DEFAULT true,
    repeating_group boolean DEFAULT true NOT NULL
);


ALTER TABLE public.item_group_metadata OWNER TO clincapture;

--
-- TOC entry 193 (class 1259 OID 143841741)
-- Name: item_group_metadata_item_group_metadata_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE item_group_metadata_item_group_metadata_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.item_group_metadata_item_group_metadata_id_seq OWNER TO clincapture;

--
-- TOC entry 3337 (class 0 OID 0)
-- Dependencies: 193
-- Name: item_group_metadata_item_group_metadata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE item_group_metadata_item_group_metadata_id_seq OWNED BY item_group_metadata.item_group_metadata_id;


--
-- TOC entry 3338 (class 0 OID 0)
-- Dependencies: 193
-- Name: item_group_metadata_item_group_metadata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('item_group_metadata_item_group_metadata_id_seq', 1, false);


--
-- TOC entry 298 (class 1259 OID 143843574)
-- Name: item_group_oid_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE item_group_oid_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.item_group_oid_id_seq OWNER TO clincapture;

--
-- TOC entry 3339 (class 0 OID 0)
-- Dependencies: 298
-- Name: item_group_oid_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('item_group_oid_id_seq', 1, false);


--
-- TOC entry 183 (class 1259 OID 143841689)
-- Name: item_item_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE item_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.item_item_id_seq OWNER TO clincapture;

--
-- TOC entry 3340 (class 0 OID 0)
-- Dependencies: 183
-- Name: item_item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE item_item_id_seq OWNED BY item.item_id;


--
-- TOC entry 3341 (class 0 OID 0)
-- Dependencies: 183
-- Name: item_item_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('item_item_id_seq', 1, false);


--
-- TOC entry 299 (class 1259 OID 143843576)
-- Name: item_oid_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE item_oid_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.item_oid_id_seq OWNER TO clincapture;

--
-- TOC entry 3342 (class 0 OID 0)
-- Dependencies: 299
-- Name: item_oid_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('item_oid_id_seq', 1, false);


--
-- TOC entry 196 (class 1259 OID 143841754)
-- Name: item_reference_type; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE item_reference_type (
    item_reference_type_id integer NOT NULL,
    name character varying(255),
    description character varying(1000)
);


ALTER TABLE public.item_reference_type OWNER TO clincapture;

--
-- TOC entry 195 (class 1259 OID 143841752)
-- Name: item_reference_type_item_reference_type_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE item_reference_type_item_reference_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.item_reference_type_item_reference_type_id_seq OWNER TO clincapture;

--
-- TOC entry 3343 (class 0 OID 0)
-- Dependencies: 195
-- Name: item_reference_type_item_reference_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE item_reference_type_item_reference_type_id_seq OWNED BY item_reference_type.item_reference_type_id;


--
-- TOC entry 3344 (class 0 OID 0)
-- Dependencies: 195
-- Name: item_reference_type_item_reference_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('item_reference_type_item_reference_type_id_seq', 1, false);


--
-- TOC entry 332 (class 1259 OID 143845042)
-- Name: item_render_metadata; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE item_render_metadata (
    id integer NOT NULL,
    crf_version_id integer,
    item_id integer,
    width integer,
    left_item_text_width integer,
    version integer
);


ALTER TABLE public.item_render_metadata OWNER TO clincapture;

--
-- TOC entry 331 (class 1259 OID 143845040)
-- Name: item_render_metadata_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE item_render_metadata_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.item_render_metadata_id_seq OWNER TO clincapture;

--
-- TOC entry 3345 (class 0 OID 0)
-- Dependencies: 331
-- Name: item_render_metadata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE item_render_metadata_id_seq OWNED BY item_render_metadata.id;


--
-- TOC entry 3346 (class 0 OID 0)
-- Dependencies: 331
-- Name: item_render_metadata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('item_render_metadata_id_seq', 1, false);


--
-- TOC entry 278 (class 1259 OID 143843120)
-- Name: measurement_unit; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE measurement_unit (
    id integer NOT NULL,
    oc_oid character varying(40) NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(255),
    version integer
);


ALTER TABLE public.measurement_unit OWNER TO clincapture;

--
-- TOC entry 277 (class 1259 OID 143843118)
-- Name: measurement_unit_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE measurement_unit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.measurement_unit_id_seq OWNER TO clincapture;

--
-- TOC entry 3347 (class 0 OID 0)
-- Dependencies: 277
-- Name: measurement_unit_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE measurement_unit_id_seq OWNED BY measurement_unit.id;


--
-- TOC entry 3348 (class 0 OID 0)
-- Dependencies: 277
-- Name: measurement_unit_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('measurement_unit_id_seq', 1, false);


--
-- TOC entry 300 (class 1259 OID 143843578)
-- Name: measurement_unit_oid_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE measurement_unit_oid_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.measurement_unit_oid_id_seq OWNER TO clincapture;

--
-- TOC entry 3349 (class 0 OID 0)
-- Dependencies: 300
-- Name: measurement_unit_oid_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('measurement_unit_oid_id_seq', 1, false);


--
-- TOC entry 198 (class 1259 OID 143841765)
-- Name: null_value_type; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE null_value_type (
    null_value_type_id integer NOT NULL,
    code character varying(20),
    name character varying(255),
    definition character varying(1000),
    reference character varying(1000)
);


ALTER TABLE public.null_value_type OWNER TO clincapture;

--
-- TOC entry 197 (class 1259 OID 143841763)
-- Name: null_value_type_null_value_type_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE null_value_type_null_value_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.null_value_type_null_value_type_id_seq OWNER TO clincapture;

--
-- TOC entry 3350 (class 0 OID 0)
-- Dependencies: 197
-- Name: null_value_type_null_value_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE null_value_type_null_value_type_id_seq OWNED BY null_value_type.null_value_type_id;


--
-- TOC entry 3351 (class 0 OID 0)
-- Dependencies: 197
-- Name: null_value_type_null_value_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('null_value_type_null_value_type_id_seq', 1, false);


--
-- TOC entry 259 (class 1259 OID 143842879)
-- Name: oc_qrtz_blob_triggers; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE oc_qrtz_blob_triggers (
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    blob_data bytea,
    sched_name character varying(120) DEFAULT 'schedulerFactoryBean'::character varying NOT NULL
);


ALTER TABLE public.oc_qrtz_blob_triggers OWNER TO clincapture;

--
-- TOC entry 260 (class 1259 OID 143842887)
-- Name: oc_qrtz_calendars; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE oc_qrtz_calendars (
    calendar_name character varying(200) NOT NULL,
    calendar bytea NOT NULL,
    sched_name character varying(120) DEFAULT 'schedulerFactoryBean'::character varying NOT NULL
);


ALTER TABLE public.oc_qrtz_calendars OWNER TO clincapture;

--
-- TOC entry 261 (class 1259 OID 143842895)
-- Name: oc_qrtz_cron_triggers; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE oc_qrtz_cron_triggers (
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    cron_expression character varying(120) NOT NULL,
    time_zone_id character varying(80),
    sched_name character varying(120) DEFAULT 'schedulerFactoryBean'::character varying NOT NULL
);


ALTER TABLE public.oc_qrtz_cron_triggers OWNER TO clincapture;

--
-- TOC entry 262 (class 1259 OID 143842903)
-- Name: oc_qrtz_fired_triggers; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE oc_qrtz_fired_triggers (
    entry_id character varying(95) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    instance_name character varying(200) NOT NULL,
    fired_time bigint NOT NULL,
    priority integer NOT NULL,
    state character varying(16) NOT NULL,
    job_name character varying(200),
    job_group character varying(200),
    requests_recovery boolean,
    is_nonconcurrent boolean,
    is_update_data boolean,
    sched_name character varying(120) DEFAULT 'schedulerFactoryBean'::character varying NOT NULL
);


ALTER TABLE public.oc_qrtz_fired_triggers OWNER TO clincapture;

--
-- TOC entry 263 (class 1259 OID 143842911)
-- Name: oc_qrtz_job_details; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE oc_qrtz_job_details (
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    job_class_name character varying(250) NOT NULL,
    is_durable boolean NOT NULL,
    requests_recovery boolean NOT NULL,
    job_data bytea,
    is_nonconcurrent boolean,
    is_update_data boolean,
    sched_name character varying(120) DEFAULT 'schedulerFactoryBean'::character varying NOT NULL
);


ALTER TABLE public.oc_qrtz_job_details OWNER TO clincapture;

--
-- TOC entry 264 (class 1259 OID 143842927)
-- Name: oc_qrtz_locks; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE oc_qrtz_locks (
    lock_name character varying(40) NOT NULL,
    sched_name character varying(120) DEFAULT 'schedulerFactoryBean'::character varying NOT NULL
);


ALTER TABLE public.oc_qrtz_locks OWNER TO clincapture;

--
-- TOC entry 265 (class 1259 OID 143842932)
-- Name: oc_qrtz_paused_trigger_grps; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE oc_qrtz_paused_trigger_grps (
    trigger_group character varying(200) NOT NULL,
    sched_name character varying(120) DEFAULT 'schedulerFactoryBean'::character varying NOT NULL
);


ALTER TABLE public.oc_qrtz_paused_trigger_grps OWNER TO clincapture;

--
-- TOC entry 266 (class 1259 OID 143842937)
-- Name: oc_qrtz_scheduler_state; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE oc_qrtz_scheduler_state (
    instance_name character varying(200) NOT NULL,
    last_checkin_time bigint NOT NULL,
    checkin_interval bigint NOT NULL,
    sched_name character varying(120) DEFAULT 'schedulerFactoryBean'::character varying NOT NULL
);


ALTER TABLE public.oc_qrtz_scheduler_state OWNER TO clincapture;

--
-- TOC entry 267 (class 1259 OID 143842942)
-- Name: oc_qrtz_simple_triggers; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE oc_qrtz_simple_triggers (
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    repeat_count bigint NOT NULL,
    repeat_interval bigint NOT NULL,
    times_triggered bigint NOT NULL,
    sched_name character varying(120) DEFAULT 'schedulerFactoryBean'::character varying NOT NULL
);


ALTER TABLE public.oc_qrtz_simple_triggers OWNER TO clincapture;

--
-- TOC entry 308 (class 1259 OID 143843838)
-- Name: oc_qrtz_simprop_triggers; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE oc_qrtz_simprop_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    str_prop_1 character varying(512),
    str_prop_2 character varying(512),
    str_prop_3 character varying(512),
    int_prop_1 integer,
    int_prop_2 integer,
    long_prop_1 bigint,
    long_prop_2 bigint,
    dec_prop_1 numeric(13,4),
    dec_prop_2 numeric(13,4),
    bool_prop_1 boolean,
    bool_prop_2 boolean
);


ALTER TABLE public.oc_qrtz_simprop_triggers OWNER TO clincapture;

--
-- TOC entry 268 (class 1259 OID 143842955)
-- Name: oc_qrtz_triggers; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE oc_qrtz_triggers (
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    next_fire_time bigint,
    prev_fire_time bigint,
    priority integer,
    trigger_state character varying(16) NOT NULL,
    trigger_type character varying(8) NOT NULL,
    start_time bigint NOT NULL,
    end_time bigint,
    calendar_name character varying(200),
    misfire_instr smallint,
    job_data bytea,
    sched_name character varying(120) DEFAULT 'schedulerFactoryBean'::character varying NOT NULL
);


ALTER TABLE public.oc_qrtz_triggers OWNER TO clincapture;

--
-- TOC entry 200 (class 1259 OID 143841782)
-- Name: openclinica_version; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE openclinica_version (
    id integer NOT NULL,
    name character varying(255),
    build_number character varying(1000),
    version integer,
    update_timestamp timestamp without time zone
);


ALTER TABLE public.openclinica_version OWNER TO clincapture;

--
-- TOC entry 199 (class 1259 OID 143841780)
-- Name: openclinica_version_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE openclinica_version_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.openclinica_version_id_seq OWNER TO clincapture;

--
-- TOC entry 3352 (class 0 OID 0)
-- Dependencies: 199
-- Name: openclinica_version_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE openclinica_version_id_seq OWNED BY openclinica_version.id;


--
-- TOC entry 3353 (class 0 OID 0)
-- Dependencies: 199
-- Name: openclinica_version_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('openclinica_version_id_seq', 1, true);


--
-- TOC entry 280 (class 1259 OID 143843163)
-- Name: password; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE password (
    passwd_id integer NOT NULL,
    user_name character varying(255),
    user_id integer,
    passwd character varying(255),
    date_first_used timestamp without time zone,
    date_last_used timestamp without time zone
);


ALTER TABLE public.password OWNER TO clincapture;

--
-- TOC entry 279 (class 1259 OID 143843161)
-- Name: password_passwd_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE password_passwd_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.password_passwd_id_seq OWNER TO clincapture;

--
-- TOC entry 3354 (class 0 OID 0)
-- Dependencies: 279
-- Name: password_passwd_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE password_passwd_id_seq OWNED BY password.passwd_id;


--
-- TOC entry 3355 (class 0 OID 0)
-- Dependencies: 279
-- Name: password_passwd_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('password_passwd_id_seq', 1, false);


--
-- TOC entry 202 (class 1259 OID 143841804)
-- Name: resolution_status; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE resolution_status (
    resolution_status_id integer NOT NULL,
    name character varying(50),
    description character varying(255)
);


ALTER TABLE public.resolution_status OWNER TO clincapture;

--
-- TOC entry 201 (class 1259 OID 143841802)
-- Name: resolution_status_resolution_status_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE resolution_status_resolution_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.resolution_status_resolution_status_id_seq OWNER TO clincapture;

--
-- TOC entry 3356 (class 0 OID 0)
-- Dependencies: 201
-- Name: resolution_status_resolution_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE resolution_status_resolution_status_id_seq OWNED BY resolution_status.resolution_status_id;


--
-- TOC entry 3357 (class 0 OID 0)
-- Dependencies: 201
-- Name: resolution_status_resolution_status_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('resolution_status_resolution_status_id_seq', 1, false);


--
-- TOC entry 204 (class 1259 OID 143841812)
-- Name: response_set; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE response_set (
    response_set_id integer NOT NULL,
    response_type_id integer,
    label character varying(80),
    options_text character varying(4000),
    options_values character varying(4000),
    version_id integer
);


ALTER TABLE public.response_set OWNER TO clincapture;

--
-- TOC entry 203 (class 1259 OID 143841810)
-- Name: response_set_response_set_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE response_set_response_set_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.response_set_response_set_id_seq OWNER TO clincapture;

--
-- TOC entry 3358 (class 0 OID 0)
-- Dependencies: 203
-- Name: response_set_response_set_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE response_set_response_set_id_seq OWNED BY response_set.response_set_id;


--
-- TOC entry 3359 (class 0 OID 0)
-- Dependencies: 203
-- Name: response_set_response_set_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('response_set_response_set_id_seq', 1, false);


--
-- TOC entry 206 (class 1259 OID 143841823)
-- Name: response_type; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE response_type (
    response_type_id integer NOT NULL,
    name character varying(255),
    description character varying(1000)
);


ALTER TABLE public.response_type OWNER TO clincapture;

--
-- TOC entry 205 (class 1259 OID 143841821)
-- Name: response_type_response_type_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE response_type_response_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.response_type_response_type_id_seq OWNER TO clincapture;

--
-- TOC entry 3360 (class 0 OID 0)
-- Dependencies: 205
-- Name: response_type_response_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE response_type_response_type_id_seq OWNED BY response_type.response_type_id;


--
-- TOC entry 3361 (class 0 OID 0)
-- Dependencies: 205
-- Name: response_type_response_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('response_type_response_type_id_seq', 1, false);


--
-- TOC entry 208 (class 1259 OID 143841837)
-- Name: rule; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE rule (
    id integer NOT NULL,
    name character varying(255),
    description character varying(255),
    oc_oid character varying(40),
    enabled boolean,
    rule_expression_id integer NOT NULL,
    owner_id integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    update_id integer,
    status_id integer,
    version integer,
    study_id integer
);


ALTER TABLE public.rule OWNER TO clincapture;

--
-- TOC entry 210 (class 1259 OID 143841848)
-- Name: rule_action; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE rule_action (
    id integer NOT NULL,
    rule_set_rule_id integer NOT NULL,
    action_type integer NOT NULL,
    expression_evaluates_to boolean NOT NULL,
    message character varying(2000),
    email_to character varying(255),
    owner_id integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    update_id integer,
    status_id integer,
    version integer,
    rule_action_run_id integer
);


ALTER TABLE public.rule_action OWNER TO clincapture;

--
-- TOC entry 209 (class 1259 OID 143841846)
-- Name: rule_action_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE rule_action_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.rule_action_id_seq OWNER TO clincapture;

--
-- TOC entry 3362 (class 0 OID 0)
-- Dependencies: 209
-- Name: rule_action_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE rule_action_id_seq OWNED BY rule_action.id;


--
-- TOC entry 3363 (class 0 OID 0)
-- Dependencies: 209
-- Name: rule_action_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('rule_action_id_seq', 1, false);


--
-- TOC entry 284 (class 1259 OID 143843334)
-- Name: rule_action_property; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE rule_action_property (
    id integer NOT NULL,
    rule_action_id integer,
    oc_oid character varying(512),
    value character varying(512),
    version integer,
    rule_expression_id integer
);


ALTER TABLE public.rule_action_property OWNER TO clincapture;

--
-- TOC entry 283 (class 1259 OID 143843332)
-- Name: rule_action_property_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE rule_action_property_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.rule_action_property_id_seq OWNER TO clincapture;

--
-- TOC entry 3364 (class 0 OID 0)
-- Dependencies: 283
-- Name: rule_action_property_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE rule_action_property_id_seq OWNED BY rule_action_property.id;


--
-- TOC entry 3365 (class 0 OID 0)
-- Dependencies: 283
-- Name: rule_action_property_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('rule_action_property_id_seq', 1, false);


--
-- TOC entry 282 (class 1259 OID 143843326)
-- Name: rule_action_run; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE rule_action_run (
    id integer NOT NULL,
    administrative_data_entry boolean,
    initial_data_entry boolean,
    double_data_entry boolean,
    import_data_entry boolean,
    batch boolean,
    version integer
);


ALTER TABLE public.rule_action_run OWNER TO clincapture;

--
-- TOC entry 281 (class 1259 OID 143843324)
-- Name: rule_action_run_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE rule_action_run_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.rule_action_run_id_seq OWNER TO clincapture;

--
-- TOC entry 3366 (class 0 OID 0)
-- Dependencies: 281
-- Name: rule_action_run_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE rule_action_run_id_seq OWNED BY rule_action_run.id;


--
-- TOC entry 3367 (class 0 OID 0)
-- Dependencies: 281
-- Name: rule_action_run_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('rule_action_run_id_seq', 1, false);


--
-- TOC entry 286 (class 1259 OID 143843345)
-- Name: rule_action_run_log; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE rule_action_run_log (
    id integer NOT NULL,
    action_type integer,
    item_data_id integer,
    value character varying(4000),
    rule_oc_oid character varying(40),
    version integer
);


ALTER TABLE public.rule_action_run_log OWNER TO clincapture;

--
-- TOC entry 285 (class 1259 OID 143843343)
-- Name: rule_action_run_log_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE rule_action_run_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.rule_action_run_log_id_seq OWNER TO clincapture;

--
-- TOC entry 3368 (class 0 OID 0)
-- Dependencies: 285
-- Name: rule_action_run_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE rule_action_run_log_id_seq OWNED BY rule_action_run_log.id;


--
-- TOC entry 3369 (class 0 OID 0)
-- Dependencies: 285
-- Name: rule_action_run_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('rule_action_run_log_id_seq', 1, false);


--
-- TOC entry 212 (class 1259 OID 143841859)
-- Name: rule_expression; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE rule_expression (
    id integer NOT NULL,
    value character varying(4000) NOT NULL,
    context integer NOT NULL,
    owner_id integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    update_id integer,
    status_id integer,
    version integer,
    target_event_oid character varying(40),
    target_version_oid character varying(40)
);


ALTER TABLE public.rule_expression OWNER TO clincapture;

--
-- TOC entry 211 (class 1259 OID 143841857)
-- Name: rule_expression_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE rule_expression_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.rule_expression_id_seq OWNER TO clincapture;

--
-- TOC entry 3370 (class 0 OID 0)
-- Dependencies: 211
-- Name: rule_expression_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE rule_expression_id_seq OWNED BY rule_expression.id;


--
-- TOC entry 3371 (class 0 OID 0)
-- Dependencies: 211
-- Name: rule_expression_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('rule_expression_id_seq', 1, false);


--
-- TOC entry 207 (class 1259 OID 143841835)
-- Name: rule_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE rule_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.rule_id_seq OWNER TO clincapture;

--
-- TOC entry 3372 (class 0 OID 0)
-- Dependencies: 207
-- Name: rule_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE rule_id_seq OWNED BY rule.id;


--
-- TOC entry 3373 (class 0 OID 0)
-- Dependencies: 207
-- Name: rule_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('rule_id_seq', 1, false);


--
-- TOC entry 214 (class 1259 OID 143841870)
-- Name: rule_set; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE rule_set (
    id integer NOT NULL,
    rule_expression_id integer NOT NULL,
    study_event_definition_id integer,
    crf_id integer,
    crf_version_id integer,
    study_id integer NOT NULL,
    owner_id integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    update_id integer,
    status_id integer,
    version integer,
    item_id integer,
    item_group_id integer
);


ALTER TABLE public.rule_set OWNER TO clincapture;

--
-- TOC entry 216 (class 1259 OID 143841878)
-- Name: rule_set_audit; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE rule_set_audit (
    id integer NOT NULL,
    rule_set_id integer NOT NULL,
    date_updated timestamp with time zone,
    updater_id integer,
    status_id integer,
    version integer
);


ALTER TABLE public.rule_set_audit OWNER TO clincapture;

--
-- TOC entry 215 (class 1259 OID 143841876)
-- Name: rule_set_audit_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE rule_set_audit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.rule_set_audit_id_seq OWNER TO clincapture;

--
-- TOC entry 3374 (class 0 OID 0)
-- Dependencies: 215
-- Name: rule_set_audit_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE rule_set_audit_id_seq OWNED BY rule_set_audit.id;


--
-- TOC entry 3375 (class 0 OID 0)
-- Dependencies: 215
-- Name: rule_set_audit_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('rule_set_audit_id_seq', 1, false);


--
-- TOC entry 213 (class 1259 OID 143841868)
-- Name: rule_set_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE rule_set_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.rule_set_id_seq OWNER TO clincapture;

--
-- TOC entry 3376 (class 0 OID 0)
-- Dependencies: 213
-- Name: rule_set_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE rule_set_id_seq OWNED BY rule_set.id;


--
-- TOC entry 3377 (class 0 OID 0)
-- Dependencies: 213
-- Name: rule_set_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('rule_set_id_seq', 1, false);


--
-- TOC entry 218 (class 1259 OID 143841886)
-- Name: rule_set_rule; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE rule_set_rule (
    id integer NOT NULL,
    rule_set_id integer NOT NULL,
    rule_id integer NOT NULL,
    owner_id integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    update_id integer,
    status_id integer,
    version integer
);


ALTER TABLE public.rule_set_rule OWNER TO clincapture;

--
-- TOC entry 220 (class 1259 OID 143841894)
-- Name: rule_set_rule_audit; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE rule_set_rule_audit (
    id integer NOT NULL,
    rule_set_rule_id integer NOT NULL,
    date_updated timestamp with time zone,
    updater_id integer,
    status_id integer,
    version integer
);


ALTER TABLE public.rule_set_rule_audit OWNER TO clincapture;

--
-- TOC entry 219 (class 1259 OID 143841892)
-- Name: rule_set_rule_audit_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE rule_set_rule_audit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.rule_set_rule_audit_id_seq OWNER TO clincapture;

--
-- TOC entry 3378 (class 0 OID 0)
-- Dependencies: 219
-- Name: rule_set_rule_audit_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE rule_set_rule_audit_id_seq OWNED BY rule_set_rule_audit.id;


--
-- TOC entry 3379 (class 0 OID 0)
-- Dependencies: 219
-- Name: rule_set_rule_audit_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('rule_set_rule_audit_id_seq', 1, false);


--
-- TOC entry 217 (class 1259 OID 143841884)
-- Name: rule_set_rule_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE rule_set_rule_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.rule_set_rule_id_seq OWNER TO clincapture;

--
-- TOC entry 3380 (class 0 OID 0)
-- Dependencies: 217
-- Name: rule_set_rule_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE rule_set_rule_id_seq OWNED BY rule_set_rule.id;


--
-- TOC entry 3381 (class 0 OID 0)
-- Dependencies: 217
-- Name: rule_set_rule_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('rule_set_rule_id_seq', 1, false);


--
-- TOC entry 292 (class 1259 OID 143843497)
-- Name: scd_item_metadata; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE scd_item_metadata (
    id integer NOT NULL,
    scd_item_form_metadata_id integer,
    control_item_form_metadata_id integer,
    control_item_name character varying(255),
    option_value character varying(500),
    message character varying(3000),
    version integer
);


ALTER TABLE public.scd_item_metadata OWNER TO clincapture;

--
-- TOC entry 291 (class 1259 OID 143843495)
-- Name: scd_item_metadata_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE scd_item_metadata_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.scd_item_metadata_id_seq OWNER TO clincapture;

--
-- TOC entry 3382 (class 0 OID 0)
-- Dependencies: 291
-- Name: scd_item_metadata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE scd_item_metadata_id_seq OWNED BY scd_item_metadata.id;


--
-- TOC entry 3383 (class 0 OID 0)
-- Dependencies: 291
-- Name: scd_item_metadata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('scd_item_metadata_id_seq', 1, false);


--
-- TOC entry 222 (class 1259 OID 143841902)
-- Name: section; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE section (
    section_id integer NOT NULL,
    crf_version_id integer NOT NULL,
    status_id integer,
    label character varying(2000),
    title character varying(2000),
    subtitle text,
    instructions text,
    page_number_label character varying(5),
    ordinal integer,
    parent_id integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    owner_id integer NOT NULL,
    update_id integer,
    borders integer
);


ALTER TABLE public.section OWNER TO clincapture;

--
-- TOC entry 221 (class 1259 OID 143841900)
-- Name: section_section_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE section_section_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.section_section_id_seq OWNER TO clincapture;

--
-- TOC entry 3384 (class 0 OID 0)
-- Dependencies: 221
-- Name: section_section_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE section_section_id_seq OWNED BY section.section_id;


--
-- TOC entry 3385 (class 0 OID 0)
-- Dependencies: 221
-- Name: section_section_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('section_section_id_seq', 1, false);


--
-- TOC entry 224 (class 1259 OID 143841913)
-- Name: status; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE status (
    status_id integer NOT NULL,
    name character varying(255),
    description character varying(1000)
);


ALTER TABLE public.status OWNER TO clincapture;

--
-- TOC entry 223 (class 1259 OID 143841911)
-- Name: status_status_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE status_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.status_status_id_seq OWNER TO clincapture;

--
-- TOC entry 3386 (class 0 OID 0)
-- Dependencies: 223
-- Name: status_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE status_status_id_seq OWNED BY status.status_id;


--
-- TOC entry 3387 (class 0 OID 0)
-- Dependencies: 223
-- Name: status_status_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('status_status_id_seq', 1, false);


--
-- TOC entry 226 (class 1259 OID 143841924)
-- Name: study; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE study (
    study_id integer NOT NULL,
    parent_study_id integer,
    unique_identifier character varying(30),
    secondary_identifier character varying(255),
    name character varying(255),
    summary character varying(2000),
    date_planned_start timestamp with time zone,
    date_planned_end timestamp with time zone,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    owner_id integer,
    update_id integer,
    type_id integer,
    status_id integer,
    principal_investigator character varying(255),
    facility_name character varying(255),
    facility_city character varying(255),
    facility_state character varying(20),
    facility_zip character varying(64),
    facility_country character varying(64),
    facility_recruitment_status character varying(60),
    facility_contact_name character varying(255),
    facility_contact_degree character varying(255),
    facility_contact_phone character varying(255),
    facility_contact_email character varying(255),
    protocol_type character varying(30),
    protocol_description character varying(1000),
    protocol_date_verification timestamp with time zone,
    phase character varying(30),
    expected_total_enrollment integer,
    sponsor character varying(255),
    collaborators character varying(1000),
    medline_identifier character varying(255),
    url character varying(255),
    url_description character varying(255),
    conditions character varying(500),
    keywords character varying(255),
    eligibility character varying(500),
    gender character varying(30),
    age_max character varying(3),
    age_min character varying(3),
    healthy_volunteer_accepted boolean,
    purpose character varying(64),
    allocation character varying(64),
    masking character varying(30),
    control character varying(30),
    assignment character varying(30),
    endpoint character varying(64),
    interventions character varying(1000),
    duration character varying(30),
    selection character varying(30),
    timing character varying(30),
    official_title character varying(255),
    results_reference boolean,
    oc_oid character varying(40) NOT NULL,
    old_status_id integer DEFAULT 1,
    brief_title character varying(255) DEFAULT ''::character varying,
    origin character varying(20) DEFAULT 'gui'::character varying NOT NULL
);


ALTER TABLE public.study OWNER TO clincapture;

--
-- TOC entry 228 (class 1259 OID 143841935)
-- Name: study_event; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE study_event (
    study_event_id integer NOT NULL,
    study_event_definition_id integer,
    study_subject_id integer,
    location character varying(2000),
    sample_ordinal integer,
    date_start timestamp without time zone,
    date_end timestamp without time zone,
    owner_id integer,
    status_id integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    update_id integer,
    subject_event_status_id integer,
    start_time_flag boolean,
    end_time_flag boolean,
    prev_subject_event_status integer DEFAULT 0 NOT NULL,
    reference_visit_id integer DEFAULT 0,
    signed_data bytea,
    old_status_id integer DEFAULT 1,
    states character varying(8) DEFAULT '0,0,0'::character varying NOT NULL
);


ALTER TABLE public.study_event OWNER TO clincapture;

--
-- TOC entry 230 (class 1259 OID 143841946)
-- Name: study_event_definition; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE study_event_definition (
    study_event_definition_id integer NOT NULL,
    study_id integer,
    name character varying(2000),
    description character varying(2000),
    repeating boolean,
    type character varying(20),
    category character varying(2000),
    owner_id integer,
    status_id integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    update_id integer,
    ordinal integer,
    oc_oid character varying(40) NOT NULL,
    day_min integer,
    day_max integer,
    day_email integer,
    schedule_day integer,
    reference_visit boolean DEFAULT false,
    email_user_id integer DEFAULT 0
);


ALTER TABLE public.study_event_definition OWNER TO clincapture;

--
-- TOC entry 301 (class 1259 OID 143843580)
-- Name: study_event_definition_oid_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE study_event_definition_oid_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.study_event_definition_oid_id_seq OWNER TO clincapture;

--
-- TOC entry 3388 (class 0 OID 0)
-- Dependencies: 301
-- Name: study_event_definition_oid_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('study_event_definition_oid_id_seq', 1, false);


--
-- TOC entry 229 (class 1259 OID 143841944)
-- Name: study_event_definition_study_event_definition_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE study_event_definition_study_event_definition_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.study_event_definition_study_event_definition_id_seq OWNER TO clincapture;

--
-- TOC entry 3389 (class 0 OID 0)
-- Dependencies: 229
-- Name: study_event_definition_study_event_definition_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE study_event_definition_study_event_definition_id_seq OWNED BY study_event_definition.study_event_definition_id;


--
-- TOC entry 3390 (class 0 OID 0)
-- Dependencies: 229
-- Name: study_event_definition_study_event_definition_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('study_event_definition_study_event_definition_id_seq', 1, false);


--
-- TOC entry 227 (class 1259 OID 143841933)
-- Name: study_event_study_event_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE study_event_study_event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.study_event_study_event_id_seq OWNER TO clincapture;

--
-- TOC entry 3391 (class 0 OID 0)
-- Dependencies: 227
-- Name: study_event_study_event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE study_event_study_event_id_seq OWNED BY study_event.study_event_id;


--
-- TOC entry 3392 (class 0 OID 0)
-- Dependencies: 227
-- Name: study_event_study_event_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('study_event_study_event_id_seq', 1, false);


--
-- TOC entry 232 (class 1259 OID 143841957)
-- Name: study_group; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE study_group (
    study_group_id integer NOT NULL,
    name character varying(255),
    description character varying(1000),
    study_group_class_id integer
);


ALTER TABLE public.study_group OWNER TO clincapture;

--
-- TOC entry 234 (class 1259 OID 143841968)
-- Name: study_group_class; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE study_group_class (
    study_group_class_id integer NOT NULL,
    name character varying(30),
    study_id integer,
    owner_id integer,
    date_created timestamp with time zone,
    group_class_type_id integer,
    status_id integer,
    date_updated timestamp with time zone,
    update_id integer,
    subject_assignment character varying(30),
    is_default boolean DEFAULT false,
    dynamic_ordinal integer DEFAULT 0
);


ALTER TABLE public.study_group_class OWNER TO clincapture;

--
-- TOC entry 233 (class 1259 OID 143841966)
-- Name: study_group_class_study_group_class_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE study_group_class_study_group_class_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.study_group_class_study_group_class_id_seq OWNER TO clincapture;

--
-- TOC entry 3393 (class 0 OID 0)
-- Dependencies: 233
-- Name: study_group_class_study_group_class_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE study_group_class_study_group_class_id_seq OWNED BY study_group_class.study_group_class_id;


--
-- TOC entry 3394 (class 0 OID 0)
-- Dependencies: 233
-- Name: study_group_class_study_group_class_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('study_group_class_study_group_class_id_seq', 1, false);


--
-- TOC entry 231 (class 1259 OID 143841955)
-- Name: study_group_study_group_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE study_group_study_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.study_group_study_group_id_seq OWNER TO clincapture;

--
-- TOC entry 3395 (class 0 OID 0)
-- Dependencies: 231
-- Name: study_group_study_group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE study_group_study_group_id_seq OWNED BY study_group.study_group_id;


--
-- TOC entry 3396 (class 0 OID 0)
-- Dependencies: 231
-- Name: study_group_study_group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('study_group_study_group_id_seq', 1, false);


--
-- TOC entry 276 (class 1259 OID 143843092)
-- Name: study_module_status; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE study_module_status (
    id integer NOT NULL,
    study_id integer,
    study integer DEFAULT 1,
    crf integer DEFAULT 1,
    event_definition integer DEFAULT 1,
    subject_group integer DEFAULT 1,
    rule integer DEFAULT 1,
    site integer DEFAULT 1,
    users integer DEFAULT 1,
    version integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    owner_id integer,
    update_id integer,
    status_id integer
);


ALTER TABLE public.study_module_status OWNER TO clincapture;

--
-- TOC entry 275 (class 1259 OID 143843090)
-- Name: study_module_status_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE study_module_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.study_module_status_id_seq OWNER TO clincapture;

--
-- TOC entry 3397 (class 0 OID 0)
-- Dependencies: 275
-- Name: study_module_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE study_module_status_id_seq OWNED BY study_module_status.id;


--
-- TOC entry 3398 (class 0 OID 0)
-- Dependencies: 275
-- Name: study_module_status_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('study_module_status_id_seq', 1, false);


--
-- TOC entry 302 (class 1259 OID 143843582)
-- Name: study_oid_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE study_oid_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.study_oid_id_seq OWNER TO clincapture;

--
-- TOC entry 3399 (class 0 OID 0)
-- Dependencies: 302
-- Name: study_oid_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('study_oid_id_seq', 1, false);


--
-- TOC entry 236 (class 1259 OID 143841976)
-- Name: study_parameter; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE study_parameter (
    study_parameter_id integer NOT NULL,
    handle character varying(50),
    name character varying(50),
    description character varying(255),
    default_value character varying(50),
    inheritable boolean DEFAULT true,
    overridable boolean
);


ALTER TABLE public.study_parameter OWNER TO clincapture;

--
-- TOC entry 235 (class 1259 OID 143841974)
-- Name: study_parameter_study_parameter_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE study_parameter_study_parameter_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.study_parameter_study_parameter_id_seq OWNER TO clincapture;

--
-- TOC entry 3400 (class 0 OID 0)
-- Dependencies: 235
-- Name: study_parameter_study_parameter_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE study_parameter_study_parameter_id_seq OWNED BY study_parameter.study_parameter_id;


--
-- TOC entry 3401 (class 0 OID 0)
-- Dependencies: 235
-- Name: study_parameter_study_parameter_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('study_parameter_study_parameter_id_seq', 1, false);


--
-- TOC entry 238 (class 1259 OID 143841985)
-- Name: study_parameter_value; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE study_parameter_value (
    study_parameter_value_id integer NOT NULL,
    study_id integer,
    value character varying(50),
    parameter character varying(50)
);


ALTER TABLE public.study_parameter_value OWNER TO clincapture;

--
-- TOC entry 237 (class 1259 OID 143841983)
-- Name: study_parameter_value_study_parameter_value_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE study_parameter_value_study_parameter_value_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.study_parameter_value_study_parameter_value_id_seq OWNER TO clincapture;

--
-- TOC entry 3402 (class 0 OID 0)
-- Dependencies: 237
-- Name: study_parameter_value_study_parameter_value_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE study_parameter_value_study_parameter_value_id_seq OWNED BY study_parameter_value.study_parameter_value_id;


--
-- TOC entry 3403 (class 0 OID 0)
-- Dependencies: 237
-- Name: study_parameter_value_study_parameter_value_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('study_parameter_value_study_parameter_value_id_seq', 4, true);


--
-- TOC entry 225 (class 1259 OID 143841922)
-- Name: study_study_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE study_study_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.study_study_id_seq OWNER TO clincapture;

--
-- TOC entry 3404 (class 0 OID 0)
-- Dependencies: 225
-- Name: study_study_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE study_study_id_seq OWNED BY study.study_id;


--
-- TOC entry 3405 (class 0 OID 0)
-- Dependencies: 225
-- Name: study_study_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('study_study_id_seq', 2, true);


--
-- TOC entry 240 (class 1259 OID 143841991)
-- Name: study_subject; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE study_subject (
    study_subject_id integer NOT NULL,
    label character varying(30),
    secondary_label character varying(30),
    subject_id integer,
    study_id integer,
    status_id integer,
    enrollment_date timestamp with time zone,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    owner_id integer,
    update_id integer,
    oc_oid character varying(40) NOT NULL,
    dynamic_group_class_id integer DEFAULT 0,
    randomization_date timestamp with time zone,
    randomization_result character varying(255),
    old_status_id integer DEFAULT 1,
    states character varying(11) DEFAULT '0,0,0,0'::character varying NOT NULL
);


ALTER TABLE public.study_subject OWNER TO clincapture;

--
-- TOC entry 320 (class 1259 OID 143844028)
-- Name: study_subject_id; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE study_subject_id (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    index integer DEFAULT 0,
    version integer
);


ALTER TABLE public.study_subject_id OWNER TO clincapture;

--
-- TOC entry 319 (class 1259 OID 143844026)
-- Name: study_subject_id_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE study_subject_id_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.study_subject_id_id_seq OWNER TO clincapture;

--
-- TOC entry 3406 (class 0 OID 0)
-- Dependencies: 319
-- Name: study_subject_id_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE study_subject_id_id_seq OWNED BY study_subject_id.id;


--
-- TOC entry 3407 (class 0 OID 0)
-- Dependencies: 319
-- Name: study_subject_id_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('study_subject_id_id_seq', 1, false);


--
-- TOC entry 303 (class 1259 OID 143843584)
-- Name: study_subject_oid_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE study_subject_oid_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.study_subject_oid_id_seq OWNER TO clincapture;

--
-- TOC entry 3408 (class 0 OID 0)
-- Dependencies: 303
-- Name: study_subject_oid_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('study_subject_oid_id_seq', 1, false);


--
-- TOC entry 239 (class 1259 OID 143841989)
-- Name: study_subject_study_subject_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE study_subject_study_subject_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.study_subject_study_subject_id_seq OWNER TO clincapture;

--
-- TOC entry 3409 (class 0 OID 0)
-- Dependencies: 239
-- Name: study_subject_study_subject_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE study_subject_study_subject_id_seq OWNED BY study_subject.study_subject_id;


--
-- TOC entry 3410 (class 0 OID 0)
-- Dependencies: 239
-- Name: study_subject_study_subject_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('study_subject_study_subject_id_seq', 1, false);


--
-- TOC entry 242 (class 1259 OID 143841999)
-- Name: study_type; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE study_type (
    study_type_id integer NOT NULL,
    name character varying(255),
    description character varying(1000)
);


ALTER TABLE public.study_type OWNER TO clincapture;

--
-- TOC entry 241 (class 1259 OID 143841997)
-- Name: study_type_study_type_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE study_type_study_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.study_type_study_type_id_seq OWNER TO clincapture;

--
-- TOC entry 3411 (class 0 OID 0)
-- Dependencies: 241
-- Name: study_type_study_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE study_type_study_type_id_seq OWNED BY study_type.study_type_id;


--
-- TOC entry 3412 (class 0 OID 0)
-- Dependencies: 241
-- Name: study_type_study_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('study_type_study_type_id_seq', 1, false);


--
-- TOC entry 243 (class 1259 OID 143842008)
-- Name: study_user_role; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE study_user_role (
    role_name character varying(40),
    study_id integer,
    status_id integer,
    owner_id integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    update_id integer,
    user_name character varying(64),
    study_user_role_id integer NOT NULL
);


ALTER TABLE public.study_user_role OWNER TO clincapture;

--
-- TOC entry 325 (class 1259 OID 143844240)
-- Name: study_user_role_study_user_role_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE study_user_role_study_user_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.study_user_role_study_user_role_id_seq OWNER TO clincapture;

--
-- TOC entry 3413 (class 0 OID 0)
-- Dependencies: 325
-- Name: study_user_role_study_user_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE study_user_role_study_user_role_id_seq OWNED BY study_user_role.study_user_role_id;


--
-- TOC entry 3414 (class 0 OID 0)
-- Dependencies: 325
-- Name: study_user_role_study_user_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('study_user_role_study_user_role_id_seq', 1, true);


--
-- TOC entry 245 (class 1259 OID 143842013)
-- Name: subject; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE subject (
    subject_id integer NOT NULL,
    father_id integer,
    mother_id integer,
    status_id integer,
    date_of_birth timestamp with time zone,
    gender character(1),
    unique_identifier character varying(255),
    date_created timestamp with time zone,
    owner_id integer,
    date_updated timestamp with time zone,
    update_id integer,
    dob_collected boolean
);


ALTER TABLE public.subject OWNER TO clincapture;

--
-- TOC entry 247 (class 1259 OID 143842021)
-- Name: subject_event_status; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE subject_event_status (
    subject_event_status_id integer NOT NULL,
    name character varying(255),
    description character varying(1000)
);


ALTER TABLE public.subject_event_status OWNER TO clincapture;

--
-- TOC entry 246 (class 1259 OID 143842019)
-- Name: subject_event_status_subject_event_status_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE subject_event_status_subject_event_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.subject_event_status_subject_event_status_id_seq OWNER TO clincapture;

--
-- TOC entry 3415 (class 0 OID 0)
-- Dependencies: 246
-- Name: subject_event_status_subject_event_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE subject_event_status_subject_event_status_id_seq OWNED BY subject_event_status.subject_event_status_id;


--
-- TOC entry 3416 (class 0 OID 0)
-- Dependencies: 246
-- Name: subject_event_status_subject_event_status_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('subject_event_status_subject_event_status_id_seq', 1, false);


--
-- TOC entry 249 (class 1259 OID 143842032)
-- Name: subject_group_map; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE subject_group_map (
    subject_group_map_id integer NOT NULL,
    study_group_class_id integer,
    study_subject_id integer,
    study_group_id integer,
    status_id integer,
    owner_id integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    update_id integer,
    notes character varying(255)
);


ALTER TABLE public.subject_group_map OWNER TO clincapture;

--
-- TOC entry 248 (class 1259 OID 143842030)
-- Name: subject_group_map_subject_group_map_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE subject_group_map_subject_group_map_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.subject_group_map_subject_group_map_id_seq OWNER TO clincapture;

--
-- TOC entry 3417 (class 0 OID 0)
-- Dependencies: 248
-- Name: subject_group_map_subject_group_map_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE subject_group_map_subject_group_map_id_seq OWNED BY subject_group_map.subject_group_map_id;


--
-- TOC entry 3418 (class 0 OID 0)
-- Dependencies: 248
-- Name: subject_group_map_subject_group_map_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('subject_group_map_subject_group_map_id_seq', 1, false);


--
-- TOC entry 244 (class 1259 OID 143842011)
-- Name: subject_subject_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE subject_subject_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.subject_subject_id_seq OWNER TO clincapture;

--
-- TOC entry 3419 (class 0 OID 0)
-- Dependencies: 244
-- Name: subject_subject_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE subject_subject_id_seq OWNED BY subject.subject_id;


--
-- TOC entry 3420 (class 0 OID 0)
-- Dependencies: 244
-- Name: subject_subject_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('subject_subject_id_seq', 1, false);


SET default_with_oids = false;

--
-- TOC entry 145 (class 1259 OID 143841381)
-- Name: system; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE system (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    value character varying(255),
    value_type character varying(255) DEFAULT 'STRING'::character varying,
    required boolean DEFAULT true,
    type character varying(255) DEFAULT 'TEXT'::character varying,
    type_values character varying(255) DEFAULT ''::character varying,
    size integer DEFAULT 60,
    show_measurements boolean DEFAULT false,
    show_description boolean DEFAULT false,
    show_note boolean DEFAULT false,
    group_id integer DEFAULT 0,
    crc character varying(255) DEFAULT 'HIDDEN'::character varying,
    investigator character varying(255) DEFAULT 'HIDDEN'::character varying,
    monitor character varying(255) DEFAULT 'HIDDEN'::character varying,
    admin character varying(255) DEFAULT 'WRITE'::character varying,
    root character varying(255) DEFAULT 'WRITE'::character varying,
    order_id integer DEFAULT 0,
    version integer DEFAULT 1
);


ALTER TABLE public.system OWNER TO clincapture;

--
-- TOC entry 143 (class 1259 OID 143841368)
-- Name: system_group; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE system_group (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    parent_id integer DEFAULT 0,
    order_id integer DEFAULT 0,
    version integer DEFAULT 1
);


ALTER TABLE public.system_group OWNER TO clincapture;

--
-- TOC entry 142 (class 1259 OID 143841366)
-- Name: system_group_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE system_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.system_group_id_seq OWNER TO clincapture;

--
-- TOC entry 3421 (class 0 OID 0)
-- Dependencies: 142
-- Name: system_group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE system_group_id_seq OWNED BY system_group.id;


--
-- TOC entry 3422 (class 0 OID 0)
-- Dependencies: 142
-- Name: system_group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('system_group_id_seq', 1, false);


--
-- TOC entry 144 (class 1259 OID 143841379)
-- Name: system_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE system_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.system_id_seq OWNER TO clincapture;

--
-- TOC entry 3423 (class 0 OID 0)
-- Dependencies: 144
-- Name: system_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE system_id_seq OWNED BY system.id;


--
-- TOC entry 3424 (class 0 OID 0)
-- Dependencies: 144
-- Name: system_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('system_id_seq', 63, true);


SET default_with_oids = true;

--
-- TOC entry 312 (class 1259 OID 143843960)
-- Name: term; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE term (
    id integer NOT NULL,
    version integer,
    dictionary_id integer,
    preferred_name character varying(255),
    date_created timestamp with time zone,
    external_dictionary_name character varying(255),
    http_path character varying(255),
    local_alias character varying(400)
);


ALTER TABLE public.term OWNER TO clincapture;

--
-- TOC entry 318 (class 1259 OID 143844014)
-- Name: term_element; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE term_element (
    id integer NOT NULL,
    term_id integer,
    element_name character varying(255),
    term_name character varying(255),
    term_code character varying(255),
    version integer
);


ALTER TABLE public.term_element OWNER TO clincapture;

--
-- TOC entry 317 (class 1259 OID 143844012)
-- Name: term_element_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE term_element_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.term_element_id_seq OWNER TO clincapture;

--
-- TOC entry 3425 (class 0 OID 0)
-- Dependencies: 317
-- Name: term_element_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE term_element_id_seq OWNED BY term_element.id;


--
-- TOC entry 3426 (class 0 OID 0)
-- Dependencies: 317
-- Name: term_element_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('term_element_id_seq', 1, false);


--
-- TOC entry 311 (class 1259 OID 143843958)
-- Name: term_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE term_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.term_id_seq OWNER TO clincapture;

--
-- TOC entry 3427 (class 0 OID 0)
-- Dependencies: 311
-- Name: term_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE term_id_seq OWNED BY term.id;


--
-- TOC entry 3428 (class 0 OID 0)
-- Dependencies: 311
-- Name: term_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('term_id_seq', 1, false);


--
-- TOC entry 294 (class 1259 OID 143843537)
-- Name: usage_statistics_data; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE usage_statistics_data (
    id integer NOT NULL,
    param_key character varying(255),
    param_value character varying(1000),
    update_timestamp timestamp without time zone,
    version integer
);


ALTER TABLE public.usage_statistics_data OWNER TO clincapture;

--
-- TOC entry 293 (class 1259 OID 143843535)
-- Name: usage_statistics_data_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE usage_statistics_data_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.usage_statistics_data_id_seq OWNER TO clincapture;

--
-- TOC entry 3429 (class 0 OID 0)
-- Dependencies: 293
-- Name: usage_statistics_data_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE usage_statistics_data_id_seq OWNED BY usage_statistics_data.id;


--
-- TOC entry 3430 (class 0 OID 0)
-- Dependencies: 293
-- Name: usage_statistics_data_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('usage_statistics_data_id_seq', 1, true);


--
-- TOC entry 251 (class 1259 OID 143842040)
-- Name: user_account; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE user_account (
    user_id integer NOT NULL,
    user_name character varying(64),
    passwd character varying(255),
    first_name character varying(50),
    last_name character varying(50),
    email character varying(120),
    active_study integer,
    institutional_affiliation character varying(255),
    status_id integer,
    owner_id integer,
    date_created timestamp with time zone,
    date_updated timestamp with time zone,
    date_lastvisit timestamp without time zone,
    passwd_timestamp timestamp with time zone,
    passwd_challenge_question character varying(64),
    passwd_challenge_answer character varying(255),
    phone character varying(64),
    user_type_id integer,
    update_id integer,
    enabled boolean DEFAULT true NOT NULL,
    account_non_locked boolean DEFAULT true NOT NULL,
    lock_counter integer DEFAULT 0 NOT NULL,
    run_webservices boolean DEFAULT false NOT NULL,
    pentaho_user_session character varying(100),
    pentaho_token_date timestamp with time zone,
    time_zone_id text
);


ALTER TABLE public.user_account OWNER TO clincapture;

--
-- TOC entry 250 (class 1259 OID 143842038)
-- Name: user_account_user_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE user_account_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_account_user_id_seq OWNER TO clincapture;

--
-- TOC entry 3431 (class 0 OID 0)
-- Dependencies: 250
-- Name: user_account_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE user_account_user_id_seq OWNED BY user_account.user_id;


--
-- TOC entry 3432 (class 0 OID 0)
-- Dependencies: 250
-- Name: user_account_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('user_account_user_id_seq', 1, true);


--
-- TOC entry 253 (class 1259 OID 143842051)
-- Name: user_role; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE user_role (
    role_id integer NOT NULL,
    role_name character varying(50) NOT NULL,
    parent_id integer,
    role_desc character varying(2000)
);


ALTER TABLE public.user_role OWNER TO clincapture;

--
-- TOC entry 252 (class 1259 OID 143842049)
-- Name: user_role_role_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE user_role_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_role_role_id_seq OWNER TO clincapture;

--
-- TOC entry 3433 (class 0 OID 0)
-- Dependencies: 252
-- Name: user_role_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE user_role_role_id_seq OWNED BY user_role.role_id;


--
-- TOC entry 3434 (class 0 OID 0)
-- Dependencies: 252
-- Name: user_role_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('user_role_role_id_seq', 1, false);


--
-- TOC entry 255 (class 1259 OID 143842062)
-- Name: user_type; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE user_type (
    user_type_id integer NOT NULL,
    user_type character varying(50)
);


ALTER TABLE public.user_type OWNER TO clincapture;

--
-- TOC entry 254 (class 1259 OID 143842060)
-- Name: user_type_user_type_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE user_type_user_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_type_user_type_id_seq OWNER TO clincapture;

--
-- TOC entry 3435 (class 0 OID 0)
-- Dependencies: 254
-- Name: user_type_user_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE user_type_user_type_id_seq OWNED BY user_type.user_type_id;


--
-- TOC entry 3436 (class 0 OID 0)
-- Dependencies: 254
-- Name: user_type_user_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('user_type_user_type_id_seq', 1, false);


--
-- TOC entry 256 (class 1259 OID 143842068)
-- Name: versioning_map; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE versioning_map (
    crf_version_id integer,
    item_id integer
);


ALTER TABLE public.versioning_map OWNER TO clincapture;

--
-- TOC entry 324 (class 1259 OID 143844048)
-- Name: widget; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE widget (
    id integer NOT NULL,
    version integer,
    widget_name character varying(250),
    description character varying(2048),
    have_access character varying(50),
    display_as_default character varying(50),
    study_metrics boolean,
    site_metrics boolean,
    two_column_widget boolean
);


ALTER TABLE public.widget OWNER TO clincapture;

--
-- TOC entry 323 (class 1259 OID 143844046)
-- Name: widget_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE widget_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.widget_id_seq OWNER TO clincapture;

--
-- TOC entry 3437 (class 0 OID 0)
-- Dependencies: 323
-- Name: widget_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE widget_id_seq OWNED BY widget.id;


--
-- TOC entry 3438 (class 0 OID 0)
-- Dependencies: 323
-- Name: widget_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('widget_id_seq', 10, true);


--
-- TOC entry 322 (class 1259 OID 143844040)
-- Name: widgets_layout; Type: TABLE; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE TABLE widgets_layout (
    id integer NOT NULL,
    user_id integer,
    study_id integer,
    widget_id integer,
    ordinal integer,
    version integer
);


ALTER TABLE public.widgets_layout OWNER TO clincapture;

--
-- TOC entry 321 (class 1259 OID 143844038)
-- Name: widgets_layout_id_seq; Type: SEQUENCE; Schema: public; Owner: clincapture
--

CREATE SEQUENCE widgets_layout_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.widgets_layout_id_seq OWNER TO clincapture;

--
-- TOC entry 3439 (class 0 OID 0)
-- Dependencies: 321
-- Name: widgets_layout_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: clincapture
--

ALTER SEQUENCE widgets_layout_id_seq OWNED BY widgets_layout.id;


--
-- TOC entry 3440 (class 0 OID 0)
-- Dependencies: 321
-- Name: widgets_layout_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clincapture
--

SELECT pg_catalog.setval('widgets_layout_id_seq', 1, false);


--
-- TOC entry 2483 (class 2604 OID 143841413)
-- Name: archived_dataset_file_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY archived_dataset_file ALTER COLUMN archived_dataset_file_id SET DEFAULT nextval('archived_dataset_file_archived_dataset_file_id_seq'::regclass);


--
-- TOC entry 2484 (class 2604 OID 143841424)
-- Name: audit_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY audit_event ALTER COLUMN audit_id SET DEFAULT nextval('audit_event_audit_id_seq'::regclass);


--
-- TOC entry 2485 (class 2604 OID 143841444)
-- Name: audit_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY audit_log_event ALTER COLUMN audit_id SET DEFAULT nextval('audit_log_event_audit_id_seq'::regclass);


--
-- TOC entry 2486 (class 2604 OID 143841455)
-- Name: audit_log_event_type_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY audit_log_event_type ALTER COLUMN audit_log_event_type_id SET DEFAULT nextval('audit_log_event_type_audit_log_event_type_id_seq'::regclass);


--
-- TOC entry 2638 (class 2604 OID 143845182)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY audit_log_randomization ALTER COLUMN id SET DEFAULT nextval('audit_log_randomization_id_seq'::regclass);


--
-- TOC entry 2598 (class 2604 OID 143843039)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY audit_user_login ALTER COLUMN id SET DEFAULT nextval('audit_user_login_id_seq'::regclass);


--
-- TOC entry 2586 (class 2604 OID 143842878)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY authorities ALTER COLUMN id SET DEFAULT nextval('authorities_id_seq'::regclass);


--
-- TOC entry 2625 (class 2604 OID 143843974)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY coded_item ALTER COLUMN id SET DEFAULT nextval('coded_item_id_seq'::regclass);


--
-- TOC entry 2626 (class 2604 OID 143844006)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY coded_item_element ALTER COLUMN id SET DEFAULT nextval('coded_item_element_id_seq'::regclass);


--
-- TOC entry 2487 (class 2604 OID 143841463)
-- Name: completion_status_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY completion_status ALTER COLUMN completion_status_id SET DEFAULT nextval('completion_status_completion_status_id_seq'::regclass);


--
-- TOC entry 2599 (class 2604 OID 143843079)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY configuration ALTER COLUMN id SET DEFAULT nextval('configuration_id_seq'::regclass);


--
-- TOC entry 2488 (class 2604 OID 143841474)
-- Name: crf_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY crf ALTER COLUMN crf_id SET DEFAULT nextval('crf_crf_id_seq'::regclass);


--
-- TOC entry 2490 (class 2604 OID 143841485)
-- Name: crf_version_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY crf_version ALTER COLUMN crf_version_id SET DEFAULT nextval('crf_version_crf_version_id_seq'::regclass);


--
-- TOC entry 2632 (class 2604 OID 143844255)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY crfs_masking ALTER COLUMN id SET DEFAULT nextval('crfs_masking_id_seq'::regclass);


--
-- TOC entry 2491 (class 2604 OID 143841496)
-- Name: dataset_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dataset ALTER COLUMN dataset_id SET DEFAULT nextval('dataset_dataset_id_seq'::regclass);


--
-- TOC entry 2597 (class 2604 OID 143843018)
-- Name: dataset_item_status_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dataset_item_status ALTER COLUMN dataset_item_status_id SET DEFAULT nextval('dataset_item_status_dataset_item_status_id_seq'::regclass);


--
-- TOC entry 2623 (class 2604 OID 143843941)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dictionary ALTER COLUMN id SET DEFAULT nextval('dictionary_id_seq'::regclass);


--
-- TOC entry 2622 (class 2604 OID 143843708)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY discrepancy_description ALTER COLUMN id SET DEFAULT nextval('discrepancy_description_id_seq'::regclass);


--
-- TOC entry 2508 (class 2604 OID 143841603)
-- Name: discrepancy_note_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY discrepancy_note ALTER COLUMN discrepancy_note_id SET DEFAULT nextval('discrepancy_note_discrepancy_note_id_seq'::regclass);


--
-- TOC entry 2509 (class 2604 OID 143841614)
-- Name: discrepancy_note_type_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY discrepancy_note_type ALTER COLUMN discrepancy_note_type_id SET DEFAULT nextval('discrepancy_note_type_discrepancy_note_type_id_seq'::regclass);


--
-- TOC entry 2613 (class 2604 OID 143843383)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dyn_item_form_metadata ALTER COLUMN id SET DEFAULT nextval('dyn_item_form_metadata_id_seq'::regclass);


--
-- TOC entry 2616 (class 2604 OID 143843392)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dyn_item_group_metadata ALTER COLUMN id SET DEFAULT nextval('dyn_item_group_metadata_id_seq'::regclass);


--
-- TOC entry 2621 (class 2604 OID 143843677)
-- Name: dynamic_event_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dynamic_event ALTER COLUMN dynamic_event_id SET DEFAULT nextval('dynamic_event_dynamic_event_id_seq'::regclass);


--
-- TOC entry 2636 (class 2604 OID 143845152)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY edc_item_metadata ALTER COLUMN id SET DEFAULT nextval('edc_item_metadata_id_seq'::regclass);


--
-- TOC entry 2510 (class 2604 OID 143841637)
-- Name: event_crf_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY event_crf ALTER COLUMN event_crf_id SET DEFAULT nextval('event_crf_event_crf_id_seq'::regclass);


--
-- TOC entry 2634 (class 2604 OID 143845000)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY event_crf_section ALTER COLUMN id SET DEFAULT nextval('event_crf_section_id_seq'::regclass);


--
-- TOC entry 2517 (class 2604 OID 143841649)
-- Name: event_definition_crf_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY event_definition_crf ALTER COLUMN event_definition_crf_id SET DEFAULT nextval('event_definition_crf_event_definition_crf_id_seq'::regclass);


--
-- TOC entry 2523 (class 2604 OID 143841658)
-- Name: export_format_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY export_format ALTER COLUMN export_format_id SET DEFAULT nextval('export_format_export_format_id_seq'::regclass);


--
-- TOC entry 2524 (class 2604 OID 143841683)
-- Name: group_class_type_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY group_class_types ALTER COLUMN group_class_type_id SET DEFAULT nextval('group_class_types_group_class_type_id_seq'::regclass);


--
-- TOC entry 2525 (class 2604 OID 143841694)
-- Name: item_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item ALTER COLUMN item_id SET DEFAULT nextval('item_item_id_seq'::regclass);


--
-- TOC entry 2526 (class 2604 OID 143841705)
-- Name: item_data_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_data ALTER COLUMN item_data_id SET DEFAULT nextval('item_data_item_data_id_seq'::regclass);


--
-- TOC entry 2528 (class 2604 OID 143841716)
-- Name: item_data_type_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_data_type ALTER COLUMN item_data_type_id SET DEFAULT nextval('item_data_type_item_data_type_id_seq'::regclass);


--
-- TOC entry 2529 (class 2604 OID 143841727)
-- Name: item_form_metadata_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_form_metadata ALTER COLUMN item_form_metadata_id SET DEFAULT nextval('item_form_metadata_item_form_metadata_id_seq'::regclass);


--
-- TOC entry 2532 (class 2604 OID 143841738)
-- Name: item_group_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_group ALTER COLUMN item_group_id SET DEFAULT nextval('item_group_item_group_id_seq'::regclass);


--
-- TOC entry 2533 (class 2604 OID 143841746)
-- Name: item_group_metadata_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_group_metadata ALTER COLUMN item_group_metadata_id SET DEFAULT nextval('item_group_metadata_item_group_metadata_id_seq'::regclass);


--
-- TOC entry 2536 (class 2604 OID 143841757)
-- Name: item_reference_type_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_reference_type ALTER COLUMN item_reference_type_id SET DEFAULT nextval('item_reference_type_item_reference_type_id_seq'::regclass);


--
-- TOC entry 2635 (class 2604 OID 143845045)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_render_metadata ALTER COLUMN id SET DEFAULT nextval('item_render_metadata_id_seq'::regclass);


--
-- TOC entry 2608 (class 2604 OID 143843123)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY measurement_unit ALTER COLUMN id SET DEFAULT nextval('measurement_unit_id_seq'::regclass);


--
-- TOC entry 2537 (class 2604 OID 143841768)
-- Name: null_value_type_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY null_value_type ALTER COLUMN null_value_type_id SET DEFAULT nextval('null_value_type_null_value_type_id_seq'::regclass);


--
-- TOC entry 2538 (class 2604 OID 143841785)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY openclinica_version ALTER COLUMN id SET DEFAULT nextval('openclinica_version_id_seq'::regclass);


--
-- TOC entry 2609 (class 2604 OID 143843166)
-- Name: passwd_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY password ALTER COLUMN passwd_id SET DEFAULT nextval('password_passwd_id_seq'::regclass);


--
-- TOC entry 2539 (class 2604 OID 143841807)
-- Name: resolution_status_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY resolution_status ALTER COLUMN resolution_status_id SET DEFAULT nextval('resolution_status_resolution_status_id_seq'::regclass);


--
-- TOC entry 2540 (class 2604 OID 143841815)
-- Name: response_set_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY response_set ALTER COLUMN response_set_id SET DEFAULT nextval('response_set_response_set_id_seq'::regclass);


--
-- TOC entry 2541 (class 2604 OID 143841826)
-- Name: response_type_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY response_type ALTER COLUMN response_type_id SET DEFAULT nextval('response_type_response_type_id_seq'::regclass);


--
-- TOC entry 2542 (class 2604 OID 143841840)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY rule ALTER COLUMN id SET DEFAULT nextval('rule_id_seq'::regclass);


--
-- TOC entry 2543 (class 2604 OID 143841851)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY rule_action ALTER COLUMN id SET DEFAULT nextval('rule_action_id_seq'::regclass);


--
-- TOC entry 2611 (class 2604 OID 143843337)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY rule_action_property ALTER COLUMN id SET DEFAULT nextval('rule_action_property_id_seq'::regclass);


--
-- TOC entry 2610 (class 2604 OID 143843329)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY rule_action_run ALTER COLUMN id SET DEFAULT nextval('rule_action_run_id_seq'::regclass);


--
-- TOC entry 2612 (class 2604 OID 143843348)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY rule_action_run_log ALTER COLUMN id SET DEFAULT nextval('rule_action_run_log_id_seq'::regclass);


--
-- TOC entry 2544 (class 2604 OID 143841862)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY rule_expression ALTER COLUMN id SET DEFAULT nextval('rule_expression_id_seq'::regclass);


--
-- TOC entry 2545 (class 2604 OID 143841873)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY rule_set ALTER COLUMN id SET DEFAULT nextval('rule_set_id_seq'::regclass);


--
-- TOC entry 2546 (class 2604 OID 143841881)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY rule_set_audit ALTER COLUMN id SET DEFAULT nextval('rule_set_audit_id_seq'::regclass);


--
-- TOC entry 2547 (class 2604 OID 143841889)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY rule_set_rule ALTER COLUMN id SET DEFAULT nextval('rule_set_rule_id_seq'::regclass);


--
-- TOC entry 2548 (class 2604 OID 143841897)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY rule_set_rule_audit ALTER COLUMN id SET DEFAULT nextval('rule_set_rule_audit_id_seq'::regclass);


--
-- TOC entry 2619 (class 2604 OID 143843500)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY scd_item_metadata ALTER COLUMN id SET DEFAULT nextval('scd_item_metadata_id_seq'::regclass);


--
-- TOC entry 2549 (class 2604 OID 143841905)
-- Name: section_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY section ALTER COLUMN section_id SET DEFAULT nextval('section_section_id_seq'::regclass);


--
-- TOC entry 2550 (class 2604 OID 143841916)
-- Name: status_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY status ALTER COLUMN status_id SET DEFAULT nextval('status_status_id_seq'::regclass);


--
-- TOC entry 2551 (class 2604 OID 143841927)
-- Name: study_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study ALTER COLUMN study_id SET DEFAULT nextval('study_study_id_seq'::regclass);


--
-- TOC entry 2555 (class 2604 OID 143841938)
-- Name: study_event_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_event ALTER COLUMN study_event_id SET DEFAULT nextval('study_event_study_event_id_seq'::regclass);


--
-- TOC entry 2560 (class 2604 OID 143841949)
-- Name: study_event_definition_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_event_definition ALTER COLUMN study_event_definition_id SET DEFAULT nextval('study_event_definition_study_event_definition_id_seq'::regclass);


--
-- TOC entry 2563 (class 2604 OID 143841960)
-- Name: study_group_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_group ALTER COLUMN study_group_id SET DEFAULT nextval('study_group_study_group_id_seq'::regclass);


--
-- TOC entry 2564 (class 2604 OID 143841971)
-- Name: study_group_class_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_group_class ALTER COLUMN study_group_class_id SET DEFAULT nextval('study_group_class_study_group_class_id_seq'::regclass);


--
-- TOC entry 2600 (class 2604 OID 143843095)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_module_status ALTER COLUMN id SET DEFAULT nextval('study_module_status_id_seq'::regclass);


--
-- TOC entry 2567 (class 2604 OID 143841979)
-- Name: study_parameter_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_parameter ALTER COLUMN study_parameter_id SET DEFAULT nextval('study_parameter_study_parameter_id_seq'::regclass);


--
-- TOC entry 2569 (class 2604 OID 143841988)
-- Name: study_parameter_value_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_parameter_value ALTER COLUMN study_parameter_value_id SET DEFAULT nextval('study_parameter_value_study_parameter_value_id_seq'::regclass);


--
-- TOC entry 2570 (class 2604 OID 143841994)
-- Name: study_subject_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_subject ALTER COLUMN study_subject_id SET DEFAULT nextval('study_subject_study_subject_id_seq'::regclass);


--
-- TOC entry 2628 (class 2604 OID 143844031)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_subject_id ALTER COLUMN id SET DEFAULT nextval('study_subject_id_id_seq'::regclass);


--
-- TOC entry 2574 (class 2604 OID 143842002)
-- Name: study_type_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_type ALTER COLUMN study_type_id SET DEFAULT nextval('study_type_study_type_id_seq'::regclass);


--
-- TOC entry 2575 (class 2604 OID 143844242)
-- Name: study_user_role_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_user_role ALTER COLUMN study_user_role_id SET DEFAULT nextval('study_user_role_study_user_role_id_seq'::regclass);


--
-- TOC entry 2576 (class 2604 OID 143842016)
-- Name: subject_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY subject ALTER COLUMN subject_id SET DEFAULT nextval('subject_subject_id_seq'::regclass);


--
-- TOC entry 2577 (class 2604 OID 143842024)
-- Name: subject_event_status_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY subject_event_status ALTER COLUMN subject_event_status_id SET DEFAULT nextval('subject_event_status_subject_event_status_id_seq'::regclass);


--
-- TOC entry 2578 (class 2604 OID 143842035)
-- Name: subject_group_map_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY subject_group_map ALTER COLUMN subject_group_map_id SET DEFAULT nextval('subject_group_map_subject_group_map_id_seq'::regclass);


--
-- TOC entry 2466 (class 2604 OID 143841384)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY system ALTER COLUMN id SET DEFAULT nextval('system_id_seq'::regclass);


--
-- TOC entry 2462 (class 2604 OID 143841371)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY system_group ALTER COLUMN id SET DEFAULT nextval('system_group_id_seq'::regclass);


--
-- TOC entry 2624 (class 2604 OID 143843963)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY term ALTER COLUMN id SET DEFAULT nextval('term_id_seq'::regclass);


--
-- TOC entry 2627 (class 2604 OID 143844017)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY term_element ALTER COLUMN id SET DEFAULT nextval('term_element_id_seq'::regclass);


--
-- TOC entry 2620 (class 2604 OID 143843540)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY usage_statistics_data ALTER COLUMN id SET DEFAULT nextval('usage_statistics_data_id_seq'::regclass);


--
-- TOC entry 2579 (class 2604 OID 143842043)
-- Name: user_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY user_account ALTER COLUMN user_id SET DEFAULT nextval('user_account_user_id_seq'::regclass);


--
-- TOC entry 2584 (class 2604 OID 143842054)
-- Name: role_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY user_role ALTER COLUMN role_id SET DEFAULT nextval('user_role_role_id_seq'::regclass);


--
-- TOC entry 2585 (class 2604 OID 143842065)
-- Name: user_type_id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY user_type ALTER COLUMN user_type_id SET DEFAULT nextval('user_type_user_type_id_seq'::regclass);


--
-- TOC entry 2631 (class 2604 OID 143844051)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY widget ALTER COLUMN id SET DEFAULT nextval('widget_id_seq'::regclass);


--
-- TOC entry 2630 (class 2604 OID 143844043)
-- Name: id; Type: DEFAULT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY widgets_layout ALTER COLUMN id SET DEFAULT nextval('widgets_layout_id_seq'::regclass);


--
-- TOC entry 3160 (class 0 OID 143841410)
-- Dependencies: 147
-- Data for Name: archived_dataset_file; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3161 (class 0 OID 143841421)
-- Dependencies: 149
-- Data for Name: audit_event; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3162 (class 0 OID 143841430)
-- Dependencies: 150
-- Data for Name: audit_event_context; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3163 (class 0 OID 143841433)
-- Dependencies: 151
-- Data for Name: audit_event_values; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3164 (class 0 OID 143841441)
-- Dependencies: 153
-- Data for Name: audit_log_event; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3165 (class 0 OID 143841452)
-- Dependencies: 155
-- Data for Name: audit_log_event_type; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO audit_log_event_type VALUES (1, 'item_data_value_updated');
INSERT INTO audit_log_event_type VALUES (2, 'study_subject_created');
INSERT INTO audit_log_event_type VALUES (3, 'study_subject_status_changed');
INSERT INTO audit_log_event_type VALUES (4, 'study_subject_value_changed');
INSERT INTO audit_log_event_type VALUES (5, 'subject_created');
INSERT INTO audit_log_event_type VALUES (6, 'subject_status_changed');
INSERT INTO audit_log_event_type VALUES (7, 'subject_global_value_changed');
INSERT INTO audit_log_event_type VALUES (8, 'event_crf_marked_complete');
INSERT INTO audit_log_event_type VALUES (9, 'event_crf_properties_changed');
INSERT INTO audit_log_event_type VALUES (10, 'event_crf_initial_data_entry_complete');
INSERT INTO audit_log_event_type VALUES (11, 'event_crf_double_data_entry_complete');
INSERT INTO audit_log_event_type VALUES (12, 'item_data_status_changed');
INSERT INTO audit_log_event_type VALUES (13, 'item_data_deleted');
INSERT INTO audit_log_event_type VALUES (14, 'event_crf_complete_with_password');
INSERT INTO audit_log_event_type VALUES (15, 'event_crf_initial_data_entry_complete_with_password');
INSERT INTO audit_log_event_type VALUES (16, 'event_crf_double_data_entry_complete_with_password');
INSERT INTO audit_log_event_type VALUES (17, 'study_event_scheduled');
INSERT INTO audit_log_event_type VALUES (18, 'study_event_data_entry_started');
INSERT INTO audit_log_event_type VALUES (19, 'study_event_completed');
INSERT INTO audit_log_event_type VALUES (20, 'study_event_stopped');
INSERT INTO audit_log_event_type VALUES (21, 'study_event_skipped');
INSERT INTO audit_log_event_type VALUES (22, 'study_event_locked');
INSERT INTO audit_log_event_type VALUES (23, 'study_event_removed');
INSERT INTO audit_log_event_type VALUES (24, 'study_event_start_date_changed');
INSERT INTO audit_log_event_type VALUES (25, 'study_event_end_date_changed');
INSERT INTO audit_log_event_type VALUES (26, 'study_event_location_changed');
INSERT INTO audit_log_event_type VALUES (27, 'subject_site_assignment');
INSERT INTO audit_log_event_type VALUES (28, 'subject_group_assignment');
INSERT INTO audit_log_event_type VALUES (29, 'subject_group_changed');
INSERT INTO audit_log_event_type VALUES (30, 'item_data_inserted_for_repeating_row');
INSERT INTO audit_log_event_type VALUES (31, 'study_event_signed');
INSERT INTO audit_log_event_type VALUES (32, 'eventcrf_sdv_status');
INSERT INTO audit_log_event_type VALUES (50, 'study_event_source_data_verified');
INSERT INTO audit_log_event_type VALUES (51, 'study_event_deleted');
INSERT INTO audit_log_event_type VALUES (52, 'item_data_skipped');
INSERT INTO audit_log_event_type VALUES (53, 'medical_coding');
INSERT INTO audit_log_event_type VALUES (54, 'event_crf_deleted');


--
-- TOC entry 3260 (class 0 OID 143845179)
-- Dependencies: 336
-- Data for Name: audit_log_randomization; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3233 (class 0 OID 143843036)
-- Dependencies: 272
-- Data for Name: audit_user_login; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3221 (class 0 OID 143842875)
-- Dependencies: 258
-- Data for Name: authorities; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO authorities VALUES (1, 'root', 'ROLE_USER', 1);


--
-- TOC entry 3250 (class 0 OID 143843971)
-- Dependencies: 314
-- Data for Name: coded_item; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3251 (class 0 OID 143844003)
-- Dependencies: 316
-- Data for Name: coded_item_element; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3166 (class 0 OID 143841460)
-- Dependencies: 157
-- Data for Name: completion_status; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO completion_status VALUES (1, 1, 'completion status', 'place filler for completion status');


--
-- TOC entry 3234 (class 0 OID 143843076)
-- Dependencies: 274
-- Data for Name: configuration; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO configuration VALUES (1, 'user.lock.switch', 'FALSE', 'You can enable/disable the User Locking functionality by setting the value to TRUE/FALSE', 0);
INSERT INTO configuration VALUES (2, 'user.lock.allowedFailedConsecutiveLoginAttempts', '3', 'You can configure the number of allowed Failed Consecutive Login Attempts before Locking the system', 0);
INSERT INTO configuration VALUES (3, 'pwd.chars.min', '8', 'Password minimum length', 0);
INSERT INTO configuration VALUES (5, 'pwd.chars.case.lower', 'false', 'Whether the password must contain lowercase letters', 0);
INSERT INTO configuration VALUES (6, 'pwd.chars.case.upper', 'false', 'Whether the password must contain upper case letters', 0);
INSERT INTO configuration VALUES (7, 'pwd.chars.digits', 'false', 'Whether the password must contain digits', 0);
INSERT INTO configuration VALUES (8, 'pwd.chars.specials', 'false', 'Whether the password must special character', 0);
INSERT INTO configuration VALUES (9, 'pwd.expiration.days', '360', 'Number of days after a password expires', 0);
INSERT INTO configuration VALUES (10, 'pwd.change.required', '1', 'If the user is required to change their password after the first login', 0);
INSERT INTO configuration VALUES (4, 'pwd.chars.max', '0', 'Password maximum length', 0);


--
-- TOC entry 3167 (class 0 OID 143841471)
-- Dependencies: 159
-- Data for Name: crf; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3168 (class 0 OID 143841482)
-- Dependencies: 161
-- Data for Name: crf_version; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3256 (class 0 OID 143844252)
-- Dependencies: 327
-- Data for Name: crfs_masking; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3157 (class 0 OID 143841360)
-- Dependencies: 141
-- Data for Name: databasechangelog; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO databasechangelog VALUES ('2014-01-16-TICKET863-10', 'skirpichenok', 'classpath:migration/clincaptrue/2014-01-16-TICKET863.xml', '2016-06-15 15:14:36.487', 1, 'EXECUTED', '7:d33065d3cee5d43e69002a2a5e1cb365', 'createTable, insert (x18)', 'Create the system_group table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-01-16-TICKET863-11', 'skirpichenok', 'classpath:migration/clincaptrue/2014-01-16-TICKET863.xml', '2016-06-15 15:14:36.774', 2, 'EXECUTED', '7:26b98c9ba654d4a4fa3d2ebec7a5e908', 'createTable, insert (x58)', 'Create the system table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-01-16-TICKET863-12', 'skirpichenok', 'classpath:migration/clincaptrue/2014-01-16-TICKET863.xml', '2016-06-15 15:14:36.815', 3, 'EXECUTED', '7:9a5ad7186fb6faa82b232d3405f56261', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-01-16-TICKET863-14', 'skirpichenok', 'classpath:migration/clincaptrue/2014-01-16-TICKET863.xml', '2016-06-15 15:14:36.853', 4, 'EXECUTED', '7:69301f977a2c42534539b4e164191f65', 'update (x7)', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-01-16-TICKET863-15', 'skirpichenok', 'classpath:migration/clincaptrue/2014-01-16-TICKET863.xml', '2016-06-15 15:14:36.865', 5, 'EXECUTED', '7:9dc2a31b603b822409395bd30113ed7c', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-01-16-TICKET863-16', 'skirpichenok', 'classpath:migration/clincaptrue/2014-01-16-TICKET863.xml', '2016-06-15 15:14:36.88', 6, 'EXECUTED', '7:a47823725f8a87c72afb18c79d7c4976', 'update, insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-01-16-TICKET863-17', 'skirpichenok', 'classpath:migration/clincaptrue/2014-01-16-TICKET863.xml', '2016-06-15 15:14:36.891', 7, 'EXECUTED', '7:d57033274a06996b2ff6379b8a67dcd3', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('changeLogCreatePLPGSQL-0', 'skirpichenok', 'migration/2.5/changeLogCreatePLPGSQL.xml', '2016-06-15 15:14:48.028', 8, 'MARK_RAN', '7:22332e6c35a64beb913f386fc9a789b3', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-0', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.049', 9, 'EXECUTED', '7:197452898d6d9ec395bda95be69964ae', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-1', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.074', 10, 'EXECUTED', '7:3037795d91a6cbd708d1817431502ee1', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-2', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.096', 11, 'EXECUTED', '7:b6fb11af82d47b26e8cbcb7d0b38c966', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-3', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.11', 12, 'EXECUTED', '7:b8abbe40a0ed75920a68167ff2faadf7', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-4', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.129', 13, 'EXECUTED', '7:aa2b481734d27af1d9c4bb06c1c17645', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-5', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.147', 14, 'EXECUTED', '7:b9024660901a5abc91fcf3e45232d1fa', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('235684743487-5-1', 'kkrumlian', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.154', 15, 'EXECUTED', '7:d41d8cd98f00b204e9800998ecf8427e', 'Empty', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('235684743487-5-2', 'kkrumlian', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.159', 16, 'EXECUTED', '7:d41d8cd98f00b204e9800998ecf8427e', 'Empty', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-6', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.173', 17, 'EXECUTED', '7:be0f921a51b8a57322f198919b7c295f', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-7', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.189', 18, 'EXECUTED', '7:9f26ce269ecfffacb8574f4e87feea2a', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-8', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.203', 19, 'EXECUTED', '7:a07ea9bfde5adcbaa1cde83f842ba299', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-9', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.218', 20, 'EXECUTED', '7:7d30f756e908c94e2f383125c55443e8', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-10', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.236', 21, 'EXECUTED', '7:1e2f687e3853b4fef8baa671a685f99c', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('235684743487-10-1', 'kkrumlian', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.245', 22, 'MARK_RAN', '7:56c11e8c4de84510c1605d6e79315de7', 'dropTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-10-2', 'kkrumlian', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.253', 23, 'MARK_RAN', '7:a341a3f0e808e96c7e5344900c791e8a', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('235684743487-10-3', 'kkrumlian', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.259', 24, 'EXECUTED', '7:d41d8cd98f00b204e9800998ecf8427e', 'Empty', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-11', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.269', 25, 'EXECUTED', '7:e96cc0c3df9fb9ac636b0b3f86266f7a', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-12', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.28', 26, 'EXECUTED', '7:d13f783865e051490e446bacba64eee1', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-13', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.289', 27, 'EXECUTED', '7:829aeeda821f85784a0f4ff5218f8d07', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-14', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.302', 28, 'EXECUTED', '7:d07fdd0a4ba7630623b799972d27fb59', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-15', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.316', 29, 'EXECUTED', '7:b5b3215af240d56df0d0b9b89c896906', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-16', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.331', 30, 'EXECUTED', '7:c49e104286ae1ec76b4e5ba683590eeb', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-17', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.344', 31, 'EXECUTED', '7:6a6961fa48976df62fa3adfbf686fa69', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-18', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.36', 32, 'EXECUTED', '7:fcef1aa34a9376661b37e1b6deeb58f7', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-19', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.376', 33, 'EXECUTED', '7:1ac7f7cdb65163875688b950e1ddf751', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-20', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.386', 34, 'EXECUTED', '7:eb274a796313832c033efc80c9afbaa6', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-21', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.4', 35, 'EXECUTED', '7:c59306062bc505ce85bcd689f13285d9', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-22', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.416', 36, 'EXECUTED', '7:fdcb12abd0f48180986648a42503dbf7', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-23', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.429', 37, 'EXECUTED', '7:4f85a1ea7bec3c08b11c759b3a8086f1', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-24', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.439', 38, 'EXECUTED', '7:924bf7a25fdd9ba29e270694a166a99c', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-25', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.45', 39, 'EXECUTED', '7:1463e50bb049935497487f30f53f3197', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-26', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.459', 40, 'EXECUTED', '7:04017c1f1acec1639472dfe98dab9fe1', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-27', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.47', 41, 'EXECUTED', '7:f36016bba36727e47d09f43ae7f487ab', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-28', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.48', 42, 'EXECUTED', '7:9a1764b1631997425e1d10786dab5bed', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-29', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.498', 43, 'EXECUTED', '7:641ea4d91286cd1e91105255a8aa0e98', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-30', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.512', 44, 'EXECUTED', '7:4b7e0ba823805638dbb4dd79f9de4ca8', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-31', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.544', 45, 'EXECUTED', '7:10cbf32028b7deec297a1ed6404752e2', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-32', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.559', 46, 'EXECUTED', '7:e326c53c4e23df95b89f3e8e241feb64', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('235684743487-32-1', 'kkrumlian', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.566', 47, 'MARK_RAN', '7:e4a7d9f3939710798926728e4eb9a803', 'dropTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-32-2', 'kkrumlian', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.573', 48, 'MARK_RAN', '7:9b5b85f057a6cbcddea5e79df1499d04', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('235684743487-32-3', 'kkrumlian', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.577', 49, 'EXECUTED', '7:d41d8cd98f00b204e9800998ecf8427e', 'Empty', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-33', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.589', 50, 'EXECUTED', '7:907f05360ba7547cb63e4e64f9af57be', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-34', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.602', 51, 'EXECUTED', '7:ecdc248c11da477b8c15dae57ba57bf2', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-35', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.618', 52, 'EXECUTED', '7:799352df0b39c9ceb0a4b7c1ac9188c6', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-36', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.633', 53, 'EXECUTED', '7:ceb86f43de612e272de72114c16d81cc', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-37', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.654', 54, 'EXECUTED', '7:6248df41d6e3955adcc8c9df5071f5ef', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-38', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.672', 55, 'EXECUTED', '7:1d95a6df2be29e69329eac0ce1a587f6', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-39', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.69', 56, 'EXECUTED', '7:75505d5cb683edf5c3d17b6187868dcd', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-40', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.71', 57, 'EXECUTED', '7:2f72fc74092193b0d327a1188ccbd11a', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-41', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.728', 58, 'EXECUTED', '7:e672750ec359a42c34e3de78a3c63f6f', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-42', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.746', 59, 'EXECUTED', '7:d99dc5405cc26043b10174833c6a7256', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-43', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.758', 60, 'EXECUTED', '7:091bc2d42f26a0932df7763ab553efe6', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-43-1', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.768', 61, 'EXECUTED', '7:de1c8951c7741cd9ba9276fac5dc18e4', 'dropTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-43-2', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.783', 62, 'EXECUTED', '7:cc811f187d79aa5c6eccaec60def0b01', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-44', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.797', 63, 'EXECUTED', '7:c5ee39e97706a75ec20c8d24690d669b', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-45', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.811', 64, 'EXECUTED', '7:2acfc1f619fbfe7e2d717326787bf587', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-46', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.826', 65, 'EXECUTED', '7:c85a6239036b4ae9c49b4e1673e49ace', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-47', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.842', 66, 'EXECUTED', '7:4ed428970d95ab2e67e6e62ee16cf9e4', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-48', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.852', 67, 'EXECUTED', '7:3e12078f3d910c490dab0e6a1ddf5b23', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-49', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.868', 68, 'EXECUTED', '7:5f179fdd04f88c18339844bb2847a880', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-50', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.885', 69, 'EXECUTED', '7:3e40422aa37e6f29c49e613abc8ec283', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-51', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.901', 70, 'EXECUTED', '7:6ec794e0e4baef6af0b88531c99f8583', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-52', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.915', 71, 'EXECUTED', '7:86ca5b40030b529a499b7f55293b41e0', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-53', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.928', 72, 'EXECUTED', '7:32015ac49a407773bdfc92a83982315c', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-54', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.944', 73, 'EXECUTED', '7:08011cc196f0f422d10d1330594783fd', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-55', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.956', 74, 'EXECUTED', '7:555e45a95c9b3d253cca171cd4606fb2', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-56', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.974', 75, 'EXECUTED', '7:e3c8679a36f9c1ba62b1c105306324a2', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-57', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:48.99', 76, 'EXECUTED', '7:6d6f7348232114e845ade6d659730d18', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-58', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.015', 77, 'EXECUTED', '7:c1fe63883bbe2c4b2551c16131b0ca99', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-59', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.034', 78, 'EXECUTED', '7:4c070eb8ad10aedac13fa6c772f11c49', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-60', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.05', 79, 'EXECUTED', '7:f46939b58f31894ce8fee7abec80fb36', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-61', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.065', 80, 'EXECUTED', '7:f82c52e847103a713b4f9763fbeb410c', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-62', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.078', 81, 'EXECUTED', '7:4125bd7f8a7a761da009c1f279cb714c', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-63', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.092', 82, 'EXECUTED', '7:b7fe5aa42a3a64efcc676b8fd055f641', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-64', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.104', 83, 'EXECUTED', '7:5ff671a38885dc05e5361b6c3ff47142', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-65', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.119', 84, 'EXECUTED', '7:fe3fafd697b82b3986167ee1632028d4', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-66', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.133', 85, 'EXECUTED', '7:c1589988329b373aed3e4f3e9545e72c', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-67', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.143', 86, 'EXECUTED', '7:87e379b93154e5db075410c916139de7', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-68', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.159', 87, 'EXECUTED', '7:ab3ce100a70ce09cecb7b107351ba585', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-69', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.173', 88, 'EXECUTED', '7:230edfbcac664077723e0ae2a63f90b0', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-70', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.188', 89, 'EXECUTED', '7:b61f9a4fa1eef7879ff1d601371b8d13', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-71', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.204', 90, 'EXECUTED', '7:0700ac421559887aeb531568ea0495df', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-72', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.219', 91, 'EXECUTED', '7:8acad1418c2bf87e0034307415f9057b', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-73', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.231', 92, 'EXECUTED', '7:8ac39eb0edc26b48c3469f6646258b4d', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-74', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.241', 93, 'EXECUTED', '7:ae00152b00ca6cbea276e64387136370', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-75', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.289', 94, 'EXECUTED', '7:573aaec3558be9c0364da5e3b994780e', 'addUniqueConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-76', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.3', 95, 'EXECUTED', '7:0c0e6150312cd1f51fc7677459ff010f', 'addUniqueConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-77', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.311', 96, 'EXECUTED', '7:6bb32b337cb2a077408610be1c29f30a', 'addUniqueConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-78', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.321', 97, 'EXECUTED', '7:fc7fa678f2b659209f29c5f5d5303e8a', 'addUniqueConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-79', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.332', 98, 'EXECUTED', '7:d4d296a7ce3933711f450ec65e8c529b', 'addUniqueConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-80', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.344', 99, 'EXECUTED', '7:e653a84e66c51f02b2ff4417912fb3f0', 'addUniqueConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-81', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.354', 100, 'EXECUTED', '7:23ab678f5a2c78276349ea8588859202', 'addUniqueConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-82', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.364', 101, 'EXECUTED', '7:1fcb5a0f069db36ee198e5903ea2ef2f', 'addUniqueConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-83', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.378', 102, 'EXECUTED', '7:74e62256f3e4535b637b2ee7df68109e', 'createIndex', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-84', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.389', 103, 'EXECUTED', '7:40da8002c3cd1f85a85c57dffb77ddc5', 'createIndex', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-85', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.401', 104, 'EXECUTED', '7:c4e290e8512824452f45889fcddbc19a', 'createIndex', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-86', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.411', 105, 'EXECUTED', '7:d30c79c05995ea552028ef551e86aec6', 'createIndex', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-87', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.421', 106, 'EXECUTED', '7:92bc6e8d9c4d6ab558a3187bc4e2b2ea', 'createIndex', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-88', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.431', 107, 'EXECUTED', '7:e102cefb18ff5a5790bce78ae204ab22', 'createIndex', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-89', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.443', 108, 'EXECUTED', '7:78bbf8b2036b05edabd2bc277f456bd5', 'createIndex', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-90', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.453', 109, 'EXECUTED', '7:1b2a1f6f2fb2de47353b3a12fdca6d13', 'createIndex', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-91', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.466', 110, 'EXECUTED', '7:b293e4cf614d7c5b03fed5959f7b85bd', 'createIndex', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-92', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.476', 111, 'EXECUTED', '7:950494348df53829c2c8243ad95c4677', 'createIndex', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-93', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.49', 112, 'EXECUTED', '7:c3582e86455260c30d7a2e955e6b2915', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-94', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.5', 113, 'EXECUTED', '7:6e3a95a68fbd5e4667ee597b27f4e6f8', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-95', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.51', 114, 'EXECUTED', '7:9aaa4a571ca0ce4566e3b79d38d53e3a', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-96', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.52', 115, 'EXECUTED', '7:6f928a2b1f35c76ce17a15e849573c92', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-97', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.531', 116, 'EXECUTED', '7:fd54450b58001caa8d4ae0f0b1f61eed', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-98', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.542', 117, 'EXECUTED', '7:69b17f986cb8a7ed1dbe6e6b1f04fe41', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-99', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.552', 118, 'EXECUTED', '7:66833f5f3279dc3a1f64e68fc0349632', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-100', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.564', 119, 'EXECUTED', '7:7a0d1b6a1c1bf9fc13117afeafa50f13', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-101', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.576', 120, 'EXECUTED', '7:a245d65918e28fa5b6b2cff930fdcc1b', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-102', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.588', 121, 'EXECUTED', '7:1d5e59d13ae1ab0ed97548f4a05a8d84', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-103', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.598', 122, 'EXECUTED', '7:40b4e283312d20af521e56370add4413', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-104', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.608', 123, 'EXECUTED', '7:1538f4ae3fff598d50cdfc219491b0fd', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-105', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.619', 124, 'EXECUTED', '7:1d8ec68cb5419f82f3517a934d895a8d', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-106', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.63', 125, 'EXECUTED', '7:a0f826eac1b71618616ca8fa9418cd66', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-107', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.64', 126, 'EXECUTED', '7:9373959a391abb236df56626981d0fb0', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-108', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.652', 127, 'EXECUTED', '7:693bb97c500f6f64b88014a5038f6892', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-109', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.662', 128, 'EXECUTED', '7:7febeaee07356e9bedaed4fb6ad314c0', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-110', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.673', 129, 'EXECUTED', '7:c45511279d353ead8ef8c5cc0d2b9e0d', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-111', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.684', 130, 'EXECUTED', '7:4acd42dec300e22ab3bd9fe942dcd1b4', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-112', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.694', 131, 'EXECUTED', '7:1e16afa76b0bf650e9377c38bf31a5d7', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-113', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.705', 132, 'EXECUTED', '7:b548a7358808f038ebe579a648d56bf4', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-114', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.717', 133, 'EXECUTED', '7:b7044ef743d5ca5e5fca0ad5f378cbe1', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-115', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.728', 134, 'EXECUTED', '7:5d55c9513eab559e23d57165d4893643', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-116', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.738', 135, 'EXECUTED', '7:297b7302853950ffab0514426599b353', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-117', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.749', 136, 'EXECUTED', '7:b755f566a6e2010e45d08706c18dc01f', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-118', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.76', 137, 'EXECUTED', '7:9e0d3575479788fffb69b23848155085', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-119', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.771', 138, 'EXECUTED', '7:9cdc23bfce4423d899dc7b8f5bf73a6e', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-120', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.781', 139, 'EXECUTED', '7:54a051d3d7be5b19cce03b1250ac2381', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-121', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.792', 140, 'EXECUTED', '7:8ddba9b62ee3de14ef9d894ac36f007b', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-122', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.803', 141, 'EXECUTED', '7:9cd3990b882bd76265354dfd79e7c3b7', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-123', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.817', 142, 'EXECUTED', '7:6837aaee66295c3acff241459350f327', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-124', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.83', 143, 'EXECUTED', '7:5e52c6bfe76d6a0d689360582d0a67cb', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-125', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.841', 144, 'EXECUTED', '7:3b1b6b0c794654a8cd922507cd8a43d6', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-126', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.852', 145, 'EXECUTED', '7:9517edb178f1df9f53abde18e74ac424', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-127', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.862', 146, 'EXECUTED', '7:e21b4566912f971f3e7bd10bcc5495d9', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-128', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.873', 147, 'EXECUTED', '7:7e837b11abc245a1ef3c2c0a916a67d8', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-129', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.884', 148, 'EXECUTED', '7:a7679c6cb7f7c2d939bd72c3d39faf44', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-130', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.894', 149, 'EXECUTED', '7:3e230dfe7259d381eb8c0401aa4e81a2', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-131', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.906', 150, 'EXECUTED', '7:e88680cd4e2f3b2d2e2b899d67162b60', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-132', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.917', 151, 'EXECUTED', '7:55b3eaadacc18b0815f33e588f2f19e7', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-133', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.929', 152, 'EXECUTED', '7:db09759df93ffdad1a1a5b2a5b64c270', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-134', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.939', 153, 'EXECUTED', '7:d073fc2ac30d16540aac79d8b41cee29', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-135', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.95', 154, 'EXECUTED', '7:fc802bc9142faee29fc4a74e4a7322b3', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-136', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.961', 155, 'EXECUTED', '7:6a125d9ae4ec6279fa40b8ee8cccf261', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-137', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.971', 156, 'EXECUTED', '7:b5c462314801ca958c76d35b8714bf9a', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-138', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.981', 157, 'EXECUTED', '7:a5e67f2feb143ac756365b4de015c67a', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-139', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:49.995', 158, 'EXECUTED', '7:7fee67a22b93ff6b6188a94bba8f276b', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-140', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.007', 159, 'EXECUTED', '7:f8575d5b9e5a23da44520fad06947d6e', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-141', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.018', 160, 'EXECUTED', '7:311902d53530ec53b5ab7cc8efc3bc4e', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-142', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.035', 161, 'EXECUTED', '7:c411969241150118bd3fde2ce38b83a6', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-143', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.046', 162, 'EXECUTED', '7:7c7bd21f62da1f2be865abb1296bd223', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-144', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.057', 163, 'EXECUTED', '7:95f1206e32863b479146e56bb4238e79', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-145', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.067', 164, 'EXECUTED', '7:524c94bde4b5d87dce081c3062523d29', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-146', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.077', 165, 'EXECUTED', '7:caeb14964af811b1ceac4e4cd02d06a6', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-147', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.09', 166, 'EXECUTED', '7:79f956aba93ddf5e62d079663680c082', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-148', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.101', 167, 'EXECUTED', '7:c166aa2d05f6ba6376d8aeaebc988ab6', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-149', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.111', 168, 'EXECUTED', '7:c403de410d7e4146042b1f44094fe35b', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-150', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.122', 169, 'EXECUTED', '7:840614f9c492c0502099e5897cf6d160', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-151', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.133', 170, 'EXECUTED', '7:37007a932eee19287d5dfd28bcfff02b', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-152', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.144', 171, 'EXECUTED', '7:e44f81ca862ebd56a25f03a980774e3e', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-153', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.155', 172, 'EXECUTED', '7:972c7131deaab61b1b5eebf10aa46ceb', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-154', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.165', 173, 'EXECUTED', '7:2ae24c4ed34aa34b8b121366b9f1522a', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-155', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.175', 174, 'EXECUTED', '7:1905098654e6ec20ebf4a8b61f5c9a8e', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-156', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.19', 175, 'EXECUTED', '7:c93bc631db56229f5a1836fe1bd826c3', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-157', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.2', 176, 'EXECUTED', '7:8d647e7f7451207ac31fbac27da0486d', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-158', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.211', 177, 'EXECUTED', '7:9b8e56fef24295c7add781aa417bd3ff', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-159', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.221', 178, 'EXECUTED', '7:2e9cc2dc6ce8b567a06c53dc2a853124', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-160', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.233', 179, 'EXECUTED', '7:793295ee8e7b7d329e87c08949a0ca32', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-161', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.244', 180, 'EXECUTED', '7:b9d26319fb27501075e0db9f207cf207', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-162', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.255', 181, 'EXECUTED', '7:2f9fd4d1279aca4c8f089d9d67a3db12', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-163', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.266', 182, 'EXECUTED', '7:039c04a7dff065f931d3a7b46845e7be', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-164', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.278', 183, 'EXECUTED', '7:a60b63ed9d393bd8bb40026dfc915818', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-165', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.289', 184, 'EXECUTED', '7:e59765247589d0241229f01c3bf21678', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-166', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.301', 185, 'EXECUTED', '7:3bffa4c33a7797aa262aac72a13ef391', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-167', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.312', 186, 'EXECUTED', '7:899546eeef89a8e69b4fc579fb46f85f', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-168', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.323', 187, 'EXECUTED', '7:ae2b9975004ba1b693a199b6c8d16903', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-169', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.334', 188, 'EXECUTED', '7:e9d61a806274bfa843b138818ae5cac5', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-170', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.346', 189, 'EXECUTED', '7:5e3428a0eaf7457d6ed1fe1d36ae6f34', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-171', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.357', 190, 'EXECUTED', '7:96cea52685a35fe4c5718dbe8c342f78', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-172', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.369', 191, 'EXECUTED', '7:ba64e332a59edafb4f0f271dde37ea4b', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-173', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.38', 192, 'EXECUTED', '7:fac5778f268bbc1f313b37c7e5451954', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-174', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.39', 193, 'EXECUTED', '7:8ffd6e170282973aa940ccb0eb4ba7a6', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-175', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.401', 194, 'EXECUTED', '7:c6fb940fdc3c70a8cd455763c81858ea', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-176', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.411', 195, 'EXECUTED', '7:b98931969b7b675bf6b0e042a288e39a', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-177', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.423', 196, 'EXECUTED', '7:03d61720d57cac30e3305d05dd0758e0', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-178', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.434', 197, 'EXECUTED', '7:c8e6059b3462bf417bdbebebd50da0a4', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-179', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.444', 198, 'EXECUTED', '7:e204eae2e54067a72f0b0690d8b4b4d6', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-180', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.455', 199, 'EXECUTED', '7:6475a3c524aa5d5c753b2f4578facbf4', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-181', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.466', 200, 'EXECUTED', '7:e1760c23b78c0b4308fe6bbe674ca197', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-182', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.476', 201, 'EXECUTED', '7:1af2b6a696a703143bf27ab7d8f4c2cb', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-183', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.486', 202, 'EXECUTED', '7:0b926029c878c9b89c61e95b1741e782', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-184', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.497', 203, 'EXECUTED', '7:6a92b0b543a9972e3c938a25030374e7', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-185', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.509', 204, 'EXECUTED', '7:6a27490e32deaded24205b2ea7c2bace', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-186', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.52', 205, 'EXECUTED', '7:dfbec3686c4b41c5ddb3fd04008aefcd', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-187', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.53', 206, 'EXECUTED', '7:2dc8cc8031838b3f30718fb995145d66', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-188', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.541', 207, 'EXECUTED', '7:9a77b4da204f0c195fcf158084be6781', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-189', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.551', 208, 'EXECUTED', '7:ecb61f4d132e3726ee078234f49729ba', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-190', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.563', 209, 'EXECUTED', '7:25a98aad41104d842181dab54a08d76c', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-191', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.573', 210, 'EXECUTED', '7:83ff5ac1e6fc5951aea12beeccac9a7b', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-192', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.584', 211, 'EXECUTED', '7:8641501add7ef623038ff0051be0b862', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-193', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.594', 212, 'EXECUTED', '7:ff65781c8f5fd92c03eba825c1ec189e', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-194', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.605', 213, 'EXECUTED', '7:198f46ca31ef5bc4180dca854f12400d', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-195', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.618', 214, 'EXECUTED', '7:138b94291ec4f2edf21e836766ed4822', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-196', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.628', 215, 'EXECUTED', '7:b31eaaa2b505fbcbb78d53cbf2eac826', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-197', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.638', 216, 'EXECUTED', '7:52a7a8ffd75bbd3e899b1c6b70ea1eb1', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-198', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.651', 217, 'EXECUTED', '7:cf7ec2d65223ff4931a91e228af953e1', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-199', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.66', 218, 'EXECUTED', '7:ab729df144b4c41269775446afa65329', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-200', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.671', 219, 'EXECUTED', '7:080e534f404d53b82c19f7169c88cc65', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-201', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.683', 220, 'EXECUTED', '7:1352262ad5e854ec4fee768955b44e6a', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-202', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.694', 221, 'EXECUTED', '7:9feac2bd6202ff01912dfd7986c8764b', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-203', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.704', 222, 'EXECUTED', '7:b10ba4f2ca209b1773a31099ddebf009', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-204', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.716', 223, 'EXECUTED', '7:3429fee2fe448a9c7adfe4588f96e4c8', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-205', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.726', 224, 'EXECUTED', '7:23fa8005a09116718b9600d8a238868c', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-206', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.736', 225, 'EXECUTED', '7:ced66e3411c8e0d6e035f8e608d6055b', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-207', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.749', 226, 'EXECUTED', '7:8260df0ebe842a66dc25095bd0918b12', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-208', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.759', 227, 'EXECUTED', '7:cc2328088bc2a5fcf16fe93c575b869f', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-209', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.769', 228, 'EXECUTED', '7:6739866a5415c326e484f60d6520d5e7', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-210', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.78', 229, 'EXECUTED', '7:5eec38f6e19b2a796ff2393547c1f5c5', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-211', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.791', 230, 'EXECUTED', '7:f0171596987dcea17b2ed981e859ddee', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-212', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.802', 231, 'EXECUTED', '7:99d96a53f84c3158fdae48a40e45e4d7', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-213', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.813', 232, 'EXECUTED', '7:725d16a947e31737c28b499a102bdcc3', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-214', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.823', 233, 'EXECUTED', '7:53e13ff5aa95d75375f718ac4447a009', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-215', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.834', 234, 'EXECUTED', '7:b277402f93ff92a6a42f326e63eb77ae', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236014212860-216', 'pgawade (generated)', 'migration/2.5/changeLogCreateTables.xml', '2016-06-15 15:14:50.845', 235, 'EXECUTED', '7:f9a6bfc4c41ea2d59ed71659c878c389', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-358', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:50.857', 236, 'EXECUTED', '7:983291679cfd836bc0ab934429825a83', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-359', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:50.87', 237, 'EXECUTED', '7:ca403bb0e1dc00ea8b69903ca63886c8', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-360', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:50.889', 238, 'EXECUTED', '7:2d0ccaba3f758cc238f9773834c12762', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-361', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:50.9', 239, 'EXECUTED', '7:a412bf84fd25f69435d397cf7763a7b6', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-362', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:50.911', 240, 'EXECUTED', '7:19ef8efc7793c925f939c8d133dd962c', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-363', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:50.924', 241, 'EXECUTED', '7:ef0cb36fc93c03891c140e12bc991f0d', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-364', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:50.936', 242, 'EXECUTED', '7:f6f9447d2858ece3c7604987aaa2f2c7', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-365', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:50.946', 243, 'EXECUTED', '7:5b2a341ddd8592a57453ac8a2c9ca02c', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-366', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:50.957', 244, 'EXECUTED', '7:82f99c9058285e52d6a41a88369dabdd', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-367', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:50.967', 245, 'EXECUTED', '7:41ec8ebc6a98fd74e5e083311802e7b8', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-368', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:50.978', 246, 'EXECUTED', '7:40aaabe831a8751563f39b1120181d7a', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-369', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:50.99', 247, 'EXECUTED', '7:8334cef16c65a8f21cfda7cd447ff230', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-370', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.001', 248, 'EXECUTED', '7:4bbd39e280d0e006ea67a58f1084166b', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-371', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.012', 249, 'EXECUTED', '7:08f20a50d6d0bf5b53ac24a719c2f649', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-93', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.02', 250, 'MARK_RAN', '7:d9f6520e962b91f28125770615bfc602', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-94', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.03', 251, 'MARK_RAN', '7:3e0b92b8d1c8ded2c71f825b94aa8124', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-95', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.038', 252, 'MARK_RAN', '7:26ba55c1f8f6069ccfdbd3a6d4ce91d7', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-96', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.046', 253, 'MARK_RAN', '7:57c5bd8c318637b11ffb0e7f21f0536b', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-97', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.055', 254, 'MARK_RAN', '7:d36d515457a5971984cd222c4614ba38', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-155', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.063', 255, 'MARK_RAN', '7:cd56a7f9d43f20f4c57aea1e333ea7c1', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-98', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.071', 256, 'MARK_RAN', '7:bffbcf503d08537bbc56065d8182b3b5', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-99', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.079', 257, 'MARK_RAN', '7:dbcb5efd091c4f88fc130afeb4ef8956', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-100', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.088', 258, 'MARK_RAN', '7:4eb6de8bc4d000067f20f4b89ef145d2', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-101', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.095', 259, 'MARK_RAN', '7:0d6374b9b3af35d4551020609d890074', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-102', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.103', 260, 'MARK_RAN', '7:e75ba85b8ed991cf52ea75bf2f97da6d', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-103', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.11', 261, 'MARK_RAN', '7:75645e1b65e72371290b18d6febde1c5', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-104', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.119', 262, 'MARK_RAN', '7:12a50a58282c0be3124f40ddf9d50818', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-105', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.127', 263, 'MARK_RAN', '7:c5a1a09aee33fac2758235a57630e352', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-106', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.135', 264, 'MARK_RAN', '7:632c441ea153b35b94dfa67f852a46b3', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-107', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.143', 265, 'MARK_RAN', '7:84283a2c0a0cebad13ee231ad37521d3', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-108', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.152', 266, 'MARK_RAN', '7:5d9d5b3150696a4e091bc866833f9a13', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-109', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.16', 267, 'MARK_RAN', '7:fdfad4a76f299586504558f2a94f77f5', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-110', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.169', 268, 'MARK_RAN', '7:6f3cb41b246eefc701bb8b7042522ace', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-111', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.176', 269, 'MARK_RAN', '7:87751612d32b23abe670fad5af786c07', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-112', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.187', 270, 'MARK_RAN', '7:ae4b59853d00336c98dfee13bfa87615', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-113', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.195', 271, 'MARK_RAN', '7:24db55d60fc3fc41bbd9e3e6250f5710', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-380', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.202', 272, 'MARK_RAN', '7:23daa03436ed6db2c4b2a1d79b8bf7c0', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-114', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.213', 273, 'MARK_RAN', '7:4a6ed0408ef5a6f5a5d899c86f3a3b2c', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-115', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.221', 274, 'MARK_RAN', '7:af22f9ef186d77401f2b442f1a78d996', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-116', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.229', 275, 'MARK_RAN', '7:08609ac54aa845e35e80cf90a4ff0558', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-117', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.236', 276, 'MARK_RAN', '7:24a7f10db33ff5612b52f179b6d719eb', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-118', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.245', 277, 'MARK_RAN', '7:909d5a2c2b8cf871a2c8372322d106f1', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-119', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.254', 278, 'MARK_RAN', '7:2999f3dca7ddf2e3a5bdeec8d1fbea0d', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-120', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.265', 279, 'MARK_RAN', '7:7999fd9491fcb7b1355c343037650089', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-388', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.273', 280, 'MARK_RAN', '7:ef4cbcd1220bd395e44a24f68dd1f686', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-121', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.282', 281, 'MARK_RAN', '7:5abcb9184c60a4eb98aa05100f7ab58e', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-122', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.29', 282, 'MARK_RAN', '7:0ba1e9992d04bd842e3f515c53279ba5', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-123', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.298', 283, 'MARK_RAN', '7:2e2da13e31dbb686f4c1a60d8cb32daf', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-124', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.308', 284, 'MARK_RAN', '7:455c42d910668a85137a9c928bd652cd', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-125', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.317', 285, 'MARK_RAN', '7:bd7df10a344ef5cc43ab6900cb5b4497', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-126', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.324', 286, 'MARK_RAN', '7:98460b1005f1958a65e1087860ab8cc3', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-127', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.333', 287, 'MARK_RAN', '7:0d91e8917090d322079dd23f19caa804', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-128', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.341', 288, 'MARK_RAN', '7:416b3312c73308a6bc42a7c8e3e6d189', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-129', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.349', 289, 'MARK_RAN', '7:107c88a21a4bc0c70b417c2acdf4e858', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-130', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.356', 290, 'MARK_RAN', '7:a58e0d6b14979683d54b405dc95d0f70', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-131', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.362', 291, 'MARK_RAN', '7:81172701dc5b680cff52129b0a167e17', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-132', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.369', 292, 'MARK_RAN', '7:f1514146d68408b0d8a10f9dc5e26121', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-133', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.376', 293, 'MARK_RAN', '7:c65bec451270e1ca867053b31e8427c8', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-134', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.383', 294, 'MARK_RAN', '7:f8ba63a13f76f4ab0a63cb9c8b60c70c', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-135', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.389', 295, 'MARK_RAN', '7:9f846e6bea9ec9102e6781f90f0c961b', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-136', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.396', 296, 'MARK_RAN', '7:6dac51d0be02ac6f6423a1eb87aa10ec', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-137', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.404', 297, 'MARK_RAN', '7:e3e9253554250d79c145c3502fd897ce', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-138', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.41', 298, 'MARK_RAN', '7:5620c338e334b0ebbdc1fb7f57df1717', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-139', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.417', 299, 'MARK_RAN', '7:8e8d42370018c89d59c1ff25e1e6ecf5', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-140', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.425', 300, 'MARK_RAN', '7:ad6ad489f0f2083ca975acc421051f48', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-141', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.431', 301, 'MARK_RAN', '7:22df3f2b366b6cc19ced6379e59e139e', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-142', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.438', 302, 'MARK_RAN', '7:7afb3c778e06655d92505708040d9416', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-143', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.445', 303, 'MARK_RAN', '7:63dc68777b1a7b292c4e49856fc4ed47', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-144', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.452', 304, 'MARK_RAN', '7:0ac51d3348c75920612ebed58feb4306', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-145', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.459', 305, 'MARK_RAN', '7:be31ca6bac55043d55ae8c5954f82c9e', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-146', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.466', 306, 'MARK_RAN', '7:5fd1fde497464621675271989ca3b0ff', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-147', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.472', 307, 'MARK_RAN', '7:8f1419a230c70dfe05102b5b78278493', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-148', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.479', 308, 'MARK_RAN', '7:7da5bbac8ba04f2aff2060a9f9006552', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-149', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.485', 309, 'MARK_RAN', '7:b1fb5f6e6e31130a5639035db4be89d9', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-150', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.493', 310, 'MARK_RAN', '7:da4812b2b85234b038c667c65eea87b3', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-151', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.5', 311, 'MARK_RAN', '7:9ba8c194a89d219c44ca8875b4e9ee30', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-152', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.507', 312, 'MARK_RAN', '7:e08d3ce9296ea064f0f16b86d2b5dcf3', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-373', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.513', 313, 'MARK_RAN', '7:03d7dfed89e7e7116d08dbc4dceae239', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-374', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.521', 314, 'MARK_RAN', '7:e7f0b3d81edfacb9f5478fe1c914bfb5', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-291', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.528', 315, 'MARK_RAN', '7:46676e2a34179b6ecf67f41ef993bd1a', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-292', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.536', 316, 'MARK_RAN', '7:6261b4c4a3b98513f9d009e5d95c93aa', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-293', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.543', 317, 'MARK_RAN', '7:7813251a12175765b57999a465d2f33c', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-294', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.551', 318, 'MARK_RAN', '7:6e80d703d28eedcc25e8573caf5863ed', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-295', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.558', 319, 'MARK_RAN', '7:23f482893c7a5837894b905da2678261', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-296', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.564', 320, 'MARK_RAN', '7:a8278576a43cdd4efb22a2dba4a07ee9', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-297', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.571', 321, 'MARK_RAN', '7:d3f8bfa33a764e0e6103657f2ff505c0', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-298', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.578', 322, 'MARK_RAN', '7:5cd2b15646eaba4b4f2eb25d3d442c8d', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-299', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.588', 323, 'MARK_RAN', '7:95c3218c590b68587120d2fdc59db4c2', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-300', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.595', 324, 'MARK_RAN', '7:56ca935261033afb10135a81fe8341e2', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-301', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.601', 325, 'MARK_RAN', '7:cbfaddbbe878b7d858ba39e5b4e5a6ad', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-302', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.608', 326, 'MARK_RAN', '7:721227fb3abdf018a7861909caf7dfd5', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-303', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.615', 327, 'MARK_RAN', '7:5292f7842015bf3475b74220931e2fa6', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-304', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.622', 328, 'MARK_RAN', '7:c552d1a968d1af2b761bc89d60c34463', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-305', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.629', 329, 'MARK_RAN', '7:830059451ecb3d2fa6a801bb2f3cce2a', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-306', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.637', 330, 'MARK_RAN', '7:4d4596fedd042344641a2a4c5a118eb9', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-307', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.644', 331, 'MARK_RAN', '7:d48a543918167ea8792daf380f2ef136', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-308', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.652', 332, 'MARK_RAN', '7:7a1566d89fe803c6f71568055755fd5e', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-309', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.659', 333, 'MARK_RAN', '7:d6238a2bcbeb876efddc14692e6d3ecb', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-310', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.665', 334, 'MARK_RAN', '7:134f5d924d2003a6e6cd8f487dab0601', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-311', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.672', 335, 'MARK_RAN', '7:dc48ee7ac0fa4ea9270b6f448856319c', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-312', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.679', 336, 'MARK_RAN', '7:b55a9b04b544382837ebcd6aedec586a', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-313', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.687', 337, 'MARK_RAN', '7:ffc3b4c8a0426ed6b1ad12a874b73298', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-314', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.694', 338, 'MARK_RAN', '7:bdda3cbb52100c229930d7945c514e8d', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-315', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.7', 339, 'MARK_RAN', '7:fda65d50518d123b8cbe10d2df05b331', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-316', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.708', 340, 'MARK_RAN', '7:c3329bb312c4b0214460dc8398fdf529', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-317', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.715', 341, 'MARK_RAN', '7:110840f7f42e01a058d2cc4a991cd0a2', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-318', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.722', 342, 'MARK_RAN', '7:3e527856b54f5b64b3052a0c0ddc22db', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-319', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.729', 343, 'MARK_RAN', '7:3675f39559d3768056de293ce9e0f977', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-320', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.737', 344, 'MARK_RAN', '7:1dd9d472a0c782f628340987d023cbb6', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-321', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.744', 345, 'MARK_RAN', '7:40ea7c59a138c049c320c81e8b27b10a', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-322', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.751', 346, 'MARK_RAN', '7:6414d1bb5e978fc95961b193800de177', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-323', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.758', 347, 'MARK_RAN', '7:871b641f95410f9e32197051cb04c848', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-324', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.765', 348, 'MARK_RAN', '7:1a289883981cf511bd8b8adfdf984d3b', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-325', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.772', 349, 'MARK_RAN', '7:48d7937dd3531819ff6148ca48588f36', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-326', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.779', 350, 'MARK_RAN', '7:2e8492e3da84c79de11e173f99f38688', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-327', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.786', 351, 'MARK_RAN', '7:d6dd09f2495e66f1298acd88a6bb088f', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-328', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.793', 352, 'MARK_RAN', '7:9b93be205338c6073294e3f3928c36fc', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-329', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.799', 353, 'MARK_RAN', '7:5749a84ff1f4821914f645f8e6b95669', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-330', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.807', 354, 'MARK_RAN', '7:e486bf5dd37840edcf7a396e049c55e1', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-331', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.814', 355, 'MARK_RAN', '7:e685c9f3f4648073c46c53c30925251b', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-332', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.821', 356, 'MARK_RAN', '7:79c98b073c41069175abbb3452cc410a', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-333', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.829', 357, 'MARK_RAN', '7:027775ee0de43f355647b51be8395f5b', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-334', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.836', 358, 'MARK_RAN', '7:8c9f7e5599bd901dfe70bbc3854e89af', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-335', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.842', 359, 'MARK_RAN', '7:c790abe64246f34910e0bf8193c52582', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-336', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.849', 360, 'MARK_RAN', '7:42f111e330fdfd743cc1290bf44202fc', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-337', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.856', 361, 'MARK_RAN', '7:4a7a26875cf1ef19083658f8d6b9c83f', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-338', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.863', 362, 'MARK_RAN', '7:3f41da9f683190eb9c8b005a8b321b02', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-339', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.871', 363, 'MARK_RAN', '7:b26355ea4ba6397d413cbe284b1df553', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-340', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.878', 364, 'MARK_RAN', '7:fd6eea45665e726c5c191e0d8e6d5103', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-341', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.884', 365, 'MARK_RAN', '7:c23eac5443e13340796da27ba90abdde', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-342', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.892', 366, 'MARK_RAN', '7:871908b2e2690774c5c20bc7dda08983', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-343', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.899', 367, 'MARK_RAN', '7:f3e800da59458ebdd1718fa89792ac44', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-344', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.905', 368, 'MARK_RAN', '7:d6634926cd47928d9ed9b301cdb576f3', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-345', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.913', 369, 'MARK_RAN', '7:f08dd005a65381ff213593b5a8723ddb', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-346', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.92', 370, 'MARK_RAN', '7:aebc3df697fe6c25e48dbd5a811ec951', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-347', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.928', 371, 'MARK_RAN', '7:2baa1b6db9b5f3e1cda2600ea2a6c871', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-348', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.935', 372, 'MARK_RAN', '7:9f6a99f801304f5a2bc0024f87f0a6ad', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-349', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.942', 373, 'MARK_RAN', '7:0fa856bce727d75e4ff5842c719dc249', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-350', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.948', 374, 'MARK_RAN', '7:5f99e8d287d6b948d95c3d9b21d3a2df', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236096695424-351', 'pgawade (generated)', 'migration/2.5/changeLogCreateFunctions.xml', '2016-06-15 15:14:51.955', 375, 'MARK_RAN', '7:6526f1897c46b595c13193ad1e769a57', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-217', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:51.965', 376, 'EXECUTED', '7:ba0db7ae80e8e0ecc123f57f20b07606', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-218', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:51.974', 377, 'EXECUTED', '7:3c920ba7872bbd77c2c6347f6b571584', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-219', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:51.982', 378, 'EXECUTED', '7:f2ccbbf3eb8185654349a725042fe8a5', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-220', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:51.992', 379, 'EXECUTED', '7:a3546275534064a95f572fd56646829b', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-221', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52', 380, 'EXECUTED', '7:4c190ab1328f8b368538ada2d1b6fc70', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-222', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.01', 381, 'EXECUTED', '7:4d0849f5aa8c6f3e5dc4f11fedbe1412', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-223', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.019', 382, 'EXECUTED', '7:38ec9d84616fa941b7037d0506fadfc7', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-224', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.028', 383, 'EXECUTED', '7:4865ea3caf308542ae289401b44b2f76', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-225', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.037', 384, 'EXECUTED', '7:ce27b3bcd5b6c5932f65c6df14365095', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-226', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.048', 385, 'EXECUTED', '7:898b75297c0bdadafca552bfd8e41332', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-227', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.06', 386, 'EXECUTED', '7:49138bcb00fe90659ba6962daafd29f1', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-228', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.071', 387, 'EXECUTED', '7:e2fa422dedfc15a5af880e6428ded446', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-229', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.084', 388, 'EXECUTED', '7:dbade8b16fbb84118468586287022bf5', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-230', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.094', 389, 'EXECUTED', '7:cfd0ce86b169565066a6f4bd96c21c87', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-231', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.106', 390, 'EXECUTED', '7:8a33a7227108080033dcc812c66aad43', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-232', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.117', 391, 'EXECUTED', '7:c69bc69d62c450f0c27a7c8f8a3369d9', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-233', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.127', 392, 'EXECUTED', '7:a534c45498321ab9699afb4559bb0702', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-234', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.137', 393, 'EXECUTED', '7:3d77c1386f5e7f3ad9dab77ecf5dbbd7', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-235', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.15', 394, 'EXECUTED', '7:6f85c2684b804405a7643dff4733dfc1', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-237', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.16', 395, 'EXECUTED', '7:030e4012f7ed13441758e724abe6cdac', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-238', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.173', 396, 'EXECUTED', '7:1ec47669e84536d61228dc1a2a1edbbf', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-239', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.184', 397, 'EXECUTED', '7:10eed5be9581fac4877ca30b9ecf98f4', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-240', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.195', 398, 'EXECUTED', '7:a1a7caaf157802650bcb7a78e4879df8', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-241', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.207', 399, 'EXECUTED', '7:b7e7889eb85ba41a6e665347743fd2d7', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-242', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.218', 400, 'EXECUTED', '7:998fdc0ef2001c5dc4f735d4e14cf08a', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-243', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.228', 401, 'EXECUTED', '7:84ff273d4838ffa2ce359a9f1180efb8', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-244', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.24', 402, 'EXECUTED', '7:1def62276e94a24cfb4c9a2f73bd2d40', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-245', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.252', 403, 'EXECUTED', '7:403caf39a9c38bd742c1beab8f2339ff', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-246', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.261', 404, 'EXECUTED', '7:a95d2ffc7fd13ca0793e94b20c793050', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-247', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.273', 405, 'EXECUTED', '7:27a788cefff81bfb2155035c4f44c571', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-248', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.284', 406, 'EXECUTED', '7:07810d106fc6c9c32485599cc3f8ceef', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-249', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.295', 407, 'EXECUTED', '7:e4d389999a90c8ae7ad9a2c62f65536b', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-250', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.307', 408, 'EXECUTED', '7:b01e198a55274ec73ddd523c291b04ce', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-251', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.319', 409, 'EXECUTED', '7:b712d47ef630a405ed3c22b8c383ac6a', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-252', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.331', 410, 'EXECUTED', '7:cd6fd0bd0669d00b3c2a3ccb17c0a3e7', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-253', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.344', 411, 'EXECUTED', '7:b5acb4523857f1eef89bea8e981bfb93', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-254', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.354', 412, 'EXECUTED', '7:73b5c480a04c44bbcc3bd4c792dbe222', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-255', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.366', 413, 'EXECUTED', '7:160a5859b479667944ea2f3389268a6e', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-256', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.376', 414, 'EXECUTED', '7:36bee85fa31e08ee57a64574bea24ff4', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-257', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.387', 415, 'EXECUTED', '7:2afdf9989f0e993dcf191ae74c185da4', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-258', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.4', 416, 'EXECUTED', '7:daf094c802204c309850721d4275e336', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-259', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.41', 417, 'EXECUTED', '7:b095f65b03faae69bfb1c3d02f695a28', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-260', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.42', 418, 'EXECUTED', '7:0b6e6cdf133e646c3383708401b9a697', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-261', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.433', 419, 'EXECUTED', '7:800ab6905a4774fc4a3fcb9bf3cdad6e', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-262', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.443', 420, 'EXECUTED', '7:266ee54a8dab945c44088466cc71d0c8', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-263', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.455', 421, 'EXECUTED', '7:0a266488f9fdc118a0c1adca642da62a', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-264', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.464', 422, 'EXECUTED', '7:0b4eb931dda4949a821d82edd105319f', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-265', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.473', 423, 'EXECUTED', '7:171d8776a1a6724104fa0b62ace3156b', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-266', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.482', 424, 'EXECUTED', '7:d1863bfc46ab0ec5c4c23ab7c30b3bca', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-267', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.492', 425, 'EXECUTED', '7:994c2147f2c2796997790d3b5de6c3e1', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-268', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.5', 426, 'EXECUTED', '7:18037675edfb799dce4673c480b2e0e7', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-269', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.509', 427, 'EXECUTED', '7:443cfd81b9de4595ef4600531626ae41', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-270', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.52', 428, 'EXECUTED', '7:bafe2a696b2f260a4a28930fab1ef16a', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-271', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.529', 429, 'EXECUTED', '7:8e68e8d38e98df7b681d9bf53ae4e4f7', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-272', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.538', 430, 'EXECUTED', '7:5e2781a43171e5bfc7b1a9c5c24dba4c', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-273', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.546', 431, 'EXECUTED', '7:08bf7d63ec5b2286fcb7f9342536f076', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-274', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.557', 432, 'EXECUTED', '7:1c5feab3216a9bf556cdaeca5c908366', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-275', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.566', 433, 'EXECUTED', '7:86717941e5f38e6656367c75fef4035f', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-276', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.575', 434, 'EXECUTED', '7:b592d6f3304de75e869756856f03aa53', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-277', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.585', 435, 'EXECUTED', '7:8e34e938f9814505a8fca17ff78ae347', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-278', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.593', 436, 'EXECUTED', '7:630aa5148999776408315b9d46ffad02', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-279', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.604', 437, 'EXECUTED', '7:d2d4bae74424d61b605b67e1f350f6ff', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-279-1', 'kkrumlian', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.614', 438, 'EXECUTED', '7:f077ef0923fb793924ef0ebeed54457e', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-279-2', 'kkrumlian', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.621', 439, 'MARK_RAN', '7:630ce7e39247f99cb8ab5a00834f526d', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-280', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.634', 440, 'EXECUTED', '7:4177083ba704901a23256c1f25bae40f', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-280-1', 'kkrumlian', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.643', 441, 'EXECUTED', '7:2d802ff1ad8e950c5ff3fd06eb4b2c06', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-280-2', 'kkrumlian', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.65', 442, 'MARK_RAN', '7:ded4e2add3f698ba7c283df653d08875', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-281', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.66', 443, 'EXECUTED', '7:72651b4139aabb47fff384c525e35ffe', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-282', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.668', 444, 'EXECUTED', '7:6b47d08ee15c07653880add1cb1621f2', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-283', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.678', 445, 'EXECUTED', '7:7acb3f452e323194569c43ac8583e150', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-284', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.687', 446, 'EXECUTED', '7:42653a861b948126e906d642c5bc3c06', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-285', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.696', 447, 'EXECUTED', '7:82166b081f80515247a09964b808ee34', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-286', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.705', 448, 'EXECUTED', '7:b52344d0ac2097e1bbbc79a6fdbb00f7', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-287', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.715', 449, 'EXECUTED', '7:58cff8e19389170c579b1951518e2120', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-288', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.723', 450, 'EXECUTED', '7:8f5736e2cfda14a49b3f1fa01dbef945', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-289', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.732', 451, 'EXECUTED', '7:45bdfeb4d59b0ae5e4a49cac18a7ecd0', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-290', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.744', 452, 'EXECUTED', '7:8708c0e4753981a34fcc32b36d71b421', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-291', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.754', 453, 'EXECUTED', '7:96c84565131e2c2a4441d5e8eb4ee0ec', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-292', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.766', 454, 'EXECUTED', '7:99d79c68726b4df2a87083fc03ac501c', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-293', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.776', 455, 'EXECUTED', '7:fa9ab339e38de7199f2438c2fd86e78c', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-294', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.785', 456, 'EXECUTED', '7:8d8967c7053c03e61c18cf8d24158f1c', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-295', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.794', 457, 'EXECUTED', '7:919f2d9e4dc65f48274cd5349bc04f71', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-296', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.804', 458, 'EXECUTED', '7:0bb2bc929e3807d7de2f73d269332f72', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-297', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.813', 459, 'EXECUTED', '7:3211f84b21e904217f50aa99913d59e5', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-298', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.821', 460, 'EXECUTED', '7:da03dd142e34d4de6ec3f0861bfbf5a2', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-299', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.831', 461, 'EXECUTED', '7:8589a82531af2f4fd7bd945a7c0eb499', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-300', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.839', 462, 'EXECUTED', '7:3bbf2941df788c73b11a516404a5e373', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-301', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.85', 463, 'EXECUTED', '7:31c1401c24f466703825cfc34fcafb4f', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-302', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.858', 464, 'EXECUTED', '7:b6fc126d4da205c3e8472b95dff606d6', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-303', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.869', 465, 'EXECUTED', '7:79a2a1b03a21619c15b63dc8fe75dbb9', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-304', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.878', 466, 'EXECUTED', '7:044de9486f362434f38ee0085fcfeec9', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-305', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.887', 467, 'EXECUTED', '7:f862e76aa952846494088be5d99498a6', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-306', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.897', 468, 'EXECUTED', '7:698faaa04bdf5314baddf291473cdd0f', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-307', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.907', 469, 'EXECUTED', '7:d5fd14192e48a507e4dabade86c14eb1', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-308', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.917', 470, 'EXECUTED', '7:d1f287b1ad843e2a08dffe9bc1042570', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-309', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.927', 471, 'EXECUTED', '7:47cf4d6a54f3b4ed862b991a71e24af9', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-310', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.935', 472, 'EXECUTED', '7:bb7fb37c790396b6a4918e7c75f4ec81', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-311', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.945', 473, 'EXECUTED', '7:f36946ae6e1562dda5177bbd8ed60cd9', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-312', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.955', 474, 'EXECUTED', '7:4b97a8d054e229050db9b3e1f0d1dee4', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-313', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.964', 475, 'EXECUTED', '7:daae4cda1894e70078a27dec3f2d4620', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-314', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.972', 476, 'EXECUTED', '7:1984686e81f6c76e03d64279250fe10e', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-315', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.981', 477, 'EXECUTED', '7:f059aecbb4fc0afa96ca4399ac36f093', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-316', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:52.992', 478, 'EXECUTED', '7:181cf6c48b7e04770cf11acc93e36e50', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-317', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.013', 479, 'EXECUTED', '7:68c38491e3472c42fdc5eb593f221a3f', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-318', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.023', 480, 'EXECUTED', '7:557c0b383638557fc755483d5c88559e', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-319', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.031', 481, 'EXECUTED', '7:782b9b2ababfc0dc64bedf0d5be5362a', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-320', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.041', 482, 'EXECUTED', '7:1e7b785175469de620a6166b4ba519ce', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-321', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.05', 483, 'EXECUTED', '7:760d11eb34d878189e16bf75d62ed17b', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-322', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.059', 484, 'EXECUTED', '7:7f0f1e64a3d83dcf410dfbd59583e89f', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-323', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.067', 485, 'EXECUTED', '7:92dd2435738b7a18872eec428566dccc', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-324', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.076', 486, 'EXECUTED', '7:28ef876db3153128903bf0ee6f49cf03', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-325', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.087', 487, 'EXECUTED', '7:4dfdad9d668fa3d040bb8e214f2c1c5d', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-326', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.096', 488, 'EXECUTED', '7:4c1c4af85b4f4e094039bbe310255963', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-327', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.104', 489, 'EXECUTED', '7:cab67a18f8c0d097a8737b71a91a291e', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-328', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.115', 490, 'EXECUTED', '7:82aa61efad605eddf4ecfd36261a3679', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-329', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.123', 491, 'EXECUTED', '7:3e60e0593f32cb8fe34327f91c4887b5', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-330', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.132', 492, 'EXECUTED', '7:0e5a77c08ceba45c0f69df159fba6bd0', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-331', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.143', 493, 'EXECUTED', '7:9c4e37f20c1246d0b6206b08e8a7ac89', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-332', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.153', 494, 'EXECUTED', '7:6f52150880fff53d51dcae291f9dae3d', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-333', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.162', 495, 'EXECUTED', '7:da70bab49ba8083253109e78a7b754e7', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-334', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.171', 496, 'EXECUTED', '7:f6211a4b6c0699e6d677bf88eb856c5e', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-335', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.181', 497, 'EXECUTED', '7:069b944e664756327c4b3f6579e96cbd', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-336', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.189', 498, 'EXECUTED', '7:ada35095780b2e49088b8cf7d3928f23', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-337', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.198', 499, 'EXECUTED', '7:b4a5b186a23ba03caed56f1e49659529', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-338', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.207', 500, 'EXECUTED', '7:0d0fdf5c8d9e323361013a6b6d6608d1', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-339', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.216', 501, 'EXECUTED', '7:c8d2b2e698356a5dd30729e81bbb50a5', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-340', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.225', 502, 'EXECUTED', '7:ea767f80c906870feb89c5ff6d548bbc', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-341', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.236', 503, 'EXECUTED', '7:2a2884abc62213c61cff6ad48a099612', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-342', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.245', 504, 'EXECUTED', '7:6c701af3390fa0dfb1a4ca4030cbdfcb', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-343', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.254', 505, 'EXECUTED', '7:ba1a86ee005449c8c60564d76fa0eafe', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-344', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.262', 506, 'EXECUTED', '7:c3b62120ae8ee009c202445476a77d8c', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-345', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.272', 507, 'EXECUTED', '7:8241391f3e11fcb11a930e55e9675965', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-346', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.281', 508, 'EXECUTED', '7:125a7a5c8966d766ff76a93a12ef862b', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-347', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.291', 509, 'EXECUTED', '7:0f1b6806518704f3856b1b20a118f5b9', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-348', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.301', 510, 'EXECUTED', '7:b7eea083948730265ba4730ae158a184', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-349', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.309', 511, 'EXECUTED', '7:5141d47d74fc503da421275ec9a63b7d', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-350', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.319', 512, 'EXECUTED', '7:ce9b01778d3276f362154038d481d051', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-351', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.328', 513, 'EXECUTED', '7:9c9dd346cda6d3777bfc8e141c977759', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-352', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.337', 514, 'EXECUTED', '7:55f9b44e442941fe84e60b8a08327807', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-353', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.346', 515, 'EXECUTED', '7:9e6566874d7177519a1f892db4401a86', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-354', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.354', 516, 'EXECUTED', '7:943a453ce27903b5b4631827bf73b7ee', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-355', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.363', 517, 'EXECUTED', '7:ccddc36a08606e7e4400bf9231e9f3c8', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-356', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.373', 518, 'EXECUTED', '7:c48c205221514db1a92345e251296ec8', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-357', 'pgawade (generated)', 'migration/2.5/changeLogDataInsert.xml', '2016-06-15 15:14:53.381', 519, 'EXECUTED', '7:ae1e3219f880a6a0ceabafac4734e718', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-372', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.39', 520, 'EXECUTED', '7:9cc33d88b6db7d5b10dbac0234404b2b', 'renameColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-373', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.399', 521, 'EXECUTED', '7:a7b4c00198d917670f6dc5158a055de1', 'renameTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-374', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.408', 522, 'EXECUTED', '7:13032ca34e199e42c14e7cafd8d465b0', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-375', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.417', 523, 'EXECUTED', '7:f161eaf00a527b4b85fd2f63a053de3f', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-376', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.426', 524, 'EXECUTED', '7:9b9632db0248caf4684a56e81f226b36', 'renameColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-377', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.434', 525, 'EXECUTED', '7:a716ecbc13e61cee1787d9ef09187333', 'renameTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-378', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.442', 526, 'EXECUTED', '7:58709d7e4d48ffa0164b77a53d8fc749', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-379', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.451', 527, 'EXECUTED', '7:5702edb890578a064204eda04d56ce5d', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-380', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.495', 528, 'EXECUTED', '7:88759aec2e432eb874b77d2dbef87adf', 'renameColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-381', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.503', 529, 'EXECUTED', '7:e4e0312ccb8d9002e6de6c789741d939', 'renameTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-382', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.512', 530, 'EXECUTED', '7:94acce50e28f5a73ccc411b343e581a6', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-383', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.522', 531, 'EXECUTED', '7:c161ad3ee585a1abac613f3d3380cb78', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-384', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.53', 532, 'EXECUTED', '7:1fbe4f2b981410aa31d2ef5a8b84520a', 'renameColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-385', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.538', 533, 'EXECUTED', '7:0e9c1c0e19d910e209d2b629e198f080', 'renameTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-386', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.547', 534, 'EXECUTED', '7:cf26efb3648af976361cb0f1419e4397', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-387', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.557', 535, 'EXECUTED', '7:32f80305d6b8d0461fd2bf2edc281fd4', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-388', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.565', 536, 'EXECUTED', '7:83f12d2e2c3182b20f38ff92f16638be', 'renameColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-389', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.573', 537, 'EXECUTED', '7:c87a2c597f89c972ccc180e5675cdb39', 'renameTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-390', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.582', 538, 'EXECUTED', '7:7f24e39866ff059bdc444553c71e6496', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-391', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.59', 539, 'EXECUTED', '7:03c75b05a03da19c957779a25332da8a', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-392', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.6', 540, 'EXECUTED', '7:ca4222f548d8c4b0461d1c09b1058063', 'renameColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-393', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.609', 541, 'EXECUTED', '7:7063d0d1014c8c66a15bb848008b1457', 'renameTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-394', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.618', 542, 'EXECUTED', '7:4314d05f7bccbdd4a862d4ad768e4956', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-395', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.626', 543, 'EXECUTED', '7:aeb5e26caa86bd7e4c29d36f8190d269', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-396', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.634', 544, 'EXECUTED', '7:4429616b3a21b56f08316072c77128b3', 'renameColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-397', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.645', 545, 'EXECUTED', '7:6fa80c45b3b929dab440e81603f432c8', 'renameTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-398', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.653', 546, 'EXECUTED', '7:61c2c6d831be63931f7821b566605a3d', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-399', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.661', 547, 'EXECUTED', '7:8e2f85493e751191661e35e7c867a17d', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-400', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.669', 548, 'EXECUTED', '7:437fb997e141ff5627359fa2fbc25b32', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-401', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.681', 549, 'EXECUTED', '7:bcf7e86fdc0e48e80a3f803f3acb945e', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-402', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.694', 550, 'EXECUTED', '7:18def06458d4544a79f571a192e4086b', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-433', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.7', 551, 'MARK_RAN', '7:cdc8b5dbb53fd675c9276260fa6b11e6', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-434', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.709', 552, 'MARK_RAN', '7:9efb31401f3d0a68ef5da6c846beb0a0', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-403', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.722', 553, 'EXECUTED', '7:b84c653f950496028773858449987a40', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-404', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.737', 554, 'EXECUTED', '7:d749b55e4db9375e3402d001d95c8976', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-405', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.752', 555, 'EXECUTED', '7:8213905a06b1bb3e205e628f286e0d28', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-406', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.768', 556, 'EXECUTED', '7:9722767965bb2f6b8ea0998478431b11', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-407', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.789', 557, 'EXECUTED', '7:c332025b56abfbc2a3457e61ce670c8d', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-408', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.806', 558, 'EXECUTED', '7:fe0f1bec80ed75dae2a96e1b60462797', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-409', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.818', 559, 'EXECUTED', '7:b89b6ca805c568bcbb9c311abf6de760', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-410', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.831', 560, 'EXECUTED', '7:caf79aa9e20df3148bff4eeb1ec9fe9d', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-411', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.843', 561, 'EXECUTED', '7:5e1072d584f6d488e8b5767d6e762af7', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-412', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.855', 562, 'EXECUTED', '7:e41b3eaa547ddde573cd7ed9b0816cf8', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-413', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.869', 563, 'EXECUTED', '7:9aedc8466ce42343311fe8365e398257', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-414', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.884', 564, 'EXECUTED', '7:5e3878208f8d028bf462543d3f72d173', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-415', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.895', 565, 'EXECUTED', '7:a15a8f64453b5ed1e35ffe6cbdad4f61', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-416', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.904', 566, 'EXECUTED', '7:6b218b793319104cc1047c4ebb7bfd02', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-417', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.913', 567, 'EXECUTED', '7:8241f7a0074fa85513dfa438ff7b7a54', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-418', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.922', 568, 'EXECUTED', '7:f543f16889323ac6b3b82ee4ba03c34e', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-419', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.931', 569, 'EXECUTED', '7:4dd74f8dd06619e9354ced401b3a12e5', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-420', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.941', 570, 'EXECUTED', '7:b673f9c3b460408323c69d0e9efa6d06', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236021533243-421', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.955', 571, 'EXECUTED', '7:272963355ceaca3e31e58ad542669461', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236021533243-422', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.969', 572, 'EXECUTED', '7:6259b34a4d8645b546cc9c13b9b0aafa', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236021533243-423', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.983', 573, 'EXECUTED', '7:c7970c99f0db4e9430633938f7a56869', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236021533243-424', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:53.996', 574, 'EXECUTED', '7:e11f0be0f851c51943fda28bb07eb35f', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236021533243-425', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:54.008', 575, 'EXECUTED', '7:6c516ebbaeab42b3c2ff7229c889933a', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1236021533243-426', 'pgawade (generated)', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:54.021', 576, 'EXECUTED', '7:41e31aa49916644bb96d8b4324268d85', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-427', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:54.034', 577, 'EXECUTED', '7:606fef0deb3883564a9a68af5541dc67', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-428', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:54.043', 578, 'EXECUTED', '7:04760175b41570985739d8370c6a6b6e', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-429', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:54.054', 579, 'EXECUTED', '7:01c6342f8925975158c496690bdd4b91', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-430', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:54.062', 580, 'EXECUTED', '7:f1053a49b4b4886a15fe4f625a27f8fb', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-431', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:54.071', 581, 'EXECUTED', '7:c62b01a81028fe9f02d48afbb36aecc7', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-432', 'pgawade', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:54.08', 582, 'EXECUTED', '7:7d5eb4031c31890d6636c6c0318cab6a', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-433', 'kkrumlian', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:54.089', 583, 'EXECUTED', '7:1d19820dae2b60277f9d38bdfcdedbf5', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-434', 'kkrumlian', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:54.097', 584, 'EXECUTED', '7:981bcaac3e09c9a1a40848b7cc873af0', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('1235684743487-435', 'kkrumlian', 'migration/3.0/schema_patch_2.5_to_3.0.xml', '2016-06-15 15:14:54.105', 585, 'EXECUTED', '7:d78ab780447c2e385a50d6588e1c29aa', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-03-16-2914-1', 'ywang', 'migration/3.0/2009-03-16-2914.xml', '2016-06-15 15:14:54.118', 586, 'EXECUTED', '7:46dda47d573d1d7b7e5189ac6534fcb9', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-03-16-2914-2', 'ywang', 'migration/3.0/2009-03-16-2914.xml', '2016-06-15 15:14:54.128', 587, 'EXECUTED', '7:1ae9a0f888d73a56102fcc8626d44b51', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-03-16-2914-3', 'ywang', 'migration/3.0/2009-03-16-2914.xml', '2016-06-15 15:14:54.136', 588, 'EXECUTED', '7:c658461fffb207de24ad771ea6cc4331', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-03-16-2914-4', 'ywang', 'migration/3.0/2009-03-16-2914.xml', '2016-06-15 15:14:54.145', 589, 'EXECUTED', '7:e32305ec174e3abc0412fcf84bfe8390', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-03-16-2914-5', 'ywang', 'migration/3.0/2009-03-16-2914.xml', '2016-06-15 15:14:54.154', 590, 'EXECUTED', '7:cbb21896f451518225c32b4ead04831e', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-03-16-2914-6', 'ywang', 'migration/3.0/2009-03-16-2914.xml', '2016-06-15 15:14:54.165', 591, 'EXECUTED', '7:1f3a3e3c78ce5cd752ca101073c332a9', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-03-30-NA01-1', 'kkrumlian', 'migration/3.0/2009-03-30-NA01.xml', '2016-06-15 15:14:54.175', 592, 'EXECUTED', '7:bdaac895078a7466488800cabf7e6bd8', 'renameColumn', 'Rename Column Name', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-03-30-NA01-2', 'kkrumlian', 'migration/3.0/2009-03-30-NA01.xml', '2016-06-15 15:14:54.186', 593, 'EXECUTED', '7:16230aa124bf464bc4f0702aab00c151', 'addForeignKeyConstraint', 'Add foreign Key for the source_study_id', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-03-30-3348-1', 'ywang', 'migration/3.0/2009-03-30-3348.xml', '2016-06-15 15:14:54.194', 594, 'EXECUTED', '7:e843e57daef362488fcba87a24655069', 'addColumn', 'event_definition_crf (source_data_verification_code)', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-03-30-3348-2', 'ywang', 'migration/3.0/2009-03-30-3348.xml', '2016-06-15 15:14:54.203', 595, 'EXECUTED', '7:db556d02c088a7fa11319c9f27762178', 'addColumn', 'event_definition_crf (selected_version_ids)', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-03-30-3348-3', 'ywang', 'migration/3.0/2009-03-30-3348.xml', '2016-06-15 15:14:54.212', 596, 'EXECUTED', '7:6d9003875dec224880b87fb89e0b6d71', 'addColumn', 'event_definition_crf (parent_id)', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-03-31-1', 'ywang', 'migration/3.0/2009-03-31-3382.xml', '2016-06-15 15:14:54.221', 597, 'EXECUTED', '7:cae442c942467a3148a3e4e8e3258436', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-03-31-2', 'ywang', 'migration/3.0/2009-03-31-3382.xml', '2016-06-15 15:14:54.228', 598, 'EXECUTED', '7:4064ff390b8f42cdcee6f82219ef61f6', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-03-31-3', 'ywang', 'migration/3.0/2009-03-31-3382.xml', '2016-06-15 15:14:54.237', 599, 'EXECUTED', '7:64c823624b972f171fe16234f22b4423', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-04-02-3397-1', 'kkrumlian', 'migration/3.0/2009-04-02-3397.xml', '2016-06-15 15:14:54.25', 600, 'EXECUTED', '7:98735e353ef3718fcc565082e0465280', 'createTable', 'Create a table named audit_user_login', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-04-02-3397-3', 'kkrumlian', 'migration/3.0/2009-04-02-3397.xml', '2016-06-15 15:14:54.262', 601, 'EXECUTED', '7:f6b749174c8e40eb04c95be1b04f0499', 'addForeignKeyConstraint', 'Add foreign Key for user_account_id', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-04-02-3397-4', 'kkrumlian', 'migration/3.0/2009-04-02-3397.xml', '2016-06-15 15:14:54.28', 602, 'EXECUTED', '7:9101494c12f92ba9ab873fd6fb542318', 'addColumn', 'Add enabled property to user_account table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-04-02-3397-5', 'kkrumlian', 'migration/3.0/2009-04-02-3397.xml', '2016-06-15 15:14:54.298', 603, 'EXECUTED', '7:54d921ad4bb4d59138d166243af00090', 'addColumn', 'Add account_non_locked property to user_account table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-04-02-3397-6', 'kkrumlian', 'migration/3.0/2009-04-02-3397.xml', '2016-06-15 15:14:54.316', 604, 'EXECUTED', '7:4f260bcb5f728d38075eb815bdc2f3e2', 'addColumn', 'Add lock_counter property to user_account table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-04-02-3397-7', 'kkrumlian', 'migration/3.0/2009-04-02-3397.xml', '2016-06-15 15:14:54.332', 605, 'EXECUTED', '7:bbd88e7b0283a6e0248ef83509ab603a', 'createTable', 'Create a table named configuration', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-04-02-3397-8', 'kkrumlian', 'migration/3.0/2009-04-02-3397.xml', '2016-06-15 15:14:54.343', 606, 'EXECUTED', '7:a4f745dbd7611db9d9809da7d343a0aa', 'createIndex', 'Create an index on table named configuration', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-04-02-3397-10', 'kkrumlian', 'migration/3.0/2009-04-02-3397.xml', '2016-06-15 15:14:54.36', 607, 'EXECUTED', '7:88524538ea481bde109a4e98189ee05e', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-04-02-3397-11', 'kkrumlian', 'migration/3.0/2009-04-02-3397.xml', '2016-06-15 15:14:54.368', 608, 'EXECUTED', '7:d080015f07e0f2b44994708cf1e147f4', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-04-10-3120-1', 'sshamim', 'migration/3.0/2009-04-10-3120.xml', '2016-06-15 15:14:54.382', 609, 'EXECUTED', '7:e1f28f2419ddc14616f8dd9e0b54f37d', 'createTable', 'Create a table named study_module_status', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-04-10-3120-2', 'sshamim', 'migration/3.0/2009-04-10-3120.xml', '2016-06-15 15:14:54.395', 610, 'EXECUTED', '7:7996829d11729e757f471de8d6a5dd5c', 'addForeignKeyConstraint', 'Add foreign Key for study_id column', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-04-29-3349-1', 'bwperry', 'migration/3.0/2009-04-29-3349.xml', '2016-06-15 15:14:54.411', 611, 'EXECUTED', '7:3a2e798b9e2eca79977bdb93cb6bbf5d', 'addColumn', 'Add sdv_status property to event_crf table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-05-15-3624-1', 'ywang', 'migration/3.0/2009-05-15-3624.xml', '2016-06-15 15:14:54.426', 612, 'EXECUTED', '7:7b3ea6acac16cdc937ecb21d27a262ae', 'createTable', 'create measurement_unit', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-05-15-3624-3', 'ywang', 'migration/3.0/2009-05-15-3624.xml', '2016-06-15 15:14:54.436', 613, 'EXECUTED', '7:d50cc57c27c04e38358cfd781d230b59', 'sql', 'mapping item.units to measurement_unit.name and oc_oid', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-05-28-3481-1', 'sshamim', 'migration/3.0/2009-05-28-3481.xml', '2016-06-15 15:14:54.444', 614, 'EXECUTED', '7:9bd34c2f26718586eb9a6253fde209b2', 'insert', 'Insert a record in Status table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-06-04-3684-1', 'thickerson', 'migration/3.0/2009-06-04-3684.xml', '2016-06-15 15:14:54.454', 615, 'EXECUTED', '7:05f16dcc8e49ee7d04b7feda81cd9004', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-06-08-3628-1', 'ywang', 'migration/3.0/2009-06-08-3628.xml', '2016-06-15 15:14:54.462', 616, 'EXECUTED', '7:4ba60cdfc891e206ba368eb2f68e67e6', 'insert', 'insert a new record to null_value_type: NPE(not performed)', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-06-10-3260-1', 'kkrumlian', 'migration/3.0/2009-06-10-3260.xml', '2016-06-15 15:14:54.472', 617, 'EXECUTED', '7:dd2399d26e86a9dd6759b64b4f041145', 'update', 'update discrepancy_note set entity_type=''itemData'' where entity_type=''ItemData''', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-07-23-3930-1', 'ywang', 'migration/3.0/2009-07-23-3930.xml', '2016-06-15 15:14:54.48', 618, 'EXECUTED', '7:35e425b101a6cf6bfd892ff6fa4f8eae', 'sql', 'create or replace event_crf_trigger for event crf sdv status', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-07-23-3930-2', 'ywang', 'migration/3.0/2009-07-23-3930.xml', '2016-06-15 15:14:54.491', 619, 'EXECUTED', '7:8e9a834eed8847b0c164b23555e3b7fd', 'insert', 'Insert event crf sdv into audit_log_event_type  table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-07-24-3648-1', 'shamim', 'migration/3.0/2009-07-24-3648.xml', '2016-06-15 15:14:54.499', 620, 'EXECUTED', '7:7cb07df79aff6e3460be089e1f70b33f', 'addColumn', 'Add a column in event_crf table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-09-10-3599-1', 'ywang', 'migration/3.0/2009-09-10-3599.xml', '2016-06-15 15:14:54.507', 621, 'EXECUTED', '7:221166e4e9c0594c9189e8058d13297f', 'sql', 'update resolution_status_id for parent note', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-12-01-4439-1', 'kkrumlian', 'migration/3.0/2009-12-01-4439.xml', '2016-06-15 15:14:54.516', 622, 'EXECUTED', '7:439a02bdb2f9d9caf1133cca1fef1eb9', 'sql', 'update study protocol_type', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-12-01-4439-2', 'kkrumlian', 'migration/3.0/2009-12-01-4439.xml', '2016-06-15 15:14:54.523', 623, 'EXECUTED', '7:732e372fbf1c088fa38b2b7f69a88289', 'sql', 'update study protocol_type', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-12-01-4439-3', 'kkrumlian', 'migration/3.0/2009-12-01-4439.xml', '2016-06-15 15:14:54.53', 624, 'EXECUTED', '7:5bce3ff248d0f06fd16961f7245702db', 'sql', 'update study protocol_type', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-12-11-4502-1', 'kkrumlian', 'migration/3.0/2009-12-11-4502.xml', '2016-06-15 15:14:54.539', 625, 'EXECUTED', '7:df7cc3c03bf9bde55a2b2065f2e672df', 'update', 'update study protocol_type', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-02-16-NA01-1', 'kkrumlian', 'migration/3.0/2010-02-16-NA01.xml', '2016-06-15 15:14:54.558', 626, 'EXECUTED', '7:31d8260b74206335f5f2dcbd0823e017', 'sql', 'update data in item_data table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-03-01-4772-2', 'kkrumlian', 'migration/3.0/2010-03-01-4772.xml', '2016-06-15 15:14:54.574', 627, 'EXECUTED', '7:30c74bc2cdb347ad143b80ac57a37d6d', 'sql', 'ALTER TABLE dataset ALTER sql_statement TYPE text', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-03-02-4776-1', 'kkrumlian', 'migration/3.0/2010-03-02-4776.xml', '2016-06-15 15:14:54.584', 628, 'EXECUTED', '7:57679cff4915a43c9dd64aad79ed4336', 'update', 'update event_definition_crf', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-03-03-4768-1', 'sshamim', 'migration/3.0/2010-03-03-4768.xml', '2016-06-15 15:14:54.591', 629, 'EXECUTED', '7:03e4b0d13731389b523eb1d20bb32096', 'sql', 'create or replace event_definition_crf_trigger', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2009-03-03-4768-2', 'sshamim', 'migration/3.0/2010-03-03-4768.xml', '2016-06-15 15:14:54.599', 630, 'EXECUTED', '7:4b09a54280bde4d8304006f10f24a5c0', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-03-03-NA01-1', 'kkrumlian', 'migration/3.0/2010-03-03-NA01.xml', '2016-06-15 15:14:54.607', 631, 'EXECUTED', '7:bf022975cd036178adf6d7ffbc463ed0', 'addColumn', 'Add Study Subject Id column to', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-03-03-NA01-2', 'kkrumlian', 'migration/3.0/2010-03-03-NA01.xml', '2016-06-15 15:14:54.616', 632, 'EXECUTED', '7:218f72beb7ae4d19d42cec3b4c635fdd', 'sql', 'Update Study Subject Id column', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-03-03-NA01-3', 'kkrumlian', 'migration/3.0/2010-03-03-NA01.xml', '2016-06-15 15:14:54.623', 633, 'EXECUTED', '7:27012d1317c54add2d2268eba985e92f', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-03-03-NA01-4', 'kkrumlian', 'migration/3.0/2010-03-03-NA01.xml', '2016-06-15 15:14:54.631', 634, 'EXECUTED', '7:9197a8a64903186fbbc69d2b90d02e07', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-03-03-NA01-5', 'kkrumlian', 'migration/3.0/2010-03-03-NA01.xml', '2016-06-15 15:14:54.641', 635, 'MARK_RAN', '7:93ff80eda2d5c9fd21d460f75493bc32', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-03-03-NA01-6', 'kkrumlian', 'migration/3.0/2010-03-03-NA01.xml', '2016-06-15 15:14:54.649', 636, 'EXECUTED', '7:9650b0b1949ae81b4888acf563e748d0', 'update', 'update discrepancy_note table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-05-31-5009-1', 'ahamid', 'migration/3.0/2010-05-31-5009.xml', '2016-06-15 15:14:54.667', 637, 'EXECUTED', '7:64a0a28235e1b2779ae4ac6a55bffb6d', 'addColumn', 'study (old_status)', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-05-31-5009-3', 'ahamid', 'migration/3.0/2010-05-31-5009.xml', '2016-06-15 15:14:54.679', 638, 'EXECUTED', '7:5ec58d2ec041a55510e86dcfc0f9b6a7', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-05-31-5009-4', 'ahamid', 'migration/3.0/2010-05-31-5009.xml', '2016-06-15 15:14:54.687', 639, 'EXECUTED', '7:355dbecf26239ee631c7e08085467fb1', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-05-27-5095-2', 'kkrumlian', 'migration/3.0/2010-05-27-5095.xml', '2016-06-15 15:14:54.695', 640, 'EXECUTED', '7:71ee8c80162c7c5771f03db969ba2037', 'addColumn', 'Add item_id column to rule_set', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-05-27-5095-3', 'kkrumlian', 'migration/3.0/2010-05-27-5095.xml', '2016-06-15 15:14:54.704', 641, 'EXECUTED', '7:2509d551fbd3bdec20aa4c961c0bda9b', 'sql', 'Populate item_id with values', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-05-27-5095-5', 'kkrumlian', 'migration/3.0/2010-05-27-5095.xml', '2016-06-15 15:14:54.712', 642, 'EXECUTED', '7:702c5bad2a579c7650fa747c6dd33a81', 'addColumn', 'Add item_group_id column to rule_set', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-05-27-5095-6', 'kkrumlian', 'migration/3.0/2010-05-27-5095.xml', '2016-06-15 15:14:54.721', 643, 'EXECUTED', '7:a37eb9a2e944bfcd5438be11dc7759dc', 'sql', 'Populate item_group_id with values', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-07-16-NA01-1', 'kkrumlian', 'migration/3.0/2010-07-16-NA01.xml', '2016-06-15 15:14:54.729', 644, 'EXECUTED', '7:5b2a335aa191d912a583fbf92f4d0998', 'addColumn', 'add study_id to rule table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-07-16-NA01-2', 'kkrumlian', 'migration/3.0/2010-07-16-NA01.xml', '2016-06-15 15:14:54.738', 645, 'EXECUTED', '7:d2d90a68feeb0475ba8eb0403811a85f', 'sql', 'update study_id to rule table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-07-16-NA01-3', 'kkrumlian', 'migration/3.0/2010-07-16-NA01.xml', '2016-06-15 15:14:54.747', 646, 'EXECUTED', '7:9bf290d5555118fe37a98a24d6dfe5c2', 'sql', 'update study_id to rule table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-07-30-5255-1', 'thickerson', 'migration/3.0/2010-07-30-5255.xml', '2016-06-15 15:14:54.755', 647, 'EXECUTED', '7:3d3077eb92e0236923e1bba84d815fbc', 'sql', 'updating ordinals in study event definition table to remove duplicates', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-09-22-5926-1', 'kkrumlian', 'migration/3.0/2010-09-22-5926.xml', '2016-06-15 15:14:54.772', 648, 'EXECUTED', '7:ebac914541c77586e4046ff52e36a246', 'sql', 'fix a bug introduced in 3.0.4 that allowed Pdate to store dd-MMM-yyyy instead of mm/dd/yyyy', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-12-10-HT01-1', 'hendra', 'migration/3.0/2010-12-10-HT01.xml', '2016-06-15 15:14:54.786', 649, 'EXECUTED', '7:ca7a570839542fb47e19b2baa615b79a', 'createTable', 'Create a table named password', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-09-24-SQ01-1', 'skirpichenok', 'migration/3.0/2012-09-24-SQ01.xml', '2016-06-15 15:14:54.797', 650, 'EXECUTED', '7:d53eac1373339af09bb63390dcc9f5aa', 'createSequence', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-05-SK02-01', 'skirpichenok', 'migration/3.0/2012-10-05-SK01.xml', '2016-06-15 15:14:54.807', 651, 'EXECUTED', '7:9a5d5a58f0745bcec872e2101a057c64', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-05-SK02-01', 'skirpichenok', 'migration/3.0/2012-10-05-SK02.xml', '2016-06-15 15:14:54.816', 652, 'MARK_RAN', '7:eb3aee0d51f45eceebde978e973fcf84', 'delete, update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-08-PL01-01', 'plukashenka', 'migration/3.0/2012-10-08-PL01.xml', '2016-06-15 15:14:54.954', 653, 'EXECUTED', '7:73e5312128db318c9c12e0de839a18d3', 'update (x33)', 'Change real messages with keys', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-05-SK03-01', 'skirpichenok', 'migration/3.0/2012-10-05-SK03.xml', '2016-06-15 15:14:54.966', 654, 'EXECUTED', '7:51aef33eee8b1a748c04b9b05d19eea3', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-05-SK03-02', 'skirpichenok', 'migration/3.0/2012-10-05-SK03.xml', '2016-06-15 15:14:54.976', 655, 'EXECUTED', '7:48fc8a8f8988d4a5547324d59f54dd2b', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-05-SK03-03', 'skirpichenok', 'migration/3.0/2012-10-05-SK03.xml', '2016-06-15 15:14:54.987', 656, 'EXECUTED', '7:4974812a4bea154db7bfb4d28c95623a', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-05-SK03-04', 'skirpichenok', 'migration/3.0/2012-10-05-SK03.xml', '2016-06-15 15:14:54.996', 657, 'EXECUTED', '7:69e4e938306c22bd09d4aaee5d4662d7', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-05-SK03-05', 'skirpichenok', 'migration/3.0/2012-10-05-SK03.xml', '2016-06-15 15:14:55.005', 658, 'EXECUTED', '7:702401a5a9e72e71cc9fac21abc8b9d8', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-05-SK03-06', 'skirpichenok', 'migration/3.0/2012-10-05-SK03.xml', '2016-06-15 15:14:55.015', 659, 'EXECUTED', '7:e6a28a773868cd19d0fb9eb6e0b5a943', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-05-SK03-07', 'skirpichenok', 'migration/3.0/2012-10-05-SK03.xml', '2016-06-15 15:14:55.023', 660, 'EXECUTED', '7:8eaa08833c1c5f6b3c5e82de0ba69124', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-05-SK03-08', 'skirpichenok', 'migration/3.0/2012-10-05-SK03.xml', '2016-06-15 15:14:55.035', 661, 'EXECUTED', '7:e9a37d1a47b62a57d2b31294fc50b4f5', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-05-SK03-09', 'skirpichenok', 'migration/3.0/2012-10-05-SK03.xml', '2016-06-15 15:14:55.049', 662, 'EXECUTED', '7:d69c214b4f1a6ae7b1b30cf3d7f3971a', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-05-SK04-01', 'skirpichenok', 'migration/3.0/2012-10-05-SK04.xml', '2016-06-15 15:14:55.068', 663, 'EXECUTED', '7:399fd299a2808ddfa8306284072175bc', 'insert (x2)', 'Insert a new record in the studyParameter table
Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-05-SK05-01', 'skirpichenok', 'migration/3.0/2012-10-05-SK05.xml', '2016-06-15 15:14:55.081', 664, 'EXECUTED', '7:10cc5ce7a52e189e1d654d167cd0288e', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-16-SK06-01', 'skirpichenok', 'migration/3.0/2012-10-16-SK06.xml', '2016-06-15 15:14:55.101', 665, 'EXECUTED', '7:98494da6255a34b97d4a0edb67c5f11b', 'addColumn', 'Add not_started property to event_crf table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-17-SK07-01', 'skirpichenok', 'migration/3.0/2012-10-17-SK07.xml', '2016-06-15 15:14:55.113', 666, 'EXECUTED', '7:c98176cec8e1e45fac19fef5bfd7dc9b', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-1', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.123', 667, 'EXECUTED', '7:1d9a639a5141b02bc8bc2d120da4279f', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-2', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.134', 668, 'EXECUTED', '7:a3c8e536483ff5250f0c9d9d0ea93cd0', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-3', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.148', 669, 'EXECUTED', '7:f916e881c69872f18f4c765dfeee4d01', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-4', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.159', 670, 'EXECUTED', '7:baaaae111a6152aaf754e8bfefda2643', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-5', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.169', 671, 'EXECUTED', '7:81c98b0dd865fe61762be5a647bc8e48', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-6', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.179', 672, 'EXECUTED', '7:a25a607511fb5b23b432d05fbed2a411', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-7', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.189', 673, 'EXECUTED', '7:83afb5abee8d584075c527ad82c98f55', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-8', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.199', 674, 'EXECUTED', '7:a2c1063665d93e8680119f4289637732', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-9', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.209', 675, 'EXECUTED', '7:49052061ef0a1008f9c5b4c0964feec5', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-10', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.219', 676, 'EXECUTED', '7:217fc0f876ddc089a14a75abf094b837', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-11', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.229', 677, 'EXECUTED', '7:b192af90351997b58b9694214f527c9d', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-12', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.241', 678, 'EXECUTED', '7:635b10bf9605a38b3973f67dcf633660', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-13', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.254', 679, 'EXECUTED', '7:acef34a6068a4cdefbb842a1697f30bd', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-14', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.266', 680, 'EXECUTED', '7:4ef5005fa70add322decb897acdc41c6', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-15', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.276', 681, 'EXECUTED', '7:66094873dbaee10ce526340184596feb', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-16', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.286', 682, 'EXECUTED', '7:86dc42f6ee712d0d8b598075a6eb7b2e', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-17', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.297', 683, 'EXECUTED', '7:846c2e4420789494e3831af91d305c2c', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-18', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.307', 684, 'EXECUTED', '7:23b128756989bcde6c427e86bbadcc59', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-19', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.317', 685, 'EXECUTED', '7:c8717bc322fdb39426fb9a2146b74433', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-20', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.328', 686, 'EXECUTED', '7:58b458059d98dbb0f2ce5f0f144d562c', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-21', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.339', 687, 'EXECUTED', '7:cd6eef876b0353f292cfcd5f6651c446', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-22', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.349', 688, 'EXECUTED', '7:7d07892a9b785a78913e7ed2f043505b', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-23', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.361', 689, 'EXECUTED', '7:a05dd10263275f5751278c7f03c28460', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-24', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.372', 690, 'EXECUTED', '7:4847190093467d23dad8358713cf36b7', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-25', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.383', 691, 'EXECUTED', '7:2efa3c298456afa46833a9c90739d2ee', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-26', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.393', 692, 'EXECUTED', '7:da9b2f293b5d0ce995e02c14fa081b01', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-27', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.403', 693, 'EXECUTED', '7:c99213cc194199a3eb694d64ebeb2582', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-28', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.413', 694, 'EXECUTED', '7:ae1dcc943eae12d493a38c71f2929b52', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-29', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.423', 695, 'EXECUTED', '7:6b235241a7e00ad47c2f87d54e6bf311', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-30', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.434', 696, 'EXECUTED', '7:f8023ab7c8b31343ef70d7bd169ab4c6', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-31', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.444', 697, 'EXECUTED', '7:dde8a7b8dcb3d12690e87077a87e4ddb', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-32', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.455', 698, 'EXECUTED', '7:4319b9be4e19292c15ef72cbaf93815d', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-33', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.465', 699, 'EXECUTED', '7:87242295b50b8a8d7ba775a10ae6213d', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-34', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.476', 700, 'EXECUTED', '7:1bb664cc8fbfd5c8aadc43d0acee6c17', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-35', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.488', 701, 'EXECUTED', '7:0abecb0ebc71da8a2ed0ddfcd5fac17e', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-36', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.497', 702, 'EXECUTED', '7:f5c87c3234fb5a129cab6270d60a9651', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-37', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.507', 703, 'EXECUTED', '7:13a77168a39f27b5ecddff5ef595e2be', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-38', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.519', 704, 'EXECUTED', '7:11907b4e6088b8d7d17dbc231cc362f6', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-39', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.531', 705, 'EXECUTED', '7:9e9b093643c75a5bec22cb94b87497f8', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-40', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.541', 706, 'EXECUTED', '7:39b03b03106e86ebbb117c464f7a1db6', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-41', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.553', 707, 'EXECUTED', '7:373e7e153c179a6532fe725dfa645043', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-42', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.563', 708, 'EXECUTED', '7:a1a11dc0624232178f464426fc3ba501', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-43', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.575', 709, 'EXECUTED', '7:4118b5f16fd36c6341433a15d9ab05f9', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-44', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.585', 710, 'EXECUTED', '7:756ce05e3cc1f6958643febf43fe217f', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-45', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.595', 711, 'EXECUTED', '7:34e70d62218c5cb5430e739ecc09c494', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-46', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.605', 712, 'EXECUTED', '7:16b38b89a4491cffe0b7df14b7801712', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-47', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.616', 713, 'EXECUTED', '7:88d28b43147a191c1d194cd899726b3a', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-48', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.627', 714, 'EXECUTED', '7:74e31380452afb60bb67ef9a78b82de8', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-49', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.637', 715, 'EXECUTED', '7:347149e9c21e61bacdb37e2812231215', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-50', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.648', 716, 'EXECUTED', '7:964e71d3a51abc8e3df4f90ab075c6e9', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-51', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.66', 717, 'EXECUTED', '7:b46b5b12a4dcb4485267eee362c00b10', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-52', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.67', 718, 'EXECUTED', '7:ef8a86a701b31f0e106fb3cc2443871f', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-53', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.68', 719, 'EXECUTED', '7:3aa9f31758e424d73cc2f195243fa440', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-54', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.69', 720, 'EXECUTED', '7:d0413630cd2091a38675d6ffab8c1d8d', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-55', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.7', 721, 'EXECUTED', '7:8d70a04f5aca87593c884f4d358d59a5', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-56', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.711', 722, 'EXECUTED', '7:911a14c8a66e13b0658792ac53f58325', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-57', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.721', 723, 'EXECUTED', '7:358fc32336e0d91e2e77b1c43e84e03e', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-58', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.731', 724, 'EXECUTED', '7:1265e0e53a3f1b111a2750e85c699d60', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-59', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.741', 725, 'EXECUTED', '7:9ba2070b223d31ec0e541ce10c7caa2d', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-60', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.753', 726, 'EXECUTED', '7:23b6bf5895a8eeafb024030a59ff48c9', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-61', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.765', 727, 'EXECUTED', '7:88960622f5b8901e5d3bdad37421c045', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-62', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.775', 728, 'EXECUTED', '7:281d7c3aba2d131b80743bc749dedc19', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-63', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.785', 729, 'EXECUTED', '7:a55b4d0b7aa42bad4365fe2be32451aa', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-64', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.796', 730, 'EXECUTED', '7:2fdca36a3482d0d79f5c66eb98499a37', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-65', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.807', 731, 'EXECUTED', '7:cee7be0478e54cf951c24fa276ae7142', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-66', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.817', 732, 'EXECUTED', '7:6da43d2b5cfd1f68c3d04cf8f832bf4a', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-67', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.828', 733, 'EXECUTED', '7:63334d9628adff06509cb895d25fe66a', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-68', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.838', 734, 'EXECUTED', '7:949fb79d5e115a7fee568c82720058ae', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-69', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.849', 735, 'EXECUTED', '7:0c7c5abce79c26ed6cb966ae106a02d4', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-70', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.861', 736, 'EXECUTED', '7:4fde0a80ad08f91e4a13562fcb16d3be', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-71', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.871', 737, 'EXECUTED', '7:32b47f32a3b416ac704047e2937e5b50', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-72', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.881', 738, 'EXECUTED', '7:77ee215bb3c391169c383cc3b529352b', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-73', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.893', 739, 'EXECUTED', '7:92137a9cc26fd652305b0779d33817e0', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-74', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.903', 740, 'EXECUTED', '7:2b32044879c5ee0d6c13b35bc7c46cde', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-75', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.913', 741, 'EXECUTED', '7:19e9512df20561142438beead66f07f2', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-76', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.924', 742, 'EXECUTED', '7:061878ef7ffe784f747f4408e787ac6e', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-77', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.934', 743, 'EXECUTED', '7:55294d7bde0aa730ae6071188e684bc3', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-78', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.946', 744, 'EXECUTED', '7:5529012def697e2fc07fae819339c472', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-79', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.956', 745, 'EXECUTED', '7:148eee465e8df2b1bd923948445b6a90', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-80', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.966', 746, 'EXECUTED', '7:dabb3b9f86d36d68b8ecb1a6e0caf593', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-81', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.976', 747, 'EXECUTED', '7:371f89ceba41497427d9e97e69c3e247', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-82', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.988', 748, 'EXECUTED', '7:2a964c87fb37005906720e143dcf6195', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-83', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:55.998', 749, 'EXECUTED', '7:ddef3a4c93b9491c346dcd24414c8d02', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-84', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.008', 750, 'EXECUTED', '7:1bf3662e28bdbea07b3d0f834e489917', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-85', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.019', 751, 'EXECUTED', '7:c57f1bd3b1fd72d0c7335d5f482a233a', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-86', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.029', 752, 'EXECUTED', '7:6c4f5f7efa50d4e13835277fe4d31ae9', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-87', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.041', 753, 'EXECUTED', '7:9c1ef55330c7ff0c11f2cd13e8bc0330', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-88', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.052', 754, 'EXECUTED', '7:7ba7a8549146d5bbc94531547c03cd68', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-89', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.064', 755, 'EXECUTED', '7:40c5f3aa346a0e030ad688eba5e9f89c', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-90', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.075', 756, 'EXECUTED', '7:69a1c00e82c07298c9a301bd3ca4bbaf', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-91', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.086', 757, 'EXECUTED', '7:3931cdf7c1e92345df77270b2e8d4d1c', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-92', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.096', 758, 'EXECUTED', '7:d48d1d3baae759c7c849995893904551', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-93', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.106', 759, 'EXECUTED', '7:40ccd2fa10298700e4df481e66ca34d9', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-94', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.117', 760, 'EXECUTED', '7:3bd7eac1e226bdcbb717561620a103bc', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-95', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.129', 761, 'EXECUTED', '7:b4ba5dfd5af79dcfc6e8c345f80ae57f', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-96', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.139', 762, 'EXECUTED', '7:062af47b2c631dd630d5aae5f3307af3', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-97', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.149', 763, 'EXECUTED', '7:58954a426ed37535f11f1b9adcbff2c3', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-98', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.16', 764, 'EXECUTED', '7:c9c8fdda1a5b46667d379fa1d0130b0f', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-99', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.169', 765, 'EXECUTED', '7:60bcf2e2b4c6299978d97194329568e1', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-100', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.181', 766, 'EXECUTED', '7:802d02bcba69ce321882752659354547', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-101', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.19', 767, 'EXECUTED', '7:0995805bf482f5eec76e44af6f3e1ea5', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-102', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.201', 768, 'EXECUTED', '7:2dfd4d3fc18c9674c4fc28b946793900', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-103', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.211', 769, 'EXECUTED', '7:3ec19d3bc729617758485f18ccc88927', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-104', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.222', 770, 'EXECUTED', '7:6f43b16eaf76518a7207a09d45bb104e', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-105', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.233', 771, 'EXECUTED', '7:9c832bdbbd4a747dabdf3169467be95f', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-106', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.243', 772, 'EXECUTED', '7:bd50c0b231e2326c045e8533061a02a6', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-107', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.254', 773, 'EXECUTED', '7:7b7e626f224ec50e765c75594e7d9806', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-108', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.267', 774, 'EXECUTED', '7:f711566dc73815c591fe44b5ab656987', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-109', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.277', 775, 'EXECUTED', '7:8f63a2b57542ce4d9638cef750175577', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-110', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.287', 776, 'EXECUTED', '7:d3e83a5f478173196b749f21321bd2e4', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-111', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.298', 777, 'EXECUTED', '7:8a9c6961da52dbef29ce23f16605e49a', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-112', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.308', 778, 'EXECUTED', '7:e0c78ab389d680a3e18bb3f71be32436', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-113', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.319', 779, 'EXECUTED', '7:b8f2859e736cf2e6df3738f49ab57da4', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-114', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.331', 780, 'EXECUTED', '7:59c47a088febaa971813d9ae82094ca8', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-115', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.34', 781, 'EXECUTED', '7:3af1f07738e7b16c7c79c7c653708a2b', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-116', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.35', 782, 'EXECUTED', '7:50eb67a3b56b9bb275a8088b70be80b1', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-117', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.363', 783, 'EXECUTED', '7:b55e518925ef7aa672aec2025a998bc9', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-118', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.374', 784, 'EXECUTED', '7:9b97ccfcd9f32ae04fd1efd91f9dde14', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-119', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.385', 785, 'EXECUTED', '7:6695d6f3b5e255deb712284df9b2fa5a', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-120', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.396', 786, 'EXECUTED', '7:a234b683f76babbd27352bf40a457ea5', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-121', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.407', 787, 'EXECUTED', '7:3af056bf9ca25e7f5d7157e4d17d85e3', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-122', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.418', 788, 'EXECUTED', '7:fed899328f6fa0ddc0311ce4190c8c16', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-123', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.429', 789, 'EXECUTED', '7:88cf3441906999bb202b461e7b73021d', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-124', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.439', 790, 'EXECUTED', '7:8f0e49f6bf034799c159d92f372c6618', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-125', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.45', 791, 'EXECUTED', '7:e61644ebfcfc5c1ebe0f6203986e68ec', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-126', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.462', 792, 'EXECUTED', '7:b18c9f93a81b3ba0a53822901b6eea8a', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-127', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.472', 793, 'EXECUTED', '7:e95e96d8d88be6e6e77f5a2fa3c35612', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-128', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.485', 794, 'EXECUTED', '7:88ae8f09fd7aa1f086600acea689f45f', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-08-8840-129', 'pgawade', 'migration/amethyst/changeLogCreateIndexes.xml', '2016-06-15 15:14:56.495', 795, 'EXECUTED', '7:e194b4d61808fb575f6dfc00d534d310', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-01-13-4575-1', 'kkrumlian', 'migration/amethyst/2010-01-13-4575.xml', '2016-06-15 15:14:56.504', 796, 'EXECUTED', '7:f937ae31d2c9cb5d6b9e030a5b1f90ce', 'addColumn', 'update rule_action add oids column', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-01-13-4575-2', 'kkrumlian', 'migration/amethyst/2010-01-13-4575.xml', '2016-06-15 15:14:56.517', 797, 'EXECUTED', '7:c8ead5b94df0033fba5363e3a0f31df1', 'createTable', 'Create a table named audit_user_login', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-01-13-4575-4', 'kkrumlian', 'migration/amethyst/2010-01-13-4575.xml', '2016-06-15 15:14:56.526', 798, 'EXECUTED', '7:631eb4b5c7c751272c28033c33d82ead', 'addColumn', 'update rule_action add rule_action_run_id column', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-01-13-4575-5', 'kkrumlian', 'migration/amethyst/2010-01-13-4575.xml', '2016-06-15 15:14:56.54', 799, 'EXECUTED', '7:f9f24742024bd726bc8c08653db1955b', 'createTable', 'Create a table named rule_action_property', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-01-13-4575-7', 'kkrumlian', 'migration/amethyst/2010-01-13-4575.xml', '2016-06-15 15:14:56.551', 800, 'EXECUTED', '7:0270a0c3ee9e83fa8776ab31711c2e17', 'dropColumn', 'update rule_action drop oids column', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-01-13-4575-8', 'kkrumlian', 'migration/amethyst/2010-01-13-4575.xml', '2016-06-15 15:14:56.566', 801, 'EXECUTED', '7:bd183d74e6f2e1c81d5ff105b1b8ee56', 'createTable', 'Create a table named rule_action_run_log', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-01-13-4575-10', 'kkrumlian', 'migration/amethyst/2010-01-13-4575.xml', '2016-06-15 15:14:56.579', 802, 'EXECUTED', '7:d276a4369f47a22db91a889a0020071b', 'dropColumn, addColumn', 'Drop/Add Column', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-03-01-4575-1', 'kkrumlian', 'migration/amethyst/2011-03-01-4575.xml', '2016-06-15 15:14:56.588', 803, 'EXECUTED', '7:6ada91ac4581ea8a67a7cbeba09d9ccf', 'sql', 'Add a row to rule_action_run and update rule_action row', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-02-03-4593-1', 'thickerson', 'migration/amethyst/2010-02-03-4593.xml', '2016-06-15 15:14:56.61', 804, 'EXECUTED', '7:96f64e3ee8dabd82e72a62c6b7eda48a', 'addColumn', 'Add a column to item_form_metadata', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-02-03-4593-2', 'thickerson', 'migration/amethyst/2010-02-03-4593.xml', '2016-06-15 15:14:56.629', 805, 'EXECUTED', '7:3f541b64d8fa69ec8add86c794a7f405', 'addColumn', 'Add a column to item_group_metadata', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-03-03-4618-1', 'thickerson', 'migration/amethyst/2010-03-03-4618.xml', '2016-06-15 15:14:56.643', 806, 'EXECUTED', '7:9782fa2423edf9461aa9d9079fd25c67', 'createTable', 'Create a table named dynamics_item_form_metadata', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-03-03-4618-2', 'thickerson', 'migration/amethyst/2010-03-03-4618.xml', '2016-06-15 15:14:56.659', 807, 'EXECUTED', '7:15396c68e6a76583248f855b63bea714', 'createTable', 'Create a table named dynamics_item_group_metadata', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-03-30-4618-1', 'thickerson', 'migration/amethyst/2010-03-30-4618.xml', '2016-06-15 15:14:56.669', 808, 'EXECUTED', '7:b80e56b286d5c572c7967c00f6eb1ae4', 'addColumn', 'Add a column to dyn_item_form_metadata', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-03-30-4618-2', 'thickerson', 'migration/amethyst/2010-03-30-4618.xml', '2016-06-15 15:14:56.681', 809, 'EXECUTED', '7:c980bcc31e0cb146f2ea1dbb329a6ecf', 'addColumn', 'Add a column to dyn_item_form_metadata: PASSED DDE', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-03-30-4618-3', 'thickerson', 'migration/amethyst/2010-03-30-4618.xml', '2016-06-15 15:14:56.697', 810, 'EXECUTED', '7:0c21b232a5cc2774283a881147862774', 'addColumn', 'Add a column to dyn_item_group_metadata: PASSED DDE', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-05-27-5095-1', 'kkrumlian', 'migration/amethyst/2010-05-27-5095.xml', '2016-06-15 15:14:56.707', 811, 'EXECUTED', '7:6eccb7cc314464ab3b782cbfa643b474', 'sql', 'Change RuleSet so study event definition id can be null', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-07-01-5245-1', 'ahamid', 'migration/amethyst/2010-07-01-5245.xml', '2016-06-15 15:14:56.719', 812, 'EXECUTED', '7:646f32b81796ecf62884cf019c418b2d', 'sql', 'CREATE A VIEW', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-07-01-5245-3', 'ahamid', 'migration/amethyst/2010-07-01-5245.xml', '2016-06-15 15:14:56.728', 813, 'EXECUTED', '7:56708c8c74c8f22ec386eadfbd47ef92', 'sql', 'RECREATE THE VIEW WITH SOME NEW CONDITION', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-05-9305-1', 'ahamid', 'migration/amethyst/2011-05-28-9305.xml', '2016-06-15 15:14:56.741', 814, 'MARK_RAN', '7:c332025b56abfbc2a3457e61ce670c8d', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-05-9305-2', 'ahamid', 'migration/amethyst/2011-05-28-9305.xml', '2016-06-15 15:14:56.75', 815, 'MARK_RAN', '7:5e3878208f8d028bf462543d3f72d173', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-07-07-5429-1', 'thickerson', 'migration/amethyst/2010-07-07-5429.xml', '2016-06-15 15:14:56.758', 816, 'EXECUTED', '7:d71d25508dedd5cd96ad9df10f1734eb', 'sql', 'Change oc_qrtz_job_details class names for EXPORT DATA JOBS', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-07-07-5429-2', 'thickerson', 'migration/amethyst/2010-07-07-5429.xml', '2016-06-15 15:14:56.766', 817, 'EXECUTED', '7:c6fb35669d6920975f9334a8f55a32d2', 'sql', 'Change oc_qrtz_job_details class names for IMPORT DATA JOBS', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-07-14-5265-1', 'ahamid', 'migration/amethyst/2010-07-14-5265.xml', '2016-06-15 15:14:56.788', 818, 'EXECUTED', '7:f989439726bd4c3aef8214129a9acae9', 'addColumn', 'item_group_metadata (repeating_group)', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-08-19-NA01-1', 'kkrumlian', 'migration/amethyst/2010-08-19-NA01.xml', '2016-06-15 15:14:56.807', 819, 'EXECUTED', '7:724c0de8d35ad44571efd25ad504899d', 'addColumn', 'add run webservices column to user_account table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-05-27-5724', 'jamuna', 'migration/amethyst/2010-08-24-5724.xml', '2016-06-15 15:14:56.816', 820, 'EXECUTED', '7:1c84272b610171839120ba0901d79a41', 'dropView', 'Dropping the view before changing the  date type', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-05-27-5724-0', 'jamuna', 'migration/amethyst/2010-08-24-5724.xml', '2016-06-15 15:14:56.84', 821, 'EXECUTED', '7:6f419930d1fc7f6e54f02dc9f5dfa737', 'sql', 'Change data type of created_date to timestamp', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-05-27-5724-2', 'jamuna', 'migration/amethyst/2010-08-24-5724.xml', '2016-06-15 15:14:56.867', 822, 'EXECUTED', '7:6f419930d1fc7f6e54f02dc9f5dfa737', 'sql', 'Change data type of created_date to timestamp', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-05-27-5724-1', 'jamuna', 'migration/amethyst/2010-08-24-5724.xml', '2016-06-15 15:14:56.878', 823, 'EXECUTED', '7:3a95c7bf711153d098b904f27548c5d2', 'createView', 'recreating the view back', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-05-27-5724-3', 'jamuna', 'migration/amethyst/2010-08-24-5724.xml', '2016-06-15 15:14:56.888', 824, 'EXECUTED', '7:1c84272b610171839120ba0901d79a41', 'dropView', 'Dropping the view before changing the  date type', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-05-27-5724-4', 'jamuna', 'migration/amethyst/2010-08-24-5724.xml', '2016-06-15 15:14:56.897', 825, 'EXECUTED', '7:3a95c7bf711153d098b904f27548c5d2', 'createView', 'recreating the view back', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-08-26-5724', 'jamuna', 'migration/amethyst/2010-08-26-5724.xml', '2016-06-15 15:14:56.905', 826, 'EXECUTED', '7:1c84272b610171839120ba0901d79a41', 'dropView', 'Dropping the view before changing the  date type', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-08-26-5724-2', 'jamuna', 'migration/amethyst/2010-08-26-5724.xml', '2016-06-15 15:14:56.929', 827, 'EXECUTED', '7:85c9a8f844d574f6d42eebcc8a95f551', 'sql', 'Change data type of created_date back to date', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-08-26-5724-1', 'jamuna', 'migration/amethyst/2010-08-26-5724.xml', '2016-06-15 15:14:56.939', 828, 'EXECUTED', '7:3a95c7bf711153d098b904f27548c5d2', 'createView', 'recreating the view back', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-08-26-5724-3', 'jamuna', 'migration/amethyst/2010-08-26-5724.xml', '2016-06-15 15:14:56.947', 829, 'EXECUTED', '7:1c84272b610171839120ba0901d79a41', 'dropView', 'Dropping the view again to create the view without owner name', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-08-26-5724-4', 'jamuna', 'migration/amethyst/2010-08-26-5724.xml', '2016-06-15 15:14:56.957', 830, 'EXECUTED', '7:3a95c7bf711153d098b904f27548c5d2', 'createView', 'recreating the view back', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-08-26-5237-1', 'ahamid', 'migration/amethyst/2010-08-26-5732.xml', '2016-06-15 15:14:56.966', 831, 'EXECUTED', '7:28325888cc66fb90e2697c33895fe612', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-08-26-5237-2', 'ahamid', 'migration/amethyst/2010-08-26-5732.xml', '2016-06-15 15:14:56.976', 832, 'EXECUTED', '7:adda03c9a7b5d459f9986621f54bdf40', 'sql', 'Change study_parameter values', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-08-26-5237-3', 'ahamid', 'migration/amethyst/2010-08-26-5732.xml', '2016-06-15 15:14:56.985', 833, 'EXECUTED', '7:a42545ff3d5bfeec430a78f074850b7a', 'sql', 'Change study_parameter values', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-10-05-5556-1', 'thickerson', 'migration/amethyst/2010-10-05-5556.xml', '2016-06-15 15:14:57.001', 834, 'EXECUTED', '7:59b491b1db982bf05242b637dabfce45', 'sql', 'Change data type of created_date to timestamp', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-10-11-5962-1', 'ywang', 'migration/amethyst/2010-10-11-5962.xml', '2016-06-15 15:14:57.009', 835, 'EXECUTED', '7:0ded8b5b1e6e69a125a8701df5f2dc39', 'sql', 'Update Resolution Status Ids of Parent DNs As Their Last Child', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-10-11-5962-3', 'ywang', 'migration/amethyst/2010-10-11-5962.xml', '2016-06-15 15:14:57.021', 836, 'EXECUTED', '7:bfd07ff6b497a0e71b26f46ffea5f921', 'sql', 'Create or Replace a View DN_DAYS', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-11-24-6084-1-1', 'kkrumlian', 'migration/amethyst/2010-11-24-6084.xml', '2016-06-15 15:14:57.03', 837, 'EXECUTED', '7:e02dcf6862571820790a2011bf9a24c8', 'sql', 'Disable item_data triggers', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-11-24-6084-1', 'kkrumlian', 'migration/amethyst/2010-11-24-6084.xml', '2016-06-15 15:14:57.039', 838, 'EXECUTED', '7:6fbcdbe32e18b930e6a0a353048a7da6', 'sql', 'Change date format for dataype date,pdate (MM/dd/YYYY) to ISO-8601', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-11-24-6084-3', 'kkrumlian', 'migration/amethyst/2010-11-24-6084.xml', '2016-06-15 15:14:57.059', 839, 'EXECUTED', '7:d0f753708f13bce8837eb01fe21300c8', 'sql', 'Change date format for dataype pdate (MMM-YYYY) to ISO-8601', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-11-24-6084-5', 'kkrumlian', 'migration/amethyst/2010-11-24-6084.xml', '2016-06-15 15:14:57.08', 840, 'EXECUTED', '7:c5fd0e0ee6a4f4041c724357d1260a36', 'sql', 'Disable item_data triggers', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-12-02-6421-1', 'ahamid', 'migration/amethyst/2010-12-02-6421.xml', '2016-06-15 15:14:57.089', 841, 'EXECUTED', '7:c3e97b058678106408f6198f1d3e46c3', 'renameColumn', 'Rename Column Name', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-12-02-6421-3', 'ahamid', 'migration/amethyst/2010-12-02-6421.xml', '2016-06-15 15:14:57.097', 842, 'EXECUTED', '7:6032adb3a8ab5985670cc340174fb302', 'sql', 'Update null values', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-12-02-6421-5', 'ahamid', 'migration/amethyst/2010-12-02-6421.xml', '2016-06-15 15:14:57.105', 843, 'EXECUTED', '7:a3e66ca581986c2a146218f97e701c42', 'sql', 'Update null values', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-12-17-NA01-1', 'kkrumlian', 'migration/amethyst/2010-12-17-NA01.xml', '2016-06-15 15:14:57.114', 844, 'EXECUTED', '7:19ef8efc7793c925f939c8d133dd962c', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-12-17-NA01-2', 'pgawade (generated)', 'migration/amethyst/2010-12-17-NA01.xml', '2016-06-15 15:14:57.122', 845, 'MARK_RAN', '7:bdc0551f70f81b98bfba4b14f08c76a2', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-12-17-NA01-3', 'pgawade (generated)', 'migration/amethyst/2010-12-17-NA01.xml', '2016-06-15 15:14:57.131', 846, 'EXECUTED', '7:65b29b09700c923106ab8dd0753a3eee', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-02-07-7181-1', 'ywang', 'migration/amethyst/2011-02-07-7181.xml', '2016-06-15 15:14:57.15', 847, 'EXECUTED', '7:aa1bc978c537ec5f2258fa62f4614ded', 'createTable', 'Create table scd_item_metadata', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-02-07-7181-4', 'ywang', 'migration/amethyst/2011-02-07-7181.xml', '2016-06-15 15:14:57.162', 848, 'EXECUTED', '7:4416aee8e095cf56cc0210a3d7275d57', 'addForeignKeyConstraint', 'Add foreign key for scd_item_metadata', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-02-07-7181-5', 'ywang', 'migration/amethyst/2011-02-07-7181.xml', '2016-06-15 15:14:57.172', 849, 'EXECUTED', '7:aa5007d0f8d9e9af1f2fb347d357fa9c', 'addForeignKeyConstraint', 'Add foreign key for scd_item_metadata', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-03-01-6567-1', 'ywang', 'migration/amethyst/2011-03-01-6567.xml', '2016-06-15 15:14:57.183', 850, 'EXECUTED', '7:73e6b63ba3116ed3edf323489cb4af02', 'createView', 'create temporary view', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-03-01-6567-3', 'ywang', 'migration/amethyst/2011-03-01-6567.xml', '2016-06-15 15:14:57.191', 851, 'EXECUTED', '7:887fbac4c18e91c0030ade8a3f99827a', 'sql', 'insert into measurement_unit.name and oc_oid', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-03-01-6567-5', 'ywang', 'migration/amethyst/2011-03-01-6567.xml', '2016-06-15 15:14:57.201', 852, 'EXECUTED', '7:f5a7997917ea67dd84de5aaf73a1adc9', 'sql', 'insert into measurement_unit.name and oc_oid', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-03-01-6567-7', 'ywang', 'migration/amethyst/2011-03-01-6567.xml', '2016-06-15 15:14:57.209', 853, 'EXECUTED', '7:8417b17ae37fe1c9856c14322e92163b', 'sql', 'update measurement_unit.oc_oid', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-03-01-6567-9', 'ywang', 'migration/amethyst/2011-03-01-6567.xml', '2016-06-15 15:14:57.217', 854, 'EXECUTED', '7:674d0314f348b16df8b658b976992c1a', 'dropView', 'drop temporary view', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-03-01-7700-1', 'ahamid', 'migration/amethyst/2011-03-01-7700.xml', '2016-06-15 15:14:57.243', 855, 'EXECUTED', '7:c3e619c23a9222605e54e20f8920fe2b', 'addColumn', 'update event_crf add sdv_update_id column', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-03-01-7700-2', 'ahamid', 'migration/amethyst/2011-03-01-7700.xml', '2016-06-15 15:14:57.253', 856, 'EXECUTED', '7:5ececb390d54d3d28848e5c7834243af', 'sql', 'create or replace event_crf_trigger for event crf sdv status', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-03-16-5930-1', 'ahamid', 'migration/amethyst/2011-03-16-5930.xml', '2016-06-15 15:14:57.262', 857, 'EXECUTED', '7:5ea70b7123ad80fc352d0eb67b845854', 'addColumn', 'Item_Data (old_status)', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-03-22-8248-1', 'pgawade', 'migration/amethyst/2011-03-22-8248.xml', '2016-06-15 15:14:57.279', 858, 'EXECUTED', '7:54f3f73e6b3e0667bd25a29fe42ce48d', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-04-05-7475-1', 'jnyayapathi', 'migration/amethyst/2011-04-05-7475.xml', '2016-06-15 15:14:57.288', 859, 'EXECUTED', '7:9a4407b9d9a45d332c1095f7f607b7a6', 'sql', 'Change export data jobs trigger status to complete', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-05-03-9430-3', 'jnyayapathi', 'migration/amethyst/2011-05-03-9430.xml', '2016-06-15 15:14:57.301', 860, 'EXECUTED', '7:4b1566ebf35a191036315d838d508a1b', 'sql', 'Add a constraint when no item data rows are present', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-05-20-9703-1', 'pgawade (generated)', 'migration/amethyst/2011-05-20-9703.xml', '2016-06-15 15:14:57.308', 861, 'MARK_RAN', '7:a3cefd4cb885c99895d0aae518bcc1fa', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-05-20-9703-2', 'jnyayapathi', 'migration/amethyst/2011-05-20-9703.xml', '2016-06-15 15:14:57.315', 862, 'MARK_RAN', '7:ef8f05df05f233fa25e7205313177100', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2010-12-17-NA01-3', 'pgawade (generated)', 'migration/amethyst/2011-05-20-9703.xml', '2016-06-15 15:14:57.327', 863, 'EXECUTED', '7:65b29b09700c923106ab8dd0753a3eee', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-07-27-8874-1', 'ywang', 'migration/amethyst/2011-07-27-8874.xml', '2016-06-15 15:14:57.335', 864, 'EXECUTED', '7:f6951f191889386c0b49287a5049b2d7', 'sql', 'create item_data_initial_trigger()', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-07-27-8874-2', 'ywang', 'migration/amethyst/2011-07-27-8874.xml', '2016-06-15 15:14:57.344', 865, 'EXECUTED', '7:9595678abf0374da7de2e9f41e78ba9f', 'sql', 'create trigger item_data_initial', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-07-27-8874-3', 'ywang', 'migration/amethyst/2011-07-27-8874.xml', '2016-06-15 15:14:57.351', 866, 'MARK_RAN', '7:faed6b08e77e73ad6ba3aca7eabd5d0f', 'sql', 'create trigger item_data_initial', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-07-27-8874-4', 'ywang', 'migration/amethyst/2011-07-27-8874.xml', '2016-06-15 15:14:57.359', 867, 'MARK_RAN', '7:b81193b9bb88db18c03d317d6252956f', 'sql', 'add procedure item_data_initial_trigger', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-07-27-8874-5', 'ywang', 'migration/amethyst/2011-07-27-8874.xml', '2016-06-15 15:14:57.366', 868, 'MARK_RAN', '7:8194efac2be93da9565c44226380d7fb', 'sql', 'create item_data_initial_trigger', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-08-03-10666-1', 'ywang', 'migration/amethyst/2011-08-03-10666.xml', '2016-06-15 15:14:57.375', 869, 'EXECUTED', '7:15f1d0768e13633202d1ccd111ae78b5', 'insert', 'insert new response type', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2011-09-06-10813-2', 'pgawade', 'migration/amethyst/2011-09-06-10813.xml', '2016-06-15 15:14:57.385', 870, 'EXECUTED', '7:c9e98be9658600562f328610285e69ef', 'sql', 'update repeating_group column of item_group_metadata for 3.0 to 3.1 migrations on postgres database', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-22-SK01-01', 'skirpichenok', 'migration/clincaptrue/2012-10-22-SK01.xml', '2016-06-15 15:14:57.418', 871, 'EXECUTED', '7:13a4efe92e5065b3df43b018f9bec09e', 'dropColumn, addColumn', 'Delete / Add not_started property to event_crf table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-10-24-SK02-1', 'skirpichenok', 'migration/clincaptrue/2012-10-24-SK02.xml', '2016-06-15 15:14:57.47', 872, 'EXECUTED', '7:8aa8c7f5b72e281a423b3f798c152152', 'dropSequence, createSequence (x9)', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-11-12-TH01-1', 'thickerson', 'migration/clincaptrue/2012-11-12-TH01.xml', '2016-06-15 15:14:57.493', 873, 'EXECUTED', '7:b7b90bc3780218dcee055096b150059d', 'modifyDataType', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-11-23-TICKET105-0', 'skirpichenok', 'migration/clincaptrue/2012-11-23-TICKET105.xml', '2016-06-15 15:14:57.503', 874, 'EXECUTED', '7:d818d52f61ae969da30e4b88523f83f4', 'sql', 'set field not_started to TRUE for all event_crf that do not have data in the item_data table (it''s needed for prev DB)', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-11-23-TICKET111-4', 'skirpichenok', 'migration/clincaptrue/2012-11-23-TICKET111.xml', '2016-06-15 15:14:57.512', 875, 'MARK_RAN', '7:1ffadb6a5b9d5ab5eb26039766c884b3', 'update (x2)', 'Set new id for the existing record in the audit_log_event_type table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-11-23-TICKET111-5', 'skirpichenok', 'migration/clincaptrue/2012-11-23-TICKET111.xml', '2016-06-15 15:14:57.52', 876, 'MARK_RAN', '7:a473edd35ea81d0ad29e893dca7a3b3c', 'update (x2)', 'Set new id for the existing record in the audit_log_event_type table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-11-23-TICKET111-6', 'skirpichenok', 'migration/clincaptrue/2012-11-23-TICKET111.xml', '2016-06-15 15:14:57.53', 877, 'EXECUTED', '7:4cc4f170e5f9d8a2bc15d3af40d4e770', 'insert', 'Insert a new record into the audit_log_event_type table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-11-23-TICKET111-7', 'skirpichenok', 'migration/clincaptrue/2012-11-23-TICKET111.xml', '2016-06-15 15:14:57.54', 878, 'EXECUTED', '7:17a9686cfa7d4f714f58e225e8dceb70', 'insert', 'Insert a new record into the audit_log_event_type table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2012-11-23-TICKET111-8', 'skirpichenok', 'migration/clincaptrue/2012-11-23-TICKET111.xml', '2016-06-15 15:14:57.551', 879, 'EXECUTED', '7:1b2ee1979109f1cc8a0dff0dd078e71f', 'sql', 'update study_event_trigger()', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-02-SK01-01', 'skirpichenok', 'migration/clincaptrue/2013-01-02-SK01.xml', '2016-06-15 15:14:57.559', 880, 'EXECUTED', '7:f2b51b22cb292e7e14a3e00635d0b2e1', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-03-TICKET120-01', 'skirpichenok', 'migration/clincaptrue/2013-01-03-TICKET120.xml', '2016-06-15 15:14:57.569', 881, 'EXECUTED', '7:fbca3f670d76191f6938b68dd26de321', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-03-TICKET120-02', 'skirpichenok', 'migration/clincaptrue/2013-01-03-TICKET120.xml', '2016-06-15 15:14:57.58', 882, 'EXECUTED', '7:c94f955a9ce61310b92f9f9a587036f6', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-03-TICKET120-03', 'skirpichenok', 'migration/clincaptrue/2013-01-03-TICKET120.xml', '2016-06-15 15:14:57.589', 883, 'EXECUTED', '7:70efa3fb781f89ab6bf4a74a54b0370f', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-03-TICKET120-04', 'skirpichenok', 'migration/clincaptrue/2013-01-03-TICKET120.xml', '2016-06-15 15:14:57.599', 884, 'EXECUTED', '7:05212e7ad032984d2227253a49348697', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-03-TICKET120-5', 'skirpichenok', 'migration/clincaptrue/2013-01-03-TICKET120.xml', '2016-06-15 15:14:57.609', 885, 'EXECUTED', '7:d1759608d5fc466c062a4664031f5b4b', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-03-TICKET120-6', 'skirpichenok', 'migration/clincaptrue/2013-01-03-TICKET120.xml', '2016-06-15 15:14:57.619', 886, 'EXECUTED', '7:34187f79f8686480efca125354357c60', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-03-TICKET120-7', 'skirpichenok', 'migration/clincaptrue/2013-01-03-TICKET120.xml', '2016-06-15 15:14:57.629', 887, 'EXECUTED', '7:2fd14f5758abaa17b16babb5f027f31b', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-03-TICKET120-8', 'skirpichenok', 'migration/clincaptrue/2013-01-03-TICKET120.xml', '2016-06-15 15:14:57.64', 888, 'EXECUTED', '7:27a547ba3c6e3103d4f3b862443b9ac5', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-05-TICKET142-1', 'skirpichenok', 'migration/clincaptrue/2013-01-05-TICKET142.xml', '2016-06-15 15:14:57.648', 889, 'EXECUTED', '7:b72e4ce79cd728e2c409fc435a8332c5', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-25-SK01-01', 'skirpichenok', 'migration/clincaptrue/2013-01-25-SK01.xml', '2016-06-15 15:14:57.657', 890, 'EXECUTED', '7:73001bf0144bbac3834bba48a12accee', 'addColumn', 'Add current_session field to user_account table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-25-SK01-02', 'skirpichenok', 'migration/clincaptrue/2013-01-25-SK01.xml', '2016-06-15 15:14:57.667', 891, 'EXECUTED', '7:40d416baa691bdf4c77cafa6c7fc83d3', 'addColumn', 'Add session_last_activity field to user_account table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-02-05-SK01-01', 'skirpichenok', 'migration/clincaptrue/2013-02-05-SK01.xml', '2016-06-15 15:14:57.689', 892, 'EXECUTED', '7:eb78739828ac07c60684c1693c2ad959', 'modifyDataType', 'Modification of the study_subject.date_created', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-02-06-REPORTING-01', 'amiklushou', 'migration/clincaptrue/2013-02-06-REPORTING.xml', '2016-06-15 15:14:57.701', 893, 'EXECUTED', '7:1ed0a582989c366ccc6f948d8a46b993', 'createView', 'DN count by status view', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-02-06-REPORTING-02', 'amiklushou', 'migration/clincaptrue/2013-02-06-REPORTING.xml', '2016-06-15 15:14:57.713', 894, 'EXECUTED', '7:35a5d5cf7b5d78cd1fafb5a57fbe7d08', 'createView', 'Enrollemnt per month view', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-02-06-REPORTING-03', 'amiklushou', 'migration/clincaptrue/2013-02-06-REPORTING.xml', '2016-06-15 15:14:57.724', 895, 'EXECUTED', '7:13d25991aacaec3e1d229a77828da93f', 'createView', 'Enrollment view', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-03-04-TICKET221-01', 'skirpichenok', 'migration/clincaptrue/2013-03-04-TICKET221.xml', '2016-06-15 15:14:57.782', 896, 'EXECUTED', '7:8bc785d4555e6c64350297a145ffc044', 'addColumn', 'Add was_locked_by property to study_event table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-03-04-TICKET221-02', 'skirpichenok', 'migration/clincaptrue/2013-03-04-TICKET221.xml', '2016-06-15 15:14:57.804', 897, 'EXECUTED', '7:c1526ee17d88de1b982bbea30c2e8a63', 'addColumn', 'Add prev_subject_event_status property to study_event table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-04-08-TICKET70-01', 'igor', 'migration/clincaptrue/2013-04-08-TICKET70.xml', '2016-06-15 15:14:57.836', 898, 'EXECUTED', '7:7992c09ccf6d31c1c300c463dd520a6c', 'modifyDataType (x2)', 'Change datatype of date_created/updated from "date" to "timestamp without time zone"', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-04-14-TICKET109-01', 'skirpichenok', 'migration/clincaptrue/2013-04-14-TICKET109.xml', '2016-06-15 15:14:57.866', 899, 'EXECUTED', '7:48840cff3e66af2821104cf00d31bc45', 'update, insert, createTable, addColumn', 'the update for ticket #109', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-04-26-TICKET114-01', 'igor', 'migration/clincaptrue/2013-04-26-TICKET114.xml', '2016-06-15 15:14:57.876', 900, 'EXECUTED', '7:6fe17a31314e03d54859d96a9bf1e782', 'update', 'Change name "Dynamic Visits" to "Dynamic Group"', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-04-29-TICKET139-01', 'igor', 'migration/clincaptrue/2013-04-29-TICKET139.xml', '2016-06-15 15:14:57.898', 901, 'EXECUTED', '7:61341d2b0d24857b80321ea73f1f7a38', 'addColumn, dropColumn', 'Move isDefault from Dynamic Events to Group Classes', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-04-19-TICKET65', 'vitaly', 'migration/clincaptrue/2013-04-19-TICKET65.xml', '2016-06-15 15:14:57.923', 902, 'EXECUTED', '7:c66b83cc0e74938c92c8382229932938', 'addColumn', 'Create new columns for calendar func', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-05-14-TH01-1', 'thickerson', 'migration/clincaptrue/2013-05-14-TH01.xml', '2016-06-15 15:14:58.022', 903, 'EXECUTED', '7:dacecec14dfc2cfc27c77435fee01e1c', 'dropTable (x13)', 'CLEANUP: dropping tables that are not used at all in the system', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-05-17-TICKET159-01', 'thickerson', 'migration/clincaptrue/2013-05-17-TICKET159.xml', '2016-06-15 15:14:58.038', 904, 'EXECUTED', '7:67db3b20643d92051ba618905f9fc9fc', 'createTable', 'creating a new table to contain discrepancy descriptions for reasons for change', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-05-28-TICKET109-01', 'igor', 'migration/clincaptrue/2013-05-28-TICKET109.xml', '2016-06-15 15:14:58.059', 905, 'EXECUTED', '7:28800a753244e184189420da535ba4ef', 'addColumn', 'Add Group Class Id column', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-03-26-quartz-2.1.7-1', 'skirpichenok', 'migration/clincaptrue/2013-06-07-quartz-2.1.7.xml', '2016-06-15 15:14:58.202', 906, 'EXECUTED', '7:6569266eb630290dc8991902793fd3e3', 'sql', 'Migrate quartz''s DB schema from 1.8.0 to 2.1.7 [http://quartz-scheduler.org/documentation/quartz-2.1.x/migration-guide]', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-06-12-TICKET238-01', 'igor', 'migration/clincaptrue/2013-06-12-TICKET238.xml', '2016-06-15 15:14:58.22', 907, 'EXECUTED', '7:1f7d8b12dab35f0f02d97ffcb5a6b945', 'modifyDataType', 'Change rule_expression.value length from 1025 to 4000', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-06-20-TICKET324', 'vitaly', 'migration/clincaptrue/2013-06-20-TICKET324.xml', '2016-06-15 15:14:58.244', 908, 'EXECUTED', '7:012ac0767203922bea8e0bbfb00e31a0', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-06-28-TICKET347-01', 'skirpichenok', 'migration/clincaptrue/2013-06-28-TICKET347.xml', '2016-06-15 15:14:58.257', 909, 'EXECUTED', '7:37df2210212bc46d4c7214caea9a6878', 'dropColumn, sql', 'Delete column was_locked_by from the study_event table
Replace wrong subject event states to the initial data entry', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-06-27-TICKET255-01', 'skirpichenok', 'migration/clincaptrue/2013-06-28-TICKET255.xml', '2016-06-15 15:14:58.266', 910, 'EXECUTED', '7:3cc9b83eff67bdc7aa8e7a0024aad14d', 'insert', 'Add source_data_verified state into the subject_event_status table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-06-27-TICKET345', 'vitaly', 'migration/clincaptrue/2013-06-27-TICKET345.xml', '2016-06-15 15:14:58.294', 911, 'EXECUTED', '7:bfe75ff354f41d3aaa538a644d4a83f4', 'dropColumn, addColumn', 'New column type in study_event table to track RV', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-06-28-TICKET256-01', 'skirpichenok', 'migration/clincaptrue/2013-06-28-TICKET256.xml', '2016-06-15 15:14:58.304', 912, 'EXECUTED', '7:253044422a90cbf8a5adb159b18967ae', 'insert', 'Add removed state into the subject_event_status table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-06-30-TICKET322', 'igor', 'migration/clincaptrue/2013-06-30-TICKET322.xml', '2016-06-15 15:14:58.32', 913, 'EXECUTED', '7:9ff5e65d266dd59e3ca6fbd686b4fecd', 'addColumn', 'Add Order column', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-07-01-TICKET283-01', 'skirpichenok', 'migration/clincaptrue/2013-07-01-TICKET283.xml', '2016-06-15 15:14:58.375', 914, 'EXECUTED', '7:30211008ee34327fbb67cb7fc5ac3490', 'sql, update (x13)', 'Fix problems with wrong DB data for user roles
Resolve the role names mismatches between UI and the database. We use only the following roles: 1->system administrator, 2->study administrator, 4->investigator, 5->clinical research coordinator, 6->m...', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-07-01-TICKET283-02', 'skirpichenok', 'migration/clincaptrue/2013-07-01-TICKET283.xml', '2016-06-15 15:14:58.387', 915, 'EXECUTED', '7:14888f09c1ff044d6c4189f8af57d62a', 'sql', 'Fix problems with wrong DB data for user roles', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-07-03-TICKET360-1', 'vitaly', 'migration/clincaptrue/2013-07-03-TICKET360.xml', '2016-06-15 15:14:58.414', 916, 'EXECUTED', '7:35da850094d05bfbdcb17446d8cddab2', 'dropColumn, addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-07-10-TICKET293-02', 'skirpichenok', 'migration/clincaptrue/2013-07-10-TICKET293.xml', '2016-06-15 15:14:58.427', 917, 'EXECUTED', '7:f9142905aba7be70937c694f0e8816a6', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-07-10-TICKET293-04', 'skirpichenok', 'migration/clincaptrue/2013-07-10-TICKET293.xml', '2016-06-15 15:14:58.437', 918, 'EXECUTED', '7:568124cc6e458f07c3963b56b0608874', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-07-10-TICKET293-05', 'skirpichenok', 'migration/clincaptrue/2013-07-10-TICKET293.xml', '2016-06-15 15:14:58.449', 919, 'EXECUTED', '7:adb7f4f231b1243eab5b5f9af5bdc109', 'addForeignKeyConstraint', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-07-30-TICKET196-01', 'igor', 'migration/clincaptrue/2013-07-30-TICKET196.xml', '2016-06-15 15:14:58.462', 920, 'EXECUTED', '7:1a10caf45847cc3f3a5c6d0e5c9ae021', 'addColumn, dropColumn', 'Remove column is_site_visible and add column visibility_level', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-07-31-TICKET381-01', 'skirpichenok', 'migration/clincaptrue/2013-07-31-TICKET381.xml', '2016-06-15 15:14:58.472', 921, 'EXECUTED', '7:b6662e8195cb969156ba3fd83b862e46', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-08-06-TICKET405-01', 'skirpichenok', 'migration/clincaptrue/2013-08-06-TICKET405.xml', '2016-06-15 15:14:58.482', 922, 'EXECUTED', '7:d1e2c821414f2ca160caffaa332500b7', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-08-09-TICKET32-01', 'skirpichenok', 'migration/clincaptrue/2013-08-09-TICKET32.xml', '2016-06-15 15:14:58.491', 923, 'EXECUTED', '7:78bd4229c951014758192d7161131b1e', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-08-09-TICKET32-02', 'skirpichenok', 'migration/clincaptrue/2013-08-09-TICKET32.xml', '2016-06-15 15:14:58.499', 924, 'MARK_RAN', '7:68d9049009afd7f5ebfaf4ad7e0eb856', 'update (x2)', 'Set new id for the existing record in the audit_log_event_type table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-08-09-TICKET32-03', 'skirpichenok', 'migration/clincaptrue/2013-08-09-TICKET32.xml', '2016-06-15 15:14:58.51', 925, 'EXECUTED', '7:9238c7e1e0b36db1e5bbf6c4aef00fac', 'insert', 'Insert a new record into the audit_log_event_type table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-09-08-TICKET513-01', 'skirpichenok', 'migration/clincaptrue/2013-09-08-TICKET513.xml', '2016-06-15 15:14:58.52', 926, 'EXECUTED', '7:4a81e683d48263f27b5e01e5b26923f2', 'dropColumn', 'Delete column dynamic_event_id from the study_event table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-05-17-TICKET50', 'markg', 'migration/clincaptrue/2013-08-09-TICKET50.xml', '2016-06-15 15:14:58.535', 927, 'EXECUTED', '7:be9b0e9122608db01465ba52f4968434', 'createTable', 'Creating the dictionary table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-05-17-TICKET50-SYNONYM', 'markg', 'migration/clincaptrue/2013-08-09-TICKET50.xml', '2016-06-15 15:14:58.552', 928, 'EXECUTED', '7:04b7b1493a579f3e49d9cf1828edf3b8', 'createTable', 'Creating the synonym table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-05-17-TICKET50-TERM', 'markg', 'migration/clincaptrue/2013-08-09-TICKET50.xml', '2016-06-15 15:14:58.569', 929, 'EXECUTED', '7:5fd42e97810dc6a86aa2acab5f94c609', 'createTable', 'Creating the term table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-05-17-TICKET50-CODED_ITEM', 'markg', 'migration/clincaptrue/2013-08-09-TICKET50.xml', '2016-06-15 15:14:58.586', 930, 'EXECUTED', '7:daaa6ece5c0074d7eef4cb1706a87bcc', 'createTable', 'Creating the code item table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-09-13-TICKET534', 'markg', 'migration/clincaptrue/2013-09-13-TICKET534.xml', '2016-06-15 15:14:58.595', 931, 'EXECUTED', '7:1b518fc20eba598d607faae7ff722099', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-09-16-TICKET357-01', 'skirpichenok', 'migration/clincaptrue/2013-09-16-TICKET357.xml', '2016-06-15 15:14:58.607', 932, 'EXECUTED', '7:5d48a3eb2e048d87861ab791fc1bf526', 'sql', 'Fix user types for existing users', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-09-16-TICKET357-02', 'skirpichenok', 'migration/clincaptrue/2013-09-16-TICKET357.xml', '2016-06-15 15:14:58.615', 933, 'EXECUTED', '7:99bd92e638846e81b86ba7bc5cc2076e', 'sql', 'Fix user types for existing users', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-09-23-TICKET52', 'markg', 'migration/clincaptrue/2013-09-23-TICKET52.xml', '2016-06-15 15:14:58.623', 934, 'EXECUTED', '7:135ad09c8bbfad4aa87ba88780688c9e', 'insert', 'Add the medical coding data type to the database', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-09-23-TICKET52-2', 'markg', 'migration/clincaptrue/2013-09-23-TICKET52.xml', '2016-06-15 15:14:58.633', 935, 'EXECUTED', '7:0b9b81c07b2cfd67d836bbdd4dcacab6', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-08-09-TICKET424', 'markg', 'migration/clincaptrue/2013-09-18-TICKET424.xml', '2016-06-15 15:14:58.642', 936, 'EXECUTED', '7:c7b9bce7bcfd3e259a72b7f74b1eb835', 'insert', 'Add coding role to the user_role table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-08-09-TICKET424-01', 'mapkon', 'migration/clincaptrue/2013-09-18-TICKET424.xml', '2016-06-15 15:14:58.651', 937, 'EXECUTED', '7:c896d58b8b167634fdcf85bd49a9785a', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-09-26-TICKET555', 'markg', 'migration/clincaptrue/2013-09-26-TICKET555.xml', '2016-06-15 15:14:58.661', 938, 'EXECUTED', '7:580b4a1883a1ef43c0f7ee3b566a9b64', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-10-01-TICKET573', 'markg', 'migration/clincaptrue/2013-10-01-TICKET573.xml', '2016-06-15 15:14:58.67', 939, 'EXECUTED', '7:541015b64f1b45388f79c3922b8fc8ad', 'insert', 'Insert the default medical coding study configuration option record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-10-07-TICKET628', 'markg', 'migration/clincaptrue/2013-10-07-TICKET628.xml', '2016-06-15 15:14:58.68', 940, 'EXECUTED', '7:f58335de70867d6997936a358bee1351', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-10-10-TICKET421', 'markg', 'migration/clincaptrue/2013-10-10-TICKET421.xml', '2016-06-15 15:14:58.688', 941, 'EXECUTED', '7:2aac0f86ab81f7eda4e9db9c7652c7c7', 'insert', 'Insert the default custom medical coding study configuration option record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-10-10-TICKET421-1', 'markg', 'migration/clincaptrue/2013-10-10-TICKET421.xml', '2016-06-15 15:14:58.698', 942, 'EXECUTED', '7:e5e6188f8441ca2b4e9057074f46e8d0', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-10-14-TICKET653', 'markg', 'migration/clincaptrue/2013-10-14-TICKET653.xml', '2016-06-15 15:14:58.71', 943, 'EXECUTED', '7:6505262f232726f9d4e76835bbdb0d06', 'dropTable', 'Dropping not so needed synonym table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-10-27-TICKET721-01', 'skirpichenok', 'migration/clincaptrue/2013-10-27-TICKET721.xml', '2016-06-15 15:14:58.719', 944, 'EXECUTED', '7:33eb6c000995d0fe4198beb9de901021', 'sql', 'Fix problems with SDV state in the event_crf table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-10-17-TICKET654', 'markg', 'migration/clincaptrue/2013-10-17-TICKET654.xml', '2016-06-15 15:14:58.728', 945, 'EXECUTED', '7:224e1eb62198bf579727ffa5726bc08d', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-10-18-TICKET654-1', 'markg', 'migration/clincaptrue/2013-10-17-TICKET654.xml', '2016-06-15 15:14:58.737', 946, 'EXECUTED', '7:fdabdebe4a1ea743a36a8812923d816b', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-10-18-TICKET611-3', 'skirpichenok', 'migration/clincaptrue/2013-10-18-TICKET611.xml', '2016-06-15 15:14:58.746', 947, 'MARK_RAN', '7:4091535e783ff4c8e00d206482e26e08', 'update (x2)', 'Set new id for the existing record in the audit_log_event_type table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-10-18-TICKET611-4', 'skirpichenok', 'migration/clincaptrue/2013-10-18-TICKET611.xml', '2016-06-15 15:14:58.756', 948, 'EXECUTED', '7:50e36f2544c03decac0ef094d3f70775', 'insert', 'Insert a new record into the audit_log_event_type table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-10-18-TICKET611-5', 'skirpichenok', 'migration/clincaptrue/2013-10-18-TICKET611.xml', '2016-06-15 15:14:58.766', 949, 'EXECUTED', '7:0d4311e3bedf26ab7241ad59a1e615ff', 'sql', 'Update item_data_trigger() for medical coding type audit', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-10-18-TICKET611-6', 'skirpichenok', 'migration/clincaptrue/2013-10-18-TICKET611.xml', '2016-06-15 15:14:58.774', 950, 'MARK_RAN', '7:8d9076d34b1cd711d7773972b6a508ab', 'sql', 'update item_data_trigger and study_event_trigger', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-11-04-TICKET694', 'markg', 'migration/clincaptrue/2013-11-04-TICKET694.xml', '2016-06-15 15:14:58.783', 951, 'EXECUTED', '7:28003706fbde493f35a994e938d88153', 'insert', 'Insert the study parameter flag to control coded and synonymized items approval', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-11-11-TICKET727-01', 'skirpichenok', 'migration/clincaptrue/2013-11-11-TICKET727.xml', '2016-06-15 15:14:58.814', 952, 'EXECUTED', '7:7f5cd7a1560bb763d86cbb083eba1544', 'modifyDataType', 'Expand the length of Summary field to 2000 characters', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-11-13-TICKET744-1', 'vitaly', 'migration/clincaptrue/2013-11-13-TICKET744.xml', '2016-06-15 15:14:58.832', 953, 'EXECUTED', '7:4d68ff209e2aa593225a0af122ab89bf', 'dropColumn (x3)', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-11-13-TICKET744-2', 'vitaly', 'migration/clincaptrue/2013-11-13-TICKET744.xml', '2016-06-15 15:14:58.847', 954, 'EXECUTED', '7:a8d7c22d3e04f4d26f0c7c65649e12d0', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-11-25-TICKET157-01', 'igor', 'migration/clincaptrue/2013-11-25-TICKET157.xml', '2016-06-15 15:14:58.871', 955, 'EXECUTED', '7:ed1ffad1773cb68587d22b0a9165ca59', 'addColumn (x2), renameColumn, sql, renameTable', 'Add column description_type_id, rename column dn_rfc_description_id, rename table dn_rfc_description', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-11-25-TICKET157-02', 'igor', 'migration/clincaptrue/2013-11-25-TICKET157.xml', '2016-06-15 15:14:58.882', 956, 'EXECUTED', '7:849331b70c50f4d5d1f33c574b6c72b2', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-11-25-TICKET157-03', 'igor', 'migration/clincaptrue/2013-11-25-TICKET157.xml', '2016-06-15 15:14:58.89', 957, 'MARK_RAN', '7:f5d7efad338ff56006e16aa7358808de', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-12-06-TICKET826-01', 'skirpichenok', 'migration/clincaptrue/2013-12-06-TICKET826.xml', '2016-06-15 15:14:58.899', 958, 'EXECUTED', '7:82ecf16db1fd0a9c123611cfac92f59c', 'sql', 'Update item_data_trigger() for medical coding type audit', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-12-09-TICKET743-1', 'vitaly', 'migration/clincaptrue/2013-12-09-TICKET743.xml', '2016-06-15 15:14:58.911', 959, 'EXECUTED', '7:dc596c8b503a9d581f2e67a3431a6593', 'dropColumn, addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-12-09-TICKET743-2', 'vitaly', 'migration/clincaptrue/2013-12-09-TICKET743.xml', '2016-06-15 15:14:58.929', 960, 'EXECUTED', '7:61c16817292108742d20f5cdc4e466a3', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-12-09-TICKET743-3', 'vitaly', 'migration/clincaptrue/2013-12-09-TICKET743.xml', '2016-06-15 15:14:58.939', 961, 'EXECUTED', '7:d8d06a7aa0365f1c0e2b198f09d3b5e6', 'sql', 'Update item_data_trigger() for medical coding type audit', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-12-09-TICKET743-4', 'vitaly', 'migration/clincaptrue/2013-12-09-TICKET743.xml', '2016-06-15 15:14:58.947', 962, 'MARK_RAN', '7:9aa04c752aa48717a10ed5ba2ce7f410', 'sql', 'update item_data_trigger and study_event_trigger', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-12-20-TICKET852-1', 'vitaly', 'migration/clincaptrue/2013-12-20-TICKET852.xml', '2016-06-15 15:14:58.957', 963, 'EXECUTED', '7:7472a1ca81fb9a5f56a1dd931e992d6f', 'sql', 'Delete old study_parameter values', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-12-20-TICKET852-2', 'vitaly', 'migration/clincaptrue/2013-12-20-TICKET852.xml', '2016-06-15 15:14:58.965', 964, 'EXECUTED', '7:dc13ed8cbfd2ddb8f4c10a86851ab3eb', 'update', 'Update old study_parameter', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-12-17-TICKET36-1', 'skirpichenok', 'migration/clincaptrue/2013-12-17-TICKET36.xml', '2016-06-15 15:14:58.974', 965, 'EXECUTED', '7:eb7fcacece3e553cf9ae2424c9a413fd', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-01-02-TICKET858', 'vitaly', 'migration/clincaptrue/2014-01-02-TICKET858.xml', '2016-06-15 15:14:58.985', 966, 'EXECUTED', '7:55a1541e0762280076a97ef7b8c4a17d', 'addColumn', 'Add new column to the coded_item table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-12-06-TICKET808-01', 'skirpichenok', 'migration/clincaptrue/2013-12-06-TICKET808.xml', '2016-06-15 15:14:58.995', 967, 'EXECUTED', '7:4731278c2ef0a14fb85f04e4587b4108', 'addColumn', 'Add column signed_data to study_event table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-01-07-TICKET859', 'vitaly', 'migration/clincaptrue/2014-01-07-TICKET859.xml', '2016-06-15 15:14:59.004', 968, 'EXECUTED', '7:ce1b7ae137cf70a95176dbda8a717abb', 'insert', 'Add new parameter to the study parameter list', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-12-19-TICKET587-1', 'skirpichenok', 'migration/clincaptrue/2013-12-19-TICKET587.xml', '2016-06-15 15:14:59.013', 969, 'EXECUTED', '7:25f9b20c97ab867a8b371067d85a16ba', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-12-19-TICKET587-2', 'skirpichenok', 'migration/clincaptrue/2013-12-19-TICKET587.xml', '2016-06-15 15:14:59.029', 970, 'EXECUTED', '7:c3472310b2cf005ed00fcd15e982200b', 'createTable', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-12-30-TICKET728-1', 'markg', 'migration/clincaptrue/2013-12-30-TICKET728.xml', '2016-06-15 15:14:59.039', 971, 'EXECUTED', '7:2747920d8ae40e1ed14255239c235908', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-12-30-TICKET728-2', 'markg', 'migration/clincaptrue/2013-12-30-TICKET728.xml', '2016-06-15 15:14:59.049', 972, 'EXECUTED', '7:59025cedd291e6dfeff9ba6984ebbefa', 'renameColumn', 'Rename coded item column name', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-15-TICKET875', 'vitaly', 'migration/clincaptrue/2014-01-15-TICKET875.xml', '2016-06-15 15:14:59.059', 973, 'EXECUTED', '7:850cdf88b938b6513de8be70e7bf881d', 'insert', 'Insert the study parameter for bioontology verification', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-01-25-TICKET893', 'vitaly', 'migration/clincaptrue/2014-01-25-TICKET893.xml', '2016-06-15 15:14:59.069', 974, 'EXECUTED', '7:58b60320ab05ed93a525d2b614f1278c', 'addColumn', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-01-30-TICKET879', 'satoshi', 'migration/clincaptrue/2014-01-30-TICKET879.xml', '2016-06-15 15:14:59.078', 975, 'MARK_RAN', '7:8b7545763f029560e5afb970ba1a3e33', 'insert', 'Re-add ''allowCodingVerfication'' if it does not exist in database', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-01-25-TICKET329-CREATE-WIDGETS-LAYOUT', 'denis', 'migration/clincaptrue/2014-01-28-TICKET329.xml', '2016-06-15 15:14:59.091', 976, 'EXECUTED', '7:80ade0e6478b1493f6eb4ea5881c9d62', 'createTable', 'Create table widgets_layout', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-01-25-TICKET329-CREATE-WIDGET', 'denis', 'migration/clincaptrue/2014-01-28-TICKET329.xml', '2016-06-15 15:14:59.108', 977, 'EXECUTED', '7:1b38bfbe66879680fc4f048e2bcec94b', 'createTable', 'Create table widget', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-25-TICKET329-4', 'denis', 'migration/clincaptrue/2014-01-28-TICKET329.xml', '2016-06-15 15:14:59.12', 978, 'EXECUTED', '7:23acfb7b0c6322e158b0a30929d00bb7', 'addForeignKeyConstraint', 'Add foreign key for widgets_layout', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-25-TICKET329-5', 'denis', 'migration/clincaptrue/2014-01-28-TICKET329.xml', '2016-06-15 15:14:59.133', 979, 'EXECUTED', '7:5e7e9c7f8efaf2e341e9afa93c3e43c6', 'addForeignKeyConstraint', 'Add foreign key for widgets_layout', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-25-TICKET329-KEY-FOR-WIDGET', 'denis', 'migration/clincaptrue/2014-01-28-TICKET329.xml', '2016-06-15 15:14:59.143', 980, 'EXECUTED', '7:31ccef078f633b1c07c32b247d8811e4', 'addForeignKeyConstraint', 'Add foreign key for widgets_layout', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-25-TICKET329-7-CREATE-NDS-ASSIGNED-TO-ME-WIDGET', 'denis', 'migration/clincaptrue/2014-01-28-TICKET329.xml', '2016-06-15 15:14:59.154', 981, 'EXECUTED', '7:8a10c7a2f7a8b950020b7e42a0ab77ed', 'insert', 'Insert "Notes and discrepancies assigned to me" widget', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-02-15-TICKET990-1', 'denis', 'migration/clincaptrue/2014-02-15-TICKET990.xml', '2016-06-15 15:14:59.163', 982, 'EXECUTED', '7:37cc6444160a0c60cc49b42a0e1c4787', 'update', 'Allow access to NDs assigned to me widget for Medical Coder', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-25-TICKET992-UPDATE-SEQENCE', 'denis', 'migration/clincaptrue/2014-02-20-TICKET992.xml', '2016-06-15 15:14:59.172', 983, 'EXECUTED', '7:b6d83fbd56e8d718761539ce8e93dfb2', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-25-TICKET992-CREATE-EVENTS-COMPLETION-WIDGET', 'denis', 'migration/clincaptrue/2014-02-20-TICKET992.xml', '2016-06-15 15:14:59.181', 984, 'EXECUTED', '7:6c756d63bd2b8e7a94bc2b127735aa87', 'insert', 'Create "Events Completion" widget', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-03-04-TICKET1074-1', 'denis', 'migration/clincaptrue/2014-03-04-TICKET1074.xml', '2016-06-15 15:14:59.189', 985, 'EXECUTED', '7:0ceeb9463754c7acc3d3cf0106b01bcd', 'update', 'Allow access to "Events Completion" widget for Site-level users', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-01-25-TICKET1082-UPDATE-SEQENCE', 'denis', 'migration/clincaptrue/2014-03-07-TICKET1082.xml', '2016-06-15 15:14:59.199', 986, 'EXECUTED', '7:b6d83fbd56e8d718761539ce8e93dfb2', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-03-07-TICKET1082-CREATE-SUBJECT-STATUS-COUNT-WIDGET', 'denis', 'migration/clincaptrue/2014-03-07-TICKET1082.xml', '2016-06-15 15:14:59.208', 987, 'EXECUTED', '7:da2c7d2a342d934a19764c24c8e6f355', 'insert', 'Create "Subject Status Count" widget', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-03-07-TICKET1107-UPDATE-SEQENCE', 'denis', 'migration/clincaptrue/2014-03-11-TICKET1107.xml', '2016-06-15 15:14:59.217', 988, 'EXECUTED', '7:b6d83fbd56e8d718761539ce8e93dfb2', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-03-07-TICKET1107-CREATE-STUDY-PROGRESS-WIDGET', 'denis', 'migration/clincaptrue/2014-03-11-TICKET1107.xml', '2016-06-15 15:14:59.226', 989, 'EXECUTED', '7:cb48bce9d5d9746538a7a8ad6aaa7834', 'insert', 'Create "Study Progress" widget', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-03-15-TICKET1021-01', 'igor', 'migration/clincaptrue/2014-03-15-TICKET1021.xml', '2016-06-15 15:14:59.238', 990, 'EXECUTED', '7:c68a5e9ff2183d67908c756557737f04', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-03-18-TICKET1126-ADD-COLUMN', 'denis', 'migration/clincaptrue/2014-03-18-TICKET1126.xml', '2016-06-15 15:14:59.246', 991, 'EXECUTED', '7:e0f321cbb4b3ba7de8c7632b0d27e00d', 'addColumn', 'Add column-marker for big widgets.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-03-18-TICKET1126-UPDATE-OLD-VALUES', 'denis', 'migration/clincaptrue/2014-03-18-TICKET1126.xml', '2016-06-15 15:14:59.255', 992, 'EXECUTED', '7:0813bb439a2ac06c47e516947d180ed2', 'update', 'Update all widgets that were added before.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-04-07-TICKET1135-1', 'denis', 'migration/clincaptrue/2014-04-07-TICKET1135.xml', '2016-06-15 15:14:59.265', 993, 'EXECUTED', '7:d288f4b67fa8da51da62894af35d20f2', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-04-07-TICKET1135-2', 'denis', 'migration/clincaptrue/2014-04-07-TICKET1135.xml', '2016-06-15 15:14:59.274', 994, 'EXECUTED', '7:c4339a0da7a6ae851449855749d927ee', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-04-11-TICKET990-1-1', 'denis', 'migration/clincaptrue/2014-04-11-TICKET990-1.xml', '2016-06-15 15:14:59.283', 995, 'EXECUTED', '7:e761668af590d23d341fbed0e6ea2b70', 'update', 'Update access to widgets for Medical Coder', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-04-11-TICKET990-1-2', 'denis', 'migration/clincaptrue/2014-04-11-TICKET990-1.xml', '2016-06-15 15:14:59.293', 996, 'EXECUTED', '7:36aa8ff0640bfd7bbe781dca96421f26', 'sql', 'Clear widgets layout for medical coder', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-03-26-TICKET1176-ADD-EMAILING-STEP-COLUMN', 'denis', 'migration/clincaptrue/2014-03-26-TICKET1176.xml', '2016-06-15 15:14:59.302', 997, 'EXECUTED', '7:7ca7836e675486d59fd2a3b84880f777', 'addColumn', 'Add column to event_definiton_crf table for ability to specify step on which email will be send.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-01-05-TICKET1949', 'denis', 'migration/clincaptrue/2015-01-05-TICKET1949.xml', '2016-06-15 15:14:59.925', 1030, 'EXECUTED', '7:a46a77234c0dbc04b2ed9bdcb17b452d', 'update', 'Update id of "allowDynamicGroupsManagement" parameter if it''s different.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-03-26-TICKET1176-ADD-EMAIL-COLUMN', 'denis', 'migration/clincaptrue/2014-03-26-TICKET1176.xml', '2016-06-15 15:14:59.315', 998, 'EXECUTED', '7:0aa40233946449e471c4a00a4e67272a', 'addColumn', 'Add column to event_definiton_crf table for ability to email CRF.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-04-15-TICKET1004-1', 'skirpichenok', 'migration/clincaptrue/2014-04-15-TICKET1004.xml', '2016-06-15 15:14:59.326', 999, 'EXECUTED', '7:e87b74241e35a63299684003fb901c66', 'sql', 'updating wrong study_subject_id in event_crf table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-04-17-TICKET1283-01', 'denis', 'migration/clincaptrue/2014-04-17-TICKET1283.xml', '2016-06-15 15:14:59.335', 1000, 'EXECUTED', '7:9f53026a729601c0b73d8074b9a138c9', 'update', 'Allow used to set Person ID Required on Site level', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-03-07-TICKET1114-UPDATE-SEQENCE', 'denis', 'migration/clincaptrue/2014-04-16-TICKET1114.xml', '2016-06-15 15:14:59.343', 1001, 'EXECUTED', '7:b6d83fbd56e8d718761539ce8e93dfb2', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-03-07-TICKET1114-CREATE-SDV-WIDGET', 'denis', 'migration/clincaptrue/2014-04-16-TICKET1114.xml', '2016-06-15 15:14:59.355', 1002, 'EXECUTED', '7:cde2414704c200ccbcab2af202dd8a64', 'insert', 'Create "Source Data Verification Progress" widget', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-04-22-TICKET1295-ADD-COLUMN-TO-SYSTEM-GROUP', 'Frank', 'migration/clincaptrue/2014-04-22-TICKET1295.xml', '2016-06-15 15:14:59.375', 1003, 'EXECUTED', '7:9b7f1e4a82795e17f25125f7ffa51e05', 'addColumn, addNotNullConstraint', 'Add column is_study_specific to system_group', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-04-22-TICKET1295-INSERT-DATA', 'Frank', 'migration/clincaptrue/2014-04-22-TICKET1295.xml', '2016-06-15 15:14:59.386', 1004, 'EXECUTED', '7:252cd5c2706601344ed1b02fa8cfb646', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-04-22-TICKET1295-UPDATE-SYSTEM-GROUP-DATA', 'Frank', 'migration/clincaptrue/2014-04-22-TICKET1295.xml', '2016-06-15 15:14:59.423', 1005, 'EXECUTED', '7:23d394e2d6512c1873c0cd41302b6768', 'update (x9)', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-04-22-TICKET1295-ADD-COLUMN-TO-STUDY-PARAMETER', 'Frank', 'migration/clincaptrue/2014-04-22-TICKET1295.xml', '2016-06-15 15:14:59.471', 1006, 'EXECUTED', '7:ed074cdfa011705e0f62349845441722', 'addColumn (x10)', 'Add columns to study_parameter to make it compliant with System Settings', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-04-22-TICKET1295-UPDATE-STUDY-PARAMETER-DATA', 'Frank', 'migration/clincaptrue/2014-04-22-TICKET1295.xml', '2016-06-15 15:14:59.526', 1007, 'EXECUTED', '7:c6c3d98e4a597dc30dce8d8d0271757b', 'update (x10)', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-05-05-TICKET1114-1-UPDATE-ACCESS-TO-SDV', 'denis', 'migration/clincaptrue/2014-05-05-TICKET1114-1.xml', '2016-06-15 15:14:59.538', 1008, 'EXECUTED', '7:88e60f56b617a13105a8364d41be160f', 'update', 'Add access to SDV widget for Study Administrator', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-05-09-TICKET1295-2-UPDATE-SYSTEM-GROUP', 'Frank', 'migration/clincaptrue/2014-05-09-TICKET1295-2.xml', '2016-06-15 15:14:59.55', 1009, 'EXECUTED', '7:f7eb07cdb7dd90c5549effcba83ed49f', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-05-09-TICKET1295-2-DROP-COLUMNS-FROM-STUDY-PARAMETER', 'Frank', 'migration/clincaptrue/2014-05-09-TICKET1295-2.xml', '2016-06-15 15:14:59.65', 1010, 'EXECUTED', '7:f8b19febcff879ded373b8c02dc283f5', 'dropColumn (x10)', 'Drop columns from study_parameter previously added by 2014-04-22-TICKET1295', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-05-09-TICKET1295-2-INSERT-SYSTEM-DATA', 'Frank', 'migration/clincaptrue/2014-05-09-TICKET1295-2.xml', '2016-06-15 15:14:59.694', 1011, 'EXECUTED', '7:a37848eb063c7a8105bf18333059676f', 'insert (x10)', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-05-12-TICKET1296-INSERT-DATA', 'Frank', 'migration/clincaptrue/2014-05-12-TICKET1296.xml', '2016-06-15 15:14:59.709', 1012, 'EXECUTED', '7:c194dcfb58091d6d0c9071b6542c8414', 'insert (x2)', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-05-21-TICKET1383-01', 'skirpichenok', 'migration/clincaptrue/2014-05-21-TICKET1383.xml', '2016-06-15 15:14:59.72', 1013, 'EXECUTED', '7:0908bc50a7c1f3c621667599c6866964', 'addColumn', 'Add column to rule_expression table for ability to know the correct event and crf version for target (it''s required for rule editing).', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-05-21-TICKET1347', 'Frank', 'migration/clincaptrue/2014-05-21-TICKET1347.xml', '2016-06-15 15:14:59.731', 1014, 'EXECUTED', '7:b60a81af2df6a07be790c38e3258d524', 'sql', 'Replace $jq with $ in section instructions', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-05-28-TICKET1412', 'Frank', 'migration/clincaptrue/2014-05-28-TICKET1412.xml', '2016-06-15 15:14:59.74', 1015, 'EXECUTED', '7:0ae6f2c5e776186794894f2d5a1ec235', 'sql', 'Remove old MC URL and API Key', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-06-03-TICKET1419-UPDATE-SYSTEM-DATA', 'Frank', 'migration/clincaptrue/2014-06-03-TICKET1419.xml', '2016-06-15 15:14:59.752', 1016, 'EXECUTED', '7:c5eaa4a3bad6a2794cf12103fa67532d', 'update (x2)', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-06-02-TICKET1416-1', 'denis', 'migration/clincaptrue/2014-06-02-TICKET1416.xml', '2016-06-15 15:14:59.762', 1017, 'EXECUTED', '7:84d989035df36dc30255236d10cb79a3', 'insert', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-06-10-TICKET1456-1', 'denis', 'migration/clincaptrue/2014-06-10-TICKET1456.xml', '2016-06-15 15:14:59.784', 1018, 'EXECUTED', '7:2957bce2141ed5a54d62d1bc11eda57c', 'modifyDataType', 'Update data type for the column ''instructions'' in the table ''section''', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-06-10-TICKET1456-2', 'denis', 'migration/clincaptrue/2014-06-10-TICKET1456.xml', '2016-06-15 15:14:59.792', 1019, 'MARK_RAN', '7:4d7a4f0b74af618f1f9ad4572bae06f3', 'modifyDataType', 'Update data type for the column ''instructions'' in the table ''section''', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-06-03-TICKET1418-UPDATE-SEQENCE', 'denis', 'migration/clincaptrue/2014-06-03-TICKET1418.xml', '2016-06-15 15:14:59.801', 1020, 'EXECUTED', '7:b6d83fbd56e8d718761539ce8e93dfb2', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-06-03-TICKET1418-CREATE-NDSPERCRF-WIDGET', 'denis', 'migration/clincaptrue/2014-06-03-TICKET1418.xml', '2016-06-15 15:14:59.81', 1021, 'EXECUTED', '7:8e1879678f922241719ae5ea0ac202de', 'insert', 'Create "NDs Per CRF" widget', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-06-12-TICKET942-1', 'vitaly', 'migration/clincaptrue/2014-06-12-TICKET942.xml', '2016-06-15 15:14:59.822', 1022, 'EXECUTED', '7:f0c30840210099910c702b509352abf7', 'sql', 'Updating ordinals in study event definition table to remove broken ordinals', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-06-10-TICKET1455', 'aram', 'migration/clincaptrue/oracle/2014-06-10-TICKET1455.xml', '2016-06-15 15:14:59.83', 1023, 'MARK_RAN', '7:233369ce377afc2ae20945dbece924de', 'addColumn', 'Adding column ''auto_layout'' into the table ''crf'' for Oracle.
			Determines if a given CRF should use auto-layout or not. True indicates that auto layout should be applied, False otherwise.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-06-11-TICKET1455', 'aram', 'migration/clincaptrue/postgres/2014-06-11-TICKET1455.xml', '2016-06-15 15:14:59.855', 1024, 'EXECUTED', '7:50b636c63513c17a10bbcae83250138e', 'addColumn', 'Adding column ''auto_layout'' into the table ''crf'' for PostgreSQL.
			Determines if a given CRF should use auto-layout or not. True indicates that auto layout should be applied, False otherwise.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-06-20-TICKET1475', 'Frank', 'migration/clincaptrue/2014-06-20-TICKET1475.xml', '2016-06-15 15:14:59.865', 1025, 'EXECUTED', '7:0512deb23bb5cdeca20a5c805c6aa5c7', 'sql', 'Set status for children of removed event definition crfs to auto-removed', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-06-17-TICKET1465-CREATE-ENROLLMENT-PROGRESS-WIDGET', 'denis', 'migration/clincaptrue/2014-06-17-TICKET1465.xml', '2016-06-15 15:14:59.877', 1026, 'EXECUTED', '7:0c1f02b7bb56ce0e5e52dc908e3328d6', 'insert', 'Create "Enrollment Progress" widget', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-06-30-TICKET1466-01', 'denis', 'migration/clincaptrue/2014-06-30-TICKET1466.xml', '2016-06-15 15:14:59.888', 1027, 'EXECUTED', '7:a6713d50963e1fe1865bf2274775bc86', 'addColumn', 'Add column into Study Subject table, to store randomization date.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-06-30-TICKET1466-02', 'denis', 'migration/clincaptrue/2014-06-30-TICKET1466.xml', '2016-06-15 15:14:59.897', 1028, 'EXECUTED', '7:6d55a56ab601b3d6a78f759167ef2c36', 'addColumn', 'Add column into Study Subject table, to store randomization result.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-07-10-TICKET1513', 'vitaly', 'migration/clincaptrue/2014-07-10-TICKET1513.xml', '2016-06-15 15:14:59.915', 1029, 'EXECUTED', '7:af16f978dcff7ce370020a3f760e4a29', 'insert (x3)', 'Insert rows into system and system_group tables for evaluation feature', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-10-07-TICKET1514-1', 'vitaly', 'migration/clincaptrue/2014-07-10-TICKET1514.xml', '2016-06-15 15:14:59.934', 1031, 'EXECUTED', '7:952dd782e9ebe270cb6521e2b9f10643', 'insert', 'Insert the study parameter flag to control allow CRF evaluation parameter', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-10-07-TICKET1514-2', 'vitaly', 'migration/clincaptrue/2014-07-10-TICKET1514.xml', '2016-06-15 15:14:59.945', 1032, 'EXECUTED', '7:556da720a3cc54b0f5d9c8b09ab0e788', 'insert', 'Insert the study parameter flag to control CRF evaluation context parameter', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-07-11-TICKET1525', 'vitaly', 'migration/clincaptrue/2014-07-11-TICKET1525.xml', '2016-06-15 15:14:59.955', 1033, 'EXECUTED', '7:e681ab4a929899f231cae408804da17f', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-03-26-TICKET1515', 'vitaly', 'migration/clincaptrue/2014-07-11-TICKET1515.xml', '2016-06-15 15:14:59.981', 1034, 'EXECUTED', '7:16aeb28653ec2fc03c107bd261abc9c3', 'addColumn', 'Add column to event_definiton_crf table for evaluated crf flag', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-07-17-TICKET1520-CREATE-CODING-PROGRESS-WIDGET', 'denis', 'migration/clincaptrue/2014-07-17-TICKET1520.xml', '2016-06-15 15:14:59.992', 1035, 'EXECUTED', '7:bd9df17d36faad21e8220c117d844557', 'insert', 'Create "Coding Progress" widget', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-08-01-TICKET1601-UPDATE-ACCESS-TO-CP-WIDGET', 'denis', 'migration/clincaptrue/2014-08-01-TICKET1601.xml', '2016-06-15 15:15:00.003', 1036, 'EXECUTED', '7:bf02dd34e7e3e5f0c7540d5bf98913f8', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-08-01-TICKET1601-UPDATE-ACCESS-TO-NDS-PER-CRF', 'denis', 'migration/clincaptrue/2014-08-01-TICKET1601.xml', '2016-06-15 15:15:00.014', 1037, 'EXECUTED', '7:c579f136c7801167287f4b39af82cb44', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-08-01-TICKET1601-REMOVE-NDS-PER-CRF-WIDGET-FROM-LAYOUT', 'denis', 'migration/clincaptrue/2014-08-01-TICKET1601.xml', '2016-06-15 15:15:00.023', 1038, 'EXECUTED', '7:5472b6a4eec28e0f4c5041c61f7bafc6', 'delete', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-08-03-TICKET1596-01', 'tom', 'migration/clincaptrue/2014-08-03-TICKET1596.xml', '2016-06-15 15:15:00.033', 1039, 'EXECUTED', '7:1490cf0ebcb721982cf2aa7dade63dc8', 'update', 'Remove all traces of -1 from the configuration table in the database', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-08-25-TICKET1673', 'aram', 'migration/clincaptrue/postgres/2014-08-25-TICKET1673.xml', '2016-06-15 15:15:00.044', 1040, 'EXECUTED', '7:6215cbfab2fc30bc0a1be03d1052c1b0', 'sql', 'Recalculates CRFs ordinal inside of study event definitions', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-08-19-TICKET1509', 'Denis', 'migration/clincaptrue/2014-08-19-TICKET1509.xml', '2016-06-15 15:15:00.058', 1041, 'EXECUTED', '7:9c996a323d8be88eec6f15378c216202', 'sql', 'Insert data about randomization into study_subject table for already randomized subjects', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-14-TICKET1735-01', 'skirpichenok', 'migration/clincaptrue/postgres/2014-09-14-TICKET1735.xml', '2016-06-15 15:15:00.068', 1042, 'EXECUTED', '7:680ae0eda12d12e1e4c7314afda33d53', 'insert', 'Insert a new record into the audit_log_event_type table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-14-TICKET1735-02', 'skirpichenok', 'migration/clincaptrue/postgres/2014-09-14-TICKET1735.xml', '2016-06-15 15:15:00.109', 1043, 'EXECUTED', '7:434e59825c582e9ef2727ebd146fa682', 'addColumn (x10)', 'Add new columns to the audit_log_event table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-14-TICKET1735-03', 'skirpichenok', 'migration/clincaptrue/postgres/2014-09-14-TICKET1735.xml', '2016-06-15 15:15:00.119', 1044, 'EXECUTED', '7:c7b7bd466b2b291171f00750135613ed', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-14-TICKET1735-04', 'skirpichenok', 'migration/clincaptrue/postgres/2014-09-14-TICKET1735.xml', '2016-06-15 15:15:00.127', 1045, 'EXECUTED', '7:b114e278f2dc7ed8e27c03863c5ef5b5', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-14-TICKET1735-05', 'skirpichenok', 'migration/clincaptrue/postgres/2014-09-14-TICKET1735.xml', '2016-06-15 15:15:00.138', 1046, 'EXECUTED', '7:f564a8f090b0ae5a178cfb2e55d428ea', 'sql', 'update event_crf_trigger and study_event_trigger', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-24-TICKET1749', 'Denis', 'migration/clincaptrue/2014-09-24-TICKET1749.xml', '2016-06-15 15:15:00.148', 1047, 'EXECUTED', '7:9284f389d13d0e1b54dedd31bf933eb1', 'delete', 'Delete all data from table rule_set_rule that has no reference row rule_id.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-02-TICKET1535-CREATE-ENROLLMENT-PER-SITE-WIDGET', 'denis', 'migration/clincaptrue/2014-09-02-TICKET1535.xml', '2016-06-15 15:15:00.158', 1048, 'EXECUTED', '7:b77129c955fec2f63f39cb8321443cf3', 'insert', 'Create "Enrollment Status Per Site" widget', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-23-TICKET1737-01', 'skirpichenok', 'migration/clincaptrue/2014-09-23-TICKET1737.xml', '2016-06-15 15:15:00.168', 1049, 'EXECUTED', '7:71acbd9bff34ad1f0f756cc2d34e7663', 'update', 'Add support of new theme on the system settings page', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-10-TICKET1722-DELETE-FROM-SYSTEM', 'Frank', 'migration/clincaptrue/2014-09-10-TICKET1722.xml', '2016-06-15 15:15:00.183', 1050, 'EXECUTED', '7:3a642a116fabf924d0857f2b84704a1a', 'delete (x3)', 'Delete medical coding properties allowCodingVerification, medicalCodingApprovalNeeded and medicalCodingContextNeeded from system', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-10-TICKET1722-UPDATE-SYSTEM', 'Frank', 'migration/clincaptrue/2014-09-10-TICKET1722.xml', '2016-06-15 15:15:00.2', 1051, 'EXECUTED', '7:fc77ca9abb8bc96a25a86aaf7b70bba0', 'update (x3)', 'Update order_id values for medical coding system properties', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-10-TICKET1722-DROP-SYSTEM-GROUP-COL', 'Frank', 'migration/clincaptrue/2014-09-10-TICKET1722.xml', '2016-06-15 15:15:00.21', 1052, 'EXECUTED', '7:8ca761b8f59d1f0a59c2d6df3f9a6d52', 'dropColumn', 'drop column is_study_specific as it is no longer used', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-15-TICKET1730-UPDATE-ACCESS-TO-NDS-WIDGET', 'denis', 'migration/clincaptrue/2014-09-15-TICKET1730.xml', '2016-06-15 15:15:00.219', 1053, 'EXECUTED', '7:bece5e781c43d5143da9d9fc45ac8c8e', 'update', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-18-TICKET1712-1', 'denis', 'migration/clincaptrue/2014-09-18-TICKET1712.xml', '2016-06-15 15:15:00.229', 1054, 'EXECUTED', '7:2ae6640a1edb2d46cde3e8107499a97b', 'insert', 'Insert the study parameter flag to control Auto Generated subject ID prefix.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-18-TICKET1712-2', 'denis', 'migration/clincaptrue/2014-09-18-TICKET1712.xml', '2016-06-15 15:15:00.238', 1055, 'EXECUTED', '7:41f266cb3cd8a34f6c2fd74a96e25e76', 'insert', 'Insert the study parameter flag to control Auto Generated subject ID separator', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-18-TICKET1712-3', 'denis', 'migration/clincaptrue/2014-09-18-TICKET1712.xml', '2016-06-15 15:15:00.248', 1056, 'EXECUTED', '7:4cf077916574cfa26b3a6e80fddbd7c7', 'insert', 'Insert the study parameter flag to control Auto Generated subject ID suffix', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-02-TICKET1535-CREATE-EVALUATION-PROGRESS-WIDGET', 'denis', 'migration/clincaptrue/2014-09-15-TICKET1524.xml', '2016-06-15 15:15:00.259', 1057, 'EXECUTED', '7:dc24a6592dacfb638b3009e5ea153c1a', 'insert', 'Create "CRF Evaluation Progress" widget', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-10-TICKET1721-UPDATE-STUDY-PARAMETER-VALUE', 'Frank', 'migration/clincaptrue/2014-09-10-TICKET1721.xml', '2016-06-15 15:15:00.268', 1058, 'EXECUTED', '7:39d27f4c7c2d5535b6d615d2b5cc136c', 'sql', 'Update unset study-level rando trialIds before deleting system level trialId', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-10-TICKET1721-DELETE-SYSTEM', 'Frank', 'migration/clincaptrue/2014-09-10-TICKET1721.xml', '2016-06-15 15:15:00.28', 1059, 'EXECUTED', '7:6df9f6f6c9cab72541916c1f2a2956d2', 'delete (x2)', 'Delete randomization trial id and assignment result from system', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-09-10-TICKET1721-UPDATE-SYSTEM', 'Frank', 'migration/clincaptrue/2014-09-10-TICKET1721.xml', '2016-06-15 15:15:00.289', 1060, 'EXECUTED', '7:ae6764dc0889b8411a41d0259e1b6b41', 'update', 'Update order_id values for randomizationEnviroment for consistency', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-10-08-TICKET1773-POSTGRES', 'denis', 'migration/clincaptrue/postgres/2014-10-08-TICKET1773.xml', '2016-06-15 15:15:00.303', 1061, 'EXECUTED', '7:fc9d6437eee2dec401f035fb05ff477d', 'sql', 'Delete rule_fk if exists.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-10-29-TICKET1789', 'Frank', 'migration/clincaptrue/2014-10-29-TICKET1789.xml', '2016-06-15 15:15:00.311', 1062, 'EXECUTED', '7:a662e1984f72270683112822d3c5368c', 'insert', 'Adds study parameter to allow/deny dynamic groups management', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-10-28-TICKET1829-1', 'skirpichenok', 'migration/clincaptrue/2014-10-28-TICKET1829.xml', '2016-06-15 15:15:00.325', 1063, 'EXECUTED', '7:7e77c5a8a6e72902ab622d43f48a49cb', 'insert (x2)', 'Add language support for clincapture', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-10-28-TICKET1849', 'denis', 'migration/clincaptrue/2014-10-28-TICKET1849.xml', '2016-06-15 15:15:00.334', 1064, 'EXECUTED', '7:1834050d3e5ee4ac6874032bd5cc7227', 'sql', 'Replace all role names by codes.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-11-11-TICKET1537', 'Frank', 'migration/clincaptrue/2014-11-11-TICKET1537.xml', '2016-06-15 15:15:00.368', 1065, 'EXECUTED', '7:419517a158b0ac58c25edc2e63dad69e', 'update (x8)', 'Add widget support for Site Monitor role', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-11-20-TICKET1884', 'vitaly', 'migration/clincaptrue/2014-11-20-TICKET1884.xml', '2016-06-15 15:15:00.377', 1066, 'EXECUTED', '7:47d30471a7103030364f738cc4c9b4a0', 'insert', 'Insert the study parameter flag to control events autoscheduling for insert rules', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-10-21-TICKET1771', 'denis', 'migration/clincaptrue/2014-10-21-TICKET1771.xml', '2016-06-15 15:15:00.388', 1067, 'EXECUTED', '7:ce707fd207a9cb00aba1ea7ba4b9f53e', 'insert', 'Insert the study parameter flag to control Auto Generated subject ID prefix.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-12-19-TICKET1956', 'vitaly', 'migration/clincaptrue/2014-12-19-TICKET1956.xml', '2016-06-15 15:15:00.423', 1068, 'EXECUTED', '7:d89b6be09d5a701b509c48ee634cd184', 'modifyDataType (x2)', 'crf_version and item group columns update for long oid names', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2014-12-24-TICKET1897', 'vitaly', 'migration/clincaptrue/2014-12-24-TICKET1897.xml', '2016-06-15 15:15:00.434', 1069, 'EXECUTED', '7:457a76c55b521eaac36b044665b0945a', 'addColumn', 'Insert the SAS name column to the item table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-01-23-TICKET1998', 'Frank', 'migration/clincaptrue/2015-01-23-TICKET1998.xml', '2016-06-15 15:15:00.443', 1070, 'EXECUTED', '7:c50fb22210951e38857c053d1c78d647', 'insert', 'Adds study parameter to allow/deny Discrepancy Correction Forms', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-01-06-TICKET1946-01', 'skirpichenok', 'migration/clincaptrue/postgres/2015-01-06-TICKET1946.xml', '2016-06-15 15:15:00.466', 1071, 'EXECUTED', '7:296c7333eb332d2cf9176a2bb43be2c1', 'addColumn', 'Insert the new "SDV" column into the item_data table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-01-06-TICKET1946-02', 'skirpichenok', 'migration/clincaptrue/postgres/2015-01-06-TICKET1946.xml', '2016-06-15 15:15:00.49', 1072, 'EXECUTED', '7:614eaad6a6323e28b488955cd3d7017a', 'addColumn', 'Insert the new "sdv_required" column into the item_form_metadata table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-01-06-TICKET1946-03', 'skirpichenok', 'migration/clincaptrue/postgres/2015-01-06-TICKET1946.xml', '2016-06-15 15:15:00.499', 1073, 'EXECUTED', '7:447f31a60888e1a9aaf64c537e6dfe56', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-01-29-TICKET2005-1', 'denis', 'migration/clincaptrue/postgres/2015-01-29-TICKET2005.xml', '2016-06-15 15:15:00.509', 1074, 'EXECUTED', '7:708c9092c947d6e56dbf3f1ec4d12ebe', 'insert', 'Insert the study parameter flag to control randomization environment.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-01-29-TICKET2005-2', 'denis', 'migration/clincaptrue/postgres/2015-01-29-TICKET2005.xml', '2016-06-15 15:15:00.52', 1075, 'EXECUTED', '7:4201046f3f11616919e60ea57a47a315', 'sql', 'Auto-populate value for randomizationEnviroment flag from System properties', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-01-29-TICKET2005-3', 'denis', 'migration/clincaptrue/postgres/2015-01-29-TICKET2005.xml', '2016-06-15 15:15:00.53', 1076, 'EXECUTED', '7:31f37ca076e831549517609fac93822a', 'delete', 'Remove randomizationEnviroment flag from System properties', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-02-11-TICKET2042-01', 'skirpichenok', 'migration/clincaptrue/2015-02-11-TICKET2042.xml', '2016-06-15 15:15:00.54', 1077, 'EXECUTED', '7:6e74b6128b8c4a79069fcb621a7ec051', 'update', 'SAS extract folder must be greyed-out for Study Admins.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-02-25-TICKET2083-1', 'denis', 'migration/clincaptrue/postgres/2015-02-25-TICKET2083.xml', '2016-06-15 15:15:00.549', 1078, 'EXECUTED', '7:428b16c57c608200bc0bbd116e1ef9be', 'sql', 'Create trigger function to auto-update date_updated in the event_crf table, if status was updated.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-02-25-TICKET2083-2', 'denis', 'migration/clincaptrue/postgres/2015-02-25-TICKET2083.xml', '2016-06-15 15:15:00.558', 1079, 'EXECUTED', '7:ad833b62bdca853ed3ed094ba812e087', 'sql', 'Create trigger to auto-update date_updated in the event_crf table, if status was updated.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-03-14-TICKET2136-1', 'tom', 'migration/clincaptrue/postgres/2015-03-14-TICKET2136.xml', '2016-06-15 15:15:00.578', 1080, 'EXECUTED', '7:0c033e483003a9f90850926cef92b132', 'modifyDataType', 'Change the subtitle in the section table to accept larger strings', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-03-08-TICKET2114-01', 'skirpichenok', 'migration/clincaptrue/2015-03-08-TICKET2114.xml', '2016-06-15 15:15:00.588', 1081, 'EXECUTED', '7:270744368509d27e85cb7b517ef3bbf2', 'insert', 'Insert a new record in the studyParameter table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-03-08-TICKET2114-02', 'skirpichenok', 'migration/clincaptrue/2015-03-08-TICKET2114.xml', '2016-06-15 15:15:00.611', 1082, 'EXECUTED', '7:462e9aee5c47397d9310416bfbb2e9c1', 'addColumn', 'Add column to event_definiton_crf table for controlling of the tabbing mode', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-03-17-TICKET2141-01', 'skirpichenok', 'migration/clincaptrue/2015-03-17-TICKET2141.xml', '2016-06-15 15:15:00.626', 1083, 'EXECUTED', '7:ca502af7d2665b9770e1ed4769e817b4', 'delete, update', 'Rename "crfTabbingMode" to "autoTabbing".', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-03-26-TICKET2178', 'denis', 'migration/clincaptrue/2015-03-26-TICKET2178.xml', '2016-06-15 15:15:00.636', 1084, 'EXECUTED', '7:14a5dfa26fecdd022026cab5c8ce012f', 'insert', 'Insert the study parameter flag to control Calendar Style.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-02-09-TICKET2033-CREATE-1', 'denis', 'migration/clincaptrue/2015-02-09-TICKET2033.xml', '2016-06-15 15:15:00.652', 1085, 'EXECUTED', '7:717db64edc90d5d4e44ee79024e9deed', 'addColumn', 'Add primary key to study_user_role table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-02-09-TICKET2033-CREATE-2', 'denis', 'migration/clincaptrue/2015-02-09-TICKET2033.xml', '2016-06-15 15:15:00.668', 1086, 'EXECUTED', '7:7827a80ac1aadba100b30e2d086d7adc', 'createTable', 'Create table crfs_masking', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-02-09-TICKET2033-3', 'denis', 'migration/clincaptrue/2015-02-09-TICKET2033.xml', '2016-06-15 15:15:00.711', 1087, 'EXECUTED', '7:5295039a8410e498eafa18d87b3ea426', 'addForeignKeyConstraint (x5)', 'Add foreign keys for crf_masking', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-02-10-TICKET2033', 'denis', 'migration/clincaptrue/postgres/2015-02-10-TICKET2033.xml', '2016-06-15 15:15:00.723', 1088, 'EXECUTED', '7:e28c370354a680d202b6a5f9a06a0f02', 'sql', 'Create child event definition CRFs for sites where they not exists.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-03-20-TICKET2099-01', 'skirpichenok', 'migration/clincaptrue/2015-03-30-TICKET2201.xml', '2016-06-15 15:15:00.733', 1089, 'EXECUTED', '7:187af00fb5f784e3ccb689922d3a035e', 'update', 'Add new lang es_MX.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-04-07-TICKET2217.xml', 'vitaly', 'migration/clincaptrue/2015-04-07-TICKET2217.xml', '2016-06-15 15:15:00.747', 1090, 'EXECUTED', '7:6bea77342dc2971d02acf9ae4eb3e316', 'insert, update (x2)', 'Insert row in system table for bioontology username', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-04-07-TICKET2110-01', 'denis', 'migration/clincaptrue/2015-04-07-TICKET2110.xml', '2016-06-15 15:15:00.763', 1091, 'EXECUTED', '7:34064b8a71bcd62561166f87ea666bcf', 'addColumn', 'Add status column to crfs_masking table.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-04-07-TICKET2110-02', 'denis', 'migration/clincaptrue/2015-04-07-TICKET2110.xml', '2016-06-15 15:15:00.773', 1092, 'EXECUTED', '7:7b6905dac3898aa040fe6d88ac98ca92', 'addForeignKeyConstraint', 'Add foreign key for status_id column.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-04-07-TICKET2110-03', 'denis', 'migration/clincaptrue/2015-04-07-TICKET2110.xml', '2016-06-15 15:15:00.783', 1093, 'EXECUTED', '7:d159fdaa64dd7e64152a085ae6995f85', 'update', 'Set status "removed" for all Masks for Investigator roles.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-03-27-TICKET2115-01', 'skirpichenok', 'migration/clincaptrue/postgres/2015-03-27-TICKET2092.xml', '2016-06-15 15:15:00.807', 1094, 'EXECUTED', '7:f74bc81a102b224a32f6fff875e8eccf', 'addColumn', 'Add new column to event_definiton_crf table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-04-14-TICKET2227-01', 'skirpichenok', 'migration/clincaptrue/2015-04-14-TICKET2227.xml', '2016-06-15 15:15:00.818', 1095, 'EXECUTED', '7:36823448e7eeec049a02c5642a64b328', 'update', 'Add new lang zh.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-04-20-TICKET2248', 'denis', 'migration/clincaptrue/2015-04-20-TICKET2248.xml', '2016-06-15 15:15:00.827', 1096, 'EXECUTED', '7:7b465be5669e49513fc73f54b0f84fb0', 'delete', 'Delete all masks for study level users.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2013-07-31-TICKET2257-01', 'denis', 'migration/clincaptrue/2015-04-27-TICKET2257.xml', '2016-06-15 15:15:00.836', 1097, 'EXECUTED', '7:12a6f305f75207a0ddd7908648fb312c', 'addColumn', 'Add column to store event ids and crf ids for dataset.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-04-TICKET2247-1', 'denis', 'migration/clincaptrue/postgres/2015-05-04-TICKET2247.xml', '2016-06-15 15:15:00.845', 1098, 'EXECUTED', '7:804a5c45b3cac5e3c064dc3ccb0d016d', 'insert', 'Insert the study parameter flag to control instance type.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-04-05-TICKET2247-2', 'denis', 'migration/clincaptrue/postgres/2015-05-04-TICKET2247.xml', '2016-06-15 15:15:00.857', 1099, 'EXECUTED', '7:ff7952ac89c0c522628e5b8ae9dcd9bb', 'sql', 'Auto-populate value for instanceType flag from System properties', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-01-TICKET2260', 'aram', 'migration/clincaptrue/postgres/2015-05-01-TICKET2260.xml', '2016-06-15 15:15:00.866', 1100, 'EXECUTED', '7:eeae38af1dba2e8d0554c34717bd923e', 'addColumn', 'Add new column to user_account table to store user''s local time zone ID', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-08-TICKET2277', 'denis', 'migration/clincaptrue/2015-05-08-TICKET2277.xml', '2016-06-15 15:15:00.875', 1101, 'EXECUTED', '7:c4bb6f2a18e231b4b213c1735b84b1bf', 'delete', 'Delete Instance Type from system.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-06-TICKET2270-01', 'aram', 'migration/clincaptrue/postgres/2015-05-06-TICKET2270.xml', '2016-06-15 15:15:00.925', 1102, 'EXECUTED', '7:1e95800c50fce1f49d2e97030990f4e8', 'dropView', 'drop unused view v_dn_count, if exists', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-06-TICKET2270-02', 'aram', 'migration/clincaptrue/postgres/2015-05-06-TICKET2270.xml', '2016-06-15 15:15:00.938', 1103, 'EXECUTED', '7:a92b6d85eaca26bad40667ebf128351e', 'dropView', 'drop unused view v_enrolled_per_site, if exists', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-06-TICKET2270-03', 'aram', 'migration/clincaptrue/postgres/2015-05-06-TICKET2270.xml', '2016-06-15 15:15:00.961', 1104, 'MARK_RAN', '7:030e9e7a8b948627e6c167f8056e88a2', 'dropView', 'drop unused view v_enrollment_per_month_2012, if exists', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-06-TICKET2270-04', 'aram', 'migration/clincaptrue/postgres/2015-05-06-TICKET2270.xml', '2016-06-15 15:15:00.975', 1105, 'EXECUTED', '7:e7d04cda96b76d48b9c94b4dda4b405d', 'dropView', 'drop unused view v_enrollment_per_month_2013, if exists', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-06-TICKET2270-05', 'aram', 'migration/clincaptrue/postgres/2015-05-06-TICKET2270.xml', '2016-06-15 15:15:00.992', 1106, 'MARK_RAN', '7:6c07a213acaa821cdd30074190d1cd05', 'dropView', 'drop view view_discrepancy_note, if exists', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-06-TICKET2270-06', 'aram', 'migration/clincaptrue/postgres/2015-05-06-TICKET2270.xml', '2016-06-15 15:15:01.009', 1107, 'MARK_RAN', '7:323acc8d8ede61dc4dfa3b02a03bb5af', 'dropView', 'drop view view_dn_event_crf, if exists', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-06-TICKET2270-07', 'aram', 'migration/clincaptrue/postgres/2015-05-06-TICKET2270.xml', '2016-06-15 15:15:01.025', 1108, 'MARK_RAN', '7:230025b2d4906299511a9dbad8f0c818', 'dropView', 'drop view view_dn_item_data, if exists', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-06-TICKET2270-08', 'aram', 'migration/clincaptrue/postgres/2015-05-06-TICKET2270.xml', '2016-06-15 15:15:01.042', 1109, 'MARK_RAN', '7:64213bfb48924d553107cc6b67ab7aab', 'dropView', 'drop view view_dn_study_event, if exists', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-06-TICKET2270-09', 'aram', 'migration/clincaptrue/postgres/2015-05-06-TICKET2270.xml', '2016-06-15 15:15:01.06', 1110, 'MARK_RAN', '7:e5482aa3b1e8027527af86cf7aea66ab', 'dropView', 'drop view view_dn_study_subject, if exists', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-06-TICKET2270-10', 'aram', 'migration/clincaptrue/postgres/2015-05-06-TICKET2270.xml', '2016-06-15 15:15:01.077', 1111, 'MARK_RAN', '7:d3db975d6ffcb461b703ef8c6f263190', 'dropView', 'drop view view_dn_subject, if exists', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-06-TICKET2270-11', 'aram', 'migration/clincaptrue/postgres/2015-05-06-TICKET2270.xml', '2016-06-15 15:15:01.094', 1112, 'MARK_RAN', '7:9799fc5feed8b78623c4bcf2ea8e5a9f', 'dropView', 'drop view view_dn_stats, if exists', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-06-TICKET2270-12', 'aram', 'migration/clincaptrue/postgres/2015-05-06-TICKET2270.xml', '2016-06-15 15:15:01.11', 1113, 'MARK_RAN', '7:87f58a82d25d687bb1d78d33006dbdba', 'dropView', 'drop view view_site_hidden_event_definition_crf, if exists', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-06-TICKET2270-13', 'aram', 'migration/clincaptrue/postgres/2015-05-06-TICKET2270.xml', '2016-06-15 15:15:01.126', 1114, 'MARK_RAN', '7:e547a682765c8fadb580f0b890d583be', 'dropView', 'drop view view_study_hidden_event_definition_crf, if exists', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-20-TICKET2313', 'aram', 'migration/clincaptrue/postgres/2015-05-20-TICKET2313.xml', '2016-06-15 15:15:01.137', 1115, 'EXECUTED', '7:d9336b4223fc709f99e8da7939dd2707', 'delete', 'cleanup empty sections', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-12-TICKET2285-01', 'aram', 'migration/clincaptrue/postgres/2015-05-12-TICKET2285.xml', '2016-06-15 15:15:01.225', 1116, 'EXECUTED', '7:6e2d58cec356768220f114eafdfe7e3c', 'modifyDataType (x4)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table event_crf', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-12-TICKET2285-02', 'aram', 'migration/clincaptrue/postgres/2015-05-12-TICKET2285.xml', '2016-06-15 15:15:01.267', 1117, 'EXECUTED', '7:3c1c2862bfedf4ad8ac3889fc63b8eef', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table study_event', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2327', 'aram', 'migration/clincaptrue/2015-05-25-TICKET2327.xml', '2016-06-15 15:15:01.31', 1118, 'EXECUTED', '7:b06e51ee27ebf4d14cf21728c0eda99e', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table study_event_definition', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-01', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.329', 1119, 'EXECUTED', '7:af2fc2137e430fd420765972f901a37b', 'modifyDataType', 'update dates type to TIMESTAMP WITH TIME ZONE in the table archived_dataset_file', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-02', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.369', 1120, 'EXECUTED', '7:57cc1c2ec97a77d76f55c49974fa524a', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table crf', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-03', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.406', 1121, 'EXECUTED', '7:8107227747baa2924f126a25d5fe4195', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table crf_version', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-04', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.434', 1122, 'EXECUTED', '7:8effc6e7484404b71d1582140ec3b6d6', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table dictionary', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-05', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.462', 1123, 'EXECUTED', '7:bf43996c65ff3f8fe7fcc38ac28c5fa5', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table dynamic_event', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-06', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.503', 1124, 'EXECUTED', '7:371b554463de87240df2cb4742b78fe2', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table event_definition_crf', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-7', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.545', 1125, 'EXECUTED', '7:8690748c338d7efcd9d46c05ebaf5e35', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table item', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-8', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.585', 1126, 'EXECUTED', '7:e1112b0864c29f2cb29949c5c1522a6c', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table item_data', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-9', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.614', 1127, 'EXECUTED', '7:5cab50283b22b63d7956794c1cf6d9e3', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table item_group', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-10', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.647', 1128, 'EXECUTED', '7:7c22c355b9dd3ecc82cb4d42f01019ed', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table rule', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-11', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.682', 1129, 'EXECUTED', '7:465e93cda48b6e9a298ddf7d63ea386b', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table rule_action', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-12', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.711', 1130, 'EXECUTED', '7:a2964878fad6ee25b4a813b201dda963', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table rule_expression', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-13', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.747', 1131, 'EXECUTED', '7:545eedfac423d9673fa77d45aa4172fd', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table rule_set', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-14', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.765', 1132, 'EXECUTED', '7:767b6d6befda5c39e35ff754dd8156cc', 'modifyDataType', 'update dates type to TIMESTAMP WITH TIME ZONE in the table rule_set_audit', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-15', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.793', 1133, 'EXECUTED', '7:cdf09cd2c1e79c6dbcac1c8b3135ab4a', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table rule_set_rule', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-16', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.808', 1134, 'EXECUTED', '7:f3fecfe978650c3bffb02aa1383f1427', 'modifyDataType', 'update dates type to TIMESTAMP WITH TIME ZONE in the table rule_set_rule_audit', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-17', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.843', 1135, 'EXECUTED', '7:bdde85abc87ce62437780a9e39505e38', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table section', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-18', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.869', 1136, 'EXECUTED', '7:bdd4f4fb9c9cc6a6b9a8474055887b83', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table study_group_class', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-19', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.891', 1137, 'EXECUTED', '7:b978e36934b94445342c6c77f1638c57', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table study_module_status', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-20', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.916', 1138, 'EXECUTED', '7:62d5897a61df5add900af78ec487c0c8', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table study_user_role', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-21', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.943', 1139, 'EXECUTED', '7:90df3a71d96b4c6583fe913511db0f03', 'modifyDataType (x2)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table subject_group_map', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-22', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:01.959', 1140, 'EXECUTED', '7:d51e4dc7bd474808d78b42fd3506f266', 'modifyDataType', 'update dates type to TIMESTAMP WITH TIME ZONE in the table term', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2326-23', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2326.xml', '2016-06-15 15:15:02.006', 1141, 'EXECUTED', '7:66f5cdcfe7e0011ae37ec2e4698a5ca0', 'modifyDataType (x3)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table user_account', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2330-01', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2330.xml', '2016-06-15 15:15:02.017', 1142, 'EXECUTED', '7:1c84272b610171839120ba0901d79a41', 'dropView', 'drop view dn_age_days before change type of column date_created in the table discrepancy_note', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2330-02', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2330.xml', '2016-06-15 15:15:02.042', 1143, 'EXECUTED', '7:5f349490fcc25ac0064516ccb2f91324', 'modifyDataType', 'update dates type to TIMESTAMP WITH TIME ZONE in the table discrepancy_note', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2330-03', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2330.xml', '2016-06-15 15:15:02.053', 1144, 'EXECUTED', '7:253b8c83e4cfd01b4ad9804b034e757a', 'sql', 'recreate view dn_age_days after change type of column date_created in the table discrepancy_note', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-05-25-TICKET2330-04', 'aram', 'migration/clincaptrue/postgres/2015-05-25-TICKET2330.xml', '2016-06-15 15:15:02.081', 1145, 'EXECUTED', '7:cdf79f828ec0eb7acaa3211d7d0265e6', 'modifyDataType', 'update dates type to TIMESTAMP WITH TIME ZONE in the table audit_log_event', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-06-24-TICKET2378-CREATE-ESPAS-WIDGET', 'denis', 'migration/clincaptrue/2015-06-04-TICKET2378.xml', '2016-06-15 15:15:02.091', 1146, 'EXECUTED', '7:190032064d29e2bd311462ccb98b44bf', 'insert', 'Create "Enrollment Status Per Available Sites" widget', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-06-02-TICKET2329-01', 'aram', 'migration/clincaptrue/2015-06-02-TICKET2329.xml', '2016-06-15 15:15:02.146', 1147, 'EXECUTED', '7:dfbe074d006eb5f93af11fff9e079576', 'modifyDataType (x4)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table study_subject', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-06-02-TICKET2329-02', 'aram', 'migration/clincaptrue/2015-06-02-TICKET2329.xml', '2016-06-15 15:15:02.195', 1148, 'EXECUTED', '7:d1d2ddfcea42abc4c8a99ccce32662eb', 'modifyDataType (x3)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table subject', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-06-16-TICKET2398-1', 'denis', 'migration/clincaptrue/2015-06-16-TICKET2398.xml', '2016-06-15 15:15:02.204', 1149, 'EXECUTED', '7:9d5feb53820fa6c01f2995ba740aa67e', 'update', '"Interviewer name editable" should be added to the list of site level parameters.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-06-16-TICKET2398-2', 'denis', 'migration/clincaptrue/2015-06-16-TICKET2398.xml', '2016-06-15 15:15:02.214', 1150, 'EXECUTED', '7:3ef6ccb4f3c2ea4cf91f8f56e5aaf861', 'update', '"Interviewer date editable" should be added to the list of site level parameters.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-06-12-TICKET2328', 'aram', 'migration/clincaptrue/2015-06-12-TICKET2328.xml', '2016-06-15 15:15:02.336', 1151, 'EXECUTED', '7:a4cbe48e9fca0394cd801e428c4b60ea', 'modifyDataType (x5)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table study', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-06-23-TICKET2421', 'aram', 'migration/clincaptrue/2015-06-23-TICKET2421.xml', '2016-06-15 15:15:02.403', 1152, 'EXECUTED', '7:c4a697529484b864545969c9217ef186', 'modifyDataType (x5)', 'update dates type to TIMESTAMP WITH TIME ZONE in the table dataset', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-07-13-TICKET2453-1', 'denis', 'migration/clincaptrue/2015-07-13-TICKET2453.xml', '2016-06-15 15:15:02.414', 1153, 'EXECUTED', '7:d7f1193ebdb34b34e32bb31266df1dd0', 'update', 'Update default value for ''secondaryIdRequired'' parameter.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-07-13-TICKET2453-2', 'denis', 'migration/clincaptrue/2015-07-13-TICKET2453.xml', '2016-06-15 15:15:02.423', 1154, 'EXECUTED', '7:4ce3e9d8703948c0987f4fc817c66250', 'update', 'Update default value for ''studySubjectIdLabel'' parameter.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-07-13-TICKET2453-3', 'denis', 'migration/clincaptrue/2015-07-13-TICKET2453.xml', '2016-06-15 15:15:02.432', 1155, 'EXECUTED', '7:99227b836a7eabe49643d4abe6c7bdbe', 'update', 'Update default value for ''dateOfEnrollmentForStudyRequired'' parameter.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-07-13-TICKET2453-4', 'denis', 'migration/clincaptrue/2015-07-13-TICKET2453.xml', '2016-06-15 15:15:02.441', 1156, 'EXECUTED', '7:06113eb934c767be83c5e2dee541d59a', 'update', 'Update default value for ''genderRequired'' parameter.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-07-13-TICKET2453-5', 'denis', 'migration/clincaptrue/2015-07-13-TICKET2453.xml', '2016-06-15 15:15:02.452', 1157, 'EXECUTED', '7:675ae4ea17a1a7423a3f022c08af558a', 'update', 'Update default value for ''collectDob'' parameter.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-07-13-TICKET2453-6', 'denis', 'migration/clincaptrue/2015-07-13-TICKET2453.xml', '2016-06-15 15:15:02.461', 1158, 'EXECUTED', '7:e0e0093c88689d521ba2dd4fd3ea8ea9', 'update', 'Update default value for ''subjectPersonIdRequired'' parameter.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-07-13-TICKET2453-7', 'denis', 'migration/clincaptrue/2015-07-13-TICKET2453.xml', '2016-06-15 15:15:02.472', 1159, 'EXECUTED', '7:67ce512ed54b7a0c03e28f9560a347d2', 'update', 'Update default value for ''startDateTimeRequired'' parameter.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-07-13-TICKET2453-8', 'denis', 'migration/clincaptrue/2015-07-13-TICKET2453.xml', '2016-06-15 15:15:02.481', 1160, 'EXECUTED', '7:3a09cfe589f12dcd16296a42a2d04816', 'update', 'Update default value for ''endDateTimeRequired'' parameter.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-07-13-TICKET2453-9', 'denis', 'migration/clincaptrue/2015-07-13-TICKET2453.xml', '2016-06-15 15:15:02.491', 1161, 'EXECUTED', '7:b60f8ed39b712556485d6503f6180587', 'update', 'Update default value for ''interviewerNameRequired'' parameter.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-07-13-TICKET2453-10', 'denis', 'migration/clincaptrue/2015-07-13-TICKET2453.xml', '2016-06-15 15:15:02.501', 1162, 'EXECUTED', '7:bb9e3cb39febc08e9c67d6ea7497b09b', 'update', 'Update default value for ''interviewDateRequired'' parameter.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-07-13-TICKET2453-11', 'denis', 'migration/clincaptrue/2015-07-13-TICKET2453.xml', '2016-06-15 15:15:02.51', 1163, 'EXECUTED', '7:76814b833e79de663e8db52b39b504f0', 'update', 'Update default value for ''showYearsInCalendar'' parameter.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-07-13-TICKET2453-12', 'denis', 'migration/clincaptrue/2015-07-13-TICKET2453.xml', '2016-06-15 15:15:02.519', 1164, 'EXECUTED', '7:25b5022c5cb3c36b1fa052b501415192', 'update', 'Update default value for ''allowRulesAutoScheduling'' parameter.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-07-31-TICKET2515', 'aram', 'migration/clincaptrue/2015-07-31-TICKET2515.xml', '2016-06-15 15:15:02.528', 1165, 'EXECUTED', '7:5fcd87b43c20192779d5ed3ffd4d1aa0', 'sql', 'Fill in empty start date fields with creation date in table study_event', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-07-07-TICKET1558', 'vincent', 'migration/clincaptrue/2015-07-08-TICKET1558.xml', '2016-06-15 15:15:02.571', 1166, 'EXECUTED', '7:b759bbb2ce0e617a1df18df4132e5384', 'insert (x11)', 'Add features level', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-08-13-TICKET2519', 'aram', 'migration/clincaptrue/2015-08-13-TICKET2519.xml', '2016-06-15 15:15:02.587', 1167, 'EXECUTED', '7:216ce73343834288914dd45558c90820', 'sql', 'Fix for CRF with sliders', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-08-10-TICKET2522', 'aram', 'migration/clincaptrue/2015-08-10-TICKET2522.xml', '2016-06-15 15:15:02.598', 1168, 'EXECUTED', '7:b99b4d7a2e846cde0e33325a8f20daa1', 'insert', 'Insert record for a new status "Partial Data Entry"', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-09-08-TICKET2553', 'denis', 'migration/clincaptrue/2015-09-08-TICKET2553.xml', '2016-06-15 15:15:02.608', 1169, 'EXECUTED', '7:bcb2c24a5ed10b6d9797d38a2750c712', 'addColumn', 'Add new column to "crf" table to store source of the CRF.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-08-07-TICKET2443', 'vincent', 'migration/clincaptrue/2015-08-07-TICKET2443.xml', '2016-06-15 15:15:02.617', 1170, 'EXECUTED', '7:4e7bc36557ed736c2681ad678f020c02', 'update', 'set markImportedCRFAsCompleted as overridable.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-09-21-TICKET2353-pg', 'denis', 'migration/clincaptrue/postgres/2015-09-21-TICKET2353.xml', '2016-06-15 15:15:02.629', 1171, 'EXECUTED', '7:c2b2ae33e33f3e13133c3c0d6a7ef7fd', 'sql', 'Create studyEvaluator study parameter value if it''s not exists.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-09-21-TICKET2353-0', 'denis', 'migration/clincaptrue/2015-09-21-TICKET2353.xml', '2016-06-15 15:15:02.639', 1172, 'EXECUTED', '7:f077f160fbc845c98c56f7e5f5957b2f', 'sql', 'Migrate value from old parameter to the new one.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-09-21-TICKET2353-1', 'denis', 'migration/clincaptrue/2015-09-21-TICKET2353.xml', '2016-06-15 15:15:02.65', 1173, 'EXECUTED', '7:aef8085265c85ac78c54c541761795d0', 'delete', 'Delete "Allow CRF evaluation?" from study parameter values.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-09-21-TICKET2353-2', 'denis', 'migration/clincaptrue/2015-09-21-TICKET2353.xml', '2016-06-15 15:15:02.667', 1174, 'EXECUTED', '7:b453834ff8a5b4d73a0a4cf58785ca7a', 'delete', 'Delete "Allow CRF evaluation?" from study parameters.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-09-21-TICKET2353-3', 'denis', 'migration/clincaptrue/2015-09-21-TICKET2353.xml', '2016-06-15 15:15:02.677', 1175, 'EXECUTED', '7:bec7d44c1dc1e056dccb22c541c6c86d', 'delete', 'Delete Evaluator from system.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-09-21-TICKET2353-4', 'denis', 'migration/clincaptrue/2015-09-21-TICKET2353.xml', '2016-06-15 15:15:02.689', 1176, 'EXECUTED', '7:4ea07397573085594ad98e55b8bcf98f', 'delete', 'Delete Evaluate with Context from system.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-09-17-TICKET2527', 'igor', 'migration/clincaptrue/2015-09-17-TICKET2527.xml', '2016-06-15 15:15:02.704', 1177, 'EXECUTED', '7:ec4bf536d40c824c020bcd2fa2cf2c55', 'createTable', 'Create event_crf_section table to implement Partial Save', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-09-17-TICKET2527-2', 'igor', 'migration/clincaptrue/2015-09-17-TICKET2527.xml', '2016-06-15 15:15:02.733', 1178, 'EXECUTED', '7:bba6b3534ca5a329d445a23756f444f1', 'addForeignKeyConstraint (x3)', 'Add foreign keys for event_crf_section', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-09-17-TICKET2563', 'thickerson', 'migration/clincaptrue/2015-09-17-TICKET2563.xml', '2016-06-15 15:15:02.76', 1179, 'EXECUTED', '7:6e3d7ecbeed3b0105f262232e6604862', 'modifyDataType', 'Expand the ability to store a longer message in the DB for Rules', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-09-29-TICKET2354-pg', 'thickerson', 'migration/clincaptrue/postgres/2015-09-29-TICKET2354.xml', '2016-06-15 15:15:02.774', 1180, 'EXECUTED', '7:dc7adcce9d2482caef5c6baefc14fe76', 'sql', 'Auto-populate default value for medicalCoding while upgrading, for all parent studies', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-09-29-TICKET2354-1', 'thickerson', 'migration/clincaptrue/2015-09-29-TICKET2354.xml', '2016-06-15 15:15:02.786', 1181, 'EXECUTED', '7:74bb1b885385a62de4df0a510abfee43', 'delete', 'Delete "Allow Medical Coding?" from study parameter values.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-09-29-TICKET2354-2', 'thickerson', 'migration/clincaptrue/2015-09-29-TICKET2354.xml', '2016-06-15 15:15:02.798', 1182, 'EXECUTED', '7:3601e7dd2c0e7a3c4a332e72a54c8af1', 'delete', 'Delete "Allow Medical Coding?" from study parameters.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-09-30-TICKET2579-01', 'skirpichenok', 'migration/clincaptrue/2015-09-30-TICKET2579.xml', '2016-06-15 15:15:02.808', 1183, 'EXECUTED', '7:00b9b6c2df057e0b8d182b42db51b432', 'insert', 'Add the divider data type to the database', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-09-30-TICKET2579-02', 'skirpichenok', 'migration/clincaptrue/2015-09-30-TICKET2579.xml', '2016-06-15 15:15:02.821', 1184, 'EXECUTED', '7:0334c5cc029d7ac357d9e4720be41635', 'insert', 'Add the label data type to the database', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-10-13-TICKET2589-01', 'skirpichenok', 'migration/clincaptrue/2015-10-13-TICKET2589.xml', '2016-06-15 15:15:02.834', 1185, 'EXECUTED', '7:c2891fc0bec3e4fdad741427f584408b', 'dropForeignKeyConstraint, dropColumn', 'Delete the study_subject_id field from the event_crf_section table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-10-13-TICKET2589-02', 'skirpichenok', 'migration/clincaptrue/postgres/2015-10-13-TICKET2589.xml', '2016-06-15 15:15:02.843', 1186, 'EXECUTED', '7:1e4142721e8c75760f53a7d8de040ded', 'sql', 'Create stored function', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-10-20-TICKET2497-0', 'denis', 'migration/clincaptrue/2015-10-20-TICKET2497.xml', '2016-06-15 15:15:02.854', 1187, 'MARK_RAN', '7:286eba50cd6becaa7e336aa62474e691', 'dropColumn', 'Remove unused OC column ''participant_form'' from event_definition_crf table.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-01-13-TICKET2783-12', 'skirpichenok', 'migration/clincaptrue/postgres/2016-01-13-TICKET2783.xml', '2016-06-15 15:15:03.399', 1221, 'EXECUTED', '7:e9ee2b241319e8a45cf4ed0f61a1fab3', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-10-20-TICKET2497-1', 'denis', 'migration/clincaptrue/2015-10-20-TICKET2497.xml', '2016-06-15 15:15:02.864', 1188, 'MARK_RAN', '7:95b8cb1a7cca5018345fecbd92b50a50', 'dropColumn', 'Remove unused OC column ''submission_url'' from event_definition_crf table.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-10-20-TICKET2497-2', 'denis', 'migration/clincaptrue/2015-10-20-TICKET2497.xml', '2016-06-15 15:15:02.874', 1189, 'MARK_RAN', '7:7cebf07b050809bfbc6851cda94a8d8f', 'dropColumn', 'Remove unused OC column ''allow_anonymous_submission'' from event_definition_crf table.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-10-20-TICKET2497-3', 'denis', 'migration/clincaptrue/2015-10-20-TICKET2497.xml', '2016-06-15 15:15:02.885', 1190, 'MARK_RAN', '7:3b094d8fe1d8d42c94f1b05c975aacb4', 'dropColumn', 'Remove unused OC column ''time_zone'' from table study_subject.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-11-03-TICKET2637-0', 'denis', 'migration/clincaptrue/postgres/2015-11-03-TICKET2637.xml', '2016-06-15 15:15:02.897', 1191, 'EXECUTED', '7:a3558cc358755661048ef4438643d080', 'sql', 'Add new index for event_crf table.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-11-03-TICKET2637-1', 'denis', 'migration/clincaptrue/postgres/2015-11-03-TICKET2637.xml', '2016-06-15 15:15:02.911', 1192, 'EXECUTED', '7:6d5fb582df2d70b5af9c5d7522b54569', 'sql', 'Add new index for event_crf table.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-11-03-TICKET2637-2', 'denis', 'migration/clincaptrue/postgres/2015-11-03-TICKET2637.xml', '2016-06-15 15:15:02.922', 1193, 'EXECUTED', '7:86613cac391bf94cde9c2e00d591afa9', 'sql', 'Add new index for event_crf table.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-11-03-TICKET2637-3', 'denis', 'migration/clincaptrue/postgres/2015-11-03-TICKET2637.xml', '2016-06-15 15:15:02.934', 1194, 'EXECUTED', '7:653adba00ccaae38a9675c80cd69b7fa', 'sql', 'Add new index for study_subject table.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-11-03-TICKET2637-4', 'denis', 'migration/clincaptrue/postgres/2015-11-03-TICKET2637.xml', '2016-06-15 15:15:02.943', 1195, 'MARK_RAN', '7:d0c2f32387444d01ed92a2ac819118b8', 'sql', 'Add new index for study_subject table.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-11-03-TICKET2637-5', 'denis', 'migration/clincaptrue/postgres/2015-11-03-TICKET2637.xml', '2016-06-15 15:15:02.952', 1196, 'MARK_RAN', '7:0dfe88c7efee9f5f6300849f195a5ec9', 'sql', 'Add new index for discrepancy_note table.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-11-03-TICKET2637-6', 'denis', 'migration/clincaptrue/postgres/2015-11-03-TICKET2637.xml', '2016-06-15 15:15:02.964', 1197, 'EXECUTED', '7:323cc85e8c6857c8c0103c03ee0ba393', 'sql', 'Add new index for study_event table.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-11-03-TICKET2637-7', 'denis', 'migration/clincaptrue/postgres/2015-11-03-TICKET2637.xml', '2016-06-15 15:15:02.978', 1198, 'EXECUTED', '7:0d56b551afcf4e932e6b64d4faa94d5d', 'sql', 'Add new index for study_event table.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-11-05-TICKET2627-0', 'denis', 'migration/clincaptrue/2015-11-05-TICKET2627.xml', '2016-06-15 15:15:02.994', 1199, 'EXECUTED', '7:f6e92cdbc8ae84bd0939fffe2764ef40', 'update', 'Allow root to edit "File path" property.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-10-27-TICKET2640', 'igor', 'migration/clincaptrue/2015-10-27-TICKET2640.xml', '2016-06-15 15:15:03.007', 1200, 'EXECUTED', '7:30391ca6ccac9e4d6b904c2fb72e2bdb', 'insert', 'Insert record for a new status "Partial Double Data Entry"', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-11-06-TICKET2578-0', 'denis', 'migration/clincaptrue/2015-11-06-TICKET2578.xml', '2016-06-15 15:15:03.02', 1201, 'EXECUTED', '7:cb6f61cb2c8b88872f62050f7aa91e6b', 'createTable', 'Create new table item_render_metadata for new CRF render support.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-11-06-TICKET2578-1', 'denis', 'migration/clincaptrue/2015-11-06-TICKET2578.xml', '2016-06-15 15:15:03.037', 1202, 'EXECUTED', '7:18a332a99f3e9c20a3be705796b6ea3e', 'addForeignKeyConstraint (x2)', 'Add foreign keys for item_render_metadata table.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-10-05-TICKET2513-01', 'skirpichenok', 'migration/clincaptrue/2015-10-05-TICKET2513.xml', '2016-06-15 15:15:03.138', 1203, 'EXECUTED', '7:fd5e349614a57ded9a3794ee49011bde', 'addColumn (x5)', 'Add states and old_status_id columns to some tables', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-10-05-TICKET2513-02', 'skirpichenok', 'migration/clincaptrue/2015-10-05-TICKET2513.xml', '2016-06-15 15:15:03.155', 1204, 'EXECUTED', '7:cdcf4b537e6801d93a6458b25bbdfbd6', 'sql', 'Add stored functions', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-11-15-TICKET2656-10', 'skirpichenok', 'migration/clincaptrue/postgres/2015-11-15-TICKET2656.xml', '2016-06-15 15:15:03.167', 1205, 'EXECUTED', '7:642b91f5f8466bb9b8716b000ad0324b', 'sql', 'drop stored functions', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-11-15-TICKET2656-11', 'skirpichenok', 'migration/clincaptrue/postgres/2015-11-15-TICKET2656.xml', '2016-06-15 15:15:03.18', 1206, 'EXECUTED', '7:6b5db4f8b2de62f2fd015c02d85766f7', 'sql', 'Create stored functions', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-10-28-TICKET2607', 'igor', 'migration/clincaptrue/2015-10-28-TICKET2607.xml', '2016-06-15 15:15:03.19', 1207, 'EXECUTED', '7:dc28afd460bf62a58e7adc92d45a752b', 'addColumn', 'Create partial_dde_value column in item_data table to implement Partial Save for DDE', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-11-10-TICKET2541-1', 'denis', 'migration/clincaptrue/2015-11-10-TICKET2541-1.xml', '2016-06-15 15:15:03.207', 1208, 'EXECUTED', '7:370680021c2b8aaa921187002007a462', 'createTable', 'Add new entity edc_item_metadata to store item-level SDV configuration per event_definition_crf.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-11-10-TICKET2541-2', 'denis', 'migration/clincaptrue/2015-11-10-TICKET2541-1.xml', '2016-06-15 15:15:03.236', 1209, 'EXECUTED', '7:516d75c22d71cd0c134e159239e490bc', 'addForeignKeyConstraint (x4)', 'Add constrains for edc_item_metadata table.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-11-10-TICKET2541-1', 'denis', 'migration/clincaptrue/postgres/2015-11-10-TICKET2541.xml', '2016-06-15 15:15:03.247', 1210, 'EXECUTED', '7:7e6b7e2471407265fbf5e166181cba47', 'createProcedure', 'Create procedure to copy item-level sdv
			from item_form_metadata into edc_item_metadata', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-11-10-TICKET2541-2', 'denis', 'migration/clincaptrue/postgres/2015-11-10-TICKET2541.xml', '2016-06-15 15:15:03.257', 1211, 'EXECUTED', '7:b075a57e9ae7428aeb7bee450c822084', 'sql', 'Execute and drop procedure move_item_level_sdv_metadata().', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-09-11-TICKET2541-3', 'denis', 'migration/clincaptrue/2015-11-10-TICKET2541-2.xml', '2016-06-15 15:15:03.267', 1212, 'EXECUTED', '7:253504c50f49410df8e5b4b758b2f0f7', 'dropColumn', 'Remove column sdv_required from item_form_metadata.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-12-07-TICKET2708-0', 'denis', 'migration/clincaptrue/2015-12-07-TICKET2708.xml', '2016-06-15 15:15:03.277', 1213, 'EXECUTED', '7:442e2601a9a0f8200dac40ad24acc741', 'sql', 'Update item_data status_id if there are some active rows for removed crf_versions.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-12-07-TICKET2708-1', 'denis', 'migration/clincaptrue/2015-12-07-TICKET2708.xml', '2016-06-15 15:15:03.288', 1214, 'EXECUTED', '7:cf7c908db51dc3ca9598c0f38ba10655', 'sql', 'Update event_crf status_id if there are some active rows for removed crf_versions.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-12-09-TICKET2671-0', 'denis', 'migration/clincaptrue/2015-12-09-TICKET2671.xml', '2016-06-15 15:15:03.305', 1215, 'EXECUTED', '7:45c1554b4f0a74bedf25b014633053bc', 'createTable', 'Add new entity audit_log_randomization.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2015-12-09-TICKET2671-1', 'denis', 'migration/clincaptrue/2015-12-09-TICKET2671.xml', '2016-06-15 15:15:03.33', 1216, 'EXECUTED', '7:3480dd07df02aa3d24b135bc0ad7ebe3', 'addForeignKeyConstraint (x3)', 'Add constrains for audit_log_randomization table.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-01-05-TICKET2766-0', 'denis', 'migration/clincaptrue/2016-01-05-TICKET2766.xml', '2016-06-15 15:15:03.349', 1217, 'EXECUTED', '7:0aebaecba44e93c9ffd4a710cc682ed7', 'modifyDataType', 'Change length of user_name column in "study_user_role" table.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-01-05-TICKET2766-1', 'denis', 'migration/clincaptrue/2016-01-05-TICKET2766.xml', '2016-06-15 15:15:03.36', 1218, 'EXECUTED', '7:5144f63a2a65a2f4010e8b24fe700699', 'modifyDataType', 'Change length of user_name column in "authorities" table.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-01-13-TICKET2783-10', 'skirpichenok', 'migration/clincaptrue/postgres/2016-01-13-TICKET2783.xml', '2016-06-15 15:15:03.375', 1219, 'EXECUTED', '7:0e80ed0f6310a9d4dbfc4731c43e0405', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-01-13-TICKET2783-11', 'skirpichenok', 'migration/clincaptrue/postgres/2016-01-13-TICKET2783.xml', '2016-06-15 15:15:03.387', 1220, 'EXECUTED', '7:a37046bffd3856ae11fe79cab3096e0e', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-01-13-TICKET2783-13', 'skirpichenok', 'migration/clincaptrue/postgres/2016-01-13-TICKET2783.xml', '2016-06-15 15:15:03.411', 1222, 'EXECUTED', '7:6c4a6391c186dc7cc6ea57d4e0b3b6c8', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-01-13-TICKET2783-14', 'skirpichenok', 'migration/clincaptrue/postgres/2016-01-13-TICKET2783.xml', '2016-06-15 15:15:03.423', 1223, 'EXECUTED', '7:0a71d820b34bc916ddfb850e372944f8', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-01-13-TICKET2783-15', 'skirpichenok', 'migration/clincaptrue/postgres/2016-01-13-TICKET2783.xml', '2016-06-15 15:15:03.435', 1224, 'EXECUTED', '7:d209a88e1973310e9ae203d6beecb72b', 'sql', '', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-03-01-TICKET2799', 'Frank', 'migration/clincaptrue/2016-03-01-TICKET2799.xml', '2016-06-15 15:15:03.469', 1225, 'EXECUTED', '7:08a66350f000f4dc7c18e6578b26bd1c', 'update (x8)', 'Add widget support for Study Sponsor role', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-03-02-TICKET2917', 'Igor', 'migration/clincaptrue/2016-03-02-TICKET2917.xml', '2016-06-15 15:15:03.483', 1226, 'EXECUTED', '7:334324ee44ee9a4a091c1f87e6ab62be', 'update (x2)', 'Add widget support for Study Sponsor role', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-03-31-TICKET1790-01', 'skirpichenok', 'migration/clincaptrue/2016-03-31-TICKET1790.xml', '2016-06-15 15:15:03.494', 1227, 'EXECUTED', '7:eea90f24e7477bb1b7303995bb4d43e4', 'sql', 'fix some values', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-03-23-TICKET2119', 'aram', 'migration/clincaptrue/2016-03-23-TICKET2119.xml', '2016-06-15 15:15:03.504', 1228, 'EXECUTED', '7:c15ce0287c026a52ac78496aacd6e967', 'insert', 'Adding new resolution status "New (DCF)" for DNs', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-04-01-TICKET2992-01', 'skirpichenok', 'migration/clincaptrue/2016-04-01-TICKET2992.xml', '2016-06-15 15:15:03.555', 1229, 'EXECUTED', '7:d13367434123b5bd5bb2b6ecf3640dbd', 'addColumn', 'add origin column to the study table', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-04-08-TICKET2963-02', 'skirpichenok', 'migration/clincaptrue/2016-04-08-TICKET2963.xml', '2016-06-15 15:15:03.568', 1230, 'EXECUTED', '7:07e346e3ce24bc582a69910f74d4cf8f', 'sql', 'fix some values', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-04-09-TICKET2936-1', 'skirpichenok', 'migration/clincaptrue/postgres/2016-04-09-TICKET2936.xml', '2016-06-15 15:15:03.577', 1231, 'EXECUTED', '7:1f679be576bc08e5168e802aee4cbf7b', 'sql', 'drop stored functions', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-04-09-TICKET2936-2', 'skirpichenok', 'migration/clincaptrue/postgres/2016-04-09-TICKET2936.xml', '2016-06-15 15:15:03.587', 1232, 'EXECUTED', '7:12fea53700a97aa1293f2f6d85938805', 'sql', 'Create stored functions', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-04-20-TICKET2700-01', 'skirpichenok', 'migration/clincaptrue/2016-04-20-TICKET2700.xml', '2016-06-15 15:15:03.597', 1233, 'EXECUTED', '7:d31e4191b6decef104f1d2dbcf3621d6', 'sql', 'fix some values', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-04-28-TICKET2979-01', 'skirpichenok', 'migration/clincaptrue/2016-04-28-TICKET2979.xml', '2016-06-15 15:15:03.607', 1234, 'EXECUTED', '7:94a6594a04806dbf7d917c6b82ec83ab', 'sql', 'fix some values', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-04-28-TICKET2979-02', 'skirpichenok', 'migration/clincaptrue/2016-04-28-TICKET2979.xml', '2016-06-15 15:15:03.616', 1235, 'EXECUTED', '7:14d335f2a3701ddbb3a3dbc3b1df2327', 'sql', 'fix some values', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-04-29-TICKET3010-01', 'aram', 'migration/clincaptrue/2016-04-29-TICKET3010.xml', '2016-06-15 15:15:03.64', 1236, 'EXECUTED', '7:34245075575af0746299ce418cef6570', 'addColumn', 'Add new filed pseudo_child to the item_form_metadata.', NULL, '3.4.2', NULL, NULL);
INSERT INTO databasechangelog VALUES ('2016-04-29-TICKET3010-02', 'aram', 'migration/clincaptrue/2016-04-29-TICKET3010.xml', '2016-06-15 15:15:03.651', 1237, 'EXECUTED', '7:1be7ea7c25983bf9dfad343b4d8ce7dd', 'dropColumn', 'Drop unused field decision_condition_id from the item_form_metadata.', NULL, '3.4.2', NULL, NULL);


--
-- TOC entry 3156 (class 0 OID 143841355)
-- Dependencies: 140
-- Data for Name: databasechangeloglock; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO databasechangeloglock VALUES (1, false, NULL, NULL);


--
-- TOC entry 3169 (class 0 OID 143841493)
-- Dependencies: 163
-- Data for Name: dataset; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3170 (class 0 OID 143841518)
-- Dependencies: 164
-- Data for Name: dataset_crf_version_map; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3232 (class 0 OID 143843015)
-- Dependencies: 270
-- Data for Name: dataset_item_status; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO dataset_item_status VALUES (1, 'completed', 'Data from CRFs Marked Complete');
INSERT INTO dataset_item_status VALUES (2, 'non_completed', 'Data from CRFs not Marked Complete');
INSERT INTO dataset_item_status VALUES (3, 'completed_and_non_completed', 'Data from all Available CRFs ');


--
-- TOC entry 3171 (class 0 OID 143841524)
-- Dependencies: 165
-- Data for Name: dataset_study_group_class_map; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3248 (class 0 OID 143843938)
-- Dependencies: 310
-- Data for Name: dictionary; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3246 (class 0 OID 143843705)
-- Dependencies: 307
-- Data for Name: discrepancy_description; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO discrepancy_description VALUES (1, 'Query response monitored', '', 1, 'both', 1, 0);
INSERT INTO discrepancy_description VALUES (2, 'CRF data was correctly entered', '', 1, 'both', 1, 0);
INSERT INTO discrepancy_description VALUES (3, 'Need additional clarification', '', 1, 'both', 1, 0);
INSERT INTO discrepancy_description VALUES (4, 'Requested information is provided', '', 1, 'both', 1, 0);
INSERT INTO discrepancy_description VALUES (5, 'Corrected CRF data', '', 1, 'both', 2, 0);
INSERT INTO discrepancy_description VALUES (6, 'CRF data change monitored', '', 1, 'both', 2, 0);
INSERT INTO discrepancy_description VALUES (7, 'Calendared event monitored', '', 1, 'both', 2, 0);
INSERT INTO discrepancy_description VALUES (8, 'Failed edit check monitored', '', 1, 'both', 2, 0);
INSERT INTO discrepancy_description VALUES (9, 'Corrected CRF data entry error', '', 1, 'both', 3, 0);
INSERT INTO discrepancy_description VALUES (10, 'Source data was missing', '', 1, 'both', 3, 0);
INSERT INTO discrepancy_description VALUES (11, 'Source data was incorrect', '', 1, 'both', 3, 0);
INSERT INTO discrepancy_description VALUES (12, 'Information was not available', '', 1, 'both', 3, 0);


--
-- TOC entry 3172 (class 0 OID 143841600)
-- Dependencies: 167
-- Data for Name: discrepancy_note; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3173 (class 0 OID 143841611)
-- Dependencies: 169
-- Data for Name: discrepancy_note_type; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO discrepancy_note_type VALUES (1, 'Failed Validation Check', '');
INSERT INTO discrepancy_note_type VALUES (5, 'Other', '');
INSERT INTO discrepancy_note_type VALUES (2, 'Annotation', '');
INSERT INTO discrepancy_note_type VALUES (3, 'Query', '');
INSERT INTO discrepancy_note_type VALUES (4, 'Reason for Change', '');
INSERT INTO discrepancy_note_type VALUES (6, 'Incomplete', '');
INSERT INTO discrepancy_note_type VALUES (7, 'Unclear/Unreadable', '');


--
-- TOC entry 3174 (class 0 OID 143841617)
-- Dependencies: 170
-- Data for Name: dn_event_crf_map; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3175 (class 0 OID 143841620)
-- Dependencies: 171
-- Data for Name: dn_item_data_map; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3176 (class 0 OID 143841623)
-- Dependencies: 172
-- Data for Name: dn_study_event_map; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3177 (class 0 OID 143841626)
-- Dependencies: 173
-- Data for Name: dn_study_subject_map; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3178 (class 0 OID 143841629)
-- Dependencies: 174
-- Data for Name: dn_subject_map; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3241 (class 0 OID 143843380)
-- Dependencies: 288
-- Data for Name: dyn_item_form_metadata; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3242 (class 0 OID 143843389)
-- Dependencies: 290
-- Data for Name: dyn_item_group_metadata; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3245 (class 0 OID 143843674)
-- Dependencies: 305
-- Data for Name: dynamic_event; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3259 (class 0 OID 143845149)
-- Dependencies: 334
-- Data for Name: edc_item_metadata; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3179 (class 0 OID 143841634)
-- Dependencies: 176
-- Data for Name: event_crf; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3257 (class 0 OID 143844997)
-- Dependencies: 330
-- Data for Name: event_crf_section; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3180 (class 0 OID 143841646)
-- Dependencies: 178
-- Data for Name: event_definition_crf; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3181 (class 0 OID 143841655)
-- Dependencies: 180
-- Data for Name: export_format; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO export_format VALUES (1, 'text/plain', 'Default export format for tab-delimited text', 'text/plain');
INSERT INTO export_format VALUES (2, 'text/plain', 'Default export format for comma-delimited text', 'text/plain');
INSERT INTO export_format VALUES (3, 'application/vnd.ms-excel', 'Default export format for Excel files', 'application/vnd.ms-excel');
INSERT INTO export_format VALUES (4, 'text/plain', 'Default export format for CDISC ODM XML files', 'text/plain');
INSERT INTO export_format VALUES (5, 'text/plain', 'Default export format for SAS files', 'text/plain');


--
-- TOC entry 3182 (class 0 OID 143841680)
-- Dependencies: 182
-- Data for Name: group_class_types; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO group_class_types VALUES (1, 'Arm', NULL);
INSERT INTO group_class_types VALUES (2, 'Family/Pedigree', NULL);
INSERT INTO group_class_types VALUES (3, 'Demographic', NULL);
INSERT INTO group_class_types VALUES (5, 'Other', NULL);
INSERT INTO group_class_types VALUES (4, 'Dynamic Group', NULL);


--
-- TOC entry 3183 (class 0 OID 143841691)
-- Dependencies: 184
-- Data for Name: item; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3184 (class 0 OID 143841702)
-- Dependencies: 186
-- Data for Name: item_data; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3185 (class 0 OID 143841713)
-- Dependencies: 188
-- Data for Name: item_data_type; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO item_data_type VALUES (1, 'BL', 'Boolean', NULL, NULL);
INSERT INTO item_data_type VALUES (2, 'BN', 'BooleanNonNull', NULL, NULL);
INSERT INTO item_data_type VALUES (3, 'ED', 'Encapsulated Data', NULL, NULL);
INSERT INTO item_data_type VALUES (4, 'TEL', 'A telecommunication address', NULL, NULL);
INSERT INTO item_data_type VALUES (5, 'ST', 'Character String', NULL, NULL);
INSERT INTO item_data_type VALUES (6, 'INT', 'Integer', NULL, NULL);
INSERT INTO item_data_type VALUES (7, 'REAL', 'Floating', NULL, NULL);
INSERT INTO item_data_type VALUES (8, 'SET', NULL, 'a value that contains other distinct values', NULL);
INSERT INTO item_data_type VALUES (9, 'DATE', 'date', 'date', NULL);
INSERT INTO item_data_type VALUES (10, 'PDATE', 'partial date', 'year only or year with month or date', NULL);
INSERT INTO item_data_type VALUES (11, 'FILE', 'File', 'File name, extension and path', NULL);
INSERT INTO item_data_type VALUES (12, 'CODE', 'Medical Coding', 'Type to be coded medically', NULL);
INSERT INTO item_data_type VALUES (13, 'DIVIDER', 'Divider', 'Type to be divider', NULL);
INSERT INTO item_data_type VALUES (14, 'LABEL', 'Label', 'Type to be label', NULL);


--
-- TOC entry 3186 (class 0 OID 143841724)
-- Dependencies: 190
-- Data for Name: item_form_metadata; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3187 (class 0 OID 143841735)
-- Dependencies: 192
-- Data for Name: item_group; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3188 (class 0 OID 143841743)
-- Dependencies: 194
-- Data for Name: item_group_metadata; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3189 (class 0 OID 143841754)
-- Dependencies: 196
-- Data for Name: item_reference_type; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO item_reference_type VALUES (1, 'literal', NULL);


--
-- TOC entry 3258 (class 0 OID 143845042)
-- Dependencies: 332
-- Data for Name: item_render_metadata; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3236 (class 0 OID 143843120)
-- Dependencies: 278
-- Data for Name: measurement_unit; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3190 (class 0 OID 143841765)
-- Dependencies: 198
-- Data for Name: null_value_type; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO null_value_type VALUES (1, 'NI', 'NoInformation', NULL, NULL);
INSERT INTO null_value_type VALUES (2, 'NA', 'not applicable', NULL, NULL);
INSERT INTO null_value_type VALUES (3, 'UNK', 'unknown', NULL, NULL);
INSERT INTO null_value_type VALUES (4, 'NASK', 'not asked', NULL, NULL);
INSERT INTO null_value_type VALUES (5, 'ASKU', 'asked but unknown', NULL, NULL);
INSERT INTO null_value_type VALUES (6, 'NAV', 'temporarily unavailable', NULL, NULL);
INSERT INTO null_value_type VALUES (7, 'OTH', 'other', NULL, NULL);
INSERT INTO null_value_type VALUES (8, 'PINF', 'positive infinity', NULL, NULL);
INSERT INTO null_value_type VALUES (9, 'NINF', 'negative infinity', NULL, NULL);
INSERT INTO null_value_type VALUES (10, 'MSK', 'masked', NULL, NULL);
INSERT INTO null_value_type VALUES (11, 'NP', 'not present', NULL, NULL);
INSERT INTO null_value_type VALUES (12, 'NPE', 'not performed', NULL, NULL);


--
-- TOC entry 3222 (class 0 OID 143842879)
-- Dependencies: 259
-- Data for Name: oc_qrtz_blob_triggers; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3223 (class 0 OID 143842887)
-- Dependencies: 260
-- Data for Name: oc_qrtz_calendars; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3224 (class 0 OID 143842895)
-- Dependencies: 261
-- Data for Name: oc_qrtz_cron_triggers; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3225 (class 0 OID 143842903)
-- Dependencies: 262
-- Data for Name: oc_qrtz_fired_triggers; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3226 (class 0 OID 143842911)
-- Dependencies: 263
-- Data for Name: oc_qrtz_job_details; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO oc_qrtz_job_details VALUES ('legacyJobDetail', 'DEFAULT', NULL, 'org.akaza.openclinica.job.LegacyJobConverterJob', true, false, '\\254\\355\\000\\005sr\\000\\025org.quartz.JobDataMap\\237\\260\\203\\350\\277\\251\\260\\313\\002\\000\\000xr\\000&org.quartz.utils.StringKeyDirtyFlagMap\\202\\010\\350\\303\\373\\305](\\002\\000\\001Z\\000\\023allowsTransientDataxr\\000\\035org.quartz.utils.DirtyFlagMap\\023\\346.\\255(v\\012\\316\\002\\000\\002Z\\000\\005dirtyL\\000\\003mapt\\000\\017Ljava/util/Map;xp\\001sr\\000\\021java.util.HashMap\\005\\007\\332\\301\\303\\026`\\321\\003\\000\\002F\\000\\012loadFactorI\\000\\011thresholdxp?@\\000\\000\\000\\000\\000\\014w\\010\\000\\000\\000\\020\\000\\000\\000\\001t\\000\\007timeoutt\\000\\0015x\\000', false, false, 'schedulerFactoryBean');


--
-- TOC entry 3227 (class 0 OID 143842927)
-- Dependencies: 264
-- Data for Name: oc_qrtz_locks; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO oc_qrtz_locks VALUES ('TRIGGER_ACCESS', 'schedulerFactoryBean');
INSERT INTO oc_qrtz_locks VALUES ('JOB_ACCESS', 'schedulerFactoryBean');
INSERT INTO oc_qrtz_locks VALUES ('CALENDAR_ACCESS', 'schedulerFactoryBean');
INSERT INTO oc_qrtz_locks VALUES ('STATE_ACCESS', 'schedulerFactoryBean');
INSERT INTO oc_qrtz_locks VALUES ('MISFIRE_ACCESS', 'schedulerFactoryBean');


--
-- TOC entry 3228 (class 0 OID 143842932)
-- Dependencies: 265
-- Data for Name: oc_qrtz_paused_trigger_grps; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3229 (class 0 OID 143842937)
-- Dependencies: 266
-- Data for Name: oc_qrtz_scheduler_state; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3230 (class 0 OID 143842942)
-- Dependencies: 267
-- Data for Name: oc_qrtz_simple_triggers; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO oc_qrtz_simple_triggers VALUES ('legacyJobTrigger', 'legacyJobConverter', 0, 0, 0, 'schedulerFactoryBean');


--
-- TOC entry 3247 (class 0 OID 143843838)
-- Dependencies: 308
-- Data for Name: oc_qrtz_simprop_triggers; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3231 (class 0 OID 143842955)
-- Dependencies: 268
-- Data for Name: oc_qrtz_triggers; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO oc_qrtz_triggers VALUES ('legacyJobTrigger', 'legacyJobConverter', 'legacyJobDetail', 'DEFAULT', NULL, 1465993804073, -1, 0, 'WAITING', 'SIMPLE', 1465993804073, 0, NULL, 0, '', 'schedulerFactoryBean');


--
-- TOC entry 3191 (class 0 OID 143841782)
-- Dependencies: 200
-- Data for Name: openclinica_version; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO openclinica_version VALUES (1, '', NULL, 0, '2016-06-15 15:15:08.531');


--
-- TOC entry 3237 (class 0 OID 143843163)
-- Dependencies: 280
-- Data for Name: password; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3192 (class 0 OID 143841804)
-- Dependencies: 202
-- Data for Name: resolution_status; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO resolution_status VALUES (1, 'New', '');
INSERT INTO resolution_status VALUES (2, 'Updated', '');
INSERT INTO resolution_status VALUES (3, 'Resolution Proposed', '');
INSERT INTO resolution_status VALUES (4, 'Closed', '');
INSERT INTO resolution_status VALUES (5, 'Not Applicable', '');
INSERT INTO resolution_status VALUES (6, 'new_dcf', '');


--
-- TOC entry 3193 (class 0 OID 143841812)
-- Dependencies: 204
-- Data for Name: response_set; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3194 (class 0 OID 143841823)
-- Dependencies: 206
-- Data for Name: response_type; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO response_type VALUES (1, 'text', 'free form text entry limited to one line');
INSERT INTO response_type VALUES (2, 'textarea', 'free form text area display');
INSERT INTO response_type VALUES (3, 'checkbox', 'selecting one from many options');
INSERT INTO response_type VALUES (4, 'file', 'for upload of files');
INSERT INTO response_type VALUES (5, 'radio', 'selecting one from many options');
INSERT INTO response_type VALUES (6, 'single-select', 'pick one from a list');
INSERT INTO response_type VALUES (7, 'multi-select', 'pick many from a list');
INSERT INTO response_type VALUES (8, 'calculation', 'value calculated automatically');
INSERT INTO response_type VALUES (9, 'group-calculation', 'value calculated automatically from an entire group of items');
INSERT INTO response_type VALUES (10, 'instant-calculation', 'for barcode');


--
-- TOC entry 3195 (class 0 OID 143841837)
-- Dependencies: 208
-- Data for Name: rule; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3196 (class 0 OID 143841848)
-- Dependencies: 210
-- Data for Name: rule_action; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3239 (class 0 OID 143843334)
-- Dependencies: 284
-- Data for Name: rule_action_property; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3238 (class 0 OID 143843326)
-- Dependencies: 282
-- Data for Name: rule_action_run; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3240 (class 0 OID 143843345)
-- Dependencies: 286
-- Data for Name: rule_action_run_log; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3197 (class 0 OID 143841859)
-- Dependencies: 212
-- Data for Name: rule_expression; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3198 (class 0 OID 143841870)
-- Dependencies: 214
-- Data for Name: rule_set; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3199 (class 0 OID 143841878)
-- Dependencies: 216
-- Data for Name: rule_set_audit; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3200 (class 0 OID 143841886)
-- Dependencies: 218
-- Data for Name: rule_set_rule; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3201 (class 0 OID 143841894)
-- Dependencies: 220
-- Data for Name: rule_set_rule_audit; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3243 (class 0 OID 143843497)
-- Dependencies: 292
-- Data for Name: scd_item_metadata; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3202 (class 0 OID 143841902)
-- Dependencies: 222
-- Data for Name: section; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3203 (class 0 OID 143841913)
-- Dependencies: 224
-- Data for Name: status; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO status VALUES (1, 'available', 'this is the active status');
INSERT INTO status VALUES (2, 'unavailable', 'this is the inactive status');
INSERT INTO status VALUES (3, 'private', NULL);
INSERT INTO status VALUES (4, 'pending', NULL);
INSERT INTO status VALUES (5, 'removed', 'this indicates that a record is specifically removed by user');
INSERT INTO status VALUES (6, 'locked', NULL);
INSERT INTO status VALUES (7, 'auto-removed', 'this indicates that a record is removed due to the removal of its parent record');
INSERT INTO status VALUES (8, 'signed', 'this indicates all StudyEvents has been signed');
INSERT INTO status VALUES (10, 'souce_data_verification', 'indicates the element has undergone SDV');
INSERT INTO status VALUES (9, 'frozen', 'frozen');
INSERT INTO status VALUES (17, 'partial_data_entry', 'indicates that CRF contains unvalidated data');
INSERT INTO status VALUES (18, 'partial_double_data_entry', 'indicates that CRF contains unvalidated data');


--
-- TOC entry 3204 (class 0 OID 143841924)
-- Dependencies: 226
-- Data for Name: study; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO study VALUES (1, NULL, 'default-study', 'default-study', 'Default Study', '', '2006-10-23 00:00:00+03', '2006-10-23 00:00:00+03', '2006-10-23 00:00:00+03', '2006-10-23 00:00:00+03', 1, NULL, 1, 1, 'default', '', '', '', '', '', '', '', '', '', '', 'observational', '', '2006-10-23 00:00:00+03', 'default', 0, 'default', '', '', '', '', '', '', '', 'both', '', '', false, 'Natural History', '', '', '', '', '', '', 'longitudinal', 'Convenience Sample', 'Retrospective', '', false, 'S_DEFAULTS1', 1, '', 'gui');


--
-- TOC entry 3205 (class 0 OID 143841935)
-- Dependencies: 228
-- Data for Name: study_event; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3206 (class 0 OID 143841946)
-- Dependencies: 230
-- Data for Name: study_event_definition; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3207 (class 0 OID 143841957)
-- Dependencies: 232
-- Data for Name: study_group; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3208 (class 0 OID 143841968)
-- Dependencies: 234
-- Data for Name: study_group_class; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3235 (class 0 OID 143843092)
-- Dependencies: 276
-- Data for Name: study_module_status; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3209 (class 0 OID 143841976)
-- Dependencies: 236
-- Data for Name: study_parameter; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO study_parameter VALUES (13, 'personIdShownOnCRF', '', '', 'false', true, false);
INSERT INTO study_parameter VALUES (11, 'interviewDateDefault', '', 'In study or site creation, CRF Interviewer Date can be set to default to blank or to be pre-populated with user''s name and the date of the study event', 'eventDate', true, true);
INSERT INTO study_parameter VALUES (8, 'interviewerNameDefault', '', 'In study or site creation, CRF Interviewer Name can be set to default to blank or to be pre-populated with user''s name and the date of the study event', 'blank', true, true);
INSERT INTO study_parameter VALUES (6, 'subjectIdPrefixSuffix', '', 'In study and/or site creation, if Study Subject ID is set to Auto-generate, user can optionally specify a prefix and suffix for the format of the ID, using the format [PRETEXT][AUTO#][POSTTEXT]', 'false', true, false);
INSERT INTO study_parameter VALUES (5, 'subjectIdGeneration', '', 'In study creation, Study Subject ID can be set to Manual Entry, Auto-generate (editable), Auto-generate (non-editable)', 'manual', true, false);
INSERT INTO study_parameter VALUES (2, 'discrepancyManagement', '', '', 'true', true, false);
INSERT INTO study_parameter VALUES (14, 'secondaryLabelViewable', '', '', 'not viewable', true, false);
INSERT INTO study_parameter VALUES (15, 'adminForcedReasonForChange', 'adminForcedResonForChange', 'In administrative editing, block changes if event CRF has already been finished.', 'true', true, false);
INSERT INTO study_parameter VALUES (20, 'secondaryIdLabel', 'secondaryIdLabel', 'Secondary ID Label', 'Secondary ID', true, false);
INSERT INTO study_parameter VALUES (21, 'dateOfEnrollmentForStudyLabel', 'dateOfEnrollmentForStudyLabel', 'Date of Enrollment for Study Label', 'Date of Enrollment for Study', true, false);
INSERT INTO study_parameter VALUES (22, 'genderLabel', 'genderLabel', 'Gender Label', 'Sex', true, false);
INSERT INTO study_parameter VALUES (24, 'startDateTimeLabel', 'startDateTimeLabel', 'Start Date Time Label', 'Start Date/Time', true, false);
INSERT INTO study_parameter VALUES (26, 'endDateTimeLabel', 'endDateTimeLabel', 'End Date Time Label', 'End Date/Time', true, false);
INSERT INTO study_parameter VALUES (27, 'useStartTime', 'useStartTime', 'Use Start Time', 'no', true, false);
INSERT INTO study_parameter VALUES (28, 'useEndTime', 'useEndTime', 'Use End Time', 'no', true, false);
INSERT INTO study_parameter VALUES (16, 'eventLocationRequired', 'eventLocationRequired', 'Location Field Required', 'not_used', true, false);
INSERT INTO study_parameter VALUES (30, 'allowSdvWithOpenQueries', 'allowSdvWithOpenQueries', 'Allow SDV with open queries', 'no', true, false);
INSERT INTO study_parameter VALUES (25, 'endDateTimeRequired', 'endDateTimeRequired', 'End Date Time Required', 'not_used', true, false);
INSERT INTO study_parameter VALUES (36, 'autoScheduleEventDuringImport', 'autoScheduleEventDuringImport', 'Auto-schedule event during import', 'no', true, false);
INSERT INTO study_parameter VALUES (38, 'autoCreateSubjectDuringImport', 'autoCreateSubjectDuringImport', 'Auto-create subject during import', 'no', true, false);
INSERT INTO study_parameter VALUES (31, 'replaceExisitingDataDuringImport', 'replaceExisitingDataDuringImport', 'Replace exisiting data during import', 'no', true, false);
INSERT INTO study_parameter VALUES (33, 'defaultBioontologyURL', 'defaultBioontologyURL', 'Study wide default bioontology URL', 'http://data.bioontology.org', true, false);
INSERT INTO study_parameter VALUES (39, 'medicalCodingApiKey', 'medicalCodingApiKey', 'Bioontology REST service API key', '', true, false);
INSERT INTO study_parameter VALUES (34, 'autoCodeDictionaryName', 'autoCodeDictionaryName', 'Auto code dictionary name configuration', '', true, false);
INSERT INTO study_parameter VALUES (35, 'medicalCodingApprovalNeeded', 'medicalCodingApprovalNeeded', 'Flag to determine if coded and synonymized items should be approved', '', true, false);
INSERT INTO study_parameter VALUES (37, 'medicalCodingContextNeeded', 'medicalCodingContextNeeded', 'Flag to determine if m.c. page should contain additional info', '', true, false);
INSERT INTO study_parameter VALUES (40, 'assignRandomizationResultTo', 'assignRandomizationResultTo', 'Assign Randomizaiton Parameters To', 'none', true, false);
INSERT INTO study_parameter VALUES (41, 'randomizationTrialId', 'randomizationTrialId', 'Randomization Trial ID', '0', true, false);
INSERT INTO study_parameter VALUES (43, 'evaluateWithContext', 'evaluateWithContext', 'Flag to control CRF evaluation context parameter', 'no', true, false);
INSERT INTO study_parameter VALUES (9, 'interviewerNameEditable', '', 'In study creation, CRF Interviewer Name can be set to editable or not editable', 'editable', true, true);
INSERT INTO study_parameter VALUES (12, 'interviewDateEditable', '', 'In study creation, CRF Interview Name and Date can be set to editable or not editable', 'editable', true, true);
INSERT INTO study_parameter VALUES (17, 'secondaryIdRequired', 'secondaryIdRequired', 'Secondary ID Required', 'not_used', true, false);
INSERT INTO study_parameter VALUES (19, 'studySubjectIdLabel', 'studySubjectIdLabel', 'Study Subject ID Label', 'Subject ID', true, false);
INSERT INTO study_parameter VALUES (18, 'dateOfEnrollmentForStudyRequired', 'dateOfEnrollmentForStudyRequired', 'Date of Enrollment for Study Required', 'not_used', true, false);
INSERT INTO study_parameter VALUES (4, 'genderRequired', '', 'In study creation, Subject Gender can be set to required or not used', 'false', true, false);
INSERT INTO study_parameter VALUES (1, 'collectDob', 'collect subject''s date of birth', 'In study creation, Subject Birthdate can be set to require collect full birthdate, year of birth, or not used', '3', true, false);
INSERT INTO study_parameter VALUES (3, 'subjectPersonIdRequired', '', 'In study creation, Person ID can be set to required, optional, or not used', 'not used', true, true);
INSERT INTO study_parameter VALUES (23, 'startDateTimeRequired', 'startDateTimeRequired', 'Start Date Time Required', 'not_used', true, false);
INSERT INTO study_parameter VALUES (7, 'interviewerNameRequired', '', 'In study or site creation, CRF Interviewer Name can be set as optional or required fields', 'not_used', true, true);
INSERT INTO study_parameter VALUES (10, 'interviewDateRequired', '', 'In study or site creation, CRF Interviewer Date can be set as optional or required fields', 'not_used', true, true);
INSERT INTO study_parameter VALUES (29, 'markImportedCRFAsCompleted', 'markImportedCRFAsCompleted', 'Mark imported CRF as Completed', 'no', true, true);
INSERT INTO study_parameter VALUES (44, 'autoGeneratedPrefix', 'autoGeneratedPrefix', 'Flag to control count of digits in Auto Generated subject ID sequence.', 'SiteID', true, false);
INSERT INTO study_parameter VALUES (45, 'autoGeneratedSeparator', 'autoGeneratedSeparator', 'Separator that will be used for Auto Generated Subject ID.', '-', true, false);
INSERT INTO study_parameter VALUES (46, 'autoGeneratedSuffix', 'autoGeneratedSuffix', 'Count of the Digits that will be used in the sequence for Auto-Generated Subject IDs.', '3', true, false);
INSERT INTO study_parameter VALUES (47, 'allowDynamicGroupsManagement', 'allowDynamicGroupsManagement', 'Allow Dynamic Groups Management', 'yes', true, false);
INSERT INTO study_parameter VALUES (49, 'annotatedCrfSasItemNames', 'annotatedCrfSasItemNames', 'Flag to check - should SAS item names be displayed in aCRFs.', 'no', true, false);
INSERT INTO study_parameter VALUES (50, 'allowDiscrepancyCorrectionForms', 'allowDiscrepancyCorrectionForms', 'Allows/denies Discrepancy Correction Forms', 'yes', true, false);
INSERT INTO study_parameter VALUES (51, 'randomizationEnviroment', 'randomizationEnviroment', 'Flag to control randomization environment.', 'test', true, false);
INSERT INTO study_parameter VALUES (52, 'autoTabbing', 'autoTabbing', 'auto tabbing', 'yes', true, false);
INSERT INTO study_parameter VALUES (54, 'instanceType', 'instanceType', 'Flag to control instance type.', 'development', true, false);
INSERT INTO study_parameter VALUES (53, 'showYearsInCalendar', 'showYearsInCalendar', 'Flag to check calendar style.', 'yes', true, false);
INSERT INTO study_parameter VALUES (48, 'allowRulesAutoScheduling', 'allowRulesAutoScheduling', 'Flag to determine if events should be scheduled with insert rules', 'yes', true, false);
INSERT INTO study_parameter VALUES (80, 'crfAnnotation', 'crfAnnotation', NULL, 'yes', true, false);
INSERT INTO study_parameter VALUES (81, 'dynamicGroup', 'dynamicGroup', NULL, 'yes', true, false);
INSERT INTO study_parameter VALUES (82, 'calendaredVisits', 'calendaredVisits', NULL, 'yes', true, false);
INSERT INTO study_parameter VALUES (83, 'interactiveDashboards', 'interactiveDashboards', NULL, 'yes', true, false);
INSERT INTO study_parameter VALUES (84, 'itemLevelSDV', 'itemLevelSDV', NULL, 'yes', true, false);
INSERT INTO study_parameter VALUES (85, 'subjectCasebookInPDF', 'subjectCasebookInPDF', NULL, 'yes', true, false);
INSERT INTO study_parameter VALUES (86, 'crfMasking', 'crfMasking', NULL, 'yes', true, false);
INSERT INTO study_parameter VALUES (87, 'sasExtracts', 'sasExtracts', NULL, 'yes', true, false);
INSERT INTO study_parameter VALUES (88, 'studyEvaluator', 'blabla', NULL, 'yes', true, false);
INSERT INTO study_parameter VALUES (89, 'randomization', 'blabla', NULL, 'yes', true, false);
INSERT INTO study_parameter VALUES (90, 'medicalCoding', 'medicalCoding', NULL, 'yes', true, false);


--
-- TOC entry 3210 (class 0 OID 143841985)
-- Dependencies: 238
-- Data for Name: study_parameter_value; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO study_parameter_value VALUES (1, 1, '3', 'collectDob');
INSERT INTO study_parameter_value VALUES (2, 1, 'true', 'discrepancyManagement');
INSERT INTO study_parameter_value VALUES (3, 1, 'false', 'genderRequired');
INSERT INTO study_parameter_value VALUES (5, 1, 'not_used', 'interviewerNameRequired');
INSERT INTO study_parameter_value VALUES (6, 1, 'blank', 'interviewerNameDefault');
INSERT INTO study_parameter_value VALUES (7, 1, 'true', 'interviewerNameEditable');
INSERT INTO study_parameter_value VALUES (8, 1, 'not_used', 'interviewDateRequired');
INSERT INTO study_parameter_value VALUES (9, 1, 'blank', 'interviewDateDefault');
INSERT INTO study_parameter_value VALUES (10, 1, 'true', 'interviewDateEditable');
INSERT INTO study_parameter_value VALUES (12, 1, '', 'subjectIdPrefixSuffix');
INSERT INTO study_parameter_value VALUES (13, 1, 'true', 'personIdShownOnCRF');
INSERT INTO study_parameter_value VALUES (14, 1, 'false', 'secondaryLabelViewable');
INSERT INTO study_parameter_value VALUES (1, 1, 'prod', 'randomizationEnviroment');
INSERT INTO study_parameter_value VALUES (2, 1, 'development', 'instanceType');
INSERT INTO study_parameter_value VALUES (3, 1, NULL, 'studyEvaluator');
INSERT INTO study_parameter_value VALUES (4, 1, 'yes', 'medicalCoding');
INSERT INTO study_parameter_value VALUES (11, 1, 'auto-editable', 'subjectIdGeneration');
INSERT INTO study_parameter_value VALUES (4, 1, 'not_used', 'subjectPersonIdRequired');


--
-- TOC entry 3211 (class 0 OID 143841991)
-- Dependencies: 240
-- Data for Name: study_subject; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3253 (class 0 OID 143844028)
-- Dependencies: 320
-- Data for Name: study_subject_id; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3212 (class 0 OID 143841999)
-- Dependencies: 242
-- Data for Name: study_type; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO study_type VALUES (1, 'genetic', NULL);
INSERT INTO study_type VALUES (2, 'non-genetic', NULL);


--
-- TOC entry 3213 (class 0 OID 143842008)
-- Dependencies: 243
-- Data for Name: study_user_role; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO study_user_role VALUES ('system_administrator', NULL, 1, 1, '2016-06-15 00:00:00+03', '2016-06-15 00:00:00+03', NULL, 'root', 1);


--
-- TOC entry 3214 (class 0 OID 143842013)
-- Dependencies: 245
-- Data for Name: subject; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3215 (class 0 OID 143842021)
-- Dependencies: 247
-- Data for Name: subject_event_status; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO subject_event_status VALUES (1, 'scheduled', '');
INSERT INTO subject_event_status VALUES (2, 'not scheduled', '');
INSERT INTO subject_event_status VALUES (3, 'data entry started', '');
INSERT INTO subject_event_status VALUES (4, 'completed', '');
INSERT INTO subject_event_status VALUES (5, 'stopped', '');
INSERT INTO subject_event_status VALUES (6, 'skipped', '');
INSERT INTO subject_event_status VALUES (7, 'locked', '');
INSERT INTO subject_event_status VALUES (8, 'signed', '');
INSERT INTO subject_event_status VALUES (9, 'source_data_verified', '');
INSERT INTO subject_event_status VALUES (10, 'removed', '');


--
-- TOC entry 3216 (class 0 OID 143842032)
-- Dependencies: 249
-- Data for Name: subject_group_map; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3159 (class 0 OID 143841381)
-- Dependencies: 145
-- Data for Name: system; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO system VALUES (1, 'currentHostName', '', 'STRING', false, 'DYNAMIC_INPUT', '', 80, false, false, false, 113, 'READ', 'READ', 'READ', 'READ', 'READ', 1, 1);
INSERT INTO system VALUES (2, 'currentWebAppName', '', 'STRING', false, 'DYNAMIC_INPUT', '', 80, false, false, false, 113, 'READ', 'READ', 'READ', 'READ', 'READ', 2, 1);
INSERT INTO system VALUES (3, 'currentDBName', '', 'STRING', false, 'DYNAMIC_INPUT', '', 80, false, false, false, 113, 'READ', 'READ', 'READ', 'READ', 'READ', 3, 1);
INSERT INTO system VALUES (4, 'sysURL', 'http://localhost:8080/${WEBAPP}/MainMenu', 'STRING', true, 'TEXT', '', 80, false, true, false, 113, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'READ', 'WRITE', 4, 1);
INSERT INTO system VALUES (6, 'attached_file_location', '', 'STRING', false, 'TEXT', '', 80, false, true, false, 115, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'READ', 'WRITE', 1, 1);
INSERT INTO system VALUES (7, 'crfFileExtensions', '', 'STRING', false, 'TEXT', '', 50, false, true, false, 115, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 2, 1);
INSERT INTO system VALUES (8, 'crfFileExtensionSettings', '', 'STRING', false, 'TEXT', '', 60, false, false, false, 115, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 3, 1);
INSERT INTO system VALUES (9, 'log.dir', '', 'STRING', true, 'DYNAMIC_INPUT', '', 80, false, true, false, 116, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'READ', 'WRITE', 1, 1);
INSERT INTO system VALUES (10, 'logLevel', '', 'STRING', false, 'DYNAMIC_RADIO', 'trace,debug,info,warning,error', 60, false, false, false, 116, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 2, 1);
INSERT INTO system VALUES (11, 'syslog.host', '', 'STRING', false, 'DYNAMIC_INPUT', '', 50, false, false, false, 116, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 3, 1);
INSERT INTO system VALUES (12, 'syslog.port', '', 'INTEGER', false, 'DYNAMIC_INPUT', '', 10, false, false, false, 116, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 4, 1);
INSERT INTO system VALUES (13, 'logLocation', '', 'STRING', false, 'DYNAMIC_INPUT', '', 60, false, false, false, 116, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 5, 1);
INSERT INTO system VALUES (14, 'org.quartz.jobStore.misfireThreshold', '18000000', 'INTEGER', true, 'TEXT', '', 10, true, true, false, 117, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 1, 1);
INSERT INTO system VALUES (15, 'org.quartz.threadPool.threadCount', '1', 'INTEGER', true, 'TEXT', '', 10, false, false, false, 117, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 2, 1);
INSERT INTO system VALUES (16, 'org.quartz.threadPool.threadPriority', '5', 'INTEGER', true, 'TEXT', '', 10, false, false, false, 117, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 3, 1);
INSERT INTO system VALUES (17, 'collectStats', '', 'STRING', false, 'DYNAMIC_RADIO', 'false,true', 60, false, true, false, 118, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 1, 1);
INSERT INTO system VALUES (18, 'usage.stats.host', '', 'STRING', false, 'DYNAMIC_INPUT', '', 50, true, false, false, 118, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'READ', 'WRITE', 2, 1);
INSERT INTO system VALUES (19, 'usage.stats.port', '', 'INTEGER', false, 'DYNAMIC_INPUT', '', 10, true, false, false, 118, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'READ', 'WRITE', 3, 1);
INSERT INTO system VALUES (20, 'cc.ver', 'Rev: ${svn_rev}', 'STRING', false, 'TEXT', '', 20, false, false, false, 118, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'READ', 'WRITE', 4, 1);
INSERT INTO system VALUES (23, 'exportFilePath', 'scheduled_data_export', 'STRING', false, 'TEXT', '', 20, true, true, false, 2, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'READ', 'WRITE', 1, 1);
INSERT INTO system VALUES (24, 'extract.number', '99', 'INTEGER', true, 'TEXT', '', 4, false, true, false, 2, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 2, 1);
INSERT INTO system VALUES (25, 'mailHost', 'mail.smtp.com', 'STRING', false, 'TEXT', '', 50, false, true, false, 3, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 1, 1);
INSERT INTO system VALUES (26, 'mailPort', '465', 'INTEGER', false, 'TEXT', '', 10, false, false, false, 3, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 2, 1);
INSERT INTO system VALUES (27, 'mailSmtpConnectionTimeout', '1000', 'INTEGER', false, 'TEXT', '', 10, false, false, false, 3, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 3, 1);
INSERT INTO system VALUES (28, 'mailUsername', 'username', 'STRING', false, 'TEXT', '', 50, false, false, false, 3, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 4, 1);
INSERT INTO system VALUES (29, 'mailPassword', 'password', 'STRING', false, 'PASSWORD', '', 50, false, false, false, 3, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 5, 1);
INSERT INTO system VALUES (30, 'mailProtocol', 'smtp', 'STRING', false, 'RADIO', 'smtp,smtps', 60, false, false, false, 3, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 6, 1);
INSERT INTO system VALUES (31, 'mailAuth', 'false', 'STRING', false, 'RADIO', 'true,false', 60, false, false, false, 3, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 7, 1);
INSERT INTO system VALUES (32, 'mailTls', 'false', 'STRING', false, 'RADIO', 'true,false', 60, false, false, false, 3, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 8, 1);
INSERT INTO system VALUES (33, 'adminEmail', 'admin@example.com', 'STRING', false, 'TEXT', '', 50, false, false, false, 3, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 9, 1);
INSERT INTO system VALUES (34, 'mailErrorMsg', 'clincapture@example.com', 'STRING', false, 'TEXT', '', 50, false, false, false, 3, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 10, 1);
INSERT INTO system VALUES (37, 'max_inactive_account', '90', 'INTEGER', true, 'TEXT', '', 6, true, true, false, 4, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 3, 1);
INSERT INTO system VALUES (38, 'maxInactiveInterval', '1800', 'INTEGER', true, 'TEXT', '', 6, true, true, false, 4, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 4, 1);
INSERT INTO system VALUES (39, 'FacName', '', 'STRING', false, 'TEXT', '', 60, false, true, false, 5, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 1, 1);
INSERT INTO system VALUES (40, 'FacCity', '', 'STRING', false, 'TEXT', '', 60, false, false, false, 5, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 2, 1);
INSERT INTO system VALUES (41, 'FacState', '', 'STRING', false, 'TEXT', '', 60, false, false, false, 5, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 3, 1);
INSERT INTO system VALUES (42, 'FacZIP', '', 'STRING', false, 'TEXT', '', 60, false, false, false, 5, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 4, 1);
INSERT INTO system VALUES (43, 'FacCountry', '', 'STRING', false, 'TEXT', '', 60, false, false, false, 5, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 5, 1);
INSERT INTO system VALUES (44, 'FacContactName', '', 'STRING', false, 'TEXT', '', 60, false, true, false, 5, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 6, 1);
INSERT INTO system VALUES (45, 'FacContactDegree', '', 'STRING', false, 'TEXT', '', 60, false, false, false, 5, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 7, 1);
INSERT INTO system VALUES (46, 'FacContactPhone', '', 'STRING', false, 'TEXT', '', 60, false, false, false, 5, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 8, 1);
INSERT INTO system VALUES (47, 'FacContactEmail', '', 'STRING', false, 'TEXT', '', 60, false, false, false, 5, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 9, 1);
INSERT INTO system VALUES (50, 'pentaho.url', 'http://pentaho:9090/pentaho/Login', 'STRING', false, 'TEXT', '', 80, false, true, false, 8, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 1, 1);
INSERT INTO system VALUES (51, 'randomizationUrl', 'https://www.randomize.net/api//RandomizeAPIService/RandomizePatientDelegated', 'STRING', false, 'TEXT', '', 80, false, true, false, 9, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'READ', 'WRITE', 1, 1);
INSERT INTO system VALUES (52, 'randomizationAuthenticationUrl', 'https://www.randomize.net/api/RandomizeAPIService/Authenticate', 'STRING', false, 'TEXT', '', 80, false, false, false, 9, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'READ', 'WRITE', 2, 1);
INSERT INTO system VALUES (53, 'randomizationusername', 'username', 'STRING', false, 'TEXT', '', 50, false, true, false, 9, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 3, 1);
INSERT INTO system VALUES (35, 'userAccountNotification', 'email', 'STRING', false, 'RADIO', 'none,email', 60, false, true, false, 4, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 1, 1);
INSERT INTO system VALUES (36, 'password_reuse_frequency', '365', 'INTEGER', true, 'TEXT', '', 6, true, true, false, 4, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 2, 1);
INSERT INTO system VALUES (54, 'randomizationpassword', 'password', 'STRING', false, 'PASSWORD', '', 50, false, false, false, 9, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 4, 1);
INSERT INTO system VALUES (22, 'logo', '/images/Logo.gif', 'STRING', false, 'FILE', '', 60, false, true, false, 119, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 3, 1);
INSERT INTO system VALUES (21, 'themeColor', 'violet', 'STRING', false, 'RADIO', 'blue,darkBlue,green,violet', 60, false, true, false, 119, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 1, 1);
INSERT INTO system VALUES (59, 'sas.dir', '', 'STRING', false, 'TEXT', '', 80, false, true, false, 2, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'READ', 'WRITE', 3, 1);
INSERT INTO system VALUES (5, 'filePath', 'C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\clincapture-data\\', 'STRING', true, 'TEXT', '', 80, false, true, false, 114, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'READ', 'WRITE', 1, 1);
INSERT INTO system VALUES (48, 'supportURL', 'http://clindesk.clinovo.com', 'STRING', true, 'TEXT', '', 80, false, true, false, 6, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 1, 1);
INSERT INTO system VALUES (49, 'rule.studio.url', 'http://rs.clincapture.clinovo.com', 'STRING', false, 'TEXT', '', 80, false, true, false, 7, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 1, 1);
INSERT INTO system VALUES (56, 'level', '${level}', 'STRING', false, 'TEXT', '', 40, false, false, false, 10, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 1, 1);
INSERT INTO system VALUES (57, 'dictionary', '${dictionary}', 'STRING', false, 'TEXT', '', 40, false, false, false, 10, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 2, 1);
INSERT INTO system VALUES (58, 'ccts.waitBeforeCommit', '6000', 'INTEGER', false, 'TEXT', '', 30, true, true, false, 11, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 'HIDDEN', 2, 1);
INSERT INTO system VALUES (61, 'sas.timer', '12', 'INTEGER', false, 'COMBOBOX', '1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24', 80, true, true, false, 2, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 4, 1);
INSERT INTO system VALUES (65, 'autoScheduleEventDuringImport', 'no', 'STRING', false, 'RADIO', 'yes,no', 60, false, false, false, 12, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 2, 1);
INSERT INTO system VALUES (66, 'autoCreateSubjectDuringImport', 'no', 'STRING', false, 'RADIO', 'yes,no', 60, false, false, false, 12, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 3, 1);
INSERT INTO system VALUES (67, 'replaceExisitingDataDuringImport', 'no', 'STRING', false, 'RADIO', 'yes,no', 60, false, false, false, 12, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 4, 1);
INSERT INTO system VALUES (64, 'markImportedCRFAsCompleted', 'no', 'STRING', false, 'RADIO', 'yes,no', 60, false, true, false, 12, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 1, 1);
INSERT INTO system VALUES (69, 'defaultBioontologyURL', 'http://data.bioontology.org', 'STRING', false, 'TEXT', '', 60, false, false, false, 10, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'READ', 'WRITE', 1, 1);
INSERT INTO system VALUES (78, 'bioontologyUsername', '', 'STRING', false, 'TEXT', '', 40, false, false, false, 10, 'READ', 'READ', 'READ', 'READ', 'WRITE', 2, 1);
INSERT INTO system VALUES (70, 'medicalCodingApiKey', '1cfae05f-9e67-486f-820b-b393dec5764b', 'STRING', false, 'TEXT', '', 60, false, false, false, 10, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'READ', 'WRITE', 3, 1);
INSERT INTO system VALUES (71, 'autoCodeDictionaryName', '', 'STRING', false, 'TEXT', '', 60, false, false, false, 10, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 4, 1);
INSERT INTO system VALUES (77, 'system.language', 'en', 'STRING', false, 'COMBOBOX', 'en,ru,es_MX,zh', 80, false, true, false, 120, 'HIDDEN', 'HIDDEN', 'HIDDEN', 'WRITE', 'WRITE', 1, 1);


--
-- TOC entry 3158 (class 0 OID 143841368)
-- Dependencies: 143
-- Data for Name: system_group; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO system_group VALUES (1, 'system', 0, 1, 1);
INSERT INTO system_group VALUES (2, 'extracts', 0, 2, 1);
INSERT INTO system_group VALUES (113, 'access', 1, 1, 1);
INSERT INTO system_group VALUES (114, 'repository', 1, 2, 1);
INSERT INTO system_group VALUES (115, 'file_management', 1, 3, 1);
INSERT INTO system_group VALUES (116, 'logging', 1, 4, 1);
INSERT INTO system_group VALUES (117, 'job_scheduler', 1, 5, 1);
INSERT INTO system_group VALUES (118, 'usage_statistics', 1, 6, 1);
INSERT INTO system_group VALUES (119, 'theme', 1, 7, 1);
INSERT INTO system_group VALUES (12, 'imports', 0, 3, 1);
INSERT INTO system_group VALUES (3, 'email', 0, 4, 1);
INSERT INTO system_group VALUES (4, 'security', 0, 5, 1);
INSERT INTO system_group VALUES (5, 'facility', 0, 6, 1);
INSERT INTO system_group VALUES (6, 'support', 0, 7, 1);
INSERT INTO system_group VALUES (7, 'rules_studio', 0, 8, 1);
INSERT INTO system_group VALUES (8, 'reporting', 0, 9, 1);
INSERT INTO system_group VALUES (11, 'cancer_clinical_trials_suites', 0, 12, 1);
INSERT INTO system_group VALUES (10, 'medical_coding', 0, 11, 1);
INSERT INTO system_group VALUES (9, 'randomization', 0, 10, 1);
INSERT INTO system_group VALUES (13, 'crf_evaluation', 0, 13, 1);
INSERT INTO system_group VALUES (120, 'language', 1, 8, 1);


--
-- TOC entry 3249 (class 0 OID 143843960)
-- Dependencies: 312
-- Data for Name: term; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3252 (class 0 OID 143844014)
-- Dependencies: 318
-- Data for Name: term_element; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3244 (class 0 OID 143843537)
-- Dependencies: 294
-- Data for Name: usage_statistics_data; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO usage_statistics_data VALUES (1, 'oc_start_time', '2016-06-15 15:15:08.649', '2016-06-15 15:15:08.649', 0);


--
-- TOC entry 3217 (class 0 OID 143842040)
-- Dependencies: 251
-- Data for Name: user_account; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO user_account VALUES (1, 'root', '25d55ad283aa400af464c76d713c07ad', 'Root', 'User', 'clincapture@example.com', 1, 'Clinovo', 1, 1, NULL, '2006-10-23 00:00:00+03', '2006-10-23 00:00:00', '2006-10-23 00:00:00+03', NULL, NULL, '888 317 7517', 1, 1, true, true, 0, false, NULL, NULL, NULL);


--
-- TOC entry 3218 (class 0 OID 143842051)
-- Dependencies: 253
-- Data for Name: user_role; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO user_role VALUES (4, 'investigator', 1, NULL);
INSERT INTO user_role VALUES (1, 'system_administrator', 1, NULL);
INSERT INTO user_role VALUES (5, 'clinical_research_coordinator', 1, NULL);
INSERT INTO user_role VALUES (2, 'study_administrator', 1, NULL);
INSERT INTO user_role VALUES (3, 'study_director', 1, NULL);
INSERT INTO user_role VALUES (6, 'study_monitor', 1, NULL);
INSERT INTO user_role VALUES (7, 'study_coder', 1, 'This role allows a user to perform medical coding');


--
-- TOC entry 3219 (class 0 OID 143842062)
-- Dependencies: 255
-- Data for Name: user_type; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO user_type VALUES (1, 'admin');
INSERT INTO user_type VALUES (2, 'user');
INSERT INTO user_type VALUES (3, 'tech-admin');


--
-- TOC entry 3220 (class 0 OID 143842068)
-- Dependencies: 256
-- Data for Name: versioning_map; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 3255 (class 0 OID 143844048)
-- Dependencies: 324
-- Data for Name: widget; Type: TABLE DATA; Schema: public; Owner: clincapture
--

INSERT INTO widget VALUES (17, 0, 'Enrollment Status Per Available Sites', NULL, '9', '9', false, true, false);
INSERT INTO widget VALUES (1, 0, 'Notes and discrepancies assigned to me', NULL, '1,2,3,4,5,6,7,8,9,10', '1,2,3,4,5,6,7,8,9,10', true, true, false);
INSERT INTO widget VALUES (2, 0, 'Events Completion', NULL, '1,2,3,4,5,6,9,10', '1,2,3,4,5,6,9,10', true, true, false);
INSERT INTO widget VALUES (4, 0, 'Subject Status Count', NULL, '1,2,3,4,5,6,9,10', '1,2,3,4,5,6,9,10', true, true, false);
INSERT INTO widget VALUES (6, 0, 'Study Progress', NULL, '1,2,3,4,5,6,9,10', '1,2,3,4,5,6,9,10', true, true, false);
INSERT INTO widget VALUES (8, 0, 'SDV Progress', NULL, '1,2,6,9,10', '1,2,6,9,10', true, true, true);
INSERT INTO widget VALUES (10, 0, 'NDS Per CRF', NULL, '1,2,3,4,5,6,9,10', '1,2,3,4,5,6,9,10', true, true, true);
INSERT INTO widget VALUES (12, 0, 'Enrollment Progress', NULL, '1,2,6,9,10', '1,2,6,9,10', true, true, true);
INSERT INTO widget VALUES (13, 0, 'Coding Progress', NULL, '1,2,4,6,7,9,10', '1,2,4,6,7,9,10', true, true, true);
INSERT INTO widget VALUES (14, 0, 'Enrollment Status per Site', NULL, '1,2,6,10', '1,2,6,10', true, false, false);
INSERT INTO widget VALUES (15, 0, 'CRF Evaluation Progress', NULL, '1,2,8,10', '1,2,8,10', true, true, true);


--
-- TOC entry 3254 (class 0 OID 143844040)
-- Dependencies: 322
-- Data for Name: widgets_layout; Type: TABLE DATA; Schema: public; Owner: clincapture
--



--
-- TOC entry 2940 (class 2606 OID 143843086)
-- Name: configuration_key_key; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY configuration
    ADD CONSTRAINT configuration_key_key UNIQUE (key);


--
-- TOC entry 2947 (class 2606 OID 143843127)
-- Name: measurement_unit_name_key; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY measurement_unit
    ADD CONSTRAINT measurement_unit_name_key UNIQUE (name);


--
-- TOC entry 2949 (class 2606 OID 143843129)
-- Name: measurement_unit_oc_oid_key; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY measurement_unit
    ADD CONSTRAINT measurement_unit_oc_oid_key UNIQUE (oc_oid);


--
-- TOC entry 2896 (class 2606 OID 143843808)
-- Name: oc_qrtz_blob_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY oc_qrtz_blob_triggers
    ADD CONSTRAINT oc_qrtz_blob_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 2898 (class 2606 OID 143843831)
-- Name: oc_qrtz_calendars_pkey; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY oc_qrtz_calendars
    ADD CONSTRAINT oc_qrtz_calendars_pkey PRIMARY KEY (sched_name, calendar_name);


--
-- TOC entry 2900 (class 2606 OID 143843815)
-- Name: oc_qrtz_cron_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY oc_qrtz_cron_triggers
    ADD CONSTRAINT oc_qrtz_cron_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 2908 (class 2606 OID 143843829)
-- Name: oc_qrtz_fired_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY oc_qrtz_fired_triggers
    ADD CONSTRAINT oc_qrtz_fired_triggers_pkey PRIMARY KEY (sched_name, entry_id);


--
-- TOC entry 2912 (class 2606 OID 143843799)
-- Name: oc_qrtz_job_details_pkey; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY oc_qrtz_job_details
    ADD CONSTRAINT oc_qrtz_job_details_pkey PRIMARY KEY (sched_name, job_name, job_group);


--
-- TOC entry 2914 (class 2606 OID 143843833)
-- Name: oc_qrtz_locks_pkey; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY oc_qrtz_locks
    ADD CONSTRAINT oc_qrtz_locks_pkey PRIMARY KEY (sched_name, lock_name);


--
-- TOC entry 2916 (class 2606 OID 143843835)
-- Name: oc_qrtz_paused_trigger_grps_pkey; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY oc_qrtz_paused_trigger_grps
    ADD CONSTRAINT oc_qrtz_paused_trigger_grps_pkey PRIMARY KEY (sched_name, trigger_group);


--
-- TOC entry 2918 (class 2606 OID 143843837)
-- Name: oc_qrtz_scheduler_state_pkey; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY oc_qrtz_scheduler_state
    ADD CONSTRAINT oc_qrtz_scheduler_state_pkey PRIMARY KEY (sched_name, instance_name);


--
-- TOC entry 2920 (class 2606 OID 143843822)
-- Name: oc_qrtz_simple_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY oc_qrtz_simple_triggers
    ADD CONSTRAINT oc_qrtz_simple_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 2973 (class 2606 OID 143843845)
-- Name: oc_qrtz_simprop_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY oc_qrtz_simprop_triggers
    ADD CONSTRAINT oc_qrtz_simprop_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 2934 (class 2606 OID 143843801)
-- Name: oc_qrtz_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY oc_qrtz_triggers
    ADD CONSTRAINT oc_qrtz_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- TOC entry 2650 (class 2606 OID 143841418)
-- Name: pk_archived_dataset_file; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY archived_dataset_file
    ADD CONSTRAINT pk_archived_dataset_file PRIMARY KEY (archived_dataset_file_id);


--
-- TOC entry 2655 (class 2606 OID 143841429)
-- Name: pk_audit_event; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY audit_event
    ADD CONSTRAINT pk_audit_event PRIMARY KEY (audit_id);


--
-- TOC entry 2665 (class 2606 OID 143841449)
-- Name: pk_audit_log_event; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY audit_log_event
    ADD CONSTRAINT pk_audit_log_event PRIMARY KEY (audit_id);


--
-- TOC entry 2667 (class 2606 OID 143841457)
-- Name: pk_audit_log_event_type; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY audit_log_event_type
    ADD CONSTRAINT pk_audit_log_event_type PRIMARY KEY (audit_log_event_type_id);


--
-- TOC entry 3007 (class 2606 OID 143845187)
-- Name: pk_audit_log_randomization; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY audit_log_randomization
    ADD CONSTRAINT pk_audit_log_randomization PRIMARY KEY (id);


--
-- TOC entry 2938 (class 2606 OID 143843041)
-- Name: pk_audit_user_login; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY audit_user_login
    ADD CONSTRAINT pk_audit_user_login PRIMARY KEY (id);


--
-- TOC entry 2979 (class 2606 OID 143843979)
-- Name: pk_coded_item; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY coded_item
    ADD CONSTRAINT pk_coded_item PRIMARY KEY (id);


--
-- TOC entry 2981 (class 2606 OID 143844011)
-- Name: pk_coded_item_element; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY coded_item_element
    ADD CONSTRAINT pk_coded_item_element PRIMARY KEY (id);


--
-- TOC entry 2670 (class 2606 OID 143841468)
-- Name: pk_completion_status; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY completion_status
    ADD CONSTRAINT pk_completion_status PRIMARY KEY (completion_status_id);


--
-- TOC entry 2943 (class 2606 OID 143843084)
-- Name: pk_configuration; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY configuration
    ADD CONSTRAINT pk_configuration PRIMARY KEY (id);


--
-- TOC entry 2676 (class 2606 OID 143841479)
-- Name: pk_crf; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY crf
    ADD CONSTRAINT pk_crf PRIMARY KEY (crf_id);


--
-- TOC entry 2683 (class 2606 OID 143841490)
-- Name: pk_crf_version; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY crf_version
    ADD CONSTRAINT pk_crf_version PRIMARY KEY (crf_version_id);


--
-- TOC entry 2999 (class 2606 OID 143844257)
-- Name: pk_crfs_masking; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY crfs_masking
    ADD CONSTRAINT pk_crfs_masking PRIMARY KEY (id);


--
-- TOC entry 2640 (class 2606 OID 143841359)
-- Name: pk_databasechangeloglock; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY databasechangeloglock
    ADD CONSTRAINT pk_databasechangeloglock PRIMARY KEY (id);


--
-- TOC entry 2688 (class 2606 OID 143841517)
-- Name: pk_dataset; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY dataset
    ADD CONSTRAINT pk_dataset PRIMARY KEY (dataset_id);


--
-- TOC entry 2936 (class 2606 OID 143843020)
-- Name: pk_dataset_item_status; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY dataset_item_status
    ADD CONSTRAINT pk_dataset_item_status PRIMARY KEY (dataset_item_status_id);


--
-- TOC entry 2975 (class 2606 OID 143843946)
-- Name: pk_dictionary; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY dictionary
    ADD CONSTRAINT pk_dictionary PRIMARY KEY (id);


--
-- TOC entry 2971 (class 2606 OID 143843714)
-- Name: pk_discrepancy_description_id; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY discrepancy_description
    ADD CONSTRAINT pk_discrepancy_description_id PRIMARY KEY (id);


--
-- TOC entry 2700 (class 2606 OID 143841608)
-- Name: pk_discrepancy_note; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY discrepancy_note
    ADD CONSTRAINT pk_discrepancy_note PRIMARY KEY (discrepancy_note_id);


--
-- TOC entry 2702 (class 2606 OID 143841616)
-- Name: pk_discrepancy_note_type; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY discrepancy_note_type
    ADD CONSTRAINT pk_discrepancy_note_type PRIMARY KEY (discrepancy_note_type_id);


--
-- TOC entry 2961 (class 2606 OID 143843386)
-- Name: pk_dyn_item_form_metadata; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY dyn_item_form_metadata
    ADD CONSTRAINT pk_dyn_item_form_metadata PRIMARY KEY (id);


--
-- TOC entry 2963 (class 2606 OID 143843395)
-- Name: pk_dyn_item_group_metadata; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY dyn_item_group_metadata
    ADD CONSTRAINT pk_dyn_item_group_metadata PRIMARY KEY (id);


--
-- TOC entry 2969 (class 2606 OID 143843683)
-- Name: pk_dynamic_event; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY dynamic_event
    ADD CONSTRAINT pk_dynamic_event PRIMARY KEY (dynamic_event_id);


--
-- TOC entry 3005 (class 2606 OID 143845155)
-- Name: pk_edc_item_metadata; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY edc_item_metadata
    ADD CONSTRAINT pk_edc_item_metadata PRIMARY KEY (id);


--
-- TOC entry 2724 (class 2606 OID 143841643)
-- Name: pk_event_crf; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY event_crf
    ADD CONSTRAINT pk_event_crf PRIMARY KEY (event_crf_id);


--
-- TOC entry 3001 (class 2606 OID 143845002)
-- Name: pk_event_crf_section; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY event_crf_section
    ADD CONSTRAINT pk_event_crf_section PRIMARY KEY (id);


--
-- TOC entry 2733 (class 2606 OID 143841652)
-- Name: pk_event_definition_crf; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY event_definition_crf
    ADD CONSTRAINT pk_event_definition_crf PRIMARY KEY (event_definition_crf_id);


--
-- TOC entry 2735 (class 2606 OID 143841663)
-- Name: pk_export_format; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY export_format
    ADD CONSTRAINT pk_export_format PRIMARY KEY (export_format_id);


--
-- TOC entry 2737 (class 2606 OID 143841688)
-- Name: pk_group_class_types; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY group_class_types
    ADD CONSTRAINT pk_group_class_types PRIMARY KEY (group_class_type_id);


--
-- TOC entry 2744 (class 2606 OID 143841699)
-- Name: pk_item; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY item
    ADD CONSTRAINT pk_item PRIMARY KEY (item_id);


--
-- TOC entry 2752 (class 2606 OID 143841710)
-- Name: pk_item_data; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY item_data
    ADD CONSTRAINT pk_item_data PRIMARY KEY (item_data_id);


--
-- TOC entry 2754 (class 2606 OID 143843548)
-- Name: pk_item_data_new; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY item_data
    ADD CONSTRAINT pk_item_data_new UNIQUE (item_id, event_crf_id, ordinal);


--
-- TOC entry 2756 (class 2606 OID 143841721)
-- Name: pk_item_data_type; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY item_data_type
    ADD CONSTRAINT pk_item_data_type PRIMARY KEY (item_data_type_id);


--
-- TOC entry 2761 (class 2606 OID 143841732)
-- Name: pk_item_form_metadata; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY item_form_metadata
    ADD CONSTRAINT pk_item_form_metadata PRIMARY KEY (item_form_metadata_id);


--
-- TOC entry 2765 (class 2606 OID 143841740)
-- Name: pk_item_group; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY item_group
    ADD CONSTRAINT pk_item_group PRIMARY KEY (item_group_id);


--
-- TOC entry 2772 (class 2606 OID 143841751)
-- Name: pk_item_group_metadata; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY item_group_metadata
    ADD CONSTRAINT pk_item_group_metadata PRIMARY KEY (item_group_metadata_id);


--
-- TOC entry 2774 (class 2606 OID 143841762)
-- Name: pk_item_reference_type; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY item_reference_type
    ADD CONSTRAINT pk_item_reference_type PRIMARY KEY (item_reference_type_id);


--
-- TOC entry 3003 (class 2606 OID 143845047)
-- Name: pk_item_render_metadata; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY item_render_metadata
    ADD CONSTRAINT pk_item_render_metadata PRIMARY KEY (id);


--
-- TOC entry 2951 (class 2606 OID 143843125)
-- Name: pk_measurement_unit; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY measurement_unit
    ADD CONSTRAINT pk_measurement_unit PRIMARY KEY (id);


--
-- TOC entry 2777 (class 2606 OID 143841773)
-- Name: pk_null_value_type; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY null_value_type
    ADD CONSTRAINT pk_null_value_type PRIMARY KEY (null_value_type_id);


--
-- TOC entry 2779 (class 2606 OID 143841790)
-- Name: pk_openclinica_version; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY openclinica_version
    ADD CONSTRAINT pk_openclinica_version PRIMARY KEY (id);


--
-- TOC entry 2953 (class 2606 OID 143843171)
-- Name: pk_password; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY password
    ADD CONSTRAINT pk_password PRIMARY KEY (passwd_id);


--
-- TOC entry 2781 (class 2606 OID 143841809)
-- Name: pk_resolution_status; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY resolution_status
    ADD CONSTRAINT pk_resolution_status PRIMARY KEY (resolution_status_id);


--
-- TOC entry 2783 (class 2606 OID 143841820)
-- Name: pk_response_set; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY response_set
    ADD CONSTRAINT pk_response_set PRIMARY KEY (response_set_id);


--
-- TOC entry 2785 (class 2606 OID 143841831)
-- Name: pk_response_type; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY response_type
    ADD CONSTRAINT pk_response_type PRIMARY KEY (response_type_id);


--
-- TOC entry 2790 (class 2606 OID 143841845)
-- Name: pk_rule; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY rule
    ADD CONSTRAINT pk_rule PRIMARY KEY (id);


--
-- TOC entry 2795 (class 2606 OID 143841856)
-- Name: pk_rule_action; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY rule_action
    ADD CONSTRAINT pk_rule_action PRIMARY KEY (id);


--
-- TOC entry 2957 (class 2606 OID 143843342)
-- Name: pk_rule_action_property; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY rule_action_property
    ADD CONSTRAINT pk_rule_action_property PRIMARY KEY (id);


--
-- TOC entry 2955 (class 2606 OID 143843331)
-- Name: pk_rule_action_run; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY rule_action_run
    ADD CONSTRAINT pk_rule_action_run PRIMARY KEY (id);


--
-- TOC entry 2959 (class 2606 OID 143843353)
-- Name: pk_rule_action_run_log; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY rule_action_run_log
    ADD CONSTRAINT pk_rule_action_run_log PRIMARY KEY (id);


--
-- TOC entry 2798 (class 2606 OID 143841867)
-- Name: pk_rule_expression; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY rule_expression
    ADD CONSTRAINT pk_rule_expression PRIMARY KEY (id);


--
-- TOC entry 2806 (class 2606 OID 143841875)
-- Name: pk_rule_set; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY rule_set
    ADD CONSTRAINT pk_rule_set PRIMARY KEY (id);


--
-- TOC entry 2810 (class 2606 OID 143841883)
-- Name: pk_rule_set_audit; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY rule_set_audit
    ADD CONSTRAINT pk_rule_set_audit PRIMARY KEY (id);


--
-- TOC entry 2815 (class 2606 OID 143841891)
-- Name: pk_rule_set_rule; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY rule_set_rule
    ADD CONSTRAINT pk_rule_set_rule PRIMARY KEY (id);


--
-- TOC entry 2819 (class 2606 OID 143841899)
-- Name: pk_rule_set_rule_audit; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY rule_set_rule_audit
    ADD CONSTRAINT pk_rule_set_rule_audit PRIMARY KEY (id);


--
-- TOC entry 2965 (class 2606 OID 143843505)
-- Name: pk_scd_item_metadata; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY scd_item_metadata
    ADD CONSTRAINT pk_scd_item_metadata PRIMARY KEY (id);


--
-- TOC entry 2824 (class 2606 OID 143841910)
-- Name: pk_section; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY section
    ADD CONSTRAINT pk_section PRIMARY KEY (section_id);


--
-- TOC entry 2826 (class 2606 OID 143841921)
-- Name: pk_status; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY status
    ADD CONSTRAINT pk_status PRIMARY KEY (status_id);


--
-- TOC entry 2834 (class 2606 OID 143841932)
-- Name: pk_study; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY study
    ADD CONSTRAINT pk_study PRIMARY KEY (study_id);


--
-- TOC entry 2844 (class 2606 OID 143841943)
-- Name: pk_study_event; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY study_event
    ADD CONSTRAINT pk_study_event PRIMARY KEY (study_event_id);


--
-- TOC entry 2849 (class 2606 OID 143841954)
-- Name: pk_study_event_definition; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY study_event_definition
    ADD CONSTRAINT pk_study_event_definition PRIMARY KEY (study_event_definition_id);


--
-- TOC entry 2853 (class 2606 OID 143841965)
-- Name: pk_study_group; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY study_group
    ADD CONSTRAINT pk_study_group PRIMARY KEY (study_group_id);


--
-- TOC entry 2857 (class 2606 OID 143841973)
-- Name: pk_study_group_class; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY study_group_class
    ADD CONSTRAINT pk_study_group_class PRIMARY KEY (study_group_class_id);


--
-- TOC entry 2945 (class 2606 OID 143843104)
-- Name: pk_study_module_status; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY study_module_status
    ADD CONSTRAINT pk_study_module_status PRIMARY KEY (id);


--
-- TOC entry 2859 (class 2606 OID 143841982)
-- Name: pk_study_parameter; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY study_parameter
    ADD CONSTRAINT pk_study_parameter PRIMARY KEY (study_parameter_id);


--
-- TOC entry 2868 (class 2606 OID 143841996)
-- Name: pk_study_subject; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY study_subject
    ADD CONSTRAINT pk_study_subject PRIMARY KEY (study_subject_id);


--
-- TOC entry 2985 (class 2606 OID 143844034)
-- Name: pk_study_subject_id; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY study_subject_id
    ADD CONSTRAINT pk_study_subject_id PRIMARY KEY (id);


--
-- TOC entry 2872 (class 2606 OID 143842007)
-- Name: pk_study_type; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY study_type
    ADD CONSTRAINT pk_study_type PRIMARY KEY (study_type_id);


--
-- TOC entry 2881 (class 2606 OID 143842018)
-- Name: pk_subject; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY subject
    ADD CONSTRAINT pk_subject PRIMARY KEY (subject_id);


--
-- TOC entry 2883 (class 2606 OID 143842029)
-- Name: pk_subject_event_status; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY subject_event_status
    ADD CONSTRAINT pk_subject_event_status PRIMARY KEY (subject_event_status_id);


--
-- TOC entry 2887 (class 2606 OID 143842037)
-- Name: pk_subject_group_map; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY subject_group_map
    ADD CONSTRAINT pk_subject_group_map PRIMARY KEY (subject_group_map_id);


--
-- TOC entry 2646 (class 2606 OID 143841405)
-- Name: pk_system; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY system
    ADD CONSTRAINT pk_system PRIMARY KEY (id);


--
-- TOC entry 2642 (class 2606 OID 143841376)
-- Name: pk_system_group; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY system_group
    ADD CONSTRAINT pk_system_group PRIMARY KEY (id);


--
-- TOC entry 2977 (class 2606 OID 143843968)
-- Name: pk_term; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY term
    ADD CONSTRAINT pk_term PRIMARY KEY (id);


--
-- TOC entry 2983 (class 2606 OID 143844022)
-- Name: pk_term_element; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY term_element
    ADD CONSTRAINT pk_term_element PRIMARY KEY (id);


--
-- TOC entry 2967 (class 2606 OID 143843545)
-- Name: pk_usage_statistics_data; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY usage_statistics_data
    ADD CONSTRAINT pk_usage_statistics_data PRIMARY KEY (id);


--
-- TOC entry 2890 (class 2606 OID 143842048)
-- Name: pk_user_account; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT pk_user_account PRIMARY KEY (user_id);


--
-- TOC entry 2892 (class 2606 OID 143842059)
-- Name: pk_user_role; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY user_role
    ADD CONSTRAINT pk_user_role PRIMARY KEY (role_id);


--
-- TOC entry 2894 (class 2606 OID 143842067)
-- Name: pk_user_type; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY user_type
    ADD CONSTRAINT pk_user_type PRIMARY KEY (user_type_id);


--
-- TOC entry 2991 (class 2606 OID 143844056)
-- Name: pk_widget; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY widget
    ADD CONSTRAINT pk_widget PRIMARY KEY (id);


--
-- TOC entry 2989 (class 2606 OID 143844045)
-- Name: pk_widgets_layout; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY widgets_layout
    ADD CONSTRAINT pk_widgets_layout PRIMARY KEY (id);


--
-- TOC entry 2861 (class 2606 OID 143842084)
-- Name: study_parameter_handle_key; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY study_parameter
    ADD CONSTRAINT study_parameter_handle_key UNIQUE (handle);


--
-- TOC entry 2987 (class 2606 OID 143844036)
-- Name: study_subject_id_name_key; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY study_subject_id
    ADD CONSTRAINT study_subject_id_name_key UNIQUE (name);


--
-- TOC entry 2875 (class 2606 OID 143844244)
-- Name: study_user_role_pkey; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY study_user_role
    ADD CONSTRAINT study_user_role_pkey PRIMARY KEY (study_user_role_id);


--
-- TOC entry 2644 (class 2606 OID 143841378)
-- Name: system_group_name_key; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY system_group
    ADD CONSTRAINT system_group_name_key UNIQUE (name);


--
-- TOC entry 2648 (class 2606 OID 143841407)
-- Name: system_name_key; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY system
    ADD CONSTRAINT system_name_key UNIQUE (name);


--
-- TOC entry 2678 (class 2606 OID 143842072)
-- Name: uniq_crf_oc_oid; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY crf
    ADD CONSTRAINT uniq_crf_oc_oid UNIQUE (oc_oid);


--
-- TOC entry 2685 (class 2606 OID 143844162)
-- Name: uniq_crf_version_oc_oid; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY crf_version
    ADD CONSTRAINT uniq_crf_version_oc_oid UNIQUE (oc_oid);


--
-- TOC entry 2767 (class 2606 OID 143844175)
-- Name: uniq_item_group_oc_oid; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY item_group
    ADD CONSTRAINT uniq_item_group_oc_oid UNIQUE (oc_oid);


--
-- TOC entry 2746 (class 2606 OID 143842076)
-- Name: uniq_item_oc_oid; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY item
    ADD CONSTRAINT uniq_item_oc_oid UNIQUE (oc_oid);


--
-- TOC entry 2851 (class 2606 OID 143842082)
-- Name: uniq_study_event_def_oid; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY study_event_definition
    ADD CONSTRAINT uniq_study_event_def_oid UNIQUE (oc_oid);


--
-- TOC entry 2836 (class 2606 OID 143842080)
-- Name: uniq_study_oid; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY study
    ADD CONSTRAINT uniq_study_oid UNIQUE (oc_oid);


--
-- TOC entry 2870 (class 2606 OID 143842086)
-- Name: uniq_study_subject_oid; Type: CONSTRAINT; Schema: public; Owner: clincapture; Tablespace: 
--

ALTER TABLE ONLY study_subject
    ADD CONSTRAINT uniq_study_subject_oid UNIQUE (oc_oid);


--
-- TOC entry 2651 (class 1259 OID 143842087)
-- Name: i_audit_event_audit_table; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_audit_event_audit_table ON audit_event USING btree (audit_table);


--
-- TOC entry 2656 (class 1259 OID 143842090)
-- Name: i_audit_event_context_study_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_audit_event_context_study_id ON audit_event_context USING btree (study_id);


--
-- TOC entry 2652 (class 1259 OID 143842088)
-- Name: i_audit_event_entity_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_audit_event_entity_id ON audit_event USING btree (entity_id);


--
-- TOC entry 2653 (class 1259 OID 143842089)
-- Name: i_audit_event_user_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_audit_event_user_id ON audit_event USING btree (user_id);


--
-- TOC entry 2657 (class 1259 OID 143843198)
-- Name: i_audit_log_event_audit_log_event_type_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_audit_log_event_audit_log_event_type_id ON audit_log_event USING btree (audit_log_event_type_id);


--
-- TOC entry 2658 (class 1259 OID 143843195)
-- Name: i_audit_log_event_audit_table; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_audit_log_event_audit_table ON audit_log_event USING btree (audit_table);


--
-- TOC entry 2659 (class 1259 OID 143843197)
-- Name: i_audit_log_event_entity_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_audit_log_event_entity_id ON audit_log_event USING btree (entity_id);


--
-- TOC entry 2660 (class 1259 OID 143843199)
-- Name: i_audit_log_event_event_crf_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_audit_log_event_event_crf_id ON audit_log_event USING btree (event_crf_id);


--
-- TOC entry 2661 (class 1259 OID 143843201)
-- Name: i_audit_log_event_event_crf_version_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_audit_log_event_event_crf_version_id ON audit_log_event USING btree (event_crf_version_id);


--
-- TOC entry 2662 (class 1259 OID 143843200)
-- Name: i_audit_log_event_study_event_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_audit_log_event_study_event_id ON audit_log_event USING btree (study_event_id);


--
-- TOC entry 2663 (class 1259 OID 143843196)
-- Name: i_audit_log_event_user_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_audit_log_event_user_id ON audit_log_event USING btree (user_id);


--
-- TOC entry 2668 (class 1259 OID 143843257)
-- Name: i_completion_status_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_completion_status_status_id ON completion_status USING btree (status_id);


--
-- TOC entry 2671 (class 1259 OID 143843304)
-- Name: i_crf_name; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_crf_name ON crf USING btree (name);


--
-- TOC entry 2672 (class 1259 OID 143843306)
-- Name: i_crf_oc_oid; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_crf_oc_oid ON crf USING btree (oc_oid);


--
-- TOC entry 2673 (class 1259 OID 143843305)
-- Name: i_crf_owner_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_crf_owner_id ON crf USING btree (owner_id);


--
-- TOC entry 2674 (class 1259 OID 143843303)
-- Name: i_crf_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_crf_status_id ON crf USING btree (status_id);


--
-- TOC entry 2679 (class 1259 OID 143843313)
-- Name: i_crf_version_name; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_crf_version_name ON crf_version USING btree (name);


--
-- TOC entry 2680 (class 1259 OID 143844160)
-- Name: i_crf_version_oc_oid; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_crf_version_oc_oid ON crf_version USING btree (oc_oid);


--
-- TOC entry 2681 (class 1259 OID 143843314)
-- Name: i_crf_version_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_crf_version_status_id ON crf_version USING btree (status_id);


--
-- TOC entry 2992 (class 1259 OID 143845214)
-- Name: i_crfs_masking_event_definition_crf_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_crfs_masking_event_definition_crf_id ON crfs_masking USING btree (event_definition_crf_id);


--
-- TOC entry 2993 (class 1259 OID 143845217)
-- Name: i_crfs_masking_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_crfs_masking_status_id ON crfs_masking USING btree (status_id);


--
-- TOC entry 2994 (class 1259 OID 143845213)
-- Name: i_crfs_masking_study_event_definition_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_crfs_masking_study_event_definition_id ON crfs_masking USING btree (study_event_definition_id);


--
-- TOC entry 2995 (class 1259 OID 143845212)
-- Name: i_crfs_masking_study_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_crfs_masking_study_id ON crfs_masking USING btree (study_id);


--
-- TOC entry 2996 (class 1259 OID 143845216)
-- Name: i_crfs_masking_study_user_role_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_crfs_masking_study_user_role_id ON crfs_masking USING btree (study_user_role_id);


--
-- TOC entry 2997 (class 1259 OID 143845215)
-- Name: i_crfs_masking_user_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_crfs_masking_user_id ON crfs_masking USING btree (user_id);


--
-- TOC entry 2689 (class 1259 OID 143843239)
-- Name: i_dataset_crf_version_map_dataset_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_dataset_crf_version_map_dataset_id ON dataset_crf_version_map USING btree (dataset_id);


--
-- TOC entry 2690 (class 1259 OID 143843240)
-- Name: i_dataset_crf_version_map_event_definition_crf_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_dataset_crf_version_map_event_definition_crf_id ON dataset_crf_version_map USING btree (event_definition_crf_id);


--
-- TOC entry 2686 (class 1259 OID 143843243)
-- Name: i_dataset_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_dataset_status_id ON dataset USING btree (status_id);


--
-- TOC entry 2691 (class 1259 OID 143843241)
-- Name: i_dataset_study_group_class_map_dataset_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_dataset_study_group_class_map_dataset_id ON dataset_study_group_class_map USING btree (dataset_id);


--
-- TOC entry 2692 (class 1259 OID 143843242)
-- Name: i_dataset_study_group_class_map_study_group_class_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_dataset_study_group_class_map_study_group_class_id ON dataset_study_group_class_map USING btree (study_group_class_id);


--
-- TOC entry 2693 (class 1259 OID 143843231)
-- Name: i_discrepancy_note_discrepancy_note_type_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_discrepancy_note_discrepancy_note_type_id ON discrepancy_note USING btree (discrepancy_note_type_id);


--
-- TOC entry 2694 (class 1259 OID 143843235)
-- Name: i_discrepancy_note_entity_type; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_discrepancy_note_entity_type ON discrepancy_note USING btree (entity_type);


--
-- TOC entry 2695 (class 1259 OID 143843233)
-- Name: i_discrepancy_note_owner_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_discrepancy_note_owner_id ON discrepancy_note USING btree (owner_id);


--
-- TOC entry 2696 (class 1259 OID 143843234)
-- Name: i_discrepancy_note_parent_dn_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_discrepancy_note_parent_dn_id ON discrepancy_note USING btree (parent_dn_id);


--
-- TOC entry 2697 (class 1259 OID 143843232)
-- Name: i_discrepancy_note_resolution_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_discrepancy_note_resolution_status_id ON discrepancy_note USING btree (resolution_status_id);


--
-- TOC entry 2698 (class 1259 OID 143843236)
-- Name: i_discrepancy_note_study_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_discrepancy_note_study_id ON discrepancy_note USING btree (study_id);


--
-- TOC entry 2703 (class 1259 OID 143843224)
-- Name: i_dn_event_crf_map_discrepancy_note_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_dn_event_crf_map_discrepancy_note_id ON dn_event_crf_map USING btree (discrepancy_note_id);


--
-- TOC entry 2704 (class 1259 OID 143843223)
-- Name: i_dn_event_crf_map_event_crf_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_dn_event_crf_map_event_crf_id ON dn_event_crf_map USING btree (event_crf_id);


--
-- TOC entry 2705 (class 1259 OID 143843226)
-- Name: i_dn_item_data_map_discrepancy_note_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_dn_item_data_map_discrepancy_note_id ON dn_item_data_map USING btree (discrepancy_note_id);


--
-- TOC entry 2706 (class 1259 OID 143843225)
-- Name: i_dn_item_data_map_item_data_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_dn_item_data_map_item_data_id ON dn_item_data_map USING btree (item_data_id);


--
-- TOC entry 2707 (class 1259 OID 143843228)
-- Name: i_dn_study_event_map_discrepancy_note_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_dn_study_event_map_discrepancy_note_id ON dn_study_event_map USING btree (discrepancy_note_id);


--
-- TOC entry 2708 (class 1259 OID 143843227)
-- Name: i_dn_study_event_map_study_event_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_dn_study_event_map_study_event_id ON dn_study_event_map USING btree (study_event_id);


--
-- TOC entry 2709 (class 1259 OID 143843230)
-- Name: i_dn_study_subject_map_discrepancy_note_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_dn_study_subject_map_discrepancy_note_id ON dn_study_subject_map USING btree (discrepancy_note_id);


--
-- TOC entry 2710 (class 1259 OID 143843229)
-- Name: i_dn_study_subject_map_study_subject_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_dn_study_subject_map_study_subject_id ON dn_study_subject_map USING btree (study_subject_id);


--
-- TOC entry 2711 (class 1259 OID 143843238)
-- Name: i_dn_subject_map_discrepancy_note_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_dn_subject_map_discrepancy_note_id ON dn_subject_map USING btree (discrepancy_note_id);


--
-- TOC entry 2712 (class 1259 OID 143843237)
-- Name: i_dn_subject_map_subject_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_dn_subject_map_subject_id ON dn_subject_map USING btree (subject_id);


--
-- TOC entry 2713 (class 1259 OID 143843294)
-- Name: i_event_crf_completion_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_event_crf_completion_status_id ON event_crf USING btree (completion_status_id);


--
-- TOC entry 2714 (class 1259 OID 143845034)
-- Name: i_event_crf_crf_version; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_event_crf_crf_version ON event_crf USING btree (crf_version_id);


--
-- TOC entry 2715 (class 1259 OID 143844353)
-- Name: i_event_crf_date_interviewed; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_event_crf_date_interviewed ON event_crf USING btree (date_interviewed);


--
-- TOC entry 2716 (class 1259 OID 143843293)
-- Name: i_event_crf_interviewer_name; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_event_crf_interviewer_name ON event_crf USING btree (interviewer_name);


--
-- TOC entry 2717 (class 1259 OID 143843297)
-- Name: i_event_crf_owner_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_event_crf_owner_id ON event_crf USING btree (owner_id);


--
-- TOC entry 2718 (class 1259 OID 143845035)
-- Name: i_event_crf_sdv_status; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_event_crf_sdv_status ON event_crf USING btree (sdv_status);


--
-- TOC entry 2719 (class 1259 OID 143843295)
-- Name: i_event_crf_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_event_crf_status_id ON event_crf USING btree (status_id);


--
-- TOC entry 2720 (class 1259 OID 143845033)
-- Name: i_event_crf_study_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_event_crf_study_id ON event_crf USING btree (study_event_id);


--
-- TOC entry 2721 (class 1259 OID 143843298)
-- Name: i_event_crf_study_subject_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_event_crf_study_subject_id ON event_crf USING btree (study_subject_id);


--
-- TOC entry 2722 (class 1259 OID 143843296)
-- Name: i_event_crf_validator_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_event_crf_validator_id ON event_crf USING btree (validator_id);


--
-- TOC entry 2725 (class 1259 OID 143843308)
-- Name: i_event_definition_crf_crf_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_event_definition_crf_crf_id ON event_definition_crf USING btree (crf_id);


--
-- TOC entry 2726 (class 1259 OID 143843309)
-- Name: i_event_definition_crf_default_version_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_event_definition_crf_default_version_id ON event_definition_crf USING btree (default_version_id);


--
-- TOC entry 2727 (class 1259 OID 143843312)
-- Name: i_event_definition_crf_electronic_signature; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_event_definition_crf_electronic_signature ON event_definition_crf USING btree (electronic_signature);


--
-- TOC entry 2728 (class 1259 OID 143843311)
-- Name: i_event_definition_crf_ordinal; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_event_definition_crf_ordinal ON event_definition_crf USING btree (ordinal);


--
-- TOC entry 2729 (class 1259 OID 143843310)
-- Name: i_event_definition_crf_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_event_definition_crf_status_id ON event_definition_crf USING btree (status_id);


--
-- TOC entry 2730 (class 1259 OID 143845038)
-- Name: i_event_definition_crf_study_event_definition_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_event_definition_crf_study_event_definition_id ON event_definition_crf USING btree (study_event_definition_id);


--
-- TOC entry 2731 (class 1259 OID 143843307)
-- Name: i_event_definition_crf_study_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_event_definition_crf_study_id ON event_definition_crf USING btree (study_id);


--
-- TOC entry 2747 (class 1259 OID 143843266)
-- Name: i_item_data_event_crf_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_item_data_event_crf_id ON item_data USING btree (event_crf_id);


--
-- TOC entry 2748 (class 1259 OID 143843265)
-- Name: i_item_data_item_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_item_data_item_id ON item_data USING btree (item_id);


--
-- TOC entry 2749 (class 1259 OID 143843268)
-- Name: i_item_data_ordinal; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_item_data_ordinal ON item_data USING btree (ordinal);


--
-- TOC entry 2750 (class 1259 OID 143843267)
-- Name: i_item_data_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_item_data_status_id ON item_data USING btree (status_id);


--
-- TOC entry 2757 (class 1259 OID 143843271)
-- Name: i_item_form_metadata_ordinal; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_item_form_metadata_ordinal ON item_form_metadata USING btree (ordinal);


--
-- TOC entry 2758 (class 1259 OID 143843269)
-- Name: i_item_form_metadata_parent_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_item_form_metadata_parent_id ON item_form_metadata USING btree (parent_id);


--
-- TOC entry 2762 (class 1259 OID 143843263)
-- Name: i_item_group_crf_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_item_group_crf_id ON item_group USING btree (crf_id);


--
-- TOC entry 2768 (class 1259 OID 143843261)
-- Name: i_item_group_metadata_crf_version_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_item_group_metadata_crf_version_id ON item_group_metadata USING btree (crf_version_id);


--
-- TOC entry 2769 (class 1259 OID 143843260)
-- Name: i_item_group_metadata_item_group_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_item_group_metadata_item_group_id ON item_group_metadata USING btree (item_group_id);


--
-- TOC entry 2770 (class 1259 OID 143843262)
-- Name: i_item_group_metadata_item_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_item_group_metadata_item_id ON item_group_metadata USING btree (item_id);


--
-- TOC entry 2763 (class 1259 OID 143843264)
-- Name: i_item_group_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_item_group_status_id ON item_group USING btree (status_id);


--
-- TOC entry 2738 (class 1259 OID 143843300)
-- Name: i_item_item_data_type_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_item_item_data_type_id ON item USING btree (item_data_type_id);


--
-- TOC entry 2739 (class 1259 OID 143843301)
-- Name: i_item_item_reference_type_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_item_item_reference_type_id ON item USING btree (item_reference_type_id);


--
-- TOC entry 2740 (class 1259 OID 143842091)
-- Name: i_item_name; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_item_name ON item USING btree (name);


--
-- TOC entry 2741 (class 1259 OID 143843302)
-- Name: i_item_oc_oid; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_item_oc_oid ON item USING btree (oc_oid);


--
-- TOC entry 2742 (class 1259 OID 143843299)
-- Name: i_item_units; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_item_units ON item USING btree (units);


--
-- TOC entry 2759 (class 1259 OID 143842092)
-- Name: i_itm_form_metadata_crf_ver_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_itm_form_metadata_crf_ver_id ON item_form_metadata USING btree (crf_version_id);


--
-- TOC entry 2941 (class 1259 OID 143843087)
-- Name: i_key_index; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_key_index ON configuration USING btree (key);


--
-- TOC entry 2775 (class 1259 OID 143843202)
-- Name: i_null_value_type_code; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_null_value_type_code ON null_value_type USING btree (code);


--
-- TOC entry 2791 (class 1259 OID 143843207)
-- Name: i_rule_action_action_type; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_action_action_type ON rule_action USING btree (action_type);


--
-- TOC entry 2792 (class 1259 OID 143843206)
-- Name: i_rule_action_rule_set_rule_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_action_rule_set_rule_id ON rule_action USING btree (rule_set_rule_id);


--
-- TOC entry 2793 (class 1259 OID 143843208)
-- Name: i_rule_action_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_action_status_id ON rule_action USING btree (status_id);


--
-- TOC entry 2796 (class 1259 OID 143843209)
-- Name: i_rule_expression_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_expression_status_id ON rule_expression USING btree (status_id);


--
-- TOC entry 2786 (class 1259 OID 143843203)
-- Name: i_rule_oc_oid; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_oc_oid ON rule USING btree (oc_oid);


--
-- TOC entry 2787 (class 1259 OID 143843204)
-- Name: i_rule_rule_expression_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_rule_expression_id ON rule USING btree (rule_expression_id);


--
-- TOC entry 2807 (class 1259 OID 143843216)
-- Name: i_rule_set_audit_rule_set_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_set_audit_rule_set_id ON rule_set_audit USING btree (rule_set_id);


--
-- TOC entry 2808 (class 1259 OID 143843217)
-- Name: i_rule_set_audit_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_set_audit_status_id ON rule_set_audit USING btree (status_id);


--
-- TOC entry 2799 (class 1259 OID 143843212)
-- Name: i_rule_set_crf_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_set_crf_id ON rule_set USING btree (crf_id);


--
-- TOC entry 2800 (class 1259 OID 143843213)
-- Name: i_rule_set_crf_version_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_set_crf_version_id ON rule_set USING btree (crf_version_id);


--
-- TOC entry 2816 (class 1259 OID 143843221)
-- Name: i_rule_set_rule_audit_rule_set_rule_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_set_rule_audit_rule_set_rule_id ON rule_set_rule_audit USING btree (rule_set_rule_id);


--
-- TOC entry 2817 (class 1259 OID 143843222)
-- Name: i_rule_set_rule_audit_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_set_rule_audit_status_id ON rule_set_rule_audit USING btree (status_id);


--
-- TOC entry 2801 (class 1259 OID 143843210)
-- Name: i_rule_set_rule_expression_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_set_rule_expression_id ON rule_set USING btree (rule_expression_id);


--
-- TOC entry 2811 (class 1259 OID 143843219)
-- Name: i_rule_set_rule_rule_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_set_rule_rule_id ON rule_set_rule USING btree (rule_id);


--
-- TOC entry 2812 (class 1259 OID 143843218)
-- Name: i_rule_set_rule_rule_set_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_set_rule_rule_set_id ON rule_set_rule USING btree (rule_set_id);


--
-- TOC entry 2813 (class 1259 OID 143843220)
-- Name: i_rule_set_rule_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_set_rule_status_id ON rule_set_rule USING btree (status_id);


--
-- TOC entry 2802 (class 1259 OID 143843215)
-- Name: i_rule_set_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_set_status_id ON rule_set USING btree (status_id);


--
-- TOC entry 2803 (class 1259 OID 143843211)
-- Name: i_rule_set_study_event_definition_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_set_study_event_definition_id ON rule_set USING btree (study_event_definition_id);


--
-- TOC entry 2804 (class 1259 OID 143843214)
-- Name: i_rule_set_study_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_set_study_id ON rule_set USING btree (study_id);


--
-- TOC entry 2788 (class 1259 OID 143843205)
-- Name: i_rule_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_rule_status_id ON rule USING btree (status_id);


--
-- TOC entry 2820 (class 1259 OID 143842093)
-- Name: i_section_ordinal; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_section_ordinal ON section USING btree (ordinal);


--
-- TOC entry 2821 (class 1259 OID 143842094)
-- Name: i_section_parent_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_section_parent_id ON section USING btree (parent_id);


--
-- TOC entry 2822 (class 1259 OID 143843272)
-- Name: i_section_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_section_status_id ON section USING btree (status_id);


--
-- TOC entry 2837 (class 1259 OID 143843278)
-- Name: i_study_event_date_end; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_event_date_end ON study_event USING btree (date_end);


--
-- TOC entry 2838 (class 1259 OID 143843277)
-- Name: i_study_event_date_start; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_event_date_start ON study_event USING btree (date_start);


--
-- TOC entry 2845 (class 1259 OID 143843275)
-- Name: i_study_event_definition_oc_oid; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_event_definition_oc_oid ON study_event_definition USING btree (oc_oid);


--
-- TOC entry 2846 (class 1259 OID 143843273)
-- Name: i_study_event_definition_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_event_definition_status_id ON study_event_definition USING btree (status_id);


--
-- TOC entry 2847 (class 1259 OID 143843274)
-- Name: i_study_event_definition_update_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_event_definition_update_id ON study_event_definition USING btree (update_id);


--
-- TOC entry 2839 (class 1259 OID 143843276)
-- Name: i_study_event_sample_ordinal; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_event_sample_ordinal ON study_event USING btree (sample_ordinal);


--
-- TOC entry 2840 (class 1259 OID 143843279)
-- Name: i_study_event_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_event_status_id ON study_event USING btree (status_id);


--
-- TOC entry 2841 (class 1259 OID 143845037)
-- Name: i_study_event_study_event_definition_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_event_study_event_definition_id ON study_event USING btree (study_event_definition_id);


--
-- TOC entry 2842 (class 1259 OID 143843280)
-- Name: i_study_event_subject_event_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_event_subject_event_status_id ON study_event USING btree (subject_event_status_id);


--
-- TOC entry 2854 (class 1259 OID 143843282)
-- Name: i_study_group_class_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_group_class_status_id ON study_group_class USING btree (status_id);


--
-- TOC entry 2855 (class 1259 OID 143843281)
-- Name: i_study_group_class_study_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_group_class_study_id ON study_group_class USING btree (study_id);


--
-- TOC entry 2827 (class 1259 OID 143843323)
-- Name: i_study_oc_oid; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_oc_oid ON study USING btree (oc_oid);


--
-- TOC entry 2828 (class 1259 OID 143843320)
-- Name: i_study_owner_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_owner_id ON study USING btree (owner_id);


--
-- TOC entry 2862 (class 1259 OID 143843316)
-- Name: i_study_parameter_value_study_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_parameter_value_study_id ON study_parameter_value USING btree (study_id);


--
-- TOC entry 2829 (class 1259 OID 143843317)
-- Name: i_study_parent_study_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_parent_study_id ON study USING btree (parent_study_id);


--
-- TOC entry 2830 (class 1259 OID 143843322)
-- Name: i_study_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_status_id ON study USING btree (status_id);


--
-- TOC entry 2863 (class 1259 OID 143843285)
-- Name: i_study_subject_label; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_subject_label ON study_subject USING btree (label);


--
-- TOC entry 2864 (class 1259 OID 143843287)
-- Name: i_study_subject_oc_oid; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_subject_oc_oid ON study_subject USING btree (oc_oid);


--
-- TOC entry 2865 (class 1259 OID 143843286)
-- Name: i_study_subject_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_subject_status_id ON study_subject USING btree (status_id);


--
-- TOC entry 2866 (class 1259 OID 143845036)
-- Name: i_study_subject_study_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_subject_study_id ON study_subject USING btree (study_id);


--
-- TOC entry 2831 (class 1259 OID 143843321)
-- Name: i_study_type_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_type_id ON study USING btree (type_id);


--
-- TOC entry 2832 (class 1259 OID 143843319)
-- Name: i_study_unique_identifier; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_unique_identifier ON study USING btree (name);


--
-- TOC entry 2873 (class 1259 OID 143845203)
-- Name: i_study_user_role_user_name; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_study_user_role_user_name ON study_user_role USING btree (user_name);


--
-- TOC entry 2876 (class 1259 OID 143844845)
-- Name: i_subject_date_created; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_subject_date_created ON subject USING btree (date_created);


--
-- TOC entry 2877 (class 1259 OID 143844862)
-- Name: i_subject_date_of_birth; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_subject_date_of_birth ON subject USING btree (date_of_birth);


--
-- TOC entry 2878 (class 1259 OID 143843289)
-- Name: i_subject_gender; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_subject_gender ON subject USING btree (gender);


--
-- TOC entry 2884 (class 1259 OID 143843284)
-- Name: i_subject_group_map_status_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_subject_group_map_status_id ON subject_group_map USING btree (status_id);


--
-- TOC entry 2885 (class 1259 OID 143843283)
-- Name: i_subject_group_map_study_group_class_id; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_subject_group_map_study_group_class_id ON subject_group_map USING btree (study_group_class_id);


--
-- TOC entry 2879 (class 1259 OID 143843290)
-- Name: i_subject_unique_identifier; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_subject_unique_identifier ON subject USING btree (unique_identifier);


--
-- TOC entry 2888 (class 1259 OID 143842096)
-- Name: i_user_account_user_name; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX i_user_account_user_name ON user_account USING btree (user_name);


--
-- TOC entry 2901 (class 1259 OID 143843866)
-- Name: idx_oc_qrtz_ft_inst_job_req_rcvry; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_ft_inst_job_req_rcvry ON oc_qrtz_fired_triggers USING btree (sched_name, instance_name, requests_recovery);


--
-- TOC entry 2902 (class 1259 OID 143843867)
-- Name: idx_oc_qrtz_ft_j_g; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_ft_j_g ON oc_qrtz_fired_triggers USING btree (sched_name, job_name, job_group);


--
-- TOC entry 2903 (class 1259 OID 143843868)
-- Name: idx_oc_qrtz_ft_jg; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_ft_jg ON oc_qrtz_fired_triggers USING btree (sched_name, job_group);


--
-- TOC entry 2904 (class 1259 OID 143843869)
-- Name: idx_oc_qrtz_ft_t_g; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_ft_t_g ON oc_qrtz_fired_triggers USING btree (sched_name, trigger_name, trigger_group);


--
-- TOC entry 2905 (class 1259 OID 143843870)
-- Name: idx_oc_qrtz_ft_tg; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_ft_tg ON oc_qrtz_fired_triggers USING btree (sched_name, trigger_group);


--
-- TOC entry 2906 (class 1259 OID 143843865)
-- Name: idx_oc_qrtz_ft_trig_inst_name; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_ft_trig_inst_name ON oc_qrtz_fired_triggers USING btree (sched_name, instance_name);


--
-- TOC entry 2909 (class 1259 OID 143843852)
-- Name: idx_oc_qrtz_j_grp; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_j_grp ON oc_qrtz_job_details USING btree (sched_name, job_group);


--
-- TOC entry 2910 (class 1259 OID 143843851)
-- Name: idx_oc_qrtz_j_req_recovery; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_j_req_recovery ON oc_qrtz_job_details USING btree (sched_name, requests_recovery);


--
-- TOC entry 2921 (class 1259 OID 143843855)
-- Name: idx_oc_qrtz_t_c; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_t_c ON oc_qrtz_triggers USING btree (sched_name, calendar_name);


--
-- TOC entry 2922 (class 1259 OID 143843856)
-- Name: idx_oc_qrtz_t_g; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_t_g ON oc_qrtz_triggers USING btree (sched_name, trigger_group);


--
-- TOC entry 2923 (class 1259 OID 143843853)
-- Name: idx_oc_qrtz_t_j; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_t_j ON oc_qrtz_triggers USING btree (sched_name, job_name, job_group);


--
-- TOC entry 2924 (class 1259 OID 143843854)
-- Name: idx_oc_qrtz_t_jg; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_t_jg ON oc_qrtz_triggers USING btree (sched_name, job_group);


--
-- TOC entry 2925 (class 1259 OID 143843859)
-- Name: idx_oc_qrtz_t_n_g_state; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_t_n_g_state ON oc_qrtz_triggers USING btree (sched_name, trigger_group, trigger_state);


--
-- TOC entry 2926 (class 1259 OID 143843858)
-- Name: idx_oc_qrtz_t_n_state; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_t_n_state ON oc_qrtz_triggers USING btree (sched_name, trigger_name, trigger_group, trigger_state);


--
-- TOC entry 2927 (class 1259 OID 143843860)
-- Name: idx_oc_qrtz_t_next_fire_time; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_t_next_fire_time ON oc_qrtz_triggers USING btree (sched_name, next_fire_time);


--
-- TOC entry 2928 (class 1259 OID 143843862)
-- Name: idx_oc_qrtz_t_nft_misfire; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_t_nft_misfire ON oc_qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time);


--
-- TOC entry 2929 (class 1259 OID 143843861)
-- Name: idx_oc_qrtz_t_nft_st; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_t_nft_st ON oc_qrtz_triggers USING btree (sched_name, trigger_state, next_fire_time);


--
-- TOC entry 2930 (class 1259 OID 143843863)
-- Name: idx_oc_qrtz_t_nft_st_misfire; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_t_nft_st_misfire ON oc_qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time, trigger_state);


--
-- TOC entry 2931 (class 1259 OID 143843864)
-- Name: idx_oc_qrtz_t_nft_st_misfire_grp; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_t_nft_st_misfire_grp ON oc_qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time, trigger_group, trigger_state);


--
-- TOC entry 2932 (class 1259 OID 143843857)
-- Name: idx_oc_qrtz_t_state; Type: INDEX; Schema: public; Owner: clincapture; Tablespace: 
--

CREATE INDEX idx_oc_qrtz_t_state ON oc_qrtz_triggers USING btree (sched_name, trigger_state);


--
-- TOC entry 3143 (class 2620 OID 143843144)
-- Name: didm_update; Type: TRIGGER; Schema: public; Owner: clincapture
--

CREATE TRIGGER didm_update
    AFTER INSERT ON dn_item_data_map
    FOR EACH ROW
    EXECUTE PROCEDURE populate_ssid_in_didm_trigger();


--
-- TOC entry 3145 (class 2620 OID 143844149)
-- Name: event_crf_delete; Type: TRIGGER; Schema: public; Owner: clincapture
--

CREATE TRIGGER event_crf_delete
    AFTER DELETE ON event_crf
    FOR EACH ROW
    EXECUTE PROCEDURE event_crf_trigger();


--
-- TOC entry 3144 (class 2620 OID 143842724)
-- Name: event_crf_update; Type: TRIGGER; Schema: public; Owner: clincapture
--

CREATE TRIGGER event_crf_update
    AFTER UPDATE ON event_crf
    FOR EACH ROW
    EXECUTE PROCEDURE event_crf_trigger();


--
-- TOC entry 3147 (class 2620 OID 143843142)
-- Name: event_definition_crf_update; Type: TRIGGER; Schema: public; Owner: clincapture
--

CREATE TRIGGER event_definition_crf_update
    AFTER UPDATE ON event_definition_crf
    FOR EACH ROW
    EXECUTE PROCEDURE event_definition_crf_trigger();


--
-- TOC entry 3154 (class 2620 OID 143842725)
-- Name: global_subject_insert_update; Type: TRIGGER; Schema: public; Owner: clincapture
--

CREATE TRIGGER global_subject_insert_update
    AFTER INSERT OR UPDATE ON subject
    FOR EACH ROW
    EXECUTE PROCEDURE global_subject_trigger();


--
-- TOC entry 3150 (class 2620 OID 143843550)
-- Name: item_data_initial; Type: TRIGGER; Schema: public; Owner: clincapture
--

CREATE TRIGGER item_data_initial
    AFTER INSERT ON item_data
    FOR EACH ROW
    EXECUTE PROCEDURE item_data_initial_trigger();


--
-- TOC entry 3148 (class 2620 OID 143842726)
-- Name: item_data_update; Type: TRIGGER; Schema: public; Owner: clincapture
--

CREATE TRIGGER item_data_update
    AFTER DELETE OR UPDATE ON item_data
    FOR EACH ROW
    EXECUTE PROCEDURE item_data_trigger();


--
-- TOC entry 3149 (class 2620 OID 143842730)
-- Name: repeating_data_insert; Type: TRIGGER; Schema: public; Owner: clincapture
--

CREATE TRIGGER repeating_data_insert
    AFTER INSERT ON item_data
    FOR EACH ROW
    EXECUTE PROCEDURE repeating_item_data_trigger();


--
-- TOC entry 3152 (class 2620 OID 143844150)
-- Name: study_event_delete; Type: TRIGGER; Schema: public; Owner: clincapture
--

CREATE TRIGGER study_event_delete
    AFTER DELETE ON study_event
    FOR EACH ROW
    EXECUTE PROCEDURE study_event_trigger();


--
-- TOC entry 3151 (class 2620 OID 143842728)
-- Name: study_event_insert_update; Type: TRIGGER; Schema: public; Owner: clincapture
--

CREATE TRIGGER study_event_insert_update
    AFTER INSERT OR UPDATE ON study_event
    FOR EACH ROW
    EXECUTE PROCEDURE study_event_trigger();


--
-- TOC entry 3153 (class 2620 OID 143842727)
-- Name: study_subject_insert_updare; Type: TRIGGER; Schema: public; Owner: clincapture
--

CREATE TRIGGER study_subject_insert_updare
    AFTER INSERT OR UPDATE ON study_subject
    FOR EACH ROW
    EXECUTE PROCEDURE study_subject_trigger();


--
-- TOC entry 3155 (class 2620 OID 143842729)
-- Name: subject_group_map_insert_update; Type: TRIGGER; Schema: public; Owner: clincapture
--

CREATE TRIGGER subject_group_map_insert_update
    AFTER INSERT OR UPDATE ON subject_group_map
    FOR EACH ROW
    EXECUTE PROCEDURE subject_group_assignment_trigger();


--
-- TOC entry 3146 (class 2620 OID 143844213)
-- Name: update_event_crf_status_trigger; Type: TRIGGER; Schema: public; Owner: clincapture
--

CREATE TRIGGER update_event_crf_status_trigger
    AFTER UPDATE ON event_crf
    FOR EACH ROW
    EXECUTE PROCEDURE update_event_crf_status();


--
-- TOC entry 3129 (class 2606 OID 143844268)
-- Name: crfs_masking_fk_event_definition_crf_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY crfs_masking
    ADD CONSTRAINT crfs_masking_fk_event_definition_crf_id FOREIGN KEY (event_definition_crf_id) REFERENCES event_definition_crf(event_definition_crf_id) ON DELETE CASCADE;


--
-- TOC entry 3128 (class 2606 OID 143844263)
-- Name: crfs_masking_fk_study_event_definition_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY crfs_masking
    ADD CONSTRAINT crfs_masking_fk_study_event_definition_id FOREIGN KEY (study_event_definition_id) REFERENCES study_event_definition(study_event_definition_id) ON DELETE CASCADE;


--
-- TOC entry 3127 (class 2606 OID 143844258)
-- Name: crfs_masking_fk_study_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY crfs_masking
    ADD CONSTRAINT crfs_masking_fk_study_id FOREIGN KEY (study_id) REFERENCES study(study_id) ON DELETE CASCADE;


--
-- TOC entry 3131 (class 2606 OID 143844278)
-- Name: crfs_masking_fk_study_user_role_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY crfs_masking
    ADD CONSTRAINT crfs_masking_fk_study_user_role_id FOREIGN KEY (study_user_role_id) REFERENCES study_user_role(study_user_role_id) ON DELETE CASCADE;


--
-- TOC entry 3130 (class 2606 OID 143844273)
-- Name: crfs_masking_fk_user_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY crfs_masking
    ADD CONSTRAINT crfs_masking_fk_user_id FOREIGN KEY (user_id) REFERENCES user_account(user_id) ON DELETE CASCADE;


--
-- TOC entry 3022 (class 2606 OID 143843024)
-- Name: dataset_fk_dataset_item_status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dataset
    ADD CONSTRAINT dataset_fk_dataset_item_status FOREIGN KEY (dataset_item_status_id) REFERENCES dataset_item_status(dataset_item_status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3030 (class 2606 OID 143843004)
-- Name: discrepancy_note_asn_u_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY discrepancy_note
    ADD CONSTRAINT discrepancy_note_asn_u_id_fkey FOREIGN KEY (assigned_user_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3027 (class 2606 OID 143842267)
-- Name: discrepancy_note_owner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY discrepancy_note
    ADD CONSTRAINT discrepancy_note_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3029 (class 2606 OID 143842277)
-- Name: discrepancy_note_study_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY discrepancy_note
    ADD CONSTRAINT discrepancy_note_study_id_fkey FOREIGN KEY (study_id) REFERENCES study(study_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3031 (class 2606 OID 143843928)
-- Name: dn_discrepancy_note_type_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY discrepancy_note
    ADD CONSTRAINT dn_discrepancy_note_type_id_fk FOREIGN KEY (discrepancy_note_type_id) REFERENCES discrepancy_note_type(discrepancy_note_type_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3033 (class 2606 OID 143842282)
-- Name: dn_event_crf_map_dn_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dn_event_crf_map
    ADD CONSTRAINT dn_event_crf_map_dn_id_fkey FOREIGN KEY (discrepancy_note_id) REFERENCES discrepancy_note(discrepancy_note_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3032 (class 2606 OID 143842287)
-- Name: dn_evnt_crf_map_evnt_crf_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dn_event_crf_map
    ADD CONSTRAINT dn_evnt_crf_map_evnt_crf_id_fk FOREIGN KEY (event_crf_id) REFERENCES event_crf(event_crf_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3035 (class 2606 OID 143842292)
-- Name: dn_item_data_map_dn_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dn_item_data_map
    ADD CONSTRAINT dn_item_data_map_dn_id_fkey FOREIGN KEY (discrepancy_note_id) REFERENCES discrepancy_note(discrepancy_note_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3034 (class 2606 OID 143842297)
-- Name: dn_itm_data_map_itm_data_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dn_item_data_map
    ADD CONSTRAINT dn_itm_data_map_itm_data_id_fk FOREIGN KEY (item_data_id) REFERENCES item_data(item_data_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3028 (class 2606 OID 143842272)
-- Name: dn_resolution_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY discrepancy_note
    ADD CONSTRAINT dn_resolution_status_id_fkey FOREIGN KEY (resolution_status_id) REFERENCES resolution_status(resolution_status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3036 (class 2606 OID 143842307)
-- Name: dn_sem_study_event_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dn_study_event_map
    ADD CONSTRAINT dn_sem_study_event_id_fkey FOREIGN KEY (study_event_id) REFERENCES study_event(study_event_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3038 (class 2606 OID 143842317)
-- Name: dn_ssm_study_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dn_study_subject_map
    ADD CONSTRAINT dn_ssm_study_subject_id_fkey FOREIGN KEY (study_subject_id) REFERENCES study_subject(study_subject_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3037 (class 2606 OID 143842302)
-- Name: dn_study_event_map_dn_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dn_study_event_map
    ADD CONSTRAINT dn_study_event_map_dn_id_fkey FOREIGN KEY (discrepancy_note_id) REFERENCES discrepancy_note(discrepancy_note_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3039 (class 2606 OID 143842312)
-- Name: dn_study_subject_map_dn_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dn_study_subject_map
    ADD CONSTRAINT dn_study_subject_map_dn_id_fk FOREIGN KEY (discrepancy_note_id) REFERENCES discrepancy_note(discrepancy_note_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3041 (class 2606 OID 143842322)
-- Name: dn_subject_map_dn_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dn_subject_map
    ADD CONSTRAINT dn_subject_map_dn_id_fkey FOREIGN KEY (discrepancy_note_id) REFERENCES discrepancy_note(discrepancy_note_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3040 (class 2606 OID 143842327)
-- Name: dn_subject_map_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dn_subject_map
    ADD CONSTRAINT dn_subject_map_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES subject(subject_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3134 (class 2606 OID 143845013)
-- Name: event_crf_section_fk_event_crf_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY event_crf_section
    ADD CONSTRAINT event_crf_section_fk_event_crf_id FOREIGN KEY (event_crf_id) REFERENCES event_crf(event_crf_id) ON DELETE CASCADE;


--
-- TOC entry 3133 (class 2606 OID 143845003)
-- Name: event_crf_section_fk_section_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY event_crf_section
    ADD CONSTRAINT event_crf_section_fk_section_id FOREIGN KEY (section_id) REFERENCES section(section_id) ON DELETE CASCADE;


--
-- TOC entry 3059 (class 2606 OID 143842437)
-- Name: fk_answer_reference_item; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_data
    ADD CONSTRAINT fk_answer_reference_item FOREIGN KEY (item_id) REFERENCES item(item_id) ON UPDATE RESTRICT;


--
-- TOC entry 3008 (class 2606 OID 143842097)
-- Name: fk_archived_reference_dataset; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY archived_dataset_file
    ADD CONSTRAINT fk_archived_reference_dataset FOREIGN KEY (dataset_id) REFERENCES dataset(dataset_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3009 (class 2606 OID 143842102)
-- Name: fk_archived_reference_export_f; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY archived_dataset_file
    ADD CONSTRAINT fk_archived_reference_export_f FOREIGN KEY (export_format_id) REFERENCES export_format(export_format_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3010 (class 2606 OID 143842107)
-- Name: fk_audit_ev_reference_audit_ev; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY audit_event_context
    ADD CONSTRAINT fk_audit_ev_reference_audit_ev FOREIGN KEY (audit_id) REFERENCES audit_event(audit_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3011 (class 2606 OID 143842112)
-- Name: fk_audit_lo_ref_audit_lo; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY audit_event_values
    ADD CONSTRAINT fk_audit_lo_ref_audit_lo FOREIGN KEY (audit_id) REFERENCES audit_event(audit_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3119 (class 2606 OID 143843042)
-- Name: fk_audit_user_login_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY audit_user_login
    ADD CONSTRAINT fk_audit_user_login_id FOREIGN KEY (user_account_id) REFERENCES user_account(user_id);


--
-- TOC entry 3012 (class 2606 OID 143842117)
-- Name: fk_completi_fk_comple_status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY completion_status
    ADD CONSTRAINT fk_completi_fk_comple_status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3013 (class 2606 OID 143842122)
-- Name: fk_crf_crf_user_user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY crf
    ADD CONSTRAINT fk_crf_crf_user_user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3014 (class 2606 OID 143842127)
-- Name: fk_crf_fk_crf_fk_status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY crf
    ADD CONSTRAINT fk_crf_fk_crf_fk_status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3068 (class 2606 OID 143842482)
-- Name: fk_crf_metadata; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_group_metadata
    ADD CONSTRAINT fk_crf_metadata FOREIGN KEY (crf_version_id) REFERENCES crf_version(crf_version_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3017 (class 2606 OID 143842137)
-- Name: fk_crf_vers_crf_versi_user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY crf_version
    ADD CONSTRAINT fk_crf_vers_crf_versi_user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3018 (class 2606 OID 143842142)
-- Name: fk_crf_vers_fk_crf_ve_status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY crf_version
    ADD CONSTRAINT fk_crf_vers_fk_crf_ve_status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3139 (class 2606 OID 143845166)
-- Name: fk_crf_version; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY edc_item_metadata
    ADD CONSTRAINT fk_crf_version FOREIGN KEY (crf_version_id) REFERENCES crf_version(crf_version_id) ON DELETE CASCADE;


--
-- TOC entry 3132 (class 2606 OID 143844289)
-- Name: fk_crfs_masking_status_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY crfs_masking
    ADD CONSTRAINT fk_crfs_masking_status_id FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3023 (class 2606 OID 143842167)
-- Name: fk_dataset__ref_event_event_de; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dataset_crf_version_map
    ADD CONSTRAINT fk_dataset__ref_event_event_de FOREIGN KEY (event_definition_crf_id) REFERENCES event_definition_crf(event_definition_crf_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3024 (class 2606 OID 143842162)
-- Name: fk_dataset_crf_ref_dataset; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dataset_crf_version_map
    ADD CONSTRAINT fk_dataset_crf_ref_dataset FOREIGN KEY (dataset_id) REFERENCES dataset(dataset_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3020 (class 2606 OID 143842152)
-- Name: fk_dataset_fk_datase_status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dataset
    ADD CONSTRAINT fk_dataset_fk_datase_status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3021 (class 2606 OID 143842157)
-- Name: fk_dataset_fk_datase_study; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dataset
    ADD CONSTRAINT fk_dataset_fk_datase_study FOREIGN KEY (study_id) REFERENCES study(study_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3019 (class 2606 OID 143842147)
-- Name: fk_dataset_fk_datase_user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dataset
    ADD CONSTRAINT fk_dataset_fk_datase_user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3025 (class 2606 OID 143842187)
-- Name: fk_dataset_ref_study_grp_class; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dataset_study_group_class_map
    ADD CONSTRAINT fk_dataset_ref_study_grp_class FOREIGN KEY (study_group_class_id) REFERENCES study_group_class(study_group_class_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3026 (class 2606 OID 143842182)
-- Name: fk_dataset_study_ref_dataset; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY dataset_study_group_class_map
    ADD CONSTRAINT fk_dataset_study_ref_dataset FOREIGN KEY (dataset_id) REFERENCES dataset(dataset_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3042 (class 2606 OID 143842332)
-- Name: fk_event_cr_fk_event__completi; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY event_crf
    ADD CONSTRAINT fk_event_cr_fk_event__completi FOREIGN KEY (completion_status_id) REFERENCES completion_status(completion_status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3045 (class 2606 OID 143842347)
-- Name: fk_event_cr_fk_event__status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY event_crf
    ADD CONSTRAINT fk_event_cr_fk_event__status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3046 (class 2606 OID 143842352)
-- Name: fk_event_cr_fk_event__study_ev; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY event_crf
    ADD CONSTRAINT fk_event_cr_fk_event__study_ev FOREIGN KEY (study_event_id) REFERENCES study_event(study_event_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3044 (class 2606 OID 143842342)
-- Name: fk_event_cr_fk_event__user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY event_crf
    ADD CONSTRAINT fk_event_cr_fk_event__user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3047 (class 2606 OID 143842357)
-- Name: fk_event_cr_reference_study_su; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY event_crf
    ADD CONSTRAINT fk_event_cr_reference_study_su FOREIGN KEY (study_subject_id) REFERENCES study_subject(study_subject_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3051 (class 2606 OID 143842377)
-- Name: fk_event_de_fk_study__status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY event_definition_crf
    ADD CONSTRAINT fk_event_de_fk_study__status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3052 (class 2606 OID 143842382)
-- Name: fk_event_de_reference_study_ev; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY event_definition_crf
    ADD CONSTRAINT fk_event_de_reference_study_ev FOREIGN KEY (study_event_definition_id) REFERENCES study_event_definition(study_event_definition_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3050 (class 2606 OID 143842372)
-- Name: fk_event_de_study_crf_user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY event_definition_crf
    ADD CONSTRAINT fk_event_de_study_crf_user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3138 (class 2606 OID 143845161)
-- Name: fk_event_definition_crf; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY edc_item_metadata
    ADD CONSTRAINT fk_event_definition_crf FOREIGN KEY (event_definition_crf_id) REFERENCES event_definition_crf(event_definition_crf_id) ON DELETE CASCADE;


--
-- TOC entry 3087 (class 2606 OID 143842582)
-- Name: fk_group_class_study_group; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_group
    ADD CONSTRAINT fk_group_class_study_group FOREIGN KEY (study_group_class_id) REFERENCES study_group_class(study_group_class_id) ON UPDATE RESTRICT ON DELETE SET NULL;


--
-- TOC entry 3070 (class 2606 OID 143842492)
-- Name: fk_item; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_group_metadata
    ADD CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES item(item_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3140 (class 2606 OID 143845171)
-- Name: fk_item; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY edc_item_metadata
    ADD CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES item(item_id) ON DELETE CASCADE;


--
-- TOC entry 3061 (class 2606 OID 143842447)
-- Name: fk_item_dat_fk_item_d_status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_data
    ADD CONSTRAINT fk_item_dat_fk_item_d_status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3060 (class 2606 OID 143842442)
-- Name: fk_item_dat_fk_item_d_user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_data
    ADD CONSTRAINT fk_item_dat_fk_item_d_user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3055 (class 2606 OID 143842417)
-- Name: fk_item_fk_item_f_item_ref; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item
    ADD CONSTRAINT fk_item_fk_item_f_item_ref FOREIGN KEY (item_reference_type_id) REFERENCES item_reference_type(item_reference_type_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3054 (class 2606 OID 143842412)
-- Name: fk_item_fk_item_i_item_dat; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item
    ADD CONSTRAINT fk_item_fk_item_i_item_dat FOREIGN KEY (item_data_type_id) REFERENCES item_data_type(item_data_type_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3057 (class 2606 OID 143842427)
-- Name: fk_item_fk_item_s_status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item
    ADD CONSTRAINT fk_item_fk_item_s_status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3056 (class 2606 OID 143842422)
-- Name: fk_item_fk_item_u_user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item
    ADD CONSTRAINT fk_item_fk_item_u_user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3067 (class 2606 OID 143842477)
-- Name: fk_item_gro_fk_item_g_status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_group
    ADD CONSTRAINT fk_item_gro_fk_item_g_status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3066 (class 2606 OID 143842472)
-- Name: fk_item_gro_fk_item_g_user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_group
    ADD CONSTRAINT fk_item_gro_fk_item_g_user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3069 (class 2606 OID 143842487)
-- Name: fk_item_group; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_group_metadata
    ADD CONSTRAINT fk_item_group FOREIGN KEY (item_group_id) REFERENCES item_group(item_group_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3065 (class 2606 OID 143842467)
-- Name: fk_item_group_crf; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_group
    ADD CONSTRAINT fk_item_group_crf FOREIGN KEY (crf_id) REFERENCES crf(crf_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3062 (class 2606 OID 143842452)
-- Name: fk_item_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_form_metadata
    ADD CONSTRAINT fk_item_id FOREIGN KEY (item_id) REFERENCES item(item_id) ON UPDATE RESTRICT;


--
-- TOC entry 3058 (class 2606 OID 143842432)
-- Name: fk_item_reference_subject; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_data
    ADD CONSTRAINT fk_item_reference_subject FOREIGN KEY (event_crf_id) REFERENCES event_crf(event_crf_id) ON UPDATE RESTRICT;


--
-- TOC entry 3079 (class 2606 OID 143843154)
-- Name: fk_old_status_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study
    ADD CONSTRAINT fk_old_status_id FOREIGN KEY (old_status_id) REFERENCES status(status_id);


--
-- TOC entry 3099 (class 2606 OID 143842642)
-- Name: fk_person_role_study_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_user_role
    ADD CONSTRAINT fk_person_role_study_id FOREIGN KEY (study_id) REFERENCES study(study_id) ON UPDATE RESTRICT;


--
-- TOC entry 3095 (class 2606 OID 143842622)
-- Name: fk_project__reference_study2; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_subject
    ADD CONSTRAINT fk_project__reference_study2 FOREIGN KEY (study_id) REFERENCES study(study_id) ON UPDATE RESTRICT;


--
-- TOC entry 3071 (class 2606 OID 143842497)
-- Name: fk_response_fk_respon_response; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY response_set
    ADD CONSTRAINT fk_response_fk_respon_response FOREIGN KEY (response_type_id) REFERENCES response_type(response_type_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3063 (class 2606 OID 143842457)
-- Name: fk_rs_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_form_metadata
    ADD CONSTRAINT fk_rs_id FOREIGN KEY (response_set_id) REFERENCES response_set(response_set_id) ON UPDATE RESTRICT;


--
-- TOC entry 3064 (class 2606 OID 143842462)
-- Name: fk_sec_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_form_metadata
    ADD CONSTRAINT fk_sec_id FOREIGN KEY (section_id) REFERENCES section(section_id) ON UPDATE RESTRICT;


--
-- TOC entry 3074 (class 2606 OID 143842522)
-- Name: fk_section_fk_sectio_status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY section
    ADD CONSTRAINT fk_section_fk_sectio_status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3073 (class 2606 OID 143842517)
-- Name: fk_section_fk_sectio_user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY section
    ADD CONSTRAINT fk_section_fk_sectio_user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3072 (class 2606 OID 143842512)
-- Name: fk_section_version; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY section
    ADD CONSTRAINT fk_section_version FOREIGN KEY (crf_version_id) REFERENCES crf_version(crf_version_id) ON UPDATE RESTRICT;


--
-- TOC entry 3015 (class 2606 OID 143843029)
-- Name: fk_source_study_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY crf
    ADD CONSTRAINT fk_source_study_id FOREIGN KEY (source_study_id) REFERENCES study(study_id);


--
-- TOC entry 3081 (class 2606 OID 143842552)
-- Name: fk_study_ev_fk_study__status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_event
    ADD CONSTRAINT fk_study_ev_fk_study__status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3086 (class 2606 OID 143842577)
-- Name: fk_study_ev_fk_study__study; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_event_definition
    ADD CONSTRAINT fk_study_ev_fk_study__study FOREIGN KEY (study_id) REFERENCES study(study_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3082 (class 2606 OID 143842557)
-- Name: fk_study_ev_fk_study__study_ev; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_event
    ADD CONSTRAINT fk_study_ev_fk_study__study_ev FOREIGN KEY (study_event_definition_id) REFERENCES study_event_definition(study_event_definition_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3080 (class 2606 OID 143842547)
-- Name: fk_study_ev_fk_study__user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_event
    ADD CONSTRAINT fk_study_ev_fk_study__user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3085 (class 2606 OID 143842572)
-- Name: fk_study_ev_fk_studye_status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_event_definition
    ADD CONSTRAINT fk_study_ev_fk_studye_status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3084 (class 2606 OID 143842567)
-- Name: fk_study_ev_fk_studye_user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_event_definition
    ADD CONSTRAINT fk_study_ev_fk_studye_user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3083 (class 2606 OID 143842562)
-- Name: fk_study_ev_reference_study_su; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_event
    ADD CONSTRAINT fk_study_ev_reference_study_su FOREIGN KEY (study_subject_id) REFERENCES study_subject(study_subject_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3137 (class 2606 OID 143845156)
-- Name: fk_study_event_definition; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY edc_item_metadata
    ADD CONSTRAINT fk_study_event_definition FOREIGN KEY (study_event_definition_id) REFERENCES study_event_definition(study_event_definition_id) ON DELETE CASCADE;


--
-- TOC entry 3077 (class 2606 OID 143842537)
-- Name: fk_study_fk_study__status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study
    ADD CONSTRAINT fk_study_fk_study__status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3075 (class 2606 OID 143842527)
-- Name: fk_study_fk_study__user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study
    ADD CONSTRAINT fk_study_fk_study__user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3088 (class 2606 OID 143842587)
-- Name: fk_study_gr_fk_study__group_ty; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_group_class
    ADD CONSTRAINT fk_study_gr_fk_study__group_ty FOREIGN KEY (group_class_type_id) REFERENCES group_class_types(group_class_type_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3090 (class 2606 OID 143842597)
-- Name: fk_study_gr_fk_study__status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_group_class
    ADD CONSTRAINT fk_study_gr_fk_study__status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3089 (class 2606 OID 143842592)
-- Name: fk_study_gr_fk_study__user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_group_class
    ADD CONSTRAINT fk_study_gr_fk_study__user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3142 (class 2606 OID 143845193)
-- Name: fk_study_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY audit_log_randomization
    ADD CONSTRAINT fk_study_id FOREIGN KEY (study_id) REFERENCES study(study_id) ON DELETE CASCADE;


--
-- TOC entry 3048 (class 2606 OID 143842362)
-- Name: fk_study_inst_reference; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY event_definition_crf
    ADD CONSTRAINT fk_study_inst_reference FOREIGN KEY (crf_id) REFERENCES crf(crf_id) ON UPDATE RESTRICT;


--
-- TOC entry 3120 (class 2606 OID 143843105)
-- Name: fk_study_module_study_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_module_status
    ADD CONSTRAINT fk_study_module_study_id FOREIGN KEY (study_id) REFERENCES study(study_id);


--
-- TOC entry 3053 (class 2606 OID 143842387)
-- Name: fk_study_reference_instrument; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY event_definition_crf
    ADD CONSTRAINT fk_study_reference_instrument FOREIGN KEY (study_id) REFERENCES study(study_id) ON UPDATE RESTRICT;


--
-- TOC entry 3096 (class 2606 OID 143842627)
-- Name: fk_study_reference_subject; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_subject
    ADD CONSTRAINT fk_study_reference_subject FOREIGN KEY (subject_id) REFERENCES subject(subject_id) ON UPDATE RESTRICT ON DELETE SET NULL;


--
-- TOC entry 3094 (class 2606 OID 143842617)
-- Name: fk_study_su_fk_study__status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_subject
    ADD CONSTRAINT fk_study_su_fk_study__status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3093 (class 2606 OID 143842612)
-- Name: fk_study_su_fk_study__user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_subject
    ADD CONSTRAINT fk_study_su_fk_study__user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3141 (class 2606 OID 143845188)
-- Name: fk_study_subject_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY audit_log_randomization
    ADD CONSTRAINT fk_study_subject_id FOREIGN KEY (study_subject_id) REFERENCES study_subject(study_subject_id) ON DELETE CASCADE;


--
-- TOC entry 3078 (class 2606 OID 143842542)
-- Name: fk_study_type; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study
    ADD CONSTRAINT fk_study_type FOREIGN KEY (type_id) REFERENCES study_type(study_type_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3098 (class 2606 OID 143842637)
-- Name: fk_study_us_fk_study__status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_user_role
    ADD CONSTRAINT fk_study_us_fk_study__status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3097 (class 2606 OID 143842632)
-- Name: fk_study_us_study_use_user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_user_role
    ADD CONSTRAINT fk_study_us_study_use_user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3104 (class 2606 OID 143842667)
-- Name: fk_subject__fk_sub_gr_user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY subject_group_map
    ADD CONSTRAINT fk_subject__fk_sub_gr_user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3107 (class 2606 OID 143842682)
-- Name: fk_subject__fk_subjec_group_ro; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY subject_group_map
    ADD CONSTRAINT fk_subject__fk_subjec_group_ro FOREIGN KEY (study_group_id) REFERENCES study_group(study_group_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3105 (class 2606 OID 143842672)
-- Name: fk_subject__fk_subjec_status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY subject_group_map
    ADD CONSTRAINT fk_subject__fk_subjec_status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3106 (class 2606 OID 143842677)
-- Name: fk_subject__fk_subjec_study_gr; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY subject_group_map
    ADD CONSTRAINT fk_subject__fk_subjec_study_gr FOREIGN KEY (study_group_class_id) REFERENCES study_group_class(study_group_class_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3108 (class 2606 OID 143842687)
-- Name: fk_subject__subject_g_study_su; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY subject_group_map
    ADD CONSTRAINT fk_subject__subject_g_study_su FOREIGN KEY (study_subject_id) REFERENCES study_subject(study_subject_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3103 (class 2606 OID 143842662)
-- Name: fk_subject_fk_subjec_status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY subject
    ADD CONSTRAINT fk_subject_fk_subjec_status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3102 (class 2606 OID 143842657)
-- Name: fk_subject_fk_subjec_user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY subject
    ADD CONSTRAINT fk_subject_fk_subjec_user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3043 (class 2606 OID 143842337)
-- Name: fk_subject_referenc_instrument; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY event_crf
    ADD CONSTRAINT fk_subject_referenc_instrument FOREIGN KEY (crf_version_id) REFERENCES crf_version(crf_version_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3109 (class 2606 OID 143842692)
-- Name: fk_user_acc_fk_user_f_user_acc; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT fk_user_acc_fk_user_f_user_acc FOREIGN KEY (owner_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3111 (class 2606 OID 143842702)
-- Name: fk_user_acc_ref_user__user_typ; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT fk_user_acc_ref_user__user_typ FOREIGN KEY (user_type_id) REFERENCES user_type(user_type_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3110 (class 2606 OID 143842697)
-- Name: fk_user_acc_status_re_status; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT fk_user_acc_status_re_status FOREIGN KEY (status_id) REFERENCES status(status_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3112 (class 2606 OID 143845198)
-- Name: fk_user_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES user_account(user_id) ON DELETE CASCADE;


--
-- TOC entry 3114 (class 2606 OID 143842707)
-- Name: fk_versioni_fk_versio_crf_vers; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY versioning_map
    ADD CONSTRAINT fk_versioni_fk_versio_crf_vers FOREIGN KEY (crf_version_id) REFERENCES crf_version(crf_version_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3113 (class 2606 OID 143842712)
-- Name: fk_versioni_fk_versio_item; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY versioning_map
    ADD CONSTRAINT fk_versioni_fk_versio_item FOREIGN KEY (item_id) REFERENCES item(item_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3016 (class 2606 OID 143842132)
-- Name: fk_versioni_reference_instrume; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY crf_version
    ADD CONSTRAINT fk_versioni_reference_instrume FOREIGN KEY (crf_id) REFERENCES crf(crf_id) ON UPDATE RESTRICT;


--
-- TOC entry 3049 (class 2606 OID 143842367)
-- Name: fk_versioning_study_inst; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY event_definition_crf
    ADD CONSTRAINT fk_versioning_study_inst FOREIGN KEY (default_version_id) REFERENCES crf_version(crf_version_id) ON UPDATE RESTRICT ON DELETE SET NULL;


--
-- TOC entry 3100 (class 2606 OID 143842647)
-- Name: has_father; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY subject
    ADD CONSTRAINT has_father FOREIGN KEY (father_id) REFERENCES subject(subject_id) ON UPDATE RESTRICT ON DELETE SET NULL;


--
-- TOC entry 3101 (class 2606 OID 143842652)
-- Name: has_mother; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY subject
    ADD CONSTRAINT has_mother FOREIGN KEY (mother_id) REFERENCES subject(subject_id) ON UPDATE RESTRICT ON DELETE SET NULL;


--
-- TOC entry 3135 (class 2606 OID 143845048)
-- Name: item_render_metadata_fk_crf_version_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_render_metadata
    ADD CONSTRAINT item_render_metadata_fk_crf_version_id FOREIGN KEY (crf_version_id) REFERENCES crf_version(crf_version_id) ON DELETE CASCADE;


--
-- TOC entry 3136 (class 2606 OID 143845053)
-- Name: item_render_metadata_fk_item_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY item_render_metadata
    ADD CONSTRAINT item_render_metadata_fk_item_id FOREIGN KEY (item_id) REFERENCES item(item_id) ON DELETE CASCADE;


--
-- TOC entry 3115 (class 2606 OID 143843809)
-- Name: oc_qrtz_blob_triggers_sched_name_fkey; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY oc_qrtz_blob_triggers
    ADD CONSTRAINT oc_qrtz_blob_triggers_sched_name_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES oc_qrtz_triggers(sched_name, trigger_name, trigger_group);


--
-- TOC entry 3116 (class 2606 OID 143843816)
-- Name: oc_qrtz_cron_triggers_sched_name_fkey; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY oc_qrtz_cron_triggers
    ADD CONSTRAINT oc_qrtz_cron_triggers_sched_name_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES oc_qrtz_triggers(sched_name, trigger_name, trigger_group);


--
-- TOC entry 3117 (class 2606 OID 143843823)
-- Name: oc_qrtz_simple_triggers_sched_name_fkey; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY oc_qrtz_simple_triggers
    ADD CONSTRAINT oc_qrtz_simple_triggers_sched_name_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES oc_qrtz_triggers(sched_name, trigger_name, trigger_group);


--
-- TOC entry 3123 (class 2606 OID 143843846)
-- Name: oc_qrtz_simprop_triggers_sched_name_fkey; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY oc_qrtz_simprop_triggers
    ADD CONSTRAINT oc_qrtz_simprop_triggers_sched_name_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES oc_qrtz_triggers(sched_name, trigger_name, trigger_group);


--
-- TOC entry 3118 (class 2606 OID 143843802)
-- Name: oc_qrtz_triggers_sched_name_fkey; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY oc_qrtz_triggers
    ADD CONSTRAINT oc_qrtz_triggers_sched_name_fkey FOREIGN KEY (sched_name, job_name, job_group) REFERENCES oc_qrtz_job_details(sched_name, job_name, job_group);


--
-- TOC entry 3076 (class 2606 OID 143842532)
-- Name: project_is_contained_within_pa; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study
    ADD CONSTRAINT project_is_contained_within_pa FOREIGN KEY (parent_study_id) REFERENCES study(study_id) ON UPDATE RESTRICT ON DELETE SET NULL;


--
-- TOC entry 3122 (class 2606 OID 143843511)
-- Name: scd_meta_fk_control_meta_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY scd_item_metadata
    ADD CONSTRAINT scd_meta_fk_control_meta_id FOREIGN KEY (control_item_form_metadata_id) REFERENCES item_form_metadata(item_form_metadata_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3121 (class 2606 OID 143843506)
-- Name: scd_meta_fk_scd_form_meta_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY scd_item_metadata
    ADD CONSTRAINT scd_meta_fk_scd_form_meta_id FOREIGN KEY (scd_item_form_metadata_id) REFERENCES item_form_metadata(item_form_metadata_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3092 (class 2606 OID 143842602)
-- Name: study_param_value_param_fkey; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_parameter_value
    ADD CONSTRAINT study_param_value_param_fkey FOREIGN KEY (parameter) REFERENCES study_parameter(handle) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3091 (class 2606 OID 143842607)
-- Name: study_param_value_study_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY study_parameter_value
    ADD CONSTRAINT study_param_value_study_id_fk FOREIGN KEY (study_id) REFERENCES study(study_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3125 (class 2606 OID 143844062)
-- Name: widgets_layout_fk_study_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY widgets_layout
    ADD CONSTRAINT widgets_layout_fk_study_id FOREIGN KEY (study_id) REFERENCES study(study_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3124 (class 2606 OID 143844057)
-- Name: widgets_layout_fk_users_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY widgets_layout
    ADD CONSTRAINT widgets_layout_fk_users_id FOREIGN KEY (user_id) REFERENCES user_account(user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3126 (class 2606 OID 143844067)
-- Name: widgets_layout_fk_widget_id; Type: FK CONSTRAINT; Schema: public; Owner: clincapture
--

ALTER TABLE ONLY widgets_layout
    ADD CONSTRAINT widgets_layout_fk_widget_id FOREIGN KEY (widget_id) REFERENCES widget(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 3267 (class 0 OID 0)
-- Dependencies: 3
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2016-06-15 15:16:54

--
-- PostgreSQL database dump complete
--

