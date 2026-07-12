-- ##########################################################################
-- #  CORRECTED SCRIPT: DYNAMICALLY & SAFELY DROP ALL TABLES IF THEY EXIST
-- ##########################################################################
-- This block loops through a predefined list of table names. For each name,
-- it queries the USER_TABLES data dictionary view to confirm its existence
-- before attempting to drop it. This avoids errors and provides clear feedback.
----------------------------------------------------------------------------
DECLARE
    -- Define a collection to hold the names of all tables to be dropped
    TYPE table_list_t IS TABLE OF VARCHAR2(128);
    l_tables table_list_t;
    v_sql    VARCHAR2(200);
    v_count  INTEGER;
BEGIN
    -- Populate the list with ALL tables identified from your files.
    -- Names must be uppercase as this is how Oracle stores them in the data dictionary.
    l_tables := table_list_t(
            'FUNCTION_AND_MENU_PROFILE',
            'THEME_INTERN',
            'RESPONSIBLE_THEME',
            'DEPARTMENT_THEME',
            'INTERN',
            'WORKER_USER',
            'THEME',
            'RESPONSIBLE',
            'FUNCTION_AND_MENU',
            'PROFILE',
            'DEPARTMENT',
            'WORKER',  -- Legacy table from your second script
            'ROLE'     -- Table identified in the Java code
                );

    -- Loop through each table name in our list
    FOR i IN 1..l_tables.COUNT LOOP
            -- Check if the table exists for the current user (SYSTEM)
            SELECT COUNT(*)
            INTO v_count
            FROM USER_TABLES
            WHERE TABLE_NAME = l_tables(i);

            -- If the count is 1, the table exists and we can drop it
            IF v_count = 1 THEN
                -- The double quotes are critical because your tables were created with them, making them case-sensitive.
                v_sql := 'DROP TABLE "' || l_tables(i) || '" CASCADE CONSTRAINTS';
                EXECUTE IMMEDIATE v_sql;
                DBMS_OUTPUT.PUT_LINE('SUCCESS: Dropped table "' || l_tables(i) || '".');
            ELSE
                DBMS_OUTPUT.PUT_LINE('INFO: Table "' || l_tables(i) || '" does not exist. Skipping.');
            END IF;
        END LOOP;
    DBMS_OUTPUT.PUT_LINE('--- Cleanup complete. ---');
EXCEPTION
    -- Add a safety net to catch and report unexpected errors during the process
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('ERROR: An unexpected error occurred during the drop process.');
        DBMS_OUTPUT.PUT_LINE('The failing SQL might have been: ' || v_sql);
        DBMS_OUTPUT.PUT_LINE('Oracle Error: ' || SQLERRM);
END;
/