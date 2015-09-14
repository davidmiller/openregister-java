#!/usr/bin/env bash
psql postgres -U postgres -c "CREATE TABLE IF NOT EXISTS ORDERED_ENTRY_INDEX (ID SERIAL PRIMARY KEY, ENTRY JSONB)"
psql postgres -U postgres -c "CREATE TABLE IF NOT EXISTS STREAMED_ENTRIES (ID INTEGER PRIMARY KEY, TIME TIMESTAMP)"
./gradlew run
